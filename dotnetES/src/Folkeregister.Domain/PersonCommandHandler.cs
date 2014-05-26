using Folkeregister.Contracts.Commands;
using Folkeregister.Domain.Aggregates;
using Folkeregister.Domain.Exceptions;
using Folkeregister.Infrastructure;
using Folkeregister.Infrastructure.Exceptions;

namespace Folkeregister.Domain
{
    internal class PersonCommandHandler 
    {
        private readonly IDomainRepository _domainRepository;

        public PersonCommandHandler(IDomainRepository domainRepository)
        {
            _domainRepository = domainRepository;
        }

        public IAggregate Handle(CreatePerson command)
        {
            Person person = null;
            try
            {
                person = _domainRepository.GetById<Person>(command.Id);
            }
            catch (AggregateNotFoundException ex)
            { }
            if (person != null)
            {
                throw new UserIdIsAlreadyInUseException();
            }
            person = Person.Create(command.Id, command.SSN, command.Name);
            return person;
        }
    }
}