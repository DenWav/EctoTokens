package com.demonwav.ectotokens.commands;

class CommandAlreadyDefinedException extends RuntimeException {

    public CommandAlreadyDefinedException(String cmd) {
        super(cmd + " has already been defined.");
    }
}
