package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class PointsCommand extends Command {

    public static final String ID = "points";

    private PointsAction _action;
    private int _value;

    public enum PointsAction { ADD, SUBTRACT, SET, CLEAR, LIST };
    
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
                action = PointsAction.LIST;
                break;
            case "clear":
                action = PointsAction.CLEAR;
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
                action = PointsAction.ADD;
                break;
            case "sub":
                action = PointsAction.SUBTRACT;
                break;
            case "set":
                action = PointsAction.SET;
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
        case ADD:
            Interpreter.setPoints(Interpreter.getPoints() + _value);
            break;
        case SUBTRACT:
            Interpreter.setPoints(Interpreter.getPoints() - _value);
            break;
        case SET:
            Interpreter.setPoints(_value);
            break;
        case CLEAR:
            Interpreter.setPoints(0);
            break;
        case LIST:
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
