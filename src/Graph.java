import enigma.console.Console;

import java.util.Random;

public class Graph {
    public static final int DRAWING_MODE_0 = 0;
    public static final int DRAWING_MODE_1 = 1;
    public static final int DRAWING_MODE_2 = 2;
    public static final boolean MAIN_GRAPH = true;
    public static final boolean SECONDARY_GRAPH = false;









    public Node[] nodeArr;
    private Edge[] edgeArr;
    private int count;



    public Graph() {
        //initializes an empty NodeArray
        this.nodeArr = new Node[26];
        this.count = 0;
    }

    public Graph(Graph nodeArr) {
        //clones nodeArr and initializes a new one
        //this.nodeCount = nodeArr.nodeCount();
        /// DOES NOT CLONE NODES, JUST REFERENCES THE EXISTING ONES

        Node[] newArray = new Node[26];
        for (int i = 0; i < 26; i++) {
            if (nodeArr.getNode(i) == null) continue;
            char nodeName = nodeArr.getNode(i).getName();
            newArray[nodeName - 'A'] = nodeArr.getNode(i);
        }
        this.nodeArr = newArray;
        this.count = nodeArr.nodeCount();

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
        this.count = degreeSequence.length;
    }

    /// the constructor below COMPLETELY clones the masterGraph
    public Graph(Graph masterGraph, char initialNodeName) {

        int[][] masterMatrix = masterGraph.buildRelationMatrix();
        int nCount = masterMatrix.length;
        Node[] newNodes = new Node[nCount];

        //initialize all new nodes
        int newNodeIx = 0;
        for (int i = 0; i < 26; i++) {
            if (masterGraph.getNode(i) == null) continue;
            Node masterNode = masterGraph.getNode(i);
            Node newNode = new Node((char)(initialNodeName + newNodeIx), masterNode.getDegree());
            newNode.setRelativeCoordinate(masterNode.getRelativeCoordinate());
            newNodes[newNodeIx++] = newNode;
        }


        // connect all nodes and construct new edges according to the relation matrix
        Edge[] newEdges = new Edge[masterGraph.getEdgeArray().length];
        int edgeIx = 0;
        for (int i = 0; i < nCount; i++) {
            Node initialNode = newNodes[i];
            for (int j = i; j < nCount; j++) {
                Node terminalNode = newNodes[j];
                if (masterMatrix[i][j] == 1) {
                    initialNode.connect(terminalNode);
                    newEdges[edgeIx++] = new Edge(initialNode, terminalNode);
                }
            }
        }

        // change node array format to match the required format
        Node[] oNodes = new Node[26];
        for (int i = 0; i < newNodes.length; i++) {
            Node node = newNodes[i];
            oNodes[node.getName() - 'A'] = node;
        }
        this.nodeArr = oNodes;
        this.edgeArr = newEdges;
        this.count = nCount;

    }




    public void addNode(Node node) {
        if(this.nodeArr[node.getName() - 'A'] == null) {
            this.nodeArr[node.getName() - 'A'] = node;
            this.count++;
        }

    }
    public void setEdgeArray(Edge[] edgeArr) {
        this.edgeArr = edgeArr;
    }
    public Edge[] getEdgeArray() {
        return this.edgeArr;
    }

