/**
 * 
 */
package org.tipitaka.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

public class DirectoryStructure {
   
    private static final Set<String> EMPTY = Collections.emptySet();
    
    public static void main(String... args) throws Exception {
        long start = System.currentTimeMillis();
        DirectoryStructure dir = new DirectoryStructure(new TipitakaUrlFactory());
//        dir.reload();
//        dir.save(new FileWriter("/home/kristian/romn.map"));
        dir.load(new FileReader("/home/kristian/romn.map"));
        
        for(String s: dir.subdirs("/anya/sihala-gantha-sangaho/samantakutavannana/samantakutavannana")){
            System.err.println(s);
        }
        System.err.println(dir.fileOf("/anya/sihala-gantha-sangaho/samantakutavannana/samantakutavannana"));
        for(String s: dir.subdirs("/anya/sihala-gantha-sangaho/samantakutavannana/")){
            System.err.println(s);
        }
        
        Script roman = new Script("roman", "romn");
        roman = dir.transcribe("romn");
        roman.save(new FileWriter("/home/kristian/roman.script"));
        roman.load(new FileReader("/home/kristian/roman.script"));
        
        Script deva = new Script("devanagari", "deva");
        deva = dir.transcribe("deva");
        deva.save(new FileWriter("/home/kristian/devanagari.script"));
        deva.load(new FileReader("/home/kristian/devanagari.script"));      
        
        Script script = deva;
        
        System.err.println("---------------------------------");
        for(String word : "anya/sihala-gantha-sangaho/samantakutavannana/samantakutavannana".split("/")){
            System.err.println(script.get(word));
        }
        System.err.println("---------------------------------");
        Map<String, String> crumbs = dir.breadCrumbs(script, "/anya/sihala-gantha-sangaho/samantakutavannana/samantakutavannana");
        for(Map.Entry<String, String> c: crumbs.entrySet()){
            System.err.println(c.getKey() + " -> " + c.getValue());
        }
        System.err.println("---------------------------------");        
        Map<String, String> dirs = dir.listDirs(script, "/anya/sihala-gantha-sangaho");
        for(Map.Entry<String, String> c: dirs.entrySet()){
            System.err.println(c.getKey() + " -> " + c.getValue());
        }
        System.err.println("---------------------------------");
        Map<String, String> files = dir.listFiles(script, "/anya/sihala-gantha-sangaho/dhatuvamsa/dhatuparamparakatha");
        for(Map.Entry<String, String> c: files.entrySet()){
            System.err.println(c.getKey() + " -> " + c.getValue());
        }
        System.err.println("---------------------------------");       
        long start2 = System.currentTimeMillis();
        BuilderFactory factory = new BuilderFactory(new ScriptFactory(), dir, new TipitakaUrlFactory());
        
        String path = "/" + script.name + "/anya/sihala-gantha-sangaho/dhatuvamsa/dhatuparamparakatha";
        HtmlBuilder builder = factory.newHtmlBuilder(new FileWriter("/home/kristian/deva-test.html"), new TipitakaPath("", path));
        builder.buildHeader();
        builder.buildScriptNavigation();
        builder.buildBreadCrumbs();
        builder.buildViewLink();
        builder.buildSubmenu();
        builder.buildPage();
        builder.buildFooter();
        System.err.println("---------------------------------");        
        System.err.println(System.currentTimeMillis() - start);
        System.err.println(System.currentTimeMillis() - start2);
        factory.newHtmlBuilder(new FileWriter("/home/kristian/deva-test-mini.html"), new TipitakaPath("", path)).buildMinimal();
    }

    public Collection<String> allPaths() {
        return map.keySet();
    }

    public Map<String, String> list(Script words, String path) {
        if(map.containsKey(path)){
            return listFiles(words, path);
        }
        else{
            return listDirs(words, path);
        }
    }
    
