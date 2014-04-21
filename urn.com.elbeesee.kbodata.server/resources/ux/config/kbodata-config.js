
/* kbodata-config.js */

(function($) {
	
	window['kbodata-config'] = {
        // search
        searchExamples: 'config/search-examples.txt',                   // path or url
        searchImplementation: 'regex',                                  // "regex" or "larq"
        // query
        sparqlExamples: 'config/sparql-examples.txt',                   // path or url
        sparqlEndpoint: 'http://data.kbodata.be/sparql',                // path or url
        // lookup
        lookupExamples: 'config/lookup-examples.txt',                   // path or url
        // reconciliation
        reconciliationExamples: 'config/reconciliation-examples.txt',   // path or url
        reconciliationEndpoint: 'http://data.kbodata.be/reconcile'      // path or url
	};
	
})(jQuery);
