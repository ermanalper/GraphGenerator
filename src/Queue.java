public class Queue {
    private Node[] nodes;
    private int front;
    private int rear;

    public Queue(int capacity) {
        this.nodes = new Node[capacity];
        this.front = 0;
        this.rear = -1;
    }

    public void enqueue(Node n) {
        if (this.isFull()) {
            System.out.println("Queue overflow!");
        } else {
            this.nodes[++rear] = n;
        }
    }
    public Node dequeue() {
        Node retVal = null;
        if (this.isEmpty()) {
            System.out.println("Queue is empty!");
        } else {
            retVal = this.nodes[front++];
        }
        return retVal;
    }

    public Node peek() {
        if (this.isEmpty()) {
            System.out.println("Queue is empty!");
            return null;
        } else {
            return this.nodes[front];
        }
    }
    public boolean isEmpty() {
        return front - rear == 1;
    }
    public boolean isFull() {
        //max insert index = [capacity - 1]
        //last inserted index = [rear]
        return rear + 1 == nodes.length;
    }
    public int size() {
        //first term (ready to dequeue) = [front]
        //last term (lastly inserted) = [rear]
        return rear - front + 1;
    }

}
