package statesman.actions;

import statesman.Interpreter;
import statesman.Scene;

public class SetSwitchCommand extends Command {

	public static final String Id = "set";
	
	private int _switchId;
	private boolean _value;
	
	public SetSwitchCommand() {
		_switchId = 0;
		_value = false;
	}
	
	public SetSwitchCommand(int switchId, boolean value) {
		if (switchId < 0 || switchId > Interpreter.getSwitches().length) {
			throw new IllegalArgumentException();
		}
		_switchId = switchId;
		_value = value;
	}

	@Override
	public Command createInstance(Scene parent, String[] arguments) {
		if (arguments.length == 3) {
			int switchId = Integer.parseInt(arguments[1]);
			boolean value = Boolean.parseBoolean(arguments[2]);
			return new SetSwitchCommand(switchId, value);
		}
		return null;
	}

	@Override
	public Command createInstance(String[] arguments) {
		return createInstance(null, arguments);
	}

	@Override
	public void execute() {
		Interpreter.getSwitches()[_switchId] = _value;
	}

}
