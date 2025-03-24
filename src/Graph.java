import com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane;
import enigma.console.Console;

import java.util.Random;

public class Graph {
    public static final int DRAWING_MODE_0 = 0;
    public static final int DRAWING_MODE_1 = 1;
    public static final int DRAWING_MODE_2 = 2;






    private Node[] nodeArr;
    private Edge[] edgeArr;
    private int nodeCount;



    public Graph() {
        //initializes an empty NodeArray
        this.nodeArr = new Node[26];
        nodeCount = 0;
    }

    public Graph(Graph nodeArr) {
        //clones nodeArr and initializes a new one
        Node[] newArray = new Node[26];
        for (int i = 0; i < 26; i++) {
            if (nodeArr.getNode(i) == null) continue;
            char nodeName = nodeArr.getNode(i).getName();
            newArray[nodeName - 'A'] = nodeArr.getNode(i);
        }
        this.nodeArr = newArray;
        this.nodeCount = nodeArr.nodeCount();
    }
    public Graph(int[] degreeSequence, char firstNodeName) {
        //creates a new non-empty NodeArray with Nodes that satisfy the degree sequence.
        //names the nodes
        //takes the name of the first node(firstNodeName), and builds up on it
        //eg: if firstNodeName = 'A', then nodes A, B, C, .... might occur

        Node[] nodeArr = new Node[26];
        for (int i = 0; i < degreeSequence.length; i++) {
            char nodeName = (char)(firstNodeName + i);
            nodeArr[nodeName - 'A'] = new Node(nodeName, degreeSequence[i]);
        }
        this.nodeArr = nodeArr;
        this.nodeCount = degreeSequence.length;
    }




    public void addNode(Node node) {
        this.nodeArr[node.getName() - 'A'] = node;
        nodeCount++;
    }
    public void setEdgeArray(Edge[] edgeArr) {
        this.edgeArr = edgeArr;
    }
    public Edge[] getEdgeArray() {
        return this.edgeArr;
    }

    public void removeNode(char nodeName) {
        this.nodeArr[nodeName - 'A'] = null;
        nodeCount--;
    }

    public void removeNode(Node node) {
        removeNode(node.getName());
    }

    public void removeNode(int ix) {
        char nodeName = nodeArr[ix].getName();
        removeNode(nodeName);
    }


    public Node getNode(int position) {
        if (this.nodeArr[position] == null) {
            return null;
        }
        return this.nodeArr[position];
    }

    public int nodeCount() {
        return nodeCount;
    }

    public boolean containsNode(Node node) {
        char nodeName = node.getName();
        return this.nodeArr[nodeName - 'A'] != null;
    }
    public boolean containsNode(char nodeName) {
        return this.nodeArr[nodeName - 'A'] != null;
    }

    public Edge[] randomlyConnectNodes() {
        //calculate how many edges there should be
        int degSum = 0;
        for (int i = 0; i < 26; i++) {
            if (nodeArr[i] == null) continue;
            degSum += nodeArr[i].getDegree();
        }
        int edgeCount = degSum / 2;
        Edge[] o = new Edge[edgeCount];
        int edgeIx = 0;
        //Takes nodes (not connected yet, Node.connectedNodes is empty) and connects them randomly (adds each other to Nodes.connectedNodes)
        Random random = new Random();
        for (int i = 0; i < 26; i++) {
            if (this.nodeArr[i] == null) continue;
            Node currNode = this.nodeArr[i];

            //find available nodes
            Graph availableNodes = new Graph();
            for (int j = 0; j < 26; j++) {
                if (this.nodeArr[j] == null) continue;
                Node candidate = this.nodeArr[j];
                if (j != i && candidate.available() && !currNode.isConnectedTo(candidate)) {
                    availableNodes.addNode(candidate);
                }
            }


            while (currNode.available()) {

                //if we had connected 2 nodes that must not be connected, reset the whole process and rerun the function
                if(availableNodes.nodeCount() == 0) {
                    //unconnect all nodes connected so far
                    for (int n = 0; n < 26; n++) {
                        if (getNode(n) == null) continue;
                        getNode(n).connectedNodes = new Graph();
                    }
                    return this.randomlyConnectNodes();
                } else {
                    int connectTo = random.nextInt(availableNodes.nodeCount()) + 1;
                    int pointer = -1;
                    int counter = 0;
                    while (counter < connectTo && pointer < 25) {
                        pointer++;
                        if (availableNodes.getNode(pointer) != null) {
                            counter++;
                        }

                    }

                    Node targetNode = availableNodes.getNode(pointer);

                    currNode.connect(targetNode);
                    targetNode.connect(currNode);
                    o[edgeIx++] = new Edge(currNode, targetNode);
                    availableNodes.removeNode(pointer);
                }

            }

        }
        return o;
    }

