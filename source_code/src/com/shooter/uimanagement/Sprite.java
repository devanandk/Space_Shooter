package com.shooter.uimanagement;

import java.awt.Image;

public class Sprite {

	private Animation guy;
	private float x;
	private float y;
	private float vx;
	private float vy;
	
	public Sprite(Animation a){
		guy = a;
	}
	
	public void update(long timePassed){
		setX(getX() + (vx * timePassed));
		setY(getY() + (vy * timePassed));
		guy.update(timePassed);
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public int getWidth(){
		return guy.getImage().getWidth(null);
	}
	
	public int getHeight(){
		return guy.getImage().getHeight(null);
	}

	public float getVelocityX(){
		return vx;
	}
	
	public float getVelocityY(){
		return vy;
	}
	
	public void setVelocityX(float vx){
		this.vx = vx;
	}
	
	public void setVelocityY(float vy){
		this.vy = vy;
	}
	
	public Image getImage(){
		return guy.getImage();
	}

}
