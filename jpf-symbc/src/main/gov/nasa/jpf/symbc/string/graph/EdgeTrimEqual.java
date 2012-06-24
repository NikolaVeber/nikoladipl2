package gov.nasa.jpf.symbc.string.graph;

import java.util.ArrayList;
import java.util.List;

public class EdgeTrimEqual implements Edge{
	Vertex v1, v2;
	final String name;
	
	public EdgeTrimEqual (String name, Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
		this.name = name;
	}
	
	@Override
	public Vertex getDest() {
		return v2;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Vertex getSource() {
		return v1;
	}

	@Override
	public List<Vertex> getSources() {
		List<Vertex> result = new ArrayList<Vertex>();
		result.add (v1);
		return result;
	}

	@Override
	public boolean isHyper() {
		return false;
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((v1 == null) ? 0 : v1.hashCode());
		result = prime * result + ((v2 == null) ? 0 : v2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdgeTrimEqual other = (EdgeTrimEqual) obj;
		if (v1 == null) {
			if (other.v1 != null)
				return false;
		} else if (!v1.equals(other.v1))
			return false;
		if (v2 == null) {
			if (other.v2 != null)
				return false;
		} else if (!v2.equals(other.v2))
			return false;
		return true;
	}

	public String toString () {
		return v1 + " --> " + v2;
	}

	@Override
	public boolean isDirected() {
		return true;
	}
	
	public void setSource (Vertex v) {
		this.v1 = v;
	}
	public void setDest (Vertex v) {
		this.v2 = v;
	}
	
	@Override
	public boolean allVertecisAreConstant() {
		return v1.isConstant() && v2.isConstant();
	}
}
