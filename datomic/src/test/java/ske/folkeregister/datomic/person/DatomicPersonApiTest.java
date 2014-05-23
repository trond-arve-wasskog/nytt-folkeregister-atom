package ske.folkeregister.datomic.person;

import datomic.Connection;
import org.junit.Before;
import org.junit.Test;
import ske.folkeregister.datomic.util.IO;

import java.util.List;
import java.util.Map;

import static datomic.Util.map;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class DatomicPersonApiTest {

   private PersonApi personApi;

   @Test
   public void testListingChanges() throws Exception {
      final String ssn = "4321";

      personApi.updatePerson(map(
         ":person/ssn", ssn,
         ":person/name", "Test Person",
         ":person/sivilstatus", ":person.sivilstatus/ugift"
      ));

      personApi.changeNameAndStatus(ssn, "Test Middlename Person", ":person.sivilstatus/gift");

      personApi.updatePerson(map(
         ":person/ssn", ssn,
         ":person/sivilstatus", ":person.sivilstatus/skilt"
      ));

      personApi.moveToNewAddress(ssn, "Street", "101", "0102");

      final List<Map> changes = personApi.changesForPerson(ssn);

      assertEquals(4, changes.size());

      printChanges(changes);
   }

   @SuppressWarnings("unchecked")
   private void printChanges(List<Map> changes) {
      System.out.println("\n### Changes for person: ###");
      changes.forEach(changeset -> {
         System.out.println("\nTidspunkt: " + changeset.get(":timestamp"));
         System.out.println("Endringer:");
         ((List) changeset.get(":changes")).forEach(obj -> System.out.println("\t" + obj));
      });
   }

   @Test
   public void testUpdateAndGet() throws Exception {
      final String ssn = "1234";
      final String name = "Test Person";

      assertTrue("Empty map == not found", personApi.getPerson(ssn).isEmpty());

      personApi.updatePerson(map(
         ":person/ssn", ssn,
         ":person/name", name
      ));

      final Map person = personApi.getPerson(ssn);

      assertEquals(ssn, person.get(":person/ssn"));
      assertEquals(name, person.get(":person/name"));

      assertTrue("Person not in all persons", personApi.findAllPersons().contains(person));
   }

   @Before
   public void setUp() throws Exception {
      Connection conn = IO.newMemConnection();

      IO.transactAllFromFile(conn, "datomic/schema.edn");
      //IO.transactAllFromFile(conn, "datomic/data.edn");

      personApi = new DatomicPersonApi(conn);
   }
}