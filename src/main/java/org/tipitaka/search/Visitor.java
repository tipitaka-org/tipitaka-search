package org.tipitaka.search;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by cmeier on 2/28/16.
 */
public interface Visitor
{
  void accept(Writer writer, Script script, String path) throws IOException;
}
