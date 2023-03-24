namespace Statesman
{
    public class InventoryItem
    {
        public string Name { get; }
        public string Description { get; }
        public Scene OwnerScene { get; }

        public InventoryItem(string name, string description, Scene ownerScene)
        {
            Name = name;
            Description = description;
            OwnerScene = ownerScene;
        }
    }
}
