package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class CallCommand extends Command {

    public enum CallType {
        NORMAL, SUPER, GLOBAL
    }
    
    public static final String ID_GOTO = "goto";
    public static final String ID_CALL = "call";
    public static final String ID_CALL_GLOB = "callglob";
    public static final String ID_SUPER = "super";

    private CallType _callType;
    private String _functionName;

    private CallCommand() {
        _functionName = "";
        _callType = null;
    }

    public CallCommand(CallType callType, String functionName) {
        if (callType == null) {
            throw new IllegalArgumentException();
        }
        if (callType != CallType.SUPER
                && (functionName == null || functionName.isBlank())) {
            throw new IllegalArgumentException();
        }
        _functionName = functionName;
        _callType = callType;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new CallCommand();
        }
        return _defaultInstance;
    }

    public CallType getCallType() {
        return _callType;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        CallType callType = null;
        String functionName = "";

        if (arguments.length == 1 && commandId.equalsIgnoreCase(ID_SUPER)) {
            callType = CallType.SUPER;
        } else if (arguments.length == 2) {
            if (commandId.equalsIgnoreCase(ID_CALL)
                    || commandId.equalsIgnoreCase(ID_GOTO)) {
                callType = CallType.NORMAL;
            } else if (commandId.equalsIgnoreCase(ID_CALL_GLOB)) {
                callType = CallType.GLOBAL;
            }
            functionName = arguments[1];
        }
        
        if (callType == null) {
            return null;
        }

        return new CallCommand(callType, functionName);
    }

    @Override
    public void execute() {
        // Return early because we're handled by the containing function.
        if (_callType == CallType.SUPER) {
            return;
        }

        Function globalFunction = Content.getScript().getFunctions()
                .get(_functionName);

        // Local functions override global functions if we're a normal call.
        if (Interpreter.getScene() != null && _callType == CallType.NORMAL) {
            Function localFunction = Interpreter
                    .getScene()
                    .getFunctions()
                    .get(_functionName);
            if (localFunction != null) {
                localFunction.execute();
                return;
            }
        }

        if (globalFunction != null) {
            globalFunction.execute();
        }
    }

}
