package statesman.commands;

import java.io.IOException;

import statesman.Content;
import statesman.GameException;
import statesman.Interpreter;

public class MenuCommand extends Command {

    private enum MenuActionType {
        SAVE, LOAD, QUIT
    }

    public static final String ID_SAVE = "save";
    public static final String ID_LOAD = "load";
    public static final String ID_QUIT = "quit";

    private final MenuActionType _actionType;

    private MenuCommand() {
        _actionType = null;
    }

    public MenuCommand(MenuActionType actionType) {
        if (actionType == null) {
            throw new IllegalArgumentException("Action type cannot be null");
        }
        _actionType = actionType;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new MenuCommand();
        }
        return _defaultInstance;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length == 1) {
            MenuActionType actionType = null;
            if (commandId.equalsIgnoreCase(ID_SAVE)) {
                actionType = MenuActionType.SAVE;
            } else if (commandId.equalsIgnoreCase(ID_LOAD)) {
                actionType = MenuActionType.LOAD;
            } else if (commandId.equalsIgnoreCase(ID_QUIT)) {
                actionType = MenuActionType.QUIT;
            } else {
                return null;
            }
            return new MenuCommand(actionType);
        }
        return null;
    }

    @Override
    public void execute() {
        if (_actionType == MenuActionType.QUIT) {
            System.exit(0);
            return;
        }

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

        boolean actionSuccess = false;
        if (_actionType == MenuActionType.SAVE) {
            try {
                Content.saveState(name);
                actionSuccess = true;
            } catch (IOException e) {
                System.out.println(Content.getScript().getMessage("sl_6"));
            }

            if (actionSuccess) {
                System.out.println(Content.getScript().getMessage("sl_7"));
            }
        } else if (_actionType == MenuActionType.LOAD) {
            try {
                Content.loadState(name);
                actionSuccess = true;
            } catch (IOException e) {
                System.out.println(Content.getScript().getMessage("sl_4"));
            } catch (GameException e) {
                System.out.println(e.getMessage());
            }

            if (actionSuccess) {
                System.out.println(Content.getScript().getMessage("sl_5"));
            }
        }
    }

}
