package com.tec.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author David Azofeifa
 * @author Daniel Sing
 * @author Manuel Bojorge
 * @author Jose Antonio Cespedes Downing
 * 
 * Clase Graph (Grafo)
 */
public class Graph {
    private List<Vertex> vertexes;
    private List<Edge> edges;

    /**
     * Constructor de la clase grafo
     * @param vertexes
     * @param edges
     */
    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    /**
     * Obtiene los v�rtices del grafo
     * @return
     */
    public List<Vertex> getVertexes() {
        return vertexes;
    }
    
    /**
     * Obtiene las aristas del grafo
     * @return
     */
    public List<Edge> getEdges() {
        return edges;
    }
    
    /**
     * Genera posiciones random a lo largo de Cartago
     * @return
     */
    public static double[] generateRandomPos() {
        double[] resultado = new double[2];

        Random randomlat = new Random();
        Random randomlng = new Random();

        double lat = 9.832350 + (9.880570 - 9.832350) * randomlat.nextDouble();
        double lng = -83.963226 + (-83.893785 - -83.963226) * randomlng.nextDouble();

        resultado[0] = lat;
        resultado[1] = lng;

        return resultado;
    }
    
    /**
     * Genera lugar aleatorios a lo largo de Cartago con peso en aristas
     */
    public void generateThirtyRandomPlaces() {
    	vertexes = new ArrayList<Vertex>();
    	edges = new ArrayList<Edge>();
    	
    	for(int i = 1; i<30;i++) {
    		double[] aux = generateRandomPos();
			Vertex location = new Vertex("Node_"+i, "Node_"+i, aux[0], aux[1]);
			vertexes.add(location);
		}
    	Vertex TEC = new Vertex("TEC", "Node_TEC: " + 30, 9.857191, -83.912284);
    	vertexes.add(TEC);
    	
    	
    	for(int i=0; i<vertexes.size()-1;i++) {
    		Random randint = new Random();
    		addLane("Edge_"+i, i, i+1,randint.nextInt((10-1)+1)+1, vertexes, edges);
    	}
    	
    	int counter = vertexes.size();
    	
    	for(int i=0; i<vertexes.size(); i++) {
    		Random randint = new Random();
    		Random randint2 = new Random();
    		Random randint3 = new Random();
    		addLane("Edge_"+counter, randint.nextInt((29-1)+1)+1, randint2.nextInt((29-1)+1)+1, randint3.nextInt((10-1)+1)+1, vertexes,edges);
    		counter+=1;
    	}
    	
    }
    
    /**
     * Facilita la creaci�n de aristas
     * @param laneId
     * @param sourceLocNo
     * @param destLocNo
     * @param duration
     * @param nodes
     * @param edges
     */
    public static void addLane(String laneId, int sourceLocNo, int destLocNo, int duration, List<Vertex> nodes , List<Edge> edges) {
		Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration );
		edges.add(lane);
	}
    
    
    

}
