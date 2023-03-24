using System.Text.RegularExpressions;

namespace Statesman.Commands
{
    public class ConditionalCommand : Command
    {
        public const string CommandConditional = "cond";

        public string[] ItemNames { get; }
        public CommandGroup Group { get; private set; }
        public CommandGroup ElseGroup { get; private set; }
        public bool[] TargetValues { get; private set; }

        private bool _orMode;
        private bool? _shouldExecute;

        public ConditionalCommand(
            CommandGroup group,
            CommandGroup elseGroup,
            string[] itemNames,
            bool[] targetValues,
            bool orMode)
        {
            Group = group;
            ElseGroup = elseGroup;
            ItemNames = itemNames;
            TargetValues = targetValues;
            _shouldExecute = null;
            _orMode = orMode;
        }

        public new static Command CreateInstance(string commandName, string[] arguments)
        {
            if (commandName != CommandConditional)
            {
                return null;
            }
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

                return new ConditionalCommand(
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
                    Interpreter.Inventory.ContainsKey(ItemNames[i]) == TargetValues[i];
                bool stopLooping = UpdateState(currentState);
                if (stopLooping)
                {
                    break;
                }
            }

            if (_shouldExecute.Value)
            {
                Group.Execute();
            }
            else
            {
                ElseGroup.Execute();
            }
            _shouldExecute = null;
        }

        private bool UpdateState(bool newState)
        {
            if (newState)
            {
                if (_orMode)
                {
                    _shouldExecute = true;
                    return true;
                }
                if (_shouldExecute == null)
                {
                    _shouldExecute = true;
                }
            }
            else
            {
                _shouldExecute = false;
            }
            return false;
        }

        private static bool UseOrOperator(string condition)
        {
            bool orMode = condition.Contains(';');
            bool andMode = condition.Contains(',');

            if (orMode && andMode)
            {
                throw new InvalidOperationException("Combining and/or conditional operators are not allowed.");
            }

            return orMode;
        }

        private static string[] GetConditionParts(string condition, bool orMode)
        {
            string delimiter = ",";

            if (orMode)
            {
                delimiter = ";";
            }

            return condition.Split(delimiter);
        }
    }
}