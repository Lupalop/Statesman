package statesman.commands;

import java.util.Iterator;

import statesman.Interpreter;
import statesman.InventoryItem;

public class InventoryCommand implements Command {

    public static final String Id = "inv";
    
    private String _itemName;
    private InventoryAction _action;
    
    public enum InventoryAction { Add, Remove, Clear, List };
    
    public InventoryCommand() {
        _itemName = null;
        _action = null;
    }
    
    public InventoryCommand(InventoryAction action, String itemName) {
        if ((action != InventoryAction.List && action != InventoryAction.Clear) && itemName.isBlank()) {
            throw new IllegalArgumentException("Inventory item name cannot be blank");
        }
        if (action == null) {
            throw new IllegalArgumentException("Invalid value was passed to the action parameter");
        }
        _itemName = itemName;
        _action = action;
    }
    
    public InventoryCommand(InventoryAction action) {
        this(action, null);
    }

    @Override
    public Command createInstance(String[] arguments) {
        String actionString = null;
        InventoryAction action = null;
        if (arguments.length == 2) {
            actionString = arguments[1].trim().toLowerCase();
            switch (actionString) {
            case "list":
                action = InventoryAction.List;
                break;
            case "clear":
                action = InventoryAction.Clear;
                break;
            default:
                action = null;
                break;
            }
            return new InventoryCommand(action);
        } else if (arguments.length == 3) {
            actionString = arguments[1].trim().toLowerCase();
            switch (actionString) {
            case "add":
                action = InventoryAction.Add;
                break;
            case "rm":
                action = InventoryAction.Remove;
                break;
            default:
                action = null;
                break;
            }
            String itemName = arguments[2].trim();
            return new InventoryCommand(action, itemName);
        }
        return null;
    }

    @Override
    public void execute() {
        InventoryItem item = getItem();
        if (item != null) {
            if (Interpreter.getInventory().containsKey(item.getName())) {
                // TODO: use messages provided by the interpreter's source
                switch (_action) {
                case Add:
                    System.out.println("This item is already in your inventory!");
                    break;
                case Remove:
                    Interpreter.getInventory().remove(_itemName);
                    break;
                default:
                    break;
                }
            } else {
                switch (_action) {
                case Add:
                    Interpreter.getInventory().put(item.getName(), item);
                    break;
                case Remove:
                    System.out.println("This item is NOT in your inventory!");
                    break;
                default:
                    break;
                }
            }
        } else if (_action == InventoryAction.List) {
            int inventorySize = Interpreter.getInventory().size();
            if (inventorySize > 0) {
                // TODO: use messages provided by the interpreter's source
                if (inventorySize == 1) {
                    System.out.printf("You only have one item in your inventory:%n", inventorySize);
                } else {
                    System.out.printf("You have %s items in your inventory:%n", inventorySize);
                }
                Iterator<InventoryItem> iterator = Interpreter.getInventory().values().iterator();
                while (iterator.hasNext()) {
                    InventoryItem currentItem = iterator.next();
                    System.out.printf("%s: %s%n", currentItem.getName(), currentItem.getDescription());
                }
            } else {
                // TODO: use messages provided by the interpreter's source
                System.out.println("Your inventory is empty!");
            }
        } else if (_action == InventoryAction.Clear) {
            Interpreter.getInventory().clear();
        }
    }

    public String getItemName() {
        return _itemName;
    }
    
    private InventoryItem getItem() {
        return Interpreter.getCurrentScene().getItems().get(_itemName);
    }

}
