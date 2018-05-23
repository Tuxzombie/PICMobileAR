package dk.picit.picmobilear.service;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.util.List;

public class XmlService {

    public String writeXml(List<String> messages){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "webqueryresponce");
                serializer.startTag("", "responce");
                    serializer.startTag("", "updatestatus");
                    serializer.text("000");
                    serializer.endTag("", "updatestatus");
                serializer.endTag("", "responce");
            serializer.endTag("", "webqueryresponce");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
