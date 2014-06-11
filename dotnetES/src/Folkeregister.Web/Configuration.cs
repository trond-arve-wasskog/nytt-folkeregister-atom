using System.Configuration;
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
            var endPoint = new IPEndPoint(EventStoreIP, EventStorePort);
            var connection = EventStoreConnection.Create(settings, endPoint, null);
            connection.Connect();
            return connection;
        }

        public static IPAddress EventStoreIP
        {
            get
            {
                var hostname = ConfigurationManager.AppSettings["EventStoreHostName"];
                if (string.IsNullOrEmpty(hostname))
                {
                    return IPAddress.Loopback;
                }
                var ipAddresses = Dns.GetHostAddresses(hostname);
                return ipAddresses[0];
            }
        }

        public static int EventStorePort
        {
            get
            {
                var esPort = ConfigurationManager.AppSettings["EventStorePort"];
                if (string.IsNullOrEmpty(esPort))
                {
                    return 1113;
                }
                return int.Parse(esPort);
            }
        }
    }
}