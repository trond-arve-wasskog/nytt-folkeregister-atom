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
            _eventStoreDeserializer = new EventStoreDeserializer();
        }

        private void SetupStream()
        {
            if (_connection == null)
            {
                _connection = Configuration.CreateConnection();
                Action<ResolvedEvent> notifyClients = re =>
                {
                    var eventObject = _eventStoreDeserializer.DeserializeEvent(re);
                    if (eventObject != null)
                    {
                        Clients.All.notify(Tuple.Create(eventObject.GetType().Name, eventObject));
                    }
                };
                _connection.SubscribeToAll(false, (ess, re) => notifyClients(re));
            }
        }

        private readonly EventStoreDeserializer _eventStoreDeserializer;
    }
}