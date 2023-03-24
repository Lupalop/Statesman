namespace Statesman
{
    internal class Content
    {
        private static string _dataPath = null;
        private static bool _manualScript = false;
        private static bool _scriptParsed = false;
        private static Script _script = null;
        private static ScriptParser _parser = null;

        public static void LoadData()
        {
            // Refuse to parse data if the script was set manually
            if (_manualScript)
            {
                return;
            }
            // Check for existence of the data path
            if (!Directory.Exists(_dataPath))
            {
                throw new GameException("The data directory does not exist");
            }
            // Read all game scripts from the given directory 
            string filter = "*.gs";
            IEnumerable<string> filePaths = Directory.EnumerateFiles(_dataPath, filter);
            // Merged game script collection
            int scriptCount = 0;
            List<string> scriptLines = new();
            foreach (var scriptPath in filePaths)
            {
                string[] scriptData = File.ReadAllLines(scriptPath);
                scriptLines.AddRange(scriptData);
                scriptCount++;
            }
            // Initialize and run the script parser
            if (scriptCount > 0)
            {
                _parser = new ScriptParser(scriptLines);
                _script = _parser.Read();
                _scriptParsed = true;
            }
            else
            {
                throw new GameException("No game scripts found");
            }
        }

        public static bool TryLoadData()
        {
            try
            {
                LoadData();
            }
            catch (Exception)
            {
                if (Program.DebugMode)
                {
                    throw;
                }
                return false;
            }
            return true;
        }

        public static Script Script
        {
            get
            {
                if (!_manualScript && !_scriptParsed)
                {
                    TryLoadData();
                }
                return _script;
            }

            set
            {
                _manualScript = true;
                _dataPath = null;
                _scriptParsed = false;
                _script = value;
            }
        }

        public static string DataPath
        {
            get => _dataPath;
            set
            {
                _manualScript = false;
                _dataPath = value;
                _scriptParsed = false;
                _script = null;
            }
        }

        public static void SaveState(string name)
        {
            string filename = name + ".sav";
            using StreamWriter writer = new(filename);

            // current scene, points, inventory, switches
            writer.WriteLine("scene {0}", Interpreter.Scene.Name);
            writer.WriteLine("points {0}", Interpreter.Points);
            var inventory = Interpreter.Inventory;
            if (inventory.Count > 0)
            {
                writer.Write("inventory ");
                // TODO: change this to use a regular for loop.
                var lastItem = inventory.Last().Value;
                foreach (var item in inventory.Values)
                {
                    if (item.OwnerScene != null)
                    {
                        writer.Write(item.OwnerScene.Name);
                        writer.Write(";");
                    }
                    writer.Write(item.Name);
                    if (item != lastItem)
                    {
                        writer.Write(",");
                    }
                    else
                    {
                        writer.WriteLine();
                    }
                }
            }
        }

        public static void LoadState(string name)
        {
            string savePath = name + ".sav";
            string[] data = File.ReadAllLines(savePath);

            bool sceneFound = false;
            bool pointsFound = false;
            bool inventoryFound = false;

            for (int i = 0; i < data.Length; i++)
            {
                string line = data[i];
                string[] lineParts = line.Split(" ");

                switch (lineParts[0])
                {
                    case "scene":
                        if (sceneFound)
                        {
                            throw new GameException("Invalid save file: multiple declarations of `scene`");
                        }
                        // Try to get the named scene if it exists
                        Scene currentScene = Script.Scenes[lineParts[1]];
                        if (currentScene != null)
                        {
                            Interpreter.Scene = currentScene;
                            sceneFound = true;
                        }
                        break;
                    case "points":
                        if (pointsFound)
                        {
                            throw new GameException("Invalid save file: multiple declarations of `points`");
                        }
                        Interpreter.Points = int.Parse(lineParts[1]);
                        pointsFound = true;
                        break;
                    case "inventory":
                        if (inventoryFound)
                        {
                            throw new GameException("Invalid save file: multiple declarations of `inventory`");
                        }
                        string[] items = lineParts[1].Split(",");
                        for (int j = 0; j < items.Length; j++)
                        {
                            string[] itemParts = items[j].Split(";");
                            // We have a switch disguised as an inventory item.
                            if (itemParts.Length == 1)
                            {
                                string switchName = itemParts[0];
                                Interpreter.Inventory[switchName] = new InventoryItem(switchName);
                            }
                            else if (itemParts.Length == 2 &&
                                     Script.Scenes.TryGetValue(itemParts[0], out Scene targetScene))
                            {
                                string itemName = itemParts[1];
                                // If the scene exists, try to get the inventory item.
                                InventoryItem item = targetScene.Items[itemName];
                                if (item != null)
                                {
                                    Interpreter.Inventory[itemName] = item;
                                }
                            }

                            // Silently ignore if either the inventory item or scene does not exist 
                        }
                        inventoryFound = true;
                        break;
                    default:
                        break;
                }
            }

            if (!sceneFound)
            {
                throw new GameException("Invalid save file: missing target scene");
            }
            if (!pointsFound)
            {
                throw new GameException("Invalid save file: missing points");
            }
        }
    }
}
