using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class LoadCommand : Command
    {
        public static readonly string ID = "load";

        public LoadCommand()
        {
        }

        public override Command createInstance(string[] arguments)
        {
            return new LoadCommand();
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
            bool gameLoaded = false;
            try
            {
                Content.loadState(name);
                gameLoaded = true;
            }
            catch (IOException)
            {
                Console.WriteLine(Content.getScript().getMessage("sl_4"));
            }
            catch (GameException e)
            {
                Console.WriteLine(e.Message);
            }

            if (gameLoaded)
            {
                Console.WriteLine(Content.getScript().getMessage("sl_5"));
            }
        }
    }
}
