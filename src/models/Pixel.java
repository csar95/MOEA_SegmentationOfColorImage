package models;

import java.awt.Color;

public class Pixel {
	
	Color color;
//	int r, g, b;
	double upperEdge, lowerEdge, leftEdge, rightEdge;
	
	public Pixel (int c) {
		this.color = new Color(c);
//		this.r = this.color.getRed();
//		this.g = this.color.getGreen();
//		this.b = this.color.getBlue();
	}	
	
	public double getEuclideanDistance (int color2) {
		
		Color c1 = this.color;
		Color c2 = new Color(color2, true);
		
		return Math.sqrt(
				Math.pow((c1.getRed() - c2.getRed()), 2) +
				Math.pow((c1.getGreen() - c2.getGreen()), 2) +
				Math.pow((c1.getBlue() - c2.getBlue()), 2));
		
	}

}
