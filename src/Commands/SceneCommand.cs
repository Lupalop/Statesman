namespace Statesman.Commands
{
    public class SceneCommand : Command
    {
        public const string kIdScene = "scene";

        private readonly string _targetScene;
        private bool _hasKey;

        public SceneCommand(string targetScene)
        {
            if (string.IsNullOrWhiteSpace(targetScene))
            {
                throw new ArgumentException("Target scene name cannot be empty");
            }
            _targetScene = targetScene;
            _hasKey = false;
        }

        public new static Command FromText(string commandId, string[] arguments)
        {
            if (commandId != kIdScene)
            {
                return null;
            }
            if (arguments.Length == 2)
            {
                return new SceneCommand(arguments[1]);
            }
            return null;
        }

        public override void Execute()
        {
            if (_hasKey || Content.Script.Scenes.ContainsKey(_targetScene))
            {
                Interpreter.Scene = Content.Script.Scenes[_targetScene];
                _hasKey = true;
            }
        }
    }
}
