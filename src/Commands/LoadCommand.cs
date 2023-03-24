using System;
using System.IO;

namespace Statesman.Commands
{
    public class LoadCommand : Command
    {
        public static readonly string ID = "load";

        public LoadCommand()
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            return new LoadCommand();
        }

        public override void Execute()
        {
            Console.WriteLine(Content.Script.FindMessage("sl_1"));
            string name = Console.ReadLine().Trim().ToLowerInvariant();
            if (string.IsNullOrWhiteSpace(name))
            {
                Console.WriteLine(Content.Script.FindMessage("sl_2"));
                return;
            }
            if (name.Length > 255)
            {
                Console.WriteLine(Content.Script.FindMessage("sl_3"));
                return;
            }
            bool gameLoaded = false;
            try
            {
                Content.LoadState(name);
                gameLoaded = true;
            }
            catch (IOException)
            {
                Console.WriteLine(Content.Script.FindMessage("sl_4"));
            }
            catch (GameException e)
            {
                Console.WriteLine(e.Message);
            }

            if (gameLoaded)
            {
                Console.WriteLine(Content.Script.FindMessage("sl_5"));
            }
        }
    }
}
