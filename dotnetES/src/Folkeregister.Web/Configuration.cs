using System.Net;
using EventStore.ClientAPI;
using EventStore.ClientAPI.SystemData;

namespace Folkeregister.Web
{
    public static class Configuration
    {
        public static IEventStoreConnection CreateConnection()
        {
            ConnectionSettings settings =
                ConnectionSettings.Create()
                    .UseConsoleLogger()
                    .SetDefaultUserCredentials(new UserCredentials("admin", "changeit"));
            var endPoint = new IPEndPoint(IPAddress.Loopback, 1113);
            var connection = EventStoreConnection.Create(settings, endPoint, null);
            connection.Connect();
            return connection;
        }
    }
}