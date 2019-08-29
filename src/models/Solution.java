package models;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Solution {
	
	private int height;
	private int width;
	private List<ArrayList<Integer>> clusters;
	private int[] solution;
	private final int MIN_CLUSTER_SIZE = 100;
	
	private double overallDeviation;
	private double edgeValue;
	
	private int strength; // Number of solutions that a given solution dominates.
	private int rawFitness; // Sum of the strength values of the solutions that dominate a given solution.
	private double density;
	private double fitness;
	
	public Solution(int[] solution, int height, int width) {
		this.solution = solution;
		this.clusters = new ArrayList<ArrayList<Integer>>();
		this.height = height;
		this.width = width;
	}
	
	public boolean dominates (Solution sol) {
		if (this.overallDeviation < sol.getOverallDeviation() && this.edgeValue < sol.getEdgeValue()) return true;
		return false;
	}
	
	public double calculate_euclidean_distance_between_objectives (Solution sol) {
		return Math.sqrt(
				Math.pow((this.overallDeviation - sol.getOverallDeviation()), 2) +
				Math.pow((this.edgeValue - sol.getEdgeValue()), 2));		
	}
	
	// setClusters
	public void identify_clusters () {
		
		int rootPos;
		ArrayList<Integer> options = new ArrayList<Integer>(), visited = new ArrayList<Integer>();
		
		// Create a new cluster for every root found in solution
		for (int i = 0; i < this.solution.length; i++) {
			
			if (i == this.solution[i]) {
				
				ArrayList<Integer> newCluster = new ArrayList<Integer>();
				newCluster.add(i);
				
				// Fill cluster with elements based on solution
				find_cluster_elems(newCluster);
				
				// If cluster is not large enough, mix it with a larger one
				if (newCluster.size() < this.MIN_CLUSTER_SIZE) {
										
					options.clear();

					if (i >= this.width) options.add(i-this.width); // Upper pixel
					if ((i+1) % this.width != 0) options.add(i+1); // Right pixel
					if (i < this.solution.length - this.width) options.add(i+this.width); // Lower pixel
					if (i % this.width != 0) options.add(i-1); // Left pixel

					this.solution[i] = options.remove( (int) Math.random() * options.size() );										
					
					// Check if there is a loop
					visited.clear();
					rootPos = i;					
					while (this.solution[rootPos] != rootPos) {
						visited.add(rootPos);
						if (visited.contains(this.solution[rootPos])) { // There is loop --> Begin again
							this.solution[i] = options.remove( (int) Math.random() * options.size() );
							rootPos = i;
						}
						rootPos = this.solution[rootPos];
					}					

					for (ArrayList<Integer> cluster : this.clusters) {
						if (cluster.contains(this.solution[i])) {
							// Add elements from the new cluster to an existing one that is larger that the minimum
							for (int pixel : newCluster) cluster.add(pixel);
							break;
						}
					}
				}
				else {
					this.clusters.add(newCluster);
				}
			}
		}
	}
	
	private void find_cluster_elems (ArrayList<Integer> cluster) {
	
		Queue<Integer> pending = new LinkedList<Integer>();
		int destination;
		pending.add(cluster.get(0));		
			
		while (!pending.isEmpty()) {		
			destination = pending.remove();
			
			if (destination >= this.width && this.solution[destination - this.width] == destination) {
				cluster.add(destination - this.width);
				pending.add(destination - this.width);
			}
			if ((destination + 1) % this.width != 0 && this.solution[destination + 1] == destination) {
				cluster.add(destination + 1);
				pending.add(destination + 1);
			}
			if (destination + this.width < (this.height * this.width) && this.solution[destination + this.width] == destination) {
				cluster.add(destination + this.width);
				pending.add(destination + this.width);
			}
			if (destination % this.width != 0 && this.solution[destination - 1] == destination) {
				cluster.add(destination - 1);
				pending.add(destination - 1);
			}				
		}	
	}
	
	// setOverallDeviation
	public void calculate_overall_deviation (BufferedImage image) {
		
		int[] centroid;
		Pixel centroidPixel;
		
		for (ArrayList<Integer> cluster : clusters) {
			
			centroid = find_centroid(cluster); // 0: i,  1: j
			centroidPixel = new Pixel(image.getRGB(centroid[1], centroid[0]));
			
			for (int pixelPos : cluster) {
				this.overallDeviation += centroidPixel.get_euclidean_distance(
						image.getRGB( pixelPos%this.width, pixelPos/this.width ));
			}
		}
		
	}
	
	private int[] find_centroid (ArrayList<Integer> cluster) {
		int totalNumElems = cluster.size(), iTotal = 0, jTotal = 0;
		for (int pixelPos : cluster) {
			iTotal += pixelPos / this.width;
			jTotal += pixelPos % this.width;
		}
		return new int[] {Math.round(iTotal/totalNumElems), Math.round(jTotal/totalNumElems)};
	}
	
	// setEdgeValue
	public void calculate_edge_value (BufferedImage image) {
		
		double edgeValue = .0;
		int pixelPos;
		Pixel pixel;
		
		// For every pixel in the image
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				
				pixel = new Pixel(image.getRGB(j, i));
				pixelPos = i * this.width + j;
				
				// Add the euclidean distance to the edge value if neighbor pixel belongs to the same cluster
				if (i > 0 && this.from_same_cluster(pixelPos, pixelPos - this.width)) { // Upper pixel
					edgeValue += pixel.get_euclidean_distance(
							image.getRGB(j, i-1));
				}
				if (j < (this.width - 1) && this.from_same_cluster(pixelPos, pixelPos + 1)) { // Right pixel
					edgeValue += pixel.get_euclidean_distance(
							image.getRGB(j+1, i));
				}
				if (i < (this.height - 1) && this.from_same_cluster(pixelPos, pixelPos + this.width)) { // Lower pixel
					edgeValue += pixel.get_euclidean_distance(
							image.getRGB(j, i+1));
				}
				if (j > 0 && this.from_same_cluster(pixelPos, pixelPos - 1)) { // Left pixel
					edgeValue += pixel.get_euclidean_distance(
							image.getRGB(j-1, i));
				}
			}
		}
		
		this.edgeValue = -edgeValue;
		
	}
	
	public boolean from_same_cluster (int pixelPos1, int pixelPos2) {
		
		int rootPos, aux2;
		
		if (this.solution[pixelPos2] == pixelPos1) return true;
		
		if (this.solution[pixelPos2] == pixelPos2) return false;
		
		// Identify root of cluster
		rootPos = pixelPos1;
		while (this.solution[rootPos] != rootPos) {
			rootPos = this.solution[rootPos];
		}
		
		aux2 = pixelPos2;
		while (this.solution[aux2] != aux2 && this.solution[aux2] != rootPos) {
			aux2 = this.solution[aux2];
		}
		
		if (this.solution[aux2] == rootPos) return true;
		
		return false;		
	}
	
	
	public int[] getSolution() {
		return this.solution;
	}
	public double getOverallDeviation() {
		return this.overallDeviation;
	}
	public double getEdgeValue() {
		return this.edgeValue;
	}
	public List<ArrayList<Integer>> getClusters() {
		return this.clusters;
	}
	public int getStrength() {
		return this.strength;
	}
	public int getRawFitness() {
		return rawFitness;
	}
	public double getDensity() {
		return density;
	}
	public double getFitness() {
		return fitness;
	}
	
	public void setSolution(int[] solution) {
		this.solution = solution;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public void setRawFitness(int rawFitness) {
		this.rawFitness = rawFitness;
	}
	public void setDensity(double density) {
		this.density = density;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
}
