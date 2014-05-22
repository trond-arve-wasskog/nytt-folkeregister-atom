package ske.folkeregister.dw.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static datomic.Util.map;

@Path("test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

   @GET
   public Object test() {
      return map(":message", "Hello World!");
   }
}
