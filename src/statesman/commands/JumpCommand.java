package statesman.commands;

public class JumpCommand extends Command {

    public static final String ID = "jmp";

    protected int _lineIfTrue;
    protected int _lineIfFalse;

    public JumpCommand() {
        _lineIfTrue = 0;
        _lineIfFalse = 0;
    }

    public JumpCommand(int line) {
        super();
        _lineIfTrue = line;
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

    protected int getLineNumberFromString(String lineNumber) {
        int index = 0;
        if (lineNumber.equalsIgnoreCase("ret")) {
            index = Integer.MAX_VALUE;
        } else {
            index = Integer.parseInt(lineNumber);
        }
        return index;
    }
    
    public int getJumpIndex() {
        return _lineIfTrue;
    }

}
