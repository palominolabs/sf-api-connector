/*
 * Copyright © 2013. Palomino Labs (http://palominolabs.com)
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

package com.palominolabs.crm.sf.soap;

import com.palominolabs.crm.sf.core.Id;
import com.palominolabs.crm.sf.core.SObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains convenience methods for manipulating SObjects.
 *
 * @author Marshall Pierce <marshall@palominolabs.com>
 */
@Immutable
final class SObjects {

    /**
     * used to create Document objects
     */
    private static final EmptyDocumentFactory DOC_FACTORY = new EmptyDocumentFactory();

    private SObjects() {

    }

    /**
     * Convert an individual SObject stub into a facade SObject.
     *
     * @param stubSObject the stub sobject to convert
     *
     * @return SObject, or null if the input was null
     *
     * @throws SObjectConversionException if conversion fails
     */
    @Nullable
    private static PartnerSObject convertStubSObjectToFacadeSObject(
            @Nullable com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject stubSObject)
            throws SObjectConversionException {

        if (stubSObject == null) {
            return null;
        }

        PartnerSObjectImpl newSObject;

        // if the sobject was queried with Id, create the facade sobject with an id
        if (stubSObject.getId() == null) {
            newSObject = PartnerSObjectImpl.getNew(stubSObject.getType());
        } else {
            newSObject = PartnerSObjectImpl.getNewWithId(stubSObject.getType(), new Id(stubSObject.getId()));
        }

        List<Object> fields = stubSObject.getAny();

        // see http://wiki.apexdevnet.com/index.php/PartnerQuery


        /*

         For dot-syntax relationship queries:

        <queryResponse>
          <result xsi:type="QueryResult">
            <done>true</done>
            <queryLocator xsi:nil="true"/>
            <records xsi:type="sf:sObject">
              <sf:type>Account</sf:type>
              <sf:Id xsi:nil="true"/>
              <sf:Owner xsi:type="sf:sObject">
                <sf:type>User</sf:type>
                <sf:Id xsi:nil="true"/>
                <sf:Name>sftestorg3 mpierce</sf:Name>
              </sf:Owner>
            </records>
            <size>1</size>
          </result>
        </queryResponse>
         */

        for (Object fieldObj : fields) {
            Element xmlElt = (Element) fieldObj;

            String xsiTypeValue = xmlElt.getAttribute("xsi:type");

            String fieldName = xmlElt.getLocalName();

            if ("QueryResult".equals(xsiTypeValue)) {
                PartnerQueryResult subqueryResult = parseQueryResult(xmlElt);
                newSObject.setRelationshipQueryResult(fieldName, subqueryResult);
            } else if ("sf:sObject".equals(xsiTypeValue)) {
                PartnerSObject subObj = parseSObject(xmlElt, fieldName);
                newSObject.setRelationshipSubObject(fieldName, subObj);
            } else {
                String fieldValue = extractFieldValue(xmlElt);

                newSObject.setField(fieldName, fieldValue);
            }
        }

        return newSObject;
    }

    /**
     * @param fieldNode the xml element for an individual field in the <any> part of an SObject
     *
     * @return the string value, or null if no text node was found
     */
    @Nullable
    private static String extractFieldValue(@Nonnull Node fieldNode) {
        // this is the text node inside the element, or null if no such child.
        // This (coincidentally) is the case when a field is sent over with xsi:nil="true".
        // TODO really check for xsi:nil
        Node firstChild = fieldNode.getFirstChild();

        if (firstChild != null) {
            // for text nodes, this is the content of the node
            return firstChild.getNodeValue();
        }
        return null;
    }

