using System;

namespace Statesman.Commands
{
    public class PrintCombineCommand : Command
    {
        public static readonly string ID = "printc";

        protected bool _initialized;
        protected string[] _messages;

        public PrintCombineCommand()
        {
            _initialized = false;
            _messages = null;
        }

        public PrintCombineCommand(string[] messages)
                : this()
        {
            _messages = messages;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length > 2)
            {
                string[] messages = new string[arguments.Length - 1];
                Array.Copy(arguments, 1, messages, 0, arguments.Length - 1);
                return new PrintCombineCommand(messages);
            }
            return null;
        }

        public override void Execute()
        {
            InitializeStrings();
            for (int i = 0; i < _messages.Length; i++)
            {
                Console.Write(_messages[i]);
            }
        }

        protected void InitializeStrings()
        {
            if (!_initialized)
            {
                for (int i = 0; i < _messages.Length; i++)
                {
                    bool unescapeString = _messages[i].StartsWith("@");
                    if (Content.Script.Messages.TryGetValue(_messages[i], out string messageValue))
                    {
                        _messages[i] = messageValue;
                    }
                    if (unescapeString)
                    {
                        _messages[i] = _messages[i].Replace("\\e", "\033");
                    }                }
                _initialized = true;
            }
        }
    }
}
