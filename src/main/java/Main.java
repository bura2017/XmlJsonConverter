import com.fasterxml.jackson.databind.JsonNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import parser.JsonToXml;
import parser.XmlToJson;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends DefaultHandler {

    public static void main (String [] args) throws IOException, ParserConfigurationException, SAXException, XMLStreamException, FactoryConfigurationError {
        if (args.length < 1) {
            throw new IOException();
        }
        String filename = args[0];
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        String input = new String(encoded, Charset.defaultCharset());

        XmlToJson parser_xml = new XmlToJson("_", "#");
        JsonNode data_json = parser_xml.parse(input);

        JsonToXml parser_json = new JsonToXml("_", "#", "xsi");
        String data_xml = parser_json.parse(data_json);
        System.out.println(data_xml);
    }
}