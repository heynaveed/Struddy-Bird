package com.heynaveed.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.heynaveed.game.StruddyBird;

import java.util.Random;

/**
 * Created by Naveed PC on 12/06/2016.
 */
public class Tube {

    public static final int TUBE_WIDTH = 52;
    private static final int EASY_GAP = 80;
    private static final int MEDIUM_GAP = 72;
    private static final int HARD_GAP = 64;

    private final Texture topTube, bottomTube;
    private final Vector2 posTopTube, posBotTube;
    private final Random rand;
    private final Rectangle boundsTop, boundsBot, boundsScore;
    private boolean isPassed = false;
    private int tubeGap, fluctuation, lowestOpening;

    public Tube(float x, int difficulty){
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        rand = new Random();

        initialiseDifficultyAttributes(difficulty);

        posTopTube = new Vector2(x, rand.nextInt(fluctuation) + tubeGap + lowestOpening);
        posBotTube = new Vector2(x, posTopTube.y - tubeGap - bottomTube.getHeight());
        boundsTop = new Rectangle(posTopTube.x, posTopTube.y, topTube.getWidth(), topTube.getHeight());
        boundsBot = new Rectangle(posBotTube.x, posBotTube.y, bottomTube.getWidth(), bottomTube.getHeight());
        boundsScore = new Rectangle(posBotTube.x, posBotTube.y, bottomTube.getWidth(), posTopTube.y - posBotTube.y);
    }

    private void initialiseDifficultyAttributes(int difficulty){
        if(difficulty == StruddyBird.EASY) {
            tubeGap = EASY_GAP;
            fluctuation = 130;
            lowestOpening = 120;
        }
        else if(difficulty == StruddyBird.MEDIUM) {
            tubeGap = MEDIUM_GAP;
            fluctuation = 130;
            lowestOpening = 120;
        }
        else {
            tubeGap = HARD_GAP;
            fluctuation = 115;
            lowestOpening = 150;
        }
    }

    public void reposition(float x){
        posTopTube.set(x, rand.nextInt(fluctuation) + tubeGap + lowestOpening);
        posBotTube.set(x, posTopTube.y - tubeGap - bottomTube.getHeight());
        boundsTop.setPosition(posTopTube.x, posTopTube.y);
        boundsBot.setPosition(posBotTube.x, posBotTube.y);
        boundsScore.setPosition(posBotTube.x, posBotTube.y);
        isPassed = false;
    }

    public boolean tubeCollides(Rectangle player){
        return player.overlaps(boundsTop) || player.overlaps(boundsBot);
    }

    public boolean scoreCollides(Rectangle player){
        return player.overlaps(boundsScore);
    }

    public void dispose(){
        topTube.dispose();
        bottomTube.dispose();
    }

    public Texture getTopTube() {
        return topTube;
    }

    public Texture getBottomTube() {
        return bottomTube;
    }

    public Vector2 getPosTopTube() {
        return posTopTube;
    }

    public Vector2 getPosBotTube() {
        return posBotTube;
    }

    public boolean getIsPassed() { return isPassed; }

    public void setIsPassed(boolean isPassed) { this.isPassed = isPassed; }
}
