package com.heynaveed.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.heynaveed.game.StruddyBird;
import com.heynaveed.game.sprites.GameBird;
import com.heynaveed.game.sprites.Tube;

/**
 * Created by Naveed PC on 12/06/2016.
 */
public class PlayState extends State {

    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -50;
    private static final int BEGINNING_OFFSET = 2;
    private static final float SCORE_TIMER = 3;
    private static final float SCORE_SCALE_MAX = 0.2f;

    private final Sound coinGet, clang, slit, flap, warp;
    private final Texture bg, ground, gameOverTexture;
    private final GameBird gameBird;
    private final Vector2 groundPos1, groundPos2;
    private final BitmapFont scoreFont, highScoreFont;
    private final GlyphLayout scoreLayout, highScoreLayout;
    private Stage stage;
    private ImageButton restartButton;
    private ImageButton.ImageButtonStyle restartButtonStyle;
    private Skin restartSkin;
    private TextureAtlas restartAtlas;
    private final Array<Tube> tubes;
    private float birdRotation;

    private boolean hasLanded = false, hasPlayed = false, hasLost = false, scoreMoved = false,
            isNewHighscore = false, hasWarpPlayed = false, hasRestartBeenAdded = false,
            isScoreGettingBigger = true, isFadingOut = false, batchReset = false, restartAdded = false;
    private int tubeSpacing = 125;
    private int score = 0, blinkCount = 0, highestScore = 0;
    private float scorePosX, scorePosY;
    private float scaleTip = 0;
    private float scoreScale = 0.9f;
    private float batchAlpha = 1.0f;
    private float dt;


