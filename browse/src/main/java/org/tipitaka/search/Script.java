/**
 * 
 */
package org.tipitaka.search;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class Script{
    
    public final String name;
    public final String tipitakaOrgName;
    private final Properties words = new Properties();
    
    Script(String name, String tipitakaOrgName){
        this.name = name;
        this.tipitakaOrgName = tipitakaOrgName;
    }
    
    void put(String key, String value){
        words.put(key,value);
    }
    
    String get(String key){
        return words.getProperty(key);
    }
    
    public void save(Writer writer) throws IOException{
        words.store(writer, name + " - " + tipitakaOrgName);
    }
    
    public void load(Reader reader) throws IOException{
        words.load(reader);
    }
}