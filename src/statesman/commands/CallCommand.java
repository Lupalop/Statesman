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
    private String _groupName;

    private CallCommand() {
        _groupName = "";
        _callType = null;
    }

    public CallCommand(CallType callType, String groupName) {
        if (callType == null) {
            throw new IllegalArgumentException();
        }
        if (callType != CallType.SUPER
                && (groupName == null || groupName.isBlank())) {
            throw new IllegalArgumentException();
        }
        _groupName = groupName;
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
        String groupName = "";

        if (arguments.length == 1 && commandId.equalsIgnoreCase(ID_SUPER)) {
            callType = CallType.SUPER;
        } else if (arguments.length == 2) {
            if (commandId.equalsIgnoreCase(ID_CALL)
                    || commandId.equalsIgnoreCase(ID_GOTO)) {
                callType = CallType.NORMAL;
            } else if (commandId.equalsIgnoreCase(ID_CALL_GLOB)) {
                callType = CallType.GLOBAL;
            }
            groupName = arguments[1];
        }
        
        if (callType == null) {
            return null;
        }

        return new CallCommand(callType, groupName);
    }

    @Override
    public void execute() {
        // Return early because we're handled by the command group.
        if (_callType == CallType.SUPER) {
            return;
        }
        
        CommandGroup group = Content.getScript().getCommandGroups()
                .get(_groupName);

        // Local scene groups override the global group if we're
        // a normal call.
        if (Interpreter.getScene() != null && _callType == CallType.NORMAL) {
            CommandGroup localGroup = Interpreter
                    .getScene()
                    .getCommandGroups()
                    .get(_groupName);
            if (localGroup != null) {
                localGroup.execute();
                return;
            }
        }

        if (group != null) {
            group.execute();
        }
    }

}
