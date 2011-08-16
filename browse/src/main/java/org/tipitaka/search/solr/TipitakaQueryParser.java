package org.tipitaka.search.solr;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.tipitaka.search.RomanScriptHelper;


public class TipitakaQueryParser extends SolrQueryParser{

    public TipitakaQueryParser(IndexSchema schema, String defaultField) {
        super(schema, defaultField);
    }

    public TipitakaQueryParser(QParser parser, String defaultField,
            Analyzer analyzer) {
        super(parser, defaultField, analyzer);
    }

    public TipitakaQueryParser(QParser parser, String defaultField) {
        super(parser, defaultField);
    }

    @Override
    public Query parse(String query)
            throws ParseException {
        return super.parse(RomanScriptHelper.removeDiacritcals(query).toLowerCase());
    }
    
}
