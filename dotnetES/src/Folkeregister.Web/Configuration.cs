using System.Configuration;
using System.Net;
using EventStore.ClientAPI;
using EventStore.ClientAPI.SystemData;

namespace Folkeregister.Web
{
    public static class Configuration
    {
        private static IEventStoreConnection _connection;
        public static IEventStoreConnection CreateConnection()
        {
            return _connection = _connection ?? Connect();
        }

        private static IEventStoreConnection Connect()
        {
            return Connect(ConfigurationManager.AppSettings["EventStore.UserName"],
                ConfigurationManager.AppSettings["EventStore.Password"]);
        }

        private static IEventStoreConnection Connect(string userName, string password)
        {
            ConnectionSettings settings =
                ConnectionSettings.Create()
                    .UseConsoleLogger()
                    .SetDefaultUserCredentials(new UserCredentials(userName, password));
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