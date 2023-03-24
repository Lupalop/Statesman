using System;

namespace Statesman.Commands
{
    public class InventoryCommand : Command
    {
        public static readonly string ID = "inv";

        private string _itemName;
        private InventoryAction _action;

        public enum InventoryAction { ADD, REMOVE, CLEAR, LIST, NONE };

        public InventoryCommand()
        {
            _itemName = null;
            _action = InventoryAction.NONE;
        }

        private InventoryCommand(InventoryAction action, string itemName, bool nameRequired)
        {
            if (nameRequired && string.IsNullOrWhiteSpace(itemName))
            {
                throw new ArgumentException("Inventory item name cannot be blank");
            }
            if (action == InventoryAction.NONE)
            {
                throw new ArgumentException("Invalid value was passed to the action parameter");
            }
            _itemName = itemName;
            _action = action;
        }

        public InventoryCommand(InventoryAction action, string itemName)
            : this(action, itemName, true)
        {
        }

        public InventoryCommand(InventoryAction action)
            : this(action, null, false)
        {
        }

        public override Command createInstance(string[] arguments)
        {
            string actionString = null;
            InventoryAction action = InventoryAction.NONE;
            if (arguments.Length == 2)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                switch (actionString)
                {
                    case "list":
                        action = InventoryAction.LIST;
                        break;
                    case "clear":
                        action = InventoryAction.CLEAR;
                        break;
                    default:
                        action = InventoryAction.NONE;
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
                        action = InventoryAction.ADD;
                        break;
                    case "rm":
                        action = InventoryAction.REMOVE;
                        break;
                    default:
                        action = InventoryAction.NONE;
                        break;
                }
                string itemName = arguments[2].Trim();
                return new InventoryCommand(action, itemName);
            }
            return null;
        }

        public override void execute()
        {
            InventoryItem item = getItem();
            if (item != null)
            {
                switch (_action)
                {
                    case InventoryAction.ADD:
                        if (Interpreter.getInventory().ContainsKey(item.getName()))
                        {
                            Console.WriteLine(Content.getScript().getMessage("i_1"));
                            break;
                        }
                        Interpreter.getInventory().Add(item.getName(), item);
                        break;
                    case InventoryAction.REMOVE:
                        if (Interpreter.getInventory().ContainsKey(item.getName()))
                        {
                            Interpreter.getInventory().Remove(_itemName);
                            break;
                        }
                        Console.WriteLine(Content.getScript().getMessage("i_2"));
                        break;
                    default:
                        break;
                }
            }
            else
            {
                switch (_action)
                {
                    case InventoryAction.LIST:
                        int inventorySize = Interpreter.getInventory().Count;
                        if (inventorySize > 0)
                        {
                            if (inventorySize == 1)
                            {
                                Console.WriteLine(Content.getScript().getMessage("i_3"));
                            }
                            else
                            {
                                Console.Write(Content.getScript().getMessage("i_4"), inventorySize);
                            }
                            foreach (var inventoryItem in Interpreter.getInventory().Values)
                            {
                                Console.Write(
                                        Content.getScript().getMessage("i_5"),
                                        inventoryItem.getName(),
                                        inventoryItem.getDescription());
                            }
                        }
                        else
                        {
                            Console.WriteLine(Content.getScript().getMessage("i_6"));
                        }
                        break;
                    case InventoryAction.CLEAR:
                        Interpreter.getInventory().Clear();
                        break;
                    default:
                        break;
                }
            }
        }

        public string getItemName()
        {
            return _itemName;
        }

        private InventoryItem getItem()
        {
            if (_itemName == null)
            {
                return null;
            }
            return Interpreter.getScene().getItems()[_itemName];
        }
    }
}
