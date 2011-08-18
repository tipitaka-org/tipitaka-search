/**
 * 
 */
package org.tipitaka.search;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TipitakaUrlFactory {
    
    private final String baseUrl;

    public TipitakaUrlFactory(){
        this("http://www.tipitaka.org/");
    }
    
    public TipitakaUrlFactory(File url){
        baseUrl  = url.toURI().toString();
    }

    public TipitakaUrlFactory(String url){
        baseUrl  = url;
    }

    public URL newURL(String script, String path) throws MalformedURLException{
        return new URL(baseUrl + script + "/" + path);
    }
    
}