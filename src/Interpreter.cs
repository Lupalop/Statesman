using Statesman.Commands;
using System;
using System.Collections.Generic;

namespace Statesman
{
    public class Interpreter
    {
        private static Scene _scene;
        public static Scene Scene
        {
            get => _scene;
            set
            {
                _scene = value;
                _scene.RunEntry();
            }
        }

        public static Dictionary<string, Command> Commands { get; private set; }
        public static bool[] Switches { get; private set; }
        public static Dictionary<string, InventoryItem> Inventory { get; set; }
        public static int Points { get; set; }
        public static bool IsRunning { get; private set; } = false;

        static Interpreter()
        {
            Commands = new Dictionary<string, Command>
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

        public static Command FindCommand(string[] arguments)
        {
            string commandId = arguments[0].ToLowerInvariant();
            if (Commands.TryGetValue(commandId, out Command command))
            {
                return command.CreateInstance(arguments);
            }
            return null;
        }

        public static Command FindAction(string keyword)
        {
            if (_scene.Actions.TryGetValue(keyword, out Command value))
            {
                return value;
            }
            else if (Content.Script.Actions.TryGetValue(keyword, out value))
            {
                return value;
            }
            else if (_scene.Actions.TryGetValue("fallback", out value))
            {
                return value;
            }
            else if (Content.Script.Actions.TryGetValue("fallback", out value))
            {
                return value;
            }
            else if (Program.DebugMode)
            {
                Console.WriteLine("Fallback message is missing");
            }
            return null;
        }

        public static void Run(string initialSceneName = null)
        {
            if (IsRunning)
            {
                Console.WriteLine("The interpreter is already running.");
                return;
            }

            if (Content.Script == null)
            {
                Console.WriteLine("The game script is missing.");
                return;
            }

            Inventory = new Dictionary<string, InventoryItem>();
            Switches = new bool[Content.Script.SwitchSize];
            Array.Fill(Switches, false);

            if (initialSceneName == null)
            {
                initialSceneName = "initial";
            }
            Scene initialScene = Content.Script.Scenes[initialSceneName];
            if (initialScene == null)
            {
                Console.WriteLine("Initial scene is missing.");
                return;
            }
            Scene = initialScene;

            IsRunning = true;

            while (IsRunning)
            {
                Console.Write("> ");
                string keyword = Console.ReadLine().Trim().ToLowerInvariant();

                if (string.IsNullOrWhiteSpace(keyword))
                {
                    continue;
                }

                Console.WriteLine();

                Command currentAction = FindAction(keyword);

                // Debug mode keywords
                try
                {
                    if (Program.DebugMode)
                    {
                        string[] keywordParts = keyword.Split(" ");
                        if (keyword.StartsWith("*tp"))
                        {
                            currentAction = Commands[SceneCommand.ID].CreateInstance(keywordParts);
                        }
                        if (keyword.StartsWith("*set"))
                        {
                            currentAction = Commands[SwitchSetCommand.ID].CreateInstance(keywordParts);
                        }
                        if (keyword.Equals("*reload", StringComparison.InvariantCultureIgnoreCase))
                        {
                            string location = Content.DataPath;
                            string oldScene = Scene.Name;
                            Program.Initialize(location, oldScene);
                        }
                        if (keyword.Equals("*restart", StringComparison.InvariantCultureIgnoreCase))
                        {
                            string location = Content.DataPath;
                            Program.Initialize(location);
                        }
                    }
                }
                catch (Exception)
                {
                    Console.WriteLine("DEBUG: Invalid arguments.");
                }

                if (currentAction != null)
                {
                    currentAction.Execute();
                    continue;
                }
            }
        }

        public static void Stop()
        {
            IsRunning = false;
        }
    }
}
