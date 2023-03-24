using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length > 2)
            {
                string[] messages = new string[arguments.Length - 1];
                Array.Copy(arguments, 1, messages, 0, arguments.Length - 1);
                return new PrintCombineCommand(messages);
            }
            return null;
        }

        public override void execute()
        {
            initializeStrings();
            for (int i = 0; i < _messages.Length; i++)
            {
                Console.Write(_messages[i]);
            }
        }

        protected void initializeStrings()
        {
            if (!_initialized)
            {
                for (int i = 0; i < _messages.Length; i++)
                {
                    bool unescapeString = _messages[i].StartsWith("@");
                    if (Content.getScript().getMessages().TryGetValue(_messages[i], out string messageValue))
                    {
                        _messages[i] = messageValue;
                    }
                    if (unescapeString)
                    {
                        _messages[i] = _messages[i].Replace("\\e", "\033");
                    }
                }
                _initialized = true;
            }
        }
    }
}
