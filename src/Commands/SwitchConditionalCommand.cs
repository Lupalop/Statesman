using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class SwitchConditionalCommand : ConditionalCommand
    {
        public static readonly string ID = "swcond";

        private int[] _switchIds;

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
            _switchIds = switchIds;
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

        public override void execute()
        {
            for (int i = 0; i < _switchIds.Length; i++)
            {
                bool currentState = false;
                currentState =
                        (Interpreter.getSwitches()[_switchIds[i]] == _targetValues[i]);
                bool stopLooping = updateState(currentState);
                if (stopLooping)
                {
                    break;
                }
            }
            base.execute();
        }

        public int[] getSwitchIds()
        {
            return _switchIds;
        }
    }
}
