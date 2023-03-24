using System;
using System.Reflection;

namespace Statesman.Commands
{
    public class PrintCommand : Command
    {
        public enum PrintType
        {
            Single,
            Concatenate,
            Random
        }

        public const string CommandPrintSingle = "print";
        public const string CommandPrintConcatenate = "printc";
        public const string CommandPrintRandom = "printr";

        protected bool _initialized;
        protected string[] _messages;

        private readonly PrintType _printType;
        private readonly Random _random;

        public PrintCommand(string[] messages, PrintType printType)
        {
            _initialized = false;
            _messages = null;
            _printType = PrintType.Single;
            _messages = messages;
            _printType = printType;
            if (printType == PrintType.Random)
            {
                _random = new Random();
            }
        }

        public new static Command CreateInstance(string commandName, string[] arguments)
        {
            if (arguments.Length < 2)
            {
                return null;
            }

            string[] messages = new string[arguments.Length - 1];
            Array.Copy(arguments, 1, messages, 0, arguments.Length - 1);

            PrintType printType;
            switch (commandName)
            {
                case CommandPrintSingle:
                    printType = PrintType.Single;
                    break;
                case CommandPrintConcatenate:
                    printType = PrintType.Concatenate;
                    break;
                case CommandPrintRandom:
                    printType = PrintType.Random;
                    break;
                default:
                    return null;
            }

            return new PrintCommand(messages, printType);
        }

        public override void Execute()
        {
            InitializeStrings();
            
            if (_printType == PrintType.Concatenate)
            {
                for (int currentIndex = 0;
                     currentIndex < _messages.Length;
                     currentIndex++)
                {
                    Console.Write(_messages[currentIndex]);
                }
                return;
            }

            int index = 0;
            if (_printType == PrintType.Random)
            {
                index = _random.Next(_messages.Length);
            }
            Console.WriteLine(_messages[index]);
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
                        _messages[i] = _messages[i].Replace("\\e", "\u001b");
                    }
                    _messages[i] = _messages[i].Replace("%n", "\n");
                }
                _initialized = true;
            }
        }
    }
}
