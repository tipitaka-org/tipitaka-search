package org.tipitaka.search;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParserException;

public class TipitakaOrgVisitorLegacyHtml
    extends TipitakaOrgVisitor {

    public TipitakaOrgVisitorLegacyHtml(TipitakaUrlFactory urlFactory) throws XmlPullParserException {
        super(urlFactory);
    }

    static public void main(String... args) throws Exception{
        TipitakaOrgVisitorLegacyHtml visitor = new TipitakaOrgVisitorLegacyHtml(new TipitakaUrlFactory());
        visitor.accept(new OutputStreamWriter(System.out), new ScriptFactory().script("romn"), "cscd/vin01m.mul0.xml");
    }

    protected void docStart(Writer writer, String url) throws IOException {
        writer.append("<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "<title>" + "</title>\n" +
            "<link rel=\"stylesheet\" href=\"tipitaka-latn.css\">\n" +
            "</head>\n" +
            "<body>");
    }

    protected void docEnd(Writer writer) throws IOException {
        writer.append("</body>\n</html>\n");
    }

    protected void hiStart(Writer writer, String clazz) throws IOException {
        writer.append("<span class=\"").append(clazz).append("\">");
    }
    
    protected void hiEnd(Writer writer) throws IOException {
        writer.append("</span>");
    }

    protected void pStart(Writer writer, String clazz, String number) throws IOException {
        writer.append("<p class=\"").append(clazz).append("\">");
        if (number != null) {
            writer.append("<a name=\"para").append(number).append("\"></a>");
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
