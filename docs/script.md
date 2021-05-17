# Statesman Script Language

## Scenes
Scenes are the primary means of facilitating user interaction in Statesman. A scene usually contains one or more of the following section blocks: messages, actions, and command groups.

A scene always starts with the keyword `scene` followed by the name of the scene, and is terminated with the `end` keyword. The scene name is **case sensitive** and does not allow spaces. Longer scene names can be achieved by using delimiters except space (i.e. `very_very_very_long_scene_name`). A scene with the name `home` can be written as:

```
scene home
    // Section blocks can be placed here.
end
```

## Messages
Messages are strings that are linked to a key and are used by the several available print commands. Message section blocks can be placed either inside or outside of scenes, and regardless of their placement, messages inside these blocks are always global (can be used by all scenes). The messages themselves can only be placed inside a message section block, which always starts with the keyword `messages`.

### Message section block (globaL)
```
messages
    test|This is a test message.
    bored|Are you bored yet?
end
```

### Message section block (local/inside a scene)
```
scene home
    messages
        test|This is a test message.
        bored|Are you bored yet?
    end
end
```

## Actions
Actions are commands associated with an input or a set of inputs. Inputs are the text that you type in the game's console. Only one command can be associated with an action, but you can use the `goto` command to reference a command group which can execute multiple commands. Action section blocks can be placed either inside or outside of scenes, and their placement affects which scenes can access them. Action section blocks that are outside of scenes (global) apply to all scenes, while section blocks that are placed inside a scene only affects that individual scene. Local action section blocks can override or replace the behavior of actions defined in global action section blocks. An action section block always starts with the `actions` keyword.

### Action section block (global)
```
actions
    talk|print,test
    walk|print,bored
end
```

### Action section block (local/inside a scene)
```
scene home
    actions
        talk|print,test
        walk|print,bored
    end
end
```

## Command Groups
Command groups, as the name implies, contain a set of commands that are executed sequentially or as the way they are ordered in the data file. They are usually called and executed using the `goto` command that is associated with an action. Conditional sections can be placed inside command groups to apply conditional behavior, but the conditional jump (`cjmp`) can also be used if you prefer that format. Command group section blocks can be placed either inside or outside of scenes, and their placement affects which scenes can access them. Command group section blocks that are outside of scenes (global) can be referenced by all scenes, while section blocks that are placed inside a scene can only be accessed by that individual scene. Local command group section blocks can override or replace global command groups in an individual scene's scope. A command group section block always starts with the `group` keyword.

### Command group section block (global)
```
group i_am_global
    print,test
    print,bored
    print,test
    print,bored
    print,test
    print,bored
end
```

### Command group section block (local/inside a scene)
```
scene home
    group walk
        print,walk
        print,walking
        print,walked
    end
end
```

### Command group section block (global overriden inside a scene)
```
group i_am_global
    print,test
    print,bored
    print,test
    print,bored
    print,test
    print,bored
end

scene home
    group i_am_global
        print,bored
        print,test
    end
end
```

### Command group section block (if-style conditional)
```
group wave
    // Checks if switch #100 is TRUE
    if 100
        // Checks if switch #101 is FALSE
        if !101
            // Print message with a key of `9`
            print,9
            // Sets switch #101 to TRUE
            set,101,true
        // If switch 101 is TRUE, execute commands below
        else
            // Print message with a key of `10`
            print,10
        end
    // If switch 100 is FALSE, execute commands below
    else
        // Print message with a key of `11`
        print,11
    end
end
```

### Command group section block (conditional jump)
```
group view
    // Conditional jump, check value of switch #2, if TRUE jump to line 1, if FALSE jump to line 3
    0|cjmp,2,1,3
    // Print message with a key of `nothing2`
    1|print,nothing2
    // RETURN (meaning stop execution of current block)
    2|ret
    // Set switch #2 to TRUE
    3|set,2,true
    // Print message with a key of `nothing`
    4|print,nothing
end
```

## Commands

### Commands that can be used anywhere (action or inside command group):

#### `goto`
- Executes commands inside a command group.
- Accepts only one argument: command group name (`string`).
- Example: `goto,view`
#### `print`
- Prints the message to console.
- Accepts only one argument: message key or inline message (`string`).
- Example (key): `print,1`, `print,test`
- Inline messages MUST NOT use commas aside from the initial one used to separate it from the keyword.
- Example (inline): `print,1`, `print,This is an inline message!`
- INVALID: `print,this,is,an,inline,message`
#### `printr`
- Randomly prints one of the provided messages to console.
- Accepts many arguments: message keys or inline messages separated by commas (`string`).
- Same rules in `print` apply, but comma can be used to separate multiple keys or inline messages.
- Example (key): `printr,f_1,f_2,f_3`
- Example (inline): `printr,Inline message 1,Inline message 2,Inline message 3`
#### `printc`
- Prints all provided messages to console.
- Accepts many arguments: message keys or inline messages separated by commas (`string`).
- Same rules in `print` apply, but comma can be used to separate multiple keys or inline messages.
- Example (key): `printc,f_1,f_2,f_3`
- Example (inline): `printc,Inline message 1,Inline message 2,Inline message 3`
#### `set`
- Sets a single switch to a boolean value.
- Accepts two arguments: switch number (`int`) and new value (`boolean`).
- Example: `set,1,true`, `set,1,false`
#### `scene`
- Changes the current scene to the provided scene.
- Accepts only one argument: scene name (`string`).
- Example: `scene,initial`, `scene,north`
#### `quit`
- Exits the application.
- Does NOT accept any arguments.

### Commands that can only be used in command groups:

#### `jmp`
- Moves execution to the provided line.
- Accepts only one argument: line number starting from zero (`int`).
- Example: `jmp,1`, `jmp,100`
#### `cjmp`
- Moves execution to the provided line depending on the provided switch's value.
- Accepts three arguments: switch number (`int`), line to jump if true (`int`), line to jump if false (`int`).
- Example: `cjmp,1,1,5`, `cjmp,2,5,1`
#### `ret`
- Stops execution of the current block and returns to the parent block.
- Does NOT accept any arguments.
#### `cond`
- This is a **reserved** command used in allowing if-style conditional blocks.
- Do NOT use this under any circumstances.