    /**
     * @param qrElement the dom node that is the root of the query result
     *
     * @return a QueryResult
     *
     * @throws SObjectConversionException if the data cannot be extracted from the xml
     */
    @Nonnull
    private static PartnerQueryResult parseQueryResult(@Nonnull Element qrElement) throws SObjectConversionException {

        /*
       QR structure:
       <complexType name="QueryResult">
           <sequence>
               <element name="done" type="xsd:boolean"/>
               <element name="queryLocator" type="tns:QueryLocator" nillable="true"/>
               <element name="records" type="ens:sObject" nillable="true"
                   minOccurs="0" maxOccurs="unbounded"/>
               <element name="size" type="xsd:int"/>
           </sequence>
       </complexType>

       <simpleType name="QueryLocator">
           <restriction base="xsd:string"/>
       </simpleType>
        */

        /* For subqueries:

        <queryResponse>
          <result xsi:type="QueryResult">
            <done>true</done>
            <queryLocator xsi:nil="true"/>
            <records xsi:type="sf:sObject">
              <sf:type>Account</sf:type>
              <sf:Id>0015000000WWD7bAAH</sf:Id>
              <sf:Id>0015000000WWD7bAAH</sf:Id>
              <sf:Name>United Oil &amp; Gas, Singapore</sf:Name>
              <sf:AnnualRevenue xsi:nil="true"/>
              <sf:Contacts xsi:type="QueryResult">              <!-- start of sub query result -->
                <done>true</done>
                <queryLocator xsi:nil="true"/>
                <records xsi:type="sf:sObject">
                  <sf:type>Contact</sf:type>
                  <sf:Id>0035000000km1owAAA</sf:Id>
                  <sf:Id>0035000000km1owAAA</sf:Id>
                  <sf:FirstName>Liz</sf:FirstName>
                  <sf:Email>ldcruz@uog.com</sf:Email>
                </records>
                <records xsi:type="sf:sObject">
                  <sf:type>Contact</sf:type>
                  <sf:Id>0035000000km1ovAAA</sf:Id>
                  <sf:Id>0035000000km1ovAAA</sf:Id>
                  <sf:FirstName>Tom</sf:FirstName>
                  <sf:Email>tripley@uog.com</sf:Email>
                </records>
                <size>2</size>
              </sf:Contacts>                                    <!-- end of sub query result -->
              <sf:Tasks xsi:nil="true"/>                        <!-- empty sub query result -->
              <sf:Cases xsi:type="QueryResult">
                <done>true</done>
                <queryLocator xsi:nil="true"/>
                <records xsi:type="sf:sObject">
                  <sf:type>Case</sf:type>
                  <sf:Id>5005000000AaQxoAAF</sf:Id>
                  <sf:Id>5005000000AaQxoAAF</sf:Id>
                  <sf:Subject>Maintenance guidelines for generator unclear</sf:Subject>
                </records>
                <records xsi:type="sf:sObject">
                  <sf:type>Case</sf:type>
                  <sf:Id>5005000000AaQxtAAF</sf:Id>
                  <sf:Id>5005000000AaQxtAAF</sf:Id>
                  <sf:Subject>Frequent mechanical breakdown</sf:Subject>
                </records>
                <records xsi:type="sf:sObject">
                  <sf:type>Case</sf:type>
                  <sf:Id>5005000000AaQxpAAF</sf:Id>
                  <sf:Id>5005000000AaQxpAAF</sf:Id>
                  <sf:Subject>Electronic panel fitting loose</sf:Subject>
                </records>
                <size>3</size>
              </sf:Cases>
            </records>                                          <!-- bottom of one sobject that contained subqueries -->
            <size>1</size>
          </result>
        </queryResponse>
         */

        NodeList childNodes = qrElement.getChildNodes();

        if (childNodes.getLength() < 3) {
            throw new SObjectConversionException("Query result element had only " + childNodes.getLength() + " nodes");
        }

        Node doneNode = childNodes.item(0);
        checkNodeIsElement(doneNode, "done");

        boolean isDone = Boolean.parseBoolean(doneNode.getTextContent());

        List<PartnerSObject> sObjects = new ArrayList<PartnerSObject>();

        // the last node is "size", not an SObject
        for (int i = 2; i < childNodes.getLength() - 1; i++) {
            sObjects.add(parseSObject(childNodes.item(i), "records"));
        }

        Node sizeNode = childNodes.item(childNodes.getLength() - 1);
        Element sizeElt = checkNodeIsElement(sizeNode, "size");

        int sizeInt = Integer.parseInt(sizeElt.getTextContent());

        if (isDone) {
            return PartnerQueryResultImpl.getDone(sObjects, sizeInt);
        }

        // not done, so extract the locator
        Node queryLocNode = childNodes.item(1);
        Element queryLocElement = checkNodeIsElement(queryLocNode, "queryLocator");

        String locXsiNil = queryLocElement.getAttribute("xsi:nil");

        if ("true".equals(locXsiNil)) {
            throw new SObjectConversionException("Got a nil locator with a not-done query result");
        }

        // if xsi:nil is something other than true, it must be a real locator
        String locatorString = queryLocElement.getTextContent();

        return PartnerQueryResultImpl.getNotDone(sObjects, sizeInt, new PartnerQueryLocator(locatorString));
    }

    /**
     * Check that a node is non-null, is an element, and has the expected local name. If all checks pass, the node is
     * cast to an element and returned.
     *
     * @param node               the node to check
     * @param desiredElementName the element name that we're expecting the node to be
     *
     * @return the node as an element
     *
     * @throws SObjectConversionException if the node is null or the node has the wrong local name
     */
    private static Element checkNodeIsElement(@Nullable Node node, @Nonnull String desiredElementName)
            throws SObjectConversionException {
        if (node == null) {
            throw new SObjectConversionException("No node found, expecting " + desiredElementName);
        }

        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new SObjectConversionException("Node is not an element, type is " + node.getNodeType());
        }

        if (!node.getLocalName().equals(desiredElementName)) {
            throw new SObjectConversionException(
                    "node was <" + node.getLocalName() + "> instead of " + desiredElementName);
        }

