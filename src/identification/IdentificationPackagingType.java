package identification;

import org.opencv.core.Mat;

import database.packaging.BallGridArray;
import database.packaging.PackagingType;
import database.packaging.QuadFlatPackage;

public class IdentificationPackagingType {

	public static PackagingType computeType(String filename) {
		HoughCircles c = new HoughCircles();
		Mat circles = c.run(filename,200,50.0,27.0,1,30);
		//TODO revoir archi
		int nbCircle = circles.cols(); 
		if (nbCircle > 10) {
			return new BallGridArray();
		}
		else {
			return new QuadFlatPackage();
		}
		
	}
	
}
