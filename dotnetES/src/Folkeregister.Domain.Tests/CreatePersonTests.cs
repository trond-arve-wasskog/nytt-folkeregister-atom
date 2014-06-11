using System;
using Folkeregister.Contracts.Commands;
using Folkeregister.Contracts.Events;
using Folkeregister.Contracts.Types;
using Folkeregister.Domain.Exceptions;
using NUnit.Framework;

namespace Folkeregister.Domain.Tests
{
    [TestFixture]
    public class AddAddressTests : TestBase
    {
        [Test]
        public void AddingAnAddressToAPerson_ShouldAddAddress()
        {
            Guid id = Guid.NewGuid();
            SSN sSN = null;
            Name name = null;
            Given(new PersonCreated(id, sSN, name));
            var address = new Address("s1", "23", "1232", "Oslo");
            When(new AddAdressToPerson(id, address));
            Then(new AddressAdded(id, address));
        }
    }

    [TestFixture]
    public class CreatePersonTests : TestBase
    {
        [Test]
        public void WhenAllParametersAreValid_PersonShouldBeCreated()
        {
            var createPerson = GetCreatePerson();
            When(createPerson);
            Then(new PersonCreated(createPerson.Id, createPerson.SSN, createPerson.Name));
        }

        [Test]
        public void WhenIdAlreadyExist_ShouldThrowException()
        {
            var id = IdGenerator.GetId();
            var createPerson = GetCreatePerson(id);
            Given(new PersonCreated(id, null, null));
            WhenThrows<UserIdIsAlreadyInUseException>(createPerson);
        }

        [Test]
        public void WhenInvalidSsn_ShouldThrowException()
        {
            var createPerson = GetCreatePerson(ssn: new SSN(""));
            WhenThrows<InvalidSsnException>(createPerson);
        }

        [Test]
        public void WhenInvalidName_ShouldThrowException()
        {
            var createPerson = GetCreatePerson(name: new Name(""));
            WhenThrows<InvalidNameException>(createPerson);
        }

        private CreatePerson GetCreatePerson(Guid? id = null, SSN ssn = null, Name name = null)
        {
            var idToUse = id.HasValue ? id.Value : IdGenerator.GetId();
            ssn = ssn ?? new SSN("08088212323");
            name = name ?? new Name("Tomas Jansson");
            return new CreatePerson(idToUse, ssn, name);
        }
    }
}
