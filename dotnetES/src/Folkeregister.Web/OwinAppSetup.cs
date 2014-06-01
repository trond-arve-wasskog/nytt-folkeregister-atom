using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Folkeregister.Contracts.Commands;
using Simple.Web;

namespace Folkeregister.Web
{
    public class OwinAppSetup
    {
        public static Type[] EnforceReferencesFor =
                {
                    typeof (Simple.Web.JsonNet.JsonMediaTypeHandler),
                    typeof(CreatePerson)
                };

        public static void Setup(Action<Func<Func<IDictionary<string, object>, Task>, Func<IDictionary<string, object>, Task>>> use)
        {
            use(Application.App);
        }
    }
}