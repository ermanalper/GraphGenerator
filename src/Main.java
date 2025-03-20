import enigma.console.Console;
import enigma.core.Enigma;


import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;


public class Main {
    private static int row;
    public static Console console;
    private static int drawingMode = 0;

    private static KeyListener mainMenuKeyListener;
    private static KeyListener inputDegreeSequenceMethodKeyListener;
    private static KeyListener escToMainMenuKeyListener;
    private static KeyListener specifyDegreeIntervalsMethodKeyListener;
    private static KeyListener graphTestMenuKeyListener;
    private static KeyListener chooseDrawingMethodKeyListener;

    private static boolean printKbInput;
    private static String kbInput;
    private static String lastVerifiedKbInput;
    private static int newGraphVertexCount;
    private static int[] newGraphDegreeSequence;
    private static int ixPointer;
    private static Graph[] savedGraphs = new Graph[10]; //savedGraphs[9] is always the main graph
    private static int minDegree;
    private static int maxDegree;

    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            console.getTextWindow().setCursorPosition(0, i);
            console.getTextWindow().output("                                                                                                                                                                       ");
        }
        row = 0;
    }
    private static void printMainMenu() {
        row = 0;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output(" _______                    __                          ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("|     __|.----.---.-.-----.|  |--.                      ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("|    |  ||   _|  _  |  _  ||     |                      ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("|_______||__| |___._|   __||__|__|                      ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("                    |__|                                ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output(" _______                                __              ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("|     __|.-----.-----.-----.----.---.-.|  |_.-----.----.");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("|    |  ||  -__|     |  -__|   _|  _  ||   _|  _  |   _|");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("|_______||_____|__|__|_____|__| |___._||____|_____|__|  ");
        row++;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("1. Generate graph with degree sequence method");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("2. Generate graph with mindegree and maxdegree method");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("3. Display main graph");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("4. Change drawing method (default: 0)");
        row ++;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("Please select option 1, 2 or 3");


    }
    private static void graphTestMenuSetup() {
        //print background
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                int x = 4 * i;
                int y = 4 * j;
                console.getTextWindow().setCursorPosition(y, x);
                console.getTextWindow().output('.');
            }
        }
        //take the main graph,
        /// savedGraphs[9] is always the main graph
        Graph graph = savedGraphs[9];

        //print the relation matrix
        console.getTextWindow().setCursorPosition(40, 0);
        for (int i = 0; i < graph.nodeCount(); i++) {
            console.getTextWindow().output(graph.getNode(i).getName());
        }
        row = 1;
        int[][] relationMatrix = graph.buildRelationMatrix();
        for (int i = 0; i < graph.nodeCount(); i++) {
            console.getTextWindow().setCursorPosition(38, row++);
            console.getTextWindow().output(graph.getNode(i).getName() + " ");
            int[] row = relationMatrix[i];
            for (int j = 0; j < row.length; j++) {
                console.getTextWindow().output(String.valueOf(row[j]));
            }
            console.getTextWindow().output(" " + String.valueOf(graph.getNode(i).getDegree()));
        }
        row += 2;
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("Graph Test Menu: ");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("----------------------");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("1. Connected?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("2. Contains C3?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("3. Isolated Vertices?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("4. Complete Graph?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("5. Bipartite?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("6. Complete Bipartite?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("7. Cycle Graph (Cn)?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("8. Wheel Graph (Wn)?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("9. Star Graph (Sn)?");
        console.getTextWindow().setCursorPosition(38, row++);
        console.getTextWindow().output("10. Isomorphic?");




    }
    private static void setupDrawMethodChoose() {
        clearConsole();
        row = 0;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("Choose a drawing method: ");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("0. {1, 2, 3, 4, ...");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("1. {+, o, #, @");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("2. {+}");
    }



    public static int[] randomlyCreateDegreeSequence(int min, int max, int n) {
        //returns a degree sequence with mean = Mean(min, max)
        Random random = new Random();
        float totalDegree = ((float) (min + max) / 2) * n; //the sum of all degrees must be equal to totalDegree in order to ensure the "mean" rule

        int[] o = new int[n];

        if (totalDegree % 2 == 0 && totalDegree <= n * (n - 1) && min <= max && max < n) { // the given parameters should be valid to create a simple graph
            for (int i = 0; i < n; i++) {
                o[i] = min;
            }
            o[1] = max;
            int remainder = (int) totalDegree - ((n - 1) * min) - max;
            while (remainder > 0) {
                int targetIx = random.nextInt(n - 2);
                targetIx += 2;
                if (o[targetIx] < max) {
                    o[targetIx]++;
                    remainder--;
                }

            }


        } else {
            System.out.print("error"); // if the given parameters by the user is not valid, this will work. this should be handled later in the project development
        }
        if (isValidSequence(o)) {
            //if the built sequence is valid, return it
            return o;
        } else {
            //if it is not valid, rerun the function until it produces a valid sequence
            return randomlyCreateDegreeSequence(min, max, n);
        }

    }
    public static boolean isValidSequence(int[] degrees) {
        //This checks if the given degree sequence is actually a graphic or not
        //Graphic: The degree sequence of a simple graph
        int n = degrees.length;
        int[] temp = new int[n];

        int degreeSum = 0;
        for (int i = 0; i < n; i++) {
            if (degrees[i] >= n) return false;
            temp[i] = degrees[i]; //Clone degrees into temp to avoid changing the original array
            degreeSum += degrees[i];
        }
        if (degreeSum % 2 != 0 || degreeSum > n * (n - 1)) return false;

        while (true) {
            sortNonIncreasing(temp);

            if (temp[0] == 0) {
                return true; //all values are 0
            }

            int deg = temp[0];
            temp[0] = 0;

            for (int i = 1; i <= deg; i++) {
                temp[i]--;
                if (temp[i] < 0) return false;
            }
        }

    }
    private static void sortNonIncreasing(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[i - 1]) {
                int hold = array[i];
                int gapIx = i;
                do {
                    array[gapIx] = array[gapIx - 1];
                    gapIx--;
                } while (gapIx > 0 && array[gapIx - 1] < hold);
                array[gapIx] = hold;
            }

        }
    }

    private static void inputDegreeSequenceMethodSetup() {
        printKbInput = false;
        newGraphVertexCount = -1; //unassigned
        kbInput = "";
        clearConsole();
        row = 0;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("How many vertices are there in the graph?: ");
        ixPointer = 0;

    }
    private static void specifyDegreeIntervalsMethodSetup() {
        clearConsole();
        printKbInput = false;
        kbInput = "";
        row = 0;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("Enter vertex count: ");
        minDegree = -1; //unassigned
        maxDegree = -1; //unassigned
        newGraphVertexCount = -1; //unassigned
    }

    public static void registerListeners() {
        escToMainMenuKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    clearConsole();
                    printMainMenu();
                    //remove all key listeners
                    console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                    console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                    console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                    console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                    console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);


                    console.getTextWindow().addKeyListener(mainMenuKeyListener);
                    printKbInput = false;
                    kbInput = "";

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        mainMenuKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    //create graph with method 1
                    //create graph with method 2
                    case KeyEvent.VK_1: //
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);


                        console.getTextWindow().addKeyListener(inputDegreeSequenceMethodKeyListener);
                        inputDegreeSequenceMethodSetup();
                        break;

                    case KeyEvent.VK_2:
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);


                        console.getTextWindow().addKeyListener(specifyDegreeIntervalsMethodKeyListener);

                        specifyDegreeIntervalsMethodSetup();
                        break;
                    case KeyEvent.VK_3:
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);


                        console.getTextWindow().addKeyListener(graphTestMenuKeyListener);
                        clearConsole();
                        graphTestMenuSetup();
                        Graph graph = savedGraphs[9];
                        int[][] inkedPoints = new int[25][37];
                        for (Edge edge : graph.getEdgeArray()) {
                            edge.drawEdge(inkedPoints, drawingMode);
                        }
                        graph.printNodeNames();
                        break;
                    case KeyEvent.VK_4:
                        setupDrawMethodChoose();
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);


                        console.getTextWindow().addKeyListener(chooseDrawingMethodKeyListener);




                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        inputDegreeSequenceMethodKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                String typed = Character.toString(e.getKeyChar());
                if("0123456789".contains(typed) && printKbInput) {
                    kbInput += typed;
                    console.getTextWindow().output(typed);

                }
                printKbInput = true;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!kbInput.isEmpty()) {
                        lastVerifiedKbInput = kbInput;
                        kbInput = "";
                        if (newGraphVertexCount == -1) { //if the user had not entered the vertex count
                            newGraphVertexCount = Integer.parseInt(lastVerifiedKbInput);
                            console.getTextWindow().setCursorPosition(0, row++);
                            if (newGraphVertexCount == 0) {
                                console.getTextWindow().output("Creating new graph is failed! Vertex count cannot be 0. Press Esc for the main menu");
                                console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);

                            } else {
                                newGraphDegreeSequence = new int[newGraphVertexCount];
                                console.getTextWindow().output("Enter new node degree (one node at a time): ");
                            }

                        } else if (ixPointer < newGraphVertexCount) {
                            //specify next node's degree
                            int deg = Integer.parseInt(lastVerifiedKbInput);
                            newGraphDegreeSequence[ixPointer++] = deg;
                            if (ixPointer < newGraphVertexCount) {
                                console.getTextWindow().setCursorPosition(0, row++);
                                console.getTextWindow().output("Enter new node degree (one node at a time): ");
                            } else {
                                console.getTextWindow().setCursorPosition(0, row++);
                                if (isValidSequence(newGraphDegreeSequence)) {
                                    Graph graph = new Graph(newGraphDegreeSequence, 'A');
                                    graph.setEdgeArray(graph.randomlyConnectNodes());
                                    graph.randomlyPlaceNodes();
                                    savedGraphs[9] = graph; //save new graph as the main graph
                                    console.getTextWindow().output("New graph is successfully created. Press Esc for the main menu");
                                } else {
                                    console.getTextWindow().output("Creating new graph is failed! Invalid degree sequence for a simple graph. Press Esc for the main menu.");
                                }
                                console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);

                            }


                        }

                    }

                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        specifyDegreeIntervalsMethodKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                String typed = Character.toString(e.getKeyChar());
                if("0123456789".contains(typed) && printKbInput) {
                    kbInput += typed;
                    console.getTextWindow().output(typed);

                }
                printKbInput = true;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !kbInput.isEmpty()) {
                    lastVerifiedKbInput = kbInput;
                    kbInput = "";
                    int input = Integer.parseInt(lastVerifiedKbInput);
                    if (newGraphVertexCount == -1) {
                        //take node count input
                        if (input == 0) {
                            console.getTextWindow().setCursorPosition(0, row++);
                            console.getTextWindow().output("Vertex count cannot be zero. Please enter something positive: ");
                        } else {
                            newGraphVertexCount = input;
                            console.getTextWindow().setCursorPosition(0, row++);
                            console.getTextWindow().output("Specify minimum degree: ");
                        }
                    } else if (minDegree == -1) {
                        //take min degree input
                        minDegree = input;
                        console.getTextWindow().setCursorPosition(0, row++);
                        console.getTextWindow().output("Specify maximum degree: ");

                    } else if (maxDegree == -1){
                        //take max degree input
                        if (input < minDegree) {
                            console.getTextWindow().setCursorPosition(0, row++);
                            console.getTextWindow().output("Max degree cannot be smaller than the min degree. Press Esc for main menu ");
                            console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);

                        } else {
                            maxDegree = input;
                            float totalDegree = ((float)(minDegree + maxDegree) / 2) * newGraphVertexCount;
                            console.getTextWindow().setCursorPosition(0, row++);
                            if (totalDegree % 2 == 0 && totalDegree <= newGraphVertexCount * (newGraphVertexCount - 1) && minDegree <= maxDegree && maxDegree < newGraphVertexCount) {
                               // console.getTextWindow().output("birinci çalışır");
                                int[] degreeSequence = randomlyCreateDegreeSequence(minDegree, maxDegree, newGraphVertexCount);
                              //  console.getTextWindow().output("ikinci çalışır");
                                Graph graph = new Graph(degreeSequence, 'A');
                                graph.setEdgeArray(graph.randomlyConnectNodes());
                                graph.randomlyPlaceNodes();
                                savedGraphs[9] = graph;
                                console.getTextWindow().output("Graph created successfully. Press Esc for main menu");
                            } else {
                                console.getTextWindow().output("Invalid values. Press Esc for main menu");
                            }
                            console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);


                        }
                    }
                }



            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        graphTestMenuKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (row < 25) {
                    row = 25;
                }
                Graph graph = savedGraphs[9];//main graph is at index 9
                console.getTextWindow().setCursorPosition(0, row++);
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_1: /// connected
                        console.getTextWindow().output(String.valueOf(graph.isConnected()));
                        break;
                    case KeyEvent.VK_2: /// contains c3
                        String o = graph.getAllC3s();
                        if (o.isEmpty()) {
                            console.getTextWindow().output("No");
                        } else {
                            console.getTextWindow().output("Yes. Vertices: " + o);
                        }
                        break;
                    case KeyEvent.VK_3: /// isolated vertices
                        Graph isoVertices = graph.getIsolatedNodes();
                        if (isoVertices.nodeCount() == 0) {
                            console.getTextWindow().output("-");
                        } else {
                            for (int i = 0; i < isoVertices.nodeCount(); i++) {
                                console.getTextWindow().output(isoVertices.getNode(i).getName() + " ");
                            }
                        }
                        break;
                    case KeyEvent.VK_4: /// complete graph
                        console.getTextWindow().output(String.valueOf(graph.isCompleteGraph()));
                        break;




                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        chooseDrawingMethodKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                console.getTextWindow().setCursorPosition(0, row++);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_0:
                        drawingMode = 0;
                        console.getTextWindow().output("Drawing method is set successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_1:
                        drawingMode = 1;
                        console.getTextWindow().output("Drawing method is set successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_2:
                        drawingMode = 2;
                        console.getTextWindow().output("Drawing method is set successfully. Press Esc for the main menu");
                        break;
                    default:
                        console.getTextWindow().output("Invalid input. Please choose 0, 1, or 2");
                        break;
                }


            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };

        console.getTextWindow().addKeyListener(escToMainMenuKeyListener);
        console.getTextWindow().addKeyListener(mainMenuKeyListener);
    }
    public static void main(String[] args) {
        /*
        int[] degrees = {2, 2, 2, 0, 1, 1};
        Graph graph = new Graph(degrees, 'A');

        graph.randomlyConnectNodes();
        int[][] relation1 = Graph.buildRelationMatrix(graph);
        for (int[] row : relation1) {
            for (int j = 0; j < row.length; j++) {
                System.out.print(String.valueOf(row[j]) + " ");
            }
            System.out.println();
        }
        Graph isolatedNodes = graph.getIsolatedNodes();
        for (int i = 0; i < isolatedNodes.nodeCount(); i++) {
            System.out.println(isolatedNodes.getNode(i).getName());
        }
        System.out.println(Graph.isConnected(graph));
        */

        console = Enigma.getConsole("Graph Generator");
        printMainMenu();
        registerListeners();




















    }

}
