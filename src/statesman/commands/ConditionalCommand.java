package statesman.commands;

import java.util.ArrayList;

import statesman.GameException;

public class ConditionalCommand extends Command {

    public static final String ID_CONDITIONAL = "cond";
    public static final String DELIMITER_OR = "||";
    public static final String DELIMITER_AND = "&&";

    protected CommandGroup _group;
    protected CommandGroup _elseGroup;

    private ArrayList<String> _targetNames;
    private ArrayList<Boolean> _targetValues;

    private boolean _orMode;
    private Boolean _shouldExecute;

    private ConditionalCommand() {
        _group = null;
        _elseGroup = null;
        _orMode = false;
        _targetNames = null;
        _targetValues = null;
        _shouldExecute = null;
    }

    public ConditionalCommand(CommandGroup group, CommandGroup elseGroup,
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
                    new CommandGroup(""),
                    new CommandGroup(""),
                    targetNames,
                    targetValues,
                    orMode);
        }
        return null;
    }

    @Override
    public void execute() {
        for (int i = 0; i < _targetNames.size(); i++) {
            String targetName = _targetNames.get(i);
            boolean targetValue = _targetValues.get(i);
            boolean keyValue = JumpCommand.getConditionValue(targetName);

            boolean stopLooping = updateState(keyValue == targetValue);
            if (stopLooping) {
                break;
            }
        }

        if (_shouldExecute) {
            _group.execute();
        } else {
            _elseGroup.execute();
        }
        _shouldExecute = null;
    }

    private boolean updateState(boolean newState) {
        if (newState) {
            if (_orMode) {
                _shouldExecute = true;
                return true;
            }
            if (_shouldExecute == null) {
                _shouldExecute = true;
            }
        } else {
            _shouldExecute = false;
        }
        return false;
    }

    public CommandGroup getGroup() {
        return _group;
    }

    public CommandGroup getElseGroup() {
        return _elseGroup;
    }

    public ArrayList<String> getTargetNames() {
        return _targetNames;
    }

    public ArrayList<Boolean> getTargetValues() {
        return _targetValues;
    }

}
