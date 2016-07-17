package com.heynaveed.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.heynaveed.game.StruddyBird;

/**
 * Created by Naveed PC on 12/06/2016.
 */
public class GameBird {

    private static final int GRAVITY = -15;

    private final Vector3 position;
    private final Vector3 velocity;
    private final Rectangle bounds;
    private final Animation birdAnimation;
    private final Sprite sprite;
    private int movement, rotation;
    private float cycleTime;

    public GameBird(int x, int y, int difficulty){
        position = new Vector3(x, y, 0);
        velocity = new Vector3(0, 0, 0);
        sprite = new Sprite(new Texture("birdanimation.png"));
        initialiseDifficultyAttributes(difficulty);
        birdAnimation = new Animation(new TextureRegion(sprite), 3, cycleTime, false);
        bounds = new Rectangle(x, y, sprite.getWidth()/3, sprite.getHeight());
    }

    private void initialiseDifficultyAttributes(int difficulty){
        if(difficulty == StruddyBird.EASY){
            movement = 140;
            cycleTime = 0.5f;
        }
        else if(difficulty == StruddyBird.MEDIUM){
            movement = 160;
            cycleTime = 0.3f;
        }
        else{
            movement = 200;
            cycleTime = 0.1f;
        }
    }

    public void update(float dt){
        if(movement != 0)
            birdAnimation.update(dt);
        if (position.y > 0)
            velocity.add(0, GRAVITY, 0);
        if(rotation > 90)
            rotation = 90;

        velocity.scl(dt);
        position.add(movement * dt, velocity.y, 0);
        velocity.scl(1 / dt);
        bounds.setPosition(position.x, position.y);
        rotation = Math.abs(Math.round(velocity.y)/GRAVITY)*3;
    }

    public void jump(){
        velocity.y = 250;
    }

    public void impactJump(){
        velocity.y = 100; // 350 to turn back to other death animation
//        movement /= 1.5f;
        movement = 0;
    }

    public void setMovement(int movement){
        this.movement = movement;
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {
        return birdAnimation.getFrame();
    }

    public int getRotation() { return rotation; }
}
