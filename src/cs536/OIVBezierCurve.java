package cs536;

/*
 *    Copyright 2017 Lewis Cannalongo

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

/*
 * a class for creating Open Inventor representations of a Bezier curve from a set of control points
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.util.CombinatoricsUtils; // binomialCoefficient() method


public class OIVBezierCurve {
	
	private Double deltaU; // input: delta u for bezier approximation
	private Double cpRadius; // input: cp radius for output string
	
	private Map<Integer, Point3d> curve; // output: the points that make up the curve approximation along with the order to trace for openIV
	private List<Point3d> lineCoords; // output: the points that make up the curve w/o an index
	
	private List<Point3d> matrixG; // matrix of control point vectors
	private List<Double> matrixMU; // essentially B(u)
	
	public OIVBezierCurve(Collection<Point3d> cps, Double dU, Double rad) {
		this.matrixG = (List<Point3d>) cps;
		this.deltaU = dU;
		this.cpRadius = rad;
		
		curve = new HashMap<Integer, Point3d>();
		generateCurve();
		lineCoords = new ArrayList<Point3d>(curve.values()); // may be useful later
	}
	

	// computes the points along the curve by iterating at deltaU and evaluating Q(u) = G dot B(u)
	// stores result in this object's "curve" field
	private void generateCurve() {		
		int idx = 0;
		//build bezier curve iteratively
		for (double u = 0; u < 1 - deltaU; u += deltaU) {
			fillMU(u);
			curve.put(idx, pointAt(u));
			idx++;
		}
		// finally, make sure you evaluate at t = 1
		fillMU(1);
		curve.put(idx, pointAt(1));
	}
	
	// computes the bernstein polynomials from 0 to #controlPoints - 1 for a given u and fills them into a matrix. shortcut to get M dot U
	private void fillMU(double u){
		matrixMU = new ArrayList<Double>();
		double tmp;
		int k = matrixG.size() - 1;
		
		for (int i = 0; i <= k; i++) {
			tmp = (CombinatoricsUtils.binomialCoefficient(k, i)) * Math.pow((1-u),(k-i)) * Math.pow(u, i);
			matrixMU.add(tmp);
		}	
	}
	
	// evaluate Q(u) = G * B(u)
	private Point3d pointAt(double u) {
		Point3d result = new Point3d(0, 0, 0);
		for (int i = 0; i < matrixG.size(); i++) {
			result.x += matrixG.get(i).x * matrixMU.get(i);
			result.y += matrixG.get(i).y * matrixMU.get(i);
			result.z += matrixG.get(i).z * matrixMU.get(i);
			}
		return result;
	}
	
	// returns the coordinates of the curve - added for posterity
	public List<Point3d> getCoords() {
		return lineCoords;
	}
	
	// format the curve and its control points as an OpenInventor separator
	public String asIVString(){
		String result = "";
		
		for (Point3d p : matrixG) {
			OIVSphere s = new OIVSphere(p, cpRadius);
			result += s.asIVString();
		}
		
		OIVPolyLine pl = new OIVPolyLine(new ArrayList<Point3d>(curve.values()), new ArrayList<Integer>(curve.keySet()));
		result += pl.asIVString();
		
		return result;
	}
}
	
