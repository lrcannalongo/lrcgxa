package cs536;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class CG_HW2 {
	// default values
	static String fileName = "cpts_in.txt";
	static double uDelta = 0.09;
	static double rad = 0.1;	
	
	static List<Point3d> tangentVectors = new ArrayList<Point3d>(); // to store the start/end tangent vectors
	static List<Point3d> controlPoints  = new ArrayList<Point3d>(); // to store control points for line
	
	// PROGRAM ENTRY POINT
	public static void main(String[] args) {
			processArgs(args); // read in the arguments
			readTVfromFile(fileName); // load the tangent vectors from the input file
			readCPfromFile(fileName); // load the control points  from the input file
			writeCRSplineIV(); // compute the catmull-rom spline and write the polyline and spheres to a file
			System.exit(0); // quit
		}
	
	//checks that arguments are valid and sets the appropriate fields
	private static void processArgs(String[] args) {
		ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
		
		try {
			
			// array list index dummy; needed to make sure we don't have superfluous arguments
			int idx;
			
			// too many arguments or arguments insufficient
			if (argList.size() > 6 || argList.size() % 2 != 0) {
				System.out.println(">> Error: Bad argument count. Acceptable arguments: '-f [filename.txt]', '-u 0.xx', '-r #'");
				throw new Exception();
			}
			
			// if supplied, get filename
			// if not .txt, throw error
			try {
				if (argList.contains("-f")) {
					idx = argList.indexOf("-f");
					fileName = argList.get(idx + 1);
					if (!(fileName.substring(fileName.length() -3).equalsIgnoreCase("txt"))) {
						throw new Exception();
					}
					argList.remove(idx + 1);
					argList.remove(idx);
				}
		} catch (Exception e) {
			System.out.println(">> Error: filename argument invalid. Please provide a text (.txt) file.");
			throw new Exception();
		}
		
		// if supplied, get uDelta
		// if not between 0-1, throw error
		if (argList.contains("-u")) {
			idx = argList.indexOf("-u");
			try {
				uDelta = Double.parseDouble(argList.get(idx + 1));
				if (!(uDelta >= 0 && uDelta <= 1)) {
					throw new Exception();
				}
				argList.remove(idx + 1);
				argList.remove(idx);
			} catch (Exception e) {
				System.out.println(">> Error: delta argument invalid. Please provide a number between 0 and 1.");
				throw new Exception();
			}
		}
		
		// if supplied, get cpt radius
		// if negative, throw error
		if (argList.contains("-r")) {
			idx = argList.indexOf("-r");
			try {
				rad = Double.parseDouble(argList.get(idx + 1));
				if (rad <= 0) {
					throw new Exception();
				}
				argList.remove(idx + 1);
				argList.remove(idx);
			} catch (Exception e) {
				System.out.println(">> Error: radius argument invalid. Please provide a positive number.");
				throw new Exception();
			}
		}
		// if any arguments beyond the above 3 were provided
		if (argList.size() != 0) {
			System.out.println(">> Error: Invalid argument(s). Valid arguments are (one or zero of each): '-f',  'u', '-r'");
				throw new Exception();
			}
		}
		catch (Exception e) {
			System.exit(0);
		}
	}
	

	// store the first 2 lines of input file for tangent vectors
	private static void readTVfromFile(String fn) {
		int i = 0;
		try {
			File file = new File(fn);
			FileReader fileReader = new FileReader(file);
			BufferedReader buf = new BufferedReader(fileReader);
			String line;
			while ((line = buf.readLine()) != null && i < 2) {
				tangentVectors.add(new Point3d(line.split(" ")));
				i++;
			}
		fileReader.close();
		} catch (FileNotFoundException f) {
			System.out.println(">> Error: File " + fileName + " not found at specified location.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println(">> Read " + i + " tangent vectors from file.");
	}
	
	// store the control points from input file
	private static void readCPfromFile(String fn) {
		// strategy: read all vectors like for bezier curve but then remove the first 2 entries, leaving only cps
		int i = 0;
		try {
			File file = new File(fn);
			FileReader fileReader = new FileReader(file);
			BufferedReader buf = new BufferedReader(fileReader);
			String line;
			while ((line = buf.readLine()) != null) {
				controlPoints.add(new Point3d(line.split(" ")));
				i++;
			}
		fileReader.close();
		controlPoints.remove(0);
		controlPoints.remove(0);
		} catch (FileNotFoundException f) {
			System.out.println(">> Error: File " + fileName + " not found at specified location.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println(">> Read " + (i-2) + " control points from file.");
	}
	
	//creates an OIVCRSpline object using the file input 
	//writes a new .iv file to the working directory
	private static void writeCRSplineIV() {
		String outFileName = fileName.replaceFirst("[.][^.]+$", "") + ".iv"; //remove file extension and replace with .iv
		
		System.out.println(">> Generating 3d Catmull-Rom Spline from " + controlPoints.size() + " control points.");
		
		// actual generation of Bezier curve happens here, in OIVBezierCurve
		OIVCRSpline crs = new OIVCRSpline(controlPoints, tangentVectors, uDelta, rad);
		
		//write the iv file
		System.out.println(">> Saving " + outFileName + " to " + System.getProperty("user.dir"));
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFileName));
		    out.write(crs.asIVString());
		    out.close();
		} catch (IOException e) {
			System.out.println(">> Error saving file.");
			e.printStackTrace();
		}
	}
}
