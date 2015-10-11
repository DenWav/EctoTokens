package com.demonwav.ectotoken.commands;

class CommandAlreadyDefinedException extends RuntimeException {

    public CommandAlreadyDefinedException(String cmd) {
        super(cmd + " has already been defined.");
    }
}
