package statesman.commands;

import java.io.IOException;

import statesman.Content;
import statesman.Interpreter;

public class LoadCommand implements Command {

    public static final String Id = "load";
    
    public LoadCommand() {
    }

    @Override
    public Command createInstance(String[] arguments) {
        return new LoadCommand();
    }

    @Override
    public void execute() {
        // TODO: move to messages
        System.out.println("Enter name of save to be loaded:");
        String name = Interpreter.getScanner().nextLine().trim().toLowerCase();
        if (name.isBlank()) {
            System.out.println("Invalid name.");
            return;
        }
        try {
            Content.loadState(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Your game has been loaded!");
    }


}
