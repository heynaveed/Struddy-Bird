package com.heynaveed.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.heynaveed.game.StruddyBird;

/**
 * Created by Naveed PC on 03/07/2016.
 */
public class LoadState extends State {

    private static final float TIME_LIMIT = 2.0f;
    private static final float TIME_OFFSET = 0.25f;
    private BitmapFont heynaveedFont;
    private GlyphLayout heynaveedLayout;
    private Sound glowSound;
    private float timer = -TIME_OFFSET;
    private boolean soundHasPlayed = false;

    public LoadState(GameStateManager gsm){
        super(gsm);
        glowSound = Gdx.audio.newSound(Gdx.files.internal("intro.wav"));
        heynaveedFont = new BitmapFont(Gdx.files.internal("sb_font.fnt"));
        heynaveedLayout = new GlyphLayout();

        cam.setToOrtho(false, StruddyBird.WIDTH / 2, StruddyBird.HEIGHT / 2);
        heynaveedFont.setColor(new Color(0.12f, 0.73f, 0.35f, 0.0f));
        heynaveedFont.getData().setScale(0.5f, 0.5f);
        heynaveedLayout.setText(heynaveedFont, "@HEYNAVEED");
    }

    @Override
    public void handleInput(){
    }

    @Override
    public void update(float dt){
        timer += dt;

        if(timer > (TIME_LIMIT*0.5f)/10 && !soundHasPlayed) {

            if(gsm.getIsFxOn())
                glowSound.play(0.4f);

            soundHasPlayed = true;
        }

        if(timer < TIME_LIMIT*0.5f)
            heynaveedFont.setColor(new Color(0.12f, 0.73f, 0.35f, 2*timer));
        else if(timer >= TIME_LIMIT)
            gsm.set(new MenuState(gsm));
    }

    @Override
    public void render(SpriteBatch sb){
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        heynaveedFont.draw(sb, "@HEYNAVEED", cam.viewportWidth/2 - (heynaveedLayout.width/2), cam.viewportHeight/2 + (heynaveedLayout.height/2));
        sb.end();
    }

    @Override
    public void dispose(){
        heynaveedFont.dispose();
        glowSound.dispose();
    }
}
