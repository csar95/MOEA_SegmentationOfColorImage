package moea;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import models.Coordinates;

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

}
