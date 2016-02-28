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
class Builder
{

    final DirectoryStructure directory;

    final Script script;

    final Writer writer;

    final Map<String, String> breadCrumbs;

    final String path;

    final boolean isSubdir;

    final String url;
    Builder(DirectoryStructure directory, Script script, Writer writer, String path) throws IOException {
        this(directory, script, writer, path, null);
    }

    Builder(DirectoryStructure directory, Script script, Writer writer, String path, String url) throws IOException {
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
            this.breadCrumbs = directory.breadCrumbs(script, this.path);//.replaceFirst("[^/]+$", ""));
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
        endBodyHtml();
        return this;
    }

    public Builder buildLeafdir() throws IOException {
        startHtmlBody();
        appendNavigation(".html");
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

    public Builder startHtmlBody() throws IOException {
        writer.append("<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<link rel=\"stylesheet\" href=\"/nav.css\">\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        if (this.url != null) {
            writer.append("<meta name=\"normative-source\" content=\"").append(url).append("\" />\n");
        }
        writer.append("<meta name=\"archive-path\" content=\"").append(this.path).append(".html\" />\n");

        appendTitle();

        writer.append("</head>\n<body>\n");

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
        if ("/".equals(path)) {
            writer.append("ROOT");
        }
        else {
            writer.append("<a href=\"").append("/index.html\">ROOT</a>");
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
                        writer.append("<a href=\"").append(part.getKey()).append("/index.html\">")
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
                writer.append("<a href=\"").append(entry.getKey()).append(postfix).append("\">")
                    .append(entry.getValue()).append("</a>");

            }
            writer.append("</div>\n");
        }

        writer.append("</span>\n");


        writer.append("\n</div>\n</div>\n");

        return this;
    }

    public void flush() throws IOException {
        writer.flush();
    }
}
