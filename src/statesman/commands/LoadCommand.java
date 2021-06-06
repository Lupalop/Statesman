package statesman.commands;

import java.io.IOException;

import statesman.Content;
import statesman.GameException;
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
        System.out.println("Enter the name of your saved game:");
        String name = Interpreter.getScanner().nextLine().trim().toLowerCase();
        if (name.isBlank()) {
            System.out.println("Invalid name.");
            return;
        }
        if (name.length() > 255) {
            System.out.println("Name too long! Try a shorter name for your saved game.");
        }
        boolean gameLoaded = false;
        try {
            Content.loadState(name);
            gameLoaded = true;
        } catch (IOException e) {
            System.out.println("The specified saved game was not found.");
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
        
        if (gameLoaded) {
            System.out.println("Your game has been loaded!");
        }
    }


}
