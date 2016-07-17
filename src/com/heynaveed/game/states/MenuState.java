package com.heynaveed.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.heynaveed.game.StruddyBird;
import com.heynaveed.game.sprites.PrettyBird;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Brent on 6/26/2015.
 */
public class MenuState extends State{

    public static final int MAX_PRETTY_BIRDS = 4;
    public static final int MAX_FLIGHT_PATH = 7;
    private static final int EASY_MOVEMENT = 125;
    private static final int MEDIUM_MOVEMENT = 200;
    private static final int HARD_MOVEMENT = 275;
    private static final int BIRD_REGION_HEIGHT = 30;
    private static final float EASY_CYCLE = 0.5f;
    private static final float MEDIUM_CYCLE = 0.3f;
    private static final float HARD_CYCLE = 0.1f;
    public static final Color TEXT_RED = new Color(1.0f, 0.06f, 0.13f, 0.8f);
    private static final Color LEVEL_BUTTON_GRAY = new Color(0.49f, 0.65f, 0.63f, 0.9f);
    private static final Color TITLE_TEXT_GREEN = new Color(0.12f, 0.73f, 0.35f, 0.8f);

    private final Texture nightBackground;
    private final BitmapFont titleFont, levelFont, highScoreFont;
    private final GlyphLayout titleLayout, highScoreLayout;
    private final Stage stage;
    private ImageButton playButton, fxButton, musicButton;
    private final ImageButton.ImageButtonStyle playButtonStyle, fxButtonStyle, musicButtonStyle;
    private final Skin playSkin, easySkin, mediumSkin, hardSkin, fxSkin, musicSkin;
    private final TextureAtlas playAtlas, easyAtlas, mediumAtlas, hardAtlas, fxAtlas, musicAtlas;
    private TextButton easyButton, mediumButton, hardButton;
    private final TextButton.TextButtonStyle easyButtonStyle, mediumButtonStyle, hardButtonStyle;
    private final ArrayList<Boolean> flightPath;
    private final Array<PrettyBird> birds;
    private final Random random;
    private float dt = 0;
    private float batchAlpha = 0.0f;
    private boolean isFadingIn = true;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        stage = new Stage();
        nightBackground = new Texture("bg2.png");
        titleFont = new BitmapFont(Gdx.files.internal("sb_font.fnt"));
        levelFont = new BitmapFont(Gdx.files.internal("sb_font.fnt"));
        highScoreFont = new BitmapFont(Gdx.files.internal("sb_font.fnt"));
        birds = new Array<PrettyBird>();
        flightPath = new ArrayList<Boolean>();
        random = new Random();
        titleLayout = new GlyphLayout();
        highScoreLayout = new GlyphLayout();
        playSkin = new Skin();
        playAtlas = new TextureAtlas(Gdx.files.internal("playButton.pack"));
        fxSkin = new Skin();
        fxAtlas = new TextureAtlas(Gdx.files.internal("fx.atlas"));
        musicSkin = new Skin();
        musicAtlas = new TextureAtlas(Gdx.files.internal("music.atlas"));
        easySkin = new Skin();
        easyAtlas = new TextureAtlas(Gdx.files.internal("levelButton.atlas"));
        mediumSkin = new Skin();
        mediumAtlas = new TextureAtlas(Gdx.files.internal("levelButton.atlas"));
        hardSkin = new Skin();
        hardAtlas = new TextureAtlas(Gdx.files.internal("levelButton.atlas"));
        playButtonStyle = new ImageButton.ImageButtonStyle();
        fxButtonStyle = new ImageButton.ImageButtonStyle();
        musicButtonStyle = new ImageButton.ImageButtonStyle();
        easyButtonStyle = new TextButton.TextButtonStyle();
        mediumButtonStyle = new TextButton.TextButtonStyle();
        hardButtonStyle = new TextButton.TextButtonStyle();

        cam.setToOrtho(false, StruddyBird.WIDTH / 2, StruddyBird.HEIGHT / 2);
        titleFont.setColor(TITLE_TEXT_GREEN);
        titleFont.getData().setScale(0.7f, 0.7f);
        titleLayout.setText(titleFont, StruddyBird.TITLE);
        highScoreFont.setColor(TEXT_RED);
        highScoreFont.getData().setScale(0.3f, 0.3f);
        highScoreFont.setUseIntegerPositions(false);
        highScoreLayout.setText(highScoreFont, "High Score: ");
        Gdx.input.setInputProcessor(stage);

