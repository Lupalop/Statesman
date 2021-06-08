package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class PointsCommand implements Command {

    public static final String Id = "points";

    private PointsAction _action;
    private int _value;

    public enum PointsAction { Add, Subtract, Set, Clear, List };
    
    public PointsCommand() {
        _action = null;
        _value = 0;
    }
    
    public PointsCommand(PointsAction action, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Target points value cannot be less than zero");
        }
        if (action == null) {
            throw new IllegalArgumentException("Invalid value was passed to the action parameter");
        }
        _action = action;
        _value = value;
    }
    
    public PointsCommand(PointsAction action) {
        this(action, 0);
    }

    @Override
    public Command createInstance(String[] arguments) {
        String actionString = null;
        if (arguments.length == 2) {
            actionString = arguments[1].trim().toLowerCase();
            PointsAction action = null;
            switch (actionString) {
            case "list":
                action = PointsAction.List;
                break;
            case "clear":
                action = PointsAction.Clear;
                break;
            default:
                action = null;
                break;
            }
            return new PointsCommand(action, 0);
        } else if (arguments.length == 3) {
            actionString = arguments[1].trim().toLowerCase();
            PointsAction action = null;
            switch (actionString) {
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
                action = null;
                break;
            }
            int value = Integer.parseInt(arguments[2]);
            return new PointsCommand(action, value);
        }
        return null;
    }

    @Override
    public void execute() {
        switch (_action) {
        case Add:
            Interpreter.setPoints(Interpreter.getPoints() + _value);
            break;
        case Subtract:
            Interpreter.setPoints(Interpreter.getPoints() - _value);
            break;
        case Set:
            Interpreter.setPoints(_value);
            break;
        case Clear:
            Interpreter.setPoints(0);
            break;
        case List:
            System.out.printf(
                    Content.getScript().getMessage("p_1"),
                    Interpreter.getPoints(),
                    Content.getScript().getMaxPoints());
            break;
        default:
            break;
        }
    }

}
