using Folkeregister.Infrastructure;
using Simple.Web;
using Simple.Web.Behaviors;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands
{
    [LinksFrom(typeof(CommandDescription<ICommand>), uriTemplate: "/api/commands")]
    public abstract class BasePostEndpoint<TCommand> : IPost, IInput<TCommand>
    {
        public Status Post()
        {
            return Status.OK;
        }

        public TCommand Input { set; private get; }
    }
}