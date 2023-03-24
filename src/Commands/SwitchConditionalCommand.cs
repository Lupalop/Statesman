namespace Statesman.Commands
{
    public class SwitchConditionalCommand : ConditionalCommand
    {
        public static readonly string ID = "swcond";

        public int[] SwitchIds { get; }

        public SwitchConditionalCommand()
                : base()
        {
        }

        public SwitchConditionalCommand(
                CommandGroup group,
                CommandGroup elseGroup,
                int[] switchIds,
                bool[] targetValues,
                bool orMode)
                : base()
        {
            _group = group;
            _elseGroup = elseGroup;
            SwitchIds = switchIds;
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
                int[] switchIds = new int[parts.Length];

                for (int i = 0; i < parts.Length; i++)
                {
                    targetValues[i] = !parts[i].StartsWith("!");
                    if (targetValues[i])
                    {
                        switchIds[i] = int.Parse(parts[i]);
                    }
                    else
                    {
                        switchIds[i] = int.Parse(parts[i].Substring(1));
                    }
                }

                return new SwitchConditionalCommand(
                        new CommandGroup(""),
                        new CommandGroup(""),
                        switchIds,
                        targetValues,
                        orMode);
            }

            return null;
        }

        public override void Execute()
        {
            for (int i = 0; i < SwitchIds.Length; i++)
            {
                bool currentState =
                    Interpreter.Switches[SwitchIds[i]] == _targetValues[i];
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
