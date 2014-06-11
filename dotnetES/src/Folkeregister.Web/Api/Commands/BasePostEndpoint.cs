using Folkeregister.Domain;
using Folkeregister.Infrastructure;
using Simple.Web;
using Simple.Web.Behaviors;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands
{
    public abstract class BasePostEndpoint<TCommand> : IPost, IInput<TCommand> where TCommand : ICommand
    {
        public Status Post()
        {
            var connection = Configuration.CreateConnection();
            var domainRepository = new EventStoreDomainRepository(connection);
            var application = new DomainEntry(domainRepository);
            application.ExecuteCommand(Input);

            return Status.OK;
        }

        public TCommand Input { set; private get; }
    }
}