/**
 * 
 */
package org.tipitaka.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;

public class XmlBuilder {
    
    private Writer writer;
    private TipitakaPath path;
    private final DirectoryStructure directory;
    private final Script script;
    private final TipitakaUrlFactory urlFactory;
    
    XmlBuilder(ScriptFactory factory, DirectoryStructure directory, Writer writer, TipitakaPath path, TipitakaUrlFactory urlFactory) throws IOException{
        this.writer = writer;
        this.path = path;
        this.directory = directory;
        this.script = factory.script(path.script);
        this.urlFactory = urlFactory;
    }
    
    public void buildTei() throws IOException{
        URL url = this.urlFactory.sourceURL(script.tipitakaOrgName, directory.fileOf(path.path.replace(".tei.xml", "")));
        System.out.println(url.toString());
        BufferedReader reader = null;
        try {
            try {
                reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-16"));
                copyAndFilterStream(reader);
            } catch (IOException e) {
                e.printStackTrace();
                if (reader != null) {
                    reader.close();
                }
                reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                copyAndFilterStream(reader);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void copyAndFilterStream(BufferedReader reader) throws IOException {
        writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        String line = reader.readLine(); //ignore xml instruction
        line = reader.readLine(); // ignore the xslt processing instruction
        line = reader.readLine();
        while(line != null){
            writer.append(line).append("\n");
            line = reader.readLine();
        }
        writer.flush();
    }

}