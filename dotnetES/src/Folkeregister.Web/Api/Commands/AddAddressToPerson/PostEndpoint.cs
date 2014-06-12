using Folkeregister.Web.TypeDeserialization;
using Simple.Web;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands.AddAddressToPerson
{
    [UriTemplate("/api/commands/addaddresstoperson")]
    [LinksFrom(typeof(CommandDescription<Contracts.Commands.AddAdressToPerson>))]
    public class PostEndpoint : BasePostEndpoint<Contracts.Commands.AddAdressToPerson>
    {
    }
}