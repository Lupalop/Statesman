using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class InventoryJumpCommand : JumpCommand
    {
        public static readonly string ID = "ijmp";

        private string _itemName;

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

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 4)
            {
                string itemName = arguments[1];
                int lineIfTrue = getLineNumberFromString(arguments[2]);
                int lineIfFalse = getLineNumberFromString(arguments[3]);
                return new InventoryJumpCommand(itemName, lineIfTrue, lineIfFalse);
            }
            return null;
        }

        public override int getJumpIndex()
        {
            if (Interpreter.getInventory().ContainsKey(_itemName))
            {
                return _lineIfTrue;
            }
            return _lineIfFalse;
        }
    }
}
