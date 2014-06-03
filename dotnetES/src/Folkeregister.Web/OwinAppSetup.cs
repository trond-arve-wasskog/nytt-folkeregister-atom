using System;
using Folkeregister.Contracts.Commands;
using Folkeregister.Web;
using Microsoft.AspNet.SignalR;
using Microsoft.Owin;
using Newtonsoft.Json;
using Owin;
using Simple.Owin.Static;
using Simple.Web;
using Simple.Web.OwinSupport;

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

        //public static void Setup(Action<Func<Func<IDictionary<string, object>, Task>, Func<IDictionary<string, object>, Task>>> use)
        public void Configuration(IAppBuilder app)
        {

            JsonConvert.DefaultSettings = () => new JsonSerializerSettings()
            {
                TypeNameHandling = TypeNameHandling.Objects
            };
            //var staticBuilder = Statics.AddFolderAlias("/Scripts", "/scripts").AddFileAlias("/Static/index.html", "/");
            //app.Use(staticBuilder.Build());

            //var hubConfiguration = new HubConfiguration();
            //app.MapSignalR("/realtime", hubConfiguration);
            app.UseSimpleWeb();
//app.Run(c => Application.App());
            app.Run(context =>
            {
                context.Response.ContentType = "text/plain";
                return context.Response.WriteAsync("Hello, world.");
            });
            //use(staticBuilder.Build());

            //use((hm) => hm);

            //use(Application.App);
        }
    }
}