/**
 * @author Mohammad Saiful Alam
 * XML parser wrapper for ease to use
 */
package com.microasset.saiful.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLParser {

    org.w3c.dom.Document mDoc = null;

    // constructor
    public XMLParser() {

    }

    /**
     * Load xml content from the asset folder
     * @param context application context
     * @param file the location with name and extension
     */
    public org.w3c.dom.Document loadXmlContent(Context context, String file) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(file);
            mDoc = db.parse(inputStream);
            mDoc.getDocumentElement().normalize();
        } catch (Exception e) {

        }

        return mDoc;
    }

    public org.w3c.dom.Document loadXmlContentFromFile(Context context, String file) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inputStream = new FileInputStream(new File(file));
            mDoc = db.parse(inputStream);
            mDoc.getDocumentElement().normalize();
        } catch (Exception e) {

        }

        return mDoc;
    }


    /**
     * Load xml content from the asset folder
     * @param string application context
     */
    public org.w3c.dom.Document loadXmlFromServer(String xmlResponse) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes());
            mDoc = db.parse(inputStream);
            mDoc.getDocumentElement().normalize();
        } catch (Exception e) {

        }

        return mDoc;
    }

    /**
     * return the NodeList from the tagname
     * @param tagname xml tag name
     */
    public NodeList getElementsByTagName(String tagname) {
        if (mDoc != null) {

            return mDoc.getElementsByTagName(tagname);
        }

        return null;
    }

    /** Getting node value
     * @param elem element
     */
    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {

                        return child.getNodeValue();
                    }
                }
            }
        }

        return "";
    }

    /**
     *
     * @param item
     * @param str
     * @return
     */
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);

        return this.getElementValue(n.item(0));
    }

    /**
     * Return the attribute value from the xml attribute node element
     * @param elem
     * @return
     */
    public final String getElementAttributeValue(org.w3c.dom.Node elem) {

        if (elem != null) {
            if (elem.getNodeType() == Node.ATTRIBUTE_NODE) {
                return elem.getNodeValue();
            }
        }

        return "";
    }

    /**
     * Return the attribute value from the xml node string name with attributename
     * @param node
     * @param attributename
     * @return
     */
    public final String getElementAttributeValue(String node, String attributename) {

        NodeList nodeList = mDoc.getElementsByTagName(node);
        if (nodeList != null) {
            if (nodeList.getLength() > 0) {
                org.w3c.dom.Node xmlnode = nodeList.item(0);
                NamedNodeMap att = xmlnode.getAttributes();
                Node elem = att.getNamedItem(attributename);

                return this.getElementAttributeValue(elem);
            }
        }

        return "";
    }

    /**
     * Return the attribute value from the node element string name with attributename
     * @param node
     * @param attributename
     * @return
     */
    public final String getElementAttributeValue(org.w3c.dom.Node node, String attributename) {

        if (node != null) {
            NamedNodeMap att = node.getAttributes();
            Node elem = att.getNamedItem(attributename);

            return this.getElementAttributeValue(elem);
        }

        return "";
    }
}
