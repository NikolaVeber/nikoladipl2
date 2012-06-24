package edu.berkley.operation.freedom;
import java.util.HashMap;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.*;
import org.apache.commons.math.random.UniformRandomGenerator;


public class VariableProbabilityManager {
	
	private HashMap<String, Distribution> probs = new HashMap<String, Distribution>();
	
	private static VariableProbabilityManager instance;
	
	public static void main(String[] args){

		getInstance().putProbability("a", 0, 10);
		getInstance().getProbability("a", -10, 10);
		System.out.println("a: " + getInstance().getProbability("a", -10, 10));
		System.out.println("b: " + getInstance().getProbability("b", -10, 10)*100 + "%");
	}
	
	public void putProbability(String varName, int mean, int variance){
		probs.put(varName, new NormalDistributionImpl(mean, variance));
	}
	
	private float getuniformDist(int x, int y){
		System.out.println("default");
		if(y < x){
			return getuniformDist(y, x);
		}
		else{
			int diff = x - y;
			float totalRange = (float)Integer.MAX_VALUE - (float)Integer.MIN_VALUE;
			return (float)Math.abs(diff) / (float)totalRange;
		}
	}
	
	public static VariableProbabilityManager getInstance(){
		if (instance == null) {
			instance = new VariableProbabilityManager();
		}
		return instance;
	}
	
	public double getProbability(String var, int x, int y){
		double p = 0;
		try {
			
			Distribution d = probs.get(var);
			
			try{
				p = d.cumulativeProbability(x, y);
			}
			catch (NullPointerException e) { // no dist for var, assume uniform
				p = getuniformDist(x, y);
			}
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     // P(T <= -2.656)
		
		return p;
	}
}
