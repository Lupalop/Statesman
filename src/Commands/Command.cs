using System.Reflection;

namespace Statesman.Commands
{
    public abstract class Command
    {
        public const string kGeneratorMethodName = "FromText";

        public static Dictionary<string, Type> All { get; private set; }

        static Command()
        {
            All = new Dictionary<string, Type>
            {
                // XXX: manually input IDs of new commands here!
                { PrintCommand.kIdPrintSingle, typeof(PrintCommand) },
                { PrintCommand.kIdPrintConcatenate, typeof(PrintCommand) },
                { PrintCommand.kIdPrintRandom, typeof(PrintCommand) },
                { JumpCommand.kIdJump, typeof(JumpCommand) },
                { JumpCommand.kIdSwitchJump, typeof(JumpCommand) },
                { JumpCommand.kIdInventoryJump, typeof(JumpCommand) },
                { JumpCommand.kIdReturn, typeof(JumpCommand) },
                { SwitchSetCommand.kIdSwitchSet, typeof(SwitchSetCommand) },
                { ConditionalCommand.kIdConditional, typeof(ConditionalCommand) },
                { InventoryCommand.kIdInventory, typeof(InventoryCommand) },
                { SceneCommand.kIdScene, typeof(SceneCommand) },
                { CallCommand.kIdCall, typeof(CallCommand) },
                { CallCommand.kIdGoto, typeof(CallCommand) },
                { CallCommand.kIdCallGlobal, typeof(CallCommand) },
                { CallCommand.kIdSuper, typeof(CallCommand) },
                { MenuCommand.kIdQuit, typeof(MenuCommand) },
                { MenuCommand.kIdSave, typeof(MenuCommand) },
                { MenuCommand.kIdLoad, typeof(MenuCommand) },
                { PointsCommand.kIdPoints, typeof(PointsCommand) },
            };
        }

        public static Command Find(string[] arguments)
        {
            string commandId = arguments[0].ToLowerInvariant();
            if (All.TryGetValue(commandId, out Type commandType))
            {
                MethodInfo generatorMethod =
                    commandType.GetMethod(kGeneratorMethodName);
                if (generatorMethod == null)
                {
                    throw new InvalidOperationException("Missing generator method.");
                }
                Command command = (Command)generatorMethod
                    .Invoke(null, new object[] { commandId, arguments });
                return command;
            }
            return null;
        }

        public virtual void Execute() { }
    }
}