    public Map<String, String> listDirs(Script words, String path) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if(fileOf(path) == null){
            if(path == null){
                path = "/";
            }
            else if(!path.endsWith("/")){
                path = path + "/";
            }
            for(String dir: subdirs(path)){
                result.put(path + dir, words.get(dir));
            }
        }
        return result;
    }

    public Map<String, String> listFiles(Script words, String path) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if(fileOf(path) != null){
            path = path.substring(0, path.lastIndexOf("/") + 1);
            for(String dir: subdirs(path)){
                result.put(path + dir, words.get(dir));
            }
        }
        return result;
    }

    public Map<String, String> breadCrumbs(Script trans, String path) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if(path != null && !"/".equals(path)) {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            int from = 1;
            int next = path.indexOf("/", from);
            while (next > -1) {
                result.put(path.substring(0, next), trans.get(path.substring(
                    from, next)));
                from = next + 1;
                next = path.indexOf("/", from);
            }
            if (path.length() == 0) {
                result.put(path, "ROOT");
            }
            else {
                result.put(path, trans.get(path.substring(from)));
            }
        }
        return result;
    }

    private final Node root = new Node("");
    
    private final Map<String, String> map = new LinkedHashMap<String, String>();
    private final Map<String, String> rmap = new HashMap<String, String>();

    private final TipitakaUrlFactory urlFactory;
    
    public DirectoryStructure(TipitakaUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    public void reload() throws XmlPullParserException, IOException{
        TipitakaOrgTocVisitor visitor = new TipitakaOrgTocVisitor(this.urlFactory);
        visitor.accept("romn");
        
        List<String> parts = new ArrayList<String>(6);
        for(Map.Entry<String, String[]> entry: visitor.map().entrySet()){
            parts.clear();
            for(String part: entry.getValue()){
                if(part != null){
                    parts.add(RomanScriptHelper.removeDiacritcals(part//.replaceFirst("[(][0-9]+[)]", "").replaceFirst("[0-9]+\\.\\ ", "")
								  //.replaceFirst("(bhikkhunīvibhaṅgo)", "- bhikkhunīvibhaṅgo")//.replaceFirst("\\ [(].*[)]", "")
								  //.replaceAll("[()]","").replace("’", " "))
								  .replaceFirst("\\.$", "")
								  .toLowerCase().trim()));//.replaceAll(" ", "_"));
                }
            }
            String path = root.addLeaf(entry.getKey(), parts);
	    while (map.containsKey(path)) path += "_";
            map.put(path, entry.getKey());
            rmap.put(entry.getKey(), path);
        }

    }

    Script transcribe(String script) throws XmlPullParserException, IOException{
        TipitakaOrgTocVisitor visitor = new TipitakaOrgTocVisitor(this.urlFactory);
        visitor.accept(script);
        ScriptFactory factory = new ScriptFactory();
        Script words = factory.newScript(script);
        
        List<String> parts = new ArrayList<String>(6);
        for(Map.Entry<String, String[]> entry: visitor.map().entrySet()){
            parts.clear();
            for(String part: entry.getValue()){
                if(part != null){
                    parts.add(part);
                }
            }
            if(rmap.containsKey(entry.getKey())){
                String[] paths = rmap.get(entry.getKey()).substring(1).split("/");
                for(int i = 0; i < paths.length; i++){
                    System.err.println(paths[i] + " <> " + parts.get(i));
                    words.put(paths[i], parts.get(i));
                }
            }
            else {
                //System.err.println("---" + entry.getKey());
            }
        }
        return words;
    }
   
    void save(File file) throws IOException {
        save(new FileWriter(file));
    }
    
    void save(Writer writer) throws IOException{
        for(Map.Entry<String, String> entry: map.entrySet()){
            writer.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        writer.close();
    }
    public void load(File file) throws IOException{
        load(new FileReader(file));
    }
    
    public void load(Reader reader) throws IOException{
        map.clear();
        rmap.clear();
        BufferedReader in = null;
        try{
            in = new BufferedReader(reader);
            String line = in.readLine();
            while(line != null){
                String[] pp = line.split("=");
                map.put(pp[0], pp[1]);
                rmap.put(pp[1], pp[0]);
                root.addLeaf(pp[1], pp[0].substring(1).split("/"));
                line = in.readLine();
            }
        }
        finally {
            if( in != null) {
                in.close();
            }
        }
    }

    public String pathOfOrignalFile(String path) {
        return rmap.get(path);
    }
    public String fileOf(String path){
        return map.get(path);
    }
    
    Set<String> subdirs(String path){
        if(path == null || "/".equals(path)){
            return root.children.keySet();
        }
        if(map.containsKey(path)){
            return EMPTY;
        }
        path = path.replaceAll("^/|/$", "");
        return root.getNode(path.split("/")).children.keySet();
    }
    
    static class Node {
        
        Map<String, Node> children;
        
        String name;
        
        Node(String name, boolean leaf){
            this.name = name;
            this.children = leaf ? null : new LinkedHashMap<String, Node>();
        }
        
        public void addLeaf(String key, String[] parts) {
            addLeaf(key, Arrays.asList(parts));
        }

        public String addLeaf(String key, List<String> parts) {
            StringBuilder result = new StringBuilder();
            Node n = this;
            for(String part: parts){
                result.append("/").append(part);
                Node node = n.get(part);
                if( node == null){
                    node = n.addNode(part);
                }
                n = node;
            }
            n.children = null;
            n.name = key;
            return result.toString();
        }

        Node(String name){
            this(name, false);
        }
        
        Node addNode(String name){
            Node n = new Node(name);
            children.put(name, n);
            return n;
        }
        
        Node addLeaf(String leaf, String value){
            Node n = new Node(value);
            children.put(leaf, n);
            return n;
        }
        
        Node get(String name){
            return children.get(name);
        }
        
        Node getNode(String... parts){
            Node n = this;
            for(String part: parts){
                n = n.get(part);
            }
            return n;
        }
    }
}
