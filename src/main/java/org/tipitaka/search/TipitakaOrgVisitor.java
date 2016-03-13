package org.tipitaka.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public abstract class TipitakaOrgVisitor implements Visitor
{

    public static final String OMIT = "<-omit->";
    public static final String NOTE = "<-note->";

    private final XmlPullParserFactory factory;
    protected final TipitakaUrlFactory urlFactory;
    
    public TipitakaOrgVisitor(TipitakaUrlFactory urlFactory) throws XmlPullParserException{
        this.factory = XmlPullParserFactory.newInstance();
        this.urlFactory = urlFactory;
    }
        
    public void accept(Writer writer, Script script, String path) throws IOException{
        URL url = urlFactory.sourceURL(script.tipitakaOrgName, path);

        docStart(writer, url.toString());
        Reader reader = null;
        try {
            XmlPullParser xpp = factory.newPullParser();
            try {
                reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-16"));
                xpp.setInput(reader);
                accept(writer, xpp);
            }
            catch (XmlPullParserException e) {
                if (reader != null) {
                    reader.close();
                }
                reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                xpp.setInput(reader);
                accept(writer, xpp);
            }
        }
        catch (XmlPullParserException e) {
            throw new IOException("pull parser error", e);
        }
        docEnd(writer);
        writer.flush();
    }
    
    private void accept(Writer writer, XmlPullParser xpp) throws XmlPullParserException, IOException{
        int eventType = xpp.getEventType();
        LinkedList<String> state = new LinkedList<String>();
        boolean omit = false;
        boolean note = false;
        String previous = null;
        while(eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String result = visitStartTag(writer, xpp);
                omit = OMIT.equals(result);
                note = NOTE.equals(result);
                state.push(result);
            }
            else if (eventType == XmlPullParser.TEXT){
                if (!omit) {
                    String text = xpp.getText().replaceFirst("^ *", "");
                    if (note) {
                        note = false;
                        noteText(writer, previous, text);
                    }
                    else {
                        previous = text;
                        writer.append(text);
                    }
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                visitEndTag(writer, xpp, state.pop());
                omit = false;
            }
            eventType = xpp.next();
        }
        writer.flush();
    }

    private boolean isot = false;
    private String visitStartTag(Writer writer, XmlPullParser xpp) throws XmlPullParserException, IOException {
        String result = null;
        if(xpp.getName().equals("pb")){
            pb(writer, xpp.getAttributeValue(null, "ed"), xpp.getAttributeValue(null, "n"));
        }
        else if(xpp.getName().equals("note")){
            noteStart(writer);
            result = NOTE;
        }
        else if (xpp.getName().equals("p")){
            result = xpp.getAttributeValue(null, "rend");
            if("centre".equals(result)){
                result = "centered";
            }
            String number = xpp.getAttributeValue(null, "n");
            pStart(writer, result, number);
        }
        else if (xpp.getName().equals("hi")){
            result = xpp.getAttributeValue(null, "rend");
            //if("bold".equals(result)){
            //    result = "bld";
            //}
            result = hiStart(writer, result);
        }
        else {
            //System.err.println(xpp.getName());
        }
        return result;
    }
    
    private void visitEndTag(Writer writer, XmlPullParser xpp, String state) throws XmlPullParserException, IOException {
        if(xpp.getName().equals("note")){
            noteEnd(writer);            
        }
        else if (xpp.getName().equals("p")){
            pEnd(writer, state);
        }
        else if (xpp.getName().equals("hi")){
          if (!"".equals(state)) hiEnd(writer, state);
        }
    }

    abstract protected void docStart(Writer writer, String url) throws IOException;

    abstract protected void docEnd(Writer writer) throws IOException;

    abstract protected String hiStart(Writer writer, String clazz) throws IOException;
    
    abstract protected void hiEnd(Writer writer, final String clazz) throws IOException;

    abstract protected void pStart(Writer writer, String clazz, String number) throws IOException;
  
    abstract protected void pEnd(Writer writer, final String clazz) throws IOException;

    abstract protected void noteStart(Writer writer) throws IOException;

    protected void noteText(Writer writer, String previous, String text) throws IOException {
        writer.append(text);
    }

    abstract protected void noteEnd(Writer writer) throws IOException;
    
    abstract protected void pb(Writer writer, String ed, String n) throws IOException;
}
