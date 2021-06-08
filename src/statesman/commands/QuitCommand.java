package statesman.commands;

public class QuitCommand implements Command {

    public static final String ID = "quit";
    
    public QuitCommand() {
    }

    @Override
    public Command createInstance(String[] arguments) {
        return new QuitCommand();
    }

    @Override
    public void execute() {
        System.exit(0);
    }

}
