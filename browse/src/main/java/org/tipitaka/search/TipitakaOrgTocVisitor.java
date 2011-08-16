package org.tipitaka.search;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.tipitaka.search.TipitakaUrlFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class TipitakaOrgTocVisitor {

    static public void main(String... args) throws Exception{
        TipitakaOrgTocVisitor visitor = new TipitakaOrgTocVisitor();
        visitor.accept("romn");
        
        int max = 0;
        for(String[] sign: visitor.map.values()){
            int count = 0;
            for(String s: sign){
                if(s != null) count++;
            }
            if (count > max) max = count;
        }
        System.out.println(max);
    }

    private XmlPullParserFactory factory;
    private Stack<String> stack;
    private String script;
    private Map<String, String[]> map;
    
    public TipitakaOrgTocVisitor() throws XmlPullParserException{
        factory = XmlPullParserFactory.newInstance();
    }
    
    public Map<String, String[]> map(){
        return map;
    }
    
    public void accept(String script) throws XmlPullParserException, IOException{
        this.stack = new Stack<String>();
        this.script = script;
        this.map = new LinkedHashMap<String, String[]>();
        
        acceptPath("tipitaka_toc.xml");
    }
    
    private void acceptPath(String path) throws XmlPullParserException, IOException{
        XmlPullParser xpp = factory.newPullParser();
        URL url = TipitakaUrlFactory.newURL(script, path);

        System.out.println("parsing " + url);
        
        Reader reader = null;
        try {
            reader = new InputStreamReader(url.openStream(), "UTF-16" );
            xpp.setInput( reader );
            accept(xpp);
        }
        catch(XmlPullParserException e){
            if( reader != null ){
                reader.close();
            }
            reader = new InputStreamReader(url.openStream(), "UTF-8" );
            xpp.setInput( reader );
            accept(xpp);
        }
    }
    
    private void accept(XmlPullParser xpp) throws XmlPullParserException, IOException{
        int eventType = xpp.getEventType();
        if(eventType == XmlPullParser.END_DOCUMENT) {
            return;
        } else if (eventType == XmlPullParser.START_TAG) {
            visitTree(xpp);
        } else if (eventType == XmlPullParser.END_TAG) {
            if(!stack.isEmpty()){
                stack.pop();
            }
        }
        xpp.next();
        accept(xpp);
    }
    
    private void visitTree(XmlPullParser xpp) throws XmlPullParserException, IOException {
        switch(xpp.getAttributeCount()){
            case 1:
                stack.push(xpp.getAttributeValue(0));
                break;
            case 2:
                stack.push(xpp.getAttributeValue(0));
                System.out.println(stack + " -> " + xpp.getAttributeValue(1));
                acceptPath(xpp.getAttributeValue(1));
                break;
            case 3:
                stack.push(xpp.getAttributeValue(0));
                System.err.println(stack + " => " + xpp.getAttributeValue(1));
                map.put(xpp.getAttributeValue(1), stack.toArray(new String[stack.size()]));
                break;
            default:
                stack.push(null);
        }
    }
}
