/**
 * 
 */
package org.tipitaka.search;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


public class ScriptFactory {
    
    private static final Map<String, String> MAP = new HashMap<String, String>();
    private static final Map<String, String> INVERSE = new HashMap<String, String>();
    static {
        MAP.put("romn", "roman");
        MAP.put("deva", "devanagari");
        for(Map.Entry<String, String> entry : MAP.entrySet()){
            INVERSE.put(entry.getValue(), entry.getKey());
        }
    }
    
    private final Map<String, Script> cache = new WeakHashMap<String, Script>();
    
    private final ResourceLocator locator = new ResourceLocator();

    public Collection<String> allScriptNames(){
        return INVERSE.keySet();
    }
    
    public Script script(String scriptName) throws IOException{
        if( MAP.containsKey(scriptName)){
            scriptName = MAP.get(scriptName);
        }
        synchronized (cache) {
            if(!cache.containsKey(scriptName)){
                Script script = new Script(scriptName, INVERSE.get(scriptName));
                script.load(locator.getResourceAsReader(scriptName + ".script"));
                cache.put(scriptName, script);
            }
            return cache.get(scriptName);                
        }
    }

    public Script newScript(String script) {
        return new Script(MAP.get(script), script);
    }
}