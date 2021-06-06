package statesman;

public class InventoryItem {

    private String _name;
    private String _description;
    private Scene _ownerScene;
    
    public InventoryItem(String name, String description, Scene ownerScene) {
        _name = name;
        _description = description;
        _ownerScene = ownerScene;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }
    
    public Scene getOwnerScene() {
        return _ownerScene;
    }

}
