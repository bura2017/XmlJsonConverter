package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class XmlToJson extends DefaultHandler {
    // settings
    final private String attrPrefix;
    final private String propertyName;

    // return value
    private ObjectNode data;
    private String namespace;

    // local variables
    private ObjectMapper mapper;
    private Stack<ObjectNode> path;
    private String element;
    private boolean valued;

    public XmlToJson (String attrPrefix, String propertyName) {
        this.attrPrefix = attrPrefix;
        this.propertyName = propertyName;
    }

    public JsonNode parseFromFile(String filename) throws ParserConfigurationException, SAXException, IOException {
        // fileURL must start from file: and have absolute parse
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(this);
        xmlReader.parse(convertToFileURL(filename));

        return this.data;
    }
    public JsonNode parse (String input) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(this);
        xmlReader.parse(new InputSource(new StringReader(input)));

        return this.data;
    }

    @Override
    public void startDocument() throws SAXException {
        mapper = new ObjectMapper();
        data = mapper.createObjectNode();
        path = new Stack<ObjectNode>();
        path.push(data);
        valued = true;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException{
        if (namespace == null) {
            namespace = namespaceURI;
        }
        if (!valued) {
            addItem(element, mapper.createObjectNode());
        }
        element = localName;
        int len = atts.getLength();
        if (len > 0) {
            ObjectNode attrs = mapper.createObjectNode();
            for (int i = 0; i < len; i++) {
                String key = atts.getLocalName(i);
                String value = atts.getValue(i);
                // add prefix set by settings to attribute keys
                attrs.put(attrPrefix.concat(key), value);
            }
            addItem(localName, attrs);
            valued = true;
        } else {
            valued = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // TODO check data types
        String value = new String(ch, start, length).trim();
        if (!value.isEmpty()){
            String key = valued ? propertyName : element;
            try {
                Integer value_int = Integer.parseInt(value);
                addItem(key, value_int);
            } catch (NumberFormatException e1) {
                try {
                    Double value_double = Double.parseDouble(value);
                    addItem(key, value_double);
                } catch (NumberFormatException e2) {
                    try {
                        if (value.equals("true") || value.equals("false")) {
                            addItem(key, Boolean.parseBoolean(value));
                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e3) {
                        addItem(key, value);
                    }
                }
            }
            if (!valued) {
                path.push(mapper.createObjectNode());
            }
            valued = true;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        path.pop();
    }

    @Override
    public void endDocument() {
        data.put("namespace", namespace);
    }

    private void addItem (String key, ObjectNode obj) {
        JsonNode node = path.peek().get(key);
        if (node == null) {
            // Common case
        	ObjectNode peek = path.peek();
        	peek.set(key, obj);
            path.push(obj);
        } else if (node.isArray()) {
            // Case
            ArrayNode temp = (ArrayNode) node;
            temp.add(obj);
            path.push(obj);
        } else {
            // Relpace node with array
            ArrayNode newNode = mapper.createArrayNode();
            newNode.add(node);
            newNode.add(obj);
            path.peek().replace(key, newNode);
            path.push(obj);
        }
    }

    private void addItem (String key, String obj) {
        if (!obj.startsWith("\"")) {
            obj = "\"".concat(obj);
        }
        if (!obj.endsWith("\"")) {
            obj = obj.concat("\"");
        }
        JsonNode node = path.peek().get(key);
        if (node == null) {
            // Common case
            path.peek().putPOJO(key, obj);
        } else if (node.isArray()) {
            // Case
            ArrayNode temp = (ArrayNode) node;
            temp.add(obj);
        } else {
            // Relpace node with array
            ArrayNode newNode = mapper.createArrayNode();
            newNode.add(node);
            newNode.add(obj);
            path.peek().replace(key, newNode);
        }
    }

    private void addItem (String key, Integer obj) {
        JsonNode node = path.peek().get(key);
        if (node == null) {
            // Common case
        	ObjectNode peek = path.peek();
        	peek.put(key, obj);
        } else if (node.isArray()) {
            // Case
            ArrayNode temp = (ArrayNode) node;
            temp.add(obj);
        } else {
            // Relpace node with array
            ArrayNode newNode = mapper.createArrayNode();
            newNode.add(node);
            newNode.add(obj);
            path.peek().replace(key, newNode);
        }
    }
    private void addItem (String key, Double obj) {
        JsonNode node = path.peek().get(key);
        if (node == null) {
            // Common case
        	ObjectNode peek = path.peek();
        	peek.put(key, obj);
        } else if (node.isArray()) {
            // Case
            ArrayNode temp = (ArrayNode) node;
            temp.add(obj);
        } else {
            // Relpace node with array
            ArrayNode newNode = mapper.createArrayNode();
            newNode.add(node);
            newNode.add(obj);
            path.peek().replace(key, newNode);
        }
    }
    private void addItem (String key, Boolean obj) {
        JsonNode node = path.peek().get(key);
        if (node == null) {
            // Common case
        	ObjectNode peek = path.peek();
        	peek.put(key, obj);
            //peek.putPOJO(key, obj);
        } else if (node.isArray()) {
            // Case
            ArrayNode temp = (ArrayNode) node;
            temp.add(obj);
        } else {
            // Relpace node with array
            ArrayNode newNode = mapper.createArrayNode();
            newNode.add(node);
            newNode.add(obj);
            path.peek().replace(key, newNode);
        }
    }

    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }
}
