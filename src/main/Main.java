package main;

import database.packaging.PackagingType;
import identification.HoughCircles;
import identification.IdentificationPackagingType;
import utils.TiffToPng;

public class Main {
	
	public static void main(String[] args) {
		String[] names = {"FPGA_20mic 3.tif"};
		for (String name : names) {
			
			if(name.contains(".tif")) {
	            TiffToPng convert = new TiffToPng(name);
	            name = convert.changeName(name);
	        }
			
			PackagingType type = IdentificationPackagingType.computeType(name);
			System.out.println("["+name+"] Type détecté : " + type);
			
		}
		System.out.println("OK");
		System.exit(0);
		
	}
}
