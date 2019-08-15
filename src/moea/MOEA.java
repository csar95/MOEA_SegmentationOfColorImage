package moea;

import java.util.ArrayList;
import java.util.List;

public class MOEA {
	
	private int[] mst;
	private int height;
	private int width;
	private final int POPULATION_SIZE = 50;
	
	public MOEA (int[] mst, int height, int width) {
		this.height = height;
		this.width = width;
		this.mst = mst;
	}
	
	public void run_algorithm () {
		
		List<int[]> population = new ArrayList<int[]>();
		long start = System.currentTimeMillis(), finish;
		// Generate an initial population
		for (int i = 1; i <= this.POPULATION_SIZE; i++) {
			int[] initialSolution = this.get_initial_solution(i);
			List<ArrayList<Integer>> clusters = get_clusters(initialSolution);
			population.add(initialSolution);
			
			finish = System.currentTimeMillis();
			System.out.println(i + "th solution generated in " + (finish - start) / 1000. + " seconds.");
			start = System.currentTimeMillis();
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
				clusters.add(newCluster);
			}
		}
		
		// Fill each cluster with elements
		for (ArrayList<Integer> cluster : clusters) {
			cluster = find_cluster_elems(cluster, solution);
		}
		
		return clusters;		
	}
	
	// TODO: Tarda entre 20 y 30 cuando el cluster es grande
	private ArrayList<Integer> find_cluster_elems (ArrayList<Integer> cluster, int[] solution) {
		
		int destination = cluster.get(cluster.size() - 1);
		
		int[] options = { (destination >= this.width) ? destination - this.width : -1, // Up
						  ((destination + 1) % this.width != 0) ? destination + 1 : -1, // Right
						  (destination + this.width < (this.height * this.width)) ? destination + this.width : -1, // Down
						  (destination % this.width != 0) ? destination - 1 : -1 // Left
						};
		
		// Find elements in solution that are directed to the destination
		for (int i : options) {		
			if (i != -1 && solution[i] == destination && !cluster.contains(i)) {
				cluster.add(i);
				cluster = find_cluster_elems(cluster, solution);
			}
		}
		
		return cluster;
	}

}
