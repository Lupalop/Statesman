package statesman.commands;

import java.util.LinkedList;

import statesman.Content;
import statesman.commands.CallCommand.CallType;

public class Function {

    private String _name;
    private LinkedList<Command> _commands;

    public Function(String name, LinkedList<Command> commands) {
        _name = name;
        _commands = commands;
    }

    public Function(String name) {
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
            if (command instanceof JumpCommand) {
                i = ((JumpCommand) command).getJumpIndex();
                // Stop execution if the index is invalid
                if (i < 0 || i > _commands.size()) {
                    break;
                }
                continue;
            }
            // Call base function.
            if (command instanceof CallCommand) {
                CallType callType = ((CallCommand) command).getCallType();
                if (callType == CallType.SUPER) {
                    Function baseFunction = Content
                            .getScript()
                            .getFunctions()
                            .get(_name);
                    if (baseFunction != null) {
                        baseFunction.execute();
                    }
                }
            }
            command.execute();
            i++;
        }
    }

}
