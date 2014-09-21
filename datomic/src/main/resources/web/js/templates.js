nf.tmpl = nf.tmpl || {};

(function(tmpl, $, dust) {
   "use strict";

   function compileAndLoadTemplate(name) {
      var $deferred = $.Deferred();

      if (_.isUndefined(dust.cache[name])) {
         $.get("/web/js/tmpl/" + name + ".html")
            .done(function (template) {
               var compiled = dust.compile(template, name);
               dust.loadSource(compiled);
               $deferred.resolve("Loaded");
            });
      } else {
         $deferred.resolve("Cached");
      }

      return $deferred.promise();
   }

   tmpl.render = function (name, data, $element) {
      return tmpl.renderFn(name, data, function (html) {
         $element.html(html);
      });
   };

   tmpl.renderFn = function (name, data, fn) {
      return $.when(compileAndLoadTemplate(name))
         .done(function () {
            dust.render(name, data, function (err, out) {
               if (err) {
                  console.error("Template error: " + name, err["message"]);
               } else {
                  fn(out);
               }
            });
         });
   };

   tmpl.init = function () {
      dust.helpers.toDate = function (chunk, context, bodies, params) {
         return chunk.write(new Date(params.value));
      };

      compileAndLoadTemplate("input");
      compileAndLoadTemplate("button");
   };
}(nf.tmpl, jQuery, dust));