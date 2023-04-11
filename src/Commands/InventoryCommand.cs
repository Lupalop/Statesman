namespace Statesman.Commands
{
    public class InventoryCommand : Command
    {
        public enum InventoryActionType
        {
            Add,
            Remove,
            Clear,
            List
        }

        public const string kIdInventory = "inv";

        public const string kInventoryAdd = "add";
        public const string kInventoryRemove = "rm";
        public const string kInventoryClear = "clear";
        public const string kInventoryList = "list";

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
        public InventoryActionType ActionType { get; set; }

        private InventoryCommand(InventoryActionType action, string itemName)
        {
            bool nameRequired = action == InventoryActionType.Add
                    || action == InventoryActionType.Remove;
            if (nameRequired && string.IsNullOrWhiteSpace(itemName))
            {
                throw new ArgumentException("Inventory item name cannot be blank");
            }
            ItemName = itemName;
            ActionType = action;
        }

        public static Command FromText(string commandId, string[] arguments)
        {
            if (commandId != kIdInventory || arguments.Length < 2)
            {
                return null;
            }

            string actionString = arguments[1].Trim().ToLowerInvariant();
            InventoryActionType? actionType = null;
            string actionValue = "";
            if (arguments.Length == 2)
            {
                if (actionString.Equals(kInventoryList))
                {
                    actionType = InventoryActionType.List;
                }
                else if (actionString.Equals(kInventoryClear))
                {
                    actionType = InventoryActionType.Clear;
                }
            }
            else if (arguments.Length == 3)
            {
                if (actionString.Equals(kInventoryAdd))
                {
                    actionType = InventoryActionType.Add;
                }
                else if (actionString.Equals(kInventoryRemove))
                {
                    actionType = InventoryActionType.Remove;
                }
                actionValue = arguments[2].Trim();
            }

            if (actionType.HasValue)
            {
                return new InventoryCommand(actionType.Value, actionValue);
            }
            return null;
        }

        public override void Execute()
        {
            switch (ActionType)
            {
                case InventoryActionType.Add:
                    if (Item == null)
                    {
                        return;
                    }
                    if (Interpreter.Inventory.ContainsKey(Item.Name))
                    {
                        Console.WriteLine(Content.Script.FindMessage("i_1"));
                        break;
                    }
                    Interpreter.Inventory.Add(Item.Name, Item);
                    break;
                case InventoryActionType.Remove:
                    if (Item == null)
                    {
                        return;
                    }
                    if (Interpreter.Inventory.Remove(ItemName))
                    {
                        break;
                    }
                    Console.WriteLine(Content.Script.FindMessage("i_2"));
                    break;
                case InventoryActionType.List:
                    var realInventory = Interpreter.Inventory.Values.Where((item) => !item.IsSwitch);
                    int inventorySize = realInventory.Count();
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
                        foreach (var inventoryItem in realInventory)
                        {
                            Console.Write(
                                    Content.Script.FindMessage("i_5"),
                                    inventoryItem.Name,
                                    inventoryItem.Description);
                        }
                        Console.WriteLine();
                    }
                    else
                    {
                        Console.WriteLine(Content.Script.FindMessage("i_6"));
                    }
                    break;
                case InventoryActionType.Clear:
                    Interpreter.Inventory.Clear();
                    break;
                default:
                    break;
            }
        }
    }
}
