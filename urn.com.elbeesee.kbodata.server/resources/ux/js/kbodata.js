
/* kbodata.js */

(function($) {
	
	var lib = {
        
        initLangNav: function() {
            $('#lang-nav a').on('click', function(e) {
                e.preventDefault();
                lib.setLang($(this).attr('data-lang'));
            });
            lib.setLang($.cookie('kbodata.lang') || 'nl');
        },
        
        setLang: function(lang) {
            $('body').attr('data-lang', lang);
            $.cookie('kbodata.lang', lang, {path: '/'});
            $('.predicate .objects').each(function() {
                // flag last visible element
                $(this).parents('.predicate').removeClass('empty');
                $(this).find(' > *').removeClass('last');
                var lastEl = $(this).find(' > *').filter(function() {
                    if (!$(this).attr('xml:lang')) {
                        return true;
                    }
                    if ($(this).attr('xml:lang') === lang) {
                        return true;
                    }
                    return false;
                }).last();
                lastEl.addClass('last');
                // hide predicates that don't have objects for the selected language                
                if (!lastEl.length) {
                    $(this).parents('.predicate').addClass('empty');
                }
            });
            // hide heading of 
            $('.properties, .links.outbound, .links.inbound').each(function() {
                var container = $(this);
                container.prev('h2').show();
                if (!$(this).find('.predicate .objects .last').length) {
                    container.prev('h2').hide();
                } 
            });
        },
        
        injectMap: function() {
            var thoroughfare = $('.predicate .label[href="http://www.w3.org/ns/locn#thoroughfare"]').next('.objects').find('p').first().text();
            var locatorDesignator = $('.predicate .label[href="http://www.w3.org/ns/locn#locatorDesignator"]').next('.objects').find('p').first().text();
            var postCode = $('.predicate .label[href="http://www.w3.org/ns/locn#postCode"]').next('.objects').find('p').first().text();
            var postName = $('.predicate .label[href="http://www.w3.org/ns/locn#postName"]').next('.objects').find('p').first().text();
            if (thoroughfare && locatorDesignator && postCode && postName) {
                var address = thoroughfare + ' ' + locatorDesignator + ' ' + postCode + ' ' + postName;
                var src = '//maps.google.be/maps?f=q&amp;source=s_q&amp;hl=en&amp;q=' + encodeURIComponent(address) + '&amp;iwloc=A&amp;output=embed';
                var html = '<div class="map"><iframe class="map" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="' + src + '"></iframe></div>';
                $('.properties').first().before(html);
            }
        },
        
        injectTitles: function() {
            $('.predicate .objects > *').each(function() {
                var text = $(this).text();
                if (text.match(/^(http|[^\s]{30})/)) {
                    text = text.replace(/([^\s]{50,60}[^a-z])([^\s]{20})/g, "$1&#8203;$2");
                    $(this).attr('title', text);
                }
            });
        },
		
        deCamelCaseLabels: function() {
            $('.predicate .label').each(function() {
               $(this).html(lib.deCamelCase($(this).html()));
            });
        },
        
        deCamelCase: function(value) {
            return value.replace(/([a-z])([A-Z])/g, '$1 $2');
        },
        
		init: function() {
            lib.initLangNav();
            lib.injectTitles();
            lib.deCamelCaseLabels();
            lib.injectMap();
		}
	
	};
	
	$(lib.init);	
 	
})(jQuery);
