/**
 * 
 */
package org.tipitaka.search;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class ResourceLocator {
    
    public InputStream getResourceAsStream(String path) throws FileNotFoundException {
        InputStream in = null;
        if(System.getProperty("solr.solr.home") != null){
            in = new FileInputStream(System.getProperty("solr.solr.home") + "/tipitaka/" + path);
        }
        if(in == null){
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if(in == null){
                in = new FileInputStream("./" + path);
                if(in == null){
                    throw new FileNotFoundException(path + " not found in classloader or current directory");
                }
            }
        }
        return in;
    }
    
    public Reader getResourceAsReader(String path) throws FileNotFoundException{
        return new InputStreamReader(getResourceAsStream(path), Charset.forName("UTF-8"));
    }
}