package ske.folkeregister.datomic.util;

import datomic.Database;
import datomic.Peer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static datomic.Util.map;

@SuppressWarnings("unchecked")
public class Changes {

   private static final String TX_ATTR_FOR_ENTITY =
      "[:find ?tx ?a :in $ ?e :where [?e ?a _ ?tx]]";

   /**
    * Fra August Lilleaas: http://dbs-are-fn.com/2013/datomic_history_of_an_entity/
    */
   public static List<Map> changeOverTimeForEntity(Database db, Object entityId) {
      return Peer
         // Extracts all transaction/attributes for given database/entity
         .q(TX_ATTR_FOR_ENTITY, db.history(), entityId).stream()
         // Group all attribute changes for one transaction
         .collect(Collectors.groupingBy(list -> list.get(0))).values().stream()
         // Convert to map of timestamped changes
         .map(changes -> {
            final Object tx = changes.get(0).get(0);
            return map(
               ":changes", mapChangesForEntity(db, entityId, changes),
               ":timestamp", db.asOf(tx).entity(tx).get(":db/txInstant")
            );
         })
         // Return as a list of change-maps
         .collect(Collectors.toList());
   }

   private static List<Map> mapChangesForEntity(
      Database db,
      Object entityId,
      List<List<Object>> changes
   ) {
      return changes.stream()
         // Convert to map of attribute-changes
         .map(list -> {
            final Long tx = (Long) list.get(0);
            final Object attr = list.get(1);

            // Get database before and after transaction occured
            final Database dbBefore = db.asOf(tx - 1);
            final Database dbAfter = db.asOf(tx);

            // Get the attribute name before and after transaction occured
            final Object attrBefore = dbBefore.entity(attr).get(":db/ident");
            final Object attrAfter = dbAfter.entity(attr).get(":db/ident");

            // Find both the old and the new value of the attribute
            final Object oldVal = IO.attrToValue(dbBefore.entity(entityId), attrBefore);
            final Object newVal = IO.attrToValue(dbAfter.entity(entityId), attrAfter);

            // Create map with attribute-name -> old/new value
            return map(
               attrAfter, map(
                  ":old", oldVal,
                  ":new", newVal
               )
            );
         })
         .collect(Collectors.toList());
   }
}
