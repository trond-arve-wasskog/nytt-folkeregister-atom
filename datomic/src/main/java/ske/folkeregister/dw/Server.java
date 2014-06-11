package ske.folkeregister.dw;

import com.bazaarvoice.dropwizard.webjars.WebJarBundle;
import com.sun.jersey.api.client.Client;
import datomic.Connection;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ske.folkeregister.datomic.feed.FeedGenerator;
import ske.folkeregister.datomic.person.DatomicPersonApi;
import ske.folkeregister.datomic.person.PersonApi;
import ske.folkeregister.datomic.util.IO;
import ske.folkeregister.dw.api.PersonResource;
import ske.folkeregister.dw.conf.ServerConfig;
import ske.folkeregister.dw.managed.DatomicConnectionManager;
import ske.folkeregister.dw.managed.ThreadManager;

public class Server extends Application<ServerConfig> {

   public static void main(String[] args) throws Exception {
      new Server().run(new String[]{"server", "src/main/conf/config.yml"});
   }

   @Override
   public void initialize(Bootstrap<ServerConfig> bootstrap) {
      bootstrap.addBundle(new WebJarBundle());
      bootstrap.addBundle(new AssetsBundle("/web/", "/web", "index.html"));
   }

   @Override
   public void run(ServerConfig conf, Environment env) throws Exception {
      final Connection conn = IO.newMemConnection();
      final PersonApi personApi = new DatomicPersonApi(conn);
      final Client client = new JerseyClientBuilder(env)
         .using(conf.getJerseyClientConfig())
         .build("feedGenClient");

      IO.transactAllFromFile(conn, "datomic/schema.edn");
      IO.transactAllFromFile(conn, "datomic/data.edn");

      env.jersey().register(new PersonResource(personApi));

      env.lifecycle().manage(new DatomicConnectionManager(conn));
      env.lifecycle().manage(new ThreadManager(new FeedGenerator(conn, client.resource(conf.getFeedServerUrl()))));
   }
}
