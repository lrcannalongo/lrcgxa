package cs536;

/*
 * a class for creating OpenInventor polylines from a set of coordinates
 */

import java.util.List;
import java.util.Arrays;
import java.util.Collection;

public class OIVPolyLine {
	protected String lightModel;
	protected List<Double> diffuseColor; 
	protected List<Point3d> coords;
	protected List<Integer> coordIndex;
	
	// uses default values for color and light model
	public OIVPolyLine(Collection<Point3d> coords, Collection<Integer> coordIndex) {
		this(coords, coordIndex, Arrays.asList(new Double[]{1.0, 0.2, 0.2}), "BASE_COLOR");
	}
	
	// full constructor
	public OIVPolyLine(Collection<Point3d> coords, Collection<Integer> coordIndex, Collection<Double> diffuseColor, String lightModel) {
		this.lightModel = lightModel;
		this.diffuseColor = (List<Double>) diffuseColor;
		this.coords = (List<Point3d>) coords;
		this.coordIndex = (List<Integer>) coordIndex;
	}
	
	// format this polyline as an OpenInventor separator
	// TODO: create IVObjectFormatter class w/ FormatObject(IVObject.TYPE, Collection<Object> params) method
	public String asIVString() {
			String crlf = System.lineSeparator();
			
			String result = "Separator {" + crlf;
			
			// add light model
			result += "\t" + "LightModel{" + crlf + "\t\tmodel " + lightModel + crlf + "\t}" + crlf;
			
			// add material
			result += "\t" + "Material{" + crlf + "\t\tdiffuseColor " + diffuseColorString() + crlf + "\t}" + crlf;
			
			//add coordinates
			result += "\t" + "Coordinate3{" + crlf +
						"\t\tpoint[" + crlf;
			for (Point3d p : coords) {
				result += "\t\t\t" + p.toStringNoFormat() + "," + crlf;
			}
			result += "\t\t]" + crlf + "\t}" + crlf;
			
			// add coord index
			result += "\t" + "IndexedLineSet{" + crlf + 
						"\t\tcoordIndex[" + crlf + "\t\t\t";
			for (Integer i : coordIndex) {
				result += i.toString() + ",";
			}
			result += "]" + crlf + "\t}" + crlf;
			
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
