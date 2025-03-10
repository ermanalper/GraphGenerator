import enigma.console.TextWindow;
import enigma.core.Enigma;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Main {
    private static int row;
    public static enigma.console.Console console;
    private static KeyListener mainMenuKeyListener;
    private static KeyListener escToMainMenuKeyListener;

    private static void clearConsole() {
        for (int i = 0; i < 40; i++) {
            console.getTextWindow().setCursorPosition(0, i);
            console.getTextWindow().output("                                                                                                         ");
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
        row ++;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("Please select option 1 or 2");


    }


    //bunları şimdilik buraya koydum tanzer hoca selamlar
    public static int[] randomlyCreateDegreeSequence(int min, int max, int n) {
        //returns a degree sequence with mean = Mean(min, max)
        Random random = new Random();
        float totalDegree = ((float) (min + max) / 2) * n; //the sum of all degrees must be equal to totalDegree in order to ensure the "mean" rule

        int[] o = new int[n];

        if (totalDegree % 2 == 0 && totalDegree <= n * (n - 1) && min < max && max < n) { // the given parameters should be valid to create a simple graph
            //first saturate the min node -> [0]
            while (o[0] < min) {
                int targetIx = random.nextInt(n - 1);
                targetIx = targetIx + 1;
                if (o[targetIx] < max) {
                    o[0]++;
                    o[targetIx]++;
                }
            }

            //then saturate the max node -> [1]
            while (o[1] < max) {
                int targetIx = random.nextInt(n - 2);
                targetIx += 2;
                if (o[targetIx] < max) {
                    o[1]++;
                    o[targetIx]++;
                }
            }

            //then connect others nodes until they reach min
            for (int i = 2; i < n; i++) {
                while (o[i] < min) {
                    int targetIx = -1;
                    do {
                        targetIx = random.nextInt(n - 2);
                        targetIx += 2;
                    } while (targetIx == i);
                    if (o[targetIx] < max) {
                        o[i]++;
                        o[targetIx]++;
                    }
                }
            }
            //now connect nodes until we reach totalDegree
            int remainder = (int) totalDegree;
            for (int d : o){
                remainder -= d;
            }
            while (remainder > 0) {
                int terminalIx = random.nextInt(n - 2);
                terminalIx += 2;
                if (o[terminalIx] < max) {
                    int targetIx = -1;
                    do {
                        targetIx = random.nextInt(n - 2);
                        targetIx += 2;
                    } while (targetIx == terminalIx);
                    if (o[targetIx] < max) {
                        o[terminalIx]++;
                        o[targetIx]++;
                        remainder -= 2;
                    }
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

    private static void inputDegreeSequenceMethod() {
        clearConsole();
        row = 0;
        console.getTextWindow().setCursorPosition(0, row++);
        console.getTextWindow().output("Enter the number of vertices: ");


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
                    console.getTextWindow().addKeyListener(mainMenuKeyListener);
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
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                        inputDegreeSequenceMethod();
                        break;

                    case KeyEvent.VK_2:
                        console.getTextWindow().removeKeyListener(mainMenuKeyListener);
                       // inputMinMaxDegreeMethod();
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
        console = Enigma.getConsole("Graph Generator");
        printMainMenu();
        registerListeners();*/
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











    }

}
