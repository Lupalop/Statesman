package statesman.commands;

import java.util.LinkedList;

import statesman.Content;

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
    
    public void setName(String name) {
        _name = name;
    }

    public LinkedList<Command> getCommands() {
        return _commands;
    }

    public void execute() {
        int i = 0;
        while (i < _commands.size()) {
            Command command = _commands.get(i);
            // Handle jumps
            if (command instanceof JumpCommand ||
                command instanceof ReturnCommand) {
                i = ((JumpCommand)command).getJumpIndex();
                // Stop execution if the index is invalid
                if (i < 0 || i > _commands.size()) {
                    break;
                }
                continue;
            }
            // Call overridden global command
            if (command instanceof GotoBaseCommand) {
                CommandGroup group = Content.getScript().getCommandGroups().get(_name);
                if (group != null) {
                    group.execute();
                }
            }
            command.execute();
            i++;
        }
    }

}
