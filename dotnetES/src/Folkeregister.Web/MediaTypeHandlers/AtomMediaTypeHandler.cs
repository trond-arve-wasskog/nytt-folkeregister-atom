using System;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using Simple.Web.MediaTypeHandling;

namespace Folkeregister.Web.MediaTypeHandlers
{
    [MediaTypes("application/atom+xml")]
    public class AtomMediaTypeHandler : IMediaTypeHandler
    {
        public Task<T> Read<T>(Stream inputStream)
        {
            throw new System.NotImplementedException();
        }

        public Task Write<T>(IContent content, Stream outputStream)
        {
            var feeder = content.Handler as IFeed;
            if (feeder == null)
            {
                throw new RouteNotSupportingFeedException(content);
            }
            string outputText = "Tomas";
            byte[] buffer = Encoding.UTF8.GetBytes(outputText);
            return outputStream.WriteAsync(buffer, 0, buffer.Length);
        }
    }

    public class RouteNotSupportingFeedException : Exception
    {
        public RouteNotSupportingFeedException(IContent content) : base("Route: " + content.Uri.ToString() + ", Handler: " + content.Handler.GetType())
        {
        }
    }

    public interface IFeed
    {
    }
}