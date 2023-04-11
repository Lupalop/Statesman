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

        public const string kIdPrintSingle = "print";
        public const string kIdPrintConcatenate = "printc";
        public const string kIdPrintRandom = "printr";

        protected bool _initialized;
        protected string[] _messages;

        private readonly PrintType _printType;
        private readonly Random _random;

        public PrintCommand(string[] messages, PrintType printType)
        {
            _initialized = false;
            _messages = null;
            _messages = messages;
            _printType = printType;
            if (printType == PrintType.Random)
            {
                _random = new Random();
            }
        }

        public static Command FromText(string commandId, string[] arguments)
        {
            if (arguments.Length < 2)
            {
                return null;
            }

            string[] messages = new string[arguments.Length - 1];
            Array.Copy(arguments, 1, messages, 0, arguments.Length - 1);

            PrintType printType;
            switch (commandId)
            {
                case kIdPrintSingle:
                    printType = PrintType.Single;
                    break;
                case kIdPrintConcatenate:
                    printType = PrintType.Concatenate;
                    break;
                case kIdPrintRandom:
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
                    _messages[i] = Content.Script.FindMessage(_messages[i], false);
                }
                _initialized = true;
            }
        }
    }
}
