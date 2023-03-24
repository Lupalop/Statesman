namespace Statesman
{
    public class InventoryItem
    {
        private string _name;
        private string _description;
        private Scene _ownerScene;

        public InventoryItem(string name, string description, Scene ownerScene)
        {
            _name = name;
            _description = description;
            _ownerScene = ownerScene;
        }

        public string getName()
        {
            return _name;
        }

        public string getDescription()
        {
            return _description;
        }

        public Scene getOwnerScene()
        {
            return _ownerScene;
        }
    }
}
