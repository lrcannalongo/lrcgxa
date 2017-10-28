package cs536;

/*
 * a simple class to represent 3d points
 */

import java.util.ArrayList;

public class Point3d {
	protected double x;
	protected double y;
	protected double z;
	
	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	protected  Point3d(Double[] xyz) {
		this.x = xyz[0];
		this.y = xyz[1];
		this.z = xyz[2];
	}
	
	public Point3d(Point3d p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	protected  Point3d(String[] xyz) {
		// must be size-3 array
		try {
			if (xyz.length != 3) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.x = Double.parseDouble(xyz[0]);
		this.y = Double.parseDouble(xyz[1]);
		this.z = Double.parseDouble(xyz[2]);
	}
	
	public String toString() {
		return "(" + Double.toString(x) + ", " + Double.toString(y) + ", " + Double.toString(z) + ")";
	}
	
	public String toStringNoFormat() {
		return Double.toString(x) + " " + Double.toString(y) + " " + Double.toString(z);
	}
	
	// returns an array list (vector class is deprecated) of the coordinates
	public ArrayList<Double> asVector() {
		ArrayList<Double> result = new ArrayList<Double>();
		result.add(x);
		result.add(y);
		result.add(z);
		return result;
	}
}
