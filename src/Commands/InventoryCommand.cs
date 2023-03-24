using System;

namespace Statesman.Commands
{
    public class InventoryCommand : Command
    {
        public static readonly string ID = "inv";

        public string ItemName { get; }
        private InventoryItem Item
        {
            get
            {
                if (ItemName == null)
                {
                    return null;
                }
                return Interpreter.Scene.Items[ItemName];
            }
        }
        public InventoryAction Action { get; set; }

        public enum InventoryAction { Add, Remove, Clear, List, None };

        public InventoryCommand()
        {
            ItemName = null;
            Action = InventoryAction.None;
        }

        private InventoryCommand(InventoryAction action, string itemName, bool nameRequired)
        {
            if (nameRequired && string.IsNullOrWhiteSpace(itemName))
            {
                throw new ArgumentException("Inventory item name cannot be blank");
            }
            if (action == InventoryAction.None)
            {
                throw new ArgumentException("Invalid value was passed to the action parameter");
            }
            ItemName = itemName;
            Action = action;
        }

        public InventoryCommand(InventoryAction action, string itemName)
            : this(action, itemName, true)
        {
        }

        public InventoryCommand(InventoryAction action)
            : this(action, null, false)
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            string actionString;
            InventoryAction action;
            if (arguments.Length == 2)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                switch (actionString)
                {
                    case "list":
                        action = InventoryAction.List;
                        break;
                    case "clear":
                        action = InventoryAction.Clear;
                        break;
                    default:
                        action = InventoryAction.None;
                        break;
                }
                return new InventoryCommand(action);
            }
            else if (arguments.Length == 3)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                switch (actionString)
                {
                    case "add":
                        action = InventoryAction.Add;
                        break;
                    case "rm":
                        action = InventoryAction.Remove;
                        break;
                    default:
                        action = InventoryAction.None;
                        break;
                }
                string itemName = arguments[2].Trim();
                return new InventoryCommand(action, itemName);
            }
            return null;
        }

        public override void Execute()
        {
            InventoryItem item = Item;
            if (item != null)
            {
                switch (Action)
                {
                    case InventoryAction.Add:
                        if (Interpreter.Inventory.ContainsKey(item.Name))
                        {
                            Console.WriteLine(Content.Script.FindMessage("i_1"));
                            break;
                        }
                        Interpreter.Inventory.Add(item.Name, item);
                        break;
                    case InventoryAction.Remove:
                        if (Interpreter.Inventory.Remove(ItemName))
                        {
                            break;
                        }
                        Console.WriteLine(Content.Script.FindMessage("i_2"));
                        break;
                    default:
                        break;
                }
                return;
            }

            switch (Action)
            {
                case InventoryAction.List:
                    int inventorySize = Interpreter.Inventory.Count;
                    if (inventorySize > 0)
                    {
                        if (inventorySize == 1)
                        {
                            Console.WriteLine(Content.Script.FindMessage("i_3"));
                        }
                        else
                        {
                            Console.Write(Content.Script.FindMessage("i_4"), inventorySize);
                        }
                        foreach (var inventoryItem in Interpreter.Inventory.Values)
                        {
                            Console.Write(
                                    Content.Script.FindMessage("i_5"),
                                    inventoryItem.Name,
                                    inventoryItem.Description);
                        }
                    }
                    else
                    {
                        Console.WriteLine(Content.Script.FindMessage("i_6"));
                    }
                    break;
                case InventoryAction.Clear:
                    Interpreter.Inventory.Clear();
                    break;
                default:
                    break;
            }
        }
    }
}
