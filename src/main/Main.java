package main;

import database.packaging.BallGridArray;
import database.packaging.PackagingType;
import database.packaging.QuadFlatPackage;
import identification.BGABallsIdentification;
import identification.HoughCircles;
import identification.IdentificationPackagingType;
import utils.*;
import identification.*;

public class Main {
	
	public static void main(String[] args) {
		String[] names = {"FPGA_20mic 3.tif"};
		for (String name : names) {
			
			if(name.contains(".tif")) {
	            TiffToPng convert = new TiffToPng(name);
	            name = convert.changeName(name);
	        }
			
			// reconnaissance du type de boîtier
			PackagingType type = IdentificationPackagingType.computeType(name);
			switch (type) {
			case BGA:
				BallGridArray bga = new BallGridArray();
				// TODO
				BGABallsIdentification getBalls = new BGABallsIdentification(bga, name);
				RemoveNoise rn = new RemoveNoise();
				rn.removeNoise(name);
				break;
			case QFP:
				QuadFlatPackage qfp = new QuadFlatPackage();
				// TODO
				break;
			}
			System.out.println("["+name+"] Type détecté : " + type.toString());
						
		}
		System.out.println("OK");
		System.exit(0);
		
	}
}
