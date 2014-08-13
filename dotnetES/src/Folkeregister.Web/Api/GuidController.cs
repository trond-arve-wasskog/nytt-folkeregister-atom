using System;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace Folkeregister.Web.Api
{
    [RoutePrefix("api/guid")]
    public class GuidController : ApiController
    {
        [HttpGet]
        [Route]
        public HttpResponseMessage Get()
        {
            return Request.CreateResponse(HttpStatusCode.OK, new { Guid = Guid.NewGuid()});
        }
    }
}
