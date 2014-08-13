using System.Net.Http;
using System.Web.Http;

namespace Folkeregister.Web.Api.Commands
{
    [RoutePrefix("api/commands/addaddresstoperson")]
    public class AddAddressToPersonController : BaseCommandController<Contracts.Commands.AddAdressToPerson>
    {
        [Route]
        [HttpPost]
        public HttpResponseMessage Post(Contracts.Commands.AddAdressToPerson command)
        {
            return Execute(command);
        }
    }
}