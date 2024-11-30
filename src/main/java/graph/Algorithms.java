package graph;

import graph.matrix.MatrixGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.BinaryOperator;

/**
 *
 * @author DEI-ISEP
 *
 */
public class Algorithms {

    /** Performs breadth-first search of a Graph starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    public static <V, E> LinkedList<V> BreadthFirstSearch(Graph<V, E> g, V vert) {

        if (!g.validVertex(vert)) return null;
        LinkedList<V> qBFS = new LinkedList<>();
        LinkedList<V> qAux = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];
        qBFS.add(vert);
        qAux.add(vert);
        visited[g.key(vert)] = true;
        while (!qAux.isEmpty()) {
            V vOrig = qAux.remove();
            for (V vAdj : g.adjVertices(vOrig)) {
                if (!visited[g.key(vAdj)]) {
                    qBFS.add(vAdj);
                    qAux.add(vAdj);
                    visited[g.key(vAdj)] = true;
                }
            }
        }
        return qBFS;
    }

    /** Performs depth-first search starting in a vertex
     *
     * @param g Graph instance
     * @param vOrig vertex of graph g that will be the source of the search
     * @param visited set of previously visited vertices
     * @param qdfs return LinkedList with vertices of depth-first search
     */
    private static <V, E> void DepthFirstSearch(Graph<V, E> g, V vOrig, boolean[] visited, LinkedList<V> qdfs) {
        if (!g.validVertex(vOrig)) return;
        qdfs.add(vOrig);
        visited[g.key(vOrig)] = true;
        for (V vAdj : g.adjVertices(vOrig)) {
            if (!visited[g.key(vAdj)]) {
                DepthFirstSearch(g, vAdj, visited, qdfs);
            }
        }
    }

