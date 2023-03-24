using Statesman.Commands;

namespace Statesman
{
    public class Script
    {
        public static readonly int DefaultSwitchSize = 2000;

        private int _maxPoints;
        public int MaxPoints
        {
            get => _maxPoints;
            set
            {
                if (value < 0)
                {
                    throw new ArgumentException(
                        "The maximum number of points must be greater than or equal to zero");
                }
                _maxPoints = value;
            }
        }

        private int _switchSize;
        public int SwitchSize
        {
            get => _switchSize;
            set
            {
                if (value < 0)
                {
                    throw new ArgumentException(
                        "The number of allocated switches must be greater than or equal to zero");
                }
                _switchSize = value;
            }
        }

        public Dictionary<string, string> Messages { get; set; }
        public Dictionary<string, Scene> Scenes { get; set; }
        public Dictionary<string, Command> Actions { get; set; }
        public Dictionary<string, CommandGroup> CommandGroups { get; set; }

        public Script()
        {
            _maxPoints = 0;
            _switchSize = DefaultSwitchSize;
            Messages = new Dictionary<string, string>();
            Scenes = new Dictionary<string, Scene>();
            Actions = new Dictionary<string, Command>();
            CommandGroups = new Dictionary<string, CommandGroup>();
        }

        public string FindMessage(string key)
        {
            string template = "[Missing message: `{0}`]";
            string defaultValue = string.Format(template, key);
            if (Messages.TryGetValue(key, out var messageValue))
            {
                return messageValue;
            }
            return defaultValue;
        }
    }
}
