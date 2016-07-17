package com.heynaveed.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by Naveed PC on 16/06/2016.
 */
public class PrettyBird {

    private final Vector3 position;
    private final Animation birdAnimation;
    private final Texture texture;
    private final Random random;
    private boolean isGoingLeft;
    private int movement;

    public PrettyBird(int x, int y, boolean isGoingLeft){
        texture = new Texture("birdanimation.png");
        random = new Random();
        this.isGoingLeft = isGoingLeft;
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f, isGoingLeft);
        position = new Vector3(x, y, 0);
        setMovement(50);
    }

    public void update(float dt){
        birdAnimation.update(dt);
        position.add(movement*dt, 0, 0);
    }

    public void dispose() { texture.dispose(); }

    public void setMovement(int movement){
        if(!isGoingLeft)
            this.movement = movement;
        else
            this.movement = -movement;
    }

    public boolean getIsGoingLeft(){
        return isGoingLeft;
    }

    public TextureRegion getTexture(){
        return birdAnimation.getFrame();
    }

    public Vector3 getPosition(){
        return position;
    }

    public Animation getAnimation(){
        return birdAnimation;
    }
}
