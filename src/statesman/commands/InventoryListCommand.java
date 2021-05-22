package statesman.commands;

import java.util.Iterator;

import statesman.Interpreter;
import statesman.InventoryItem;

public class InventoryListCommand implements Command {

    public static final String Id = "invlist";
    
    public InventoryListCommand() {
    }

    @Override
    public Command createInstance(String[] arguments) {
        return new InventoryListCommand();
    }

    @Override
    public void execute() {
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
                InventoryItem item = iterator.next();
                System.out.printf("%s: %s%n", item.getName(), item.getDescription());
            }
        } else {
            // TODO: use messages provided by the interpreter's source
            System.out.println("Your inventory is empty!");
        }
    }

}
