using System.Timers;
using Microsoft.AspNet.SignalR;

namespace Folkeregister.Web.RealTime
{
    public class EventHub : Hub
    {
        private static Timer _timer = null;
        public EventHub()
        {
            SetupTimer();
        }

        private void SetupTimer()
        {
            if (_timer == null)
            {
                var timer = new Timer(5000);
                var i = 0;
                timer.Elapsed += (s, e) =>
                {
                    Clients.All.Send(i);
                    i++;
                };
                timer.Enabled = true;
            }
        }
    }
}