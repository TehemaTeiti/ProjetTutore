package identification;

import org.opencv.core.Mat;

import database.packaging.BallGridArray;
import database.packaging.PackagingType;
import database.packaging.QuadFlatPackage;

public class IdentificationPackagingType {

	public static PackagingType computeType(String filename) {
		HoughCircles c = new HoughCircles();
		Mat circles = c.run(filename);
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
