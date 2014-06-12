using Folkeregister.Web.TypeDeserialization;
using Simple.Web;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands.CreatePerson
{
    [UriTemplate("/api/commands/createperson")]
    [LinksFrom(typeof(CommandDescription<Contracts.Commands.CreatePerson>))]
    public class PostEndpoint : BasePostEndpoint<Contracts.Commands.CreatePerson>
    {
    }
}