package ske.folkeregister.datomic.person;

import java.util.List;
import java.util.Map;

public interface PersonApi {
   List<Object> findAllPersons();

   void changeNameAndStatus(String ssn, String newName, String newStatus) throws Exception;

   void moveToNewAddress(String ssn, String street, String number, String postnumber) throws Exception;

   List<Map> changesForPerson(String ssn) throws Exception;

   Map getPerson(String ssn) throws Exception;

   void updatePerson(Map person) throws Exception;
}
