/**
 * 
 */
package org.tipitaka.search;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

public class HtmlBuilder {

    final Writer writer;
    private final Script script;
    private final DirectoryStructure structure;
    private final TipitakaOrgVisitorHtml tipitaka;
    private final String path;
    private final String prefix;
    private final ScriptFactory factory;
    
    public HtmlBuilder(Writer writer, String prefix, Script script, String path, 
            DirectoryStructure structure, ScriptFactory factory) {
        this.writer = writer;
        this.path = path;
        this.prefix = prefix;
        this.script = script;
        this.structure = structure;
        this.factory = factory;
        try {
            this.tipitaka = new TipitakaOrgVisitorHtml();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("error creating html visitor", e);
        }
    }
    
    public void buildAll() throws IOException {
        buildHeader();
        buildScriptNavigation();
        buildBreadCrumbs();
        buildViewLink();
        buildSubmenu();
        buildPage();
        buildFooter();   
    }

    public void buildMinimal() throws IOException {
        buildHeader(null);
        buildPage();
        buildFooter();   
    }
    
    public void buildPage() throws IOException {
        if(path == null || "/".equals(path)){
            return;
        }
        String file = structure.fileOf(path.replaceFirst("\\.[a-z]+$", ""));
        if(file != null){
            writer.append("    <div class='page'>\n");
            try {
                tipitaka.accept(writer, script.tipitakaOrgName, file);
            } catch (XmlPullParserException e) {
                throw new RuntimeException("parse error", e);
            }
            writer.append("    </div>\n");
        }
    }

    void buildHeader() throws IOException {
        buildHeader("/main.css");
    }
    
    void buildHeader(String css) throws IOException {
        writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
        		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "  <head>\n" +
                "    <title>");
        Collection<String> crumbs = structure.breadCrumbs(script, path).values();
        for(String crumb : crumbs){
            writer.append(crumb).append("/");
        }
        writer.append("</title>\n");
        writer.append("    <meta name='tipitaka-script' content='").append(script.name).append("' />\n");
        writer.append("    <meta name='tipitaka-path' content='").append(path).append("' />\n");
        writer.append("    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n");
        if(css != null){
            writer.append("    <link rel=\"stylesheet\" href=\"").append(css).append("\" />\n");
        }
        writer.append("    <link rel=\"stylesheet\" href=\"http://tipitaka.org/")
            .append(script.tipitakaOrgName).append("/cscd/tipitaka-").append("romn".equals(script.tipitakaOrgName)? "latn" : script.tipitakaOrgName).append(".css\" />\n");
        writer.append("    <link rel=\"icon\" type=\"image/ico\" href=\"http://tipitaka.org/favicon.ico\" />\n");
        writer.append("  </head>\n" +
                "  <body>\n");
        writer.flush();
    }
    
    void buildScriptNavigation() throws IOException {
        writer.append("    <div class='scripts'>\n");
        for(String script : factory.allScriptNames()){
            writer.append("      <a href='").append(this.prefix).append("/").append(script).append(path)
            .append("'>")
            .append(script)
            .append("</a>\n");
        }
        writer.append("    </div>\n");         
    }
    
    void buildBreadCrumbs() throws IOException {
        writer.append("    <div class='breadcrumbs'>\n" +
                "      <a href='").append(this.prefix).append("/").append(script.name).append("\'>ALL</a>\n");
        Map<String,String> crumbs = structure.breadCrumbs(script, path);
        for(Map.Entry<String, String> crumb : crumbs.entrySet()){
            writer.append("      &gt;\n" +
                "      <a href='").append(this.prefix).append("/").append(script.name)
                    .append(crumb.getKey())
                    .append("'>")
                    .append(crumb.getValue())
                    .append("</a>\n");
        }
        writer.append("    </div>\n");   
    }

    void buildViewLink() throws IOException {
        String file = structure.fileOf(path);
        if( file != null){
            writer.append("    <div class='views'>\n" +
                "      <a href='").append(this.prefix).append("/").append(script.name).append(path).append(".html'>full page</a>\n" +
                "      |\n" +
                "      <a href='").append(this.prefix).append("/").append(script.name).append(path).append(".tei.xml'>TEI format</a>\n" +
                "      |\n" +
                "      <a href='http://tipitaka.org/").append(script.tipitakaOrgName).append("/").append(file).append("'>page on tipitaka.org</a>\n" +
                "    </div>\n");
        }
    }

    void buildSubmenu() throws IOException {
        writer.append("    <div class='sidemenu'>\n");
        Map<String, String> list = structure.list(script, path);
        for (Map.Entry<String, String> item : list.entrySet()) {
            writer.append("      <a href='")
                .append(this.prefix)
                .append("/")
                .append(script.name)
                .append(item.getKey())
                .append("'>")
                .append(item.getValue())
                .append("</a>\n");
        }
        writer.append("    </div>\n");
    }

    void buildFooter() throws IOException {
        writer.append("  </body>\n" +
                "</html>\n");
        writer.flush();
    }
    
}