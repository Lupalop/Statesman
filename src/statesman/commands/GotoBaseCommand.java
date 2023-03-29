package statesman.commands;

public class GotoBaseCommand extends Command {

    public static final String ID = "gotob";

    public GotoBaseCommand() {
    }

    @Override
    public Command createInstance(String[] arguments) {
        return new GotoBaseCommand();
    }

    @Override
    public void execute() {
    }

}
