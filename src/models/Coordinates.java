package models;

public class Coordinates {
	
	private int i, j;
	
	public Coordinates (int x, int y) {
		this.setCoordI(y);
		this.setCoordJ(x);
	}
	
	public boolean equals(Object o) {
		
	    if (!(o instanceof Coordinates))
	    	return false;
	    
	    Coordinates aux = (Coordinates) o;
	    return (this.i == aux.i && this.j == aux.j);
	    
	}

	public int getCoordI() {
		return this.i;
	}

	public void setCoordI(int i) {
		this.i = i;
	}

	public int getCoordJ() {
		return this.j;
	}

	public void setCoordJ(int j) {
		this.j = j;
	}

}
