package ru.alex9127.app;

import com.google.gson.Gson;

import org.junit.Test;

import ru.alex9127.app.classes.*;
import ru.alex9127.app.saving.CompactTerrain;
import ru.alex9127.app.terrain.Terrain;
import ru.alex9127.app.terrain.Tree;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void terrainCompactionTest() {
        Gson gson = new Gson();
        for (int i = 0; i < 50; i++) {
            Terrain terrain = new Terrain(128, 1, "", 0, 0, 0, 0, "common");
            CompactTerrain compactTerrain = new CompactTerrain(terrain);
            Terrain t = new Terrain(compactTerrain);
            String s1 = gson.toJson(terrain);
            String s2 = gson.toJson(t);
            for (int j = 0; j < Math.min(s1.length(), s2.length()); j++) {
                if (s1.charAt(j) != s2.charAt(j)) {
                    System.out.println(j + ":");
                    System.out.println(s1.substring(Math.max(0, j - 50), Math.min(s1.length(), j + 50)));
                    System.out.println(s2.substring(Math.max(0, j - 50), Math.min(s2.length(), j + 50)));
                    break;
                }
            }
        }
    }

    @Test
    public void treeCompactionTest() {
        Gson gson = new Gson();
        Tree<Terrain> tree1 = new Tree<>(new Terrain(128, 1, "", 0, 0, 0, 0, "common"));
        Terrain terrain = new Terrain(128, 1, "", 0, 0, 0, 0, "common");
        Terrain terrain2 = new Terrain(128, 1, "", 0, 0, 0, 0, "common");
        tree1.addChild(0, terrain);
        tree1.addChild(1, terrain2);
        Tree<CompactTerrain> t = Tree.compact(tree1);
        Tree<Terrain> tree2 = Tree.restore(t);
        String s1 = gson.toJson(tree1);
        String s2 = gson.toJson(tree2);
        for (int j = 0; j < Math.min(s1.length(), s2.length()); j++) {
            if (s1.charAt(j) != s2.charAt(j)) {
                System.out.println(j + ":");
                System.out.println(s1.substring(Math.max(0, j - 50), Math.min(s1.length(), j + 50)));
                System.out.println(s2.substring(Math.max(0, j - 50), Math.min(s2.length(), j + 50)));
                break;
            }
        }
    }
}