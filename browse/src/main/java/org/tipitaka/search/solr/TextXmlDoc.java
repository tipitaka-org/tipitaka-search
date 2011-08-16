/**
 * 
 */
package org.tipitaka.search.solr;

import java.net.URL;

public class TextXmlDoc {
    
    enum Context { volume, pitaka, book, chapter, section }

    public final String path;
    public final String script;

    public final String[] context = new String[5];
    public final String text;

    public TextXmlDoc(String script, String path, String text, String... context){
        int index = 0;
        
        for(String c: context){
            if(c != null){
                this.context[index++] = c;
            }
        }
        
        this.path = path;
        this.script = script;
        this.text = text;
    }
    
    public void readText(URL baseUrl){
        
    }
    public String toXml(){
        StringBuilder xml = new StringBuilder("<add>\n");
        appendDoc(xml);
        xml.append("</add>");
        return xml.toString();
    }

    public void appendDoc(StringBuilder xml){
        xml.append("  <doc>\n");
        appendField(xml, "id", script + "-" + path.replaceFirst(".xml$", "").replaceFirst("^.*/", ""));
        appendField(xml, "path", path);
        appendField(xml, "script", script);
        for(TextXmlDoc.Context c: Context.values()){
            if (context[c.ordinal()] != null) {
                appendField(xml, c.name(), context[c.ordinal()]);
            }
        }
        appendField(xml, "text", text);
        xml.append("  </doc>\n");
    }
    
    private void appendField(StringBuilder xml, String name, String value){
        xml.append("    <field name=\"").append(name).append("\">");
        xml.append(value);
        xml.append("</field>\n");
    }
}