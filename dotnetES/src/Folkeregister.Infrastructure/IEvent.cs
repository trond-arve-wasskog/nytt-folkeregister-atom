using System;

namespace Folkeregister.Infrastructure
{
    public interface IEvent
    {
        Guid Id { get; }
    }
}