package ske.folkeregister.datomic.feed;

import datomic.Connection;
import datomic.Peer;
import datomic.db.Datum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static datomic.Connection.*;
import static datomic.Util.map;

@SuppressWarnings("unchecked")
public class FeedGenerator implements Runnable {

   private static final String query =
      "[:find ?ssn ?attrname ?value" +
         " :in $ ?entity ?attr ?value" +
         " :where [?entity ?attr ?value]" +
         "        [?entity :person/ssn ?ssn]" +
         "        [?attr :db/ident ?attrname]]";

   private final BlockingQueue<Map> txReportQueue;

   public FeedGenerator(Connection conn) {
      txReportQueue = conn.txReportQueue();
   }

   @Override
   public void run() {
      while (true) {
         try {
            final Map tx = txReportQueue.take();
            final List<Datum> txData = (List<Datum>) tx.get(TX_DATA);

            System.out.println("### Got tx - data:");

            System.out.println("Timestamp: " + txData.get(0).v());

            txData
               .stream()
               .substream(1)
               .map(datum -> {
                  if (datum.added()) {
                     return map(
                        "add",
                        queryFor(tx.get(DB_AFTER), datum)
                     );
                  } else {
                     return map(
                        "rem",
                        queryFor(tx.get(DB_BEFORE), datum)
                     );
                  }
               })
               .forEach(System.out::println);

            /*
            Entry entry = Abdera.getInstance().newEntry();
            entry.setId("idstr");
            entry.setUpdated(timestamp);
            entry.addCategory(entry.addCategory("cat-term"));
            entry.addLink(entry.addLink("http://dsf.no/api/v1/person/05047954321/", "person"));

            System.out.println(Abdera.getNewWriter().write(entry));
            */

            /*
            <entry xmlns="http://www.w3.org/2005/Atom">
               <id>idstr</id>
               <updated>2014-05-25T20:38:36.819Z</updated>
               <category term="cat-term" />
               <link href="http://dsf.no/api/v1/person/05047954321/" rel="person" />
            </entry>
            */
         } catch (Exception e) {
            System.err.println("### Yikes: " + e.getMessage());
            e.printStackTrace();
         }
      }
   }

   private List<Object> queryFor(Object db, Datum datum) {
      return Peer.q(query, db, datum.e(), datum.a(), datum.v()).iterator().next();
   }
}
