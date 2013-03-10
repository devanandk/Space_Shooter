package com.shooter.game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.shooter.elements.Bullet;
import com.shooter.elements.EnemyShip;
import com.shooter.elements.SpaceShip;
import com.shooter.uimanagement.Animation;
import com.shooter.uimanagement.Core;
import com.shooter.uimanagement.Sprite;

@SuppressWarnings("unused")
public class GameLauncher extends Core implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, Runnable {
	
	private Animation shipAnimation;
	private Image deepSpaceBg;
	
	private SpaceShip spaceShip;
	private ArrayList<Bullet> bullets;
	private ArrayList<EnemyShip> enemies;
	
	private Point image;
	
	//private float spaceShipSpeed = 0.70F;
	private float bulletSpeed = 0.7F;
	private int backgroundScrollSpeed = 1;
	
	private Image bulletImg;
	private Animation bulletAnimation;
	private Bullet leftBulletSprite;
	private Bullet rightBulletSprite;
	
	private ArrayList<Image> enemyImg;
	private Animation enemyAnimation;
	
	private ArrayList<Image> explosionImg;
	private Animation ExplosionAnimation;
	private Sprite explosion;
	private long explosionTimer = 0;
	
	private int bulletCount = 0;
	private int EnemyCount = 0;
	private int EnemiesKilled = 0;
	private int playerScore = 0;
	
	private int health = 10;
//	private String resources = "src\\com\\shooter\\resources";
	private String resources = "images";
	
	private String[] enemyImages = {
			"/characters/enemy1.png",
			"/characters/enemy2.png",
			"/characters/enemy3.png",
			"/characters/enemy4.png"
	};
	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream input;
    
	private String message = "";
	
	
	
	public static void main(String[] args){
		new GameLauncher().run();
	}
	
	
	
	public void init(){
		super.init();
		try{
			Window w = screen.getFullScreenWindow();
			w.addMouseListener(this);
			w.addMouseMotionListener(this);
			w.addMouseWheelListener(this);
			w.addKeyListener(this);
			w.setFocusable(true);
			w.requestFocusInWindow();
		}
		catch(Exception e){ System.out.println("Exception 1"); }
		
		
		
		try{
			input = classLoader.getResourceAsStream(resources + "/background/deep_space.png");
			deepSpaceBg = ImageIO.read(input);
//			deepSpaceBg = new ImageIcon(getClass().getResource(resources + "/background/deep_space.png")).getImage();
			
			shipAnimation = new Animation();
			
			input = classLoader.getResourceAsStream(resources + "/characters/fightership_small.png");
			Image shot = ImageIO.read(input);
			shipAnimation.addScene(shot, 1000);
			
			spaceShip = new SpaceShip(shipAnimation);
			
			spaceShip.setX((screen.getWidth() / 2) - (spaceShip.getWidth() / 2));
			spaceShip.setY(screen.getHeight() - spaceShip.getHeight()-20);
			
			image = new Point();
			image.x = 0;
			image.y = 0;
			
			input = classLoader.getResourceAsStream(resources + "/characters/laser2_small.png");
			bulletImg = ImageIO.read(input);
			
			bulletAnimation = new Animation();
			bulletAnimation.addScene(bulletImg, 1000);
			
			bullets = new ArrayList<Bullet>();
			enemies = new ArrayList<EnemyShip>();
			enemyImg = new ArrayList<Image>();
			explosionImg = new ArrayList<Image>();
			
			for(int i = 0; i < enemyImages.length; i++){
			input = classLoader.getResourceAsStream(resources + enemyImages[i]);
			enemyImg.add(ImageIO.read(input));
			}
			enemyAnimation = new Animation();
			
			ExplosionAnimation = new Animation();
			for(int i = 1; i < 15; i++){
			
				input = classLoader.getResourceAsStream(resources + "/explosion/explosion" + i + ".png");
				Image explode = ImageIO.read(input);
				explosionImg.add(explode);
				ExplosionAnimation.addScene(explode, 25);
			}
			explosion = new Sprite(ExplosionAnimation);
						
		}
		catch(Exception e){ System.out.println("Exeption 2"); }
	}

