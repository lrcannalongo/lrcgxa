package cs536;

/*
 * a class for creating OpenInventor strings that represent spheres from an origin and radius
 */

import java.util.List;
import java.util.Arrays;
import java.util.Collection;

public class OIVSphere {
	
	protected String lightModel;
	protected List<Double> diffuseColor; 
	protected Point3d origin;
	protected double radius;
	
	// constructor using default material and light model
	public OIVSphere(Point3d origin, double radius) {
		this(origin, radius, Arrays.asList(new Double[]{1.0, 1.0, 1.0}), "PHONG");
	}
	
	// full constructor
	public OIVSphere(Point3d origin, double radius, Collection<Double> diffuseColor, String lightModel) {
		this.origin = origin;
		this.radius = radius;
		this.diffuseColor =  (List<Double>) diffuseColor;
		this.lightModel = lightModel;
	}
	
	// for growing spheres later
	public void setRadius(double r) {
		try {
			if (r <= 0) {
				throw new IllegalArgumentException();
			}
			radius = r;
		} catch (IllegalArgumentException e) {
			System.out.println("Error: This program is intended only to create spheres with a positive radius.");
			e.printStackTrace();
		}
	}
	
	// move sphere to a specific point
	public void setOrigin(Point3d o) {
		origin = o;
	}
	
	// stub: move the sphere by a vector
	public void moveByVector(Object vector) {
		// origin = origin * vector
	}
	
// format this sphere as an OpenInventor separator
// TODO: create IVObject interface/adt w/ FormatObject(IVObject.TYPE, Collection<Object> params) method
	public String asIVString() {
		String crlf = System.lineSeparator();
		
		String result = "Separator {" + crlf;
		
		// add light model
		result += "\t" + "LightModel{" + crlf + "\t\tmodel " + lightModel + crlf + "\t}" + crlf;
		
		// add material
		result += "\t" + "Material{" + crlf + "\t\tdiffuseColor " + diffuseColorString() + crlf + "\t}" + crlf;
		
		//add origin translation
		result += "\t" + "Transform{" + crlf + "\t\ttranslation " + origin.toStringNoFormat() + crlf + "\t}" + crlf;
		
		// add radius
		result += "\t"  + "Sphere{" + crlf + "\t\tradius " + Double.toString(radius) + crlf + "\t}" + crlf;
				
		// close separator 
		result += "}" + crlf;
		
		return result;
	}

	private String diffuseColorString() {
		String result = "";
		for (Double d : diffuseColor) {
			result += d.toString() + " ";
		}
		return result.trim();
	}
}