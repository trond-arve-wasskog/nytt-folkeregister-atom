//using Folkeregister.Infrastructure;
//using Folkeregister.Web.TypeDeserialization;
//using Simple.Web;
//using Simple.Web.Behaviors;
//using Simple.Web.Links;

//namespace Folkeregister.Web.Api.Commands
//{
//    [UriTemplate("/api/commands")]
//    [Root(Rel = "commands", Title = "Command list")]
//    public class GetEndpoint : IGet, IOutput<CommandDescription<ICommand>>
//    {
//        public Status Get()
//        {
//            Output = new CommandDescription<ICommand>();
//            return Status.OK;
//        }
//        public CommandDescription<ICommand> Output { get; private set; }
//    }
//}