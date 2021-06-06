package statesman.commands;

import java.io.IOException;

import statesman.Content;
import statesman.Interpreter;

public class SaveCommand implements Command {

    public static final String Id = "save";
    
    public SaveCommand() {
    }

    @Override
    public Command createInstance(String[] arguments) {
        return new SaveCommand();
    }

    @Override
    public void execute() {
        // TODO: move to messages
        System.out.println("Enter save name:");
        String name = Interpreter.getScanner().nextLine().trim().toLowerCase();
        if (name.isBlank()) {
            System.out.println("Invalid name.");
            return;
        }
        try {
            Content.saveState(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Your game has been saved!");
    }

}
