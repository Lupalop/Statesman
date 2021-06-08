package statesman.commands;

import statesman.Interpreter;

public class SwitchConditionalCommand extends ConditionalCommand {

    public static final String ID = "swcond";

    private int[] _switchIds;

    public SwitchConditionalCommand() {
        super();
    }
    
    public SwitchConditionalCommand(
            CommandGroup group,
            CommandGroup elseGroup,
            int[] switchIds,
            boolean[] targetValues,
            boolean orMode) {
        super();
        _group = group;
        _elseGroup = elseGroup;
        _switchIds = switchIds;
        _targetValues = targetValues;
        _orMode = orMode;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length == 2) {
            String condition = arguments[1];
            boolean orMode = useOrOperator(condition);
            String[] parts = getConditionParts(condition, orMode);
            boolean[] targetValues = new boolean[parts.length];
            int[] switchIds = new int[parts.length];
            
            for (int i = 0; i < parts.length; i++) {
                targetValues[i] = !parts[i].startsWith("!");
                if (targetValues[i]) {
                    switchIds[i] = Integer.parseInt(parts[i]);
                } else {
                    switchIds[i] = Integer.parseInt(parts[i].substring(1));
                }
            }
            
            return new SwitchConditionalCommand(
                    new CommandGroup(""),
                    new CommandGroup(""),
                    switchIds,
                    targetValues,
                    orMode);
        }
        
        return null;
    }
    
    @Override
    public void execute() {
        for (int i = 0; i < _switchIds.length; i++) {
            boolean currentState = false;
            currentState =
                    (Interpreter.getSwitches()[_switchIds[i]] == _targetValues[i]);
            boolean stopLooping = updateState(currentState); 
            if (stopLooping) {
                break;
            }
        }
        super.execute();
    }
    
    public int[] getSwitchIds() {
        return _switchIds;
    }

}
