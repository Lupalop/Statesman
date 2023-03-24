using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;

namespace Statesman
{
    internal class Content
    {
        private static string _dataPath = null;
        private static bool _manualScript = false;
        private static bool _scriptParsed = false;
        private static Script _script = null;
        private static ScriptParser _parser = null;

        public static void loadData()
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
            List<string> scriptLines = new List<string>();
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
                _script = _parser.read();
                _scriptParsed = true;
            }
            else
            {
                throw new GameException("No game scripts found");
            }
        }

        public static bool tryLoadData()
        {
            try
            {
                loadData();
            }
            catch (Exception)
            {
                if (Program.debugMode)
                {
                    throw;
                }
                return false;
            }
            return true;
        }

        public static Script getScript()
        {
            if (!_manualScript && !_scriptParsed)
            {
                tryLoadData();
            }
            return _script;
        }

        public static void setScript(Script script)
        {
            _manualScript = true;
            _dataPath = null;
            _scriptParsed = false;
            _script = script;
        }

        public static string getDataPath()
        {
            return _dataPath;
        }

        public static void setDataPath(string path)
        {
            _manualScript = false;
            _dataPath = path;
            _scriptParsed = false;
            _script = null;
        }

        public static void saveState(string name)
        {
            string filename = name + ".sav";
            StreamWriter writer = new StreamWriter(filename);

            // current scene, points, inventory, switches
            writer.WriteLine("scene {0}", Interpreter.getScene().getName());
            writer.WriteLine("points {0}", Interpreter.getPoints());
            writer.Write("switches ");
            for (int i = 0; i < Interpreter.getSwitches().Length; i++)
            {
                writer.Write(Interpreter.getSwitches()[i]);
                if (i < Interpreter.getSwitches().Length - 1)
                {
                    writer.Write(",");
                }
                else
                {
                    writer.WriteLine();
                }
            }
            var inventory = Interpreter.getInventory();
            if (inventory.Count > 0)
            {
                writer.Write("inventory ");
                // TODO: change this to use a regular for loop.
                var lastItem = inventory.Last().Value;
                foreach (var item in inventory.Values)
                {
                    writer.Write(item.getOwnerScene().getName());
                    writer.Write(";");
                    writer.Write(item.getName());
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
            writer.Flush();
            writer.Close();
        }

        public static void loadState(string name)
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
                        Scene currentScene = getScript().getScenes()[lineParts[1]];
                        if (currentScene != null)
                        {
                            Interpreter.setScene(currentScene);
                            sceneFound = true;
                        }
                        break;
                    case "points":
                        if (pointsFound)
                        {
                            throw new GameException("Invalid save file: multiple declarations of `points`");
                        }
                        Interpreter.setPoints(int.Parse(lineParts[1]));
                        pointsFound = true;
                        break;
                    case "switches":
                        if (switchesFound)
                        {
                            throw new GameException("Invalid save file: multiple declarations of `switches`");
                        }
                        string[] boolParts = lineParts[1].Split(",");
                        for (int j = 0; j < Interpreter.getSwitches().Length; j++)
                        {
                            Interpreter.getSwitches()[j] = bool.Parse(boolParts[j]);
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
                            // get scene name (first part)
                            Scene targetScene = getScript().getScenes()[itemParts[0]];
                            // get item name (second part)
                            string itemName = itemParts[1];
                            // If scene exists, try to get the inventory item
                            if (targetScene != null)
                            {
                                InventoryItem item = targetScene.getItems()[itemName];
                                if (item != null)
                                {
                                    Interpreter.getInventory().Add(itemName, item);
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
            if (!switchesFound)
            {
                throw new GameException("Invalid save file: missing switches");
            }
        }
    }
}
