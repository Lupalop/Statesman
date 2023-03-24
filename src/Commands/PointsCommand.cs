using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class PointsCommand : Command
    {
        public static readonly string ID = "points";

        private PointsAction _action;
        private int _value;

        public enum PointsAction { ADD, SUBTRACT, SET, CLEAR, LIST, NONE };

        public PointsCommand()
        {
            _action = PointsAction.NONE;
            _value = 0;
        }

        public PointsCommand(PointsAction action, int value)
        {
            if (value < 0)
            {
                throw new ArgumentException("Target points value cannot be less than zero");
            }
            if (action == PointsAction.NONE)
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

        public override Command createInstance(string[] arguments)
        {
            string actionString = null;
            if (arguments.Length == 2)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                PointsAction action = PointsAction.NONE;
                switch (actionString)
                {
                    case "list":
                        action = PointsAction.LIST;
                        break;
                    case "clear":
                        action = PointsAction.CLEAR;
                        break;
                    default:
                        action = PointsAction.NONE;
                        break;
                }
                return new PointsCommand(action, 0);
            }
            else if (arguments.Length == 3)
            {
                actionString = arguments[1].Trim().ToLowerInvariant();
                PointsAction action = PointsAction.NONE;
                switch (actionString)
                {
                    case "add":
                        action = PointsAction.ADD;
                        break;
                    case "sub":
                        action = PointsAction.SUBTRACT;
                        break;
                    case "set":
                        action = PointsAction.SET;
                        break;
                    default:
                        action = PointsAction.NONE;
                        break;
                }
                int value = int.Parse(arguments[2]);
                return new PointsCommand(action, value);
            }
            return null;
        }

        public override void execute()
        {
            switch (_action)
            {
                case PointsAction.ADD:
                    Interpreter.setPoints(Interpreter.getPoints() + _value);
                    break;
                case PointsAction.SUBTRACT:
                    Interpreter.setPoints(Interpreter.getPoints() - _value);
                    break;
                case PointsAction.SET:
                    Interpreter.setPoints(_value);
                    break;
                case PointsAction.CLEAR:
                    Interpreter.setPoints(0);
                    break;
                case PointsAction.LIST:
                    Console.Write(
                            Content.getScript().getMessage("p_1"),
                            Interpreter.getPoints(),
                            Content.getScript().getMaxPoints());
                    break;
                default:
                    break;
            }
        }
    }
}
