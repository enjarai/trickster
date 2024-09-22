#!/usr/bin/env bash

# thanks to https://github.com/Pathoschild/SMAPI/blob/ab34b6142dcd04e629012a1c30d37bc9c7e5df5e/src/SMAPI.Installer/assets/unix-launcher.sh#L95-L147
run() {
    COMMAND="./gradlew runClient --debug-jvm"
    
	# select terminal (prefer xterm for best compatibility, then known supported terminals)
	for terminal in xterm gnome-terminal kitty terminator xfce4-terminal konsole terminal termite alacritty mate-terminal x-terminal-emulator; do
	    if command -v "$terminal" 2>/dev/null; then
	        export TERMINAL_NAME=$terminal
	        break;
	    fi
	done

	# find the true shell behind x-terminal-emulator
	if [ "$TERMINAL_NAME" = "x-terminal-emulator" ]; then
	    TERMINAL_NAME="$(basename "$(readlink -f "$(command -v x-terminal-emulator)")")"
	    export TERMINAL_NAME
	fi

	# run in selected terminal and account for quirks
	TERMINAL_PATH="$(command -v "$TERMINAL_NAME")"
	export TERMINAL_PATH
	if [ -x "$TERMINAL_PATH" ]; then
	    case $TERMINAL_NAME in
	        terminal|termite)
	            # consumes only one argument after -e
	            # options containing space characters are unsupported
	            "$TERMINAL_NAME" -e "$COMMAND" 1>/dev/null 2>&1 &
	            ;;

	        xterm|konsole|alacritty)
	            # consumes all arguments after -e
	            "$TERMINAL_NAME" -e $COMMAND 1>/dev/null 2>&1 &
	            ;;

	        terminator|xfce4-terminal|mate-terminal)
	            # consumes all arguments after -x
	            "$TERMINAL_NAME" -x $COMMAND 1>/dev/null 2>&1 &
	            ;;

	        gnome-terminal)
	            # consumes all arguments after --
	            "$TERMINAL_NAME" -- $COMMAND 1>/dev/null 2>&1 &
	            ;;

	        kitty)
	            # consumes all trailing arguments
	            "$TERMINAL_NAME" $COMMAND 1>/dev/null 2>&1 &
	            ;;

	        *)
	            exit -1
	    esac
	else
	    exit -1
	fi
}

run
read -p "Press ENTER to debug" && jdb -attach 5005
