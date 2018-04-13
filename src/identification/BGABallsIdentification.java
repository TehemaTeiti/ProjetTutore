package identification;

import database.packaging.BallGridArray;
import database.packaging.PackagingType;
import utils.GetPopular;

import java.util.*;
import java.util.Map.Entry;

import org.opencv.core.Mat;

import database.components.*;


public class BGABallsIdentification {
	
	public BGABallsIdentification(BallGridArray bga, String filename) {
		/* TODO
		 * get circles with hough transform 
		 * get radius (most popular)
		 * get rows
		 * get columns 
		 * check false positive
		 * check false negative
		 * set balls in bga
		 */
		
		// Getting circles with hough transform 
		System.out.println("Balls identification :");
		HoughCircles c = new HoughCircles();
		Mat circles = c.run(filename,200,50.0,27.0,1,30);
		/* TODO
		 * ask if most popular radius should be calculated with int (round or cut) (94 out of 307) or double (25 out of 307)
		 */
		// Getting most popular radius
		GetPopular radiusP = new GetPopular(circles,2);
		double radius = radiusP.getPopular();
		// Getting rows
		double rows[] = getRows(circles);
		
		
	}
	
	/* TODO
	 * ask if rows/col should be determined by median or most popular
	 * last row not selected
	 */
	public double[] getRows(Mat mat) {
		double rows[] = new double[mat.cols()] ;
		for (int i = 0 ; i < mat.cols(); i++) {
			rows[i] = mat.get(0, i)[1];
		}
		Arrays.sort(rows);
		System.out.println(Arrays.toString(rows));
		double last = 0.0 ;
		Boolean same = true ;
		ArrayList<Integer> diffList = new ArrayList<>();
		for(int i = 0 ; i < rows.length ; i++) {
			same = ((rows[i]-last) < 5.0);
			last = rows[i] ;
			if(!same) {
				diffList.add(i);
			}	
		}
		System.out.println("index of new row list " + diffList + " number of rows " + diffList.size());
		GetPopular rowP ;
		double rowsFused[] = new double[diffList.size()];
		for(int i = 0 ; i < rowsFused.length ; i++) {
			double row[] ;
			if (i == rowsFused.length-1) {
				row = Arrays.copyOfRange(rows, diffList.get(i), rows.length);
			} else {
				row = Arrays.copyOfRange(rows,diffList.get(i) , diffList.get(i+1));
			}
			rowP = new GetPopular(row);
			System.out.println("row " + i + "" + Arrays.toString(row));
			rowsFused[i] = rowP.getPopular();
		}
		System.out.println("rows after fusion " + Arrays.toString(rowsFused));
		ArrayList<Double> resL = new ArrayList<>(); 
		for(double d : rowsFused) {
			if(d != 0)
				resL.add(d);
		}
		Double aux[] = new Double[resL.size()];
		aux = resL.toArray(aux);
		double res[] = new double[aux.length];
		for (int i = 0 ; i < aux.length ; i++)
			res[i] = (double) aux[i];
		System.out.println("rows after first cleaning " + Arrays.toString(res));
		double diff[] = new double[res.length-1];
		for (int i = 0 ; i < diff.length ; i++) {
				diff[i] = res[i+1] - res[i];
		}
		System.out.println("space between rows " + Arrays.toString(diff) );
		rowP = new GetPopular(diff);
		double space = rowP.getPopular();
		ArrayList<Double> finalList = new ArrayList<>();
		finalList.add(res[0]);
		for (int i = 0 ; i < diff.length ; i++ ) {
			if(diff[i] >= space - 5 ) {
				finalList.add(res[i+1]);
			} else {
				if(i < diff.length -1)
					diff[i+1] = diff[i+1]+diff[i];
			}
				
		}
		aux = new Double[finalList.size()];
		aux = finalList.toArray(aux);
		res = new double[aux.length];
		for(int i = 0 ; i < aux.length ; i++)
			res[i] = aux[i];
		System.out.println("rows after second cleaning " + Arrays.toString(res));
		return res ;
	}
	

}