    /** Performs depth-first search starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex of graph g that will be the source of the search

     * @return a LinkedList with the vertices of depth-first search
     */
    public static <V, E> LinkedList<V> DepthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert)) return null;
        LinkedList<V> qdfs = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];
        DepthFirstSearch(g, vert, visited, qdfs);
        return qdfs;
    }

    /** Returns all paths from vOrig to vDest
     *
     * @param g       Graph instance
     * @param vOrig   Vertex that will be the source of the path
     * @param vDest   Vertex that will be the end of the path
     * @param visited set of discovered vertices
     * @param path    stack with vertices of the current path (the path is in reverse order)
     * @param paths   ArrayList with all the paths (in correct order)
     */
    private static <V, E> void allPaths(Graph<V, E> g, V vOrig, V vDest, boolean[] visited,
                                        LinkedList<V> path, ArrayList<LinkedList<V>> paths) {

        path.add(vOrig);
        visited[g.key(vOrig)] = true;
        for (V vAdj : g.adjVertices(vOrig)) {
            if (vAdj.equals(vDest)) {
                path.add(vDest);
                paths.add(new LinkedList<>(path));
                path.removeLast();
            } else if (!visited[g.key(vAdj)]) {
                allPaths(g, vAdj, vDest, visited, path, paths);
            }
        }
    }

    /** Returns all paths from vOrig to vDest
     *
     * @param g     Graph instance
     * @param vOrig information of the Vertex origin
     * @param vDest information of the Vertex destination
     * @return paths ArrayList with all paths from vOrig to vDest
     */
    public static <V, E> ArrayList<LinkedList<V>> allPaths(Graph<V, E> g, V vOrig, V vDest) {

        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) return null;
        ArrayList<LinkedList<V>> paths = new ArrayList<>();
        boolean[] visited = new boolean[g.numVertices()];
        LinkedList<V> path = new LinkedList<>();
        allPaths(g, vOrig, vDest, visited, path, paths);
        return paths;
    }

    /**
     * Computes shortest-path distance from a source vertex to all reachable
     * vertices of a graph g with non-negative edge weights
     * This implementation uses Dijkstra's algorithm
     *
     * @param g        Graph instance
     * @param vOrig    Vertex that will be the source of the path
     * @param visited  set of previously visited vertices
     * @param pathKeys minimum path vertices keys
     * @param dist     minimum distances
     */
    private static <V, E> void shortestPathDijkstra(Graph<V, E> g, V vOrig,
                                                    Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                                    boolean[] visited, V[] pathKeys, E[] dist) {

        // Check if the starting vertex is valid
        if (!g.validVertex(vOrig)) return;

        // Initialize visited, pathKeys, and dist arrays
        Arrays.fill(visited, false);
        Arrays.fill(pathKeys, null);
        Arrays.fill(dist, null);

        // Get the key (index) for the origin vertex
        int vOrigKey = g.key(vOrig);
        dist[vOrigKey] = zero; // Distance to the origin is zero

        // Process the graph
        while (vOrigKey != -1) {
            visited[vOrigKey] = true;

            // Iterate through adjacent vertices of the current vertex
            for (V vAdj : g.adjVertices(vOrig)) {
                int vAdjKey = g.key(vAdj);
                if (!visited[vAdjKey]) {
                    // Get the weight of the edge
                    E edgeWeight = g.edge(vOrig, vAdj).getWeight();
                    E newDist = sum.apply(dist[vOrigKey], edgeWeight);

                    // Update distance and path if a shorter path is found
                    if (dist[vAdjKey] == null || ce.compare(newDist, dist[vAdjKey]) < 0) {
                        dist[vAdjKey] = newDist;
                        pathKeys[vAdjKey] = vOrig;
                    }
                }
            }

            // Find the next vertex with the smallest distance
            E minDist = null;
            vOrigKey = -1; // Reset to find the next vertex
            for (int i = 0; i < g.numVertices(); i++) {
                if (!visited[i] && dist[i] != null && (minDist == null || ce.compare(dist[i], minDist) < 0)) {
                    minDist = dist[i];
                    vOrigKey = i;
                }
            }

            // Update current vertex if a valid one is found
            if (vOrigKey != -1) {
                vOrig = g.vertex(vOrigKey); // Get vertex from the key
            }
        }
    }



    /** Shortest-path between two vertices
     *
     * @param g graph
     * @param vOrig origin vertex
     * @param vDest destination vertex
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @param zero neutral element of the sum in elements of type E
     * @param shortPath returns the vertices which make the shortest path
     * @return if vertices exist in the graph and are connected, true, false otherwise
     */
    public static <V, E> E shortestPath(Graph<V, E> g, V vOrig, V vDest,
                                        Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                        LinkedList<V> shortPath) {

        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) return null;
        if (vOrig.equals(vDest)) {
            if (shortPath != null) {
                shortPath.add(vOrig);
            }
            return zero;
        }
        int nVerts = g.numVertices();
        boolean[] visited = new boolean[nVerts];
        V[] pathKeys = (V[]) new Object[nVerts];
        E[] dist = (E[]) new Object[nVerts];
        Arrays.fill(dist, null);
        dist[g.key(vOrig)] = zero;
        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);
        if (dist[g.key(vDest)] == null) return null;
        if (shortPath != null) {
            shortPath.clear();
            getPath(g, vOrig, vDest, pathKeys, shortPath);
        }
        return dist[g.key(vDest)];
    }





    /** Shortest-path between a vertex and all other vertices
     *
     * @param g graph
     * @param vOrig start vertex
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @param zero neutral element of the sum in elements of type E
     * @param paths returns all the minimum paths
     * @param dists returns the corresponding minimum distances
     * @return if vOrig exists in the graph true, false otherwise
     */
    public static <V, E> boolean shortestPaths(Graph<V, E> g, V vOrig,
                                               Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                               ArrayList<LinkedList<V>> paths, ArrayList<E> dists) {

        if (!g.validVertex(vOrig)) return false;
        int nVerts = g.numVertices();
        boolean[] visited = new boolean[nVerts];
        V[] pathKeys = (V[]) new Object[nVerts];
        E[] dist = (E[]) new Object[nVerts];
        Arrays.fill(dist, null);
        dist[g.key(vOrig)] = zero;
        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        for (int i = 0; i < nVerts; i++) {
            LinkedList<V> path = new LinkedList<>();
            if (i == g.key(vOrig)) {
                path.add(vOrig);
            } else {
                getPath(g, vOrig, g.vertex(i), pathKeys, path);
            }
            if (paths.size() > i) {
                paths.set(i, path);
                dists.set(i, dist[i]);
            } else {
                paths.add(path);
                dists.add(dist[i]);
            }
        }
        return true;
    }


    /**
     * Extracts from pathKeys the minimum path between voInf and vdInf
     * The path is constructed from the end to the beginning
     *
     * @param g        Graph instance
     * @param vOrig    information of the Vertex origin
     * @param vDest    information of the Vertex destination
     * @param pathKeys minimum path vertices keys
     * @param shortPath     stack with the minimum path (correct order)
     */
    private static <V, E> void getPath(Graph<V, E> g, V vOrig, V vDest, V[] pathKeys, LinkedList<V> shortPath) {
        LinkedList<V> stack = new LinkedList<>();
        V v = vDest;
        while (v != null && !v.equals(vOrig)) {
            stack.push(v);
            v = pathKeys[g.key(v)];
        }
        if (v != null) {
            stack.push(vOrig);
        }
        while (!stack.isEmpty()) {
            shortPath.add(stack.pop());
        }
    }


    /** Calculates the minimum distance graph using Floyd-Warshall
     *
     * @param g initial graph
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @return the minimum distance graph
     */
    public static <V, E> MatrixGraph<V, E> minDistGraph(Graph<V, E> g, Comparator<E> ce, BinaryOperator<E> sum) {
        if (g == null) return null;
        if (g.numVertices() == 0) return new MatrixGraph<>(g.isDirected());

        // Create a new graph for the result
        MatrixGraph<V, E> minDistGraph = new MatrixGraph<>(g.isDirected());

        // Add all vertices to the result graph
        for (V v : g.vertices()) {
            minDistGraph.insertVertex(v);
        }

        // Initialize edges in the result graph
        for (V vOrig : g.vertices()) {
            for (V vDest : g.vertices()) {
                if (vOrig.equals(vDest)) {
                    minDistGraph.insertEdge(vOrig, vDest, (E) Integer.valueOf(0)); // Set distance to self as zero
                } else if (g.edge(vOrig, vDest) != null) {
                    minDistGraph.insertEdge(vOrig, vDest, g.edge(vOrig, vDest).getWeight());
                }
            }
        }

        // Floyd-Warshall Algorithm
        for (V k : g.vertices()) {
            for (V i : g.vertices()) {
                for (V j : g.vertices()) {
                    if (i.equals(j) || k.equals(i) || k.equals(j)) continue;

                    // Distances for i->k, k->j, and i->j
                    E distIK = minDistGraph.edge(i, k) != null ? minDistGraph.edge(i, k).getWeight() : null;
                    E distKJ = minDistGraph.edge(k, j) != null ? minDistGraph.edge(k, j).getWeight() : null;
                    E distIJ = minDistGraph.edge(i, j) != null ? minDistGraph.edge(i, j).getWeight() : null;

                    // Update path i->j if a shorter path via k exists
                    if (distIK != null && distKJ != null) {
                        E newDist = sum.apply(distIK, distKJ);
                        if (distIJ == null || ce.compare(newDist, distIJ) < 0) {
                            minDistGraph.insertEdge(i, j, newDist);
                        }
                    }
                }
            }
        }

        return minDistGraph;
    }

}