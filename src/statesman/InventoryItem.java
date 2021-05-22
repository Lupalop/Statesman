package statesman;

public class InventoryItem {

    private String _name;
    private String _description;
    
    public InventoryItem(String name, String description) {
        _name = name;
        _description = description;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

}
