using System;
using System.Collections.Generic;
using System.Text;
using EventStore.ClientAPI;
using Newtonsoft.Json;

namespace Folkeregister.Web.RealTime
{
    public class EventStoreDeserializer
    {
        public string EventClrTypeHeader = "EventClrTypeName";

        private T DeserializeObject<T>(byte[] data)
        {
            return (T)(DeserializeObject(data, typeof(T).AssemblyQualifiedName));
        }

        private object DeserializeObject(byte[] data, string typeName)
        {
            var jsonString = Encoding.UTF8.GetString(data);
            return JsonConvert.DeserializeObject(jsonString, Type.GetType(typeName));
        }

        public object DeserializeEvent(ResolvedEvent resolvedEvent)
        {
            var metadata = DeserializeObject<Dictionary<string, string>>(resolvedEvent.OriginalEvent.Metadata);
            if (metadata != null && metadata.ContainsKey(EventClrTypeHeader))
            {
                var eventData = DeserializeObject(resolvedEvent.OriginalEvent.Data, metadata[EventClrTypeHeader]);
                return eventData as object;
            }
            return null;
        }
    }
}