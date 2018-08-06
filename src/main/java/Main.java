import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import parser.XmlToJson;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class Main extends DefaultHandler {

    public static void main (String [] args) throws SAXException, ParserConfigurationException, IOException {
        if (args.length < 1) {
            throw new IOException();
        }
        String filename = args[0];

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        XmlToJson converter = new XmlToJson("_", "#");
        xmlReader.setContentHandler(converter);
        xmlReader.parse(convertToFileURL(filename));
        System.out.println(converter.data);
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