/**
 * 
 */
package org.tipitaka.search.solr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class TextXmlDocFactory {
    private XmlPullParserFactory factory;
    private final String script;
    private final TipitakaUrlFactory urlFactory;
    
    public static void main(String... args) throws Exception {
        TextXmlDocFactory factory = new TextXmlDocFactory("deva", new TipitakaUrlFactory());
        TextXmlDoc t = factory.newTextXmlDoc("cscd/s0304m.mul6.xml", new String[0]);
        System.out.println(t.toXml());
    }
    
    public TextXmlDocFactory(String script, TipitakaUrlFactory urlFactory) throws XmlPullParserException{
        factory = XmlPullParserFactory.newInstance();
        this.script = script;
        this.urlFactory = urlFactory;
    }
    
    public TextXmlDoc newTextXmlDoc(String path, String[] context) 
            throws XmlPullParserException, IOException {
        XmlPullParser xpp = factory.newPullParser();
        URL url = urlFactory.newURL(script, path);

        System.out.println("parsing text " + url);
        
        Reader reader = new InputStreamReader(url.openStream(), "UTF-16" );
        xpp.setInput( reader );
     
        StringWriter writer = new StringWriter();
        int eventType = xpp.getEventType();

        while(eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.TEXT) {
               writer.append(xpp.getText().trim()).append(" ");
            }
            eventType = xpp.next();
        }
        
        return new TextXmlDoc(script, path, writer.toString().trim(), context);
    }
}