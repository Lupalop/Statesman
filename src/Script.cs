using Statesman.Commands;
using System.Text.RegularExpressions;

namespace Statesman
{
    public class Script
    {
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

        public Dictionary<string, string> Messages { get; }
        public Dictionary<string, string> SubstitutedMessages { get; }
        public Dictionary<string, Scene> Scenes { get; }
        public Dictionary<string, Command> Actions { get; }
        public Dictionary<string, CommandGroup> CommandGroups { get; }

        public Script()
        {
            _maxPoints = 0;
            Messages = new Dictionary<string, string>();
            SubstitutedMessages = new Dictionary<string, string>();
            Scenes = new Dictionary<string, Scene>();
            Actions = new Dictionary<string, Command>();
            CommandGroups = new Dictionary<string, CommandGroup>();
        }

        public string FindMessage(string key, bool replaceMissing = true)
        {
            if (SubstitutedMessages.TryGetValue(key, out var messageValue))
            {
                return messageValue;
            }
            else if (Messages.TryGetValue(key, out messageValue))
            {
                bool unescapeString = key.StartsWith("@");
                if (unescapeString)
                {
                    messageValue = messageValue.Replace("\\e", "\u001b");
                }
                // Try to replace C-style format specifiers with C#-compatible ones.
                SubstituteCFormatString(messageValue, out messageValue);
                SubstitutedMessages.Add(key, messageValue);
                return messageValue;
            }
            else if (replaceMissing)
            {
                string template = "[Missing message: `{0}`]";
                string defaultValue = string.Format(template, key);
                return defaultValue;
            }
            // Check if there's anything to substitute in the given key.
            SubstituteCFormatString(key, out messageValue);
            SubstitutedMessages.Add(key, messageValue);
            return messageValue;
        }

        private static bool SubstituteCFormatString(string messageValue, out string newValue)
        {
            newValue = messageValue;
            string pattern = @"%(\d+(\.\d+)?)?(?<Type>d|f|n|s)";
            // The given string doesn't have any known format specifiers.
            if (!Regex.IsMatch(messageValue, pattern))
            {
                return false;
            }

            int argumentIndex = 0;
            newValue = Regex.Replace(messageValue, pattern, matches =>
            {
                // XXX: we're effectively discarding the subspecifiers here.
                var type = matches.Groups["Type"].Value;
                switch (type)
                {
                    case "d":
                    case "f":
                    case "s":
                        return string.Concat("{", argumentIndex++, "}");
                    case "n":
                        return "\n";
                    default:
                        return matches.Value;
                }
            });

            return true;
        }
    }
}
