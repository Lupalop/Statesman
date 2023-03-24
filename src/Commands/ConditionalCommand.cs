using System;

namespace Statesman.Commands
{
    public abstract class ConditionalCommand : Command
    {
        public static readonly string ID = "cond";

        protected CommandGroup _group;
        protected CommandGroup _elseGroup;
        protected bool _orMode;
        protected bool[] _targetValues;
        protected bool? _shouldExecute;

        public ConditionalCommand()
        {
            _group = null;
            _elseGroup = null;
            _orMode = false;
            _targetValues = null;
            _shouldExecute = null;
        }

        public override Command createInstance(string[] arguments)
        {
            return null;
        }

        public override void execute()
        {
            if (_shouldExecute.Value)
            {
                _group.execute();
            }
            else
            {
                _elseGroup.execute();
            }
            _shouldExecute = null;
        }

        protected bool updateState(bool newState)
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

        protected bool useOrOperator(string condition)
        {
            bool orMode = condition.Contains(';');
            bool andMode = condition.Contains(',');

            if (orMode && andMode)
            {
                throw new InvalidOperationException("Combining and/or conditional operators are not allowed.");
            }

            return orMode;
        }

        protected string[] getConditionParts(string condition, bool orMode)
        {
            string delimiter = ",";

            if (orMode)
            {
                delimiter = ";";
            }

            return condition.Split(delimiter);
        }

        public CommandGroup getGroup()
        {
            return _group;
        }

        public CommandGroup getElseGroup()
        {
            return _elseGroup;
        }

        public bool[] getTargetValues()
        {
            return _targetValues;
        }
    }
}