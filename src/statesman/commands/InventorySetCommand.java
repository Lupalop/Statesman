package statesman.commands;

import java.util.Iterator;

import statesman.Interpreter;
import statesman.InventoryItem;

public class InventorySetCommand implements Command {

    public static final String Id = "invset";
    
    private String _itemName;
    private InventoryAction _action;
    
    public enum InventoryAction { Add, Remove, List };
    
    public InventorySetCommand() {
        _itemName = null;
        _action = null;
    }
    
    public InventorySetCommand(String itemName, InventoryAction action) {
        if (action != InventoryAction.List && itemName.isBlank()) {
            throw new IllegalArgumentException("Inventory item name cannot be blank");
        }
        if (action == null) {
            throw new IllegalArgumentException("Invalid value was passed to the action parameter");
        }
        _itemName = itemName;
        _action = action;
    }
    
    public InventorySetCommand(InventoryAction action) {
        this(null, action);
    }

    @Override
    public Command createInstance(String[] arguments) {
        String actionString = null;
        if (arguments.length == 2) {
            actionString = arguments[1].trim().toLowerCase();
            InventoryAction action = null;
            switch (actionString) {
            case "list":
                action = InventoryAction.List;
                break;
            default:
                action = null;
                break;
            }
            return new InventorySetCommand(action);
        } else if (arguments.length == 3) {
            String itemName = arguments[1].trim();
            actionString = arguments[2].trim().toLowerCase();
            InventoryAction action = null;
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
            return new InventorySetCommand(itemName, action);
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
        }
    }

    public String getItemName() {
        return _itemName;
    }
    
    private InventoryItem getItem() {
        return Interpreter.getCurrentScene().getItems().get(_itemName);
    }

}
