package ru.alex9127.app.drawing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import ru.alex9127.app.R;
import ru.alex9127.app.classes.*;

import static ru.alex9127.app.drawing.ImageManager.*;

@SuppressLint("ViewConstructor")
public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {
    private final GameLogic game;
    private int xStart, yStart, yBlocks;
    public DrawThread drawer;
    private final Activity a;

    public DrawSurface(Context context, String name) {
        super(context);
        game = new GameLogic(name);
        a = (Activity) context;
        getHolder().addCallback(this);
        game.generateEnemies();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        TerrainData td = TerrainData.get(this);
        xStart = td.getXStartPos();
        yStart = td.getYStartPos();
        yBlocks = td.getYBlocks();
        generateImages(getResources(), getWidth(), getHeight(), yStart, yBlocks);
    }

    static class TerrainData {
        private final int xStartPos;
        private final int yStartPos;
        private final int yBlocks;

        private TerrainData(int xStartPos, int yStartPos, int yBlocks) {
            this.xStartPos = xStartPos;
            this.yStartPos = yStartPos;
            this.yBlocks = yBlocks;
        }

        public int getXStartPos() {
            return xStartPos;
        }

        public int getYStartPos() {
            return yStartPos;
        }

        public int getYBlocks() {
            return yBlocks;
        }

        static TerrainData get(DrawSurface s) {
            return new TerrainData(s.getWidth() / 18,
                    (s.getHeight() / 2) % ((s.getWidth() / 9) == 0 ? 1 : (s.getWidth() / 9)),
                    s.getHeight() / ((s.getWidth() / 9) == 0 ? 1 : (s.getWidth() / 9)));
        }
    }

    public void handleClick(int x, int y) {
        String s = "";
        if (!drawer.castingMagic) {
            if (!drawer.paused) {
                if (buttonDown.getBoundaryRect().contains(x, y)) {
                    s = game.unit.checkMove(0, 1, game.terrain);
                }
                if (buttonUp.getBoundaryRect().contains(x, y)) {
                    s = game.unit.checkMove(0, -1, game.terrain);
                }
                if (buttonLeft.getBoundaryRect().contains(x, y)) {
                    s = game.unit.checkMove(-1, 0, game.terrain);
                }
                if (buttonRight.getBoundaryRect().contains(x, y)) {
                    s = game.unit.checkMove(1, 0, game.terrain);
                }
                if (buttonMiniMap.getBoundaryRect().contains(x, y)) {
                    drawer.miniMapOpened = !drawer.miniMapOpened;
                }
                if (buttonAttack.getBoundaryRect().contains(x, y)) {
                    for (Enemy e : game.enemies) {
                        if (Pathfinder.distance(game.unit.getX(), game.unit.getY(), e.getX(), e.getY()) <= 2.0) {
                            e.changeHp(-1 * (game.unit.getAttackPower() - e.getDefensePower()));
                        }
                    }
                    game.enemyAI();
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
                    game.enemyAI();
                }
                if (game.terrain.getBlockConfig(game.unit.getX(), game.unit.getY()).equals("chest")) {
                    game.terrain.setBlockConfig(game.unit.getX(), game.unit.getY(), "none");
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
            if (game.unit.getManaPercentage() > 0) {
                int blockX = game.unit.getX() - 4;
                int blockY = game.unit.getY() - (int) Math.floor(yBlocks / 2.0);
                blockX += Math.round((double) x / unitOfLength);
                blockY += Math.round((double) (y - yStart) / unitOfLength);
                game.unit.changeMana(-1);
                for (Enemy e : game.enemies) {
                    if (Pathfinder.distance(e.getX(), e.getY(), blockX, blockY) < 2)
                        e.changeHp(-1 * (game.unit.getAttackPower() - e.getDefensePower()));
                }
                game.enemyAI();
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
        private final Paint black = new Paint();
        private final ArrayList<TextImage> textImages = new ArrayList<>();
        private long currentTime = System.currentTimeMillis();
        private long runTime = 0;
        private long seconds = 0;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            black.setColor(getResources().getColor(R.color.black));
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
                            game.checkUnitAlive(a);
                            game.checkNextLevel();
                            animationsUpdate();
                            textPopupsDraw(canvas);
                            timeCount();
                        } else {
                            canvas.drawColor(getResources().getColor(R.color.darkGray));
                            buttonPause.draw(canvas);
                            drawCompoundText(game.unit.stats(), buttonPause.getBoundaryRect().left,
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
                if (seconds % 3 == 0 && game.unit.getManaPercentage() < 1)
                    game.unit.changeMana(1);
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

        void drawTerrain(Canvas canvas) {
            int drawX = xStart;
            int drawY = yStart;
            for (int y = game.unit.getY() - (int) Math.floor(yBlocks / 2.0);
                 y <= game.unit.getY() + (int) Math.floor(yBlocks / 2.0); y++) {
                for (int x = game.unit.getX() - 4; x <= game.unit.getX() + 4; x++) {
                    game.terrain.revealBlock(x, y);
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
                    unitOfLength / 4), darkGray, red, game.unit.getHpPercentage());
            drawBar(canvas, new Rect(0, unitOfLength / 4, getWidth(),
                    unitOfLength / 2), darkGray, blue, game.unit.getManaPercentage());
        }

        private void drawAndUpdateEnemies(Canvas canvas, int drawX, int drawY, int y, int x) {
            ArrayList<Integer> deadEnemies = new ArrayList<>();
            for (Enemy e:game.enemies) {
                if (x == e.getX() && y == e.getY()) {
                    if (e.alive()) {
                        drawEnemyImage(e, canvas, drawX, drawY);
                        drawBar(canvas, new Rect(drawX - unitOfLength / 2,
                                drawY + (unitOfLength / 2) - 10,
                                drawX + unitOfLength / 2,
                                drawY + (unitOfLength / 2)), darkGray,
                                red, e.getHpPercentage());
                    } else {
                        if (drawEnemyDeadImage(e, canvas, drawX, drawY))
                            deadEnemies.add(game.enemies.indexOf(e));
                    }
                }
            }
            for (int i = deadEnemies.size() - 1; i >= 0; i--) {
                Enemy e = game.enemies.get(deadEnemies.get(i));
                game.unit.addXp(e.getXpReward());
                game.enemies.remove(e);
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
            boolean walkable = game.terrain.getBlockWalkable(x, y);
            switch (game.terrain.getBlockMaterial(x, y)) {
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
            switch (game.terrain.getBlockConfig(x, y)) {
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
                    if (game.terrain instanceof Terrain && ((Terrain) game.terrain).getTrap(x, y).isNotRevealed()) {
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
                        (compactMode ? 35 : game.terrain.getSize());
                float drawX = left;
                float drawY = top;
                for (int y = compactMode ? game.unit.getY() - 17 : 0;
                     y < (compactMode ? game.unit.getY() + 18 : game.terrain.getSize()); y++) {
                    for (int x = compactMode ? game.unit.getX() - 17 : 0;
                         x < (compactMode ? game.unit.getX() + 18 : game.terrain.getSize()); x++) {
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
            if (game.terrain.isBlockRevealed(x, y)) {
                try {
                    boolean walkable = game.terrain.getBlockWalkable(x, y);
                    switch (game.terrain.getBlockMaterial(x, y)) {
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
                    switch (game.terrain.getBlockConfig(x, y)) {
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
                            if (game.terrain instanceof Terrain && ((Terrain) game.terrain).getTrap(x, y).isNotRevealed()) {
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
                    if (game.unit.getX() == x && game.unit.getY() == y) {
                        p = green;
                    }
                    for (Enemy e : game.enemies) {
                        if (x == e.getX() && y == e.getY()) {
                            p = red;
                            break;
                        }
                    }
                }
                return p;
            } else {
                return black;
            }
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
Использование статических полей в Pathfinder'е -- не самое лучшее решение. Подумайте, как можно заменить использование глобальной переменной info на локальную версию.
Логика InventoryItem на строках тоже меня настораживает.

Не надо привязываться строго к уровню 6.
Может стоит отдельно решить вопрос придумывания точки спавна (можно вообще делегировать этот вопрос Terrain'у, т.к. он может решить эту задачу) и силы врага, а уже потом создавать его?
В рисовалках может стоит избавиться от case'ов заменив их на мапы или массивы (Вы же уберёте ещё и строки?)
Почему в getCorrespondingImage что-то рисуется?
В ImageManager'е я бы подумал от избавления от статических полей. Можно попробовать сделать это без них.
Может логику работы с кнопками как-то загнать в общий цикл обработки?
 */