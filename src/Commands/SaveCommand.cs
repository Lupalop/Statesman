using System;
using System.IO;

namespace Statesman.Commands
{
    public class SaveCommand : Command
    {
        public static readonly string ID = "save";

        public SaveCommand()
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            return new SaveCommand();
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
            bool gameSaved = false;
            try
            {
                Content.SaveState(name);
                gameSaved = true;
            }
            catch (IOException)
            {
                Console.WriteLine(Content.Script.FindMessage("sl_6"));
            }

            if (gameSaved)
            {
                Console.WriteLine(Content.Script.FindMessage("sl_7"));
            }
        }
    }
}
