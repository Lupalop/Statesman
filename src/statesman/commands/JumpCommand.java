package statesman.commands;

import statesman.Interpreter;

public class JumpCommand extends Command {

    public static final String ID_JMP = "jmp";
    public static final String ID_IJMP = "ijmp";
    public static final String ID_SJMP = "sjmp";
    public static final String ID_RET = "ret";

    private static final int LINE_INVALID = -1;

    private String _targetName;
    private boolean _isUnconditional;

    protected int _lineIfTrue;
    protected int _lineIfFalse;

    private JumpCommand() {
        _lineIfTrue = 0;
        _lineIfFalse = 0;
        _targetName = "";
    }

    public JumpCommand(int lineIfTrue, int lineIfFalse, String targetName) {
        if (targetName == null) {
            _isUnconditional = true;
        }
        _lineIfTrue = lineIfTrue;
        _lineIfFalse = lineIfFalse;
        _targetName = targetName;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new JumpCommand();
        }
        return _defaultInstance;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        switch (arguments.length) {
        case 1:
            if (!commandId.equalsIgnoreCase(ID_RET)) {
                break;
            }
            return new JumpCommand(Integer.MAX_VALUE, LINE_INVALID, null);
        case 2:
            if (!commandId.equalsIgnoreCase(ID_JMP)) {
                break;
            }
            int line = getLineNumberFromString(arguments[1]);
            if (line == LINE_INVALID) {
                break;
            }
            return new JumpCommand(line, LINE_INVALID, null);
        case 4:
            if (!commandId.equalsIgnoreCase(ID_IJMP)
                    && !commandId.equalsIgnoreCase(ID_SJMP)) {
                break;
            }
            String targetName = arguments[1].trim();
            int lineIfTrue = getLineNumberFromString(arguments[2]);
            int lineIfFalse = getLineNumberFromString(arguments[3]);
            if (lineIfTrue == LINE_INVALID || lineIfFalse == LINE_INVALID) {
                break;
            }
            return new JumpCommand(lineIfTrue, lineIfFalse, targetName);
        default:
            break;
        }
        return null;
    }

    @Override
    public void execute() {
    }

    protected int getLineNumberFromString(String lineNumber) {
        if (lineNumber.equalsIgnoreCase("ret")) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(lineNumber);
        } catch (NumberFormatException ex) {
            return LINE_INVALID;
        }
    }

    public int getJumpIndex() {
        if (_isUnconditional || getConditionValue(_targetName)) {
            return _lineIfTrue;
        }
        return _lineIfTrue;
    }

    public static boolean getConditionValue(String targetName) {
        if (targetName.startsWith("i:")) {
            targetName = targetName.substring(2);
            return Interpreter.getInventory().containsKey(targetName)
                    ? Interpreter.getInventory().get(targetName) != null
                    : false;
        }
        return Interpreter.getSwitches().containsKey(targetName)
                ? Interpreter.getSwitches().get(targetName)
                : false;
    }

}
