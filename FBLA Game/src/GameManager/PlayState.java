// The main playing GameState.
/*
 * 
 * 
 * move line around a space while collecting coins and avoiding spikes
 * 
 * 
 */

package GameManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

public class PlayState extends GameState {
	
private Player p;
	public static BufferedImage bg;
	public static int level;
	private int coinsLeft;
	private ArrayList<Point> coinPoints;
	private ArrayList<Rectangle> obstacles;
	private Rectangle phitbox;
	private ArrayList<Point> phitboxPoints;
	private Rectangle noObstacles;
	private static ArrayList<String> facts;
	private int fps = 20;

	public PlayState(GameStateManager gsm) {
		super(gsm);
		// TODO Auto-generated constructor stub
	}
	
	public static String getFact()
	{
		
		return facts.get(level);
		
	}
	

	@Override
	//init() method is basically just a constructor for this. it's just easier to understand because it stands for initialization.
	public void init() {
		// TODO Auto-generated method stub
		level = 0;
		fps = 20;
		GamePanel.setFPS(fps);
		try {
			bg = ImageIO.read(new File("src/GameManager/icetexture.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p = new Player(GamePanel.WIDTH/2, GamePanel.HEIGHT/2);
		
		obstacles = new ArrayList<Rectangle>();
		noObstacles = new Rectangle(GamePanel.WIDTH/2-15, GamePanel.HEIGHT/2-30, 30, 60);
		
		coinsLeft = 0;
		coinPoints = new ArrayList<Point>();
		phitbox = new Rectangle(p.getX()-4, p.getY()-4, 9, 9);
		phitboxPoints = new ArrayList<Point>();
		
		facts = new ArrayList<String>();
		
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(new File("src/GameManager/randomfacts.txt"));
			br = new BufferedReader(fr);
			String s = "";
			while ((s = br.readLine()) != null)
			{
				
				facts.add(s);
				
			}
			fr.close();
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collections.shuffle(facts, new Random(System.nanoTime()));
		
		
		
		
		
	}
	
	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		GamePanel.setFPS(fps);
		if (p.getX() >= 0 && p.getY() >= 0 && p.getX() <= GamePanel.WIDTH && p.getY() <= GamePanel.HEIGHT)
		{
			p.move();
		}
		else
		{
			p.rotateRight();
			p.rotateRight();
			p.move();
		}
		
		
		phitbox.setBounds(p.getX()-4, p.getY()-4, 9, 9);
		phitboxPoints.clear();
		for (int i = p.getX()-3; i <= p.getX()+3; i++)
		{
			
			phitboxPoints.add(new Point(i, p.getY()-3));
			phitboxPoints.add(new Point(i, p.getY()-2));
			phitboxPoints.add(new Point(i, p.getY()-1));
			phitboxPoints.add(new Point(i, p.getY()));
			phitboxPoints.add(new Point(i, p.getY()+1));
			phitboxPoints.add(new Point(i, p.getY()+2));
			phitboxPoints.add(new Point(i, p.getY()+3));
			
		}
		
		
		
		
		for (Iterator<Point> it = coinPoints.iterator(); it.hasNext();)
		{
			
			Point point = it.next();
			
			if (phitbox.contains(point))
			{
				
				it.remove();
				
				
			}
			
		}
		
		for (Rectangle r : obstacles)
		{
			
			for (Point p : phitboxPoints)
			{
				
				if (r.contains(p))
				{
					
					gsm.setState(GameStateManager.GAMEOVER);
					
				}
				
			}
			
		}
		
		coinsLeft = coinPoints.size();
		
		if (coinsLeft == 0)
		{
			nextLevel();
		}
		
		
		
		handleInput();
		
		
	}
	
	public void nextLevel()
	{
		level++;
		
		p.setX(GamePanel.WIDTH/2);
		p.setY(GamePanel.HEIGHT/2);
		p.setDirection("up");
		int i = 0;
		while (i < 10)
		{
			coinPoints.add(new Point(ThreadLocalRandom.current().nextInt(10, GamePanel.WIDTH - 10), ThreadLocalRandom.current().nextInt(10, GamePanel.HEIGHT - 10)));
			i++;
		}
		obstacles.clear();
		i = 0;
		while (i < 7)
		{
			Point point = new Point(ThreadLocalRandom.current().nextInt(10, GamePanel.WIDTH - 10), ThreadLocalRandom.current().nextInt(10, GamePanel.HEIGHT - 10));
			Rectangle r = new Rectangle((int) point.getX(), (int) point.getY(), 4, 4);
			obstacles.add(r);
			
				
				if (noObstacles.contains(point))
				{
					
					obstacles.remove(r);
					
				}
				
				
			
			i++;
		}
		
		for (Iterator<Rectangle> it = obstacles.iterator(); it.hasNext();)
		{
			
			Rectangle r = it.next();
			
			for (Point p : coinPoints)
			{
				
				
				if (r.contains(p))
				{
					
					it.remove();
					
				}
				
			}
			
		}
		
		
		
		
		
		fps += 10;
		
		
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
		g.setColor(Color.BLACK);
		g.drawImage(bg, 0, 0, 250, 180, null);
		g.setColor(Color.ORANGE);
		g.drawOval(p.getX()-3, p.getY()-3, 7, 7);
		g.setColor(Color.RED);
		g.fillOval(p.getX()-3, p.getY()-3, 7, 7);
		
		g.setColor(Color.YELLOW);
		for (Point point : coinPoints)
		{
			
			g.fillOval((int) point.getX()-2, (int) point.getY()-2, 5, 5);
			
		}
		
		for (Rectangle r : obstacles)
		{
			
			g.setColor(Color.RED);
			g.fill(r);
			
		}
		
		
	}
	
	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
		if (Keys.isPressed(Keys.LEFT)){
			
			p.rotateLeft();
			
		}
		if (Keys.isPressed(Keys.RIGHT)){

			p.rotateRight();
			
		}
		if (Keys.isPressed(Keys.ESCAPE))
		{
			gsm.setPaused(true);
		}
		
	}
	
	public static int getLevel()
	{
		return level;
	}
	

	
	
}
