namespace Statesman
{
    public class InventoryItem
    {
        public string Name { get; }
        public string Description { get; }
        public Scene OwnerScene { get; }
        public bool IsSwitch { get; }

        public InventoryItem(string name, string description, Scene ownerScene, bool isSwitch = false)
        {
            Name = name;
            Description = description;
            OwnerScene = ownerScene;
            IsSwitch = isSwitch;
        }

        public InventoryItem(string name)
            : this(name, null, null, true)
        {
        }
    }
}
