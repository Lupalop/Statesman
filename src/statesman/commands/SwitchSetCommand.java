package statesman.commands;

import statesman.Interpreter;

public class SwitchSetCommand implements Command {

    public static final String Id = "set";

    private int _switchId;
    private boolean _value;

    public SwitchSetCommand() {
        _switchId = 0;
        _value = false;
    }

    public SwitchSetCommand(int switchId, boolean value) {
        if (switchId < 0 || switchId > Interpreter.getSwitches().length) {
            throw new IllegalArgumentException();
        }
        _switchId = switchId;
        _value = value;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length == 3) {
            int switchId = Integer.parseInt(arguments[1]);
            boolean value = Boolean.parseBoolean(arguments[2]);
            return new SwitchSetCommand(switchId, value);
        }
        return null;
    }

    @Override
    public void execute() {
        Interpreter.getSwitches()[_switchId] = _value;
    }

}
