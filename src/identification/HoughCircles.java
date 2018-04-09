package identification;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import database.packaging.BallGridArray;
import database.packaging.PackagingType;
import database.packaging.QuadFlatPackage;

/**
 * Cette classe permet de détecter des cercles dans une image.
 *
 */
public class HoughCircles {
	
	/*      distInv = gray.rows/distInv: Minimum distance between detected centers.
			uppTresholdCanny : Upper threshold for the internal Canny edge detector.
			centerTreshold : Threshold for center detection.
			minRadius : Minimum radius to be detected. If unknown, put zero as default.
			maxRadius : Maximum radius to be detected. If unknown, put zero as default.
			*/
	public Mat run(String filename, int distInv, double uppTresholdCanny, double centerTreshold, int minRadius, int maxRadius) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // TODO voir si à utiliser dans fonction appelante
    	//TODO travailler sur une partie de l'image
        // Load an image
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        if( src.empty() ) {
            System.out.println("Error opening image!");
            System.exit(-1);
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/distInv, // change this value to detect circles with different distances to each other
                uppTresholdCanny, centerTreshold, minRadius, maxRadius); // change the last two parameters
                // (min_radius & max_radius) to detect larger circles
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
        }
        HighGui.imshow("detected circles wesh", src); // TODO voir si à retirer
        HighGui.waitKey();
        return circles;
    }

}
