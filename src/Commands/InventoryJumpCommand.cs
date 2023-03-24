namespace Statesman.Commands
{
    public class InventoryJumpCommand : JumpCommand
    {
        public static readonly string ID = "ijmp";

        private readonly string _itemName;

        public InventoryJumpCommand()
            : base()
        {
            _itemName = "";
        }

        public InventoryJumpCommand(string itemName, int lineIfTrue, int lineIfFalse)
        {
            _itemName = itemName;
            _lineIfTrue = lineIfTrue;
            _lineIfFalse = lineIfFalse;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 4)
            {
                string itemName = arguments[1];
                int lineIfTrue = GetLineNumberFromString(arguments[2]);
                int lineIfFalse = GetLineNumberFromString(arguments[3]);
                return new InventoryJumpCommand(itemName, lineIfTrue, lineIfFalse);
            }
            return null;
        }

        public override int GetJumpIndex()
        {
            if (Interpreter.Inventory.ContainsKey(_itemName))
            {
                return _lineIfTrue;
            }
            return _lineIfFalse;
        }
    }
}
