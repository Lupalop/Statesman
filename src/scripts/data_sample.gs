/*
// This line should be ignored by the parser
*/

maxpoints 200
// deprecated
switches 3000

// Global messages
string
    // Fallback
    f_1|I don't understand that!
    f_2|I don't know that word.
    f_3|What?
    f_4|You're confusing!
    // Talk
    t_1|There's nobody in here.
    t_2|There's no one to talk to.
    // Walk
    w_1|Where?
    w_2|May I know which direction?
    // Inventory
    i_1|This item is already in your inventory!
    i_2|This item is NOT in your inventory!
    i_3|You only have one item in your inventory:
    i_4|You have %s items in your inventory:%n
    i_5|%s: %s%n
    i_6|Your inventory is empty!
    // Points
    p_1|You currently have %s of %s points.%n
    // Save/Load Game
    sl_1|Enter the name of your saved game:
    sl_2|Invalid name.
    sl_3|Name too long! Try a shorter name for your saved game.
    sl_4|The specified saved game was not found.
    sl_5|Your game has been loaded!
    sl_6|Your game cannot be saved.
    sl_7|Your game has been saved!
end

// Test messages
string
    // Dummies
    dummy|Dummy message.
    junk|You are a piece of junk too!
    otherjunk|You are a huge steaming pile of junk, man!
end

// Global actions
action
    fallback|printr,f_1,f_2,f_3,f_4
    junk|print,junk
    junktest|call,junk
    talk|printr,t_1,t_2
    walk|printr,w_1,w_2
    quit|quit
    inventory,check inventory,get inventory,inv,check inv,get inv|inv,list
    invclear|inv,clear
    points+5|points,add,5
    points-5|points,sub,5
    points200|points,set,200
    pointsc|points,clear
    points|points,list
end

// Global functions
function junk
    sjmp,0,1,3
    print,otherjunk
    ret
    set,0,true
    print,junk
end

// Scene: initial (called upon start-up)
scene initial
    // Messages
    string
        0|Welcome to the year 1914, situated in the Philippines, a colony of the United States of America. You are currently standing in front of a branch of the Intercontinental Bank, holding a leather briefcase containing thousands of pesos. You were instructed by your manager to deposit a large sum of money to a private account in another bank. What are you going to do now?%n
        1|You are talking to someone now.
        2|You've picked up a rock.%nA person is approaching you from far away, waving.
        3|After waving back, the person ran towards you.%n"Don't you remember me?" He asked.
        4|"You've got to be kidding!" He answered back, taking something from their pockets.
        5|There's no one to talk to anymore. He left!
        6|You picked the rock from the filthy ground.
        7|You've already picked the rock
        8|There's no rock to be picked (yet!).
        9|You've waved back at the person.
        10|You've already waved at that person!
        11|Who are you waving at?
        12|You screamed, "No!"
        13|You've already said no, weirdo.
    end
    
    // Intro
    function $
        print,0
    end
    
    // Actions
    action
        look,view,see|call,view
        #look,view,see|print,0
        take rock|print,2
        wave back|print,3
        no|print,4
        dummy|print,dummy
        random|printr,1,2
        combine|printc,1,2,3,4
        walk north, walk n|scene,north
        
        // Global function test
        junked|call,junk
        
		// Sample convo
        talk|call,conversation
        pick rock|call,pick_rock
        wave|call,wave
        no|call,response_no
    end

    // Function: view
    function view
        // conditional jump, switch #, line if true, line if false
        sjmp,1,1,3
        print,0
        ret
        set,1,true
        print,dummy
    end

    /*
    // Sample conversation
    */
    function conversation
        if !100
            print,1
            set,99,true
        end
        if !101 && 100
            print,2
        end
        if !102 && 101
            print,3
        end
        if !103 && 102
            print,4
            set,103,true
        end
        if 100 && 101 && 102 && 103
            print,5
        end
    end
    
    function pick_rock
        if 99
            if !i:rock
                inv,add,rock
                points,add,10
                print,6
                set,100,true
            else
                print,7
            end
        else
            print,8
        end
    end
    
    function wave
        if 100
            if !101
                print,9
                points,add,10
                set,101,true
            else
                print,10
            end
        else
            print,11
        end
    end
    
    function response_no
        if 100 && 101
            if !102
                print,12
                points,add,10
                set,102,true
            else
                print,13
            end
        else
            printr,t_1,t_2
        end
    end
    
    action
        rocka|call,inventory_test
        rockb|call,inventory_test_inverted
        rockadd|inv,add,rock
        rockrm|inv,rm,rock
    end

    item
        rock|A dirty rock picked from somewhere. Hmm?
        paper|This is a paper
        pen|This is a pen
    end
    
    function inventory_test
        if i:rock
            print,Rock is in your inventory!
        else
            print,There's no rock!
        end
    end

    function inventory_test_inverted
        if !i:rock
            print,There's no rock!
        else
            print,Rock is in your inventory!
        end
    end

    action
        pp|call,paper_pen
        penadd|inv,add,pen
        penrm|inv,rm,pen
        paperadd|inv,add,paper
        paperrm|inv,rm,paper
    end
    
    function paper_pen
        print,Individual conditions:%n
        if i:paper
            print,we have paper
        end
        if !i:paper
            print,no paper here
        end
        if i:pen
            print,we have a pen
        end
        if !i:pen
            print,no pen here
        end
        print,%nAND/OR conditions:%n
        if i:paper && i:pen
            print,paper AND pen
        end
        if i:paper || i:pen
            print,paper OR pen
        end
        if i:pen && i:paper
            print,pen AND paper
        end
        if i:pen || i:paper
            print,pen OR paper
        end
        print,%nNOT AND conditions:%n
        if !i:paper && !i:pen
            print,NOT paper AND NOT pen
        end
        if !i:pen && !i:paper
            print,NOT pen AND NOT paper
        end
        print,%nNOT OR conditions:%n
        if !i:paper || !i:pen
            print,NOT paper OR NOT pen
        end
        if !i:pen || !i:paper
            print,NOT pen OR NOT paper
        end
        print,%nNOT AND HAS conditions:%n
        if !i:paper && i:pen
            print,NOT paper AND HAS pen
        end
        if !i:pen && i:paper
            print,NOT pen AND HAS paper
        end
        print,%nHAS AND NOT conditions:%n
        if i:paper && !i:pen
            print,HAS paper AND NOT pen
        end
        if i:pen && !i:paper
            print,HAS pen AND NOT paper
        end
        print,%nNOT OR HAS conditions:%n
        if !i:paper || i:pen
            print,NOT paper OR HAS pen
        end
        if !i:pen || i:paper
            print,NOT pen OR HAS paper
        end
        print,%nHAS OR NOT conditions:%n
        if i:paper || !i:pen
            print,HAS paper OR NOT pen
        end
        if i:pen || !i:paper
            print,HAS pen OR NOT paper
        end
    end
end

// Scene: north
scene north
    string
        nothing|There's nothing here.
        nothing2|THERE IS NOTHING! I REPEAT, THERE IS NOTHING!
    end

    action
        look,view,see|call,view
        walk south,walk s|scene,initial
    end

    // Function: view
    function view
        // conditional jump, switch #, line if true, line if false
        sjmp,2,1,3
        print,nothing2
        ret
        set,2,true
        print,nothing
    end

    // Overriden global function test: junk
    function junk
        print,this is an inline message
        print,this message is inline
        print,this message is not in the source
        print,nothing
        print,nothing2
    end
end
