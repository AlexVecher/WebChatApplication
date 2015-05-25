package bsu.famcs.chat.storage.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bsu.famcs.chat.model.Message;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public final class XMLHistoryUtil {
    private static final String XML_LOCATION = "C:\\TMP\\history.xml";
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATE = "sendDate";

    private XMLHistoryUtil() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        System.out.println(XML_LOCATION);
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement(MESSAGES);
        document.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(XML_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addMessage(Message message) throws ParserConfigurationException, IOException,
            SAXException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(XML_LOCATION);

        Element rootElement = document.getDocumentElement();

        Element messageElement = document.createElement(MESSAGE);
        rootElement.appendChild(messageElement);
        messageElement.setAttribute(ID, message.getId());

        Element userNameElement = document.createElement(NAME);
        userNameElement.appendChild(document.createTextNode(message.getUserName()));
        messageElement.appendChild(userNameElement);

        Element messElement = document.createElement("text");
        messElement.appendChild(document.createTextNode(message.getMsgText()));
        messageElement.appendChild(messElement);

        Element sendDateElement = document.createElement(DATE);
        sendDateElement.appendChild(document.createTextNode(message.getDate()));
        messageElement.appendChild(sendDateElement);


        Transformer transformer = getTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(XML_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized List<Message> getMessages() throws SAXException, IOException, ParserConfigurationException {
        List<Message> messages = new ArrayList<Message>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(XML_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList taskList = root.getElementsByTagName(MESSAGE);
        for (int i = 0; i < taskList.getLength(); i++) {
            Element messageElement = (Element) taskList.item(i);
            String id = messageElement.getAttribute(ID);
            String userName = messageElement.getElementsByTagName(NAME).item(0).getTextContent().trim();
            String msgText = messageElement.getElementsByTagName("text").item(0).getTextContent().trim();
            String sendDate = messageElement.getElementsByTagName(DATE).item(0).getTextContent().trim();
            messages.add(new Message(userName, msgText, id, sendDate));
        }
        return messages;
    }

    public static synchronized Message updateMessage(Message message) throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, TransformerException, NullPointerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(XML_LOCATION);
        Node update = getNodeById(document, message.getId());
        String userName = null;
        String sendDate = null;
        if(update!=null) {
            NodeList nodeList = update.getChildNodes();
            for (int i = nodeList.getLength() - 1; i >= 0; i--) {
                Node node = nodeList.item(i);

                if (NAME.equals(node.getNodeName())) {
                    userName = node.getTextContent();
                }
                if (MESSAGE.equals(node.getNodeName())) {
                    node.setTextContent(message.getMsgText());
                }
                if (DATE.equals(node.getNodeName())) {
                    sendDate = node.getTextContent();
                }
            }

            Transformer transformer = getTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(XML_LOCATION);
            transformer.transform(source, result);
        }
        else {
            throw new NullPointerException();
        }
        return new Message(userName, message.getMsgText(), message.getId(), message.getDate());
    }

    private static Node getNodeById(Document document, String id) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression = xPath.compile("//" + MESSAGE + "[@id='" + id + "']");
        return (Node) xPathExpression.evaluate(document, XPathConstants.NODE);
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    public static synchronized boolean isStorageExist() {
        File file = new File(XML_LOCATION);
        return file.exists();
    }
}
