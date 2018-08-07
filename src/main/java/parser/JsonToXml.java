package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.POJONode;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class JsonToXml {
    // settings
    final private String attrPrefix;
    final private String propertyName;
    final private String prefixType;
    final private String xmlInsertName;

    private Stack<String> path_field;
    private Stack<JsonNode> path;
    private String input;

    public JsonToXml(String attrPrefix, String propertyName, String prefixType) {
        this.attrPrefix = attrPrefix;
        this.propertyName = propertyName;
        if (!prefixType.endsWith(":")) {
            prefixType = prefixType.concat(":");
        }
        this.prefixType = prefixType;
        if (propertyName.equals("xmlInsertName")) {
            xmlInsertName = "xmlInsertName#";
        } else {
            xmlInsertName = "xmlInsertName";
        }
    }

    public String parse (JsonNode input) throws XMLStreamException, FactoryConfigurationError {
    	StringWriter writer = new StringWriter();
    	final XMLStreamWriter output = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
    	output.writeStartDocument();
    	xmlRepresent(input, output);
    	output.writeEndDocument();
        return writer.toString();
    }
    public void xmlRepresent(JsonNode json, XMLStreamWriter writer) throws XMLStreamException {
		if (json.isObject()) {
			for (Iterator<Map.Entry<String, JsonNode>> it = json.fields(); it.hasNext();) {
				Map.Entry<String, JsonNode> field = it.next();
				final JsonNode value = field.getValue();
				writer.writeStartElement(field.getKey());
				xmlRepresent(value, writer);
				writer.writeEndElement();
			}
		} else {
			writer.writeCharacters(json.asText());
		}
    }
}
