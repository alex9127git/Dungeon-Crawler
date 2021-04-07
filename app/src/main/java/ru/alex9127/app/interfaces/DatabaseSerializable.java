package ru.alex9127.app.interfaces;

import ru.alex9127.app.exceptions.SerializationException;

public interface DatabaseSerializable {
    String serialize();
    DatabaseSerializable deserialize(String serialized) throws SerializationException;
}
