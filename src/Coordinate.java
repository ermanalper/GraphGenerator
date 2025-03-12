public class Coordinate {
    private final int x;
    private final int y;
    public static final boolean MAIN_GRAPH = false;
    public static final boolean SECONDARY_GRAPH = true;
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
         return this.x;
    }
    public int getY() {
        return this.y;
    }

    public Coordinate calculateAbsoluteCoordinate(int mainGraphNodeCount, int secondaryGraphNodeCount, boolean whichGraph) {
        /// if you are calculating absolute coordinate for the main graph, the first two parameters are unnecessary
        int x = (this.getX() * 4);
        int y = this.getY() * 4;
        if (whichGraph == SECONDARY_GRAPH) {
            x += mainGraphNodeCount + secondaryGraphNodeCount + 48;
        }

        return new Coordinate(x, y);
    }

}
