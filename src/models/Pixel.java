package models;

import java.awt.Color;

public class Pixel {
	
	Color color;
//	int r, g, b;
	private double upperEdge, lowerEdge, leftEdge, rightEdge;
	
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
	
	public int get_minimum_edge () {
		
		double minimumValue = 9999999.9;
		int edge = 0;
		
		if (this.upperEdge != 0 && this.upperEdge < minimumValue) {
			minimumValue = this.upperEdge;
			edge = 1;
		}
		
		if (this.rightEdge != 0 && this.rightEdge < minimumValue) {
			minimumValue = this.rightEdge;
			edge = 2;
		}
		
		if (this.lowerEdge != 0 && this.lowerEdge < minimumValue) {
			minimumValue = this.lowerEdge;
			edge = 3;
		}
		
		if (this.leftEdge != 0 && this.leftEdge < minimumValue) {
			minimumValue = this.leftEdge;
			edge = 4;
		}
		
		return edge;
		
	}
	
	public void setUpperEdge(double value) {
		this.upperEdge = value;
	}
	public void setRightEdge(double value) {
		this.rightEdge = value;
	}
	public void setLowerEdge(double value) {
		this.lowerEdge = value;
	}
	public void setLeftEdge(double value) {
		this.leftEdge = value;
	}
	
	public double getUpperEdge() {
		return this.upperEdge;
	}
	public double getRightEdge() {
		return this.rightEdge;
	}
	public double getLowerEdge() {
		return this.lowerEdge;
	}
	public double getLeftEdge() {
		return this.leftEdge;
	}

}
