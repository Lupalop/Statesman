﻿using System.ComponentModel.Design;

namespace Statesman.Commands
{
    public class MenuCommand : Command
    {
        public enum MenuActionType
        {
            None,
            Save,
            Load,
            Quit
        }

        public const string CommandSave = "save";
        public const string CommandLoad = "load";
        public const string CommandQuit = "quit";

        private readonly MenuActionType _actionType;

        public MenuCommand(MenuActionType action)
        {
            _actionType = action;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            if (arguments.Length == 1)
            {
                MenuActionType? actionType = null;
                if (commandName.Equals(CommandSave, StringComparison.InvariantCultureIgnoreCase))
                {
                    actionType = MenuActionType.Save;
                }
                else if (commandName.Equals(CommandLoad, StringComparison.InvariantCultureIgnoreCase))
                {
                    actionType = MenuActionType.Load;
                }
                else if (commandName.Equals(CommandQuit, StringComparison.InvariantCultureIgnoreCase))
                {
                    actionType = MenuActionType.Quit;
                }

                if (actionType.HasValue)
                {
                    return new MenuCommand(actionType.Value);
                }
            }
            return null;
        }

        public override void Execute()
        {
            if (_actionType == MenuActionType.Quit)
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

            if (_actionType == MenuActionType.Save)
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
            else if (_actionType == MenuActionType.Load)
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
