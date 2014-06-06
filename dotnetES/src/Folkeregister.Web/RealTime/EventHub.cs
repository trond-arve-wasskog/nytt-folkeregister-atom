using System;
using System.Collections.Generic;
using System.Text;
using System.Timers;
using EventStore.ClientAPI;
using Microsoft.AspNet.SignalR;
using Newtonsoft.Json;

namespace Folkeregister.Web.RealTime
{
    public class EventHub : Hub
    {
        private static Timer _timer = null;
        private static IEventStoreConnection _connection;
        public EventHub()
        {
            SetupStream();
        }

        private void SetupStream()
        {
            if (_connection == null)
            {
                _connection = Configuration.CreateConnection();
                Action<ResolvedEvent> notifyClients = re =>
                {
                    var metadata = DeserializeObject<Dictionary<string, string>>(re.OriginalEvent.Metadata);
                    if (metadata != null && metadata.ContainsKey(EventClrTypeHeader))
                    {
                        var eventData = DeserializeObject(re.OriginalEvent.Data, metadata[EventClrTypeHeader]);
                        Clients.All.notify(Tuple.Create(eventData.GetType().Name, eventData));
                    }
                };
                _connection.SubscribeToAll(false, (ess, re) => notifyClients(re));
            }
        }

        private T DeserializeObject<T>(byte[] data)
        {
            return (T)(DeserializeObject(data, typeof(T).AssemblyQualifiedName));
        }

        private object DeserializeObject(byte[] data, string typeName)
        {
            var jsonString = Encoding.UTF8.GetString(data);
            return JsonConvert.DeserializeObject(jsonString, Type.GetType(typeName));
        }

        public string EventClrTypeHeader = "EventClrTypeName";
    }
}