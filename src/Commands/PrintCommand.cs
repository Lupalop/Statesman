using System;

namespace Statesman.Commands
{
    public class PrintCommand : Command
    {
        public static readonly string ID = "print";

        private bool _initialized;
        private string _message;

        public PrintCommand()
        {
            _initialized = false;
            _message = null;
        }

        public PrintCommand(string message)
                : this()
        {
            _message = message;
        }

        public override void Execute()
        {
            if (!_initialized)
            {
                bool unescapeString = _message.StartsWith("@");
                if (Content.Script.Messages.TryGetValue(_message, out string messageValue))
                {
                    _message = messageValue;
                }
                if (unescapeString)
                {
                    _message = _message.Replace("\\e", "\033");
                }
                _initialized = true;
            }

            Console.WriteLine(_message);
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                return new PrintCommand(arguments[1]);
            }
            return null;
        }
    }
}
