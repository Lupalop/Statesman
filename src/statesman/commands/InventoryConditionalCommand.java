package statesman.commands;

import statesman.Interpreter;

public class InventoryConditionalCommand extends ConditionalCommand {

    public static final String ID = "invcond";

    private String[] _itemNames;

    public InventoryConditionalCommand() {
        super();
    }

    public InventoryConditionalCommand(
            CommandGroup group,
            CommandGroup elseGroup,
            String[] itemNames,
            boolean[] targetValues,
            boolean orMode) {
        super();
        _group = group;
        _elseGroup = elseGroup;
        _itemNames = itemNames;
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
            String[] itemNames = new String[parts.length];
            
            for (int i = 0; i < parts.length; i++) {
                targetValues[i] = !parts[i].startsWith("!");
                if (targetValues[i]) {
                    itemNames[i] = parts[i];
                } else {
                    itemNames[i] = parts[i].substring(1);
                }
            }
            
            return new InventoryConditionalCommand(
                    new CommandGroup(""),
                    new CommandGroup(""),
                    itemNames,
                    targetValues,
                    orMode);
        }
        
        return null;
    }

    @Override
    public void execute() {
        for (int i = 0; i < _itemNames.length; i++) {
            boolean currentState = false;
            currentState =
                    (Interpreter.getInventory().containsKey(_itemNames[i]) == _targetValues[i]);
            boolean stopLooping = updateState(currentState); 
            if (stopLooping) {
                break;
            }
        }
        super.execute();
    }

    public String[] getItemNames() {
        return _itemNames;
    }

}
