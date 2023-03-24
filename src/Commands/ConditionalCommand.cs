using System;

namespace Statesman.Commands
{
    public abstract class ConditionalCommand : Command
    {
        public static readonly string ID = "cond";

        protected CommandGroup _group;
        public CommandGroup Group => _group;

        protected CommandGroup _elseGroup;
        public CommandGroup ElseGroup => _elseGroup;

        protected bool[] _targetValues;
        public bool[] TargetValues => _targetValues;

        protected bool _orMode;
        protected bool? _shouldExecute;

        public ConditionalCommand()
        {
            _group = null;
            _elseGroup = null;
            _orMode = false;
            _targetValues = null;
            _shouldExecute = null;
        }

        public override void Execute()
        {
            if (_shouldExecute.Value)
            {
                _group.Execute();
            }
            else
            {
                _elseGroup.Execute();
            }
            _shouldExecute = null;
        }

        protected bool UpdateState(bool newState)
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

        protected static bool UseOrOperator(string condition)
        {
            bool orMode = condition.Contains(';');
            bool andMode = condition.Contains(',');

            if (orMode && andMode)
            {
                throw new InvalidOperationException("Combining and/or conditional operators are not allowed.");
            }

            return orMode;
        }

        protected static string[] GetConditionParts(string condition, bool orMode)
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