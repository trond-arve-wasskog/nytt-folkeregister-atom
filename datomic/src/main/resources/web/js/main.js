(function () {
   "use strict";

   function loadAllPersons() {
      $.get("/persons", function (persons) {
         var $main = $("#main");

         $main.html("<h1>Personer</h1>");

         var $personlist = $("<ul/>");

         _.each(persons, function (person) {
            var $link = $("<a href='#" + person[":person/ssn"] + "'>" + person[":person/name"] + "</a>");
            $personlist.append($("<li/>").append($link));
         });

         $main.append($personlist);
      });
   }

   function loadPerson(ssn) {
      $.get("/persons/" + ssn, function (person) {
         var $main = $("#main");

         $main.html("<h1>Persondetaljer</h1>");

         var $fieldset = $("<fieldset/>");

         createInput($fieldset, "ssn", ssn, "Fødselsnummer");
         createInput($fieldset, "name", person[":person/name"], "Navn");
         createInput($fieldset, "birthplace", person[":person/birthplace"], "Fødested");
         createInput($fieldset, "sivilstatus", person[":person/sivilstatus"], "Sivilstatus");

         $main
            .append($("<form class='pure-form pure-form-aligned'/>")
            .append($fieldset));

         console.log("Loaded", person);
      });
   }

   function createInput($parent, id, value, label) {
      if (value) {
         var $div = $("<div class='pure-control-group'>");

         var $label = $("<label for='" + id + "'>" + label + "</label>");
         var $input = $("<input readonly class='pure-input-rounded' id='" + id + "' type='text' placeholder='" + label + "' value='" + value + "'>");

         $div.append($label, $input);

         $parent.append($div);
      }
   }

   routie({
      "": loadAllPersons,
      ":ssn": loadPerson
   });
})();