    public void removeNode(char nodeName) {
        if (this.nodeArr[nodeName - 'A'] != null) {
            this.nodeArr[nodeName - 'A'] = null;
            this.count--;
        }

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
        return this.count;
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
        //Takes nodes (not connected yet, for each Node node, node.connectedNodes is empty) and connects them randomly (adds each other to Node.connectedNodes)
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
                    //disconnect all nodes connected so far
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
        int row = 0;
        int col = 0;
        int n = graph.nodeCount();
        int[][] o = new int[n][n];
        for (int i = 0; i < 26; i++) {
            if (this.nodeArr[i] == null) continue;

            for (int j = 0; j < 26; j++) {
                if (this.nodeArr[j] == null) continue;

                if (graph.getNode(i).isConnectedTo(graph.getNode(j))) {
                    o[row][col] = 1;
                    // o[j][i] = 1;
                }
                col++;
            }
            row++;
            col = 0;

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
    public void printNodeNames(boolean whichGraph, int mainGraphNodeCount, int secondaryGraphNodeCount) {
        /// when printing the main graph, mainGraphNodeCount and secondaryGraphNodeCount does not matter
        Console console = Main.console;
        for (int i = 0; i < 26; i++) {
            if (nodeArr[i] == null) continue;
            Node node = nodeArr[i];
            Coordinate c = node.getRelativeCoordinate().calculateAbsoluteCoordinate(whichGraph, mainGraphNodeCount, secondaryGraphNodeCount);
            console.getTextWindow().setCursorPosition(c.getX(), c.getY());
            console.getTextWindow().output(node.getName());
        }
    }


    public String getAllC3s() {
        int edgeCount = this.edgeArr.length;
        String c3Piles = "";
        int threshold = 40;
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
                        if (c3Piles.length() > threshold) {
                            c3Piles += "\n";
                            threshold += 40;
                        }
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


    public Graph[] bipartiteSeparation() { // returns new Graph[] {pos, neg} or null if the graph is not bipartite
        Graph pos = new Graph();
        Graph neg = new Graph();///if a node does not occur in either positives or negatives, this node is not visited;
                                ///but having a node in either of these arrays, DOES NOT mean that we had visited that node
        Graph unvisited = new Graph(this); ///hence, we still need to clone this graph and create an unvisited nodes array, to jump from one connected
                                                                                    /// component to another, if needed
        Queue q = new Queue(1000);


        while(unvisited.nodeCount() > 0) {
            Node nextNode = null;
            /// get the next node
            int nextNodeIx = -1;
            while (nextNode == null) {
                nextNode = unvisited.getNode(++nextNodeIx);
            }
            /// add a starting node from the connected component, if there is no more than 1 connected component in the whole graph, while-loops above will run only once
            q.enqueue(nextNode);
            pos.addNode(nextNode);
            while (!q.isEmpty()) {
                Node currNode = q.dequeue();
                unvisited.removeNode(currNode);

                for (int i = 0; i < 26; i++) {
                    Node neighbour = currNode.connectedNodes.getNode(i);
                    if (neighbour == null) continue;
                    /// determine the current node's sign: pos(+) or neg(-)
                    if (pos.containsNode(currNode)) { // if curr node is labeled +, label its neighbours -
                        neg.addNode(neighbour);
                    }
                    if (neg.containsNode(currNode)) { //if curr node is labeled -, label its neighbours +
                        pos.addNode(neighbour);
                    }

                    if (unvisited.containsNode(neighbour)) {
                        q.enqueue(neighbour);
                    }
                }
            }
        }
        for (int i = 0; i < 26; i++) {
            Node n = pos.getNode(i);
            if (n == null) continue;
            if (neg.containsNode(n)) {
                return null;
            }

        }

        for (int i = 0; i < 26; i++) {
            Node n = neg.getNode(i);
            if (n == null) continue;
            if (pos.containsNode(n)) {
                return null;
            }

        }

        return new Graph[] {pos, neg};







    }

    public boolean isCycle() {
        /// returns true if and only if the whole graph is cycle, does not search its subgraphs
        if (!this.isConnected() || this.nodeCount() < 3) return false;
        for (int i = 0; i < 26; i++) {
            if (this.getNode(i) != null && this.getNode(i).getDegree() != 2) return false; //all degrees must be 2
        }
        return true;
    }

    public Node isStarGraph() {
        if (!isConnected()) return null; // graph must be fully connected

        Node centralNode = null;

        // find the central node (connected to all others)
        for (int i = 0; i < this.nodeCount(); i++) {
            if (nodeArr[i] != null && nodeArr[i].getConnectedNodes().nodeCount() == this.nodeCount() - 1) {
                centralNode = nodeArr[i];
                break;
            }
        }

        if (centralNode == null) return null; // no valid center found

        // verify all other nodes are only connected to the central node
        for (int i = 0; i < 26; i++) {
            Node current = nodeArr[i];
            if (current == null || current == centralNode) continue;

            if (!current.isConnectedTo(centralNode)) return null;

            for (int j = 0; j < this.nodeCount(); j++) {
                Node other = nodeArr[j];
                if (other == null || other == current || other == centralNode) continue;

                if (current.isConnectedTo(other)) {
                    return null; // non-central nodes should not be connected to each other
                }
            }
        }

        return centralNode; // all checks passed, it's a star graph
    }

    public Node wheelCenter() {
        //if wheel graph : returns the center node
        //if not : return null
        /// works on the whole graph, does not include its subgraphs
        if (!this.isConnected() || this.nodeCount() < 4) return null; //wheel graphs must be connected, and have a cycle graph surrounding the center (3 + 1 = 4)
        Node center = null;
        for (int i = 0; i < 26; i++) {
            Node currNode = this.getNode(i);
            if (currNode == null) continue;
            /// all nodes other than the center node should have a degree of 3
            if (currNode.getDegree() != 3) { //if its degree is not 3, it might be the center node
                if (center != null) return null; //another node is already labeled as the center-node-candidate
                else {
                    center = currNode;
                }
            }
        }

        //all the nodes have a degree of 3, it might be a W3 graph with 4 nodes
        /// choose any random node as the center node
        if (center == null) {
            if (this.nodeCount() == 4) {
                int ix = 0;
                while (center == null) {
                    center = this.getNode(ix++);
                }
                return center;
            } else {
                return null; // all the nodes have a degree of 3, but it is not a W3 graph
            }
        }

        if (this.nodeCount() - 1 == center.getDegree())
            return center;
        return null;

    }



    public int[][] getInkMatrix() {
        //this might return different values in back-to-back calls due to the random selection of the starting and the ending node
        Random random = new Random();
        int nodeCount = this.nodeCount();
        int[][] inkedPoints = new int[25][37];

        for (int e = 0; e < this.edgeArr.length; e++) {
            Edge edge = this.edgeArr[e];
            //decide starting and ending points
            int rand = random.nextInt(2) + 1; //returns either 1 or 2
            Coordinate startingPoint = edge.getNodes()[rand / 2].getRelativeCoordinate().calculateAbsoluteCoordinate(MAIN_GRAPH, 0, 0);
            Coordinate endingPoint = edge.getNodes()[rand % 2].getRelativeCoordinate().calculateAbsoluteCoordinate(MAIN_GRAPH, 0, 0);

            int printX = startingPoint.getX();
            int printY = startingPoint.getY();

            while (Math.abs(printX - endingPoint.getX()) > 1 || Math.abs(printY - endingPoint.getY()) > 1) {

                if (printX > endingPoint.getX()) { //if the cursor is on the left
                    printX--;
                } else if (printX < endingPoint.getX()) { //cursor is on the right
                    printX++;
                }
                if (printY > endingPoint.getY()) { //if the cursor is below the ending point
                    printY--;
                } else if (printY < endingPoint.getY()) { //cursor is above
                    printY++;
                }
                /// now we are pointing printY, printX on the inkedPoints matrix
                inkedPoints[printY][printX]++;
            }


        }
        return inkedPoints;
    }

    public static int calculatePenalty(int[][] inkMatrix, Graph graph) {
        int penalty = 0;
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 37; j++) {
                if(inkMatrix[i][j] != 0)
                    penalty += inkMatrix[i][j] - 1;
            }
        }

        for (int n = 0; n < 26; n++) {
            Node node = graph.getNode(n);
            if (node == null) continue;
            int j = node.getRelativeCoordinate().getX() * 4;
            int i = node.getRelativeCoordinate().getY() * 4;
            if (inkMatrix[i][j] != 0)
                penalty += 1000 * (inkMatrix[i][j]);
        }
        return penalty;
    }



    /// functions below are all for finding isomorphism

    private static int[][] buildRelationMatrixWithNodeOrder(Node[] nodes) {
        /// this is a helper function used in findIsomorphicSequenceTo()
        int n = nodes.length;
        int[][] relation = new int[n][n];
        for (int i = 0; i < n; i++) {
            Node initialNode = nodes[i];
            for (int j = 0; j < n; j++) {
                Node terminalNode = nodes[j];
                if (initialNode.isConnectedTo(terminalNode)) {
                    relation[i][j] = 1;
                }

            }
        }
        return relation; //this should return a symmetric matrix for a simple graph
    }

    private boolean isIsomorphismCandidate(Graph secondaryGraph) {
        //this is a helper function used in findIsomorphicSequenceTo()

        /// for two graphs to be isomorphic, their node counts, edge counts and degree sequences must be the same.
        /// this function checks if these specs match and return false if they do not, so we do not have to calculate permutations for
        /// isomorphism and call it "Not Isomorphic" rapidly
        if (this.nodeCount() != secondaryGraph.nodeCount() || this.getEdgeArray().length != secondaryGraph.getEdgeArray().length) {
            return false;
        }
        int[] masterGraphDegrees = new int[this.nodeCount()];
        int[] secondaryGraphDegrees = new int[secondaryGraph.nodeCount()]; //node counts should be the same
        int masterPlaceIx = 0;
        int secondaryPlaceIx = 0;

        //record all nodes' degrees to decide quick on not being isomorphic if their degrees does not match
        for (int i = 0; i < 26; i++) {
            if (this.getNode(i) != null) {
                masterGraphDegrees[masterPlaceIx++] = this.getNode(i).getDegree();
            }
            if (secondaryGraph.getNode(i) != null) {
                secondaryGraphDegrees[secondaryPlaceIx++] = secondaryGraph.getNode(i).getDegree();
            }
        }
        //compare all degrees
        for (int i = 0; i < masterGraphDegrees.length; i++) {
            int searchFor = masterGraphDegrees[i];
            int searchIx = 0;
            boolean flag = false;
            do {
                if(secondaryGraphDegrees[searchIx] == searchFor) {
                    secondaryGraphDegrees[searchIx] = -1; //label as corresponded
                    flag = true;
                }
                searchIx++;
            }while(!flag && searchIx < secondaryGraphDegrees.length);
            if (!flag) return false;
        }
        return true;
    }

    private static boolean isSameMatrix(int[][] m1, int[][] m2) {
        if (m1.length != m2.length || m1[0].length != m2[0].length) return false;
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                if (m1[i][j] != m2[i][j]) return false;
            }
        }
        return true;
    }

    private static int factorial(int num) {
        int o = 1;
        for (int i = 1; i <= num; i++) {
            o *= i;
        }
        return o;
    }

    private static Node[] takeNthPermutation(Node[] nodeSeq, int n) {
        int nodeCount = nodeSeq.length;
        Node[] nThPermutation = new Node[nodeCount];

        int[] ixHolder = new int[nodeCount]; //last ix of ixHolder is always 0
        int remainder = n;
        for (int i = 1; i < nodeCount; i++) {
            int factorial = factorial(nodeCount - i);
            ixHolder[i - 1] = (int) (remainder / factorial);
            remainder -= factorial * ixHolder[i - 1];
        }

        //clone nodeSeq
        Node[] clonedNodeSeq = new Node[nodeSeq.length];
        for(int i = 0; i < nodeSeq.length; i++) {
            clonedNodeSeq[i] = nodeSeq[i];
        }

        /// order the permutation according to the indices in the ixHolder array
        for (int i = 0; i < nodeSeq.length; i++) {
            nThPermutation[i] = clonedNodeSeq[ixHolder[i]];
            //take the node out of the clonedNodeSeq
            clonedNodeSeq[ixHolder[i]] = null;
            Node[] nodeSeqContinue = new Node[clonedNodeSeq.length - 1];
            int placeIx = 0;
            for (int j = 0; j < clonedNodeSeq.length; j++) {
                if (clonedNodeSeq[j] != null) {
                    nodeSeqContinue[placeIx++] = clonedNodeSeq[j];
                }
            }
            clonedNodeSeq = nodeSeqContinue;
        }
        return nThPermutation;



    }

    private static boolean isPossibleOrder(Node[] nodeArr1, Node[] nodeArr2) {
        // they have the same length
        for (int i = 0; i < nodeArr1.length; i++) {
            if (nodeArr1[i].getDegree() != nodeArr2[i].getDegree()) return false;
        }
        return true;
    }

    public Node[] findIsomorphicSequenceTo(Graph candidateGraph) {
        //returns null if not isomorphic
        /// if isomorphic, returns an ordered node array, oNodeArr, with the nth node in the oNodeArr corresponding the nth node in this graph
        //return an ordered node sequence of the candidateGraph's nodes
        if (!this.isIsomorphismCandidate(candidateGraph)) return null;

        int[][] masterMatrix = this.buildRelationMatrix();

        Node[] nodes = new Node[candidateGraph.nodeCount()];
        int placeIx = 0;
        for (int i = 0; i < 26; i++) {
            if(candidateGraph.getNode(i) != null) {
                nodes[placeIx++] = candidateGraph.getNode(i);
            }
        }

        int permutationCount = factorial(nodes.length);
        for (int p = 0; p < permutationCount; p++) {
            //find all permutations P(i), P(0) is the array itself
            Node[] ordered = takeNthPermutation(nodes, p);
            if (!isPossibleOrder(nodes, ordered)) continue; // if the degree sequences does not match, continue without even calculating the matrix
            int[][] orderedMatrix = buildRelationMatrixWithNodeOrder(ordered);
            if (isSameMatrix(masterMatrix, orderedMatrix)) return ordered; ///found
        }
        return null; /// no isomorphism is found

    }




}
