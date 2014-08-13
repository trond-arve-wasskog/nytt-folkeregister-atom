using System.Collections.Generic;

namespace Folkeregister.Web.TypeDeserialization
{
    public class ObjectModel
    {
        public string Type { get; set; }
        public string Name { get; set; }
        public string QualifiedName { get; set; }
        public IEnumerable<Argument> Arguments { get; set; }
    }
}