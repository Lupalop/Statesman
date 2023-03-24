using Statesman.Commands;
using System;
using System.Collections.Generic;

namespace Statesman
{
    public class Scene
    {
        public static readonly string CommandGroupEntry = "$";

        public string Name { get; }
        public Dictionary<string, Command> Actions { get; }
        public Dictionary<string, CommandGroup> CommandGroups { get; }
        public Dictionary<string, InventoryItem> Items { get; }

        public Scene(
                string name,
                Dictionary<string, Command> actions,
                Dictionary<string, CommandGroup> commandGroups,
                Dictionary<string, InventoryItem> items)
        {
            if (string.IsNullOrWhiteSpace(name))
            {
                throw new ArgumentException("Scene name cannot be empty");
            }
            Name = name;
            Actions = actions;
            CommandGroups = commandGroups;
            Items = items;
        }

        public Scene(string name) : this(
              name,
              new Dictionary<string, Command>(),
              new Dictionary<string, CommandGroup>(),
              new Dictionary<string, InventoryItem>())
        {
        }

        public void RunEntry()
        {
            if (CommandGroups.TryGetValue(CommandGroupEntry, out CommandGroup value))
            {
                value.Execute();
            }
        }
    }
}