	@Override
	public void update(long timePassed) {
		if(health <= 0){
			explosion = new Sprite(ExplosionAnimation);
			explosion.setX(spaceShip.getX() + 75);
			explosion.setY(spaceShip.getY() + 60);
			explosionTimer = System.currentTimeMillis();
			explosion.update(timePassed);
			return;
		}
		spaceShip.restrictSpaceShipPath(screen);
		
		restrictEnemiesPath();
		
		spaceShip.update(timePassed);
		
		image.y += backgroundScrollSpeed;
		
		fireCannons();
		
		sendEnemies();
		
		for(int i = 0; i < enemies.size(); i++){
			Sprite enemy = enemies.get(i);
			if(hasColided(enemy)){
				explosion = new Sprite(ExplosionAnimation);
				explosion.setX(enemies.get(i).getX());
				explosion.setY(enemies.get(i).getY());
				explosionTimer = System.currentTimeMillis();
				enemies.remove(i);
				EnemiesKilled++;
				playerScore += 10;
			}
			else{
				enemy.update(timePassed);
			}
		}
				
		explosion.update(timePassed);

		for(int i = 0; i < bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(bullet.getY() + bullet.getHeight() < 0){
				bullets.remove(i);
			}
			else{
				bullet.update(timePassed);
			}
		}
		fireCannons();
	}
	
	private void restrictEnemiesPath() {
	for(int i = 0; i < enemies.size(); i++){
		Sprite enemy = enemies.get(i);
	
	if(enemy.getX() <= 0){
		enemy.setVelocityX(0 - enemy.getVelocityX());
	}
	else if(enemy.getX() + enemy.getWidth() >= screen.getWidth()){
		enemy.setVelocityX(0 - enemy.getVelocityX());
	}
	else if(enemy.getY() > screen.getHeight()){
		enemies.remove(i);
	}
	}
	}

	private void sendEnemies() {
	long currentTime = System.currentTimeMillis() - startTime;
	//message = "Time Passed : " + currentTime;
	
	if((currentTime % 100) == 0){
	Image currEnemy = enemyImg.get((int)(Math.random() * enemyImages.length));
	enemyAnimation = new Animation();
	enemyAnimation.addScene(currEnemy, 1000);
	
	float ex, ey;
	float enemySpeed = 0.5F;
	
	ex = (float) Math.random() * screen.getWidth();
	ey = 0 - currEnemy.getHeight(null);
	int dir = (int) (Math.random() * 100);
	if((dir%2) == 0){
	enemySpeed = -0.5F;
	}
	else
	{
		enemySpeed = 0.5F; 
	}
	
		for(int i = 0; i < 5; i++){
			EnemyShip enemy = new EnemyShip(enemyAnimation);
			enemy.setVelocityX(enemySpeed);
			enemy.setVelocityY(Math.abs(enemySpeed));
			enemy.setX(ex - (i * enemy.getWidth()));
			enemy.setY(ey - (i * enemy.getHeight()));
			ex = ex - enemy.getWidth();
			ey = ey - enemy.getHeight();
			enemies.add(enemy);
		}
		EnemyCount += 5;
	}
	}

	private boolean hasColided(Sprite enemy) {

		Rectangle enemyBounds;
		enemyBounds = new Rectangle();
		enemyBounds.x = (int) enemy.getX();
		enemyBounds.y = (int) enemy.getY();
		enemyBounds.height = enemy.getHeight();
		enemyBounds.width = enemy.getWidth();
		
		Rectangle bulletBounds = new Rectangle();

		if(enemyBounds.getY() >= 0)
		for(int i = 0; i < bullets.size(); i++){
			
			Sprite bullet = bullets.get(i);
			
			bulletBounds.x = (int) bullet.getX();
			bulletBounds.y = (int) bullet.getY();
			bulletBounds.height = bullet.getHeight();
			bulletBounds.width = bullet.getWidth();
			
			if(enemyBounds.intersects(bulletBounds)){
				bullets.remove(i);
				return true;
			}
		}
		
		Rectangle shipBounds = new Rectangle();
		shipBounds.x = (int) spaceShip.getX();
		shipBounds.y = (int) spaceShip.getY();
		shipBounds.height = spaceShip.getHeight();
		shipBounds.width = spaceShip.getWidth();
		
		if(enemyBounds.intersects(shipBounds)){
			health--;
			return true;
		}
		return false;
	}

	@Override
	public void draw(Graphics2D g) {
		
		int h = deepSpaceBg.getHeight(null);
		
		image.y %= h;
		
		if(image.y < 0){
			image.y += h;
		}
		
		int y = image.y;
		
		g.drawImage(deepSpaceBg, 0, y, null);
		g.drawImage(deepSpaceBg, 0, y-h, null);
		
		
		if(health <= 0){
			g.setFont(new Font("Arial", Font.BOLD, 50));
			g.drawString("GAME OVER", (screen.getWidth()/2) - 160, 60);
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.drawString("Press ESC Key to Exit. ", (screen.getWidth()/2) - 110, 90);
		}
		else{
			for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).draw(g);
			
		}
		
