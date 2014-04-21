
/* kbodata-documentation.js */

(function($) {
	
	var lib = {
        
        optimiseWhitespace: function() {
            $('pre code').each(function() {
                $(this).html($(this).html()
                    .replace(/\s+$/, '')
                    .replace(/^   /g, '')
                    .replace(/([\r\n]+)   /g, '$1')
                );
            });
        },
        
        splitRegex: function() {
            $('p:contains("^http://data.kbodata.be/(")').each(function() {
               $(this).html($(this).html().replace(/\|/g, '|&#8203;'));
            });
        },
        
		init: function() {
            lib.optimiseWhitespace();
            lib.splitRegex();
		}
	
	};
	
	$(lib.init);	
 	
})(jQuery);
