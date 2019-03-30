package dev.game;

import dev.game.display.Display;
import dev.game.gfx.Assets;
import dev.game.gfx.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game implements Runnable {

	private Display display;
	public int width, height;
	public String title;
	private boolean showFPS = false;
	private boolean running = false;
	private Thread thread;

	/* A way for the computer to draw things to the screen */
	private BufferStrategy bs;
	private Graphics g;

	/* Add test properties here */
	private Zombie ella;
	private Plant maxim;
	private BufferedImage background;


	private static Tile[][] grid;
	private static Plant maximPlant;
	private boolean justPlanted;


	public Game(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}

	private void init() {
		display = new Display(title, width, height);
		Assets.init();
		ella = new Zombie(width, 25, -2, 0, Assets.zombie);
		maxim = new Plant(25, 25, 0, 0, Assets.plant);
		background = ImageLoader.loadImage("/backgrounds/lawn.png");

		fillGrid(6, 10, 200);

	}

	/* Updates to various objects happen here */
	private void tick() {
		ella.updatePos();
	}

	private void render() {
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		/* Draw graphics */
		g.clearRect(0, 0, width, height);
		g.drawImage(background, 0, 0, null);
		g.drawImage(ella.sprite, ella.posX, ella.posY, null);
		g.drawImage(maxim.sprite, maxim.posX, maxim.posY, null);
		for(int i = 0; i<grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if(!grid[i][j].empty){
					Plant p = grid[i][j].getPlant();
					g.drawImage(p.sprite, p.posX, p.posY, null);
				}

			}
		}
		bs.show();
		g.dispose();
	}

	/* Required for Runnable */
	public void run() {
		init();

		int FPS = 60;
		double timePerTick = 1000000000 / FPS;
		double delta = 0;
		long currentTime;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;

		/* Game loop */
		while (running) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / timePerTick;
			timer += currentTime - lastTime;
			lastTime = currentTime;

			if (delta >= 1) {
				tick();
				render();
				ticks++;
				delta--;
			}

			if (timer >= 1000000000) {
				if (showFPS) {
					System.out.println("Frames: " + ticks);
				}
				ticks = 0;
				timer = 0;
			}

		}

		stop();
	}

	public synchronized void start() {
		/* Ensures evm is not restarted */
		if (running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		/* Calls this.run() */
		thread.start();
	}

	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//Vertical and horizontal determine number of tiles in the grid, border the free space on the right
	public void fillGrid(int vertical, int horizontal, int border){
		grid = new Tile[vertical][horizontal];
		int w = (width-border)/horizontal;
		int h = height/vertical;
		for(int i = 0; i<vertical; i++){
			for(int j = 0; j<horizontal; j++){
				grid[i][j] = new Tile();
				grid[i][j].setPosition((border + j*w), (i*h));
				grid[i][j].setDimensions(w, h);
			}
		}
	}

	public static Tile[][] getGrid(){
		return grid;
	}

	//adds plant to tile which contains clicked coordinates
	public static void addPlant(int x, int y){
		maximPlant = new Plant(25, 25, 0, 0,ImageLoader.loadImage("/textures/plant.png"));
		for(int i = 0; i<grid.length; i++){
			for(int j = 0; j<grid[i].length; j++){
				int posX = grid[i][j].getPosX();
				int posY = grid[i][j].getPosY();
				int w = grid[i][j].getWidth();
				int h = grid[i][j].getHeight();
				if(x<(posX+w) && x>(posX) && y<(posY+h) && y>(posY)){
					grid[i][j].setPlant(new Plant(maximPlant, posX+25, posY+25));
					grid[i][j].empty = false;
				}

			}
		}
	}




}
