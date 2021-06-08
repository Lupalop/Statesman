package statesman.commands;

public class ReturnCommand extends JumpCommand {

    public static final String ID = "ret";

    public ReturnCommand() {
        super();
    }

    @Override
    public Command createInstance(String[] arguments) {
        return new ReturnCommand();
    }

    @Override
    public void execute() {
    }

    public int getJumpIndex() {
        return Integer.MAX_VALUE;
    }

}
