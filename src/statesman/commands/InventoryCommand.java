package statesman.commands;

import java.util.Iterator;

import statesman.Content;
import statesman.Interpreter;
import statesman.InventoryItem;

public class InventoryCommand extends Command {

    public enum InventoryActionType {
        ADD, REMOVE, CLEAR, LIST
    }

    public static final String ID_INV = "inv";
    public static final String ID_INV_ADD = "add";
    public static final String ID_INV_RM = "rm";
    public static final String ID_INV_CLEAR = "clear";
    public static final String ID_INV_LIST = "list";

    private String _itemName;
    private InventoryActionType _action;

    private InventoryCommand() {
        _itemName = null;
        _action = null;
    }

    public InventoryCommand(InventoryActionType action, String itemName) {
        boolean nameRequired = (action == InventoryActionType.ADD
                || action == InventoryActionType.REMOVE);
        if (nameRequired && itemName.isBlank()) {
            throw new IllegalArgumentException(
                    "Inventory item name cannot be blank");
        }
        if (action == null) {
            throw new IllegalArgumentException(
                    "Invalid value was passed to the action parameter");
        }
        _itemName = itemName;
        _action = action;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new InventoryCommand();
        }
        return _defaultInstance;
    }

    private InventoryItem getItem() {
        if (_itemName == null) {
            return null;
        }
        return Interpreter.getScene().getItems().get(_itemName);
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length < 2) {
            return null;
        }

        String action = arguments[1].trim();
        InventoryActionType actionType = null;
        String actionValue = null;
        if (arguments.length == 2) {
            if (action.equalsIgnoreCase(ID_INV_LIST)) {
                actionType = InventoryActionType.LIST;
            } else if (action.equalsIgnoreCase(ID_INV_CLEAR)) {
                actionType = InventoryActionType.CLEAR;
            }
        } else if (arguments.length == 3) {
            if (action.equalsIgnoreCase(ID_INV_ADD)) {
                actionType = InventoryActionType.ADD;
            } else if (action.equalsIgnoreCase(ID_INV_RM)) {
                actionType = InventoryActionType.REMOVE;
            }
            actionValue = arguments[2].trim();
        }

        if (actionType == null) {
            return null;
        }
        return new InventoryCommand(actionType, actionValue);
    }

    @Override
    public void execute() {
        InventoryItem item = getItem();
        switch (_action) {
        case ADD:
            if (item == null) {
                break;
            }
            if (Interpreter.getInventory().containsKey(item.getName())) {
                System.out.println(Content.getScript().findMessage("i_1"));
                break;
            }
            Interpreter.getInventory().put(item.getName(), item);
            break;
        case REMOVE:
            if (item == null) {
                break;
            }
            if (Interpreter.getInventory().containsKey(item.getName())) {
                Interpreter.getInventory().remove(_itemName);
                break;
            }
            System.out.println(Content.getScript().findMessage("i_2"));
            break;
        case LIST:
            int inventorySize = Interpreter.getInventory().size();
            if (inventorySize > 0) {
                if (inventorySize == 1) {
                    System.out.println(Content.getScript().findMessage("i_3"));
                } else {
                    System.out.printf(Content.getScript().findMessage("i_4"),
                            inventorySize);
                }
                Iterator<InventoryItem> iterator = Interpreter.getInventory()
                        .values().iterator();
                while (iterator.hasNext()) {
                    InventoryItem currentItem = iterator.next();
                    System.out.printf(Content.getScript().findMessage("i_5"),
                            currentItem.getName(),
                            currentItem.getDescription());
                }
            } else {
                System.out.println(Content.getScript().findMessage("i_6"));
            }
            break;
        case CLEAR:
            Interpreter.getInventory().clear();
            break;
        default:
            break;
        }
    }

}
