/**
 * 
 */
package org.tipitaka.search;

import java.io.IOException;
import java.io.Writer;


public class HtmlBuilderFactory {
    private final ScriptFactory scriptFactory;
    private final DirectoryStructure structure;
    private final TipitakaUrlFactory urlFactory;
    
    public HtmlBuilderFactory(ScriptFactory scriptFactory, DirectoryStructure structure, TipitakaUrlFactory urlFactory) {
        this.scriptFactory = scriptFactory;
        this.structure = structure;
        this.urlFactory = urlFactory;
    }
    
    public HtmlBuilder newHtmlBuilder(Writer writer, TipitakaPath path) throws IOException{
        return new HtmlBuilder(writer, path.prefix, scriptFactory.script(path.script), 
                path.path, structure, scriptFactory, urlFactory);
    }
}