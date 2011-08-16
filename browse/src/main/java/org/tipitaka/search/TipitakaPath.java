/**
 * 
 */
package org.tipitaka.search;

public class TipitakaPath {
    public enum Type {
        HTML, MAIN, NONE, TEI, XML
    }
    
    public final String script;
    public final String path;
    public final String prefix;
    
    public TipitakaPath(String prefix, String fullpath){
        this.prefix = prefix;
        if(fullpath.length() <= 1){
            script = "roman";
            path = "roman";
        }
        else {
            int index = fullpath.indexOf("/", 1);
            script = index < 0 ? fullpath.substring(1) : fullpath.substring(1, index);
            path = index < 0 ? "/" : fullpath.substring(index);
        }
    }
    
    public Type type(){
        if(path == null){
            return Type.NONE;
        }
        else if(path.endsWith(".html")){
            return Type.HTML;
        }
        else if(path.endsWith(".tei.xml")){
            return Type.TEI;
        }
        else if(path.endsWith(".xml")){
            return Type.XML;
        }
        else {
            return Type.MAIN;
        }
    }
}