using System;

namespace Statesman
{
    internal class Program
    {
        public static readonly bool debugMode = true;

        static void Main(string[] args)
        {
            string location = "./scripts";
            if (args.Length == 1)
            {
                location = args[0];
            }

            initialize(location);
        }

        public static void initialize(string scriptLocation, string overrideInitialScene)
        {
            if (Interpreter.isRunning())
            {
                Interpreter.stop();
            }
            Content.setDataPath(scriptLocation);
            if (Content.tryLoadData())
            {
                if (overrideInitialScene == null)
                {
                    Interpreter.run();
                }
                else
                {
                    Interpreter.run(overrideInitialScene);
                }
            }
            else
            {
                Console.WriteLine("An error occurred while loading the game scripts.");
            }
        }

        public static void initialize(string scriptLocation)
        {
            initialize(scriptLocation, null);
        }

    }
}