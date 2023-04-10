package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class PointsCommand extends Command {

    public enum PointsActionType {
        ADD, SUBTRACT, SET, CLEAR, LIST
    }

    public static final String ID_POINTS = "points";
    public static final String ID_POINTS_ADD = "add";
    public static final String ID_POINTS_SUB = "sub";
    public static final String ID_POINTS_SET = "set";
    public static final String ID_POINTS_CLEAR = "clear";
    public static final String ID_POINTS_LIST = "list";

    private PointsActionType _action;
    private int _value;

    private PointsCommand() {
        _action = null;
        _value = 0;
    }

    public PointsCommand(PointsActionType action, int value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "Target points value cannot be less than zero");
        }
        if (action == null) {
            throw new IllegalArgumentException(
                    "Invalid value was passed to the action parameter");
        }
        _action = action;
        _value = value;
    }

    public PointsCommand(PointsActionType action) {
        this(action, 0);
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new PointsCommand();
        }
        return _defaultInstance;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length < 2) {
            return null;
        }

        String action = arguments[1].trim();
        PointsActionType actionType = null;
        int actionValue = 0;
        if (arguments.length == 2) {
            if (action.equalsIgnoreCase(ID_POINTS_LIST)) {
                actionType = PointsActionType.LIST;
            } else if (action.equalsIgnoreCase(ID_POINTS_CLEAR)) {
                actionType = PointsActionType.CLEAR;
            }
        } else if (arguments.length == 3) {
            if (action.equalsIgnoreCase(ID_POINTS_ADD)) {
                actionType = PointsActionType.ADD;
            } else if (action.equalsIgnoreCase(ID_POINTS_SUB)) {
                actionType = PointsActionType.SUBTRACT;
            } else if (action.equalsIgnoreCase(ID_POINTS_SET)) {
                actionType = PointsActionType.SET;
            }
            try {
                actionValue = Integer.parseInt(arguments[2]);
            } catch (NumberFormatException ex) {
                // Second argument was not a number.
                return null;
            }
        }

        if (actionType == null) {
            return null;
        }
        return new PointsCommand(actionType, actionValue);
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
            System.out.printf(Content.getScript().getMessage("p_1"),
                    Interpreter.getPoints(),
                    Content.getScript().getMaxPoints());
            break;
        default:
            break;
        }
    }

}
