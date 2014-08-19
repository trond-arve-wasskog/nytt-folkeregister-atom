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

   function showPerson(ssn, person) {
      var $main = $("#main");

      $main.html("<h1>Persondetaljer</h1>");

      var $fieldset = $("<fieldset/>");

      createInput($fieldset, "ssn", ssn, "Fødselsnummer");
      createInput($fieldset, "name", person[":person/name"], "Navn");
      createInput($fieldset, "birthplace", person[":person/birthplace"], "Fødested");
      createInput($fieldset, "sivilstatus", person[":person/sivilstatus"], "Sivilstatus");

      var address = person[":person/address"];
      if (address) {
         createInput(
            $fieldset,
            "address_street",
               address[":address/street"] + " " + address[":address/streetnumber"],
            "Adresse"
         );
         createInput($fieldset, "address_postnumber", address[":address/postnumber"], "");
      }

      var $buttonDiv = $("<div class='pure-controls'/>");

      var clickFn = function () {
         console.log("Yoz clicked the button!", $(this).text());
      };

      createButton($buttonDiv, "Flytting", clickFn);
      createButton(
         $buttonDiv,
         "Navneendring",
         _.bind(changeName, this, person[":person/name"], ssn)
      );
      createButton($buttonDiv, "Statusendring", clickFn);

      $fieldset.append($buttonDiv);

      $main.append($("<form class='pure-form pure-form-aligned'/>").append($fieldset));
   }

   function showChanges(changes) {
      var $extra = $("#extra");

      $extra.html("<h1>Endringer</h1>");

      _.each(changes, function(change) {
         console.log("Endring", new Date(change[":timestamp"]), change);
      });
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

   function createButton($buttonDiv, text, fn) {
      var $button = $("<button type='button' class='pure-button pure-button-primary'>" + text + "</button>");
      $button.click(fn);
      $buttonDiv.append($button);
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