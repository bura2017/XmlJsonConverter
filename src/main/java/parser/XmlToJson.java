package parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XmlToJson extends DefaultHandler {
    // settings
    final private String attrPrefix;
    final private String propertyName;

    // return value
    public ObjectNode data;

    // local variables
    private ObjectMapper mapper;
    private ArrayList<ObjectNode> path;
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
        path = new ArrayList<>();
        path.add(data);
        valued = true;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException{
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
        if (valued) {
            path.get(path.size() - 1).put(propertyName, new String(ch, start, length));
        } else {
            path.get(path.size() - 1).put(element, new String(ch, start, length));
        }
        valued = true;
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        path.remove(path.size() - 1);
    }

    private void addItem (String key, ObjectNode obj) {
        path.get(path.size() - 1).putPOJO(key, obj);
        path.add(obj);
    }
}
