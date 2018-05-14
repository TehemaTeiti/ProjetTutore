package identification;

import database.packaging.BallGridArray;
import database.packaging.PackagingType;
import utils.Dist;
import utils.GetPopular;

import java.util.*;
import java.util.Map.Entry;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import database.components.*;


public class BGABallsIdentification {
	
	public BGABallsIdentification(BallGridArray bga, String filename) {
		/* TODO
		 * get circles with hough transform OK
		 * get radius (most popular) OK
		 * get rows OK
		 * get columns OK 
		 * remove false positive  OK
		 * add false negative
		 * set balls in bga  OK
		 */
		
		// Getting circles with hough transform 
		System.out.println("Balls identification :");
		HoughCircles c = new HoughCircles();
		Mat circles = c.run(filename,200,50.0,27.0,1,30);
		/* TODO
		 * ask if most popular radius should be calculated with int (round or cut) (94 out of 307) or double (25 out of 307)
		 * double for now
		 */
		// Getting most popular radius
		GetPopular radiusP = new GetPopular(circles,2);
		double radius = radiusP.getPopular();
		// Getting rows
		double rows[] = getGrid(circles,"row");
		// Getting columns
		double cols[] = getGrid(circles,"col");
		// check false positive
		ArrayList<Integer> indexFalsePositive = checkFalsePositive(circles, radius, rows, cols);
		// create list
		ArrayList<Ball> balls = new ArrayList<>(); 
		Ball b ;
		for(int i = 0 ; i < circles.cols(); i++) {
			if(!indexFalsePositive.contains(i)) {
				double[] coord = circles.get(0, i);
				b = new Ball(coord[0],coord[1],radius);
				balls.add(b);
			}	
		}
		// format coord
		balls = formatCoord(balls,rows,cols);
		// check false negative
		checkFalseNegative(balls, rows, cols, radius);
		// add in bga
		bga.setBalls(balls);
		
		// print debug
		Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
		src.setTo(new Scalar(255,255,255));
		for (int x = 0; x < balls.size(); x++) {
	           Ball b1 = balls.get(x);
	           Point center = new Point(b1.getX(), b1.getY());
	           // circle center
	           Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
	           // circle outline
	           Imgproc.circle(src, center, (int)radius, new Scalar(255,0,255), 3, 8, 0 );
	       }
	       HighGui.imshow("balls detected", src); 
	       HighGui.waitKey();
		
	}
	
