package identification;

import org.opencv.highgui.HighGui;

import utils.*;

public class RemoveNoise {
	
	private static ImageToArray toFFT ; 

	public static void removeNoise(String filename) {
		
	    toFFT = new ImageToArray(filename);
	    double[][] spatialData = toFFT.toDoubleArray();
	    //Create the array of test data.
	    int rows = spatialData.length;
	    int cols = spatialData[0].length;
	    //Get a test surface in the space domain.
	         
	    //Display the spatial data.  Don't display
	    // the axes.
	    System.out.println("Display the spatial data");
	    new ImgMod29(spatialData,1,false,1);
	    //Perform the forward transform from the
	    // space domain into the wave-number domain.
	    // First prepare some array objects to
	    // store the results.
	    double[][] realSpect = //Real part
	                          new double[rows][cols];
	    double[][] imagSpect = //Imaginary part
	                          new double[rows][cols];
	    double[][] amplitudeSpect = //Amplitude
	                          new double[rows][cols];
	    System.out.println("Test FFT");
	    //Now perform the transform
	    ImgMod30.xform2D(spatialData,realSpect,
	                       imagSpect,amplitudeSpect);
	 
	    //Display the raw amplitude spectrum without
	    // shifting the origin first.  Display the
	    // axes.
	    System.out.println("Display the raw amplitude spectrum");
	    new ImgMod29(amplitudeSpect,3,true,1);
        HighGui.waitKey();
	    //At this point, the wave-number spectrum is
	    // not in a format that is good for viewing.
	    // In particular, the origin is at the upper
	    // left corner.  The horizontal Nyquist
	    // folding  wave-number is near the
	    // horizontal center of the plot.  The
	    // vertical Nyquist folding wave number is
	    // near the vertical center of the plot.  It
	    // is much easier for most people to
	    // understand what is going on when the
	    // wave-number origin is shifted to the
	    // center of the plot with the Nyquist
	    // folding wave numbers at the edges of the
	    // plot.  The method named shiftOrigin can be
	    // used to rearrange the data and to shift
	    // the orgin in that manner.
	    //Shift the origin and display the real part
	    // of the spectrum, the imaginary part of the
	    // spectrum, and the amplitude of the
	    // spectrum.  Display the axes in all three
	    // cases.
	    double[][] shiftedRealSpect =
	                 ImgMod30.shiftOrigin(realSpect);
	    System.out.println("Display the shifted real part");
	    new ImgMod29(shiftedRealSpect,3,true,1);
        HighGui.waitKey();
	   
	    double[][] shiftedImagSpect =
	                 ImgMod30.shiftOrigin(imagSpect);
	    System.out.println("Display the shifted imaginary part");
	    new ImgMod29(shiftedImagSpect,3,true,1);
        HighGui.waitKey();
	   
	    double[][] shiftedAmplitudeSpect =
	            ImgMod30.shiftOrigin(amplitudeSpect);
	    System.out.println("Display the shifted amplitude");
	    new ImgMod29(shiftedAmplitudeSpect,3,true,1);
        HighGui.waitKey();
	      
	    //Now test the inverse transform by
	    // performing an inverse transform on the
	    // real and imaginary parts produced earlier
	    // by the forward transform.
	    //Begin by preparing an array object to store
	    // the results.
	    double[][] recoveredSpatialData =
	                          new double[rows][cols];
	    //Now perform the inverse transform.
	    ImgMod30.inverseXform2D(realSpect,imagSpect,
	                           recoveredSpatialData);
	                          
	    //Display the output from the inverse
	    // transform.  It should compare favorably
	    // with the original spatial surface.
	    new ImgMod29(recoveredSpatialData,3,false,1);
        HighGui.waitKey();
	}
}
