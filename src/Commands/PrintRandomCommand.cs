using System;

namespace Statesman.Commands
{
    public class PrintRandomCommand : PrintCombineCommand
    {
        public static readonly string ID = "printr";

        private readonly Random _random;

        public PrintRandomCommand()
            : base()
        {
            _random = new Random();
        }

        public PrintRandomCommand(string[] messages)
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
                return new PrintRandomCommand(messages);
            }
            return null;
        }

        public override void Execute()
        {
            InitializeStrings();
            int i = _random.Next(_messages.Length);
            Console.WriteLine(_messages[i]);
        }
    }
}
