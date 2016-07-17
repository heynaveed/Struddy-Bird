package com.heynaveed.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Naveed PC on 12/06/2016.
 */
public class Animation {

    private final Array<TextureRegion> frames;
    private int frameCount, frame;
    private float maxFrameTime, currentFrameTime;

    public Animation(TextureRegion region, int frameCount, float cycleTime, boolean isGoingLeft){
        frames = new Array<TextureRegion>();
        int frameWidth = region.getRegionWidth()/frameCount;

        for(int i = 0; i < frameCount; i++){
            frames.add(new TextureRegion(region, i*frameWidth, 0, frameWidth, region.getRegionHeight()));
        }

        this.frameCount = frameCount;
        maxFrameTime = cycleTime/frameCount;
        frame = 0;

        if(isGoingLeft) {
            for (TextureRegion f : frames) {
                f.flip(true, false);
            }
        }
    }

    public void update(float dt){
        currentFrameTime += dt;
        if(currentFrameTime > maxFrameTime){
            frame++;
            currentFrameTime = 0;
        }
        if(frame >= frameCount){
            frame = 0;
        }
    }

    public void changeAnimationSpeed(float cycleTime){
        maxFrameTime = cycleTime/frameCount;
    }

    public TextureRegion getFrame(){
        return frames.get(frame);
    }
}
