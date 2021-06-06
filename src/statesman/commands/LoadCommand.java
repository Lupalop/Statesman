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
        System.out.println(Content.getSource().getMessage("sl_1"));
        String name = Interpreter.getScanner().nextLine().trim().toLowerCase();
        if (name.isBlank()) {
            System.out.println(Content.getSource().getMessage("sl_2"));
            return;
        }
        if (name.length() > 255) {
            System.out.println(Content.getSource().getMessage("sl_3"));
        }
        boolean gameLoaded = false;
        try {
            Content.loadState(name);
            gameLoaded = true;
        } catch (IOException e) {
            System.out.println(Content.getSource().getMessage("sl_4"));
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
        
        if (gameLoaded) {
            System.out.println(Content.getSource().getMessage("sl_5"));
        }
    }


}
