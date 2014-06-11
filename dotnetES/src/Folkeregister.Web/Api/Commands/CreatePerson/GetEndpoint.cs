using Folkeregister.Infrastructure;
using Simple.Web;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands.CreatePerson
{
    [UriTemplate("/api/commands/createperson")]
    [Canonical(typeof(CommandDescription<ICommand>))]
    public class GetEndpoint : BaseGetEndpoint<Contracts.Commands.CreatePerson>
    {
    }
}