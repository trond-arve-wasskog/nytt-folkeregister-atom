using System.Net.Http;
using System.Web.Http;

namespace Folkeregister.Web.Api.Commands
{
    [RoutePrefix("api/commands/createperson")]
    public class CreatePersonController : BaseCommandController<Contracts.Commands.CreatePerson>
    {
        [Route]
        [HttpPost]
        public HttpResponseMessage Do(Contracts.Commands.CreatePerson command)
        {
            return Execute(command);
        }
    }
}