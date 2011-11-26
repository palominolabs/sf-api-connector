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

import com.google.common.collect.Lists;
import com.teamlazerbeez.crm.sf.core.Id;
import com.teamlazerbeez.crm.sf.core.SObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@ThreadSafe
final class RestConnectionImpl implements RestConnection {
    private static final String ID_KEY = "Id";
    private static final String ATTRIBUTES_KEY = "attributes";

    private final ObjectMapper objectMapper;

    private final HttpApiClientProvider httpApiClientProvider;

    RestConnectionImpl(ObjectMapper objectMapper, HttpApiClientProvider httpApiClientProvider) {
        this.objectMapper = objectMapper;
        this.httpApiClientProvider = httpApiClientProvider;
    }

    @Override
    @Nonnull
    public SaveResult create(SObject sObject) throws IOException {
        return getSaveResult(this.getHttpApiClient().create(sObject));
    }

    @Override
    public void delete(String sObjectType, Id id) throws IOException {
        this.getHttpApiClient().delete(sObjectType, id);
    }

    @Override
    @Nonnull
    public DescribeGlobalResult describeGlobal() throws IOException {
        String describeGlobalJson = this.getHttpApiClient().describeGlobal();

        ObjectNode objectNode = this.objectMapper.readValue(describeGlobalJson, ObjectNode.class);
        String encoding = objectNode.get("encoding").getTextValue();
        int maxBatchSize = objectNode.get("maxBatchSize").getIntValue();

        ArrayNode descriptionsNode = this.objectMapper.readValue(objectNode.get("sobjects"), ArrayNode.class);

        Iterator<JsonNode> elements = descriptionsNode.getElements();

        List<GlobalSObjectDescription> descriptions = Lists.newArrayList();
        while (elements.hasNext()) {
            JsonNode node = elements.next();

            descriptions.add(this.objectMapper.readValue(node, BasicSObjectMetadata.class));
        }

        return new DescribeGlobalResult(encoding, maxBatchSize, descriptions);
    }

    @Override
    @Nonnull
    public SObjectDescription describeSObject(String sObjectType) throws IOException {
        String descrJson = this.getHttpApiClient().describeSObject(sObjectType);
        return this.objectMapper.readValue(descrJson, SObjectDescription.class);
    }

    @Override
    @Nonnull
    public BasicSObjectMetadataResult getBasicObjectInfo(String sObjectType) throws IOException {
        String jsonStr = this.getHttpApiClient().basicSObjectInfo(sObjectType);
        ObjectNode objectNode = this.objectMapper.readValue(jsonStr, ObjectNode.class);

        BasicSObjectMetadata metadata =
                this.objectMapper.readValue(objectNode.get("objectDescribe"), BasicSObjectMetadata.class);

        ArrayNode recentItems = this.objectMapper.readValue(objectNode.get("recentItems"), ArrayNode.class);

        List<SObject> sObjects = getSObjects(recentItems.getElements());

        return new BasicSObjectMetadataResult(metadata, sObjects);
    }

    @Override
    @Nonnull
    public RestQueryResult query(String soql) throws IOException {
        return getQueryResult(this.objectMapper.readValue(this.getHttpApiClient().query(soql), JsonNode.class));
    }

    @Override
    @Nonnull
    public RestQueryResult queryMore(RestQueryLocator queryLocator) throws IOException {
        return getQueryResult(
                this.objectMapper.readValue(this.getHttpApiClient().queryMore(queryLocator), JsonNode.class));
    }

    @Override
    @Nonnull
    public SObject retrieve(String sObjectType, Id id, List<String> fields) throws IOException {
        String json = this.getHttpApiClient().retrieve(sObjectType, id, fields);
        return getSObject(this.objectMapper.readValue(json, JsonNode.class));
    }

    @Override
    @Nonnull
    public List<SObject> search(String sosl) throws IOException {
        String json = this.getHttpApiClient().search(sosl);
        return getSObjects(this.objectMapper.readValue(json, ArrayNode.class).getElements());
    }

    @Override
    public void update(SObject sObject) throws IOException {
        this.getHttpApiClient().update(sObject);
    }

    @Override
    @Nonnull
    public UpsertResult upsert(SObject sObject, String externalIdField) throws IOException {
        int statusCode = this.getHttpApiClient().upsert(sObject, externalIdField);

        if (statusCode == 204) {
            return UpsertResult.UPDATED;
        }
        return UpsertResult.CREATED;
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
    private SaveResult getSaveResult(String saveResultJson) throws IOException {
        ObjectNode objectNode = this.objectMapper.readValue(saveResultJson, ObjectNode.class);
        String id = objectNode.get("id").getTextValue();
        boolean success = objectNode.get("success").getBooleanValue();

        List<ApiError> errors = this.objectMapper
                .readValue(objectNode.get("errors"), HttpApiClient.API_ERRORS_TYPE);

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
            sObject = RestSObjectImpl.getNewWithId(type, new Id(idNode.getTextValue()));
        }

        jsonNode.remove(ID_KEY);
        jsonNode.remove(ATTRIBUTES_KEY);

        Iterator<String> fieldNames = jsonNode.getFieldNames();
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
                sObject.setRelationshipQueryResult(fieldName, getQueryResult(fieldValueNode));
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

        Iterator<JsonNode> elements = records.getElements();

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
        return node.getTextValue();
    }

    private static int getInt(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isInt()) {
            throw new ResponseParseException("Node <" + node + "> isn't int for key <" + key + ">");
        }
        return node.getIntValue();
    }

    private static boolean getBoolean(ObjectNode jsonNode, String key) throws ResponseParseException {
        JsonNode node = getNode(jsonNode, key);
        if (!node.isBoolean()) {
            throw new ResponseParseException("Node <" + node + "> isn't boolean for key <" + key + ">");
        }
        return node.getBooleanValue();
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
