package com.shooter.elements;

import java.awt.Graphics2D;

import com.shooter.uimanagement.Animation;
import com.shooter.uimanagement.Sprite;

public class Bullet extends Sprite {

	public Bullet(Animation a) {
		super(a);
	}

	public void draw(Graphics2D g){
		g.drawImage(this.getImage(),(int) this.getX(),(int) this.getY(), null);
	}
}
