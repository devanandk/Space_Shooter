package com.shooter.elements;

import com.shooter.uimanagement.*;

public class SpaceShip extends Sprite {

	private float spaceShipSpeed = 0.8F;
	
	public SpaceShip(Animation a) {
		super(a);
	}

	/**
	 * @return the spaceShipSpeed
	 */
	public float getSpaceShipSpeed() {
		return spaceShipSpeed;
	}

	/**
	 * @param spaceShipSpeed the spaceShipSpeed to set
	 */
	public void setSpaceShipSpeed(float spaceShipSpeed) {
		this.spaceShipSpeed = spaceShipSpeed;
	}

	public void restrictSpaceShipPath(ScreenManager screen){
		
		if(this.getX() < 0){
			this.setX(0.00F);
		}
		else if(this.getX() + this.getWidth() >= screen.getWidth()){
			this.setX(screen.getWidth() - this.getWidth());
		}
		
		if(this.getY() < 0){
			this.setY(0.00F);
		}
		else if(this.getY() + this.getHeight() >= screen.getHeight()){
			this.setY(screen.getHeight() - this.getHeight());
		}
}
}