    public PlayState(GameStateManager gsm) {
        super(gsm);
        bg = new Texture("bg2.png");
        gameBird = new GameBird(60, 300, gsm.getDifficulty());
        ground = new Texture("ground.png");
        tubes = new Array<Tube>();
        groundPos1 = new Vector2(cam.position.x - (cam.viewportWidth/2), GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - (cam.viewportWidth/2)) + ground.getWidth(), GROUND_Y_OFFSET);
        scoreFont = new BitmapFont(Gdx.files.internal("sb_font.fnt"));
        highScoreFont = new BitmapFont(Gdx.files.internal("sb_font.fnt"));
        scoreLayout = new GlyphLayout(scoreFont, Integer.toString(score));
        highScoreLayout = new GlyphLayout();
        gameOverTexture = new Texture("gameOver.png");
        coinGet = Gdx.audio.newSound(Gdx.files.internal("coinGet.ogg"));
        clang = Gdx.audio.newSound(Gdx.files.internal("clang.wav"));
        slit = Gdx.audio.newSound(Gdx.files.internal("slit.wav"));
        flap = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));
        warp = Gdx.audio.newSound(Gdx.files.internal("warp.wav"));

        cam.setToOrtho(false, StruddyBird.WIDTH/2, StruddyBird.HEIGHT/2);
        scoreFont.setColor(new Color(1.0f, 0.06f, 0.13f, 0.8f));
        scoreFont.setUseIntegerPositions(false);
        scoreFont.getData().setScale(scoreScale, scoreScale);
        highScoreFont.setColor(MenuState.TEXT_RED);
        highScoreFont.getData().setScale(0.3f, 0.3f);
        highScoreFont.setUseIntegerPositions(false);
        highScoreLayout.setText(highScoreFont, "High Score: ");

        if(gsm.getDifficulty() == StruddyBird.HARD)
            tubeSpacing = 150;

        for(int i = BEGINNING_OFFSET; i < TUBE_COUNT+BEGINNING_OFFSET; i++)
            tubes.add(new Tube(i*(tubeSpacing + Tube.TUBE_WIDTH), gsm.getDifficulty()));

        initialiseRestartButton();
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && !hasLost){
            if(gsm.getIsFxOn())
                flap.play(1.2f);
            gameBird.jump();
        }
    }

    @Override
    public void update(float dt) {
        this.dt = dt;
        handleInput();
        gameBird.update(dt);
        if(!hasLost) {
            updateGround();
            applyCameraRepositioning();
            applyTubeLogic();
            applyMaximumScore();
        }
        applyScorePosition(dt);
        checkForHighestScore();
        checkForGroundCollision();
        applyBirdRotation();
        checkHighScoreMusic();
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {

        if(!batchReset){
            scoreFont.setColor(new Color(1.0f, 0.06f, 0.13f, 1.0f));
            highScoreFont.setColor(new Color(1.0f, 0.06f, 0.13f, 1.0f));
            sb.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            for (int i = 0; i < stage.getActors().size; i++)
                stage.getActors().get(i).setColor(1.0f, 1.0f, 1.0f, 1.0f);

            batchReset = true;
        }

        if(isFadingOut && batchAlpha > 0.0f) {
            scoreFont.setColor(new Color(1.0f, 0.06f, 0.13f, batchAlpha));
            highScoreFont.setColor(new Color(1.0f, 0.06f, 0.13f, batchAlpha));
            sb.setColor(1.0f, 1.0f, 1.0f, batchAlpha);
            for (int i = 0; i < stage.getActors().size; i++)
                stage.getActors().get(i).setColor(1.0f, 1.0f, 1.0f, batchAlpha);

            batchAlpha -= StruddyBird.FADE_SPEED*dt;

            if(batchAlpha < 0.0f){
                gsm.set(new MenuState(gsm));
            }
        }

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0, cam.viewportWidth, cam.viewportHeight);

        for(Tube tube: tubes){
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }

        sb.draw(gameBird.getTexture(), gameBird.getPosition().x, gameBird.getPosition().y, gameBird.getTexture().getRegionWidth()/2,
                gameBird.getTexture().getRegionHeight()/2, gameBird.getTexture().getRegionWidth(),
                gameBird.getTexture().getRegionHeight(), 1, 1, birdRotation);
        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        scoreFont.draw(sb, Integer.toString(score), scorePosX, scorePosY);

        if(hasLost)
            sb.draw(gameOverTexture, cam.position.x - (gameOverTexture.getWidth() / 2), cam.viewportHeight / 1.5f);

        if((!hasLost) || (hasLost && !isNewHighscore) || (isNewHighscore && blinkCount < 25))
            highScoreFont.draw(sb, "High Score: " + highestScore, cam.position.x - cam.viewportWidth/2 + 4, cam.viewportHeight-4);

        sb.end();

        if(hasLost)
            stage.draw();
    }

    @Override
    public void dispose() {
        bg.dispose();
        ground.dispose();
        gameOverTexture.dispose();
        highScoreFont.dispose();
        scoreFont.dispose();
        stage.dispose();
        restartSkin.dispose();
        restartAtlas.dispose();
        flap.dispose();
        warp.dispose();
        coinGet.dispose();
        clang.dispose();
        slit.dispose();
        for(Tube tube: tubes){
            tube.dispose();
        }
    }

    private void applyScorePosition(float dt){
        boolean xDone = false, yDone = false;

        if(!hasLanded){
            scorePosX = cam.position.x - cam.viewportWidth/2 + 4;
            scorePosY = scoreLayout.height + 4;
        }
        else if(isNewHighscore && !scoreMoved){
            if(scorePosX < (cam.position.x - scoreLayout.width/2))
                scorePosX += ((cam.position.x - scoreLayout.width/2) - (cam.position.x - cam.viewportWidth/2 + 4)/ SCORE_TIMER)*dt;
            else {
                xDone = true;
                yDone = true;
            }

            if(scorePosY < cam.position.y)
                scorePosY += (cam.position.y - ((scoreLayout.height + 4)/ SCORE_TIMER))*dt;
            else {
                xDone = true;
                yDone = true;
            }

            scoreFont.getData().setScale(scoreScale += 1.25f * dt, scoreScale += 1.25f * dt);
            scoreLayout.setText(scoreFont, Integer.toString(score));
        }
        else{
            scoreMoved = true;
        }

        if(xDone && yDone){
            scoreMoved = true;
            scorePosX = cam.position.x - scoreLayout.width/2;
            scorePosY = cam.position.y + scoreLayout.height/2;
        }

        if(scoreMoved && isNewHighscore){
            if(scaleTip < SCORE_SCALE_MAX && isScoreGettingBigger){
                scoreFont.getData().setScale(scoreScale += 0.2f * dt, scoreScale += 0.2f * dt);
                scoreLayout.setText(scoreFont, Integer.toString(score));
                scaleTip += 1.25f * dt;

                if(scaleTip > SCORE_SCALE_MAX)
                    isScoreGettingBigger = false;
            }
            else if(scaleTip > -SCORE_SCALE_MAX && !isScoreGettingBigger){
                scoreFont.getData().setScale(scoreScale -= 0.2f * dt, scoreScale -= 0.2f * dt);
                scoreLayout.setText(scoreFont, Integer.toString(score));
                scaleTip -= 1.25f * dt;

                if(scaleTip < -SCORE_SCALE_MAX)
                    isScoreGettingBigger = true;
            }
        }
    }

    private void updateGround(){
        if(cam.position.x - (cam.viewportWidth/2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth() * 2, 0);
        if(cam.position.x - (cam.viewportWidth/2) > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth() * 2, 0);
    }

    private void applyCameraRepositioning(){
        cam.position.x = gameBird.getPosition().x + 80;
        scoreLayout.setText(scoreFont, Integer.toString(score));
    }

    private void applyTubeLogic(){
        for (Tube tube : tubes) {
            if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth())
                tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + tubeSpacing) * TUBE_COUNT));

            if (tube.tubeCollides(gameBird.getBounds()) || gameBird.getPosition().y > 500) {
                if(gsm.getIsFxOn())
                    clang.play(0.9f);
                hasLost = true;
                gameBird.impactJump();
                checkForHighScore();
                break;
            }

            if (tube.scoreCollides(gameBird.getBounds()) && !tube.getIsPassed()) {
                score++;
                tube.setIsPassed(true);
                if(gsm.getIsFxOn())
                    coinGet.play(0.2f);
            }
        }
    }

    private void applyMaximumScore(){
        if (score >= 1000)
            score = 999;
    }

    private void initialiseRestartButton(){
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        restartSkin = new Skin();
        restartAtlas = new TextureAtlas(Gdx.files.internal("restartButton.atlas"));
        restartSkin.addRegions(restartAtlas);
        restartButtonStyle = new ImageButton.ImageButtonStyle();
        restartButtonStyle.up = restartSkin.getDrawable("restartButtonUp");
        restartButtonStyle.down = restartSkin.getDrawable("restartButtonDown");
        restartButton = new ImageButton(restartButtonStyle);
        restartButton.setWidth(Gdx.graphics.getWidth()/2.29f);
        restartButton.setHeight(Gdx.graphics.getHeight()/8.09f);
        restartButton.setPosition(Gdx.graphics.getWidth()/2 - (restartButton.getWidth()/2), Gdx.graphics.getHeight()*0.1f);
    }

    private void checkForGroundCollision(){
        if(gameBird.getPosition().y < ground.getHeight() + GROUND_Y_OFFSET - gameBird.getTexture().getRegionHeight()*0.05f){
            gameBird.getPosition().y = ground.getHeight() + GROUND_Y_OFFSET - gameBird.getTexture().getRegionHeight()*0.05f;
            hasLost = true;
            gameBird.setMovement(0);

            if(!hasLanded) {
                if(gsm.getIsFxOn())
                    slit.play(0.5f);
                hasLanded = true;
                checkForHighScore();
            }

            if(scoreMoved && !hasRestartBeenAdded) {
                stage.addActor(restartButton);
                hasRestartBeenAdded = true;
            }

            if(!restartAdded) {
                restartButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!hasPlayed) {
                            if (gsm.getIsFxOn())
                                StruddyBird.getButtonSwitch().play(StruddyBird.BUTTON_SWITCH_VOLUME);
                            hasPlayed = true;
                        }
                        isFadingOut = true;
//                    gsm.set(new MenuState(gsm));
                    }
                });
                restartAdded = true;
            }
        }
    }

    private void applyBirdRotation(){
        if(hasLanded)
            birdRotation = -90;
        else
            birdRotation = -gameBird.getRotation() + 40;
    }

    private void checkForHighestScore(){
        if(score < StruddyBird.getPreferences().getInteger(StruddyBird.PREF_KEYS[gsm.getDifficulty()]))
            highestScore = StruddyBird.getPreferences().getInteger(StruddyBird.PREF_KEYS[gsm.getDifficulty()]);
        else
            highestScore = score;

        if(hasLost && isNewHighscore){
            blinkCount++;
            if(blinkCount > 50)
                blinkCount = 0;
        }
    }

    private void checkForHighScore(){
        if(hasLost && score > StruddyBird.getPreferences().getInteger(StruddyBird.PREF_KEYS[gsm.getDifficulty()])) {
            StruddyBird.getPreferences().putInteger(StruddyBird.PREF_KEYS[gsm.getDifficulty()], score);
            StruddyBird.getPreferences().flush();
            isNewHighscore = true;
        }
    }

    private void checkHighScoreMusic(){
        if (!hasWarpPlayed && hasLanded && isNewHighscore){
            if(gsm.getIsFxOn())
                warp.play();
            hasWarpPlayed = true;
        }
    }
}
