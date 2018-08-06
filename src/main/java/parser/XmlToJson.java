package parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

public class XmlToJson extends DefaultHandler {
    // settings
    final private String attrPrefix;
    final private String propertyName;

    // return value
    public ObjectNode data;

    // local variables
    private ObjectMapper mapper;
    private Stack<ObjectNode> path;
    private String element;
    private String schema;
    private boolean valued;

    public XmlToJson (String attrPrefix, String propertyName) {
        this.attrPrefix = attrPrefix;
        this.propertyName = propertyName;
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
        //TODO hold arrays
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
            if (valued) {
                path.peek().put(propertyName, value);
            } else {
                path.peek().put(element, value);
            }
            path.push(mapper.createObjectNode());
            valued = true;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        path.pop();
    }

    private void addItem (String key, ObjectNode obj) {
        path.peek().putPOJO(key, obj);
        path.push(obj);
    }

}
