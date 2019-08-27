package moea;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import models.Pixel;
import models.Solution;

public class MOEA {
	
	private int[] mst;
	private BufferedImage image;
	private int height;
	private int width;
	private final int POPULATION_SIZE = 30;
	private int keySol;
	private HashMap<Integer, Solution> population;	
	private final int ARCHIVE_SIZE = 8;
	private HashMap<Integer, Solution> archive;
	
	public MOEA (int[] mst, BufferedImage img) {
		this.keySol = 1;
		this.image = img;
		this.height = img.getHeight();
		this.width = img.getWidth();
		this.mst = mst;
		this.population = new HashMap<Integer, Solution>();
		this.archive = new HashMap<Integer, Solution>();
	}
	
	public void run_algorithm () {
		
		HashMap<Integer, Solution> union = new HashMap<Integer, Solution>();
		
		long start = System.currentTimeMillis(), finish;
		
		// 1. Initialization
		while (this.keySol <= this.POPULATION_SIZE) {
			int[] initialSolution = this.get_initial_solution(this.keySol);
			List<ArrayList<Integer>> clusters = get_clusters(initialSolution);
			// Calculate objectives
			this.population.put(this.keySol, new Solution(initialSolution, this.calculate_overall_deviation(clusters), this.calculate_edge_value(clusters, initialSolution)));
			this.keySol++;
		}
		
		// 50 pop - 3 mins | 30 pop - <1,5 min
		finish = System.currentTimeMillis();
		System.out.println("Initial solutions generated in " + (finish - start) / 1000. + " seconds.");
		System.out.println("------------------------------------");
		start = System.currentTimeMillis();
		
		union.clear();
		union.putAll(this.population);
		union.putAll(this.archive);
		
		// 2. Fitness assignment
		calculate_strength_and_density(union);
		calculate_fitness(union);
		
		// 3. Environmental selection
		get_non_dominated(union);		
		if (this.archive.size() < this.ARCHIVE_SIZE) {
			populate_archive_with_remaining_best(union);
		}
		else if (this.archive.size() > this.ARCHIVE_SIZE) {
			remove_most_similar_from_archive();
		}
	}
	
	private int[] get_initial_solution (int ithIndividual) {
		
		int[] solution = mst.clone(); // Deepcopy
		int numOfClusters = 0;
		
		// In the initialization of the ith individual in the population, the (i − 1) long links are removed from the MST individual
		while (numOfClusters < (ithIndividual - 1)) {
			int randomCut = (int) (Math.random() * solution.length);
			if (solution[randomCut] != randomCut) {
				solution[randomCut] = randomCut;
				numOfClusters++;
			}
		}
		
		return solution;
	}
	
	private List<ArrayList<Integer>> get_clusters (int[] solution) {
		
		List<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
		
		// Create a new cluster for every root found in solution
		for (int i = 0; i < solution.length; i++) {
			if (i == solution[i]) {
				ArrayList<Integer> newCluster = new ArrayList<Integer>();
				newCluster.add(i);
				// Fill cluster with elements based on solution
				find_cluster_elems(newCluster, solution);
				clusters.add(newCluster);
			}
		}
		
		return clusters;		
	}
	
	private void find_cluster_elems (ArrayList<Integer> cluster, int[] solution) {
	
		Queue<Integer> pending = new LinkedList<Integer>();
		int destination;
		pending.add(cluster.get(0));		
			
		while (!pending.isEmpty()) {		
			destination = pending.remove();
			
			if (destination >= this.width && solution[destination - this.width] == destination) {
				cluster.add(destination - this.width);
				pending.add(destination - this.width);
			}
			if ((destination + 1) % this.width != 0 && solution[destination + 1] == destination) {
				cluster.add(destination + 1);
				pending.add(destination + 1);
			}
			if (destination + this.width < (this.height * this.width) && solution[destination + this.width] == destination) {
				cluster.add(destination + this.width);
				pending.add(destination + this.width);
			}
			if (destination % this.width != 0 && solution[destination - 1] == destination) {
				cluster.add(destination - 1);
				pending.add(destination - 1);
			}				
		}	
	}

	private double calculate_overall_deviation (List<ArrayList<Integer>> clusters) {
		
		double overallDeviation = .0;
		int[] centroid;
		Pixel centroidPixel;
		
		for (ArrayList<Integer> cluster : clusters) {
			
			centroid = find_centroid(cluster); // 0: i,  1: j
			centroidPixel = new Pixel(this.image.getRGB(centroid[1], centroid[0]));
			
			for (int pixelPos : cluster) {
				overallDeviation += centroidPixel.get_euclidean_distance(
						this.image.getRGB( pixelPos%this.width, pixelPos/this.width ));
			}
		}
		
		return overallDeviation;		
	}
	
