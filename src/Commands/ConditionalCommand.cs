namespace Statesman.Commands
{
    public class ConditionalCommand : Command
    {
        public const string CommandConditional = "cond";
        private const string OrDelimiter = "||";
        private const string AndDelimiter = "&&";

        public List<string> TargetNames { get; }
        public List<bool> TargetValues { get; private set; }
        public Function Group { get; private set; }
        public Function ElseGroup { get; private set; }

        private readonly bool _orMode;
        private bool? _executeTrueGroup;

        public ConditionalCommand(
            Function group,
            Function elseGroup,
            List<string> targetNames,
            List<bool> targetValues,
            bool orMode)
        {
            Group = group;
            ElseGroup = elseGroup;
            TargetNames = targetNames;
            TargetValues = targetValues;
            _executeTrueGroup = null;
            _orMode = orMode;
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            if (commandName != CommandConditional)
            {
                return null;
            }
            if (arguments.Length >= 2)
            {
                bool? orMode = null;
                List<string> targetNames = new();
                List<bool> targetValues = new();

                // The first argument is the command name, so start evaluating
                // the parts of the conditional with the second argument.
                bool isConditionNext = false;
                bool isConditionPrevious = false;
                for (int i = 1; i < arguments.Length; i++)
                {
                    string operatorOrTargetName = arguments[i].Trim();

                    bool foundOrOperator = operatorOrTargetName.Equals(OrDelimiter);
                    bool foundAndOperator = operatorOrTargetName.Equals(AndDelimiter);

                    if (operatorOrTargetName.Length == 2 && isConditionNext)
                    {
                        if (!foundOrOperator && !foundAndOperator)
                        {
                            throw new GameException("Unknown operator found.");
                        }

                        if (!orMode.HasValue)
                        {
                            orMode = foundOrOperator;
                        }
                        else if ((orMode.Value && foundAndOperator) || (!orMode.Value && foundOrOperator))
                        {
                            throw new GameException("Combining and/or conditional operators are not allowed.");
                        }
                        isConditionNext = false;
                        isConditionPrevious = true;
                        continue;
                    }

                    if (foundOrOperator || foundAndOperator)
                    {
                        throw new GameException("Incorrect condition order.");
                    }

                    // Check for the negation operator.
                    bool targetValue = !operatorOrTargetName.StartsWith("!");
                    if (!targetValue)
                    {
                        operatorOrTargetName = operatorOrTargetName.Substring(1);
                    }
                    targetNames.Add(operatorOrTargetName);
                    targetValues.Add(targetValue);
                    isConditionNext = true;
                    isConditionPrevious = false;
                }

                if (isConditionPrevious)
                {
                    throw new GameException("Found stray operator inside condition.");
                }

                // Assume Or mode if it's a single condition.
                if (!orMode.HasValue)
                {
                    orMode = true;
                }

                return new ConditionalCommand(
                        new Function(""),
                        new Function(""),
                        targetNames,
                        targetValues,
                        orMode.Value);
            }
            return null;
        }

        public override void Execute()
        {
            // Evaluate all conditions.
            for (int i = 0; i < TargetNames.Count; i++)
            {
                bool currentValue = JumpCommand.GetConditionValue(TargetNames[i]);
                bool condition = currentValue == TargetValues[i];
                if (UpdateState(condition))
                {
                    break;
                }
            }
            // Execute the commands inside the condition's scope depending
            // on whether the condition is true or false.
            if (_executeTrueGroup.Value)
            {
                Group.Execute();
            }
            else
            {
                ElseGroup.Execute();
            }
            // Reset the state.
            _executeTrueGroup = null;
        }

        private bool UpdateState(bool condition)
        {
            // The condition is true.
            if (condition)
            {
                // Or mode: requires only one condition to be true.
                if (_orMode)
                {
                    _executeTrueGroup = true;
                    return true;
                }
                // And mode: the first condition is true.
                else if (_executeTrueGroup == null)
                {
                    _executeTrueGroup = true;
                }
            }
            else
            {
                // The condition is false.
                _executeTrueGroup = false;
                // There's no point in checking the other conditions if we're
                // not in or mode.
                return !_orMode;
            }
            return false;
        }
    }
}