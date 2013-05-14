/*
 * Copyright © 2011. Team Lazer Beez (http://teamlazerbeez.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teamlazerbeez.crm.sf.rest;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.core.SObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

@ThreadSafe
final class RestConnectionImpl implements RestConnection {
    private static final String ID_KEY = "Id";
    private static final String ATTRIBUTES_KEY = "attributes";

    private final ObjectReader objectReader;

    private final HttpApiClientProvider httpApiClientProvider;
    private final Timer createTimer;
    private final Timer deleteTimer;
    private final Timer describeGlobalTimer;
    private final Timer describeSObjectTimer;
    private final Timer queryTimer;
    private final Timer queryMoreTimer;
    private final Timer retrieveTimer;
    private final Timer searchTimer;
    private final Timer updateTimer;
    private final Timer basicSObjectInfoTimer;
    private final Timer upsertTimer;

    RestConnectionImpl(ObjectReader objectReader, HttpApiClientProvider httpApiClientProvider,
            MetricRegistry metricRegistry) {
        this.objectReader = objectReader;
        this.httpApiClientProvider = httpApiClientProvider;
        createTimer = metricRegistry.timer(name(RestConnectionImpl.class, "create.request"));
        deleteTimer = metricRegistry.timer(name(RestConnectionImpl.class, "delete.request"));
        describeGlobalTimer = metricRegistry.timer(name(RestConnectionImpl.class, "describeGlobal.request"));
        describeSObjectTimer = metricRegistry.timer(name(RestConnectionImpl.class, "describeSObject.request"));
        basicSObjectInfoTimer = metricRegistry.timer(name(RestConnectionImpl.class, "getBasicSObjectInfo.request"));
        queryTimer = metricRegistry.timer(name(RestConnectionImpl.class, "query.request"));
        queryMoreTimer = metricRegistry.timer(name(RestConnectionImpl.class, "queryMore.request"));
        retrieveTimer = metricRegistry.timer(name(RestConnectionImpl.class, "retrieve.request"));
        searchTimer = metricRegistry.timer(name(RestConnectionImpl.class, "search.request"));
        updateTimer = metricRegistry.timer(name(RestConnectionImpl.class, "update.request"));
        upsertTimer = metricRegistry.timer(name(RestConnectionImpl.class, "upsert.request"));
    }

    @Override
    @Nonnull
    public SaveResult create(SObject sObject) throws IOException {
        Timer.Context context = createTimer.time();

        try {
            return getSaveResult(this.getHttpApiClient().create(sObject));
        } finally {
            context.stop();
        }
    }

    @Override
    public void delete(String sObjectType, Id id) throws IOException {
        Timer.Context context = deleteTimer.time();
        try {
            this.getHttpApiClient().delete(sObjectType, id);
        } finally {
            context.stop();
        }
    }

    @Override
    @Nonnull
    public DescribeGlobalResult describeGlobal() throws IOException {
        Timer.Context context = describeGlobalTimer.time();
        String describeGlobalJson;
        try {
            describeGlobalJson = this.getHttpApiClient().describeGlobal();
        } finally {
            context.stop();
        }

        ObjectNode objectNode = this.objectReader.withType(ObjectNode.class).readValue(describeGlobalJson);
        String encoding = objectNode.get("encoding").textValue();
        int maxBatchSize = objectNode.get("maxBatchSize").intValue();

        ArrayNode descriptionsNode = this.objectReader.withType(ArrayNode.class).readValue(objectNode.get("sobjects"));

        Iterator<JsonNode> elements = descriptionsNode.elements();

        List<GlobalSObjectDescription> descriptions = Lists.newArrayList();
        while (elements.hasNext()) {
            JsonNode node = elements.next();

            descriptions.add(this.objectReader.readValue(node.traverse(), BasicSObjectMetadata.class));
        }

        return new DescribeGlobalResult(encoding, maxBatchSize, descriptions);
    }

    @Override
    @Nonnull
    public SObjectDescription describeSObject(String sObjectType) throws IOException {
        Timer.Context context = describeSObjectTimer.time();
        String descrJson;
        try {
            descrJson = this.getHttpApiClient().describeSObject(sObjectType);
        } finally {
            context.stop();
        }
        return this.objectReader.withType(SObjectDescription.class).readValue(descrJson);
    }

    @Override
    @Nonnull
    public BasicSObjectMetadataResult getBasicObjectInfo(String sObjectType) throws IOException {
        Timer.Context context = basicSObjectInfoTimer.time();
        String jsonStr;
        try {
            jsonStr = this.getHttpApiClient().basicSObjectInfo(sObjectType);
        } finally {
            context.stop();
        }
        ObjectNode objectNode = this.objectReader.withType(ObjectNode.class).readValue(jsonStr);

        BasicSObjectMetadata metadata =
                this.objectReader.withType(BasicSObjectMetadata.class).readValue(objectNode.get("objectDescribe"));

        ArrayNode recentItems = this.objectReader.withType(ArrayNode.class).readValue(objectNode.get("recentItems"));

        List<SObject> sObjects = getSObjects(recentItems.elements());

        return new BasicSObjectMetadataResult(metadata, sObjects);
    }

    @Override
    @Nonnull
    public RestQueryResult query(String soql) throws IOException {
        Timer.Context context = queryTimer.time();
        String json;
        try {
            json = this.getHttpApiClient().query(soql);
        } finally {
            context.stop();
        }
        return getQueryResult(this.objectReader.readValue(parse(json), JsonNode.class));
    }

    @Override
    @Nonnull
    public RestQueryResult queryMore(RestQueryLocator queryLocator) throws IOException {
        Timer.Context context = queryMoreTimer.time();
        String json;
        try {
            json = this.getHttpApiClient().queryMore(queryLocator);
        } finally {
            context.stop();
        }
        return getQueryResult(this.objectReader.readValue(parse(json), JsonNode.class));
    }

    @Override
    @Nonnull
    public SObject retrieve(String sObjectType, Id id, List<String> fields) throws IOException {
        Timer.Context context = retrieveTimer.time();
        String json;
        try {
            json = this.getHttpApiClient().retrieve(sObjectType, id, fields);
        } finally {
            context.stop();
        }
        return getSObject(this.objectReader.readValue(parse(json), JsonNode.class));
    }

    @Override
    @Nonnull
    public List<SObject> search(String sosl) throws IOException {
        Timer.Context context = searchTimer.time();
        String json;
        try {
            json = this.getHttpApiClient().search(sosl);
        } finally {
            context.stop();
        }
        return getSObjects(this.objectReader.readValue(parse(json), ArrayNode.class).elements());
    }

    @Override
    public void update(SObject sObject) throws IOException {
        Timer.Context context = updateTimer.time();
        try {
            this.getHttpApiClient().update(sObject);
        } finally {
            context.stop();
        }
    }

    @Override
    @Nonnull
    public UpsertResult upsert(SObject sObject, String externalIdField) throws IOException {
        // TODO write tests for upsert
        Timer.Context context = upsertTimer.time();
        int statusCode;
        try {
            statusCode = this.getHttpApiClient().upsert(sObject, externalIdField);
        } finally {
            context.stop();
        }

        if (statusCode == 204) {
            return UpsertResult.UPDATED;
        }
        return UpsertResult.CREATED;
    }

    @Nonnull
    private JsonParser parse(@Nullable String str) throws IOException {
        return objectReader.getFactory().createParser(str);
    }

    @Nonnull
    private HttpApiClient getHttpApiClient() {
        return this.httpApiClientProvider.getClient();
    }

    /**
     * @param elements an iterator across json elements, each of which represents an sObject
     *
     * @return list of SObjects
     *
     * @throws IOException on error
     */
    @Nonnull
    private static List<SObject> getSObjects(Iterator<JsonNode> elements) throws IOException {
        List<SObject> sObjects = Lists.newArrayList();
        while (elements.hasNext()) {
            JsonNode node = elements.next();

            sObjects.add(getSObject(node));
        }
        return sObjects;
    }

    @Nonnull
    private SaveResult getSaveResult(@Nullable String saveResultJson) throws IOException {
        ObjectNode objectNode = this.objectReader.withType(ObjectNode.class).readValue(parse(saveResultJson));
        String id = objectNode.get("id").textValue();
        boolean success = objectNode.get("success").booleanValue();

        List<ApiError> errors = this.objectReader.withType(HttpApiClient.API_ERRORS_TYPE).readValue(
                objectNode.get("errors"));

        return new SaveResultImpl(new Id(id), success, errors);
    }

    @Nonnull
    private static RestSObject getSObject(JsonNode rawNode) throws IOException {
        ObjectNode jsonNode = asObjectNode(rawNode);

        ObjectNode attributes = getObjectNode(jsonNode, ATTRIBUTES_KEY);

        String type = getString(attributes, "type");
        JsonNode idNode = jsonNode.get(ID_KEY);
        RestSObjectImpl sObject;
        if (isNull(idNode)) {
            sObject = RestSObjectImpl.getNew(type);
        } else {
            if (!idNode.isTextual()) {
                throw new ResponseParseException("Id node <" + idNode + "> wasn't textual");
            }
            sObject = RestSObjectImpl.getNewWithId(type, new Id(idNode.textValue()));
        }

        jsonNode.remove(ID_KEY);
        jsonNode.remove(ATTRIBUTES_KEY);

        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValueNode = jsonNode.get(fieldName);

            if (fieldValueNode.isNull()) {
                // null node is a value node so handle it first
                sObject.setField(fieldName, null);
                continue;
            } else if (fieldValueNode.isValueNode()) {
                sObject.setField(fieldName, fieldValueNode.asText());
                continue;
            } else if (fieldValueNode.isObject()) {
                // it could either be a subquery or a sub object at this point.
                if (fieldValueNode.path("attributes").isObject()) {
                    sObject.setRelationshipSubObject(fieldName, getSObject(fieldValueNode));
                } else if (fieldValueNode.path("records").isArray()) {
                    sObject.setRelationshipQueryResult(fieldName, getQueryResult(fieldValueNode));
                } else {
                    throw new ResponseParseException("Could not understand field value node: " + fieldValueNode);
                }

                continue;
            }

            throw new ResponseParseException("Unknown node type <" + fieldValueNode + ">");
        }
        return sObject;
    }

    @Nonnull
    private static RestQueryResult getQueryResult(JsonNode rawNode) throws IOException {
        ObjectNode results = asObjectNode(rawNode);

        int totalSize = getInt(results, "totalSize");
        boolean done = getBoolean(results, "done");

        ArrayNode records = getArrayNode(results, "records");

        List<RestSObject> sObjects = Lists.newArrayList();

        Iterator<JsonNode> elements = records.elements();

        while (elements.hasNext()) {
            JsonNode recordNode = elements.next();
            sObjects.add(getSObject(recordNode));
        }

        if (done) {
            return RestQueryResultImpl.getDone(sObjects, totalSize);
        }

        String nextRecordsUrl = getString(results, "nextRecordsUrl");

        return RestQueryResultImpl.getNotDone(sObjects, totalSize, new RestQueryLocator(nextRecordsUrl));
    }

    @Nonnull
    private static ObjectNode asObjectNode(JsonNode jsonNode) throws ResponseParseException {
        if (jsonNode == null) {
            throw new ResponseParseException("Got a null object node");
        }

        if (!jsonNode.isObject()) {
            throw new ResponseParseException("Got a node that wasn't an object <" + jsonNode + ">");
        }

        return (ObjectNode) jsonNode;
    }

    @Nonnull
    private static String getString(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isTextual()) {
            throw new ResponseParseException("Node <" + node + "> isn't text for key <" + key + ">");
        }
        return node.textValue();
    }

    private static int getInt(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isInt()) {
            throw new ResponseParseException("Node <" + node + "> isn't int for key <" + key + ">");
        }
        return node.intValue();
    }

    private static boolean getBoolean(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isBoolean()) {
            throw new ResponseParseException("Node <" + node + "> isn't boolean for key <" + key + ">");
        }
        return node.booleanValue();
    }

    @Nonnull
    private static ArrayNode getArrayNode(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isArray()) {
            throw new ResponseParseException("Node <" + node + "> isn't an array for key <" + key + ">");
        }
        return (ArrayNode) node;
    }

    @Nonnull
    private static ObjectNode getObjectNode(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isObject()) {
            throw new ResponseParseException("Node <" + node + "> isn't an object for key <" + key + ">");
        }
        return (ObjectNode) node;
    }

    @Nonnull
    private static JsonNode getNode(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode value = jsonNode.get(key);
        if (isNull(value)) {
            throw new ResponseParseException("Null value for key <" + key + ">");
        }
        return value;
    }

    private static boolean isNull(JsonNode node) {
        return node == null || node == NullNode.instance;
    }
}
