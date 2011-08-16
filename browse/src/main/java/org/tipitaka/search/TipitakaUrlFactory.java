/**
 * 
 */
package org.tipitaka.search;

import java.net.MalformedURLException;
import java.net.URL;

public class TipitakaUrlFactory {
    
    private TipitakaUrlFactory(){}
    
    public static URL newURL(String script, String path) throws MalformedURLException{
        return new URL("http://www.tipitaka.org/" + script + "/" + path);
    }
    
}