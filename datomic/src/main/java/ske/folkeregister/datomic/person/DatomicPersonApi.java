package ske.folkeregister.datomic.person;

import datomic.Connection;
import datomic.Database;
import datomic.Entity;
import datomic.Peer;
import ske.folkeregister.datomic.util.Changes;
import ske.folkeregister.datomic.util.IO;

import java.util.*;
import java.util.stream.Collectors;

import static datomic.Peer.tempid;
import static datomic.Util.*;
import static datomic.Util.list;
import static datomic.Util.map;

public class DatomicPersonApi implements PersonApi {

   private final Connection conn;

   public DatomicPersonApi(Connection conn) {
      this.conn = conn;
   }

   @Override
   public List<Object> findAllPersons() {
      final Database db = conn.db();
      return Peer
         .q("[:find ?p :where [?p :person/ssn]]", db)
         .stream()
         .map(list -> resultListToEntityMap(db, list))
         .collect(Collectors.toList());
   }

   @Override
   public void changeNameAndStatus(String ssn, String newName, String newStatus) throws Exception {
      updatePerson(map(
         ":person/ssn", ssn,
         ":person/sivilstatus", read(newStatus),
         ":person/name", newName
      ));
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
      return findPersonBySSN(ssn)
         .map(id -> Changes.changeOverTimeForEntity(conn.db(), id))
         .orElse(Collections.<Map>emptyList());
   }

   @Override
   public Map getPerson(String ssn) throws Exception {
      return findPersonBySSN(ssn)
         .map(id -> conn.db().entity(id))
         .map(IO::entityToMap)
         .orElse(Collections.emptyMap());
   }

   @Override
   public void updatePerson(Map person) throws Exception {
      final HashMap<Object, Object> txMap = new HashMap<>();

      txMap.put(":db/id", tempid("db.part/user"));
      txMap.putAll(person);

      conn.transact(list(txMap)).get();
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
