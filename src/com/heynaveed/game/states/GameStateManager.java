package com.heynaveed.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.heynaveed.game.StruddyBird;

import java.util.Stack;

/**
 * Created by Naveed PC on 12/06/2016.
 */
public class GameStateManager {

    private Stack<State> states;
    protected static int difficulty;
    protected static boolean isFxOn;
    protected static boolean isMusicOn;

    public GameStateManager(){
        states = new Stack<State>();
    }

    public void push(State state){
        states.push(state);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }

    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    public void setIsFxOn(boolean isFxOn){ this.isFxOn = isFxOn; }

    public void setIsMusicOn(boolean isMusicOn){
        this.isMusicOn = isMusicOn;
        if(isMusicOn)
            StruddyBird.getMusic().setVolume(0.3f);
        else
            StruddyBird.getMusic().setVolume(0.0f);
    }

    protected int getDifficulty(){
        return difficulty;
    }

    protected boolean getIsFxOn(){ return isFxOn; }

    protected boolean getIsMusicOn(){ return isMusicOn; }
}
