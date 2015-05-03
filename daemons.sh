#!/bin/bash

SESSION=$USER

# Test if the session has windows
function is_closed(){
    n=$(tmux ls 2> /dev/null | grep "^$sess" | wc -l)
    [[ $n -eq 0 ]]
}

function split_window() {
    byobu-tmux split-window -v;
    byobu-tmux select-layout tiled
}

function make_pane() {
    id=$1;
    cmd=$2;
    byobu-tmux select-pane -t "$id"
    byobu-tmux send-keys "$cmd" C-m
}

function attach_session() {
    id=$1;
    byobu-tmux -2 attach-session -t $SESSION:"$id"
}


# Attach to existing session or create a new one
if ! is_closed; then
    attach_session 0
else
    # -2: forces 256 colors,
    byobu-tmux -2 new-session -d -s $SESSION

    # Start window for daemons
    byobu-tmux rename-window -t $SESSION:0 'daemons'

    split_window
    make_pane 0 "zkServer.sh start-foreground";
    make_pane 1 "storm nimbus";

    split_window
    make_pane 2 "storm supervisor";

    split_window
    make_pane 3 "storm drpc";

    split_window
    make_pane 4 "storm ui";

    # Set default window
    byobu-tmux select-window -t $SESSION:0

    # Attach to new session, flip between windows using m-l, m-r
    attach_session 0
fi

# The session is now either closed or detatched
if is_closed; then
    # Perform cleanup here as needed
    :
fi
