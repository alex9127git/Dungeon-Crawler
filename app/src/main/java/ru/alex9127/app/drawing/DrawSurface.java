package ru.alex9127.app.drawing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.*;
import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import ru.alex9127.app.R;
import ru.alex9127.app.classes.*;
import ru.alex9127.app.saving.Save;

import static ru.alex9127.app.drawing.ImageManager.*;

@SuppressLint("ViewConstructor")
public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {
    public final GameLogic game;
    private int xStart, yStart, yBlocks;
    public DrawThread drawer;
    private final Activity a;
    public double dmgPlus, atkUpgradeMult, defUpgradeMult, startHp, startMana;

    public DrawSurface(Context context, String name, Save save, int time) {
        super(context);
        a = (Activity) context;
        dmgPlus = time / 30000.0;
        SharedPreferences p = a.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
        atkUpgradeMult = 1 + p.getInt("Attack power level", 0) * 0.01;
        defUpgradeMult = 1 + p.getInt("Defense power level", 0) * 0.01;
        startHp = p.getInt("Start HP level", 0) * 10;
        startMana = p.getInt("Start mana level", 0);
        if (save != null) {
            game = new GameLogic(save);
        } else {
            game = new GameLogic(name, startHp, startMana);
        }
        getHolder().addCallback(this);
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
            if (!drawer.isAttackMiniGame && !drawer.isDefenseMiniGame) {
                if (!drawer.paused) {
                    if (buttonDown.getBoundaryRect().contains(x, y)) {
                        s = game.unit.checkMove(0, 1, game.getTerrain());
                    }
                    if (buttonUp.getBoundaryRect().contains(x, y)) {
                        s = game.unit.checkMove(0, -1, game.getTerrain());
                    }
                    if (buttonLeft.getBoundaryRect().contains(x, y)) {
                        s = game.unit.checkMove(-1, 0, game.getTerrain());
                    }
                    if (buttonRight.getBoundaryRect().contains(x, y)) {
                        s = game.unit.checkMove(1, 0, game.getTerrain());
                    }
                    if (buttonMiniMap.getBoundaryRect().contains(x, y)) {
                        drawer.miniMapOpened = !drawer.miniMapOpened;
                    }
                    if (buttonAttack.getBoundaryRect().contains(x, y)) {
                        for (Enemy e : game.getTerrain().getEnemies()) {
                            if (Pathfinder.distance(game.unit.getX(), game.unit.getY(), e.getX(), e.getY()) <= 2.0) {
                                drawer.attackedEnemies.add(e);
                            }
                        }
                        if (drawer.attackedEnemies.isEmpty()) {
                            text.rewind();
                            TextImage t = text.clone();
                            t.setText("No enemies nearby to attack");
                            drawer.textImages.add(t);
                        } else {
                            drawer.isAttackMiniGame = true;
                            drawer.attackMiniGameTime = 0;
                            drawer.attackMiniGameCoinsSpawned = 0;
                            drawer.atkMultiplier = 1;
                        }
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
                        drawer.dmg = game.enemyAI();
                    }
                    if (game.getTerrain().getBlockConfig(game.unit.getX(), game.unit.getY()).equals("chest")) {
                        game.getTerrain().setBlockConfig(game.unit.getX(), game.unit.getY(), "none");
                        String str = "";
                        switch (s) {
                            case "atk":
                                str = "Found better weapon, ATK power up";
                                break;
                            case "def":
                                str = "Found better armor, DEF power up";
                                break;
                            case "heal":
                                str = "Found healing potion, restored 50% HP";
                                break;
                        }
                        text.rewind();
                        TextImage t = text.clone();
                        t.setText(str);
                        drawer.textImages.add(t);
                    }
                }
                if (buttonPause.getBoundaryRect().contains(x, y)) {
                    drawer.paused = !drawer.paused;
                }
            } else if (drawer.isAttackMiniGame) {
                ArrayList<StaticImage> usedCoins = new ArrayList<>();
                for (StaticImage coin:drawer.attackCoins) {
                    if (coin.getBoundaryRect().contains(x, y)) {
                        drawer.atkMultiplier *= 1.1;
                        usedCoins.add(coin);
                    }
                }
                for (StaticImage c:usedCoins) drawer.attackCoins.remove(c);
            } else if (drawer.isDefenseMiniGame) {
                ArrayList<StaticImage> usedCoins = new ArrayList<>();
                for (StaticImage coin:drawer.defenseCoins) {
                    if (coin.getBoundaryRect().contains(x, y)) {
                        drawer.dmgMultiplier *= 0.95;
                        usedCoins.add(coin);
                    }
                }
                for (StaticImage c:usedCoins) drawer.defenseCoins.remove(c);
            }
        } else {
            drawer.castingMagic = false;
            if (game.unit.getManaPercentage() > 0) {
                int blockX = game.unit.getX() - 4;
                int blockY = game.unit.getY() - (int) Math.floor(yBlocks / 2.0);
                blockX += Math.round((double) x / unitOfLength);
                blockY += Math.round((double) (y - yStart) / unitOfLength);
                game.unit.changeMana(-1);
                for (Enemy e : game.getTerrain().getEnemies()) {
                    if (Pathfinder.distance(e.getX(), e.getY(), blockX, blockY) < 2)
                        e.changeHp(-1 * (game.unit.getAttackPower() - e.getDefensePower()));
                }
                drawer.dmg = game.enemyAI();
            }
        }
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

    public Save save() {
        return new Save(game);
    }

    private class DrawThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private volatile boolean running = true;
        private boolean miniMapOpened = false, compactMode = false, paused = false, castingMagic = false,
        isAttackMiniGame = false, isDefenseMiniGame = false;
        private int dmg;
        private int attackMiniGameTime = 0;
        private int attackMiniGameCoinsSpawned = 0;
        private double atkMultiplier = 1;
        private int defenseMiniGameTime = 0;
        private int defenseMiniGameCoinsSpawned = 0;
        private double dmgMultiplier = 1;
        private final Paint red = new Paint();
        private final Paint darkGray = new Paint();
        private final Paint lightGray = new Paint();
        private final Paint darkBrown = new Paint();
        private final Paint lightBrown = new Paint();
        private final Paint gold = new Paint();
        private final Paint paleYellow = new Paint();
        private final Paint green = new Paint();
        private final Paint blue = new Paint();
        private final Paint purple = new Paint();
        private final Paint white = new Paint();
        private final ArrayList<TextImage> textImages = new ArrayList<>();
        private final ArrayList<StaticImage> attackCoins = new ArrayList<>();
        private final ArrayList<StaticImage> defenseCoins = new ArrayList<>();
        private final ArrayList<Enemy> attackedEnemies = new ArrayList<>();
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
            paleYellow.setColor(getResources().getColor(R.color.paleYellow));
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
                        timeCount();
                        if (!paused) {
                            if (!isAttackMiniGame && !isDefenseMiniGame) {
                                canvas.drawColor(Color.BLACK);
                                drawTerrain(canvas);
                                if (!castingMagic) drawButtons(canvas);
                                updateMiniMap(canvas);
                                game.checkUnitAlive(a);
                                game.checkNextLevel();
                                game.checkGoingBack();
                                game.checkAllEnemiesKilled(a);
                                animationsUpdate();
                                textPopupsDraw(canvas);
                            } else if (isAttackMiniGame) {
                                canvas.drawColor(getResources().getColor(R.color.darkGray));
                                drawCompoundText("Try to get as many attack coins\nas you can in five seconds!", unitOfLength,
                                        unitOfLength, white, canvas);
                                if (attackMiniGameTime > 200 * (attackMiniGameCoinsSpawned + 1)) {
                                    spawnAttackMiniGameCoin();
                                }
                                drawAttackMiniGameCoins(canvas);
                                checkAttackMiniGameOver();
                            } else if (isDefenseMiniGame) {
                                canvas.drawColor(getResources().getColor(R.color.darkGray));
                                drawCompoundText("Try to get as many defense coins\nas you can in five seconds!", unitOfLength,
                                        unitOfLength, white, canvas);
                                if (defenseMiniGameTime > 200 * (defenseMiniGameCoinsSpawned + 1)) {
                                    spawnDefenseMiniGameCoin();
                                }
                                drawDefenseMiniGameCoins(canvas);
                                checkDefenseMiniGameOver();
                            }
                        } else {
                            canvas.drawColor(getResources().getColor(R.color.darkGray));
                            buttonPause.draw(canvas);
                            drawCompoundText(game.unit.stats() + "\nYou also get +" +
                                            (int) (dmgPlus * 100) + "% to attack power\nbased on" +
                                            " your time playing this game\nYou will get " +
                                            game.coinsGotten + " coins\nby the end of this round",
                                    buttonPause.getBoundaryRect().left,
                                    buttonPause.getBoundaryRect().bottom + unitOfLength, white,
                                    canvas);
                        }
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void checkAttackMiniGameOver() {
            if (attackMiniGameTime > 5000) {
                isAttackMiniGame = false;
                attackCoins.clear();
                for (Enemy e : drawer.attackedEnemies) {
                    int atk = (int) ((game.unit.getAttackPower() * atkMultiplier * (1 + dmgPlus) * atkUpgradeMult - e.getDefensePower()));
                    e.changeHp(-atk);
                    text.rewind();
                    TextImage t = text.clone();
                    t.setText(Integer.toString(atk));
                    drawer.textImages.add(t);
                }
                drawer.dmg = game.enemyAI();
            }
        }

        private void checkDefenseMiniGameOver() {
            if (defenseMiniGameTime > 5000) {
                isDefenseMiniGame = false;
                defenseCoins.clear();
                game.unit.changeHp((int) (dmg * dmgMultiplier / defUpgradeMult));
                dmg = 0;
            }
        }

        private void drawAttackMiniGameCoins(Canvas canvas) {
            try {
                for (StaticImage coin : attackCoins) {
                    coin.draw(canvas);
                }
            } catch (ConcurrentModificationException ignored) {}
        }

        private void drawDefenseMiniGameCoins(Canvas canvas) {
            try {
                for (StaticImage coin : defenseCoins) {
                    coin.draw(canvas);
                }
            } catch (ConcurrentModificationException ignored) {}
        }

        private void spawnAttackMiniGameCoin() {
            attackMiniGameCoinsSpawned++;
            StaticImage coin = attackMiniGame.clone();
            coin.setDefaultX(unitOfLength / 2 + (int) (Math.random() * (screenWidth - unitOfLength)));
            coin.setDefaultY(unitOfLength / 2 + (int) (Math.random() * (screenHeight - unitOfLength)));
            attackCoins.add(coin);
        }

        private void spawnDefenseMiniGameCoin() {
            defenseMiniGameCoinsSpawned++;
            StaticImage coin = defenseMiniGame.clone();
            coin.setDefaultX(unitOfLength / 2 + (int) (Math.random() * (screenWidth - unitOfLength)));
            coin.setDefaultY(unitOfLength / 2 + (int) (Math.random() * (screenHeight - unitOfLength)));
            defenseCoins.add(coin);
        }

        private void timeCount() {
            int diff = (int) (System.currentTimeMillis() - currentTime);
            runTime += diff;
            if (isAttackMiniGame) attackMiniGameTime += diff;
            if (isDefenseMiniGame) defenseMiniGameTime += diff;
            currentTime = System.currentTimeMillis();
            if (((int) (runTime / 1000)) > seconds) {
                seconds++;
                if (seconds % 3 == 0 && game.unit.getManaPercentage() < 1)
                    game.unit.changeMana(1);
                if (seconds % 5 == 0 && textImages.size() >= 3) {
                    textImages.clear();
                    ArrayList<Enemy> dead = new ArrayList<>();
                    for (Enemy e:game.getTerrain().getEnemies()) {
                        if (!e.alive()) {
                            dead.add(e);
                            game.unit.addXp(e.getXpReward());
                            game.getTerrain().getEnemies().remove(e);
                            game.getTerrain().removeBlockEntity(e.getX(), e.getY(), e);
                            game.coinsGotten += game.floor * (game.bossesDefeated + 1);
                            text.rewind();
                            TextImage t = text.clone();
                            t.setText("+" + e.getXpReward() + " XP");
                            textImages.add(t);
                        }
                    }
                    game.getTerrain().enemies.removeAll(dead);
                }
            }
            if (dmg < 0 && !isDefenseMiniGame) {
                isDefenseMiniGame = true;
                defenseMiniGameTime = 0;
                defenseMiniGameCoinsSpawned = 0;
                dmgMultiplier = 1;
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
                    game.getTerrain().revealBlock(x, y);
                    Images images = getCorrespondingImages(x, y);
                    ((DefaultImage) images.getBase()).draw(canvas, drawX, drawY);
                    if (images.getConfig() != null) {
                        if (images.getConfig() instanceof DefaultImage) {
                            ((DefaultImage) images.getConfig()).draw(canvas, drawX, drawY);
                        } else {
                            ((AnimatedImage) images.getConfig()).draw(canvas, drawX, drawY);
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
            for (Enemy e:game.getTerrain().getEnemies()) {
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
                            deadEnemies.add(game.getTerrain().getEnemies().indexOf(e));
                    }
                }
            }
            for (int i = deadEnemies.size() - 1; i >= 0; i--) {
                Enemy e = game.getTerrain().getEnemies().get(deadEnemies.get(i));
                game.unit.addXp(e.getXpReward());
                game.getTerrain().getEnemies().remove(e);
                game.getTerrain().removeBlockEntity(x, y, e);
                game.coinsGotten += game.floor * (game.bossesDefeated + 1);
                text.rewind();
                TextImage t = text.clone();
                t.setText("+" + e.getXpReward() + " XP");
                textImages.add(t);
            }
        }

        private void drawEnemyImage(Enemy enemy, Canvas canvas, int x, int y) {
            AnimatedImage image = null;
            switch (enemy.getName()) {
                case "GreenSlime":
                case "KingSlime":
                    image = greenSlime;
                    break;
                case "BlueSlime":
                    image = blueSlime;
                    break;
                case "Zombie":
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
                case "GreenSlime":
                case "KingSlime":
                    image = greenSlimeDefeated;
                    break;
                case "BlueSlime":
                    image = blueSlimeDefeated;
                    break;
                case "Zombie":
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

        class Images {
            private final Image base;
            private final Image config;

            public Images(Image base, Image config) {
                this.base = base;
                this.config = config;
            }

            public Image getBase() {
                return base;
            }

            public Image getConfig() {
                return config;
            }
        }

        private Images getCorrespondingImages(int x, int y) {
            Image base = null;
            Image config = null;
            boolean walkable = game.getTerrain().getBlockWalkable(x, y);
            switch (game.getTerrain().getBlockMaterial(x, y)) {
                case "stone":
                    if (walkable) {
                        base = stoneFloor;
                    } else {
                        base = stoneWall;
                    }
                    break;
                case "wooden":
                    if (walkable) {
                        base = woodenFloor;
                    } else {
                        base = woodenWall;
                    }
                    break;
            }
            switch (game.getTerrain().getBlockConfig(x, y)) {
                case "none":
                    config = null;
                    break;
                case "chest":
                    config = chest;
                    break;
                case "spawn":
                    config = spawn;
                    break;
                case "spikes":
                    if (game.getTerrain() != null && game.getTerrain().getTrap(x, y).isNotRevealed()) {
                        config = null;
                    } else {
                        config = spikesStatic;
                    }
                    break;
            }
            if (game.getTerrain() != null && game.getTerrain().getBlockConfig(x, y).startsWith("portal"))
                config = portal;
            return new Images(base, config);
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
                p.setColor(getResources().getColor(R.color.paleYellow));
                canvas.drawRect((float) (unitOfLength * 0.5),
                        (float) (yStart + unitOfLength * (yBlocks / 2.0 - 4)),
                        (float) (unitOfLength * 8.5),
                        (float) (yStart + unitOfLength * (yBlocks / 2.0 + 4)), p);
                float left = (float) (unitOfLength);
                float top = (float) (yStart + unitOfLength * (yBlocks / 2.0 - 3.5));
                float side = (float) (unitOfLength * 7) /
                        (compactMode ? 35 : game.getTerrain().getSize());
                float drawX = left;
                float drawY = top;
                for (int y = compactMode ? game.unit.getY() - 17 : 0;
                     y < (compactMode ? game.unit.getY() + 18 : game.getTerrain().getSize()); y++) {
                    for (int x = compactMode ? game.unit.getX() - 17 : 0;
                         x < (compactMode ? game.unit.getX() + 18 : game.getTerrain().getSize()); x++) {
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
            if (game.getTerrain().isBlockRevealed(x, y)) {
                try {
                    boolean walkable = game.getTerrain().getBlockWalkable(x, y);
                    switch (game.getTerrain().getBlockMaterial(x, y)) {
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
                    switch (game.getTerrain().getBlockConfig(x, y)) {
                        case "none":
                        case "spawn":
                            p = floor;
                            break;
                        case "chest":
                            p = gold;
                            break;
                        case "spikes":
                            if (game.getTerrain() != null &&
                                    game.getTerrain().getTrap(x, y).isNotRevealed()) {
                                p = floor;
                            } else {
                                p = white;
                            }
                            break;
                    }
                    if (game.getTerrain() != null && game.getTerrain().getBlockConfig(x, y).startsWith("portal")) {
                        p = purple;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    p = darkGray;
                }
                if (floor != null) {
                    if (game.unit.getX() == x && game.unit.getY() == y) {
                        p = green;
                    }
                    for (Enemy e : game.getTerrain().getEnemies()) {
                        if (x == e.getX() && y == e.getY()) {
                            p = red;
                            break;
                        }
                    }
                }
                return p;
            } else {
                return paleYellow;
            }
        }

        private void drawBar(Canvas canvas, Rect rect, Paint paint1, Paint paint2, double percentage) {
            canvas.drawRect(rect, paint1);
            canvas.drawRect(rect.left, rect.top, (float) (rect.left + (rect.right - rect.left) * percentage),
                    rect.bottom, paint2);
        }
    }
}

/* TODO:
Бегло посмотрел проект.
Метод checkMove в Unit'е:
Проверки и возврат строк -- не лучшее решение. Подумайте над enum'ами.
Использование статических полей в Pathfinder'е -- не самое лучшее решение. Подумайте, как можно заменить использование глобальной переменной info на локальную версию.
Логика InventoryItem на строках тоже меня настораживает.
В рисовалках может стоит избавиться от case'ов заменив их на мапы или массивы (Вы же уберёте ещё и строки?)
В ImageManager'е я бы подумал от избавления от статических полей. Можно попробовать сделать это без них.
Может логику работы с кнопками как-то загнать в общий цикл обработки?
 */