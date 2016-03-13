package org.tipitaka.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.URL;

import org.xmlpull.v1.XmlPullParserException;

public class TipitakaOrgVisitorHtml extends TipitakaOrgVisitor {

    static class State {
        boolean isAnchor = false;
        int count = 0;
        Builder builder;

        String nextNumber() {
            count ++;
            return String.valueOf(count);
        }
        Writer append(CharSequence string) throws IOException {
            return builder.append(string);
        }
    }

    private final DirectoryStructure directory;

    private State state = new State();

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
        TipitakaOrgVisitorHtml visitor = new TipitakaOrgVisitorHtml(new TipitakaUrlFactory("file:../tipitaka-search/solr/tipitaka/"),
            new File("solr/tipitaka/directory.map"));

        visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"),
            "/tipitaka (mula)/vinayapitaka/parajikapali/veranjakandam");
        visitor.generateAll(new File("archive"), new ScriptFactory().script("romn"));
    }

    public void generateAll(File basedir, Script script) throws IOException {
        for (String path: this.directory.allPaths()) {
            File index = new File(basedir, script.name + path + ".html");
            System.err.println(index + " " + directory.fileOf(path));
            index.getParentFile().mkdirs();
            // TODO ensure utf-8
            Writer writer = new FileWriter(index);
            accept(writer, script, path);
            writer.close();
        }
    }

    public void accept(Writer writer, Script script, String path) throws IOException {
        String legacy = this.directory.fileOf(path);
        URL url = urlFactory.normativeURL(script, legacy);
        state = new State();
        state.builder = new Builder(this.directory, script, writer, path, url.toString());
        super.accept(writer, script, legacy);
    }

    protected void docStart(Writer writer, String url) throws IOException {
        state.builder.startHtmlBody();
        state.builder.appendNavigation(".html");

        writer.append("<div class=\"document-container\"><div class=\"document\">");
    }

    protected void docEnd(Writer writer) throws IOException {
        state.builder.appendImpressum().append("</div>\n</div>\n");
        state.builder.endBodyHtml();
    }

    protected String hiStart(Writer writer, String clazz) throws IOException {
        if (!"dot".equals(clazz)) {
            writer.append("<span class=\"").append(clazz).append("\">");
        }
        return clazz;
    }
    
    protected void hiEnd(Writer writer, final String clazz) throws IOException {
        if ("dot".equals(clazz)) {
           return;
        }
        state.append("</span>");
        if (state.isAnchor) {
            state.isAnchor = false;
            state.append("</a>");
        }
    }

    protected void pStart(Writer writer, String clazz, String number) throws IOException {
        state.append("<p class=\"").append(clazz).append("\">");
        String lineNumber = state.nextNumber();
        state.append("<a class=\"line-number\" name=\"line").append(lineNumber).append("\" href=\"")
            .append("/").append(state.builder.script.name)
            .append(state.builder.path).append(".html#line").append(lineNumber).append("\">")
            .append(lineNumber).append("</a>");
        if (number != null) {
            state.isAnchor = true;
            state.append("<a name=\"para").append(number).append("\" href=\"")
                .append("/").append(state.builder.script.name)
                .append(state.builder.path).append(".html#para").append(number).append("\">");
        }
    }

    protected void pEnd(Writer writer, final String clazz) throws IOException {
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
