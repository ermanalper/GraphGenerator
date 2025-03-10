import java.util.Random;

public class Graph {
    private Node[] nodeArr;
    private int nodeCount;



    public Graph() {
        //initializes an empty NodeArray
        this.nodeArr = new Node[26];
        nodeCount = 0;
    }
    public Graph(Graph nodeArr) {
        //clones nodeArr and initializes a new one
        Node[] newArray = new Node[26];
        for (int i = 0; i < nodeArr.nodeCount(); i++) {
            newArray[i] = nodeArr.getNode(i);
        }
        this.nodeArr = newArray;
        nodeCount = nodeArr.nodeCount;
    }
    public Graph(int[] degreeSequence, char firstNodeName) {
        //creates a new non-empty NodeArray with Nodes that satisfy the degree sequence.
        //names the nodes
        //takes the name of the first node(firstNodeName), and builds up on it
        //eg: if firstNodeName = 'A', then nodes A, B, C, .... might occur

        Node[] nodeArr = new Node[26];
        for (int i = 0; i < degreeSequence.length; i++) {
            nodeArr[i] = new Node((char) (firstNodeName + i), degreeSequence[i]);
        }
        this.nodeArr = nodeArr;
        this.nodeCount = degreeSequence.length;
    }

    public void add(Node node) {
        this.nodeArr[nodeCount] = node;
        nodeCount++;
    }

    public void removeNode(char nodeName) {
        Node[] newArr = new Node[26];
        int placeIx = 0;
        int getIx = 0;
        while (getIx < 26) {
            if (this.nodeArr[getIx].getName() != nodeName) {
                newArr[placeIx++] = this.nodeArr[getIx];
            }
            getIx++;
        }
        this.nodeArr = newArr;
        nodeCount--;
    }

    public void removeNode(Node node) {
        removeNode(node.getName());
    }

    public void removeNode(int ix) {

    }


    public Node getNode(int position) {
        return this.nodeArr[position];
    }

    public int nodeCount() {
        return this.nodeArr.length;
    }

    public boolean containsNode(Node node) {
        char nodeName = node.getName();
        for (Node n : this.nodeArr) {
            if (n.getName() == nodeName) {
                return true;
            }
        }
        return false;
    }
    public boolean containsNode(char nodeName) {
        for (int i = 0; i < this.nodeArr.length; i++) {
            if (this.nodeArr[i].getName() == nodeName) {
                return true;
            }
        }
        return false;
    }

    public static void randomlyConnectNodes(Graph graph) {
        //Takes nodes (not connected yet, Node.connectedNodes is empty) and connects them randomly (adds each other to Nodes.connectedNodes)
        Random random = new Random();
        for (int i = 0; i < graph.nodeCount(); i++) {
            Node currNode = graph.getNode(i);

            //find available nodes
            Graph availableNodes = new Graph();
            for (int j = 0; j < graph.nodeCount(); j++) {
                Node candidate = graph.getNode(j);
                if (j != i && candidate.available() && !currNode.isConnectedTo(candidate)) {
                    availableNodes.add(candidate);
                }
            }


            while (currNode.available()) {

                //if we had connected 2 nodes that must not be connected, reset the whole process and rerun the function
                if(availableNodes.nodeCount() == 0) {
                    //unconnect all nodes connected so far
                    for (int n = 0; n < graph.nodeCount(); n++) {
                        graph.getNode(n).connectedNodes = new Graph();
                    }
                    randomlyConnectNodes(graph);
                } else {
                    int connectToIx = random.nextInt(availableNodes.nodeCount());
                    Node targetNode = availableNodes.getNode(connectToIx);

                    currNode.connect(targetNode);
                    targetNode.connect(currNode);
                    availableNodes.removeNode(connectToIx);
                }

            }

        }
    }

    public static Graph getIsolatedNodes(Graph graph) {
        Graph iso = new Graph();
        for (int i = 0; i < graph.nodeCount(); i++) {
            if (graph.getNode(i).getDegree() == 0) {
                iso.add(graph.getNode(i));
            }
        }
        return iso;
    }

    public static boolean isCompleteGraph(int[][] transitiveClosureMatrix) {
        int row = transitiveClosureMatrix.length;
        int col = transitiveClosureMatrix[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i != j && transitiveClosureMatrix[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
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
        for (int i = 0; i < neighbours.nodeCount(); i++) {
            //foreach neighbour, visit neighbour
            if (unvisitedNodes.containsNode(neighbours.getNode(i))) {
                visit(neighbours.getNode(i), unvisitedNodes);
            }

        }
    }

    public static boolean isConnected(Graph nodes) {
        Graph clonedArray = new Graph(nodes);
        Node startingNode = nodes.getNode(0);

        visit(startingNode, clonedArray);

        return clonedArray.nodeCount() == 0;


    }

    public static int[][] buildRelationMatrix(Graph graph)    {
        //takes nodes, but with non-empty Nodes.connectedNodes, builds a relation matrix using this list
        // YOU MUST USE THIS AFTER CONNECTING THE NODES WITH DegreeOperation.randomlyConnectNodes(nodes);
        int n = graph.nodeCount();
        int[][] o = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
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
            allCoords[i] = new int[]{(int)(i / 10), i % 10};
        }

        //choose a corrdinate that had not been chosen before
        for (int n = 0; n < nodeArr.length; n++) {
            int coorIx = -1;
            do {
                coorIx = random.nextInt(70);
            } while(allCoords[coorIx][0] == -1);
            int x = allCoords[coorIx][0];
            int y  = allCoords[coorIx][1];
            nodeArr[n].setCoordinate(new Coordinate(x, y));

            //mark that coordinate as chosen by another node
            allCoords[coorIx] = new int[] {-1, -1};

        }

    }








}
