using Statesman.Commands;
using System;
using System.Collections.Generic;

namespace Statesman
{
    public class Script
    {
        public static readonly int DEFAULT_SWITCH_SIZE = 2000;

        private int _maxPoints;
        private int _switchSize;
        private Dictionary<string, string> _messages;
        private Dictionary<string, Scene> _scenes;
        private Dictionary<string, Command> _actions;
        private Dictionary<string, CommandGroup> _commandGroups;

        public Script()
        {
            _maxPoints = 0;
            _switchSize = DEFAULT_SWITCH_SIZE;
            _messages = new Dictionary<string, string>();
            _scenes = new Dictionary<string, Scene>();
            _actions = new Dictionary<string, Command>();
            _commandGroups = new Dictionary<string, CommandGroup>();
        }

        public int getMaxPoints()
        {
            return _maxPoints;
        }

        public void setMaxPoints(int maxPoints)
        {
            if (maxPoints < 0)
            {
                throw new ArgumentException("The maximum number of points must be greater than or equal to zero");
            }
            _maxPoints = maxPoints;
        }

        public int getSwitchSize()
        {
            return _switchSize;
        }

        public void setSwitchSize(int switchSize)
        {
            if (switchSize < 0)
            {
                throw new ArgumentException("The number of allocated switches must be greater than or equal to zero");
            }
            _switchSize = switchSize;
        }

        public Dictionary<string, string> getMessages()
        {
            return _messages;
        }

        public void setMessages(Dictionary<string, string> messages)
        {
            _messages = messages;
        }

        public Dictionary<string, Scene> getScenes()
        {
            return _scenes;
        }

        public void setScenes(Dictionary<string, Scene> scenes)
        {
            _scenes = scenes;
        }

        public Dictionary<string, Command> getActions()
        {
            return _actions;
        }

        public void setActions(Dictionary<string, Command> actions)
        {
            _actions = actions;
        }

        public Dictionary<string, CommandGroup> getCommandGroups()
        {
            return _commandGroups;
        }

        public void setCommandGroups(Dictionary<string, CommandGroup> commandGroups)
        {
            _commandGroups = commandGroups;
        }

        public string getMessage(string key)
        {
            string template = "[Missing message: `{0}`]";
            string defaultValue = string.Format(template, key);
            if (getMessages().TryGetValue(key, out var messageValue))
            {
                return messageValue;
            }
            return defaultValue;
        }
    }
}
