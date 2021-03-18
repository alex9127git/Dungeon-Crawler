package ru.alex9127.app.classes;

import java.util.ArrayList;

import ru.alex9127.app.interfaces.TerrainLike;

public class Pathfinder {
    private static int[][] info;
    public static int[][] findPath(TerrainLike terrain, Entity start, Entity end) {
        info = initializePathFind(terrain, start, end);
        return calculatePath(end);
    }

    private static int[][] calculatePath(Entity end) {
        int step = 0;
        do {
            step++;
            for (int y = 0; y < info.length; y++) {
                for (int x = 0; x < info[0].length; x++) {
                    if (info[y][x] == step - 1) {
                        try {
                            info[y][x + 1] = info[y][x + 1] < 0 && info[y][x + 1] != -100 ? step : info[y][x + 1];
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                        try {
                            info[y][x - 1] = info[y][x - 1] < 0 && info[y][x - 1] != -100 ? step : info[y][x - 1];
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                        try {
                            info[y + 1][x] = info[y + 1][x] < 0 && info[y + 1][x] != -100 ? step : info[y + 1][x];
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                        try {
                            info[y - 1][x] = info[y - 1][x] < 0 && info[y - 1][x] != -100 ? step : info[y - 1][x];
                        } catch (ArrayIndexOutOfBoundsException ignored) {}
                    }
                }
            }
        } while (!targetFound(info) && haveFreeCells(info));
        if (targetFound(info)) {
            int[][] path = new int[step + 1][2];
            path[step] = new int[] {end.getX(), end.getY()};
            do {
                step--;
                ArrayList<int[]> possibleTiles = new ArrayList<>();
                for (int y = 0; y < info.length; y++) {
                    for (int x = 0; x < info[0].length; x++) {
                        if (info[y][x] == step && distance(x, y, path[step + 1][0], path[step + 1][1]) == 1) {
                            possibleTiles.add(new int[] {x, y});
                        }
                    }
                }
                path[step] = possibleTiles.get((int) (Math.random() * possibleTiles.size()));
            } while (step > 0);
            return path;
        }
        else return new int[][] {{-1, -1}};
    }

    private static int[][] initializePathFind(TerrainLike terrain, Entity start, Entity end) {
        int[][] info = new int[terrain.getSize()][terrain.getSize()];
        for (int y = 0; y < info.length; y++) {
            for (int x = 0; x < info[0].length; x++) {
                info[y][x] = terrain.getBlock(x, y).getType().endsWith("floor") ? -1 : -100;
                if (x == start.getX() && y == start.getY()) {
                    info[y][x] = 0;
                }
                if (x == end.getX() && y == end.getY()) {
                    info[y][x] = -2;
                }
            }
        }
        return info;
    }

    private static boolean targetFound(int[][] info) {
        for (int[] row : info) {
            for (int x:row) {
                if (x == -2) return false;
            }
        }
        return true;
    }

    private static boolean haveFreeCells(int[][] info) {
        for (int[] row : info) {
            for (int x:row) {
                if (x == 0) return true;
            }
        }
        return false;
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2) +
                Math.abs(y1 - y2) * Math.abs(y1 - y2));
    }
}
