using System.Web.Http;
using Folkeregister.Web;
using Microsoft.Owin;
using Newtonsoft.Json;
using Owin;
using Simple.Owin.Static;

//[assembly: OwinStartup(typeof(Startup))]
namespace Folkeregister.Web
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {

            //JsonConvert.DefaultSettings = () => new JsonSerializerSettings()
            //{
            //    TypeNameHandling = TypeNameHandling.Objects
            //};

            var config = new HttpConfiguration();
            config.Formatters.JsonFormatter.SerializerSettings = new JsonSerializerSettings()
            {
                ConstructorHandling = ConstructorHandling.AllowNonPublicDefaultConstructor
            };
            config.MapHttpAttributeRoutes();
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            var staticBuilder = Statics.AddFolder("/assets").AddFileAlias("/Static/index.html", "/");
            app.Use(staticBuilder.Build());

            config.EnsureInitialized();
            app.UseWebApi(config);
        }
    }
}