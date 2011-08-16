package org.tipitaka.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public abstract class TipitakaOrgVisitor {

    private XmlPullParserFactory factory;
    
    public TipitakaOrgVisitor() throws XmlPullParserException{
        factory = XmlPullParserFactory.newInstance();
    }
        
    public void accept(Writer writer, String script, String path) throws XmlPullParserException, IOException{
        XmlPullParser xpp = factory.newPullParser();
        URL url = TipitakaUrlFactory.newURL(script, path);

        System.out.println("parsing " + url);
        
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-16" ));
            xpp.setInput( reader );
            accept(writer, xpp);
        }
        catch(XmlPullParserException e){
            if( reader != null ){
                reader.close();
            }
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8" ));
            xpp.setInput( reader );
            accept(writer, xpp);
        }
    }
    
    private void accept(Writer writer, XmlPullParser xpp) throws XmlPullParserException, IOException{
        int eventType = xpp.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                visitStartTag(writer, xpp);
            }
            else if (eventType == XmlPullParser.TEXT){
                writer.append(xpp.getText().trim());
            }
            else if (eventType == XmlPullParser.END_TAG) {
                visitEndTag(writer, xpp);
            }
            eventType = xpp.next();
        }
        writer.flush();
    }
    
    private void visitStartTag(Writer writer, XmlPullParser xpp) throws XmlPullParserException, IOException {
        if(xpp.getName().equals("pb")){
            pb(writer, xpp.getAttributeValue(null, "ed"), xpp.getAttributeValue(null, "n"));
        }
        else if(xpp.getName().equals("note")){
            noteStart(writer);
        }
        else if (xpp.getName().equals("p")){
            String rend = xpp.getAttributeValue(null, "rend");
            if("centre".equals(rend)){
                rend = "centered";
            }
            pStart(writer, rend);
        }
        else if (xpp.getName().equals("hi")){
            String rend = xpp.getAttributeValue(null, "rend");
            if("bold".equals(rend)){
                rend = "bld";
            }
            hiStart(writer, rend);
        }
    }
    
    private void visitEndTag(Writer writer, XmlPullParser xpp) throws XmlPullParserException, IOException {
        if(xpp.getName().equals("note")){
            noteEnd(writer);            
        }
        else if (xpp.getName().equals("p")){
            pEnd(writer);
        }
        else if (xpp.getName().equals("hi")){
            hiEnd(writer);
        }
    }

    abstract protected void hiStart(Writer writer, String clazz) throws IOException;
    
    abstract protected void hiEnd(Writer writer) throws IOException;

    abstract protected void pStart(Writer writer, String clazz) throws IOException;
  
    abstract protected void pEnd(Writer writer) throws IOException;
    
    abstract protected void noteStart(Writer writer) throws IOException;

    abstract protected void noteEnd(Writer writer) throws IOException;
    
    abstract protected void pb(Writer writer, String ed, String n) throws IOException;
}
