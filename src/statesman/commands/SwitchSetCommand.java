package statesman.commands;

import statesman.Interpreter;

public class SwitchSetCommand extends Command {

    public static final String ID_SET = "set";

    private String _switchId;
    private boolean _value;

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new SwitchSetCommand();
        }
        return _defaultInstance;
    }

    private SwitchSetCommand() {
        _switchId = "";
        _value = false;
    }

    public SwitchSetCommand(String switchId, boolean value) {
        _switchId = switchId;
        _value = value;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length == 3) {
            String switchId = arguments[1];
            boolean value = Boolean.parseBoolean(arguments[2]);
            return new SwitchSetCommand(switchId, value);
        }
        return null;
    }

    @Override
    public void execute() {
        Interpreter.getSwitches().put(_switchId, _value);
    }

}
