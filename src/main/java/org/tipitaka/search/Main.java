package org.tipitaka.search;

import java.io.File;

import org.tipitaka.search.solr.TipitakaSolrIndexer;

public class Main {

    enum Command {
        MIRROR, INDEX
    }
    public static void main(String... args) throws Exception {
        if( args.length == 0){
            showHelp();
        }
        if (args.length == 1) {
            throw new RuntimeException("no <basedir> given");
        }
        try{
        switch(Command.valueOf(args[0].toUpperCase())){
        case MIRROR:
            if (args.length == 2) {
                // TODO all
            } else {
                new TipitakaMirrorer(args[1]).mirror(args[2]);
            }
            break;
        case INDEX:
            if (args.length == 2) {
                // TODO all
            } else {
                new TipitakaSolrIndexer("http://localhost:8080/update", 
                        new TipitakaUrlFactory(new File(args[1]))).index(args[2]);
            }
            break;
        default:
            showHelp();
        }
        }
        catch(IllegalArgumentException e){
            showHelp();
        }
    }
    private static void showHelp() {
        System.err.println("usage:");
        for(Command com: Command.values()){
            System.err.println("\tjava " + Main.class.getName() + " " + 
                    com.toString().toLowerCase() + " <basedir> <script>");
        }
    }
}
