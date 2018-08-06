package parser;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonToXml {
    // settings
    final private String attrPrefix;
    final private String propertyName;
    final private String prefixType;

    public JsonToXml(String attrPrefix, String propertyName, String prefixType) {
        this.attrPrefix = attrPrefix;
        this.propertyName = propertyName;
        if (!prefixType.endsWith(":")) {
            prefixType = prefixType.concat(":");
        }
        this.prefixType = prefixType;
    }

    public String parse (JsonNode input) {
        String output = new String();
        return output;
    }
}
