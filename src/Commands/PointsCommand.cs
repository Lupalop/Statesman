namespace Statesman.Commands
{
    public class PointsCommand : Command
    {
        public static readonly string ID = "points";

        private readonly PointsAction _action;
        private readonly int _value;

        public enum PointsAction { Add, Subtract, Set, Clear, List, None };

        public PointsCommand()
        {
            _action = PointsAction.None;
            _value = 0;
        }

        public PointsCommand(PointsAction action, int value)
        {
            if (value < 0)
            {
                throw new ArgumentException("Target points value cannot be less than zero");
            }
            if (action == PointsAction.None)
            {
                throw new ArgumentException("Invalid value was passed to the action parameter");
            }
            _action = action;
            _value = value;
        }

        public PointsCommand(PointsAction action)
                : this(action, 0)
        {
        }

        public override Command CreateInstance(string[] arguments)
        {
            string actionString;
            if (arguments.Length == 2)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                PointsAction action;
                switch (actionString)
                {
                    case "list":
                        action = PointsAction.List;
                        break;
                    case "clear":
                        action = PointsAction.Clear;
                        break;
                    default:
                        action = PointsAction.None;
                        break;
                }
                return new PointsCommand(action, 0);
            }
            else if (arguments.Length == 3)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                PointsAction action;
                switch (actionString)
                {
                    case "add":
                        action = PointsAction.Add;
                        break;
                    case "sub":
                        action = PointsAction.Subtract;
                        break;
                    case "set":
                        action = PointsAction.Set;
                        break;
                    default:
                        action = PointsAction.None;
                        break;
                }
                int value = int.Parse(arguments[2]);
                return new PointsCommand(action, value);
            }
            return null;
        }

        public override void Execute()
        {
            switch (_action)
            {
                case PointsAction.Add:
                    Interpreter.Points += _value;
                    break;
                case PointsAction.Subtract:
                    Interpreter.Points -= _value;
                    break;
                case PointsAction.Set:
                    Interpreter.Points = _value;
                    break;
                case PointsAction.Clear:
                    Interpreter.Points = 0;
                    break;
                case PointsAction.List:
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
