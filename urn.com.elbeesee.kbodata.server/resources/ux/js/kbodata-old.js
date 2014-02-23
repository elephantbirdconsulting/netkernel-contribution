
/* kbodata.js */

(function($) {
	
	var lib = {
        
        sortProperties: function() {
            // ordered by priority
            var props = [
                'http://www.w3.org/ns/locn#fullAddress',
                'prefLabel',
                'Label',
                'label',
                'identifier',
                'name', 'Name',
                'Type',
                'type'
            ].reverse();
            $.each(props, function() {
                $('th a[href*="' + this + '"]').parents('tr').each(function() {
                    $(this).prependTo($(this).parents('tbody'));
                });
            });
        },
        
        reformatResources: function() {
            $('[about]').each(lib.reformatResource);
        },
        
        reformatResource: function() {
            var subject = $(this).attr('about');
            var subjectContainer = $(this);
            subjectContainer.append([
                '<div class="grid" data-s="' + subject + '">',
                    '<div class="col-1"></div>',
                    '<div class="col-2"></div>',
                '</div>'
            ].join("\n"));
            $(this).find('tr').each(function() {
                var predicate = $(this).find('th a').attr('href');
                var predicateLabel = $(this).find('th a').html();
                var object, title;
                $(this).find('td a').each(function() {
                    object = '<a href="' + $(this).attr('href') + '">' + $(this).text() + '</a>';
                    title = lib.getTooltipTitle($(this).attr('resource'));
                });
                $(this).find('td span').each(function() {
                    object = $(this).html();
                    title = lib.getTooltipTitle(object);
                });
                var predicateContainer = lib.getPredicateContainer(subjectContainer, predicate, predicateLabel);
                predicateContainer.find('.objects').append([
                    '<div class="object" ' + title + '>',
                    object,
                    '</div>'
                ].join("\n"));
            });
            $(this).find('table').hide();
        },
        
        getTooltipTitle: function(value) {
            if (value.match(/^(http|[^\s]{30})/)) {
                value = value.replace(/([^\s]{50,60}[^a-z])([^\s]{20})/g, "$1&#8203;$2");
                return ' title="' + value + '"';
            }
            return '';
        },
        
        getPredicateContainer: function(subjectContainer, predicate, predicateLabel) {
            var result = subjectContainer.find('.predicate[data-p="' + predicate +'"]');
            if (!result.length) {
                var col1Height = subjectContainer.find('.col-1').height();
                var col2Height = subjectContainer.find('.col-2').height();
                var container = col1Height > col2Height ? subjectContainer.find('.col-2') : subjectContainer.find('.col-1');
                var suffix = '';
                // address: always right
                if (predicate === 'http://www.w3.org/ns/locn#fullAddress') {
                    container = subjectContainer.find('.col-2');
                    suffix = '<div class="map"></div>';
                }
                container.append([
                    '<div class="predicate" data-p="' + predicate + '">',
                    '   <a class="label" href="' + predicate + '">' + lib.deCamelCase(predicateLabel) + '</a>',
                    '   <div class="objects"></div>',
                        suffix,
                    '</div>'
                ].join("\n"));
                return lib.getPredicateContainer(subjectContainer, predicate);
            }
            return result;
        },
        
        deCamelCase: function(value) {
            return value.replace(/([a-z])([A-Z][a-z])/g, '$1 $2');
        },
        
        rearrangeResources: function() {
            $('[about] h2').each(function() {
                // move properly titled resources to the top
                if (!$(this).text().match(/^http/)) {
                    $('h1').after($(this).parents('[about]'));
                }
            });
        },
        
        injectMaps: function() {
            $('.map').each(function() {
                var address = $(this).prev('.objects').find('.object').first().text();
                var src = 'https://maps.google.be/maps?f=q&amp;source=s_q&amp;hl=en&amp;q=' + encodeURIComponent(address) + '&amp;iwloc=A&amp;output=embed';
                $(this).html('\
                    <iframe style="width:100%;height:400px" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="' + src + '"></iframe>\
                ');
            });
        },
		
		init: function() {
            lib.sortProperties();
            lib.reformatResources();
            lib.rearrangeResources();
            lib.injectMaps();
		}
	
	};
	
	$(lib.init);	
 	
})(jQuery);
