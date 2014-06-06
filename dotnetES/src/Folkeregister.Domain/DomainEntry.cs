using System;
using System.Collections.Generic;
using System.Linq;
using Folkeregister.Contracts.Commands;
using Folkeregister.Infrastructure;

namespace Folkeregister.Domain
{
    public class DomainEntry
    {
        private readonly CommandDispatcher _commandDispatcher;

        public DomainEntry(IDomainRepository domainRepository, IEnumerable<Action<ICommand>> preExecutionPipe = null, IEnumerable<Action<object>> postExecutionPipe = null)
        {
            preExecutionPipe = preExecutionPipe ?? Enumerable.Empty<Action<ICommand>>();
            postExecutionPipe = CreatePostExecutionPipe(postExecutionPipe);
            _commandDispatcher = CreateCommandDispatcher(domainRepository, preExecutionPipe, postExecutionPipe);
        }

        public void ExecuteCommand(ICommand command)
        {
            _commandDispatcher.ExecuteCommand(command);
        }

        private CommandDispatcher CreateCommandDispatcher(IDomainRepository domainRepository, IEnumerable<Action<ICommand>> preExecutionPipe, IEnumerable<Action<object>> postExecutionPipe)
        {
            var commandDispatcher = new CommandDispatcher(domainRepository, preExecutionPipe, postExecutionPipe);

            var personCommandHandler = new PersonCommandHandler(domainRepository);
            commandDispatcher.RegisterHandler<CreatePerson>(personCommandHandler.Handle);
            commandDispatcher.RegisterHandler<AddAdressToPerson>(personCommandHandler.Handle);


            return commandDispatcher;
        }

        private IEnumerable<Action<object>> CreatePostExecutionPipe(IEnumerable<Action<object>> postExecutionPipe)
        {
            if (postExecutionPipe != null)
            {
                foreach (var action in postExecutionPipe)
                {
                    yield return action;
                }
            }
        }
    }
}
