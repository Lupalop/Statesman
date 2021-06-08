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
        System.out.println(Content.getScript().getMessage("sl_1"));
        String name = Interpreter.getScanner().nextLine().trim().toLowerCase();
        if (name.isBlank()) {
            System.out.println(Content.getScript().getMessage("sl_2"));
            return;
        }
        if (name.length() > 255) {
            System.out.println(Content.getScript().getMessage("sl_3"));
            return;
        }
        boolean gameSaved = false;
        try {
            Content.saveState(name);
            gameSaved = true;
        } catch (IOException e) {
            System.out.println(Content.getScript().getMessage("sl_6"));
        }
        
        if (gameSaved) {
            System.out.println(Content.getScript().getMessage("sl_7"));
        }
    }

}
