package org.tipitaka.search.solr;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.ExtendedDismaxQParserPlugin;
import org.apache.solr.search.QParser;
import org.tipitaka.search.RomanScriptHelper;


public class RomanExtendedDismaxQParserPlugin extends ExtendedDismaxQParserPlugin {
 
    public static final String NAME = "roman";

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
        return super.createParser(qstr == null? null: RomanScriptHelper.removeDiacritcals(qstr).toLowerCase(), localParams, params, req);
    }
}
