using System.Runtime.Serialization;

namespace Statesman
{
    [Serializable]
    public class GameException : Exception
    {
        public GameException() { }
        public GameException(string message)
            : base(message) { }
        public GameException(string message, Exception inner)
            : base(message, inner) { }
        protected GameException(SerializationInfo info, StreamingContext context)
            : base(info, context) { }
    }
}
