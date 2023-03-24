using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Statesman.Commands
{
    public class SceneCommand : Command
    {
        public static readonly string ID = "scene";

        private string _targetScene;
        private bool _hasKey;

        public SceneCommand()
        {
            _targetScene = "";
            _hasKey = false;
        }

        public SceneCommand(string targetScene)
        {
            if (string.IsNullOrWhiteSpace(targetScene))
            {
                throw new ArgumentException();
            }
            _targetScene = targetScene;
            _hasKey = false;
        }

        public override Command createInstance(string[] arguments)
        {
            if (arguments.Length == 2)
            {
                return new SceneCommand(arguments[1]);
            }
            return null;
        }

        public override void execute()
        {
            if (_hasKey || Content.getScript().getScenes().ContainsKey(_targetScene))
            {
                Interpreter.setScene(Content.getScript().getScenes()[_targetScene]);
                _hasKey = true;
            }
        }
    }
}