        initialisePrettyBirds();
        initialisePlayButton();
        initialiseMusicButton();
        initialiseFXButton();
        initialiseEasyButton();
        initialiseMediumButton();
        initialiseHardButton();
        initialiseDifficultyLevel();

        StruddyBird.getMusic().play();
    }

    private void initialisePrettyBirds(){

        for(int i = 0; i < MAX_PRETTY_BIRDS; i++){
            boolean isGoingLeft;
            int xPos;
            if(i % 2 == 0){
                isGoingLeft = true;
                xPos = random.nextInt(StruddyBird.WIDTH*2) - StruddyBird.WIDTH/2;
            }
            else{
                isGoingLeft = false;
                xPos = random.nextInt(StruddyBird.WIDTH*2) - StruddyBird.WIDTH/2;
            }
            PrettyBird pB = new PrettyBird(xPos, 0, isGoingLeft);
            birds.add(pB);
        }

        for(int i = 0; i < MAX_FLIGHT_PATH; i++){
            flightPath.add(false);
        }

        for(int i = 0; i < MAX_PRETTY_BIRDS; i++){
            birds.set(i, trafficController(birds.get(i)));
        }
    }

    private void initialisePlayButton(){
        playSkin.addRegions(playAtlas);
        playButtonStyle.up = playSkin.getDrawable("playButtonUp");
        playButtonStyle.down = playSkin.getDrawable("playButtonDown");
        playButton = new ImageButton(playButtonStyle);
        playButton.setWidth(Gdx.graphics.getWidth()/2.29f);
        playButton.setHeight(Gdx.graphics.getHeight()/8.09f);
        playButton.setPosition(Gdx.graphics.getWidth()/2 - (playButton.getWidth()/2), Gdx.graphics.getHeight()*0.1f);
        stage.addActor(playButton);

        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                checkForButtonSound();
                gsm.set(new PlayState(gsm));
            }
        });
    }

    private void initialiseMusicButton(){
        musicSkin.addRegions(musicAtlas);
        musicButtonStyle.up = musicSkin.getDrawable("musicOff");
        musicButtonStyle.down = musicSkin.getDrawable("musicOn");
        musicButtonStyle.checked = musicSkin.getDrawable("musicOn");
        musicButton = new ImageButton(musicButtonStyle);
        musicButton.setWidth(Gdx.graphics.getWidth()/7.5f);
        musicButton.setHeight(Gdx.graphics.getWidth()/7.5f);
        musicButton.setPosition(Gdx.graphics.getWidth() - Gdx.graphics.getWidth()/1.25f - musicButton.getWidth(), Gdx.graphics.getHeight()*0.1f + musicButton.getHeight()/4);

        if(gsm.getIsMusicOn())
            musicButton.setChecked(true);
        else
            musicButton.setChecked(false);

        stage.addActor(musicButton);

        musicButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!musicButton.isChecked()){
                    gsm.setIsMusicOn(false);
                    checkForButtonSound();
                    StruddyBird.getPreferences().putInteger(StruddyBird.PREF_KEYS[4], 0);
                }
                else{
                    gsm.setIsMusicOn(true);
                    checkForButtonSound();
                    StruddyBird.getPreferences().putInteger(StruddyBird.PREF_KEYS[4], 1);
                }
                StruddyBird.getPreferences().flush();
            }
        });
    }

    private void initialiseFXButton(){
        fxSkin.addRegions(fxAtlas);
        fxButtonStyle.up = fxSkin.getDrawable("fxOff");
        fxButtonStyle.down = fxSkin.getDrawable("fxOn");
        fxButtonStyle.checked = fxSkin.getDrawable("fxOn");
        fxButton = new ImageButton(fxButtonStyle);
        fxButton.setWidth(Gdx.graphics.getWidth()/7.5f);
        fxButton.setHeight(Gdx.graphics.getWidth()/7.5f);
        fxButton.setPosition(Gdx.graphics.getWidth()/1.25f, Gdx.graphics.getHeight()*0.1f + fxButton.getHeight()/4);

        if(gsm.getIsFxOn())
            fxButton.setChecked(true);
        else
            fxButton.setChecked(false);

        stage.addActor(fxButton);

        fxButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if(!fxButton.isChecked()){
                    gsm.setIsFxOn(false);
                    checkForButtonSound();
                    StruddyBird.getPreferences().putInteger(StruddyBird.PREF_KEYS[5], 0);
                }
                else{
                    gsm.setIsFxOn(true);
                    checkForButtonSound();
                    StruddyBird.getPreferences().putInteger(StruddyBird.PREF_KEYS[5], 1);
                }
                StruddyBird.getPreferences().flush();
            }
        });
    }

    private void initialiseEasyButton(){
        easyButton = initialiseLevelButton(easySkin, easyAtlas, easyButton, easyButtonStyle, "EASY", 0.71f);
        stage.addActor(easyButton);
        easyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                easyButton.setChecked(true);
                mediumButton.setChecked(false);
                hardButton.setChecked(false);
                gsm.setDifficulty(StruddyBird.EASY);
                checkForButtonSound();

                updateDifficulty();
                changeAnimation(EASY_MOVEMENT, EASY_CYCLE);
            }
        });
    }

    private void initialiseMediumButton(){
        mediumButton = initialiseLevelButton(mediumSkin, mediumAtlas, mediumButton, mediumButtonStyle, "MEDIUM", 0.65f);
        stage.addActor(mediumButton);
        mediumButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                easyButton.setChecked(false);
                mediumButton.setChecked(true);
                hardButton.setChecked(false);
                gsm.setDifficulty(StruddyBird.MEDIUM);
                checkForButtonSound();

                updateDifficulty();
                changeAnimation(MEDIUM_MOVEMENT, MEDIUM_CYCLE);
            }
        });
    }

    private void initialiseHardButton(){
        hardButton = initialiseLevelButton(hardSkin, hardAtlas, hardButton, hardButtonStyle, "HARD", 0.59f);
        stage.addActor(hardButton);
        hardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                easyButton.setChecked(false);
                mediumButton.setChecked(false);
                hardButton.setChecked(true);
                gsm.setDifficulty(StruddyBird.HARD);
                checkForButtonSound();

                updateDifficulty();
                changeAnimation(HARD_MOVEMENT, HARD_CYCLE);
            }
        });
    }

    private Skin initialiseLevelSkin(Skin skin, TextureAtlas atlas){
        skin.addRegions(atlas);
        return skin;
    }

    private TextButton.TextButtonStyle initialiseLevelButtonStyle(Skin skin, TextButton.TextButtonStyle buttonStyle){
        buttonStyle.font = levelFont;
        buttonStyle.up = skin.getDrawable("levelButtonUp");
        buttonStyle.down = skin.getDrawable("levelButtonDown");
        buttonStyle.fontColor = TEXT_RED;
        buttonStyle.downFontColor = LEVEL_BUTTON_GRAY;
        buttonStyle.checkedFontColor = LEVEL_BUTTON_GRAY;

        return buttonStyle;
    }

    private TextButton initialiseLevelButton(Skin skin, TextureAtlas atlas, TextButton textButton, TextButton.TextButtonStyle buttonStyle, String level, float yPosScale){
        textButton = new TextButton(level, initialiseLevelButtonStyle(initialiseLevelSkin(skin, atlas), buttonStyle));
        textButton.setWidth(Gdx.graphics.getWidth()/2.5f);
        textButton.setHeight(Gdx.graphics.getHeight()/21);
        textButton.setPosition(Gdx.graphics.getWidth()/2 - (textButton.getWidth()/2), Gdx.graphics.getHeight()*yPosScale);
        textButton.getLabel().setFontScale(textButton.getWidth()/240, textButton.getHeight()/60);
        return textButton;
    }

    private void initialiseDifficultyLevel(){
        if(gsm.getDifficulty() == StruddyBird.EASY){
            easyButton.setChecked(true);
            changeAnimation(EASY_MOVEMENT, EASY_CYCLE);
        }
        else if(gsm.getDifficulty() == StruddyBird.MEDIUM){
            mediumButton.setChecked(true);
            changeAnimation(MEDIUM_MOVEMENT, MEDIUM_CYCLE);
        }
        else if(gsm.getDifficulty() == StruddyBird.HARD){
            hardButton.setChecked(true);
            changeAnimation(HARD_MOVEMENT, HARD_CYCLE);
        }
    }

    private void changeAnimation(int movement, float speed){
        for(PrettyBird pB: birds){
            pB.setMovement(movement);
            pB.getAnimation().changeAnimationSpeed(speed);
        }
    }

    private PrettyBird trafficController(PrettyBird pB){
        int yPos;
        do {
            yPos = random.nextInt(MAX_FLIGHT_PATH) + 1;
        }
        while (flightPath.get(yPos - 1));

        flightPath.set(yPos - 1, true);

        pB.getPosition().y = yPos * BIRD_REGION_HEIGHT + 130;

        return pB;
    }

    private void updateDifficulty(){
        StruddyBird.getPreferences().putInteger(StruddyBird.PREF_KEYS[3], gsm.getDifficulty());
        StruddyBird.getPreferences().flush();
    }

    private void checkForButtonSound(){
        if(gsm.getIsFxOn())
            StruddyBird.getButtonSwitch().play(StruddyBird.BUTTON_SWITCH_VOLUME);
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {
        this.dt = dt;

        for(int i = 0; i < birds.size; i++) {
            birds.get(i).update(dt);

            if(birds.get(i).getIsGoingLeft() && birds.get(i).getPosition().x < -50){
                flightPath.set((((int)birds.get(i).getPosition().y - 130) / BIRD_REGION_HEIGHT)-1, false);
                birds.get(i).getPosition().x = random.nextInt(StruddyBird.WIDTH/2)+StruddyBird.WIDTH;
                birds.set(i, trafficController(birds.get(i)));
            }
            else if(!birds.get(i).getIsGoingLeft() && birds.get(i).getPosition().x > 450){
                flightPath.set((((int)birds.get(i).getPosition().y - 130) / BIRD_REGION_HEIGHT)-1, false);
                birds.get(i).getPosition().x = random.nextInt(StruddyBird.WIDTH/2)-StruddyBird.WIDTH;
                birds.set(i, trafficController(birds.get(i)));
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        if(isFadingIn && batchAlpha < 1.0f) {

            for (int i = 0; i < stage.getActors().size; i++)
                stage.getActors().get(i).setColor(1.0f, 1.0f, 1.0f, batchAlpha);

            titleFont.setColor(0.12f, 0.73f, 0.35f, batchAlpha);
            highScoreFont.setColor(1.0f, 0.06f, 0.13f, batchAlpha);
            sb.setColor(1.0f, 1.0f, 1.0f, batchAlpha);

            batchAlpha += StruddyBird.FADE_SPEED*dt;

            if(batchAlpha > 1.0f){
                batchAlpha = 1.0f;
                isFadingIn = false;
            }
        }

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(nightBackground, 0, 0, cam.viewportWidth, cam.viewportHeight);
        for(PrettyBird pB: birds) {
            sb.draw(pB.getTexture(), pB.getPosition().x, pB.getPosition().y);
        }
        titleFont.draw(sb, StruddyBird.TITLE, cam.position.x - (titleLayout.width/2), cam.position.y*1.75f);
        highScoreFont.draw(sb, "High Score: " + StruddyBird.getPreferences().getInteger(StruddyBird.PREF_KEYS[gsm.getDifficulty()]), cam.position.x - cam.viewportWidth/2 + 4, cam.viewportHeight-4);
        sb.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        nightBackground.dispose();
        titleFont.dispose();
        levelFont.dispose();
        stage.dispose();
        playSkin.dispose();
        fxSkin.dispose();
        musicSkin.dispose();
        easySkin.dispose();
        mediumSkin.dispose();
        hardSkin.dispose();
        playAtlas.dispose();
        fxAtlas.dispose();
        musicAtlas.dispose();
        easyAtlas.dispose();
        mediumAtlas.dispose();
        hardAtlas.dispose();
        highScoreFont.dispose();

        for(PrettyBird pB: birds){
            pB.dispose();
        }
    }
}