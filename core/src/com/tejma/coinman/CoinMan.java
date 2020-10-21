package com.tejma.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int birdState= 0;
	int pause = 0;
	float gravity = 0.4f;
	float velocity = 0;
	int birdY = 0;
	Rectangle birdRectangle;
	BitmapFont font, start;
	int score = 0;
	int speedCoin = 6;
	int gameState;
	Texture dizzy;
	float fontY;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<>();
	Texture bomb;
	int bombCount;
	Random random;
	int night = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg4.png");

		//Texture Array to store different frames
		man = new Texture[8];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		man[4] = new Texture("frame-5.png");
		man[5] = new Texture("frame-6.png");
		man[6] = new Texture("frame-7.png");
		man[7] = new Texture("frame-8.png");
		birdY = Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		birdRectangle = new Rectangle();
		random = new Random();
		dizzy = new Texture("dizzy-1.png");

		gameState = 0;

		font = new BitmapFont();
		font.setColor(Color.YELLOW);
		font.getData().setScale(10);

		start = new BitmapFont();
		start.setColor(Color.YELLOW);
		start.getData().setScale(5);
		fontY = start.getRegion().getRegionHeight();
	}

	public void makeCoin()
	{
		//Create coins between bounds of screen
		float height = random.nextFloat()*((Gdx.graphics.getHeight()-coin.getHeight())-130)+130;
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb()
	{
		//Create bombs between bounds of screen
		float height = random.nextFloat()*((Gdx.graphics.getHeight()-coin.getHeight())-130)+130;
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){
			//GAME RUNNING

			//Change speed after every theme change
			if(night==5100){
				speedCoin++;
			}

			night++;

			//Change theme
			if(night<=5000){
				background = new Texture("bg4.png");
			}else {
				background = new Texture("bg4_night.png");
				if(night==10000)
					night = 0;
			}

			//BOMBS
			if(bombCount<250){
				bombCount++;
			}else{
				bombCount = 0;
				makeBomb();
			}

			//move bombs
			bombRectangle.clear();
			for (int i=0; i<bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i)-(speedCoin+3));
				bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//COINS
			if(coinCount<100){
				coinCount++;
			}else{
				coinCount = 0;
				makeCoin();
			}

			//move coins
			coinRectangle.clear();
			for (int i=0; i<coinYs.size(); i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i)-speedCoin);
				coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			//go up on touch
			if(Gdx.input.justTouched()){
				velocity = -15;
			}

			//update running action
			if(pause < 5){
				pause++;
			}else {
				pause = 0;
				if (birdState < 7)
					birdState++;
				else
					birdState = 0;
			}

			//fall when not touched
			velocity += gravity;
			birdY -= velocity;

			// adjust out of window situations
			if(birdY <= 50)
				gameState = 2;
			if(birdY >= Gdx.graphics.getHeight()-man[birdState].getHeight())
				birdY = Gdx.graphics.getHeight()-man[birdState].getHeight();

			//SCORE BOARD
			font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()-2*fontY);

		} else if(gameState==0) {
			//GAME RESET
			start.setColor(Color.YELLOW);
			start.draw(batch, "TAP TO PLAY", 100, (Gdx.graphics.getHeight()-2*fontY));
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		} else if(gameState==2){
			//GAME OVER
			start.setColor(Color.RED);
			start.draw(batch, "GAME OVER SCORE: "+score, 100, (Gdx.graphics.getHeight()-2*fontY));
			if(Gdx.input.justTouched()){
				//Reset Everything
				gameState = 0;
				birdY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombRectangle.clear();
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
				night = 0;
				speedCoin = 6;
				background = new Texture("bg4.png");
			}
		}

		//DRAW MAN ACC TO GAME STATE
		if(gameState==2){
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[birdState].getWidth() / 2, birdY);
		}else {
			batch.draw(man[birdState], Gdx.graphics.getWidth() / 2 - man[birdState].getWidth() / 2, birdY);
		}
		birdRectangle = new Rectangle(Gdx.graphics.getWidth()/2-man[birdState].getWidth()/2, birdY, man[birdState].getWidth(), man[birdState].getHeight());

		//UPDATE SCORE AND REMOVE COIN
		for(int i=0; i<coinRectangle.size(); i++){
			if(Intersector.overlaps(birdRectangle, coinRectangle.get(i))){
				score++;
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		//GAME OVER WHEN BOMB HIT
		for(int i=0; i<bombRectangle.size(); i++){
			if(Intersector.overlaps(birdRectangle, bombRectangle.get(i))){
				gameState = 2;
			}
		}
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
