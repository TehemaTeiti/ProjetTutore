package utils;

import java.util.Arrays;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageToArray {
	
	private Mat src ;
	
	public ImageToArray(String filename) {
        src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
	}

	public double[][] toDoubleArray(){
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        double array[][] = new double[gray.cols()][gray.rows()];
        System.out.println("cols " + gray.cols() + " rows " + gray.rows());
        for(int i = 0 ; i < gray.cols() ; i++) {
        	for( int j = 0 ; j < gray.rows() ; j++) {
        		array[i][j] = gray.get(j, i)[0];
        	}
        }
        return array;
	}
}
