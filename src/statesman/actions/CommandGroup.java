package statesman.actions;

import java.util.LinkedList;

public class CommandGroup {
	
	private String _name;
	private LinkedList<Command> _commands;
	
	public CommandGroup(String name, LinkedList<Command> commands) {
		_name = name;
		_commands = commands;
	}
	
	public CommandGroup(String name) {
		this(name, new LinkedList<Command>());
	}

	public String getName() {
		return _name;
	}

	public LinkedList<Command> getCommands() {
		return _commands;
	}

	public void execute() {
		int i = 0;
		while (i < _commands.size()) {
			Command command = _commands.get(i);
			// Handle jumps or breaks
			if (command instanceof JumpCommand ||
				command instanceof ConditionalJumpCommand ||
				command instanceof BreakCommand) {
				i = ((JumpCommand)command).getJumpIndex();
				// Break if the index is invalid
				if (i < 0 || i > _commands.size()) {
					break;
				}
				continue;
			}
			command.execute();
			i++;
		}
	}

}
