(function (nf) {
   "use strict";

   function loadAllPersons() {
      $.get("/persons").done(function (persons) {
         nf.tmpl.render("persons", {persons: persons}, $("#main"))
            .done(function () {
               $("#extra").html("");
            });
      });
   }

   function showPerson(ssn, person) {
      var clickFn = function () {
         console.log("Yoz clicked the button!", $(this).text());
      };

      nf.tmpl.renderFn("person", person, function (html) {
         $("#main").html(html);

         $("#moveBtn").click(clickFn);
         $("#renameBtn").click(_.bind(changeName, this, person["name"], ssn));
         $("#statusBtn").click(clickFn);
      });
   }

   function showChanges(changes) {
      nf.tmpl.render("changes", changes, $("#extra"));
   }

   function loadPerson(ssn) {
      $.when(
         $.get("/persons/" + ssn),
         $.get("/persons/" + ssn + "/changes")
      ).then(
         function (personResp, changesResp) {
            showPerson(ssn, personResp[0]);
            showChanges(changesResp[0]);
         }
      );
   }

   function changeName(currentName, ssn) {
      var newName = prompt("Nytt navn:", currentName);

      if (newName != null && newName != currentName) {
         $.ajax({
            url: "/persons",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
               ":person/ssn": ssn,
               ":person/name": newName
            }),
            success: _.bind(loadPerson, this, ssn)
         });
      }
   }

   nf.tmpl.init();

   routie({
      "": loadAllPersons,
      ":ssn": loadPerson
   });
})(nf);