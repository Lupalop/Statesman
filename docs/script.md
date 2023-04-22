# Statesman Script Language

## Scenes
Scenes are the primary means of facilitating user interaction in Statesman. A scene usually contains one or more of the following section blocks: messages, actions, and functions.

A scene always starts with the keyword `scene` followed by the name of the scene, and is terminated with the `end` keyword. The scene name is **case sensitive** and does not allow spaces. Longer scene names can be achieved by using delimiters except space (i.e. `very_very_very_long_scene_name`). A scene with the name `home` can be written as:

```
scene home
    // Section blocks can be placed here.
end
```

## Strings
Strings can be accessed using a key and are used by the several available print commands. String section blocks can be placed either inside or outside of scenes, and regardless of their placement, strings inside these blocks are always global (can be used by all scenes). The strings themselves can only be placed inside a string section block, which always starts with the keyword `string`.

### String section block (global)
```
string
    test|This is a test message.
    bored|Are you bored yet?
end
```

### String section block (local/inside a scene)
```
scene home
    string
        test|This is a test message.
        bored|Are you bored yet?
    end
end
```

## Actions
Actions are commands associated with an input or a set of inputs. Inputs are the text that you type in the game's console. Only one command can be associated with an action, but you can use the `call` command to invoke a function which can execute multiple commands. Action section blocks can be placed either inside or outside of scenes, and their placement affects which scenes can access them. Action section blocks that are outside of scenes (global) apply to all scenes, while section blocks that are placed inside a scene only affects that individual scene. Local action section blocks can override or replace the behavior of actions defined in global action section blocks. An action section block always starts with the `action` keyword.

### Action section block (global)
```
action
    talk|print,test
    walk|print,bored
end
```

### Action section block (local/inside a scene)
```
scene home
    action
        talk|print,test
        walk|print,bored
    end
end
```

## Functions
Functions contain a set of commands or instructions that are executed sequentially or as the way they are ordered in the game script. They are usually called and executed using the `call` command that is associated with an action. Conditional blocks of code can be also placed inside functions to apply conditional behavior, but the conditional jump [`cjmp`] can also be used if you prefer that format. Functions can be placed either outside or inside of scenes, and their placement affects which scenes can access them. Functions that are outside of scenes (global) can be referenced by all scenes, while functions that are placed inside a scene can only be accessed by that individual scene. Local functions can override or replace global functions in an individual scene's scope. A function block always starts with the `function` keyword.

### Function section block (global)
```
function i_am_global
    print,test
    print,bored
    print,test
    print,bored
    print,test
    print,bored
end
```

### Function section block (local/inside a scene)
```
scene home
    function walk
        print,walk
        print,walking
        print,walked
    end
end
```

### Function section block (global overriden inside a scene)
```
function i_am_global
    print,test
    print,bored
    print,test
    print,bored
    print,test
    print,bored
end

scene home
    function i_am_global
        print,bored
        print,test
    end
end
```

### Function section block (if-style conditional)
```
function wave
    // Checks if switch no. 100 is TRUE
    if 100
        // Checks if switch no. 101 is FALSE
        if !101
            // Print message with a key of `9`
            print,9
            // Sets switch no. 101 to TRUE
            set,101,true
        // If switch no. 101 is TRUE, execute commands below
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

### Function section block (if-style conditional - inventory)
```
function check_rock
    // Checks if the item "rock" exists in the global inventory
    if i:rock
        // Print message with a key of `9`
        print,9
    // If the item "rock" is NOT in the global inventory
    else
        // Print message with a key of `11`
        print,11
    end
end
```

### Function section block (conditional jump)
```
function view
    // Conditional jump, check value of switch #2, if TRUE jump to line 1, if FALSE jump to line 3
    cjmp,2,1,3
    // Print message with a key of `nothing2`
    print,nothing2
    // RETURN (meaning stop execution of current block)
    ret
    // Set switch #2 to TRUE
    set,2,true
    // Print message with a key of `nothing`
    print,nothing
end
```

## Preference tags
Preference tags are optional and follow a key-value pair format.

### `maxpoints`
This preference sets the highest number of points that a player can achieve in-game. This must appear **outside** section blocks (`scene` and others). If this section tag is not set, this defaults to zero (0).
```
maxpoints 200
```

### `switches` (deprecated)
This preference sets the number of switches that is allocated by the game's interpreter. This must appear **outside** section blocks (`scene` and others). If this section tag is not set, this defaults to two thousand (2000). This has no effect anymore since the size restriction has been removed in the latest update.
```
switches 5000
```

## Comments
Statesman allows both single-line and block comments, with the following syntax:

### Single-line comments
```
// This is a comment
// This is a comment (another)
// This is a comment (and another one)
```

### Block comments
```
/*
 * This is a block comment.
 * This line is ignored by the parser.
 * This one too!
 */
