using Statesman.Commands;
using System;
using System.Collections.Generic;

namespace Statesman
{
    public class Scene
    {
        public static readonly string CG_ENTRY = "$";

        private string _name;
        private Dictionary<string, Command> _actions;
        private Dictionary<string, CommandGroup> _commandGroups;
        private Dictionary<string, InventoryItem> _items;

        public Scene(
                string name,
                Dictionary<string, Command> actions,
                Dictionary<string, CommandGroup> commandGroups,
                Dictionary<string, InventoryItem> items)
        {
            if (string.IsNullOrWhiteSpace(name))
            {
                throw new ArgumentException();
            }
            _name = name;
            _actions = actions;
            _commandGroups = commandGroups;
            _items = items;
        }

        public Scene(string name) : this(
              name,
              new Dictionary<string, Command>(),
              new Dictionary<string, CommandGroup>(),
              new Dictionary<string, InventoryItem>())
        {
        }

        public string getName()
        {
            return _name;
        }

        public Dictionary<string, Command> getActions()
        {
            return _actions;
        }

        public Dictionary<string, CommandGroup> getCommandGroups()
        {
            return _commandGroups;
        }

        public Dictionary<string, InventoryItem> getItems()
        {
            return _items;
        }

        public void runEntry()
        {
            if (_commandGroups.ContainsKey(CG_ENTRY))
            {
                _commandGroups[CG_ENTRY].execute();
            }
        }
    }
}
