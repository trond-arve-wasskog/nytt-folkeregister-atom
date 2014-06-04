using Folkeregister.Web.Api.Commands;
using Simple.Web;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands.CreatePerson
{
    [UriTemplate("/api/commands/createperson")]
    [LinksFrom(typeof(CommandDescription<Contracts.Commands.CreatePerson>), uriTemplate: "/api/commands/createperson")]
    public class PostEndpoint : BasePostEndpoint<Contracts.Commands.CreatePerson>
    {
    }
}