package models;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Set;

public class ImageGraph {
	
	private int height;
	private int width;
	private Pixel[][] adjacencyMatrix;
	private BufferedImage image;
	
	public ImageGraph (BufferedImage img) {
		
		this.image = img;
		this.height = img.getHeight();
		this.width = img.getWidth();
		this.adjacencyMatrix = new Pixel[this.height][this.width];
		
	}
	
	public void fillGraph () {
		
		// Initialize vertexes
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				adjacencyMatrix[i][j] = this.getPixelFromImg(i, j);
			}
		}	
		
	}
	
	private Pixel getPixelFromImg(int i, int j) {
		
		Pixel pixel = new Pixel(this.image.getRGB(j, i));
		
		if (i == 0) { // First row			
			pixel.lowerEdge = pixel.getEuclideanDistance(this.image.getRGB(j, i+1));
			this.setLeftRightEdges(pixel, i, j);			
		}
		else if (i == this.height - 1) { // Last row			
			pixel.upperEdge = this.adjacencyMatrix[i-1][j].lowerEdge;
			this.setLeftRightEdges(pixel, i, j);			
		}
		else { // Middle rows			
			pixel.upperEdge = this.adjacencyMatrix[i-1][j].lowerEdge;
			pixel.lowerEdge = pixel.getEuclideanDistance(this.image.getRGB(j, i+1));			
			this.setLeftRightEdges(pixel, i, j);			
		}
		
		return pixel;
		
	}
	
	private void setLeftRightEdges (Pixel pixel, int i, int j) {
		
		if (j == 0) { // First column
			pixel.rightEdge = pixel.getEuclideanDistance(this.image.getRGB(j+1, i));
		}
		else if (j == this.width - 1) { // Last column
			pixel.leftEdge = this.adjacencyMatrix[i][j-1].rightEdge;
		}
		else { // Middle columns
			pixel.rightEdge = pixel.getEuclideanDistance(this.image.getRGB(j+1, i));
			pixel.leftEdge = this.adjacencyMatrix[i][j-1].rightEdge;
		}
		
	}

}


