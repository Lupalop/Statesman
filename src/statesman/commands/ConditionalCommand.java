package statesman.commands;

public abstract class ConditionalCommand implements Command {

    public static final String Id = "cond";

    protected CommandGroup _group;
    protected CommandGroup _elseGroup;
    protected boolean _orMode;
    protected boolean[] _targetValues;
    protected Boolean _shouldExecute;

    public ConditionalCommand() {
        _group = null;
        _elseGroup = null;
        _orMode = false;
        _targetValues = null;
        _shouldExecute = null;
    }

    @Override
    public Command createInstance(String[] arguments) {
        return null;
    }

    @Override
    public void execute() {
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
        boolean orMode = condition.contains(";");
        boolean andMode = condition.contains(",");

        if (orMode && andMode) {
            throw new UnsupportedOperationException("Combining and/or conditional operators are not allowed.");
        }

        return orMode;
    }
    
    protected String[] getConditionParts(String condition, boolean orMode) {
        String delimiter = ",";
        
        if (orMode) {
            delimiter = ";";
        }
        
        return condition.split(delimiter);
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
