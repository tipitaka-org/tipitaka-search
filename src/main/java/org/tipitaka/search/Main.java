package org.tipitaka.search;

import java.io.File;

import org.tipitaka.search.solr.TipitakaSolrIndexer;

public class Main {

    private static final int DONE = 1000;

    enum Command {
        MIRROR, INDEX, DIRECTORY_STRUCTURE, DIRECTORY_TRANSCRIBE
    }
    public static void main(String... args) throws Exception {
        if( args.length == 0){
            showHelp();
        }
        int offset = 0;
        while(offset < args.length){
            offset += run(offset, args);
        }
    }
    
    public static int run(int offset, String... args) throws Exception {
        if (args.length == offset + 1) {
            throw new RuntimeException("no <basedir> given");
        }
        File basedir = new File(args[offset + 1]);
        TipitakaUrlFactory urlFactory = new TipitakaUrlFactory(basedir);
        try {
            switch (Command.valueOf(args[offset + 0].toUpperCase().replace("-",
                    "_"))) {
                case MIRROR :
                    if (args.length == offset + 2) {
                        throw new RuntimeException(
                                "no <script> or <script,script> or ... given");
                    } else {
                        new TipitakaMirrorer(urlFactory).mirror(args[offset + 2]);
                        return 3;
                    }
                case INDEX :
                    if (args.length == offset + 2) {
                        throw new RuntimeException(
                                "no <script> or <script,script> or ... given");
                    } else {
                        new TipitakaSolrIndexer("http://localhost:8080/update", urlFactory)
                                .index(args[offset + 2]);
                        return 3;
                    }
                case DIRECTORY_STRUCTURE :
                    DirectoryStructure dir = new DirectoryStructure(urlFactory);
                    dir.reload();
                    dir.save(new File(basedir, "directory.map"));
                    return 2;
                case DIRECTORY_TRANSCRIBE:
                    if (args.length == offset + 2) {
                        throw new RuntimeException(
                                "no <script> or <script,script> or ... given");
                    } else {
                        dir = new DirectoryStructure(urlFactory);
                        dir.load(new File(basedir, "directory.map"));
                        Script script = dir.transcribe(args[offset + 2]);
                        script.save(new File(basedir, script.name + ".script"));
                        return 3;
                    }
                default :
                    showHelp();
                    return DONE;
            }
        } catch (IllegalArgumentException e) {
            showHelp();
            return DONE;
        }
    }

    private static void showHelp() {
        System.err.println("usage:");
        for (Command com : Command.values()) {
            System.err.println("\tjava " + Main.class.getName() + " "
                    + com.toString().toLowerCase() + " <basedir> <script>");
        }
    }
}
