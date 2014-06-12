using EventStore.ClientAPI;
using Folkeregister.Infrastructure;
using Folkeregister.Web.TypeDeserialization;
using Simple.Web;
using Simple.Web.Behaviors;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands
{
    public abstract class BaseGetEndpoint<TCommand> : IGet, IOutput<CommandDescription<TCommand>>
    {
        private IEventStoreConnection _connection;

        public BaseGetEndpoint()
        {
            _connection = Configuration.CreateConnection();
            Output = new CommandDescription<TCommand>();
        }

        public Status Get()
        {

            return Status.OK;
        }

        public CommandDescription<TCommand> Output { get; private set; }
    }
}