	private int[] find_centroid (ArrayList<Integer> cluster) {
		int totalNumElems = cluster.size(), iTotal = 0, jTotal = 0;
		for (int pixelPos : cluster) {
			iTotal += pixelPos / this.width;
			jTotal += pixelPos % this.width;
		}
		return new int[] {Math.round(iTotal/totalNumElems), Math.round(jTotal/totalNumElems)};
	}

	private double calculate_edge_value (List<ArrayList<Integer>> clusters, int[] solution) {
		
		double edgeValue = .0;
		int pixelPos;
		Pixel pixel;
		
		// For every pixel in the image
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				
				pixel = new Pixel(this.image.getRGB(j, i));
				pixelPos = i * this.width + j;
				
				// Add the euclidean distance to the edge value if neighbor pixel belongs to the same cluster
				if (i > 0 && this.from_same_cluster(pixelPos, pixelPos - this.width, solution)) { // Upper pixel
					edgeValue += pixel.get_euclidean_distance(
							this.image.getRGB(j, i-1));
				}
				if (j < (this.width - 1) && this.from_same_cluster(pixelPos, pixelPos + 1, solution)) { // Right pixel
					edgeValue += pixel.get_euclidean_distance(
							this.image.getRGB(j+1, i));
				}
				if (i < (this.height - 1) && this.from_same_cluster(pixelPos, pixelPos + this.width, solution)) { // Lower pixel
					edgeValue += pixel.get_euclidean_distance(
							this.image.getRGB(j, i+1));
				}
				if (j > 0 && this.from_same_cluster(pixelPos, pixelPos - 1, solution)) { // Left pixel
					edgeValue += pixel.get_euclidean_distance(
							this.image.getRGB(j-1, i));
				}
			}
		}
		
		return -edgeValue;
	}
	
	private boolean from_same_cluster (int pixelPos1, int pixelPos2, int[] solution) {
		
		int rootPos, aux2;
		
		if (solution[pixelPos2] == pixelPos1) return true;
		
		if (solution[pixelPos2] == pixelPos2) return false;
		
		// Identify root of cluster
		rootPos = pixelPos1;
		while (solution[rootPos] != rootPos) {
			rootPos = solution[rootPos];
		}
		
		aux2 = pixelPos2;
		while (solution[aux2] != aux2 && solution[aux2] != rootPos) {
			aux2 = solution[aux2];
		}
		
		if (solution[aux2] == rootPos) return true;
		
		return false;		
	}
	
	/*
	 * Calculates the strength of a solution based on the number of solutions that a given solution dominates.
	 * Estimates the density of an area of the Pareto front. Density is calculated between the solution and its kth nearest neighbor in the population.
	 */	
	private void calculate_strength_and_density (HashMap<Integer, Solution> union) {
		
		ArrayList<Double> distancesToOtherSolutions = new ArrayList<Double>();
		int strength, solKey, kthNearestNeighbor;
		Solution solution, auxSolution;
		double density;
		
		for (Map.Entry<Integer, Solution> sol : union.entrySet()) {
			
			strength = 0;
			distancesToOtherSolutions.clear();
			solKey = sol.getKey();
			solution = sol.getValue();
			
			for (Map.Entry<Integer, Solution> auxSol : union.entrySet()) {				
				if (solKey == auxSol.getKey()) continue;
				auxSolution = auxSol.getValue();
				if (solution.dominates(auxSolution)) strength++;
				distancesToOtherSolutions.add(solution.calculate_euclidean_distance_between_objectives(auxSolution));				
			}
			
			Collections.sort(distancesToOtherSolutions);
			
			kthNearestNeighbor = (int) Math.sqrt(union.size());
			density = 1.0 / (distancesToOtherSolutions.get(kthNearestNeighbor) + 2.0);
			
			solution.setDensity(density);
			solution.setStrength(strength);			
			union.put(solKey, solution);			
		}
	}
	
	// Calculates the raw fitness as the sum of the strength values of the solutions that dominate a given candidate.
	private void calculate_fitness (HashMap<Integer, Solution> union) {
		
		int rawFitness, solKey;
		Solution solution, auxSolution;
		
		for(Map.Entry<Integer, Solution> sol : union.entrySet()) {
			
			rawFitness = 0;
			solKey = sol.getKey();
			solution = sol.getValue();
			
			for(Map.Entry<Integer, Solution> auxSol : union.entrySet()) {
				
				if (solKey == auxSol.getKey()) continue;
				auxSolution = auxSol.getValue();
				if (auxSolution.dominates(solution)) rawFitness += auxSolution.getStrength();
			}
			
			solution.setRawFitness(rawFitness);
			solution.setFitness(rawFitness + solution.getDensity());
			union.put(sol.getKey(), solution);
			
			if (this.population.containsKey(solKey)) this.population.put(solKey, solution);
			if (this.archive.containsKey(solKey)) this.archive.put(solKey, solution);			
		}		
	}

	private boolean is_non_dominated_solution (int memberKey, HashMap<Integer, Solution> map) {
		
		Solution solution = map.get(memberKey), auxSolution;
		
		for(Map.Entry<Integer, Solution> sol : map.entrySet()) {
			if (memberKey == sol.getKey()) continue;
			auxSolution = sol.getValue();
			if (auxSolution.dominates(solution)) return false;
		}
		
		return true;
	}
	
	private void get_non_dominated (HashMap<Integer, Solution> union) {
		
		this.archive.clear();
		ArrayList<Integer> membersPendingToBeChecked = new ArrayList<Integer>(union.keySet());
		
		while (!membersPendingToBeChecked.isEmpty()) {
			
			int randomMemberKey = membersPendingToBeChecked.remove( (int) (Math.random() * membersPendingToBeChecked.size()) );
			if (this.is_non_dominated_solution(randomMemberKey, union))
				this.archive.put(randomMemberKey, union.get(randomMemberKey));
			
		}		
	}
	
	private void populate_archive_with_remaining_best (HashMap<Integer, Solution> union) {
		
		double minFitness, solutionFitness;
		int newArchiveMember = 0;
		Solution solution;
		ArrayList<Integer> membersInArchive = new ArrayList<Integer>(this.archive.keySet());
		
		for (int key : membersInArchive) {
			union.remove(key);
		}		
		
		while (this.archive.size() < this.ARCHIVE_SIZE) {
			
			minFitness = 999999999;
			
			for(Map.Entry<Integer, Solution> sol : union.entrySet()) {
				
				solution = sol.getValue();
				solutionFitness = solution.getFitness();
				if (solutionFitness < minFitness) {
					minFitness = solutionFitness;
					newArchiveMember = sol.getKey();
				}
			}
			
			this.archive.put(newArchiveMember, union.remove(newArchiveMember));
		}
		
	}
	
	private void remove_most_similar_from_archive () {
		
		int solKey, kthNearestNeighbor, archiveKeyToDelete;
		Solution solution, auxSolution;
		ArrayList<Double[]> arrayForComparison = new ArrayList<Double[]>();
		ArrayList<Double> distancesToOtherMembersOfTheArchive = new ArrayList<Double>();		
		
		while (this.archive.size() > this.ARCHIVE_SIZE) {
			
			arrayForComparison.clear();
			
			for (Map.Entry<Integer, Solution> sol : this.archive.entrySet()) {	
				
				distancesToOtherMembersOfTheArchive.clear();
				solKey = sol.getKey();
				solution = sol.getValue();
				
				for (Map.Entry<Integer, Solution> auxSol : this.archive.entrySet()) {				
					if (solKey == auxSol.getKey()) continue;
					auxSolution = auxSol.getValue();
					distancesToOtherMembersOfTheArchive.add(solution.calculate_euclidean_distance_between_objectives(auxSolution));				
				}
				
				Collections.sort(distancesToOtherMembersOfTheArchive);			
				kthNearestNeighbor = (int) Math.sqrt(this.archive.size());			
				arrayForComparison.add(new Double[] {(double) solKey, distancesToOtherMembersOfTheArchive.get(kthNearestNeighbor)});
				
			}
			
			// Order array by the euclidean distance of the kth nearest neighbor
			Collections.sort(arrayForComparison,new Comparator<Double[]>() {
	            public int compare(Double[] doubles, Double[] otherdDoubles) {
	                return doubles[1].compareTo(otherdDoubles[1]);
	            }
	        });			
			
			archiveKeyToDelete = (int) ((double) arrayForComparison.get(0)[0]);
			this.archive.remove(archiveKeyToDelete);
		}
		
	}
}
