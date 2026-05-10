package com.players.core.channel;

public abstract class AbstractChannel implements MessageChannel{
    private final String name;

    protected AbstractChannel(String name) {
        if(name == null || name.isBlank())
            throw new IllegalArgumentException("Channel shouldn't be blank");
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AbstractChannel{" +
                "name='" + name + '\'' +
                '}';
    }
}
