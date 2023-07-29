/**
 * 
 */
package org.tipitaka.search;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TipitakaUrlFactory {

    public static final String TIPITAKA_ORG = "https://tipitaka.org";

    private final String baseUrl;

    public TipitakaUrlFactory(){
        baseUrl = TIPITAKA_ORG;
    }
    
    public TipitakaUrlFactory(File mirrorUrl){
        baseUrl  = mirrorUrl.toURI().toString();
    }

    public TipitakaUrlFactory(URL url){
        baseUrl  = url.toString();
    }

    public TipitakaUrlFactory(String url){
        baseUrl  = url;
    }

    public URL sourceURL(String script, String path) throws MalformedURLException {
        if (!script.startsWith("/")) {
            script = "/" + script;
        }
        return new URL(baseUrl + script + "/" + path);
    }

    public URL sourceURL(Script script, String path) throws MalformedURLException {
        return new URL(baseUrl + "/" + script.tipitakaOrgName + "/" + path);
    }

    public URL normativeURL(Script script, String path)  throws MalformedURLException {
        return new URL(TIPITAKA_ORG + "/" + script.tipitakaOrgName + "/" + path);
    }
    
}