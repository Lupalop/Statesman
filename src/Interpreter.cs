using System;
using System.Collections.Generic;
using Statesman.Commands;

namespace Statesman
{
    public class Interpreter
    {
        private static Dictionary<string, Command> _commands;
        private static Scene _scene;
        private static bool[] _switches;
        private static Dictionary<string, InventoryItem> _inventory;
        private static int _points;
        private static bool _isRunning = false;

        static Interpreter()
        {
            _commands = new Dictionary<string, Command>
            {
                // XXX: manually input IDs of new commands here!
                { PrintCommand.ID, new PrintCommand() },
                { PrintRandomCommand.ID, new PrintRandomCommand() },
                { PrintCombineCommand.ID, new PrintCombineCommand() },
                { SceneCommand.ID, new SceneCommand() },
                { GotoCommand.ID, new GotoCommand() },
                { JumpCommand.ID, new JumpCommand() },
                { ReturnCommand.ID, new ReturnCommand() },
                { SwitchSetCommand.ID, new SwitchSetCommand() },
                { SwitchJumpCommand.ID, new SwitchJumpCommand() },
                { SwitchConditionalCommand.ID, new SwitchConditionalCommand() },
                { InventoryCommand.ID, new InventoryCommand() },
                { InventoryJumpCommand.ID, new InventoryJumpCommand() },
                { InventoryConditionalCommand.ID, new InventoryConditionalCommand() },
                { PointsCommand.ID, new PointsCommand() },
                { QuitCommand.ID, new QuitCommand() },
                { SaveCommand.ID, new SaveCommand() },
                { LoadCommand.ID, new LoadCommand() },
                { GotoBaseCommand.ID, new GotoBaseCommand() }
            };
            _scene = null;
        }

        public static Command findCommand(string[] arguments)
        {
            string commandId = arguments[0].ToLowerInvariant();
            Command command = null;
            if (getCommands().TryGetValue(commandId, out command))
            {
                return command.createInstance(arguments);
            }
            return null;
        }

        public static Command findAction(string keyword)
        {
            Command localAction = null;
            if (_scene.getActions().ContainsKey(keyword))
            {
                localAction = _scene.getActions()[keyword];
            }
            Command globalAction = null;
            if (Content.getScript().getActions().ContainsKey(keyword))
            {
                globalAction = Content.getScript().getActions()[keyword];
            }
            Command localFallbackAction = null;
            if (_scene.getActions().ContainsKey("fallback"))
            {
                localFallbackAction = _scene.getActions()["fallback"];
            }
            Command fallbackAction = null;
            if (Content.getScript().getActions().ContainsKey("fallback"))
            {
                fallbackAction = Content.getScript().getActions()["fallback"];
            }

            if (localAction != null)
            {
                return localAction;
            }
            else if (globalAction != null)
            {
                return globalAction;
            }
            else if (localFallbackAction != null)
            {
                return localFallbackAction;
            }
            else if (fallbackAction != null)
            {
                return fallbackAction;
            }
            else if (Program.debugMode)
            {
                Console.WriteLine("Fallback message is missing");
            }

            return null;
        }

        public static void run(string initialSceneName)
        {
            if (_isRunning)
            {
                Console.WriteLine("The interpreter is already running.");
                return;
            }

            if (Content.getScript() == null)
            {
                Console.WriteLine("The game script is missing.");
                return;
            }

            _inventory = new Dictionary<string, InventoryItem>();
            _switches = new bool[Content.getScript().getSwitchSize()];
            Array.Fill(_switches, false);

            if (initialSceneName == null)
            {
                initialSceneName = "initial";
            }
            Scene initialScene = Content.getScript().getScenes()[initialSceneName];
            if (initialScene == null)
            {
                Console.WriteLine("Initial scene is missing.");
                return;
            }
            setScene(initialScene);

            _isRunning = true;

            while (_isRunning)
            {
                Console.Write("> ");
                string keyword = Console.ReadLine().Trim().ToLowerInvariant();

                if (string.IsNullOrWhiteSpace(keyword))
                {
                    continue;
                }

                Console.WriteLine();

                Command currentAction = findAction(keyword);

                // Debug mode keywords
                try
                {
                    if (Program.debugMode)
                    {
                        string[] keywordParts = keyword.Split(" ");
                        if (keyword.StartsWith("*tp"))
                        {
                            currentAction = _commands[SceneCommand.ID].createInstance(keywordParts);
                        }
                        if (keyword.StartsWith("*set"))
                        {
                            currentAction = _commands[SwitchSetCommand.ID].createInstance(keywordParts);
                        }
                        if (keyword.Equals("*reload", StringComparison.InvariantCultureIgnoreCase))
                        {
                            string location = Content.getDataPath();
                            string oldScene = getScene().getName();
                            Program.initialize(location, oldScene);
                        }
                        if (keyword.Equals("*restart", StringComparison.InvariantCultureIgnoreCase))
                        {
                            string location = Content.getDataPath();
                            Program.initialize(location);
                        }
                    }
                }
                catch (Exception)
                {
                    Console.WriteLine("DEBUG: Invalid arguments.");
                }

                if (currentAction != null)
                {
                    currentAction.execute();
                    continue;
                }
            }
        }

        public static void run()
        {
            run(null);
        }

        public static void stop()
        {
            _isRunning = false;
        }

        public static Dictionary<string, Command> getCommands()
        {
            return _commands;
        }

        public static Scene getScene()
        {
            return _scene;
        }

        public static void setScene(Scene scene)
        {
            _scene = scene;
            _scene.runEntry();
        }

        public static bool[] getSwitches()
        {
            return _switches;
        }

        public static Dictionary<string, InventoryItem> getInventory()
        {
            return _inventory;
        }

        public static void setInventory(Dictionary<string, InventoryItem> inventory)
        {
            _inventory = inventory;
        }

        public static int getPoints()
        {
            return _points;
        }

        public static void setPoints(int points)
        {
            _points = points;
        }

        public static bool isRunning()
        {
            return _isRunning;
        }
    }
}
