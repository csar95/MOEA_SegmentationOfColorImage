package models;

public class Coordinates {
	
	private int i, j;
	
	public Coordinates (int x, int y) {
		this.setCoordI(y);
		this.setCoordJ(x);
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
