using System;
using Folkeregister.Contracts.Types;

namespace Folkeregister.Domain.Exceptions
{
    public class InvalidNameException : Exception
    {
        public InvalidNameException(Name name)
        {
        }
    }
}