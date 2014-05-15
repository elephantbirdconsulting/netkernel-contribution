/* kbodata-explorer.js */

(function($) {
  
  var lib = {
        
        initAccordion: function() {
            $('#explorer .api h4').on('click', lib.toggleApi);
        },
        
        toggleApi: function() {
            var api = $(this).parents('.api');
            var wasActive = api.is('.active');
            // collapse prev api form
            $('#explorer .api.active .form-area').animate({'height': 0}, 250, function() {
                $(this).parents('.api').removeClass('active');
                $(this).css('height', 'auto');
            });
            // expand api form, if newly selected
            if (!wasActive) {
                var h = api.find('.form-area form').height();
                api.find('.form-area').height(0);
                api.addClass('active');
                api.find('.form-area').animate({'height': h}, function() {
                    $(this).css('height', 'auto');
                });
            }
        },
        
        initExamples: function() {
            lib.injectSearchExamples();
            lib.injectFragmentExamples();
            lib.injectSparqlExamples();
            lib.injectLookupExamples();
            lib.injectReconciliationExamples();
            $('#explorer .api select.samples').on('change', function() {
                $(this).prev().val($(this).val().replace(/\&quot\;/, '-'));
                $(this).val('');
            });
        },
        
        injectSearchExamples: function() {
            var src = window['kbodata-config']['searchExamples'];
            var container = $('#explorer .api[data-api="search"] select.samples');
            $.get(src, function(data) {
                var lines = data.split(/[\r\n]+/);
                $.each(lines, function(index, value) {
                    if (!value.match(/^#/) && !value.match(/^\s*$/)) {
                        container.append('<option value="' + value + '">' + value + '</option>');
                    }
                });
            });
        },      
        injectSparqlExamples: function() {
            var src = window['kbodata-config']['sparqlExamples'];
            var container = $('#explorer .api[data-api="sparql"] select.samples');
            $.get(src, function(data) {
                var lines = data.replace(/\r\n/, "\n").replace(/\r/, "\n").split("\n");
                lines.push("##"); // make sure the buffer gets flushed at the end
                var inQuery = false;
                var title = '';
                var query = '';
                $.each(lines, function(index, value) {
                    if (value.match(/^##/)) {
                        // flush buffer
                        if (inQuery && title && query) {
                            container.append('<option value="' + query.replace(/\"/g, '&quot;') + '">' + title + '</option>');
                        }
                        // reset buffer
                        inQuery = true;
                        title = value.replace(/^[#\s]+(.+)\s*$/, '$1');
                        query = '# ' + title + "\n";
                    }
                    else if (inQuery) {
                        query += value + "\n";
                    }
                });
            });
        },
        
        injectFragmentExamples: function() {
            var src = window['kbodata-config']['fragmentExamples'];
            var containersubject = $('#samplessubject');
            var containerpredicate = $('#samplespredicate');
            var containerobject = $('#samplesobject');
            $.get(src, function(data) {
                var lines = data.split(/[\r\n]+/);
                $.each(lines, function(index, value) {
                    if (!value.match(/^#/) && !value.match(/^\s*$/)) {
                      var parts = value.split(/^(.+),,(.+),,(.+)$/);
                      if (parts[3].match(/^subject/)) {
                        containersubject.append('<option value="' + parts[1].replace(/\"/g, '&quot;') + '">' + parts[2] + '</option>');                        
                      }
                      if (parts[3].match(/^object/)) {
                        containerobject.append('<option value="' + parts[1].replace(/\"/g, '&quot;') + '">' + parts[2] + '</option>');
                      }
                      if (parts[3].match(/^predicate/)) {
                        containerpredicate.append('<option value="' + parts[1].replace(/\"/g, '&quot;') + '">' + parts[2] + '</option>');
                      }
                    }
                });
            });
        },
        injectLookupExamples: function() {
          var src = window['kbodata-config']['lookupExamples'];
          var container = $('#explorer .api[data-api="lookup"] select.samples');
          $.get(src, function(data) {
              var lines = data.split(/[\r\n]+/);
              $.each(lines, function(index, value) {
                  if (!value.match(/^#/) && !value.match(/^\s*$/)) {
                      var parts = value.split(/^([^\s]+)\s+(.+)$/);
                      container.append('<option value="' + parts[1] + '">' + parts[2] + '</option>');
                  }
              });
          });
        },
        
        injectReconciliationExamples: function() {
            var src = window['kbodata-config']['reconciliationExamples'];
            var container = $('#explorer .api[data-api="reconciliation"] select.samples');
            $.get(src, function(data) {
                var lines = data.split(/[\r\n]+/);
                $.each(lines, function(index, value) {
                    if (!value.match(/^#/) && !value.match(/^\s*$/)) {
                        container.append('<option value="' + value + '">' + value + '</option>');
                    }
                });
            });
        },
        
        initForms: function() {
            $('#explorer .api form').on('submit', function(e) {
                e.preventDefault();
                var form = $(this);
                var api = $(this).parents('.api').attr('data-api');
                if (api === 'search') {
                    lib.submitSearch($(this).find('[name="keyword"]').val());
                }
                else if (api === 'sparql') {
                    lib.submitSparql($(this).find('[name="query"]').val());
                }
                else if (api === 'lookup') {
                    lib.submitLookup($(this).find('[name="identifier"]').val());
                }
                else if (api === 'fragments') {
                  lib.submitFragment($(this).find('[name="rdfsubject"]').val(),$(this).find('[name="rdfpredicate"]').val(),$(this).find('[name="rdfobject"]').val());
                }
                else if (api === 'reconciliation') {
                    lib.submitReconciliation($(this).find('[name="label"]').val());
                }
                form.find('.buttons')
                    .find('.status').remove().end()
                    .append('<span class="status">Please wait...</span>')
                ;
                lib.pulsate.call(form.find('.status'));
            });
            $(window).on("beforeunload", function() {
                form.find('.status').remove();
            });
        },
        
        pulsate: function() {
            $(this).delay(500).fadeOut(500).delay(250).fadeIn(1000, lib.pulsate);
        },
        
        submitSearch: function(keyword) {
          var endpoint = window['kbodata-config']['searchEndpoint'];
          var url = endpoint + '?search=' + encodeURIComponent(keyword) + "&limit=10";
          location.href = url;
        },

        submitSparql: function(query) {
            var endpoint = window['kbodata-config']['sparqlEndpoint'];
            var url = endpoint + '?query=' + encodeURIComponent(query);
            location.href = url;
        },
        
        submitFragment: function(rdfsubject, rdfpredicate, rdfobject) {
            var endpoint = window['kbodata-config']['fragmentEndpoint'];
            var url = endpoint + '?subject=' + encodeURIComponent(rdfsubject);
            url = url + '&predicate=' + encodeURIComponent(rdfpredicate);
            url = url + '&object=' + encodeURIComponent(rdfobject);
            location.href = url;
        },
        
        submitLookup: function(identifier) {
            location.href = identifier.replace(/\#.+$/, '') + '.html';
        },
        
        submitReconciliation: function(label) {
            var endpoint = window['kbodata-config']['reconciliationEndpoint'];
            var url = endpoint + '?query=' + encodeURIComponent(label) + '&limit=10';
            location.href = url;
        },
        
    init: function() {
            lib.initAccordion();
            lib.initExamples();
            lib.initForms();
    }
  
  };
  
  $(lib.init);  
  
})(jQuery);