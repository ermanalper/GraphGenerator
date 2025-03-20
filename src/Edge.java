import enigma.console.Console;

import java.util.Random;

public class Edge {
    private final Node node1;
    private final Node node2;
    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public void drawEdge(int[][] inkedPoints, int drawingMode) {
        String[] palette = {};
        if (drawingMode == 1) {
            palette = new String[]{"+", "o", "#", "@"};
        } else if (drawingMode == 2) {
            palette = new String[]{"+", "+", "+", "+"};
        }


        Console console = Main.console;
        Random random = new Random();
        int bound = random.nextInt(2);
        Coordinate startingPoint;
        Coordinate endingPoint;

        /// START AND END POINTS ARE CHOSEN RANDOMLY, THIS SHOULD MAKE THE EDGES OVERLAP LESS
        if (bound == 0) {
            startingPoint = node1.getRelativeCoordinate().calculateAbsoluteCoordinate(0, 0, Coordinate.MAIN_GRAPH);
            endingPoint = node2.getRelativeCoordinate().calculateAbsoluteCoordinate(0, 0, Coordinate.MAIN_GRAPH);
        } else {
            startingPoint = node2.getRelativeCoordinate().calculateAbsoluteCoordinate(0, 0, Coordinate.MAIN_GRAPH);
            endingPoint = node1.getRelativeCoordinate().calculateAbsoluteCoordinate(0, 0, Coordinate.MAIN_GRAPH);
        }





        int printX = startingPoint.getX();
        int printY = startingPoint.getY();

        while (printX != endingPoint.getX() || printY != endingPoint.getY()) {
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
            String ink;
            int prevInkCount = inkedPoints[printY][printX]++;
            if (drawingMode == 0) {
                int inkInt = Math.min(prevInkCount + 1, 9);
                ink = String.valueOf(inkInt);
            } else {
                int inkIntIx = Math.min(prevInkCount, 3);
                ink = palette[inkIntIx];
            }



            console.getTextWindow().setCursorPosition(printX, printY);
            console.getTextWindow().output(ink);
        }

    }
    public Node[] getNodes() {
        return new Node[] {this.node1, this.node2};
    }

}
