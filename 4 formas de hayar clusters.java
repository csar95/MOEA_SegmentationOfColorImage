// METODO RECURSIVO - MUY LENTO (ENTRE 20 Y 30 SEGS)
// -------------------------------------------------------------------------------------------------------------------------
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

// 2 ARRAYLIST
// -------------------------------------------------------------------------------------------------------------------------
private ArrayList<Integer> find_cluster_elems (ArrayList<Integer> cluster, int[] solution) {
	
	ArrayList<Integer> pending = new ArrayList<Integer>(), options = new ArrayList<Integer>();
	int destination;
	pending.add(cluster.get(0));
	
	while (!pending.isEmpty()) {
		
		Iterator<Integer> iterator = pending.iterator();		
		while (iterator.hasNext()) {			
			destination = iterator.next();
			
			if (destination >= this.width && solution[destination - this.width] == destination) {
				options.add(destination - this.width);
			}
			if ((destination + 1) % this.width != 0 && solution[destination + 1] == destination) {
				options.add(destination + 1);
			}
			if (destination + this.width < (this.height * this.width) && solution[destination + this.width] == destination) {
				options.add(destination + this.width);
			}
			if (destination % this.width != 0 && solution[destination - 1] == destination) {
				options.add(destination - 1);
			}				
		}
		
		pending.clear();
		
		for (int i : options) {		
			cluster.add(i);
			pending.add(i);
		}
		
		options.clear();
	}

	return cluster;		
}

// 1 ARRAYLIST
// -------------------------------------------------------------------------------------------------------------------------
private ArrayList<Integer> find_cluster_elems (ArrayList<Integer> cluster, int[] solution) {
	
	ArrayList<Integer> pending = new ArrayList<Integer>();
	int destination;
	pending.add(cluster.get(0));		
		
	Iterator<Integer> iterator = pending.iterator();		
	while (iterator.hasNext()) {
	
		destination = iterator.next();
		iterator.remove();
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

	return cluster;
}

// QUEUE - LINKEDLIST (LO MAS RAPIDO)
// -------------------------------------------------------------------------------------------------------------------------
private ArrayList<Integer> find_cluster_elems (ArrayList<Integer> cluster, int[] solution) {
	
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
	
	return cluster;
}