/**
 * 
 */
package org.tipitaka.search;

import java.io.IOException;
import java.io.Writer;


public class HtmlBuilderFactory {
    private final ScriptFactory scriptFactory;
    private final DirectoryStructure structure;
    public HtmlBuilderFactory(ScriptFactory scriptFactory, DirectoryStructure structure) {
        this.scriptFactory = scriptFactory;
        this.structure = structure;
    }
    
    public HtmlBuilder newHtmlBuilder(Writer writer, TipitakaPath path) throws IOException{
        return new HtmlBuilder(writer, path.prefix, scriptFactory.script(path.script), path.path, structure, scriptFactory);
    }
}