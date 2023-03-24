using System;

namespace Statesman
{
    internal class Program
    {
        public static readonly bool DebugMode =
#if DEBUG
            true;
#else
            false;
#endif

        static void Main(string[] args)
        {
            string location = "./scripts";
            if (args.Length == 1)
            {
                location = args[0];
            }

            Initialize(location);
        }

        public static void Initialize(string scriptLocation, string overrideInitialScene = null)
        {
            if (Interpreter.IsRunning)
            {
                Interpreter.Stop();
            }
            Content.DataPath = scriptLocation;
            if (Content.TryLoadData())
            {
                Interpreter.Run(overrideInitialScene);
            }
            else
            {
                Console.WriteLine("An error occurred while loading the game scripts.");
            }
        }
    }
}