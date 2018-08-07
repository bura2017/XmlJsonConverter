package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.POJONode;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

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

    public String parse (JsonNode input) {
        return xmlRepresent(input);
    }
    public String xmlRepresent(JsonNode json) {
        String out = new String();
        for (Iterator<Map.Entry<String,JsonNode>> it = json.fields(); it.hasNext(); ) {
            Map.Entry<String,JsonNode> field = it.next();
            final JsonNode value = field.getValue();
            if (value instanceof POJONode) {
				POJONode new_name = (POJONode) value;
				final Object pojo = new_name.getPojo();
				out = out.concat("<").concat(field.getKey()).concat(">").concat(xmlRepresent((JsonNode) pojo))
	                    .concat("</").concat(field.getKey()).concat(">");
			} else {
				out = out.concat("<").concat(field.getKey()).concat(">").concat(xmlRepresent(value))
	                    .concat("</").concat(field.getKey()).concat(">");
			}
        }
        if (!out.isEmpty()) {
            return out;
        } else {
            return json.asText();
        }
    }
}
