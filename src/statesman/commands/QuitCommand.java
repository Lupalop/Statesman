package statesman.commands;

import statesman.Scene;

public class QuitCommand implements Command {

    public static final String Id = "quit";
    
    public QuitCommand() {
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        return new QuitCommand();
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, null);
    }

    @Override
    public void execute() {
        System.exit(0);
    }

}
