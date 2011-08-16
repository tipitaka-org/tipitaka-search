package org.tipitaka.search.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.search.DirectoryStructure;
import org.tipitaka.search.HtmlBuilder;
import org.tipitaka.search.HtmlBuilderFactory;
import org.tipitaka.search.ResourceLocator;
import org.tipitaka.search.Script;
import org.tipitaka.search.ScriptFactory;
import org.tipitaka.search.TipitakaPath;
import org.tipitaka.search.TipitakaUrlFactory;

public class TipitakaServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    private HtmlBuilderFactory builderFactory;
    private DirectoryStructure directory;
    private ScriptFactory factory;
    private String prefix;
    
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        this.prefix = config.getInitParameter("prefix");
        if(this.prefix == null){
            this.prefix = "";
        }
        
        log("using tipitaka browsing path prefix: " + prefix);
        factory = new ScriptFactory();
        directory = new DirectoryStructure();
        ResourceLocator locator = new ResourceLocator();
        try {
            directory.load(locator.getResourceAsReader("romn.map"));
        } catch (IOException e) {
            throw new ServletException("can not setup directory structure", e);
        }
        builderFactory = new HtmlBuilderFactory(factory, directory);
    }

    @Override
    protected void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws ServletException,
            IOException {
        
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("Vary", "Accept");

        TipitakaPath path = new TipitakaPath(this.prefix, req.getPathInfo());
        
        HtmlBuilder builder = builderFactory.newHtmlBuilder(resp.getWriter(), path);
        
        switch(path.type()){
            case MAIN:
            case NONE:
                resp.setContentType("text/html");
                builder.buildAll();
                break;
            case HTML:
                resp.setContentType("text/html");
                builder.buildMinimal();
                break;
            case TEI:
                resp.setContentType("application/xml");
                new XmlBuilder(factory, directory, resp.getWriter(), path).buildTei();
                break;
        }
    }
    
    static class XmlBuilder {
        
        private Writer writer;
        private TipitakaPath path;
        private DirectoryStructure directory;
        private final Script script;
        
        XmlBuilder(ScriptFactory factory, DirectoryStructure directory, Writer writer, TipitakaPath path) throws IOException{
            this.writer = writer;
            this.path = path;
            this.directory = directory;
            this.script = factory.script(path.script);
        }
        
        void buildTei() throws IOException{
            URL url = TipitakaUrlFactory.newURL(script.tipitakaOrgName, directory.fileOf(path.path.replace(".tei.xml", "")));
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

}
