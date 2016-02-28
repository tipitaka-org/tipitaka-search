package org.tipitaka.search;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.xmlpull.v1.XmlPullParserException;

public class TipitakaOrgVisitorIndexHtml implements Visitor {

    private final DirectoryStructure directory;

    public TipitakaOrgVisitorIndexHtml(TipitakaUrlFactory urlFactory) throws XmlPullParserException, IOException {
        this(urlFactory, null);
    }

    public TipitakaOrgVisitorIndexHtml(TipitakaUrlFactory urlFactory, File directoryMap) throws XmlPullParserException, IOException {
        this.directory = new DirectoryStructure(urlFactory);
        if (directoryMap == null) {
            this.directory.reload();
        }
        else {
            this.directory.load(directoryMap);
        }
    }

    static public void main(String... args) throws Exception{
        TipitakaOrgVisitorIndexHtml visitor = new TipitakaOrgVisitorIndexHtml(new TipitakaUrlFactory(),
            new File("solr/tipitaka/directory.map"));
        visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    }

    public void generateAll(File basedir, Script script) throws IOException {
        Set<String> done = new TreeSet<String>();
        for (String path: this.directory.allPaths()) {
            File current = new File(path);
            while(current != null) {
                String key = current.getParent();
                if (key == null) key = "";
                if (!done.contains(key)) {
                    done.add(key);
                    File index = new File(basedir, key + "/index.html");
                    System.err.println(index);
                    index.getParentFile().mkdirs();
                    // TODO ensure utf-8
                    Writer writer = new FileWriter(index);
                    accept(writer, script, current.getPath());
                    writer.close();
                }
                if ("/".equals(current)) {
                    current = null;
                }
                else {
                    current = current.getParentFile();
                }
            }
        }

    }

    public void accept(Writer writer, Script script, String path) throws IOException {
        Builder builder = new Builder(directory, script, writer, path);
        builder.buildDir();
        builder.flush();

    }
}
