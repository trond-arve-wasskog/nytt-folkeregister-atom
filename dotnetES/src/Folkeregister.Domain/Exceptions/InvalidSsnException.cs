using System;
using Folkeregister.Contracts.Types;

namespace Folkeregister.Domain.Exceptions
{
    public class InvalidSsnException : Exception
    {
        public InvalidSsnException(SSN ssn)
        {
            
        }
    }
}