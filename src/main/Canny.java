package main;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Canny {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String filename = "test.png";
		
		Mat src = Imgcodecs.imread(filename);
		Mat canny = src.clone();
		if (src.empty()) {
			System.out.println("Error opening image");
			System.exit(-1);
		}

		Imgproc.Canny(src, canny, 10, 50);
		
		HighGui.imshow("", src);
		HighGui.imshow("", canny);
		HighGui.waitKey();
		System.exit(0);
		
	}
	
}