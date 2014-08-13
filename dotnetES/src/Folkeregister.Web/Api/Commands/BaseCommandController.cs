using System.Net;
using System.Net.Http;
using System.Web.Http;
using Folkeregister.Domain;
using Folkeregister.Infrastructure;

namespace Folkeregister.Web.Api.Commands
{
    public abstract class BaseCommandController<TCommand> : ApiController where TCommand : ICommand
    {
        public HttpResponseMessage Execute(TCommand command)
        {
            var connection = Web.Configuration.CreateConnection();
            var domainRepository = new EventStoreDomainRepository(connection);
            var application = new DomainEntry(domainRepository);
            application.ExecuteCommand(command);
            return Request.CreateResponse(HttpStatusCode.OK);
        }
    }
}