package org.fog.utils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <h1>CSV Reader </h1>
 * A class to read in cartesian coordinates from a CSV file 
 * 
 * @author 	William McCarty
 * @version 1.0.0
 * @since 	June 14, 2017
 */
public class CSVReader {

	private String csvFileName;
	
	/////////////////////////////////////////////////////////
	/////////////////// CONSTRUCTORS
	/////////////////////////////////////////////////////////

	/** 
	 * <b> Generic Constructor </b>
	 * Basic constructor for CSVReader 
	 * @param filenamein the path to the CSV file 
	 */
	public CSVReader(String filenamein) {
		this.setFileName(filenamein);
	}

	/////////////////////////////////////////////////////////
	/////////////////// 	GETTERS AND SETTERS
	/////////////////////////////////////////////////////////
	public 	void setFileName(String fn) {this.csvFileName = new String(fn);}
	public 	String getFileName() {return this.csvFileName;}



	/////////////////////////////////////////////////////////
	/////////////////// 	METHODS
	/////////////////////////////////////////////////////////
	/**
	 * <b> Parse File Method </b>
	 * A function to parse a CSV File containing comma delimited cartesian coordinates 
	 * 		x1,y1
	 *		x2,y2...
	 *
	 * @param 	csvFileName path to the CSV File 
	 * @return 	an array of cartesian points 
	 */
	public static Point[] parseFile(String csvFileName) {
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
		ArrayList<Point> pointArrayList = new ArrayList<Point>(50);
		try {
			br = new BufferedReader(new FileReader(csvFileName));
			while((line = br.readLine()) !=null) {
				String[] row = line.split(csvSplitBy);
				Double xcoord = Double.parseDouble(row[0]);
				Double ycoord = Double.parseDouble(row[1]);
				Point point = new Point(xcoord, ycoord);

				pointArrayList.add(point);
			}
	 	} catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (NumberFormatException e) {
	        e.printStackTrace();
	    } finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}

		Point pointarray[] = pointArrayList.toArray(new Point[pointArrayList.size()]);
		return pointarray;
	}
}