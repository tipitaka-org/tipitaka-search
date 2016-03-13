package org.tipitaka.search;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cmeier on 2/28/16.
 */
public class Builder
{

    final DirectoryStructure directory;

    public final Script script;

    final Writer writer;

    final Map<String, String> breadCrumbs;

    public final String path;

    final boolean isSubdir;

    final String url;
    Builder(DirectoryStructure directory, Script script, Writer writer, String path) throws IOException {
        this(directory, script, writer, path, null);
    }

    public Builder(DirectoryStructure directory, Script script, Writer writer, String path, String url) throws IOException {
        this.url = url;
        if (url == null) {
            this.isSubdir = directory.fileOf(path) == null;
            if (isSubdir) {
                this.path = path.replaceFirst("/[^/]+$", "");
                ;
            }
            else {
                this.path = path.replaceFirst("[^/]+$", "");
            }
            this.breadCrumbs = directory.breadCrumbs(script, this.path);
        }
        else {
            this.isSubdir = false;
            this.path = path;
            this.breadCrumbs = directory.breadCrumbs(script, this.path);
        }
        this.directory = directory;
        this.script = script;
        this.writer = writer;
    }

    public Builder buildDir() throws IOException {
        if (isSubdir) {
            return buildSubdir();
        }
        return buildLeafdir();
    }

    public Builder buildSubdir() throws IOException {
        startHtmlBody();
        appendNavigation("/index.html");
        appendImpressum();
        endBodyHtml();
        return this;
    }

    public Builder buildLeafdir() throws IOException {
        startHtmlBody();
        appendNavigation(".html");
        appendImpressum();
        endBodyHtml();
        return this;
    }

    public Builder appendTitle() throws IOException {
        writer.append("<title>");
        boolean first = true;
        List<String> parts = new LinkedList<String>(breadCrumbs.values());
        Collections.reverse(parts);
        for (String part : parts) {
            if (part != null) {
                if (first) {
                    first = false;
                }
                else {
                    writer.append(" - ");
                }
                writer.append(part);
            }
        }
        writer.append("</title>");
        return this;
    }

    public Builder startXmlBody() throws IOException {
        writer.append("<?xml version=\"1.0\"?>\n").append("<document>\n<header>\n");

        if (this.url != null) {
            writer.append("<normativeSource>").append(url).append("</normativeSource>\n");
        }
        writer.append("<archivePath>").append("/").append(script.name).append(this.path).append(".xml</archivePath>\n");

        appendTitle();

        writer.append("\n</header>\n");

        return this;
    }

    public Builder endXmlBody() throws IOException {
        writer.append("\n</document>\n");

        return this;
    }

    public Builder startHtmlBody() throws IOException {
        writer.append("<!DOCTYPE HTML>\n" +
            "<html>\n" +
            "<head>\n" +
            "<link rel=\"stylesheet\" href=\"/").append(script.name).append("/style.css\">\n" +
            "<link rel=\"stylesheet\" href=\"file:///Users/cmeier/projects/active/tipitaka/tipitaka-search/archive/").append(script.name).append("/style.css\">\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        if (this.url != null) {
            writer.append("<meta name=\"normative-source\" content=\"").append(url).append("\" />\n");
        }
        writer.append("<meta name=\"archive-path\" content=\"").append("/").append(script.name).append(this.path).append(".html\" />\n");

        appendTitle();

        writer.append("</head>\n<body>\n");

        return this;
    }

    public Builder appendImpressum() throws IOException {

        writer.append("<div class=\"impressum\"><a href=\"/impressum.html\">impressum</a></div>\n");
        return this;
    }

    public Builder endBodyHtml() throws IOException {
        writer.append("</body>\n</html>\n");
        return this;
    }

    public Builder appendNavigation(String postfix) throws IOException {
        writer.append("<div class=\"navigation-container\">\n" +
            "<div class=\"navigation\">\n");
        writer.append("<span>");
        //if ("/".equals(path)) {
        //    writer.append("ROOT");
        //}
        //else {
            writer.append("<a href=\"").append("/index.html\">ROOT</a>");
        //}
        writer.append("</span>\n<span> - </span>\n<span>");
        if ("/".equals(path)) {
            writer.append(script.name);
        }
        else {
            writer.append("<a href=\"").append("/").append(script.name).append("/index.html\">").append(script.name)
                .append("</a>");
        }
        writer.append("</span>");
        List<Map.Entry<String, String>> crumbs = new LinkedList<Map.Entry<String, String>>(breadCrumbs.entrySet());
        for (Map.Entry<String, String> part : crumbs) {
            if (part.getValue() != null) {
                boolean hilight = path.length() == part.getKey().length() || path.length() == part.getKey().length() +1;
                if ((!hilight && url != null) || url == null) {
                    writer.append("<span> - </span>\n");
                    writer.append("<span>");
                    if (hilight) {
                        writer.append(part.getValue());
                    }
                    else {
                        writer.append("<a href=\"").append("/").append(script.name).append(part.getKey()).append(
                            "/index.html\">")
                            .append(part.getValue()).append("</a>");
                    }
                    writer.append("</span>");
                }
            }
        }
        writer.append("<span> - </span>\n");
        writer.append("<span class=\"context-toc\">\n");

        for (Map.Entry<String, String> entry : this.directory.list(this.script, this.path).entrySet()) {

            writer.append("<div>");
            if (path.equals(entry.getKey())) {
                writer.append(entry.getValue());
            }
            else {
                writer.append("<a href=\"").append("/").append(script.name).append(entry.getKey()).append(postfix).append("\">")
                    .append(entry.getValue()).append("</a>");

            }
            writer.append("</div>\n");
        }

        writer.append("</span>\n");

        writer.append("<div class=\"extras\"><a target=\"_page\" href=\"http://blog.tipitaka.de\">blog</a>" +
            "<a target=\"_page\" href=\"https://github.com/tipitaka-org/tipitaka-archive/tree/master/archive/data/")
            .append(script.name).append(this.path).append(".xml")
            .append("\">export as xml (experimental)<a href=\"\">original TEI format (pending)</a></div>\n");

        writer.append("\n</div>\n</div>\n");

        return this;
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public Writer append(CharSequence string) throws IOException {
        return writer.append(string);
    }
}
