package ske.folkeregister.dw.api;

import ske.folkeregister.datomic.person.PersonApi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
}
