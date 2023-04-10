package statesman.commands;

public class ConditionalCommand extends Command {

    public static final String ID_CONDITIONAL = "cond";
    public static final String DELIMITER_OR = ";";
    public static final String DELIMITER_AND = ",";

    private String[] _targetNames;
    protected CommandGroup _group;
    protected CommandGroup _elseGroup;
    protected boolean[] _targetValues;

    protected boolean _orMode;
    protected Boolean _shouldExecute;

    private ConditionalCommand() {
        _group = null;
        _elseGroup = null;
        _orMode = false;
        _targetNames = null;
        _targetValues = null;
        _shouldExecute = null;
    }

    public ConditionalCommand(CommandGroup group, CommandGroup elseGroup,
            String[] targetNames, boolean[] targetValues, boolean orMode) {
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
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length == 2) {
            String condition = arguments[1];
            boolean orMode = useOrOperator(condition);
            String[] parts = getConditionParts(condition, orMode);
            boolean[] targetValues = new boolean[parts.length];
            String[] itemNames = new String[parts.length];

            for (int i = 0; i < parts.length; i++) {
                targetValues[i] = !parts[i].startsWith("!");
                if (targetValues[i]) {
                    itemNames[i] = parts[i];
                } else {
                    itemNames[i] = parts[i].substring(1);
                }
            }

            return new ConditionalCommand(new CommandGroup(""),
                    new CommandGroup(""), itemNames, targetValues, orMode);
        }
        return null;
    }

    @Override
    public void execute() {
        for (int i = 0; i < _targetNames.length; i++) {
            String targetName = _targetNames[i];
            boolean targetValue = _targetValues[i];
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

    protected boolean updateState(boolean newState) {
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

    protected boolean useOrOperator(String condition) {
        boolean orMode = condition.contains(DELIMITER_OR);
        boolean andMode = condition.contains(DELIMITER_AND);

        if (orMode && andMode) {
            throw new UnsupportedOperationException(
                    "Combining and/or conditional operators are not allowed.");
        }

        return orMode;
    }

    protected String[] getConditionParts(String condition, boolean orMode) {
        String delimiter = DELIMITER_AND;

        if (orMode) {
            delimiter = DELIMITER_OR;
        }

        return condition.split(delimiter);
    }

    public String[] getItemNames() {
        return _targetNames;
    }

    public CommandGroup getGroup() {
        return _group;
    }

    public CommandGroup getElseGroup() {
        return _elseGroup;
    }

    public boolean[] getTargetValues() {
        return _targetValues;
    }

}
