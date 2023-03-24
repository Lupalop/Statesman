using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class InventoryConditionalCommand : ConditionalCommand
    {
        public static readonly string ID = "invcond";

        private string[] _itemNames;

        public InventoryConditionalCommand()
            : base()
        {
        }

        public InventoryConditionalCommand(
                CommandGroup group,
                CommandGroup elseGroup,
                string[] itemNames,
                bool[] targetValues,
                bool orMode)
            : base()
        {
            _group = group;
            _elseGroup = elseGroup;
            _itemNames = itemNames;
            _targetValues = targetValues;
            _orMode = orMode;
        }

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                string condition = arguments[1];
                bool orMode = useOrOperator(condition);
                string[] parts = getConditionParts(condition, orMode);
                bool[] targetValues = new bool[parts.Length];
                string[] itemNames = new string[parts.Length];

                for (int i = 0; i < parts.Length; i++)
                {
                    targetValues[i] = !parts[i].StartsWith("!");
                    if (targetValues[i])
                    {
                        itemNames[i] = parts[i];
                    }
                    else
                    {
                        itemNames[i] = parts[i].Substring(1);
                    }
                }

                return new InventoryConditionalCommand(
                        new CommandGroup(""),
                        new CommandGroup(""),
                        itemNames,
                        targetValues,
                        orMode);
            }

            return null;
        }

        public override void execute()
        {
            for (int i = 0; i < _itemNames.Length; i++)
            {
                bool currentState = false;
                currentState =
                        (Interpreter.getInventory().ContainsKey(_itemNames[i]) == _targetValues[i]);
                bool stopLooping = updateState(currentState);
                if (stopLooping)
                {
                    break;
                }
            }
            base.execute();
        }

        public string[] getItemNames()
        {
            return _itemNames;
        }
    }
}
