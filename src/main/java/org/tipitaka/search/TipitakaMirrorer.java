/**
 * 
 */
package org.tipitaka.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

public class TipitakaMirrorer {

    private final TipitakaUrlFactory urlFactory = new TipitakaUrlFactory();
    private final TipitakaUrlFactory mirrorFactory;

    public static void main(String... args) throws Exception {
        TipitakaMirrorer mirrorer = new TipitakaMirrorer("/home/kristian");
        mirrorer.mirror("romn");
    }
    
    public TipitakaMirrorer(File basedir){
        mirrorFactory = new TipitakaUrlFactory(basedir.toURI().toString());
    }
    
    public TipitakaMirrorer(String basedir) throws MalformedURLException{
        this(new File(basedir));
    }
    
    public void mirror(String script) throws XmlPullParserException, IOException{
        TipitakaOrgTocVisitor visitor = new TipitakaOrgTocVisitor(mirrorFactory){

            @Override
            protected void acceptPath(String script, String path)
                    throws XmlPullParserException, IOException {
                copy(script, path);
                super.acceptPath(script, path);
            }
            
        };
        visitor.accept(script);
        for(Map.Entry<String, String[]> entry: visitor.map().entrySet()){
            copy(script, entry.getKey());
        }
    }
    
    

    private void copy(String script, String path) throws IOException {
        URL from = urlFactory .newURL(script, path);
        URL to = mirrorFactory.newURL(script, path);
        File file = new File(to.getPath());
        file.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try { 
            URLConnection con = from.openConnection();
            System.err.print(script + "/" + path);
            if(file.lastModified() < con.getLastModified()){
                in = new BufferedInputStream(con.getInputStream());
                out = new BufferedOutputStream(new FileOutputStream(to.getPath()));
                int b = in.read();
                while( b != -1){
                    out.write(b);
                    b = in.read();
                }
                out.close();
                file.setLastModified(con.getLastModified());
                System.err.println( " - stored" );
            }
            else {
                System.err.println( " - up to date" );
            }
        }
        finally {
            if( in != null ){
                in.close();
            }
            if( out != null ){
                out.close();
            }
        }
    }
}