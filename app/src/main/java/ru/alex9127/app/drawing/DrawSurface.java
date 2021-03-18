package ru.alex9127.app.drawing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.view.*;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import ru.alex9127.app.R;
import ru.alex9127.app.classes.*;
import ru.alex9127.app.interfaces.*;

import static ru.alex9127.app.drawing.ImageManager.*;

@SuppressLint("ViewConstructor")
public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {
    private TerrainLike terrain = new Terrain(128);
    private final Unit unit;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private int xStart, yStart, yBlocks;
    public DrawThread drawer;
    private final Activity a;
    private int level = 1;
    private int enemiesKilled = 0;

    public DrawSurface(Context context, String name) {
        super(context);
        unit = new Unit(name, 100, 10, 0, 15,
                terrain.getSpawnPoint().getX(), terrain.getSpawnPoint().getY());
        a = (Activity) context;
        getHolder().addCallback(this);
        generateEnemies();
    }

    private void generateEnemies() {
        enemies.clear();
        boolean enemyPlaced;
        for (int i = 0; i < (level % 6 == 0 ? 1 : level); i++) {
            enemyPlaced = false;
            do {
                int enemyX = (int) (Math.random() * 128);
                int enemyY = (int) (Math.random() * 128);
                if (terrain.getBlockWalkable(enemyX, enemyY)) {
                    if (level % 6 != 0) {
                        if (level <= 6) {
                            enemies.add(new Enemy("SLIME",
                                    (int) (20 + (int) (Math.random() * 10) * (1 + level * 0.5)),
                                    (int) (4 + (int) (Math.random() * 2) * (1 + level * 0.5)),
                                    (int) (2 + (int) (Math.random() * 1) * (1 + level * 0.5)), 0,
                                    enemyX, enemyY, (30 + (int) (Math.random() * 20)) * level));
                        } else {
                            enemies.add(new Enemy("ZOMBIE",
                                    (int) (50 + (int) (Math.random() * 30) * (1 + level * 0.5)),
                                    (int) (10 + (int) (Math.random() * 5) * (1 + level * 0.5)),
                                    (int) (5 + (int) (Math.random() * 3) * (1 + level * 0.5)), 0,
                                    enemyX, enemyY, (70 + (int) (Math.random() * 50)) * level));
                        }
                    } else {
                        enemies.add(new Enemy("KING SLIME",
                                (int) (1000 + (int) (Math.random() * 500) * (1 + level * 0.5)),
                                (int) (20 + (int) (Math.random() * 10) * (1 + level * 0.5)),
                                (int) (0 + (int) (Math.random() * 0) * (1 + level * 0.5)), 0,
                                enemyX, enemyY, (1000 + (int) (Math.random() * 500)) * level));
                    }
                    enemyPlaced = true;
                }
            } while (!enemyPlaced);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        xStart = getTerrainDrawData()[0];
        yStart = getTerrainDrawData()[1];
        yBlocks = getTerrainDrawData()[2];
        generateImages(getResources(), getWidth(), getHeight(), yStart, yBlocks);
    }

    private void enemyAI() {
        for (Enemy e : enemies) {
            e.decide(unit, terrain);
            if (Pathfinder.distance(unit.getX(), unit.getY(), e.getX(), e.getY()) == 0 && e.alive()) {
                int dmg = -1 * (e.getAttackPower() - unit.getDefensePower());
                unit.changeHp(Math.min(dmg, 0));
            }
        }
    }

    public int[] getTerrainDrawData() {
        /*
        [0] = start X pos
        [1] = start Y pos
        [2] = number of available blocks by Y axis
        */
        return new int[] {getWidth() / 18,
                (getHeight() / 2) % ((getWidth() / 9) == 0 ? 1 : (getWidth() / 9)),
                getHeight() / ((getWidth() / 9) == 0 ? 1 : (getWidth() / 9))};
    }

    public void handleClick(int x, int y) {
        String s = "";
        if (!drawer.castingMagic) {
            if (!drawer.paused) {
                if (buttonDown.getBoundaryRect().contains(x, y)) {
                    s = unit.checkMove(0, 1, terrain, enemies.size());
                }
                if (buttonUp.getBoundaryRect().contains(x, y)) {
                    s = unit.checkMove(0, -1, terrain, enemies.size());
                }
                if (buttonLeft.getBoundaryRect().contains(x, y)) {
                    s = unit.checkMove(-1, 0, terrain, enemies.size());
                }
                if (buttonRight.getBoundaryRect().contains(x, y)) {
                    s = unit.checkMove(1, 0, terrain, enemies.size());
                }
                if (buttonMiniMap.getBoundaryRect().contains(x, y)) {
                    drawer.miniMapOpened = !drawer.miniMapOpened;
                }
                if (buttonAttack.getBoundaryRect().contains(x, y)) {
                    for (Enemy e : enemies) {
                        if (Pathfinder.distance(unit.getX(), unit.getY(), e.getX(), e.getY()) <= 2.0) {
                            e.changeHp(-1 * (unit.getAttackPower() - e.getDefensePower()));
                        }
                    }
                    enemyAI();
                }
                if (buttonMagic.getBoundaryRect().contains(x, y)) {
                    drawer.castingMagic = true;
                }
                if (new Rect((int) (getWidth() / 9 * 0.5), getHeight() / 2 - getWidth() / 9 * 4,
                        (int) (getWidth() / 9 * 8.5), getHeight() / 2 + getWidth() / 9 * 4).
                        contains(x, y)) {
                    if (drawer.miniMapOpened) {
                        drawer.compactMode = !drawer.compactMode;
                    }
                }
                if (s.startsWith("moved")) {
                    enemyAI();
                }
                if (terrain.getBlockConfig(unit.getX(), unit.getY()).equals("chest")) {
                    terrain.setBlockConfig(unit.getX(), unit.getY(), "none");
                    String str = "";
                    if (s.equals("atk")) {
                        str = "Found better weapon";
                    } else if (s.equals("def")) {
                        str = "Found better armor";
                    }
                    text.rewind();
                    TextImage t = text;
                    t.setText(str);
                    drawer.textImages.add(t);
                }
            }
            if (buttonPause.getBoundaryRect().contains(x, y)) {
                drawer.paused = !drawer.paused;
            }
        } else {
            drawer.castingMagic = false;
            if (unit.getManaPercentage() > 0) {
                int blockX = unit.getX() - 4;
                int blockY = unit.getY() - (int) Math.floor(yBlocks / 2.0);
                blockX += Math.round((double) x / unitOfLength);
                blockY += Math.round((double) (y - yStart) / unitOfLength);
                unit.changeMana(-1);
                for (Enemy e : enemies) {
                    if (Pathfinder.distance(e.getX(), e.getY(), blockX, blockY) < 2)
                        e.changeHp(-1 * (unit.getAttackPower() - e.getDefensePower()));
                }
                enemyAI();
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        drawer = new DrawThread(getHolder());
        drawer.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        drawer.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawer.join();
                retry = false;
            } catch (InterruptedException ignored) {}
        }
    }

    private class DrawThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private volatile boolean running = true;
        private boolean miniMapOpened = false, compactMode = false, paused = false, castingMagic = false;
        private final Paint red = new Paint();
        private final Paint darkGray = new Paint();
        private final Paint lightGray = new Paint();
        private final Paint darkBrown = new Paint();
        private final Paint lightBrown = new Paint();
        private final Paint gold = new Paint();
        private final Paint green = new Paint();
        private final Paint blue = new Paint();
        private final Paint purple = new Paint();
        private final Paint white = new Paint();
        private final ArrayList<TextImage> textImages = new ArrayList<>();
        private long currentTime = System.currentTimeMillis();
        private long runTime = 0;
        private long seconds = 0;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            darkGray.setColor(getResources().getColor(R.color.darkGray));
            lightGray.setColor(getResources().getColor(R.color.lightGray));
            darkBrown.setColor(getResources().getColor(R.color.darkBrown));
            lightBrown.setColor(getResources().getColor(R.color.lightBrown));
            red.setColor(getResources().getColor(R.color.red));
            gold.setColor(getResources().getColor(R.color.gold));
            green.setColor(getResources().getColor(R.color.green));
            blue.setColor(getResources().getColor(R.color.blue));
            purple.setColor(getResources().getColor(R.color.purple));
            white.setColor(getResources().getColor(R.color.white));
            white.setTextSize(50);
            white.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }

        public void requestStop() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        if (!paused) {
                            canvas.drawColor(Color.BLACK);
                            drawTerrain(canvas);
                            if (!castingMagic) drawButtons(canvas);
                            updateMiniMap(canvas);
                            checkUnitAlive();
                            checkNextLevel();
                            animationsUpdate();
                            textPopupsDraw(canvas);
                            timeCount();
                        } else {
                            canvas.drawColor(getResources().getColor(R.color.darkGray));
                            buttonPause.draw(canvas);
                            drawCompoundText(unit.stats(), buttonPause.getBoundaryRect().left,
                                    buttonPause.getBoundaryRect().bottom + unitOfLength, white, canvas);
                        }
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void timeCount() {
            runTime += (System.currentTimeMillis() - currentTime);
            currentTime = System.currentTimeMillis();
            if (((int) (runTime / 1000)) > seconds) {
                seconds++;
                if (seconds % 3 == 0 && unit.getManaPercentage() < 1)
                    unit.changeMana(1);
            }
        }

        private void checkUnitAlive() {
            if (!unit.alive()) {
                Intent i = new Intent();
                i.putExtra("Name", unit.getName());
                i.putExtra("Floors cleared", level - 1);
                i.putExtra("Unit level", unit.getLevel());
                i.putExtra("Enemies killed", enemiesKilled);
                a.setResult(Activity.RESULT_OK, i);
                a.finish();
            }
        }

        private void textPopupsDraw(Canvas canvas) {
            ArrayList<TextImage> t = new ArrayList<>();
            for (TextImage ti : textImages) {
                ti.update();
            }
            for (TextImage ti : textImages) {
                ti.draw(canvas, white);
                if (!ti.isDrawn()) t.add(ti);
            }
            for (TextImage ti : t) textImages.remove(ti);
        }

        private void drawCompoundText(String text, float x, float y, Paint paint, Canvas canvas) {
            float drawY = y;
            for (String s:text.split("\n")) {
                canvas.drawText(s, x, drawY, paint);
                drawY += paint.getTextSize() + 10;
            }
        }

        private void checkNextLevel() {
            if (unit.getX() == terrain.getPortalPoint().getX() && unit.getY() == terrain.getPortalPoint().getY()) {
                level++;
                if (level % 6 == 0) {
                    terrain = new BossArena(128);
                } else {
                    terrain = new Terrain(128);
                }
                unit.setX(terrain.getSpawnPoint().getX());
                unit.setY(terrain.getSpawnPoint().getY());
                generateEnemies();
            }
        }

        void drawTerrain(Canvas canvas) {
            int drawX = xStart;
            int drawY = yStart;
            for (int y = unit.getY() - (int) Math.floor(yBlocks / 2.0);
                 y <= unit.getY() + (int) Math.floor(yBlocks / 2.0); y++) {
                for (int x = unit.getX() - 4; x <= unit.getX() + 4; x++) {
                    Image img = getCorrespondingImage(canvas, drawX, drawY, x, y);
                    if (img != null) {
                        if (img instanceof DefaultImage) {
                            ((DefaultImage) img).draw(canvas, drawX, drawY);
                        } else {
                            ((AnimatedImage) img).draw(canvas, drawX, drawY);
                        }
                    }
                    drawAndUpdateEnemies(canvas, drawX, drawY, y, x);
                    drawX += unitOfLength;
                }
                drawY += unitOfLength;
                drawX = xStart;
            }
            warrior.draw(canvas);
            drawBar(canvas, new Rect(0, 0, getWidth(),
                    unitOfLength / 4), darkGray, red, unit.getHpPercentage());
            drawBar(canvas, new Rect(0, unitOfLength / 4, getWidth(),
                    unitOfLength / 2), darkGray, blue, unit.getManaPercentage());
        }

        private void drawAndUpdateEnemies(Canvas canvas, int drawX, int drawY, int y, int x) {
            ArrayList<Integer> deadEnemies = new ArrayList<>();
            for (Enemy e:enemies) {
                if (x == e.getX() && y == e.getY()) {
                    if (e.alive()) {
                        drawEnemyImage(e, canvas, drawX, drawY);
                        drawBar(canvas, new Rect(drawX - unitOfLength / 2,
                                drawY + (unitOfLength / 2) - 10,
                                drawX + unitOfLength / 2,
                                drawY + (unitOfLength / 2)), darkGray,
                                red, e.getHpPercentage());
                    } else {
                        if (drawEnemyDeadImage(e, canvas, drawX, drawY)) deadEnemies.add(enemies.indexOf(e));
                    }
                }
            }
            for (int i = deadEnemies.size() - 1; i >= 0; i--) {
                Enemy e = enemies.get(deadEnemies.get(i));
                unit.addXp(e.getXpReward());
                enemies.remove(e);
                enemiesKilled++;
                text.rewind();
                TextImage t = text;
                t.setText("+" + e.getXpReward() + " XP");
                textImages.add(t);
            }
        }

        private void drawEnemyImage(Enemy enemy, Canvas canvas, int x, int y) {
            AnimatedImage image = null;
            switch (enemy.getName()) {
                case "SLIME":
                case "KING SLIME":
                    image = slime;
                    break;
                case "ZOMBIE":
                    image = zombie;
                    break;
            }
            if (image != null) {
                image.draw(canvas, x, y);
            }
        }

        private boolean drawEnemyDeadImage(Enemy enemy, Canvas canvas, int x, int y) {
            AnimatedImage image = null;
            switch (enemy.getName()) {
                case "SLIME":
                case "KING SLIME":
                    image = slimeDefeated;
                    break;
                case "ZOMBIE":
                    image = zombieDefeated;
                    break;
            }
            if (image != null) {
                if (image.getAnimationCycles() > 0) {
                    image.rewind();
                    return true;
                } else {
                    image.draw(canvas, x, y);
                }
            }
            return false;
        }

        private Image getCorrespondingImage(Canvas canvas, int drawX, int drawY,
                                                   int x, int y) {
            Image img = null;
            boolean walkable = terrain.getBlockWalkable(x, y);
            switch (terrain.getBlockMaterial(x, y)) {
                case "stone":
                    if (walkable) {
                        stoneFloor.draw(canvas, drawX, drawY);
                    } else {
                        stoneWall.draw(canvas, drawX, drawY);
                    }
                    break;
                case "wooden":
                    if (walkable) {
                        woodenFloor.draw(canvas, drawX, drawY);
                    } else {
                        woodenWall.draw(canvas, drawX, drawY);
                    }
                    break;
            }
            switch (terrain.getBlockConfig(x, y)) {
                case "none":
                    img = null;
                    break;
                case "chest":
                    img = chest;
                    break;
                case "spawn":
                    img = spawn;
                    break;
                case "portal":
                    img = portal;
                    break;
                case "spikes":
                    if (terrain instanceof Terrain && !((Terrain) terrain).getTrap(x, y).getRevealed()) {
                        img = null;
                    } else {
                        img = spikesStatic;
                    }
                    break;
            }
            return img;
        }

        void drawButtons(Canvas canvas) {
            buttonDown.draw(canvas);
            buttonUp.draw(canvas);
            buttonRight.draw(canvas);
            buttonLeft.draw(canvas);
            buttonMiniMap.draw(canvas);
            buttonAttack.draw(canvas);
            buttonMagic.draw(canvas);
            buttonPause.draw(canvas);
        }

        void updateMiniMap(Canvas canvas) {
            if (miniMapOpened) {
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.argb(255, 255, 233, 128));
                canvas.drawRect((float) (unitOfLength * 0.5),
                        (float) (yStart + unitOfLength * (yBlocks / 2.0 - 4)),
                        (float) (unitOfLength * 8.5),
                        (float) (yStart + unitOfLength * (yBlocks / 2.0 + 4)), p);
                float left = (float) (unitOfLength);
                float top = (float) (yStart + unitOfLength * (yBlocks / 2.0 - 3.5));
                float side = (float) (unitOfLength * 7) /
                        (compactMode ? 35 : terrain.getSize());
                float drawX = left;
                float drawY = top;
                for (int y = compactMode ? unit.getY() - 17 : 0;
                     y < (compactMode ? unit.getY() + 18 : terrain.getSize()); y++) {
                    for (int x = compactMode ? unit.getX() - 17 : 0;
                         x < (compactMode ? unit.getX() + 18 : terrain.getSize()); x++) {
                        p = chooseCorrespondingColor(x, y);
                        canvas.drawRect(drawX, drawY, drawX + side, drawY + side, p);
                        drawX += side;
                    }
                    drawY += side;
                    drawX = left;
                }
            }
        }

        private Paint chooseCorrespondingColor(int x, int y) {
            Paint p = null;
            Paint floor = null;
            try {
                boolean walkable = terrain.getBlockWalkable(x, y);
                switch (terrain.getBlockMaterial(x, y)) {
                    case "stone":
                        if (walkable) {
                            floor = lightGray;
                        } else {
                            floor = darkGray;
                        }
                        break;
                    case "wooden":
                        if (walkable) {
                            floor = lightBrown;
                        } else {
                            floor = darkBrown;
                        }
                        break;
                }
                switch (terrain.getBlockConfig(x, y)) {
                    case "none":
                    case "spawn":
                        p = floor;
                        break;
                    case "chest":
                        p = gold;
                        break;
                    case "portal":
                        p = purple;
                        break;
                    case "spikes":
                        if (terrain instanceof Terrain && !((Terrain) terrain).getTrap(x, y).getRevealed()) {
                            p = floor;
                        } else {
                            p = white;
                        }
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                p = darkGray;
            }
            if (floor != null) {
                if (unit.getX() == x && unit.getY() == y) {
                    p = green;
                }
                for (Enemy e : enemies) {
                    if (x == e.getX() && y == e.getY()) {
                        p = red;
                        break;
                    }
                }
            }
            return p;
        }

        private void drawBar(Canvas canvas, Rect rect, Paint paint1, Paint paint2, double percentage) {
            canvas.drawRect(rect, paint1);
            canvas.drawRect(rect.left, rect.top, (float) (rect.left + (rect.right - rect.left) * percentage),
                    rect.bottom, paint2);
        }
    }
}


