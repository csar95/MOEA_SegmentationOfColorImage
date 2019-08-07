package models;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Pixel {
	
	private int coordinateX;
	private int coordinateY;
	private Color color;
	private Pixel upPixel;
	private Pixel downPixel;
	private Pixel lefPixel;
	private Pixel rightPixel;
	
	public Pixel (int x, int y, Color color) {
		
		this.coordinateX = x;
		this.coordinateY = y;
		this.color = color;
		
	}
	
	public Pixel (int index, BufferedImage img, int rgbColor) {
		
		this.coordinateX = index % img.getWidth();
		this.coordinateY = index % img.getHeight();
		this.color = new Color(rgbColor); 
		
	}

}
