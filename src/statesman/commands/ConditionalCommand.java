package statesman.commands;

import java.util.ArrayList;

import statesman.GameException;

public class ConditionalCommand extends Command {

    public static final String ID_CONDITIONAL = "cond";
    public static final String DELIMITER_OR = "||";
    public static final String DELIMITER_AND = "&&";

    protected Function _group;
    protected Function _elseGroup;

    private ArrayList<String> _targetNames;
    private ArrayList<Boolean> _targetValues;

    private boolean _orMode;
    private Boolean _executeTrueGroup;

    private ConditionalCommand() {
        _group = null;
        _elseGroup = null;
        _orMode = false;
        _targetNames = null;
        _targetValues = null;
        _executeTrueGroup = null;
    }

    public ConditionalCommand(Function group, Function elseGroup,
            ArrayList<String> targetNames, ArrayList<Boolean> targetValues, boolean orMode) {
        this();
        _group = group;
        _elseGroup = elseGroup;
        _targetNames = targetNames;
        _targetValues = targetValues;
        _orMode = orMode;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new ConditionalCommand();
        }
        return _defaultInstance;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) throws GameException {
        if (arguments.length >= 2) {
            Boolean orMode = null;
            ArrayList<Boolean> targetValues = new ArrayList<Boolean>();
            ArrayList<String> targetNames = new ArrayList<String>();

            // The first argument is the command name, so start evaluating
            // the parts of the conditional with the second argument.
            boolean isConditionNext = false;
            boolean isConditionPrevious = false;
            for (int i = 1; i < arguments.length; i++) {
                String operatorOrTargetName = arguments[i].trim();

                boolean foundOrOperator =
                        operatorOrTargetName.equalsIgnoreCase(DELIMITER_OR);
                boolean foundAndOperator =
                        operatorOrTargetName.equalsIgnoreCase(DELIMITER_AND);

                if (operatorOrTargetName.length() == 2 && isConditionNext)
                {
                    if (!foundOrOperator && !foundAndOperator)
                    {
                        throw new GameException("Unknown operator found.");
                    }

                    if (orMode == null)
                    {
                        orMode = foundOrOperator;
                    }
                    else if ((orMode.booleanValue() && foundAndOperator)
                            || (!orMode.booleanValue() && foundOrOperator))
                    {
                        throw new GameException(
                                "Combining and/or conditional operators are not allowed.");
                    }
                    isConditionNext = false;
                    isConditionPrevious = true;
                    continue;
                }

                if (foundOrOperator || foundAndOperator)
                {
                    throw new GameException("Incorrect condition order.");
                }

                // Check for the negation operator.
                boolean targetValue = !operatorOrTargetName.startsWith("!");
                if (!targetValue)
                {
                    operatorOrTargetName = operatorOrTargetName.substring(1);
                }
                targetNames.add(operatorOrTargetName);
                targetValues.add(targetValue);
                isConditionNext = true;
                isConditionPrevious = false;
            }

            if (isConditionPrevious)
            {
                throw new GameException("Found stray operator inside condition.");
            }

            // Assume Or mode if it's a single condition.
            if (orMode == null)
            {
                orMode = true;
            }

            return new ConditionalCommand(
                    new Function(""),
                    new Function(""),
                    targetNames,
                    targetValues,
                    orMode);
        }
        return null;
    }

    @Override
    public void execute() {
        // Evaluate all conditions.
        for (int i = 0; i < _targetNames.size(); i++) {
            String targetName = _targetNames.get(i);
            boolean targetValue = _targetValues.get(i);
            boolean currentValue = JumpCommand.getConditionValue(targetName);
            boolean condition = (currentValue == targetValue);
            if (updateState(condition)) {
                break;
            }
        }
        // Execute the commands inside the condition's scope depending
        // on whether the condition is true or false.
        if (_executeTrueGroup) {
            _group.execute();
        } else {
            _elseGroup.execute();
        }
        // Reset the state.
        _executeTrueGroup = null;
    }

    private boolean updateState(boolean condition) {
        // The condition is true.
        if (condition) {
            // Or mode: requires only one condition to be true.
            if (_orMode) {
                _executeTrueGroup = true;
                return true;
            }
            // And mode: the first condition is true.
            if (_executeTrueGroup == null) {
                _executeTrueGroup = true;
            }
        } else {
            // The condition is false.
            _executeTrueGroup = false;
            // There's no point in checking the other conditions if we're
            // not in or mode.
            return !_orMode;
        }
        return false;
    }

    public Function getTrueGroup() {
        return _group;
    }

    public Function getFalseGroup() {
        return _elseGroup;
    }

    public ArrayList<String> getTargetNames() {
        return _targetNames;
    }

    public ArrayList<Boolean> getTargetValues() {
        return _targetValues;
    }

}
