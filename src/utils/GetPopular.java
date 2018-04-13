package utils;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.opencv.core.Mat;


public class GetPopular {
	
	private double[] array ;
	
	
	// Only Works with mat given by Hough transform
	public GetPopular(Mat mat , int x) {
		array = new double[mat.cols()] ;
		for (int i = 0 ; i < mat.cols(); i++) {
			array[i] = mat.get(0, i)[x];
		}
		
	}
	
	public GetPopular(double[] array) {
		this.array = array ;
	}
	
	public double getPopular() {
        //Creating HashMap object with elements as keys and their occurrences as values
        HashMap<Double, Integer> elementCountMap = new HashMap<Double, Integer>();
        //Inserting all the elements of inputArray into elementCountMap
        for (double i  : array) 
        {
            if (elementCountMap.containsKey(i))
            {
                //If an element is present, incrementing its count by 1      
                elementCountMap.put(i, elementCountMap.get(i)+1);
            }
            else
            {
                //If an element is not present, put that element with 1 as its value                 
                elementCountMap.put(i, 1);
            }
        }         
        double element = 0;         
        int frequency = 1;         
        //Iterating through elementCountMap to get the most frequent element and its frequency         
        Set<Entry<Double, Integer>> entrySet = elementCountMap.entrySet();         
        for (Entry<Double, Integer> entry : entrySet) 
        {
            if(entry.getValue() > frequency)
            {
                element = entry.getKey();                 
                frequency = entry.getValue();
            }
        }         
        //Printing the most frequent element in array and its frequency         
        if(frequency > 1)
        {           
            System.out.println("The most frequent element : "+element);             
            System.out.println("Its frequency : "+frequency);             
            System.out.println("========================");
        }
        else
        {           
            System.out.println("No frequent element. All elements are unique.");             
            System.out.println("=========================");
        }
		return element;
	}
	
}
