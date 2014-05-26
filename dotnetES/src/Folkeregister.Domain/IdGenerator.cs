using System;

namespace Folkeregister.Domain
{
    public static class IdGenerator
    {
        private static Func<Guid> _guidGenerator;

        public static Func<Guid> GuidGenerator
        {
            get
            {
                _guidGenerator = _guidGenerator ?? (Guid.NewGuid);
                return _guidGenerator;
            }
            set
            {
                _guidGenerator = value;
            }
        }

        public static Guid GetId()
        {
            return GuidGenerator();
        }
    }
}