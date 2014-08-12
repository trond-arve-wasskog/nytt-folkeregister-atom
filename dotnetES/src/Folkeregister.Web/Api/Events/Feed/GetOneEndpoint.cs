using EventStore.ClientAPI;
using Folkeregister.Infrastructure;
using Folkeregister.Web.RealTime;
using Simple.Web;
using Simple.Web.Behaviors;

namespace Folkeregister.Web.Api.Events
{
    [UriTemplate("/api/events/feed/{EventStream}/{EventNumber}")]
    public class GetOneEndpoint : IGet, IOutput<EventViewModel>
    {
        public Status Get()
        {
            var connection = Configuration.CreateConnection();
            var readEvent = connection.ReadEvent(EventStream, EventNumber, true);
            var eventStoreDeserializer = new EventStoreDeserializer();
            if (readEvent.Status == EventReadStatus.Success)
            {
                Output = new EventViewModel()
                {
                    EventStream = EventStream,
                    EventNumber = EventNumber,
                    Event = eventStoreDeserializer.DeserializeEvent(readEvent.Event.Value) as IEvent
                };
            }
            return Status.OK;
        }

        public string EventStream { set; private get; }
        public int EventNumber { set; private get; }
        public EventViewModel Output { get; private set; }
    }

    public class EventViewModel
    {
        public object Event { get; set; }
        public string EventStream { get; set; }
        public int EventNumber { get; set; }
    }
}