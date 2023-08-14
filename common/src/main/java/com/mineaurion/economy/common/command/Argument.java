package com.mineaurion.economy.common.command;

public class Argument {

    private final String name;
    private final boolean required;
    private final String description;

    public Argument(String name, boolean required, String description){
        this.name = name;
        this.required = required;
        this.description = description;
    }

    public String getName(){
        return this.name;
    }

    public boolean isRequired(){
        return this.required;
    }

    public String getDescription(){
        return this.description;
    }
}
