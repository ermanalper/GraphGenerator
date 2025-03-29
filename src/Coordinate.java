public class Coordinate {
    private final int x;
    private final int y;
    public static final boolean MAIN_GRAPH = true;
    public static final boolean SECONDARY_GRAPH = false;
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

    public Coordinate calculateAbsoluteCoordinate(boolean whichGraph, int mainGraphNodeCount, int secondaryGraphNodeCount) {
        /// if you are calculating the absolute coordinates for the main graph, the last two parameters are unnecessary
        int x = this.getX() * 4;
        if (!whichGraph) x += 48 + mainGraphNodeCount + secondaryGraphNodeCount;
        int y = this.getY() * 4;
        return new Coordinate(x, y);
    }

}
