package models;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
	
	public void fill_graph () {
		
		// Initialize vertexes
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				adjacencyMatrix[i][j] = this.get_pixel_from_img(i, j);
			}
		}	
		
	}
	
	private Pixel get_pixel_from_img(int i, int j) {
		
		Pixel pixel = new Pixel(this.image.getRGB(j, i));
		
		if (i == 0) { // First row			
			pixel.setLowerEdge( pixel.getEuclideanDistance(this.image.getRGB(j, i+1)) );
			this.set_left_right_edges(pixel, i, j);			
		}
		else if (i == this.height - 1) { // Last row			
			pixel.setUpperEdge( this.adjacencyMatrix[i-1][j].getLowerEdge() );
			this.set_left_right_edges(pixel, i, j);			
		}
		else { // Middle rows			
			pixel.setUpperEdge( this.adjacencyMatrix[i-1][j].getLowerEdge() );
			pixel.setLowerEdge( pixel.getEuclideanDistance(this.image.getRGB(j, i+1)) );			
			this.set_left_right_edges(pixel, i, j);			
		}
		
		return pixel;
		
	}
	
	private void set_left_right_edges (Pixel pixel, int i, int j) {
		
		if (j == 0) { // First column
			pixel.setRightEdge( pixel.getEuclideanDistance(this.image.getRGB(j+1, i)) );
		}
		else if (j == this.width - 1) { // Last column
			pixel.setLeftEdge( this.adjacencyMatrix[i][j-1].getRightEdge() );
		}
		else { // Middle columns
			pixel.setRightEdge( pixel.getEuclideanDistance(this.image.getRGB(j+1, i)) );
			pixel.setLeftEdge( this.adjacencyMatrix[i][j-1].getRightEdge() );
		}
		
	}
	
	public int[] prims_algorithm () {
		
		int[] minimumShortestTree = new int [height * width];
		Arrays.fill(minimumShortestTree, -1);
		
		ArrayList<Coordinates> pixelsInTree = new ArrayList<Coordinates>();
		
		// Random pixel to begin with
		int i = (int) (Math.random() * this.height);
		int j = (int) (Math.random() * this.width);		
		
		int pos = i * this.width + j;
		minimumShortestTree[pos] = -2;
		pixelsInTree.add( new Coordinates(j, i));
		
		int newPixelPos;
		
		// Store the pixel to which the new one is directed
		switch (adjacencyMatrix[i][j].get_minimum_edge()) {
		case 1: // Upper
			newPixelPos = (i-1) * this.width + j;
			minimumShortestTree[newPixelPos] = pos;
			pixelsInTree.add( new Coordinates( j, i-1 ));
			break;
		case 2:	// Right
			newPixelPos = i * this.width + (j+1);
			minimumShortestTree[newPixelPos] = pos;
			pixelsInTree.add( new Coordinates( j+1, i ));
			break;
		case 3:	// Lower
			newPixelPos = (i+1) * this.width + j;
			minimumShortestTree[newPixelPos] = pos;
			pixelsInTree.add( new Coordinates( j, i+1 ));
			break;
		default: // Left
			newPixelPos = i * this.width + (j-1);
			minimumShortestTree[newPixelPos] = pos;
			pixelsInTree.add( new Coordinates( j-1, i ));
			break;
		}
		
		// TODO: Debug
		while (pixelsInTree.size() < this.height * this.width) {
			int[] nextPixelData = this.find_next_pixel(pixelsInTree);
			minimumShortestTree[nextPixelData[0]] = nextPixelData[1];
			pixelsInTree.add( new Coordinates(nextPixelData[0]%this.width, nextPixelData[0]/this.width) );
		}
		
		minimumShortestTree[pos] = pos;		
		return minimumShortestTree;
		
	}
	
	private int[] find_next_pixel (ArrayList<Coordinates> pixels) {
		
		Coordinates coordinates = null;
		int i, j, newPixelPos = -1, destPos = -1;
		double minEdge = 9999999.9, edgeValue;
		
		Iterator<Coordinates> iterator = pixels.iterator();		
		while (iterator.hasNext()) {
			coordinates = iterator.next();
			i = coordinates.getCoordI();
			j = coordinates.getCoordJ();
			
			// Check if there's any neighbor not included in tree yet
			if ((i-1) >= 0 && pixels.indexOf( new Coordinates(j, i-1) ) == -1) { // Upper pixel	doesn't exist on tree
				edgeValue = this.adjacencyMatrix[i][j].getUpperEdge();
				if (edgeValue < minEdge) {
					minEdge = edgeValue;
					newPixelPos = (i-1) * this.width + j;
					destPos = i * this.width + j;
				}				
			}
			if ((j+1) < this.width && pixels.indexOf( new Coordinates(j+1, i) ) == -1) { // Right pixel doesn't exist on tree
				edgeValue = this.adjacencyMatrix[i][j].getRightEdge();
				if (edgeValue < minEdge) {
					minEdge = edgeValue;
					newPixelPos = i * this.width + (j+1);
					destPos = i * this.width + j;
				}				
			}
			if ((i+1) < this.height && pixels.indexOf( new Coordinates(j, i+1) ) == -1) { // Lower pixel doesn't exist on tree
				edgeValue = this.adjacencyMatrix[i][j].getLowerEdge();
				if (edgeValue < minEdge) {
					minEdge = edgeValue;
					newPixelPos = (i+1) * this.width + j;
					destPos = i * this.width + j;
				}				
			}
			if ((j-1) >= 0 && pixels.indexOf( new Coordinates(j-1, i) ) == -1) { // Left pixel doesn't exist on tree
				edgeValue = this.adjacencyMatrix[i][j].getLeftEdge();
				if (edgeValue < minEdge) {
					minEdge = edgeValue;
					newPixelPos = i * this.width + (j-1);
					destPos = i * this.width + j;
				}				
			}
		}
		
		return new int[] {newPixelPos, destPos};
		
	}	

}


