package statesman.actions;

import statesman.Interpreter;
import statesman.Scene;

public class ConditionalCommand implements Command {

    public static final String Id = "cond";

    private CommandGroup _group;
    private CommandGroup _elseGroup;
    private int[] _switchIds;
    private boolean[] _targetValues;
    private boolean _orMode;
    
    public ConditionalCommand(
            CommandGroup group,
            CommandGroup elseGroup,
            int[] switchIds,
            boolean[] targetValues,
            boolean orMode) {
        _group = group;
        _elseGroup = elseGroup;
        _switchIds = switchIds;
        _targetValues = targetValues;
        _orMode = orMode;
    }
    
    public ConditionalCommand() {
        this(null, null, null, null, false);
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        if (arguments.length == 2) {
            String[] parts;
            boolean orMode = arguments[1].contains(";");
            boolean andMode = arguments[1].contains(",");
            if (orMode && andMode) {
                throw new UnsupportedOperationException("Combining and/or conditional operators are not allowed.");
            }
            if (orMode) {
                parts = arguments[1].split(";");
                orMode = true;
            } else {
                parts = arguments[1].split(",");
            }
            
            int[] switchIds = new int[parts.length];
            boolean[] targetValues = new boolean[parts.length];
            for (int i = 0; i < parts.length; i++) {
                targetValues[i] = !parts[i].startsWith("!");
                if (targetValues[i]) {
                    switchIds[i] = Integer.parseInt(parts[i]);
                } else {
                    switchIds[i] = Integer.parseInt(parts[i].substring(1));
                }
            }
            return new ConditionalCommand(
                    new CommandGroup(""),
                    new CommandGroup(""),
                    switchIds,
                    targetValues,
                    orMode);
        }
        return null;
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, arguments);
    }

    @Override
    public void execute() {
        Boolean shouldExecute = null;
        for (int i = 0; i < _switchIds.length; i++) {
            boolean switchState = (Interpreter.getSwitches()[_switchIds[i]] == _targetValues[i]);
            if (switchState) {
                if (_orMode) {
                    shouldExecute = true;
                    break;
                }
                if (shouldExecute == null) {
                    shouldExecute = true;
                }
            } else {
                shouldExecute = false;
            }
        }
        
        if (shouldExecute) {
            _group.execute();
        } else {
            _elseGroup.execute();
        }
    }

    public CommandGroup getGroup() {
        return _group;
    }

    public CommandGroup getElseGroup() {
        return _elseGroup;
    }

    public int[] getSwitchIds() {
        return _switchIds;
    }
    
    public boolean[] getTargetValues() {
        return _targetValues;
    }

}
