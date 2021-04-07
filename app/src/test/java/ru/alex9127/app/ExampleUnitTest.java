package ru.alex9127.app;

import org.junit.Test;

import ru.alex9127.app.classes.*;
import ru.alex9127.app.terrain.Terrain;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void serializationTest() {
        Terrain terrain = new Terrain(128, 0, new Unit("", 0, 0, 0, 0, 0, 0), "common");
        System.out.println(terrain.serialize());
    }
}