using System;
using Folkeregister.Contracts.Events;
using Folkeregister.Contracts.Types;
using Folkeregister.Domain.Exceptions;
using Folkeregister.Infrastructure;

namespace Folkeregister.Domain.Aggregates
{
    public class Person : AggregateBase
    {
        public Person()
        {
            RegisterTransition<PersonCreated>(Apply);
        }

        private void Apply(PersonCreated @event)
        {
            Id = @event.Id;
        }

        private Person(Guid id, SSN ssn, Name name) : this()
        {
            RaiseEvent(new PersonCreated(id, ssn, name));
        }

        public static Person Create(Guid id, SSN ssn, Name name)
        {
            if (ssn == null || string.IsNullOrEmpty(ssn.Value))
            {
                throw new InvalidSsnException(ssn);
            }
            if (name == null || string.IsNullOrEmpty(name.Value))
            {
                throw new InvalidNameException(name);
            }
            return new Person(id, ssn, name);
        }

        public void AddAdressToPerson(Address address)
        {
            RaiseEvent(new AddressAdded(Id, address));
        }
    }
}