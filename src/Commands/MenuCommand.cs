namespace Statesman.Commands
{
    public class MenuCommand : Command
    {
        public const string CommandSave = "save";
        public const string CommandLoad = "load";
        public const string CommandQuit = "quit";

        private readonly string _actionName;

        public MenuCommand(string actionName)
        {
            if (string.IsNullOrWhiteSpace(actionName))
            {
                throw new ArgumentNullException(nameof(actionName));
            }

            _actionName = actionName;
        }

        public new static Command FromText(string actionName, string[] arguments)
        {
            if (actionName != CommandSave && actionName != CommandLoad && actionName != CommandQuit)
            {
                return null;
            }
            if (arguments.Length == 1)
            {
                return new MenuCommand(actionName);
            }
            return null;
        }

        public override void Execute()
        {
            if (_actionName == CommandQuit)
            {
                Environment.Exit(0);
                return;
            }

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

            if (_actionName == CommandSave)
            {
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
            else if (_actionName == CommandLoad)
            {
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
}
