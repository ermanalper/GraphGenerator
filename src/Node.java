public class Node {
    private final char name;
    private final int degree;
    private Coordinate coordinate;

    //connectedNodes : if any node has an edge with this node, it will appear in connectedNodes
    public Graph connectedNodes;





    public Node (char name, int degree) {
        this.name = name;
        //degree : Degree the node will have WHEN ALL NODES ARE CONNECTED
        this.degree = degree;
        //connectedNodes will be initialized as an empty array, then nodes will be added at DegreeOperation.randomlyConnectNodes(NodeArray nodes)
        this.connectedNodes = new Graph();
    }
    public char getName() {
        return name;
    }
    public int getDegree() {
        return degree;
    }

    public void connect(Node node) {
        this.connectedNodes.addNode(node);

    }

    public boolean isConnectedTo(Node node) {
        return connectedNodes.containsNode(node);
    }


    public boolean available() {
        //returns false if it is saturated, true otherwise
        return this.connectedNodes.nodeCount() < degree;
    }

    public void setRelativeCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    public Coordinate getRelativeCoordinate() {
        return this.coordinate;
    }

}
