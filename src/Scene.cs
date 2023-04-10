using Statesman.Commands;

namespace Statesman
{
    public class Scene
    {
        public static readonly string FunctionEntry = "$";

        public string Name { get; }
        public Dictionary<string, Command> Actions { get; }
        public Dictionary<string, Function> Functions { get; }
        public Dictionary<string, InventoryItem> Items { get; }

        public Scene(
                string name,
                Dictionary<string, Command> actions,
                Dictionary<string, Function> functions,
                Dictionary<string, InventoryItem> items)
        {
            if (string.IsNullOrWhiteSpace(name))
            {
                throw new ArgumentException("Scene name cannot be empty");
            }
            Name = name;
            Actions = actions;
            Functions = functions;
            Items = items;
        }

        public Scene(string name) : this(
              name,
              new Dictionary<string, Command>(),
              new Dictionary<string, Function>(),
              new Dictionary<string, InventoryItem>())
        {
        }

        public void RunEntry()
        {
            if (Functions.TryGetValue(FunctionEntry, out Function value))
            {
                value.Execute();
            }
        }
    }
}
