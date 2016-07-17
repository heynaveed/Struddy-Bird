package com.heynaveed.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.heynaveed.game.states.GameStateManager;
import com.heynaveed.game.states.LoadState;


public class StruddyBird extends ApplicationAdapter {

	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static final int EASY = 0;
	public static final int MEDIUM = 1;
	public static final int HARD = 2;
	public static final String TITLE = "Struddy Bird";
	public static final String[] PREF_KEYS = {"EASY", "MEDIUM", "HARD", "DIFFICULTY", "MUSIC", "FX"};
	public static final float BUTTON_SWITCH_VOLUME = 0.4f;
	public static final float FADE_SPEED = 4.0f;
	private GameStateManager gsm;
	private SpriteBatch batch;
	private static Preferences preferences;

	protected static Music music;
	protected static Sound buttonSwitch;
	private int prefData[];

	@Override
	public void create () {
		gsm = new GameStateManager();
		batch = new SpriteBatch();
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		buttonSwitch = Gdx.audio.newSound(Gdx.files.internal("buttonSwitch.wav"));
		preferences = Gdx.app.getPreferences("Preferences");
		prefData = new int[6];
		music.setLooping(true);
		music.setVolume(0.5f);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		gsm.push(new LoadState(gsm));
		updatePreferences();
	}

	private void updatePreferences(){
		if(!preferences.contains(PREF_KEYS[0])){
			for(int i = 0; i < prefData.length; i++){
				if(i < 3)
					preferences.putInteger(PREF_KEYS[i], 0);
				else
					preferences.putInteger(PREF_KEYS[i], 1);
			}
			preferences.flush();
		}

		gsm.setDifficulty(preferences.getInteger(PREF_KEYS[3]));

		if(preferences.getInteger(PREF_KEYS[4]) == 1)
			gsm.setIsMusicOn(true);
		else
			gsm.setIsMusicOn(false);

		if(preferences.getInteger(PREF_KEYS[5]) == 1)
			gsm.setIsFxOn(true);
		else
			gsm.setIsFxOn(false);

//		preferences.clear();
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}

	@Override
	public void dispose() {
		super.dispose();
		music.dispose();
		buttonSwitch.dispose();
	}

	public static Preferences getPreferences(){
		return preferences;
	}

	public static Music getMusic(){
		return music;
	}

	public static Sound getButtonSwitch(){ return buttonSwitch; }
}