```

### Notes
The start tag for block comments [`/*`] must appear as the first sequence (white space is excluded). The end tag for block comments [`*/`] must appear either as the first or the last sequence and MUST NOT be placed in between.

The following locations for comments are not allowed:
```
/* This is not allowed */
 blabla /* This is not allowed */
 bla /*
 This is not allowed
 */ bla
 
group test // This comment is not allowed
    messages
        test|// This comment is treated as a message and is read by the parser.
    end
end
```

## Commands

### Commands that can be used anywhere (action or inside function):

#### `call`
- Invokes a function; executes commands contained by a function.
- Accepts only one argument: function name [`string`].
- Example: `call,view`
#### `print`
- Prints the message to console.
- Accepts only one argument: message key or inline message [`string`].
- Example (key): `print,1`, `print,test`
- Inline messages MUST NOT use commas aside from the initial one used to separate it from the keyword.
- Example (inline): `print,1`, `print,This is an inline message!`
- INVALID: `print,this,is,an,inline,message`
#### `printr`
- Randomly prints one of the provided messages to console.
- Accepts many arguments: message keys or inline messages separated by commas [`string`].
- Same rules in `print` apply, but comma can be used to separate multiple keys or inline messages.
- Example (key): `printr,f_1,f_2,f_3`
- Example (inline): `printr,Inline message 1,Inline message 2,Inline message 3`
#### `printc`
- Prints all provided messages to console.
- Accepts many arguments: message keys or inline messages separated by commas [`string`].
- Same rules in `print` apply, but comma can be used to separate multiple keys or inline messages.
- Example (key): `printc,f_1,f_2,f_3`
- Example (inline): `printc,Inline message 1,Inline message 2,Inline message 3`
#### `set`
- Sets a single switch to a boolean value.
- Accepts two arguments: switch number [`int`] and new value [`boolean`].
- Example: `set,1,true`, `set,1,false`
#### `inv`
- Lists all the items in the global inventory, adds or removes an item, and clearing the inventory's contents.
- The item MUST be present in the current scene's `items` section **only adding items**.
- The item MUST be present in the **global inventory** when **removing items**, otherwise a message will be shown stating that the item does not exist in the inventory.
- Accepts one to two (1-2) arguments: action [`add` for adding an item, `rm` for removing an item, `list` for showing all the items, and `clear` for removing all the items] and item name [`string`] (required for `add` and `rm` actions, otherwise optional).
- Example: `inv,add,rock`, `inv,rm,rock`, `inv,list`, `inv,clear`
#### `points`
- Adds, subtracts, sets to a specific value, shows all points, and resets the points to zero.
- The specific value that will be added, subtracted, or set to, MUST NOT be zero.
- Accepts one to two (1-2) arguments: action [`add` for adding points, `sub` for subtracting points, `set` for setting the points to a specific value, `list` for showing all the points, and `clear` for resetting the points to zero] and specific value [`int`] (required for `add`, `sub`, and `set` actions, otherwise optional).
- Example: `points,add,5`, `points,sub,5`, `points,set,200`, `points,list`, `points,clear`
#### `scene`
- Changes the current scene to the provided scene.
- Accepts only one argument: scene name [`string`].
- Example: `scene,initial`, `scene,north`
#### `quit`
- Exits the application.
- Does NOT accept any arguments.
- Example: `quit`

### Commands that can only be used inside functions:

#### `jmp`
- Moves execution to the provided line.
- Accepts only one argument: line number starting from zero [`int`].
- Example: `jmp,1`, `jmp,100`
#### `sjmp`
- Moves execution to the provided line depending on the provided switch's value.
- Accepts three arguments: switch number [`int`], line to jump if true [`int`], line to jump if false [`int`].
- Example: `sjmp,1,1,5`, `sjmp,2,5,1`
#### `ijmp`
- Moves execution to the provided line depending on the presence of an item in the global inventory
- Accepts three arguments: item name [`string`], line to jump if present [`int`], line to jump if absent [`int`].
- Example: `ijmp,rock,1,5`, `ijmp,pen,5,1`
#### `ret`
- Stops execution of the current block and returns to the parent block.
- Does NOT accept any arguments.
