namespace Statesman
{
    internal class Content
    {
        private const string kScriptFilter = "*.gs";

        private static string _dataPath = null;
        private static bool _manualScript = false;
        private static bool _scriptParsed = false;
        private static Script _script = null;

        public static void LoadData()
        {
            // Don't load any data if our game script was set manually.
            if (_manualScript)
            {
                return;
            }
            // Check for existence of data path first before doing anything.
            if (!Directory.Exists(_dataPath))
            {
                throw new GameException("The data directory does not exist.");
            }
            // Retrieve all game scripts from the data directory.
            string[] scriptPaths = Directory.GetFiles(_dataPath, kScriptFilter);
            // Initialize and run the script parser.
            _script = new Script();
            foreach (string path in scriptPaths)
            {
                using FileStream stream = new(path, FileMode.Open);
                ScriptParser parser = new(stream, path, _script);
                parser.Read();
            }
            if (scriptPaths.Length == 0) {
                throw new GameException("No game scripts found.");
            }
            _scriptParsed = true;
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
            var switches = Interpreter.Switches;
            if (switches.Count > 0)
            {
                writer.Write("switches ");
                int count = 0;
                foreach (var keyValuePair in switches)
                {
                    count++;
                    if (keyValuePair.Value)
                    {
                        writer.Write(keyValuePair.Key);
                        if (count != switches.Count)
                        {
                            writer.Write(",");
                        }
                    }
                    if (count == switches.Count)
                    {
                        writer.WriteLine();
                    }
                }
            }
            var inventory = Interpreter.Inventory;
            if (inventory.Count > 0)
            {
                writer.Write("inventory ");
                int count = 0;
                foreach (var item in inventory.Values)
                {
                    count++;
                    writer.Write(item.OwnerScene.Name);
                    writer.Write(";");
                    writer.Write(item.Name);
                    if (count < inventory.Count)
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
            bool switchesFound = false;
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
                    case "switches":
                        if (switchesFound)
                        {
                            throw new GameException("Invalid save file: multiple declarations of `switches`");
                        }
                        string[] switches = lineParts[1].Split(",");
                        for (int j = 0; j < switches.Length; j++)
                        {
                            if (!string.IsNullOrWhiteSpace(switches[j]))
                            {
                                Interpreter.Switches[switches[j]] = true;
                            }
                            // Silently ignore if the switch is invalid.
                        }
                        switchesFound = true;
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
                            // Try to get the inventory item if the scene exists.
                            if (Script.Scenes.TryGetValue(itemParts[0], out Scene targetScene))
                            {
                                string itemName = itemParts[1];
                                InventoryItem item = targetScene.Items[itemName];
                                if (item != null)
                                {
                                    Interpreter.Inventory[itemName] = item;
                                }
                            }
                            // Silently ignore if either the inventory item or scene does not exist .
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
