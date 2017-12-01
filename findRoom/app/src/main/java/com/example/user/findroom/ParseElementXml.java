package com.example.user.findroom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
/**
 * Created by user on 2017-11-29.
 */

public class ParseElementXml {
    private String getstr="";

    public String GetElementXml(String xmlParam, String tagName) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xmlParam.getBytes());
            Document doc = docBuilder.parse(is);
            NodeList resultNodes = doc.getElementsByTagName(tagName);

            getstr = resultNodes.item(0).getFirstChild().getNodeValue();

            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getstr;
    }
}