        return (Element) node;
    }

    private static PartnerSObject parseSObject(@Nonnull Node sObjectNode, String expectedNodeLocalName) throws SObjectConversionException {
        Element sObjElt = checkNodeIsElement(sObjectNode, expectedNodeLocalName);

        String parentNodeTypeStr = sObjElt.getAttribute("xsi:type");
        if (!"sf:sObject".equals(parentNodeTypeStr)) {
            throw new SObjectConversionException(
                    "Type of the sobject node is <" + parentNodeTypeStr + "> instead of xsi:type");
        }

        /*
        SObject schema
        <complexType name="sObject">
            <sequence>
                <element name="type" type="xsd:string"/>
                <element name="fieldsToNull" type="xsd:string" nillable="true" minOccurs="0" maxOccurs="unbounded"/>
                <element name="Id" type="tns:ID" nillable="true"/>
                <any namespace="##targetNamespace" minOccurs="0" maxOccurs="unbounded" processContents="lax"/>
            </sequence>
        </complexType>
         */

        /*
        Sample:
        <records xsi:type="sf:sObject">
              <sf:type>Contact</sf:type>
              <sf:Id xsi:nil="true"/>
              <sf:FirstName>Stella</sf:FirstName>
              <sf:LastName>Pavlova</sf:LastName>
        </records>
         */

        NodeList sObjChildNodes = sObjElt.getChildNodes();

        Node typeNode = sObjChildNodes.item(0);
        checkNodeIsElement(typeNode, "type");
        String sObjTypeStr = typeNode.getTextContent();

        Node fieldsToNullOrId = sObjChildNodes.item(1);
        Element idElt = checkNodeIsElement(fieldsToNullOrId, "Id");

        PartnerSObject sObj;
        if ("true".equals(idElt.getAttribute("xsi:nil"))) {
            // id is null
            sObj = PartnerSObjectImpl.getNew(sObjTypeStr);
        } else {
            Id id = new Id(idElt.getTextContent());
            sObj = PartnerSObjectImpl.getNewWithId(sObjTypeStr, id);
        }

        for (int i = 2; i < sObjChildNodes.getLength(); i++) {
            Node fieldNode = sObjChildNodes.item(i);

            String fieldName = fieldNode.getLocalName();
            String fieldValue = extractFieldValue(fieldNode);
            sObj.setField(fieldName, fieldValue);
        }

        return sObj;
    }

    /**
     * Convert an instance of our facade sobject into a corresponding stub objecta
     *
     * @param facadeSObject the sobject to convert
     *
     * @return a stub sobject
     *
     * @throws SObjectConversionException if xml wrangling fails
     */
    @Nonnull
    static com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject convertFacadeSObjectToStubSObject(
            @Nonnull SObject facadeSObject) throws SObjectConversionException {

        com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject stub =
                new com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject();

        if (facadeSObject.getId() == null) {
            stub.setId(null);
        } else {
            stub.setId(facadeSObject.getId().toString());
        }

        stub.setType(facadeSObject.getType());

        List<Object> stubFields = stub.getAny();

        Document doc = DOC_FACTORY.newDocument();

        // create an Element for each field containing a Node that has the value

        Iterator<String> fieldNameIter = facadeSObject.getAllFields().keySet().iterator();

        Element fieldElt;
        Node valueNode;

        String fieldName;
        String value;
        while (fieldNameIter.hasNext()) {
            fieldName = fieldNameIter.next();
            value = facadeSObject.getField(fieldName);

            if (value == null) {
                stub.getFieldsToNull().add(fieldName);
            } else {
                try {
                    fieldElt = doc.createElement(fieldName);

                    valueNode = doc.createTextNode(value);

                    fieldElt.appendChild(valueNode);
                } catch (DOMException e) {
                    throw new SObjectConversionException(
                            "Couldn't create DOM nodes for field name <" + fieldName + "> and value <" + value + ">",
                            e);
                }
                stubFields.add(fieldElt);
            }
        }

        return stub;
    }

    /**
     * Convert a whole list of soap stub SObjects into facade SObjects
     *
     * @param stubSObjects list of stub sobjects
     *
     * @return list of facade SObjects
     *
     * @throws SObjectConversionException if the facade sobjects can't be extracted
     */
    @SuppressWarnings("TypeMayBeWeakened")
    static List<PartnerSObject> convertStubListToSObjectList(
            List<com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject> stubSObjects)
            throws SObjectConversionException {

        List<PartnerSObject> sObjects = new ArrayList<PartnerSObject>();

        for (com.palominolabs.crm.sf.soap.jaxwsstub.partner.SObject stub : stubSObjects) {
            sObjects.add(convertStubSObjectToFacadeSObject(stub));
        }

        return sObjects;
    }

    /**
     * Creating a DocumentBuilderFactory and DocumentBuilder is 2000x slower (really! I benchmarked!) than creating a
     * Document from an existing DocumentBuilder. Thus, we create only one factory and builder by using an instance of
     * this class as a static field in the parent class.
     *
     * @author Marshall Pierce <marshall@palominolabs.com>
     */
    @ThreadSafe
    private static final class EmptyDocumentFactory {

        /**
         * The doc builder is not necessarily thread safe, so access to it must be synchronized.
         */
        private final DocumentBuilder docBuilder;

        /**
         * Non-private for performance.
         */
        EmptyDocumentFactory() {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

            try {
                this.docBuilder = docBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException("Somehow the doc builder factory couldn't create a doc builder", e);
            }
        }

        /**
         * Non-private for performance.
         *
         * @return a new Document object
         */
        synchronized Document newDocument() {
            return this.docBuilder.newDocument();
        }
    }
}
