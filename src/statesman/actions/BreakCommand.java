package statesman.actions;

import statesman.Scene;

public class BreakCommand extends JumpCommand {

    public static final String Id = "break";

    public BreakCommand() {
        super();
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        return new BreakCommand();
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
