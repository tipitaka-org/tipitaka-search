/**
 * 
 */
package org.tipitaka.search.solr;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.tipitaka.search.TipitakaOrgTocVisitor;
import org.xmlpull.v1.XmlPullParserException;

public class TipitakaSolrIndexer {
    private URL updateUrl;

    public static void main(String... args) throws Exception {
        TipitakaSolrIndexer indexer = new TipitakaSolrIndexer("http://localhost:8983/solr/update");
        indexer.index("romn");
//        TipitakaTextFactory factory = new TipitakaTextFactory("romn");
//        indexer.updateIndex(factory.newTipitakaText("cscd/s0304m.mul6.xml", new String[0]));
//        indexer.commit();
    }
    
    public TipitakaSolrIndexer(URL updateUrl){
        this.updateUrl = updateUrl;
    }
    
    public TipitakaSolrIndexer(String updateUrl) throws MalformedURLException{
        this(new URL(updateUrl));
    }
    
    public void index(String script) throws XmlPullParserException, IOException{
        TipitakaOrgTocVisitor visitor = new TipitakaOrgTocVisitor();
        visitor.accept(script);
        TextXmlDocFactory factory = new TextXmlDocFactory(script);
        for(Map.Entry<String, String[]> entry: visitor.map().entrySet()){
            updateIndex(factory.newTextXmlDoc(entry.getKey(), entry.getValue()));
        }
        commit();
    }

    public void commit() throws IOException {
        post("<commit/>");        
    }
    
    public HttpURLConnection openConnection() throws IOException{
        HttpURLConnection con = (HttpURLConnection) updateUrl.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/xml; charset: utf-8");
        con.setRequestMethod("POST");
        con.connect();
        return con;
    }
    
    public void updateIndex(TextXmlDoc text) throws IOException {
        post(text.toXml());
    }
    
    private void post(String message) throws IOException {
        HttpURLConnection con = openConnection();
        System.err.println(message);
        Writer writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
        writer.append(message);
        writer.flush();
        con.disconnect();
        System.err.println(con.getResponseCode() + " " + con.getResponseMessage());
    }
}