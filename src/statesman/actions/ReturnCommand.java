package statesman.actions;

import statesman.Scene;

public class ReturnCommand extends JumpCommand {

    public static final String Id = "ret";

    public ReturnCommand() {
        super();
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        return new ReturnCommand();
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, arguments);
    }

    @Override
    public void execute() {
    }

    public int getJumpIndex() {
        return Integer.MAX_VALUE;
    }

}
