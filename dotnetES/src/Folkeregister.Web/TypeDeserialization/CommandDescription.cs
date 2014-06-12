using System.Collections.Generic;

namespace Folkeregister.Web.TypeDeserialization
{
    public class CommandDescription<TCommand> : ObjectDescription<TCommand>
    {
        public IEnumerable<string> Commands
        {
            get { return _mainTypeNames; }
        }
    }
}