    public Graph getIsolatedNodes() {
        Graph iso = new Graph();
        for (int i = 0; i < 26; i++) {
            if (getNode(i) != null && getNode(i).getDegree() == 0) {
                iso.addNode(getNode(i));
            }
        }
        return iso;
    }

    public boolean isCompleteGraph() {
        int degSum = 0;
        for (int n = 0; n < 26; n++) {
            if (this.nodeArr[n] == null) continue;
            Node node = this.nodeArr[n];
            degSum += node.getDegree();
        }
        return (this.nodeCount() * (this.nodeCount() - 1)) == degSum;
    }

    public static int[][] multiplyRelationMatrix(int[][] relationMatrixN, int[][] relationMatrix) {
        // relationMatrixN: Rn    ;    relationMatrix: R1
        //returns : R(n + 1)
        int n = relationMatrix.length;
        int[][] o = new int[n][n];
        for (int i= 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //calculate relation
                boolean flag = false;
                for (int k = 0; k < n; k++) {
                    if (relationMatrix[i][j] == 1 || (relationMatrixN[i][k] == 1 && relationMatrix[k][j] == 1)) {
                        flag = true;
                    }
                }
                if (flag) {
                    o[i][j] = 1;
                }
            }

        }
        return o;
    }
    public static int[][] floydWarshall(int[][] relationMatrix) {
		int n = relationMatrix.length;
		int[][] result = new int[n][n];

		// copy the relationMatrix
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (relationMatrix[i][j] == 0 && i != j) {
					result[i][j] = Integer.MAX_VALUE; // if there is no connection consider as infinite
				} else {
					result[i][j] = relationMatrix[i][j]; // if there is connection, consider as 1
				}
			}
		}

		// Floyd-Warshall
		for (int k = 0; k < n; k++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					// if i -> k and k -> j exist, update i -> j
					if (result[i][k] != Integer.MAX_VALUE && result[k][j] != Integer.MAX_VALUE) {
						// find the shortest way: current way versus new way
						result[i][j] = Math.min(result[i][j], result[i][k] + result[k][j]);
					}

		// in project file they represent the nodes connection with themselves as 2
		// with using floyd,warshall itreturns as 0, so i changed it with 2
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (i == j)
					result[i][j] = 2;

		return result;
	}


    private static void visit(Node visitedNode, Graph unvisitedNodes) {
        //this is a helper function used in isConnected(NodeArray nodes)

        if (unvisitedNodes.containsNode(visitedNode)) {
            unvisitedNodes.removeNode(visitedNode);
        }
        Graph neighbours = visitedNode.connectedNodes;
        for (int i = 0; i < 26; i++) {
            if (neighbours.getNode(i) == null) continue;
            //foreach neighbour, visit neighbour
            if (unvisitedNodes.containsNode(neighbours.getNode(i))) {
                visit(neighbours.getNode(i), unvisitedNodes);
            }

        }
    }

    public boolean isConnected() {
        Graph clonedArray = new Graph(this);
        Node startingNode = clonedArray.getNode(0);

        visit(startingNode, clonedArray);

        return clonedArray.nodeCount() == 0;


    }

    public int[][] buildRelationMatrix()    {
        //takes nodes, but with non-empty Nodes.connectedNodes, builds a relation matrix using this list
        // YOU MUST USE THIS AFTER CONNECTING THE NODES WITH DegreeOperation.randomlyConnectNodes(nodes);
        Graph graph = this;
        int n = graph.nodeCount();
        int[][] o = new int[n][n];
        for (int i = 0; i < 26; i++) {
            if (this.nodeArr[i] == null) continue;
            for (int j = 0; j < 26; j++) {
                if (this.nodeArr[j] == null) continue;

                if (graph.getNode(i).isConnectedTo(graph.getNode(j))) {
                    o[i][j] = 1;
                    // o[j][i] = 1;
                }
            }
        }
        return o;
    }

    public void randomlyPlaceNodes() {
        Random random = new Random();
        int[][] allCoords = new int[70][2];
        //fill all coordinates
        for (int i = 0; i < 70; i++) {
            allCoords[i] = new int[]{i % 10, (int)i / 10};
        }

        //choose a coordinate that had not been chosen before
        for (int n = 0; n < 26; n++) {
            if (nodeArr[n] == null) continue;
            int coorIx = -1;
            do {
                coorIx = random.nextInt(70);
            } while(allCoords[coorIx][0] == -1);
            int x = allCoords[coorIx][0];
            int y  = allCoords[coorIx][1];
            nodeArr[n].setRelativeCoordinate(new Coordinate(x, y));

            //mark that coordinate as chosen by another node
            allCoords[coorIx] = new int[] {-1, -1};

        }

    }
    public void printNodeNames() {
        Console console = Main.console;
        for (int i = 0; i < 26; i++) {
            if (nodeArr[i] == null) continue;
            Node node = nodeArr[i];
            Coordinate c = node.getRelativeCoordinate().calculateAbsoluteCoordinate(0,0, Coordinate.MAIN_GRAPH);
            console.getTextWindow().setCursorPosition(c.getX(), c.getY());
            console.getTextWindow().output(node.getName());
        }
    }


    public String getAllC3s() {
        int edgeCount = this.edgeArr.length;
        String c3Piles = "";

        //this n^3 iteration will reach all edge combinations of 3
        for (int i = 0; i < edgeCount - 2; i++) {
            for (int j = i + 1; j < edgeCount - 1; j++) {
                for (int k = j + 1; k < edgeCount; k++) {
                    String visitedNodeNames = "";
                    Edge e1 = edgeArr[i];
                    Edge e2 = edgeArr[j];
                    Edge e3 = edgeArr[k];
                    Node[] e1Nodes = e1.getNodes();
                    Node[] e2Nodes = e2.getNodes();
                    Node[] e3Nodes = e3.getNodes();
                    visitedNodeNames += String.valueOf(e1Nodes[0].getName()) + e1Nodes[1].getName();
                    if (!visitedNodeNames.contains(String.valueOf(e2Nodes[0].getName()))) {
                        visitedNodeNames += String.valueOf(e2Nodes[0].getName());
                    }
                    if (!visitedNodeNames.contains(String.valueOf(e2Nodes[1].getName()))) {
                        visitedNodeNames += String.valueOf(e2Nodes[1].getName());
                    }
                    if (!visitedNodeNames.contains(String.valueOf(e3Nodes[0].getName()))) {
                        visitedNodeNames += String.valueOf(e3Nodes[0].getName());
                    }
                    if (!visitedNodeNames.contains(String.valueOf(e3Nodes[1].getName()))) {
                        visitedNodeNames += String.valueOf(e3Nodes[1].getName());
                    }
                    if (visitedNodeNames.length() == 3) {
                        c3Piles += String.join(", ", visitedNodeNames) + " - ";
                    }

                }
            }
        }
        if (c3Piles.length() > 3) {
            c3Piles = c3Piles.substring(0, c3Piles.length() - 3);
        }
        /// if c3Piles.length() == 0  -> no C3
        return c3Piles;
    }



}
