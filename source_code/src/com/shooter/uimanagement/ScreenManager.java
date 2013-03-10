package com.shooter.uimanagement;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;

public class ScreenManager {
	
	JFrame myFrame;
	
	JButton winkBtn;
	JButton smileBtn;
	JButton exitBtn;
	
	JPanel Menu;
	
	private GraphicsDevice vc;
	
	public ScreenManager(){
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		vc = env.getDefaultScreenDevice();
	}
	
	public DisplayMode[] getCompatibleDisplayModes(){
		
		return vc.getDisplayModes();
	}
	
	public DisplayMode findFirstCompatibleMode(DisplayMode modes[]){
		DisplayMode goodModes[] = vc.getDisplayModes();
		for(int x = 0; x < modes.length; x++){
			for(int y = 0; y < goodModes.length; y++){
				if(displayModesMatch(modes[x], goodModes[y])){
					return modes[x];
				}
			}
		}
		return null;
	}

	private boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2) {
		
		if(mode1.getWidth() != mode2.getWidth() || mode1.getHeight() != mode2.getHeight()){
			return false;
		}
		if(mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& mode1.getBitDepth() != mode2.getBitDepth()){
			return false;
		}
		if(mode1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN && mode2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& mode1.getRefreshRate() != mode2.getRefreshRate()){
			return false;
		}
		return true;
	}

	public DisplayMode getCurrentDisplayMode(){
		return vc.getDisplayMode();
	}
	
	public void setFullScreen(DisplayMode dm){
		myFrame = new JFrame();
		myFrame.setUndecorated(true);
		myFrame.setIgnoreRepaint(true);
		myFrame.setResizable(false);
		
		Menu = new JPanel();
		winkBtn = new JButton("Wink");
		smileBtn = new JButton("Smile");
		exitBtn = new JButton("Exit");
		
		Menu.add(winkBtn);
		Menu.add(smileBtn);
		Menu.add(exitBtn);
		Menu.setOpaque(false);
		
		myFrame.add(Menu);
		
		RepaintManager repaintManager = new NoRepaintManager();
		repaintManager.setDoubleBufferingEnabled(false);
		RepaintManager.setCurrentManager(repaintManager);
		
		vc.setFullScreenWindow(myFrame);
		
		if(dm != null && vc.isDisplayChangeSupported()){
			try{
				vc.setDisplayMode(dm);
			}
			catch(Exception ex){
				
			}
		}
		myFrame.createBufferStrategy(2);
	}
	
	public Graphics2D getGraphics(){
		Window w = vc.getFullScreenWindow();
		if(w != null){
			BufferStrategy s = w.getBufferStrategy();
			return (Graphics2D) s.getDrawGraphics();
		}
		else{
			return null;
		}
	}
	
	public void update(){
		Window w = vc.getFullScreenWindow();
		
		if(w != null){
			BufferStrategy s = w.getBufferStrategy();
			if(!s.contentsLost()){
				s.show();
			}
		}
	}
	
	public Window getFullScreenWindow(){
		return vc.getFullScreenWindow();
	}
	
	public int getWidth(){
		Window w = vc.getFullScreenWindow();
		if(w != null){
			return w.getWidth();
		}
		else{
			return 0;
		}
	}
	
	public int getHeight(){
		Window w = vc.getFullScreenWindow();
		if(w != null){
			return w.getHeight();
		}
		else{
			return 0;
		}
	}
	
	public void restoreScreen(){
		Window w = vc.getFullScreenWindow();
		
		if(w != null)
		{
			w.dispose();
		}
	}
	
	public BufferedImage createCompatibleImage(int w, int h, int t){
		Window win = vc.getFullScreenWindow();
		
		if(win != null){
			GraphicsConfiguration gc = win.getGraphicsConfiguration();
			return gc.createCompatibleImage(w, h, t);
		}
		return null;
	}

	class NoRepaintManager extends RepaintManager
	{
	    public void addDirtyRegion(JComponent c, int x, int y, int w, int h){}

	    public void addInvalidComponent(JComponent invalidComponent){}
	
	    public void markCompletelyDirty(JComponent aComponent){}
	    
	    public void paintDirtyRegions(){}
	}
}
