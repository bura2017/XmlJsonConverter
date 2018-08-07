package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
    final private String namespace;
    final private String schema;
    private boolean ns_flag;

    public JsonToXml(String attrPrefix, String propertyName, String prefixType, String namespace, String schema) {
        this.attrPrefix = attrPrefix;
        this.propertyName = propertyName;
        this.prefixType = prefixType;
        this.namespace = namespace != null ? namespace : "";
        this.schema = schema;
    }

    public String parse (JsonNode input) throws XMLStreamException, FactoryConfigurationError {
        this.ns_flag = true;
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
				if (propertyName.equals(field.getKey())) {
                    writer.writeCharacters(value.asText());
                } else if (!field.getKey().startsWith(attrPrefix)) {
                    if (value.isArray()) {
                        //todo handle array
                        ArrayNode arrayNode = (ArrayNode) value;
                        for (Iterator<JsonNode> jt = arrayNode.iterator(); jt.hasNext(); ) {
                            JsonNode v = jt.next();
                            writer.writeStartElement(field.getKey());
                            this.writeAttributes(v, writer);
                            xmlRepresent(v, writer);
                            writer.writeEndElement();
                        }
                    } else {
                        writer.writeStartElement(field.getKey());
                        addNamespace(writer);
                        this.writeAttributes(value, writer);
                        xmlRepresent(value, writer);
                        writer.writeEndElement();
                    }
                }
			}
        } else {
			writer.writeCharacters(json.asText());
		}
    }
    private void writeAttributes (JsonNode value, XMLStreamWriter writer) throws XMLStreamException {
        for (Iterator<Map.Entry<String, JsonNode>> jt = value.fields(); jt.hasNext();) {
            Map.Entry<String, JsonNode> f = jt.next();
            if (f.getKey().startsWith(attrPrefix)) {
                String localName = f.getKey().substring(attrPrefix.length());
                if ("type".equals(localName)) {
                    writer.writeAttribute(prefixType, namespace, localName, f.getValue().asText());
                } else {
                    writer.writeAttribute(localName, f.getValue().asText());
                }
            }
        }
    }
    private void addNamespace (XMLStreamWriter writer) throws XMLStreamException {
        if (ns_flag) {
            writer.writeNamespace("", namespace);
            ns_flag = false;
            // TODO hard code!!!
            writer.writeAttribute("xmlns:xsi", schema);
        }

    }
}
