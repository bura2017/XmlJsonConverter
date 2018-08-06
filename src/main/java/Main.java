import com.fasterxml.jackson.databind.JsonNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import parser.XmlToJson;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Main extends DefaultHandler {

    public static void main (String [] args) throws SAXException, ParserConfigurationException, IOException {
        if (args.length < 1) {
            throw new IOException();
        }
        String filename = args[0];

        XmlToJson converter = new XmlToJson("_", "#");
        JsonNode data = converter.parse(convertToFileURL(filename));
        System.out.println(data);
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