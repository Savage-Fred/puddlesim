package org.fog.utils;

/*
 * Adjacency List in Java
 * using Collections API
 *
 * http://theoryofprogramming.com/adjacency-list-in-java/
 * Authored by,
 * Vamsi Sangam.
 */
 
import java.util.LinkedList;
import java.util.Scanner;
import javafx.util.Pair;
 
public class AdjacencyList {
    private final LinkedList< Pair<Integer, Integer> >[] adjacencyList;
    
    private static String LOG_TAG = "ADJACENCY_LIST";
    
    // Constructor
    public AdjacencyList(int vertices) {
        adjacencyList = (LinkedList< Pair<Integer, Integer> >[]) new LinkedList[vertices];
         
        for (int i = 0; i < adjacencyList.length; ++i) {
            adjacencyList[i] = new LinkedList<>();
        }
    }
    
    // Appends a new Edge to the linked list
    public void addEdge(int startVertex, int endVertex, int weight) {
        adjacencyList[startVertex].add(new Pair<>(endVertex, weight));
    }
     
    // Returns number of vertices
    // Does not change for an object
    public int getNumberOfVertices() {
        return adjacencyList.length;
    }
     
    // Returns number of outward edges from a vertex
    public int getNumberOfEdgesFromVertex(int startVertex) {
        return adjacencyList[startVertex].size();
    }
     
    // Returns a copy of the Linked List of outward edges from a vertex
    public LinkedList< Pair<Integer, Integer> > getEdgesFromVertex(int startVertex) {
        LinkedList< Pair<Integer, Integer> > edgeList
        = (LinkedList< Pair<Integer, Integer> >) new LinkedList(adjacencyList[startVertex]);
         
        return edgeList;
    }
     
    // Prints the Adjacency List
    public void printAdjacencyList() {
        int i = 0;
         
        for (LinkedList< Pair<Integer, Integer> > list : adjacencyList) {
            System.out.print("adjacencyList[" + i + "] -> ");
             
            for (Pair<Integer, Integer> edge : list) {
                System.out.print(edge.getKey() + "(" + edge.getValue() + ")");
            }
             
            ++i;
            System.out.println();
        }
    }
     
    // Removes an edge and returns true if there
    // was any change in the collection, else false
    public boolean removeEdge(int startVertex, Pair<Integer, Integer> edge) {
        return adjacencyList[startVertex].remove(edge);
    }
    
    /**
	 * Returns the path between source and destination.
	 * @author Avi Rynderman
	 * @param sourceId the id of the entity requesting the next node id. 
	 * @param destinationId the id of the entity the module must eventually be sent to.
	 * @return Integer indicating the next node/entity a module should be sent to.
	 * <p><b>-1 if no node.
	 */
	public LinkedList<Integer> pathFindingUsingBFS(int sourceId, int destinationId){
		boolean found = false;
		int nextEntityId = -1;
			
		Logger.debug(LOG_TAG, "Finding path between: "+sourceId+"->"+destinationId);
		
		LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
		LinkedList path = new LinkedList();
		path.add(sourceId);
		
		paths.add(path);
		Logger.debug(LOG_TAG, "Starting at node: "+paths.getLast().getFirst());
		
		while(!found){
			// Iterate over all the edges leading from sourceId
			for(Pair<Integer, Integer> pair : this.getEdgesFromVertex(paths.getLast().getLast())){
				// If we haven't already traversed this edge, add it to the path and return the path to the queue.
				if(!paths.getLast().contains(pair)){
					Logger.debug(LOG_TAG, "Found link between: " + pair.getKey() + "<->" + paths.getLast().getFirst());
					Logger.debug(LOG_TAG, "Adding path: " + paths.getLast().getFirst() + "->" + pair.getKey());
					LinkedList<Integer> newPath = (LinkedList<Integer>) paths.getLast().clone();
					newPath.addFirst(pair.getKey());
					paths.addFirst(newPath);
					// If the node node we just examined was the node we're searching for then we've finished.
					// Check after adding to the list because I want to return the list.
					if(pair.getKey() == destinationId){
						nextEntityId = paths.getFirst().get(paths.getLast().size()-1);
						Logger.debug(LOG_TAG, "Path " + sourceId + "->" + nextEntityId);
						found = true;
						break;
					}
				}
				else
					Logger.debug(LOG_TAG, "No link between "+pair.getKey()+" and "+paths.getLast().getFirst());
			}
			paths.removeLast();
			if(paths.isEmpty())
				found = true;
		}
		Logger.debug(LOG_TAG, "Go from "+sourceId+" to "+nextEntityId+" to get to "+destinationId);
		return paths.getFirst();
	}
}
 
class testGraph
{
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
         
        int vertices = s.nextInt();
        int edges = s.nextInt();
        int u, v, weight;
         
        AdjacencyList adjacencyList = new AdjacencyList(vertices);
         
        int i = 0;
         
        while (i < edges) {
            u = s.nextInt() - 1;
            v = s.nextInt() - 1;
            weight= s.nextInt();
             
            adjacencyList.addEdge(u, v, weight);
            ++i;
        }
         
        adjacencyList.printAdjacencyList();
        adjacencyList.removeEdge(0, new Pair<>(1, 1));
        adjacencyList.printAdjacencyList();
    }
}