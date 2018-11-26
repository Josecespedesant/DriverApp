package com.tec.graph;
/**
 * 
 * @author David Azofeifa
 * @author Daniel Sing
 * @author Manuel Bojorge
 * @author Jose Antonio Cespedes Downing
 * 
 * Clase Vertex (Vertice)
 */
public class Vertex {
	final private String id;
	final private String name;
	final private double lat;
	final private double lon;

	/**
	 * Constructor de la clase vertice
	 * @param id
	 * @param name
	 * @param lat
	 * @param lon
	 */
	public Vertex(String id, String name, double lat, double lon) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Vertex other = (Vertex) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}