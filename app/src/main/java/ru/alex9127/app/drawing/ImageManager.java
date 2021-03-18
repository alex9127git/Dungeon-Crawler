package ru.alex9127.app.drawing;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import ru.alex9127.app.R;

public class ImageManager {
    public static DefaultImage stoneFloor, stoneWall, woodenFloor, woodenWall, spikesStatic, chest, spawn;
    public static AnimatedImage slime, slimeDefeated, zombie, zombieDefeated, portal, spikes;
    public static StaticImage warrior, buttonUp, buttonDown, buttonLeft, buttonRight, buttonMiniMap,
            buttonAttack, buttonMagic, buttonPause;
    public static TextImage text;
    public static int unitOfLength;

    public static void generateImages(Resources resources, int screenWidth, int screenHeight,
                                      int yStart, int yBlocks) {
        unitOfLength = screenWidth / 9;
        stoneFloor = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.stonefloor),
                unitOfLength, unitOfLength);
        stoneWall = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.stonewall),
                unitOfLength, unitOfLength);
        woodenFloor = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.woodenfloor),
                unitOfLength, unitOfLength);
        woodenWall = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.woodenwall),
                unitOfLength, unitOfLength);
        spikes = new AnimatedImage(BitmapFactory.decodeResource(resources, R.drawable.spikes),
                unitOfLength, unitOfLength, 4, 250);
        spikesStatic = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.spikesstatic),
                unitOfLength, unitOfLength);
        chest = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.chest),
                unitOfLength - 20, unitOfLength - 20);
        spawn = new DefaultImage(BitmapFactory.decodeResource(resources, R.drawable.spawn),
                unitOfLength, unitOfLength);
        warrior = new StaticImage(BitmapFactory.decodeResource(resources, R.drawable.warrior),
                unitOfLength, unitOfLength, screenWidth / 2,
                screenHeight / 2);
        slime = new AnimatedImage(BitmapFactory.decodeResource(resources, R.drawable.slime),
                unitOfLength, unitOfLength, 4, 100);
        slimeDefeated = new AnimatedImage(BitmapFactory.decodeResource(resources,
                R.drawable.slimedefeated), unitOfLength, unitOfLength, 4,
                200);
        zombie = new AnimatedImage(BitmapFactory.decodeResource(resources, R.drawable.zombie),
                unitOfLength, unitOfLength, 4, 100);
        zombieDefeated = new AnimatedImage(BitmapFactory.decodeResource(resources,
                R.drawable.zombiedefeated), unitOfLength, unitOfLength, 4,
                200);
        portal = new AnimatedImage(BitmapFactory.decodeResource(resources, R.drawable.portal),
                unitOfLength, unitOfLength, 4, 250);
        buttonUp = new StaticImage(BitmapFactory.decodeResource(resources, R.drawable.buttonup),
                unitOfLength, unitOfLength, unitOfLength * 2,
                (int) (yStart + unitOfLength * (yBlocks - 2.5 - yBlocks % 2)));
        buttonDown = new StaticImage(BitmapFactory.decodeResource(resources, R.drawable.buttondown),
                unitOfLength, unitOfLength, unitOfLength * 2,
                (int) (yStart + unitOfLength * (yBlocks - 0.5 - yBlocks % 2)));
        buttonLeft = new StaticImage(BitmapFactory.decodeResource(resources, R.drawable.buttonleft),
                unitOfLength, unitOfLength, unitOfLength,
                (int) (yStart + unitOfLength * (yBlocks - 1.5 - yBlocks % 2)));
        buttonRight = new StaticImage(BitmapFactory.decodeResource(resources,
                R.drawable.buttonright), unitOfLength, unitOfLength, unitOfLength * 3,
                (int) (yStart + unitOfLength * (yBlocks - 1.5 - yBlocks % 2)));
        buttonMiniMap = new StaticImage(BitmapFactory.decodeResource(resources,
                R.drawable.buttonminimap), unitOfLength * 2, unitOfLength * 2,
                (int) (unitOfLength * 7.5), (int) (yStart + unitOfLength * (1.5 - yBlocks % 2)));
        buttonPause = new StaticImage(BitmapFactory.decodeResource(resources,
                R.drawable.buttonpause), unitOfLength * 2, unitOfLength * 2,
                (int) (unitOfLength * 1.5), (int) (yStart + unitOfLength * (1.5 - yBlocks % 2)));
        buttonAttack = new StaticImage(BitmapFactory.decodeResource(resources,
                R.drawable.buttonattack), unitOfLength * 2, unitOfLength * 2,
                (int) (unitOfLength * 7.5),
                (int) (yStart + unitOfLength * (yBlocks - 1.5 - yBlocks % 2)));
        buttonMagic = new StaticImage(BitmapFactory.decodeResource(resources,
                R.drawable.buttonmagic), unitOfLength * 2, unitOfLength * 2,
                (int) (unitOfLength * 7.5),
                (int) (yStart + unitOfLength * (yBlocks - 4 - yBlocks % 2)));
        text = new TextImage("Text", 1000, screenWidth / 2, screenHeight / 2);
    }

    public static void animationsUpdate() {
        slime.update();
        slimeDefeated.update();
        zombie.update();
        zombieDefeated.update();
        portal.update();
        spikes.update();
    }
}