	public ArrayList<Ball> checkFalseNegative(ArrayList<Ball> balls, double[] rows, double[] cols, double radius){
		ArrayList<Ball> res = new ArrayList<>();
		double diff ;
		double d ;
		int indexX = -1;
		int indexY = -1;
		int grid[][] = new int[cols.length][rows.length];
		for(Ball b : balls) {
			diff = Double.MAX_VALUE;
			for(int i = 0 ; i < rows.length ; i++) {
				d = Math.abs(b.getY()-rows[i]);
				if(d < diff) {
					diff = d ;
					indexY = i ;
				}
			}
			diff = Double.MAX_VALUE;
			for(int i = 0 ; i < cols.length ; i++) {
				d = Math.abs(b.getX()-cols[i]);
				if(d < diff) {
					diff = d ;
					indexX = i ;
				}
			}
			grid[indexX][indexY] = 1 ;
		}
		ArrayList<Integer[]> coordMissing = new ArrayList<>();
		Integer coord[] = new Integer[2];
		//Count missing balls (full grid)
		for(int i = 0 ; i < rows.length ; i++) {
			for(int j = 0 ; j < cols.length ; j++) {
				if(grid[j][i] == 0) {
					coord[0] = j ;
					coord[1] = i ;
					coordMissing.add(coord);
				}
			}
		}
		System.out.println("Nb of missing balls" + coordMissing.size());
		if (coordMissing.size() > 1) {
			Integer symX[] = new Integer[2] ;
			Integer symY[] = new Integer[2] ;
			Integer symO[] = new Integer[2] ;
			Boolean symOK ;
			// Look for symetry 
			for(Integer i[] : coordMissing) {
				symOK = false ;
				symX[0] = i[0] ;
				symX[1] = rows.length - i[1] ;
				symY[0] = cols.length - i[0] ;
				symY[1] = i[1] ;
				symO[0] = symY[0] ;
				symO[1] = symX[0] ;
				for(Integer j[] : coordMissing) {
					if(j == symX || j == symY || j == symO)
						symOK = true ;
				}
				if(symOK) {
					Ball b = new Ball(cols[i[0]], rows[i[1]], radius);
					balls.add(b);	
				}
			}			
		}
		else if (coordMissing.size() == 1) {
			coord = coordMissing.get(0);
			Ball b = new Ball(cols[coord[0]], rows[coord[1]], radius);
			balls.add(b);
		}
		return balls;
	}
	public ArrayList<Ball> formatCoord(ArrayList<Ball> balls, double[] rows, double[] cols){
		ArrayList<Ball> res = new ArrayList<>();
		Ball b ;
		double d ;
		double yDiff ;
		double xDiff ;
		int index ;
		for(int i = 0 ; i < balls.size() ; i++) {
			b = balls.get(i);
			index = -1 ;
			xDiff = Double.MAX_VALUE ;
			yDiff = Double.MAX_VALUE ;
			for(int j = 0 ; j < rows.length ; j++) {
					d = Math.abs(b.getY()-rows[j]);
					if(d < yDiff) {
						yDiff = d ;
						index = j ;
					}
			}
			b.setY(rows[index]);
	
			index = -1 ;
			for (int j = 0 ; j < rows.length ; j++) {
				d = Math.abs(b.getX()-cols[j]);
				if(d < xDiff) {
					xDiff = d ;
					index = j ;
				}
			}
			b.setX(cols[index]);
			res.add(b);
		}
		return res;
	}
	public ArrayList<Integer> checkFalsePositive(Mat mat, double radius, double[] rows, double[] cols){
		double coord[] ;
		double xDiff ;
		double yDiff ;
		double d ;
		Double diff[] = new Double[2] ;
		ArrayList<Integer> indexFalse = new ArrayList<>();
		ArrayList<ArrayList<Integer>> indexConflict = new ArrayList<>() ;
		ArrayList<Double> diffs = new ArrayList<>(); 
		ArrayList<Integer> aux ;
		for(int i = 0 ; i < mat.cols() ; i++) {
			coord = mat.get(0, i);
			xDiff = Double.MAX_VALUE ;
			yDiff = Double.MAX_VALUE ;
			for (double row : rows) {
				d = Math.abs(coord[1]-row);
				if(d < yDiff) {
					yDiff = d ;
					if(i==228) {
						System.out.println("closest row " + row + " d " + d);
					}
				}
			}
			for (double col : cols) {
				d = Math.abs(coord[0]-col);
				if(d < xDiff) {
					xDiff = d ;
					if(i==228) {
						System.out.println("closest col"
								+ " " + col + " d " + d);
					}
				}
			}
			diff[0] = xDiff ;
			diff[1] = yDiff ;
			diffs.add(xDiff);
			diffs.add(yDiff);
			if(i==306)
				System.out.println("circle " + i + " diff " + diffs.get(i*2) + " " + diffs.get(i*2+1));
			if(xDiff > 5.0 || yDiff > 5.0) {
				indexFalse.add(i);
			} else {
				/*if distCarte < 5.0 
				 *  chooseBest
				 */
				if(i==228)
					System.out.println("circle " + 228 + " diff " + diffs.get(228*2) + " " + diffs.get(228*2+1));
				aux = new ArrayList<>();
				for(int j = i+1 ; j < mat.cols(); j++) {
					if(Dist.dist(coord[0], coord[1], mat.get(0, j)[0], mat.get(0, j)[1]) < 30.0) {
						aux.add(j);
					}
				}
				if(i==228)
					System.out.println("circle " + 228 + " diff " + diffs.get(228*2) + " " + diffs.get(228*2+1));
				if(aux.size()>0) {
					aux.add(i);
					indexConflict.add(aux);
				}
				if(i==228)
					System.out.println("circle " + 228 + " diff " + diffs.get(228*2) + " " + diffs.get(228*2+1));
			}
		}
		/*System.out.println("circle " + 228 + " diff " + diffs.get(228*2) + " " + diffs.get(228*2+1));
		System.out.println("Test rows " + Arrays.toString(rows) + "\ncols " + Arrays.toString(cols) );
		System.out.println("Test2  circle 228" + Arrays.toString(mat.get(0, 228)));
		System.out.println("After first check (col/row) nb Balls " + (mat.cols() - indexFalse.size()) + "\n nb false balls " + indexFalse.size() + "\nfalse balls " + indexFalse);
		*/
		double dMin;
		for(ArrayList<Integer> l : indexConflict) {
			dMin = Double.MAX_VALUE;
			int previous = -1 ;
			for(int j : l) {
				if(!indexFalse.contains(j)) {
					System.out.println( j + " not registered");
					double dist = Math.sqrt(diffs.get(j*2)+diffs.get(j*2+1));
					if(dist < dMin) {
						if(previous != -1) {
							indexFalse.add(j);
						}
						System.out.print("test conflict inf " + j + " " + previous + " dist " + dist + " dmin " + dMin);
						dist = dMin ;
						previous = j ;
					}
					else {
						indexFalse.add(j);
						System.out.print("test conflict sup " + j + " " + previous + " dist " + dist + " dmin " + dMin);
					}
				}				
			}
			System.out.println(l);
		}
		/*System.out.println("circle " + 228 + " diff " + diffs.get(228*2) + " " + diffs.get(228*2+1));
		System.out.println("Test circle 261 " + Arrays.toString(mat.get(0, 261)) + "circle 228 " + Arrays.toString(mat.get(0, 228)));
		System.out.println("After second check (dist between circles) nb Balls " + (mat.cols() - indexFalse.size()) + "\n nb false balls " + indexFalse.size() + "\nfalse balls " + indexFalse);
		*/
		return indexFalse ;
	}
	/* TODO
	 * ask if rows/col should be determined by median or most popular
	 */
	public double[] getGrid(Mat mat, String mode) {
		double tabs[] = new double[mat.cols()] ;
		for (int i = 0 ; i < mat.cols(); i++) {
			if(mode.equals("row"))
				tabs[i] = mat.get(0, i)[1];
			else if(mode.equals("col"))
				tabs[i] = mat.get(0, i)[0];
			else 
				System.exit(-1);
		}
		Arrays.sort(tabs);
		System.out.println(Arrays.toString(tabs));
		double last = 0.0 ;
		Boolean same = true ;
		ArrayList<Integer> diffList = new ArrayList<>();
		for(int i = 0 ; i < tabs.length ; i++) {
			same = ((tabs[i]-last) < 5.0);
			last = tabs[i] ;
			if(!same) {
				diffList.add(i);
			}	
		}
		System.out.println("index of new tab list " + diffList + " number of tabs " + diffList.size());
		GetPopular tabP ;
		double tabsFused[] = new double[diffList.size()];
		for(int i = 0 ; i < tabsFused.length ; i++) {
			double tab[] ;
			if (i == tabsFused.length-1) {
				tab = Arrays.copyOfRange(tabs, diffList.get(i), tabs.length);
			} else {
				tab = Arrays.copyOfRange(tabs,diffList.get(i) , diffList.get(i+1));
			}
			tabP = new GetPopular(tab);
			System.out.println("tab " + i + "" + Arrays.toString(tab));
			tabsFused[i] = tabP.getPopular();
		}
		System.out.println("tabs after fusion " + Arrays.toString(tabsFused));
		ArrayList<Double> resL = new ArrayList<>(); 
		for(double d : tabsFused) {
			if(d != 0)
				resL.add(d);
		}
		Double aux[] = new Double[resL.size()];
		aux = resL.toArray(aux);
		double res[] = new double[aux.length];
		for (int i = 0 ; i < aux.length ; i++)
			res[i] = (double) aux[i];
		System.out.println("tabs after first cleaning " + Arrays.toString(res));
		double diff[] = new double[res.length-1];
		for (int i = 0 ; i < diff.length ; i++) {
				diff[i] = res[i+1] - res[i];
		}
		System.out.println("space between tabs " + Arrays.toString(diff) );
		tabP = new GetPopular(diff);
		double space = tabP.getPopular();
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
		System.out.println("tabs after second cleaning " + Arrays.toString(res));
		return res ;
	}
	

}
