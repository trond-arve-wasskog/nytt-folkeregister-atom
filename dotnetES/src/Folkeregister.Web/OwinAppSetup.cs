using System;
using System.Threading.Tasks;
using Folkeregister.Contracts.Commands;
using Folkeregister.Web;
using Microsoft.AspNet.SignalR;
using Microsoft.Owin;
using Newtonsoft.Json;
using Owin;
using Simple.Owin.Static;
using Simple.Web;

[assembly: OwinStartup(typeof(OwinAppSetup))]
namespace Folkeregister.Web
{
    public class OwinAppSetup
    {
        public static Type[] EnforceReferencesFor =
                {
                    typeof (Simple.Web.JsonNet.JsonMediaTypeHandler),
                    typeof(CreatePerson)
                };

        public void Configuration(IAppBuilder app)
        {

            JsonConvert.DefaultSettings = () => new JsonSerializerSettings()
            {
                TypeNameHandling = TypeNameHandling.Objects
            };
            var staticBuilder = Statics.AddFolderAlias("/Scripts", "/scripts").AddFileAlias("/Static/index.html", "/");
            app.Use(staticBuilder.Build());

            var hubConfiguration = new HubConfiguration();
            app.MapSignalR("/realtime", hubConfiguration);
            app.Run(context => Application.App(_ => null as Task)(context.Environment));

        }
    }
}