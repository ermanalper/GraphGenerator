import enigma.console.Console;
import enigma.core.Enigma;

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
    private static KeyListener graphTransferMenuKeyListener;


    private static boolean printKbInput;
    private static String kbInput;
    private static String lastVerifiedKbInput;
    private static int newGraphVertexCount;
    private static int[] newGraphDegreeSequence;
    private static int ixPointer;
    private static Graph[] depotGraphs = new Graph[9];
    private static Graph mainGraph;
    private static Graph secondaryGraph;
    private static int minDegree;
    private static int maxDegree;

    private static Graph[] bipartiteInfo;



    private static void clearConsole() {
        for (int i = 0; i < row + 50; i++) {
            console.getTextWindow().setCursorPosition(0, i);
            console.getTextWindow().output("                                                                                                                                                                                                                                           ");
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
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("5. Graph Transfer Menu");

        row ++;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("Please select option 1, 2, 3, 4 or 5");


    }

    private static void printRelationMatrixStartingFromSpecifiedCursorPosition(int x, int y, int[][] relationMatrix, int relationNumber) {
        console.getTextWindow().setCursorPosition(x, y++);
        console.getTextWindow().output("R^" + relationNumber);
        int n = relationMatrix.length;
        for (int i = 0; i < n; i++) {
            console.getTextWindow().setCursorPosition(x, y++);
            for (int j = 0; j < n; j++) {
                console.getTextWindow().output(String.valueOf(relationMatrix[i][j]));
            }
        }

    }
    private static void printRelationMatrices(int[][][] relationMatrices, int[][] minMatrix) {
        int n = relationMatrices.length;
        int x;
        int y;
        int r;
        //print relation matrices
        for (r = 1; r < n; r++) {
            //find the starting point
            x = 65 + ((r - 1) % 3) * (n + 2);
            y = (((r - 1) / 3) * (n + 2));
            printRelationMatrixStartingFromSpecifiedCursorPosition(x, y, relationMatrices[r], r + 1);
        }

        x = 65 + ((r - 1) % 3) * (n + 2);
        y = (((r - 1) / 3) * (n + 2));
        //print transitive closure
        console.getTextWindow().setCursorPosition(x, y++);
        console.getTextWindow().output("R*");
        for (int i = 0; i < n; i++) {
            console.getTextWindow().setCursorPosition(x, y++);
            for (int j = 0; j < n; j++) {
                console.getTextWindow().output(String.valueOf(relationMatrices[n - 1][i][j])); ///for an n-node-graph, R* = R^n, so we display R^n without doing any further calculation
            }
        }
        r++;
        x = 65 + ((r - 1) % 3) * (n + 2);
        y = (((r - 1) / 3) * (n + 2));
        //print Rmin
        console.getTextWindow().setCursorPosition(x, y++);
        console.getTextWindow().output("Rmin");
        for (int i = 0; i < n; i++) {
            console.getTextWindow().setCursorPosition(x, y++);
            for (int j = 0; j < n; j++) {
                console.getTextWindow().output(String.valueOf(minMatrix[i][j])); ///for an n-node-graph, R* = R^n, so we display R^n without doing any further calculation
            }
        }






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
        Graph graph = mainGraph;

        //print the relation matrix
        console.getTextWindow().setCursorPosition(40, 0);
        for (int i = 0; i < 26; i++) {
            if (graph.getNode(i) != null) {
                console.getTextWindow().output(graph.getNode(i).getName());
            }

        }
        row = 1;
        int[][] relationMatrix = graph.buildRelationMatrix();
        for (int i = 0; i < 26; i++) {
            if (graph.getNode(i) == null) continue;
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
        console.getTextWindow().output("0. Isomorphic?");

        int n = graph.nodeCount();
        int[][] minMatrix = new int[n][n];
        int[][][] relationMatrices = new int[n][n][n];

        //R1 matrix
        relationMatrices[0] = graph.buildRelationMatrix();
        //Mark 1-step-away nodes as 1 in the min matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (relationMatrices[0][i][j] == 1) {
                    minMatrix[i][j] = 1;
                }
            }
        }
        //find all other relation matrices using R1
        for (int r = 1; r < n; r++) {
            relationMatrices[r] = Graph.multiplyRelationMatrix(relationMatrices[r - 1], relationMatrices[0]);
            //if a node is newly reached, write down the steps needed to the min matrix
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (minMatrix[i][j] == 0 && relationMatrices[r][i][j] == 1) {
                        minMatrix[i][j] = r + 1;
                    }
                }
            }
        }
        printRelationMatrices(relationMatrices, minMatrix);

        bipartiteInfo = graph.bipartiteSeparation();





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
    private static void setupGraphTransferMenu() {
        clearConsole();
        row = 0;

        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("1. Copy main graph to secondary graph  (Key: G)");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("2. Copy secondary graph to main graph  (Key: H)");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("3. Load a graph file (\"graph1.txt\") to main graph (Key: L)");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("4. Save main graph to a file (\"graph1.txt\")          (Key: S)");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("5. Copy main graph to a depot graph (1-9) (Keys: QWE RTY UIO)");
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("6. Copy a depot graph to main graph         (Keys: 123  456  789)");
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
                    console.getTextWindow().removeKeyListener(graphTransferMenuKeyListener);


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
                        console.getTextWindow().removeKeyListener(graphTransferMenuKeyListener);


                        console.getTextWindow().addKeyListener(inputDegreeSequenceMethodKeyListener);
                        inputDegreeSequenceMethodSetup();
                        break;

                    case KeyEvent.VK_2:
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);
                        console.getTextWindow().removeKeyListener(graphTransferMenuKeyListener);


                        console.getTextWindow().addKeyListener(specifyDegreeIntervalsMethodKeyListener);

                        specifyDegreeIntervalsMethodSetup();
                        break;
                    case KeyEvent.VK_3:
                        if (mainGraph == null) {
                            console.getTextWindow().setCursorPosition(0, 19);
                            console.getTextWindow().output("Error! You must create the main graph first.");
                        } else {
                            console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                            console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                            console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                            console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                            console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);
                            console.getTextWindow().removeKeyListener(graphTransferMenuKeyListener);


                            console.getTextWindow().addKeyListener(graphTestMenuKeyListener);
                            clearConsole();
                            graphTestMenuSetup();

                            int[][] inkedPoints = new int[25][37];
                            for (Edge edge : mainGraph.getEdgeArray()) {
                                edge.drawEdge(inkedPoints, drawingMode, Graph.MAIN_GRAPH, 0, 0);
                            }
                            mainGraph.printNodeNames(Graph.MAIN_GRAPH, 0, 0);
                        }

                        break;
                    case KeyEvent.VK_4:
                        setupDrawMethodChoose();
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);
                        console.getTextWindow().removeKeyListener(graphTransferMenuKeyListener);


                        console.getTextWindow().addKeyListener(chooseDrawingMethodKeyListener);
                        break;
                    case KeyEvent.VK_5:
                        setupGraphTransferMenu();

                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                        console.getTextWindow().removeKeyListener(inputDegreeSequenceMethodKeyListener);
                        console.getTextWindow().removeKeyListener(specifyDegreeIntervalsMethodKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);
                        console.getTextWindow().removeKeyListener(chooseDrawingMethodKeyListener);


                        console.getTextWindow().addKeyListener(graphTransferMenuKeyListener);
                        break;





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
                                    mainGraph = graph; //save new graph as the main graph
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
                                mainGraph = graph;
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

                console.getTextWindow().setCursorPosition(0, row++);
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_1: /// connected
                        console.getTextWindow().output(String.valueOf(mainGraph.isConnected()));
                        break;
                    case KeyEvent.VK_2: /// contains c3
                        String o = mainGraph.getAllC3s();
                        row += o.length() / 40;
                        if (o.isEmpty()) {
                            console.getTextWindow().output("No");
                        } else {
                            console.getTextWindow().output("Yes. Vertices: " + o);
                        }
                        break;
                    case KeyEvent.VK_3: /// isolated vertices
                        Graph isoVertices = mainGraph.getIsolatedNodes();
                        if (isoVertices.nodeCount() == 0) {
                            console.getTextWindow().output("-");
                        } else {
                            for (int i = 0; i < 26; i++) {
                                if (isoVertices.getNode(i) != null) {
                                    console.getTextWindow().output(isoVertices.getNode(i).getName() + " ");
                                }

                            }
                        }
                        break;
                    case KeyEvent.VK_4: /// complete graph
                        console.getTextWindow().output(String.valueOf(mainGraph.isCompleteGraph()));
                        break;
                    case KeyEvent.VK_5: ///bipartite
                        if (bipartiteInfo == null) console.getTextWindow().output("No.");

                        else {
                            Graph pos = bipartiteInfo[0];
                            Graph neg = bipartiteInfo[1];
                            String out = "Yes. V1={";
                            for (int i = 0; i < 26; i++) {
                                if (pos.getNode(i) != null) out += pos.getNode(i).getName() + ",";
                            }
                            out = out.substring(0, out.length() - 1); //delete last comma ','
                            out += "} V2={";
                            for (int i = 0; i < 26; i++) {
                                if (neg.getNode(i) != null) out += neg.getNode(i).getName() + ",";
                            }
                            out = out.substring(0, out.length() - 1); //delete last comma ','
                            out += "}";
                            console.getTextWindow().output(out);

                        }
                        break;

                    case KeyEvent.VK_6: ///complete bipartite
                        if (bipartiteInfo == null) console.getTextWindow().output("No."); //the graph is not even bipartite, hence it cannot be "complete" bipartite
                        else {
                            Graph pos = bipartiteInfo[0];
                            Graph neg = bipartiteInfo[1];
                            int edgeCount = mainGraph.getEdgeArray().length;
                            if (pos.nodeCount() * neg.nodeCount() == edgeCount) { // complete bipartite
                                String out = "Yes. V1={";
                                for (int i = 0; i < 26; i++) {
                                    if (pos.getNode(i) != null) out += pos.getNode(i).getName() + ",";
                                }
                                out = out.substring(0, out.length() - 1); //delete last comma ','
                                out += "} V2={";
                                for (int i = 0; i < 26; i++) {
                                    if (neg.getNode(i) != null) out += neg.getNode(i).getName() + ",";
                                }
                                out = out.substring(0, out.length() - 1); //delete last comma ','
                                out += "}";
                                console.getTextWindow().output(out);

                            } else {
                                console.getTextWindow().output("No."); //bipartite, but not complete bipartite
                            }


                        }
                        break;
                    case KeyEvent.VK_7: ///cycle graph
                        console.getTextWindow().output(String.valueOf(mainGraph.isCycle()));
                        break;
                    case KeyEvent.VK_8: ///wheel graph
                        Node center = mainGraph.wheelCenter();
                        if (center == null) {
                            console.getTextWindow().output("No.");
                        } else {
                            console.getTextWindow().output("Yes. Center: " + center.getName());
                        }
                        break;
                    case KeyEvent.VK_0: ///isomorphism
                        if (secondaryGraph == null) {
                            console.getTextWindow().output("Error! You must choose a secondary graph from the \ngraph transfer menu.");
                            row++;
                        } else {
                            console.getTextWindow().removeKeyListener(graphTestMenuKeyListener);
                            //clear the info field on the right side of the screen
                            int mainNodeCount = mainGraph.nodeCount();
                            for (int i = 0; i <= mainNodeCount; i++) {
                                console.getTextWindow().setCursorPosition(43 + mainNodeCount, i);
                                console.getTextWindow().output("                                                                                                                                    ");
                            }
                            for (int i = mainNodeCount + 1; i < 25; i++) {
                                console.getTextWindow().setCursorPosition(37, i);
                                console.getTextWindow().output("                                                                                                                        ");
                            }
                            int n1 = mainGraph.nodeCount();
                            n1 = ((n1 + 1) * (n1 + 4)) / 3;
                            for (int i = 25; i < n1; i++) {
                                console.getTextWindow().setCursorPosition(0, i);
                                console.getTextWindow().output("                                                                                                                                                                                                                                   ");
                            }
                            row = 25;
                            // the field is cleared above
                            int n2 = secondaryGraph.nodeCount();

                            //print background
                            for (int i = 0; i < 7; i++) {
                                for (int j = 0; j < 10; j++) {
                                    int x = 4 * i;
                                    int y = (4 * j) + (48 + n1 + n2);
                                    console.getTextWindow().setCursorPosition(y, x);
                                    console.getTextWindow().output('.');
                                }
                            }
                            Edge[] edges = secondaryGraph.getEdgeArray();
                            int[][] inkedPoints = new int[25][37];
                            for (Edge edge : edges) {
                                edge.drawEdge(inkedPoints, drawingMode, Graph.SECONDARY_GRAPH, n1, n2);
                            }
                            secondaryGraph.printNodeNames(Graph.SECONDARY_GRAPH, n1, n2);



                        }







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
                        drawingMode = Graph.DRAWING_MODE_0;
                        console.getTextWindow().output("Drawing method is set successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_1:
                        drawingMode = Graph.DRAWING_MODE_1;
                        console.getTextWindow().output("Drawing method is set successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_2:
                        drawingMode = Graph.DRAWING_MODE_2;
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
        graphTransferMenuKeyListener= new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                clearConsole();
                console.getTextWindow().setCursorPosition(0, 0);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_G:
                    secondaryGraph=mainGraph;
                    console.getTextWindow().output("Main graph copied second graph successfully. Press Esc for the main menu");
                    break;
                    case KeyEvent.VK_H:
                        mainGraph= secondaryGraph;
                        console.getTextWindow().output("Second graph copied Main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_L:
                     //nası yapacağımızı tam bilmiyorum
                        break;
                    case KeyEvent.VK_S:
                        //burayı da bilmiyorum
                        break;
                        /*
              savedgraph[9]=main
                        8=1
                        7=2
                        6=3....
                         */
                    case KeyEvent.VK_Q:
                            depotGraphs[8]=mainGraph;
                            console.getTextWindow().output("Main graph copied 1. depot graph successfully. Press Esc for the main menu");
                            break;
                    case KeyEvent.VK_W:
                                depotGraphs[7]=mainGraph;
                                console.getTextWindow().output("Main graph copied 2. depot graph successfully. Press Esc for the main menu");
                                break;
                    case KeyEvent.VK_E:
                                    depotGraphs[6]=mainGraph;
                                    console.getTextWindow().output("Main graph copied 3. depot graph successfully. Press Esc for the main menu");
                                    break;
                    case KeyEvent.VK_R:
                        depotGraphs[5]=mainGraph;
                        console.getTextWindow().output("Main graph copied 4. depot graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_T:
                        depotGraphs[4]=mainGraph;
                        console.getTextWindow().output("Main graph copied 5. depot graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_Y:
                        depotGraphs[3]=mainGraph;
                        console.getTextWindow().output("Main graph copied 6. depot graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_U:
                        depotGraphs[2]=mainGraph;
                        console.getTextWindow().output("Main graph copied 7. depot graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_I:
                        depotGraphs[1]=mainGraph;
                        console.getTextWindow().output("Main graph copied 8. depot graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_O:
                        depotGraphs[0]=mainGraph;
                        console.getTextWindow().output("Main graph copied 9. depot graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_1:
                        mainGraph= depotGraphs[8];
                        console.getTextWindow().output("1. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_2:
                        mainGraph= depotGraphs[7];
                        console.getTextWindow().output("2. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_3:
                        mainGraph= depotGraphs[6];
                        console.getTextWindow().output("3. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_4:
                        mainGraph= depotGraphs[5];
                        console.getTextWindow().output("4. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_5:
                        mainGraph= depotGraphs[4];
                        console.getTextWindow().output("5. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_6:
                        mainGraph= depotGraphs[3];
                        console.getTextWindow().output("6. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_7:
                        mainGraph= depotGraphs[2];
                        console.getTextWindow().output("7. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_8:
                        mainGraph= depotGraphs[1];
                        console.getTextWindow().output("8. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;
                    case KeyEvent.VK_9:
                        mainGraph= depotGraphs[0];
                        console.getTextWindow().output("9. Depot graph copied main graph successfully. Press Esc for the main menu");
                        break;

                    default:
                        console.getTextWindow().output("Invalid input. Please enter G H L S QWE RTY UIO 123 456 789.");
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
