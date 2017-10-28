package cs536;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class OIVCRSpline {
	// matrix M is given for cubic C-R splines
	private static final Double[][] matrixM = {{-1.0, 2.0, -1.0, 0.0}, {3.0, -5.0, 0.0, 2.0}, {-3.0, 4.0, 1.0, 0.0}, {1.0, -1.0, 0.0, 0.0}};
	
	private Double deltaU;   // input: delta u for segment approximation
	private Double cpRadius; // input: cp radius for output string
	
	private Map<Integer, Point3d> spline; // output: the points that make up the curve approximation along with the order to trace for openIV
	private List<Point3d> lineCoords;     // output: the points that make up the curve w/o an index
	
	private List<Point3d> controlPoints; 		// matrix of provided control point vectors
	private List<Point3d> workingControlPoints; // matrix of control point vectors including "hidden" points before and after initial/terminal points
	private List<Point3d> tanVecs;       		// matrix of provided tangent vectors
	
	private List<Point3d> matrixG; 	// refilled as necessary for which segment we are evaluating
	private Double[] matrixU;		// refilled as necessary for current u
	
	
	private List<Double> matrixMU; 

	
	public OIVCRSpline(Collection<Point3d> cps, Collection<Point3d> tvs, Double dU, Double rad) {
		this.controlPoints = (List<Point3d>) cps;
		this.tanVecs = (List<Point3d>) tvs;
		this.deltaU = dU;
		this.cpRadius = rad;
		
		this.matrixU = new Double[4]; // 4 entries needed for cubic spline
		
		// calculate "hidden" control points P-1 & PN+1 before initial and after terminal provided cps 
		this.workingControlPoints = new ArrayList<Point3d>(controlPoints);
		workingControlPoints.add(0, cpBefore(workingControlPoints.get(0), tanVecs.get(0)));
		workingControlPoints.add(cpLast(workingControlPoints.get(controlPoints.size() - 1), tanVecs.get(1)));
		
		// generate the spline
		spline = new HashMap<Integer, Point3d>();
		generateSpline();
		lineCoords = (new ArrayList<Point3d>(spline.values())); // may be useful later
	}	
	
	// vary u between 0 and 1 for each control point segment to approximate the spline as a series of polyline points
	private void generateSpline() {
		// determine number of spline segments based on number of control points
		int splineSegments = controlPoints.size() - 1;
		
		// keep track of polyline point index
		int idx = 0;
		
		// for each segment
		for (int i = 1; i <= splineSegments; i++) {
			//calculate new matrix G
			fillG(i);
			// vary u from 0 to 1 to get points on the polyline 
			for (double u = 0; u < 1; u += deltaU) {
				fillU(u);
				fillMU();
				spline.put(idx++, pointAt(u));
			}
			// terminate the segment at u = 1 (causes same control point to be interpolated twice but matches provided test cases)
			fillU(1);
			fillMU();
			spline.put(idx++, pointAt(1));
		}
	}
	
	// fills matrix G with necessary control point vectors based on the segment we are filling
	private void fillG(int idx) {
		matrixG = new ArrayList<Point3d>();
		matrixG.add(workingControlPoints.get(idx-1));
		matrixG.add(workingControlPoints.get(idx));
		matrixG.add(workingControlPoints.get(idx+1));
		matrixG.add(workingControlPoints.get(idx+2));
	}
	
	// fill matrix u with current values
	private void fillU(double u) {
		matrixU[0] = (Math.pow(u, 3));
		matrixU[1] = (Math.pow(u,  2));
		matrixU[2] = u;
		matrixU[3] = 1.0;
	}
	
	// calculate m dot u
	private void fillMU(){
		Double[] tmp = new Double[matrixU.length];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = 0.0;
		}
		matrixMU = new ArrayList<Double>();
		// matrix multiply
		for (int i = 0; i < matrixM.length; i++) {
			for (int j = 0; j < matrixU.length; j++) {
				tmp[i] += (matrixM[i][j] * matrixU[j]);
			}
		}
		
		// put m dot u in a vector for easier handling
		matrixMU.addAll(Arrays.asList((tmp)));
	}
	
	// evaluate the spline at a given point on the current segment
	private Point3d pointAt(double u) {
		Point3d result = new Point3d(0, 0, 0);
		for (int i = 0; i < matrixG.size(); i++) {
			result.x += 0.5 * matrixG.get(i).x * matrixMU.get(i);
			result.y += 0.5 * matrixG.get(i).y * matrixMU.get(i);
			result.z += 0.5 * matrixG.get(i).z * matrixMU.get(i);
			}
		return result;
	}
	
	// finds the control point prior to a control point based on the vector tangent to given cp
	// used for finding P-1
	private Point3d cpBefore(Point3d cp, Point3d tv) {
		// first we need the control point after this one
		Point3d next = controlPoints.get(workingControlPoints.indexOf(cp) + 1);		
		double x, y, z;
		
		// tv = (CP+1 - CP-1) / 2
		// CP-1 = CP+1 - 2tv
		
		x = next.x - (2 * tv.x);
		y = next.y - (2 * tv.y);
		z = next.z - (2 * tv.z);
		
		return new Point3d(x, y, z);
	}
	
	// finds the 'hidden' control point after the terminal control point based on the cp prior to terminal cp
	private Point3d cpLast(Point3d cp, Point3d tv) {
		double x, y , z;
		
		// tv = (CP+2 - CP) / 2
		// CP+2 = CP + 2tv
		
		x = cp.x + (2 * tv.x);
		y = cp.y + (2 * tv.y);
		z = cp.z + (2 * tv.z);
		
		return new Point3d(x, y, z);
	}

	// format the curve and its control points as an OpenInventor separator
	public String asIVString(){
		String result = "";
		
		for (Point3d p : controlPoints) {
			OIVSphere s = new OIVSphere(p, cpRadius);
			result += s.asIVString();
		}
		
		OIVPolyLine pl = new OIVPolyLine(new ArrayList<Point3d>(spline.values()), new ArrayList<Integer>(spline.keySet()));
		result += pl.asIVString();
		
		return result;
	}

	public List<Point3d> getLineCoords() {
		return lineCoords;
	}
}
