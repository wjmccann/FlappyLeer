package com.wjmccann.flappyleer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.Random;

public class FlappyLeer extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture bird;
	Texture topTube;
	Texture bottomTube;
	Texture gameover;
	Texture title;
	Sound sound;

	Circle birdCircle;
	ShapeRenderer shapeRenderer;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	int score = 106;
	int scoringTube = 0;
	int highscore = 0;

	BitmapFont font;
	BitmapFont hsFont;
	Label test;

	float birdY = 0;
	float velocity = 0;
	float gap = 600;
	float maxTubeOffset;

	float tubeVelocity = 4;

	int numberOfTubes = 4;
	float distanceBetweenTubes;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];

	Random randomGenerator;

	int gameState = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		bird = new Texture("turner.png");
		title = new Texture("title.png");
		sound = Gdx.audio.newSound(Gdx.files.internal("eh.mp3"));

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameover = new Texture("gameover.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		shapeRenderer = new ShapeRenderer();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		hsFont = new BitmapFont();
		hsFont.setColor(Color.WHITE);
		hsFont.getData().setScale(5);

		startGame();

	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - bird.getHeight() / 2;

		for (int i=0; i<numberOfTubes; i++){

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() /2){

				score++;

				Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube < numberOfTubes - 1){
					scoringTube++;
				} else {
					scoringTube = 0;
				}


			}

			if (Gdx.input.justTouched()){
				velocity = -20;
				sound.play(1.0f);
			}

			if (score == 5){
				font.draw(batch, "5.56mm FMJ", Gdx.graphics.getWidth() / 2 - 400, Gdx.graphics.getHeight() / 2 + 600);
			}

			if (score == 16){
				font.draw(batch, "A late 16", Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 + 600);
			}

			for (int i=0; i<numberOfTubes; i++) {
				 if (tubeX[i] < - topTube.getWidth()){

					 tubeX[i] += numberOfTubes * distanceBetweenTubes;
					 tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				 } else {
					 tubeX[i] = tubeX[i] - tubeVelocity;


				 }

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if (birdY > 0){
				velocity++;
				birdY -= velocity;
			} else {
				gameState = 2;
			}

		} else if (gameState == 0){
			batch.draw(title, Gdx.graphics.getWidth() / 2 - title.getWidth() / 2, birdY + 600);
			if (Gdx.input.justTouched()){
				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() /2);
			if (score > highscore){
				highscore = score;
			}
			if (Gdx.input.justTouched()){
				gameState = 0;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}



		batch.draw(bird, Gdx.graphics.getWidth() / 2 - bird.getWidth() / 2, birdY);

		if (score >= 106){
			font.draw(batch, "+", Gdx.graphics.getWidth() / 2 - bird.getWidth() / 2, birdY + 50);
		}

		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + bird.getHeight() / 2, bird.getWidth() / 2);

		font.draw(batch, String.valueOf(score),50, Gdx.graphics.getHeight() - 50 );

		hsFont.draw(batch, "High Score:",50, 100 );

		font.draw(batch, String.valueOf(highscore), 450, 150);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i=0; i<numberOfTubes; i++) {
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){

				gameState = 2;
			}

		}
		//shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
