package org.tipitaka.search.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tipitaka.search.BuilderFactory;
import org.tipitaka.search.DirectoryStructure;
import org.tipitaka.search.HtmlBuilder;
import org.tipitaka.search.ResourceLocator;
import org.tipitaka.search.ScriptFactory;
import org.tipitaka.search.TipitakaPath;
import org.tipitaka.search.TipitakaUrlFactory;

public class TipitakaServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    private BuilderFactory builderFactory;
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
        ResourceLocator locator = new ResourceLocator();
        File dir = locator.localDir();
        TipitakaUrlFactory urlFactory = dir.exists() ? new TipitakaUrlFactory(dir) : new TipitakaUrlFactory();
        directory = new DirectoryStructure(urlFactory);
        try {
            directory.load(locator.getResourceAsReader("directory.map"));
        } catch (IOException e) {
            throw new ServletException("can not setup directory structure", e);
        }
        builderFactory = new BuilderFactory(factory, directory, urlFactory);
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
                builderFactory.newXmlBuilder(resp.getWriter(), path).buildTei();
                break;
        }
    }

}
