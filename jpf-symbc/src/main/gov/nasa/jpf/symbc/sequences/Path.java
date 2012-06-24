package gov.nasa.jpf.symbc.sequences;

import java.util.Vector;

public class Path {
	public boolean isVisited = false;
	public Vector<State> states = new Vector<State> ();
	public int getDeltaCoverage(){
		return 0;
	}
}
