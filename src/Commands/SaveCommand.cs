using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class SaveCommand : Command
    {
        public static readonly string ID = "save";

        public SaveCommand()
        {
        }

        public override Command createInstance(string[] arguments)
        {
            return new SaveCommand();
        }

        public override void execute()
        {
            Console.WriteLine(Content.getScript().getMessage("sl_1"));
            string name = Console.ReadLine().Trim().ToLowerInvariant();
            if (string.IsNullOrWhiteSpace(name))
            {
                Console.WriteLine(Content.getScript().getMessage("sl_2"));
                return;
            }
            if (name.Length > 255)
            {
                Console.WriteLine(Content.getScript().getMessage("sl_3"));
                return;
            }
            bool gameSaved = false;
            try
            {
                Content.saveState(name);
                gameSaved = true;
            }
            catch (IOException e)
            {
                Console.WriteLine(Content.getScript().getMessage("sl_6"));
            }

            if (gameSaved)
            {
                Console.WriteLine(Content.getScript().getMessage("sl_7"));
            }
        }
    }
}
