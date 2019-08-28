package models;

public class Solution {
	
	private int[] solution;
	private double overallDeviation;
	private double edgeValue;
	
	private int strength; // Number of solutions that a given solution dominates.
	private int rawFitness; // Sum of the strength values of the solutions that dominate a given solution.
	private double density;
	private double fitness;
	
	public Solution(int[] solution, double overallDev, double edgeVal) {
		this.solution = solution;
		this.overallDeviation = overallDev;
		this.edgeValue = edgeVal;
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

	public int[] getSolution() {
		return this.solution;
	}
	public double getOverallDeviation() {
		return this.overallDeviation;
	}
	public double getEdgeValue() {
		return this.edgeValue;
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
	
	public void setOverallDeviation(double overallDeviation) {
		this.overallDeviation = overallDeviation;
	}
	public void setEdgeValue(double edgeValue) {
		this.edgeValue = edgeValue;
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
