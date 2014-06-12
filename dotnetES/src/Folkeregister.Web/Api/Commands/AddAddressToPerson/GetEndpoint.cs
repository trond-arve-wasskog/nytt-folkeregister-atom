using Folkeregister.Infrastructure;
using Folkeregister.Web.TypeDeserialization;
using Simple.Web;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands.AddAddressToPerson
{
    [UriTemplate("/api/commands/addaddresstoperson")]
    [Canonical(typeof(CommandDescription<ICommand>))]
    public class GetEndpoint : BaseGetEndpoint<Contracts.Commands.AddAdressToPerson>
    {
    }
}