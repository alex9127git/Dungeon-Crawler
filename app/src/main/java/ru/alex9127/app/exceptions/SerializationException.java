package ru.alex9127.app.exceptions;

import androidx.annotation.Nullable;

public class SerializationException extends Exception {
    private final String msg;

    public SerializationException(String msg) {
        this.msg = msg;
    }

    @Nullable
    @Override
    public String getMessage() {
        return msg;
    }
}
