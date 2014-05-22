package ske.folkeregister.datomic.person;

import datomic.Connection;
import datomic.Database;
import datomic.Entity;
import datomic.Peer;
import ske.folkeregister.datomic.util.Changes;
import ske.folkeregister.datomic.util.IO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static datomic.Peer.tempid;
import static datomic.Util.*;

public class DatomicPersonApi implements PersonApi {

   private final Connection conn;

   public DatomicPersonApi(Connection conn) {
      this.conn = conn;
   }

   @Override
   public List<Object> findAllPersons() {
      final Database db = conn.db();
      return Peer
         .q("[:find ?p :where [?p :person/name]]", db)
         .stream()
         .map(list -> resultListToEntityMap(db, list))
         .collect(Collectors.toList());
   }

   @Override
   public void changeNameAndStatus(String ssn, String newName, String newStatus) throws Exception {
      conn.transact(list(map(
         ":db/id", lookupRef(ssn),
         ":person/sivilstatus", read(":person.sivilstatus/gift"),
         ":person/name", "Eivind Barstad Waaler"
      ))).get();
   }

   @Override
   public void moveToNewAddress(String ssn, String street, String number, String postnumber) throws Exception {
      Object newTmpAddressId = tempid("db.part/user");
      conn.transact(list(
         map(
            ":db/id", newTmpAddressId,
            ":address/street", street,
            ":address/streetnumber", number,
            ":address/postnumber", postnumber
         ),
         map(
            ":db/id", list(":person/ssn", ssn),
            ":person/address", newTmpAddressId
         )
      )).get();
   }

   @Override
   public List<Map> changesForPerson(String ssn) throws Exception {
      final Object personId = findPersonBySSN(ssn).get();
      return Changes.changeOverTimeForEntity(conn.db(), personId);
   }

   @Override
   public Map getPerson(String ssn) throws Exception {
      return IO.entityToMap(conn.db().entity(lookupRef(ssn)));
   }

   private Optional<Object> findPersonBySSN(String ssn) {
      return Optional.ofNullable(conn.db().entid(lookupRef(ssn)));
   }

   private List lookupRef(String ssn) {
      return list(":person/ssn", ssn);
   }

   private Map resultListToEntityMap(Database db, List results) {
      final Entity entity = db.entity(results.get(0));
      return IO.entityToMap(entity);
   }
}
