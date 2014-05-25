package ske.folkeregister.datomic.feed;

import datomic.Connection;
import datomic.db.Datum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static datomic.Connection.TX_DATA;

@SuppressWarnings("unchecked")
public class FeedGenerator implements Runnable {

   private final Connection conn;

   private final BlockingQueue<Map> txReportQueue;

   public FeedGenerator(Connection conn) {
      this.conn = conn;
      txReportQueue = conn.txReportQueue();
   }

   @Override
   public void run() {
      while (true) {
         try {
            final Map tx = txReportQueue.take();
            final List<Datum> txData = (List<Datum>) tx.get(TX_DATA);

            System.out.println("### Got tx - data:");
            txData.forEach(data ->
               System.out.printf("ent: %s attr: %s val: %s%n", data.e(), data.a(), data.v()));

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
}