// TODO: сделать серверную базу данных
// TODO: сделать туман войны
// TODO: сделать мини игры, связанные с атакой или защитой
// TODO: сделать раунд игры конечной
/* TODO:
Бегло посмотрел проект.
Надо поработать над БД. Запрос данных должен происходить в отдельном потоке. Вывод в виде текста -- скучно. Использовать глобальную переменную -- решение не из лучших.
Метод checkMove в Unit'е:
Проверки и возврат строк -- не лучшее решение. Подумайте над enum'ами.
Подумайте над тем, чтобы использовать класс Random.
Логику циклов по генерации комнаты generateRoom может выделить в отдельный метод? В чём у них принципиальное отличие?
В generatePaths почему вложенный уикл до rooms[0].length-1, хотя бежите Вы по roomsList?
Зачем Вам в generatePaths два набора циклов? Вы же дублируете проходы для первых комнат.
fillRow/fillColumn содержат логику со строками, от которой, я надеюсь, Вы избавитесь.
Кстати, к вопросу хранения информации в Block. Может вместо enum'ов хранить набор флагов (пол/не пол; каменный/деревянный)? Также можно хранить Chest в виде поля. Т.е. Chest -- это не тип ячейки, а её опционально содержимое.
Использование статических полей в Pathfinder'е -- не самое лучшее решение. Подумайте, как можно заменить использование глобальной переменной info на локальную версию.
Логика InventoryItem на строках тоже меня настораживает.

Логику самой игры лучше убрать из класса DrawSurface в какой-нибудь класс Game. DrawSurface пусть отвечает только за рисование(вывод игры на экран).
При генерации врагов много одинакового кода. Может как-то можно его обобщить?
Не надо привязываться строго к уровню 6.
Может стоит отдельно решить вопрос придумывания точки спавна (можно вообще делегировать этот вопрос Terrain'у, т.к. он может решить эту задачу) и силы врага, а уже потом создавать его?
В getTerrainDrawData лучше возвращать специально созданный класс, а не массив.
В рисовалках может стоит избавиться от case'ов заменив их на мапы или массивы (Вы же уберёте ещё и строки?)
Почему в getCorrespondingImage что-то рисуется?
В ImageManager'е я бы подумал от избавления от статических полей. Можно попробовать сделать это без них.
Может логику работы с кнопками как-то загнать в общий цикл обработки?

Думаю, пока что хватит. Вам ещё надо сделать что-то с БД. Придумайте что-то более интересное, чем просто таблицу рекордов.
Если я правильно понял, то сейчас у Вас игра -- набор случайных локаций. Можете подумать и сделать локации не случайными.
Т.е. в процессе движения Вы накапливаете комнаты, чтобы в них можно было потом возвращаться (это актуально, если у Вас будет несколько выходов).
Конечно, в этом случае Ваш мир будет ограничен. Цель же будет заключаться в том, что надо что-то найти и это что-то будет находиться в одной из последних комнат.
Можно при этом добавить каких-то глобальных злодеев, которые также ходят между комнатами и могут наткнуться на героя. Но это как вариант.
Воспринимайте то, что я написал, как советы/рекомендации.
 */