namespace Statesman.Commands
{
    public class InventoryConditionalCommand : ConditionalCommand
    {
        public static readonly string ID = "invcond";

        public string[] ItemNames { get; }

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
            ItemNames = itemNames;
            _targetValues = targetValues;
            _orMode = orMode;
        }

        public override Command CreateInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                string condition = arguments[1];
                bool orMode = UseOrOperator(condition);
                string[] parts = GetConditionParts(condition, orMode);
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

        public override void Execute()
        {
            for (int i = 0; i < ItemNames.Length; i++)
            {
                bool currentState =
                    Interpreter.Inventory.ContainsKey(ItemNames[i]) == _targetValues[i];
                bool stopLooping = UpdateState(currentState);
                if (stopLooping)
                {
                    break;
                }
            }
            base.Execute();
        }
    }
}
