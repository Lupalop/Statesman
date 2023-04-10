namespace Statesman.Commands
{
    public class PointsCommand : Command
    {
        public enum PointsActionType
        {
            Add,
            Subtract,
            Set,
            Clear,
            List
        }

        public const string CommandPoints = "points";

        public const string kPointsAdd = "add";
        public const string kPointsSubtract = "sub";
        public const string kPointsSet = "set";
        public const string kPointsClear = "clear";
        public const string kPointsList = "list";

        private readonly PointsActionType _actionType;
        private readonly int _value;

        public PointsCommand(PointsActionType action, int value)
        {
            if (value < 0)
            {
                throw new ArgumentException("Target points value cannot be less than zero");
            }
            _actionType = action;
            _value = value;
        }

        public PointsCommand(PointsActionType action)
                : this(action, 0)
        {
        }

        public new static Command FromText(string commandName, string[] arguments)
        {
            if (commandName != CommandPoints || arguments.Length < 2)
            {
                return null;
            }

            string actionString = arguments[1].Trim().ToLowerInvariant();
            PointsActionType? actionType = null;
            int actionValue = 0;
            if (arguments.Length == 2)
            {
                if (actionString.Equals(kPointsList))
                {
                    actionType = PointsActionType.List;
                }
                else if (actionString.Equals(kPointsClear))
                {
                    actionType = PointsActionType.Clear;
                }
            }
            else if (arguments.Length == 3)
            {
                if (actionString.Equals(kPointsAdd))
                {
                    actionType = PointsActionType.Add;
                }
                else if (actionString.Equals(kPointsSubtract))
                {
                    actionType = PointsActionType.Subtract;
                }
                else if (actionString.Equals(kPointsSet))
                {
                    actionType = PointsActionType.Set;
                }
                
                if (!int.TryParse(arguments[2], out actionValue))
                {
                    return null;
                }
            }

            if (!actionType.HasValue)
            {
                return null;
            }
            return new PointsCommand(actionType.Value, actionValue);
        }

        public override void Execute()
        {
            switch (_actionType)
            {
                case PointsActionType.Add:
                    Interpreter.Points += _value;
                    break;
                case PointsActionType.Subtract:
                    Interpreter.Points -= _value;
                    break;
                case PointsActionType.Set:
                    Interpreter.Points = _value;
                    break;
                case PointsActionType.Clear:
                    Interpreter.Points = 0;
                    break;
                case PointsActionType.List:
                    Console.Write(
                            Content.Script.FindMessage("p_1"),
                            Interpreter.Points,
                            Content.Script.MaxPoints);
                    break;
                default:
                    break;
            }
        }
    }
}
