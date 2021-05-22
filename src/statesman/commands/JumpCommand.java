package statesman.commands;

public class JumpCommand implements Command {

    public static final String Id = "jmp";

    private int _jumpIndex;

    public JumpCommand() {
        _jumpIndex = 0;
    }

    public JumpCommand(int line) {
        _jumpIndex = line;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length == 2) {
            int line = Integer.parseInt(arguments[1]);
            return new JumpCommand(line);
        }
        return null;
    }

    @Override
    public void execute() {
    }

    public int getJumpIndex() {
        return _jumpIndex;
    }

}
