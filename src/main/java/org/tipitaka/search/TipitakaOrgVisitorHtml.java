package org.tipitaka.search;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParserException;

public class TipitakaOrgVisitorHtml extends TipitakaOrgVisitor {

    public TipitakaOrgVisitorHtml(TipitakaUrlFactory urlFactory) throws XmlPullParserException {
        super(urlFactory);
    }

    static public void main(String... args) throws Exception{
        TipitakaOrgVisitorHtml visitor = new TipitakaOrgVisitorHtml(new TipitakaUrlFactory());
        visitor.accept(new OutputStreamWriter(System.out), "deva", "cscd/vin01m.mul0.xml");
    }

    protected void hiStart(Writer writer, String clazz) throws IOException {
        writer.append("<span class=\"").append(clazz).append("\">");
    }
    
    protected void hiEnd(Writer writer) throws IOException {
        writer.append("</span>");
    }

    protected void pStart(Writer writer, String clazz) throws IOException {
        writer.append("<p class=\"").append(clazz).append("\">");
    }

    protected void pEnd(Writer writer) throws IOException {
        writer.append("</p>\n");
    }
    
    protected void noteStart(Writer writer) throws IOException {
        writer.append("<span class=\"note\"> [ ");
    }

    protected void noteEnd(Writer writer) throws IOException {
        writer.append(" ] </span>");
    }
    
    protected void pb(Writer writer, String ed, String n) throws IOException {
        writer.append("<a name=\"").append(ed).append(n).append("\" />");
    }
}
