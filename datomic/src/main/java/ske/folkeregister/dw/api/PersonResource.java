package ske.folkeregister.dw.api;

import ske.folkeregister.datomic.person.PersonApi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

   private final PersonApi personApi;

   public PersonResource(PersonApi personApi) {
      this.personApi = personApi;
   }

   @GET
   public Object findAll() {
      return personApi.findAllPersons();
   }

   @GET
   @Path("{ssn}/changes")
   public Object changes(@PathParam("ssn") String ssn) throws Exception {
      return personApi.changesForPerson(ssn);
   }

   @GET
   @Path("{ssn}")
   public Object get(@PathParam("ssn") String ssn) throws Exception {
      return personApi.getPerson(ssn);
   }

   @POST
   public void update(Map person) throws Exception {
      personApi.updatePerson(person);
   }
}
