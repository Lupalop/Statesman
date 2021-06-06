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
        System.out.println("Enter the name of your saved game:");
        String name = Interpreter.getScanner().nextLine().trim().toLowerCase();
        if (name.isBlank()) {
            System.out.println("Invalid name.");
            return;
        }
        if (name.length() > 255) {
            System.out.println("Name too long! Try a shorter name for your saved game.");
            return;
        }
        boolean gameSaved = false;
        try {
            Content.saveState(name);
            gameSaved = true;
        } catch (IOException e) {
            System.out.println("Your game cannot be saved.");
        }
        
        if (gameSaved) {
            System.out.println("Your game has been saved!");
        }
    }

}