		for(int i = 0; i < enemies.size(); i++){
			Sprite enemy = enemies.get(i);
			g.drawImage(enemy.getImage(),(int) enemy.getX(),(int) enemy.getY(), null);
		}
		
		
		g.drawString("Press esc to exit.", 30, 60);
//		g.drawString(message, 30, 100);
//		g.drawString("Bullets Fired : " + bulletCount, 30, 90);
//		g.drawString("Enemies Encountered : " + EnemyCount, 30, 120);
//		g.drawString("Enemies Killed : " + EnemiesKilled, 30, 150);
		}
		
		g.drawImage(spaceShip.getImage(),(int) spaceShip.getX(),(int) spaceShip.getY(), null);
		
		if((System.currentTimeMillis() - explosionTimer)< 300){
			
			g.drawImage(explosion.getImage(),(int) explosion.getX(),(int) explosion.getY(), null);
			
		}
		
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString("Ship Health : " + health, 30, 30);
		
		g.drawString("Score : " + playerScore, screen.getWidth() - 220, 30);
		g.setFont(new Font("Arial", Font.PLAIN, 20));
	}
	
	private synchronized void fireCannons(){
		long currentTime = System.currentTimeMillis() - startTime;
		//message = "Time Passed : " + currentTime;
		
		if((currentTime % 3) == 0){
		leftBulletSprite = new Bullet(bulletAnimation);
		leftBulletSprite.setX(spaceShip.getX() + 5);
		leftBulletSprite.setY(spaceShip.getY() + 30);
		leftBulletSprite.setVelocityX(0.00F);
		leftBulletSprite.setVelocityY(-bulletSpeed);
		
		rightBulletSprite = new Bullet(bulletAnimation);
		rightBulletSprite.setX(spaceShip.getX() + spaceShip.getWidth() - 45);
		rightBulletSprite.setY(spaceShip.getY() + 30);
		rightBulletSprite.setVelocityX(0.00F);
		rightBulletSprite.setVelocityY(-bulletSpeed);
		
		bullets.add(leftBulletSprite);
		bullets.add(rightBulletSprite);
		bulletCount += 2;
		}
		
		
		
	}

	

	
	
		@Override
	public void keyTyped(KeyEvent e) {
			fireCannons();
			e.consume();
		}

		@Override
	public void keyPressed(KeyEvent e) {
			fireCannons();
			int KeyCode = e.getKeyCode();
			message = "key pressed";
			if(KeyCode == KeyEvent.VK_ESCAPE){
				stop();
			}
			else{
				message = "Pressed " + KeyEvent.getKeyText(KeyCode);
				e.consume();
			}
			
			if(KeyCode == KeyEvent.VK_UP || KeyCode == KeyEvent.VK_W){
				spaceShip.setVelocityY(-spaceShip.getSpaceShipSpeed());
			}
			if(KeyCode == KeyEvent.VK_DOWN || KeyCode == KeyEvent.VK_S){
				spaceShip.setVelocityY(spaceShip.getSpaceShipSpeed());
			}
			if(KeyCode == KeyEvent.VK_LEFT || KeyCode == KeyEvent.VK_A){
				spaceShip.setVelocityX(-spaceShip.getSpaceShipSpeed());
			}
			if(KeyCode == KeyEvent.VK_RIGHT || KeyCode == KeyEvent.VK_D){
				spaceShip.setVelocityX(spaceShip.getSpaceShipSpeed());
			}
			if(KeyCode == KeyEvent.VK_SPACE){
				fireCannons();
			}
			e.consume();
		}

		@Override
	public void keyReleased(KeyEvent e) {
			
			fireCannons();
			int keyCode = e.getKeyCode();
			message = "Released " + KeyEvent.getKeyText(keyCode);
			
			if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W){
				spaceShip.setVelocityY(0.00F);
			}
			if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S){
				spaceShip.setVelocityY(0.00F);
			}
			if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A){
				spaceShip.setVelocityX(0.00F);
			}
			if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D){
				spaceShip.setVelocityX(0.00F);
			}
			if(keyCode == KeyEvent.VK_SPACE){
				fireCannons();
			}
			e.consume();
		}

	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
