package org.tipitaka.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.xmlpull.v1.XmlPullParserException;

public class TipitakaOrgVisitorHtml extends TipitakaOrgVisitor {

    private final DirectoryStructure directory;
    //private final Script script;dd

    public TipitakaOrgVisitorHtml(TipitakaUrlFactory urlFactory) throws XmlPullParserException, IOException {
        this(urlFactory, null);
    }

    public TipitakaOrgVisitorHtml(TipitakaUrlFactory urlFactory, File directoryMap) throws XmlPullParserException, IOException {
        super(urlFactory);
        this.directory = new DirectoryStructure(urlFactory);
        if (directoryMap == null) {
            this.directory.reload();
        }
        else {
            this.directory.load(directoryMap);
        }
        //this.script = new ScriptFactory().script("romn");
    }

    static public void main(String... args) throws Exception{
        TipitakaOrgVisitorHtml visitor = new TipitakaOrgVisitorHtml(new TipitakaUrlFactory(),
            new File("solr/tipitaka/directory.map"));

        visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"),
            "/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam");
        visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    }

    public void generateAll(File basedir, Script script) throws IOException {
        for (String path: this.directory.allPaths()) {
            File index = new File(basedir, script.name + path + ".html");
            System.err.println(index);
            index.getParentFile().mkdirs();
            // TODO ensure utf-8
            Writer writer = new FileWriter(index);
            accept(writer, script, path);
            writer.close();
        }
    }

    private Builder builder;
    public void accept(Writer writer, Script script, String path) throws IOException {
        String legacy = this.directory.fileOf(path);
        URL url = urlFactory.newURL(script.tipitakaOrgName, legacy);
        this.builder = new Builder(this.directory, script, writer, path, url.toString());
        super.accept(writer, script, legacy);
    }

    protected void docStart(Writer writer, String url) throws IOException {
        builder.startHtmlBody();
        builder.appendNavigation(".html");

        writer.append("<div class=\"document-container\"><div class=\"document\">");
    }

    protected void docEnd(Writer writer) throws IOException {
        writer.append("</div>\n</div>\n");
        builder.endBodyHtml();
    }

    protected void hiStart(Writer writer, String clazz) throws IOException {
        writer.append("<span class=\"").append(clazz).append("\">");
    }
    
    protected void hiEnd(Writer writer) throws IOException {
        writer.append("</span>");
        if (isAnchor) {
            isAnchor = false;
            writer.append("</a>");
        }
    }

    static class State {
        boolean isAnchor = false;
        int count = 0;
        Builder builder;
    }
    private boolean isAnchor = false;
    protected void pStart(Writer writer, String clazz, String number) throws IOException {
        writer.append("<p class=\"").append(clazz).append("\">");
        if (number != null) {
            isAnchor = true;
            writer.append("<a name=\"para").append(number).append("\" href=\"")
                .append("/").append(this.builder.script.name)
                .append(this.builder.path).append(".html#para").append(number).append("\">");
        }
    }

    protected void pEnd(Writer writer) throws IOException {
        writer.append("</p>");
    }
    
    protected void noteStart(Writer writer) throws IOException {
        writer.append("<span class=\"note\">[");
    }

    protected void noteEnd(Writer writer) throws IOException {
        writer.append("]</span>");
    }
    
    protected void pb(Writer writer, String ed, String n) throws IOException {
        writer.append("<a name=\"").append(ed).append(n).append("\"></a>");
    }
}
