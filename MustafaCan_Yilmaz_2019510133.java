import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;
import java.io.File;

class Task{
    private Piece piece;
    private int priority;

    public Task(Piece piece, int priority) {
        this.piece = piece;
        this.priority = priority;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getPriority() {
        return priority;
    }
}

class PriorityQueue{
    private Task[] heap;
    private int heapSize, capacity;


    public PriorityQueue(int capacity)
    {
        this.capacity = capacity + 1;
        heap = new Task[this.capacity];
        heapSize = 0;
    }

    public void clear()
    {
        heap = new Task[capacity];
        heapSize = 0;
    }

    public boolean isEmpty()
    {
        return heapSize == 0;
    }

    public boolean isFull()
    {
        return heapSize == capacity - 1;
    }

    public int size()
    {
        return heapSize;
    }

    public void insert(Piece piece, int priority)
    {
        Task newJob = new Task(piece, priority);

        heap[++heapSize] = newJob;
        int pos = heapSize;
        while (pos != 1 && newJob.getPriority() > heap[pos/2].getPriority())
        {
            heap[pos] = heap[pos/2];
            pos /=2;
        }
        heap[pos] = newJob;
    }

    public Task remove()
    {
        int parent, child;
        Task item, temp;
        if (isEmpty() )
        {
            System.out.println("Heap is empty");
            return null;
        }

        item = heap[1];
        temp = heap[heapSize--];

        parent = 1;
        child = 2;
        while (child <= heapSize)
        {
            if (child < heapSize && heap[child].getPriority() < heap[child + 1].getPriority())
                child++;
            if (temp.getPriority() >= heap[child].getPriority())
                break;

            heap[parent] = heap[child];
            parent = child;
            child *= 2;
        }
        heap[parent] = temp;

        return item;
    }
}

class Piece{
    private String name;
    private String type;
    private int cost;
    private int attackPoint;

    public Piece(String name, String type, int cost, int attackPoint) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.attackPoint = attackPoint;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public int getAttackPoint() {
        return attackPoint;
    }
}



public class MustafaCan_Yilmaz_2019510133 {

    public static void randomApproach(ArrayList<Piece> pieces, int gold){
        Random rnd = new Random();
        ArrayList<Piece> selectedPieces = new ArrayList<>();
        ArrayList<String> selectedTypes = new ArrayList<>();
        int totalAttackPoint = 0;
        // determine mincost
        int minCost = Integer.MAX_VALUE;
        Iterator<Piece> minIt = pieces.iterator();
        while (minIt.hasNext()){
            Piece p = minIt.next();
            if(p.getCost() < minCost){
                minCost = p.getCost();
            }
        }

        // iterate until cannot buy anything
        while (gold >= minCost){
            int index = rnd.nextInt(pieces.size());
            boolean has = false;
            Iterator<Piece> hasIt = selectedPieces.iterator();
            // find that if piece's kind already taken
            while (hasIt.hasNext()){
                Piece p = hasIt.next();
                if(p.getType().equals(pieces.get(index).getType())){
                    has = true;
                }

            }
            // add piece
            if(!has && pieces.get(index).getCost() <= gold){
                selectedPieces.add(pieces.get(index));
                selectedTypes.add(pieces.get(index).getType());
                totalAttackPoint += pieces.get(index).getAttackPoint();
                gold = gold - pieces.get(index).getCost();
            }

            // update min cost
            minCost = Integer.MAX_VALUE;
            Iterator<Piece> piecesIt = pieces.iterator();
            while (piecesIt.hasNext()){
                Piece p = piecesIt.next();
                if(!selectedTypes.contains(p.getType())){
                    if(p.getCost() < minCost){
                        minCost = p.getCost();
                    }
                }
            }
        }
        System.out.println("Total attack point: " + totalAttackPoint);
        System.out.println("Selected pieces: ");
        Iterator<Piece> printIt = selectedPieces.iterator();
        while (printIt.hasNext()){
            Piece p = printIt.next();
            System.out.println(p.getName() + " (" + p.getType() + ", " + p.getCost() + " Gold, " + p.getAttackPoint() + " Attack Point)");
        }
        System.out.println();
    }

    public static void greedyProgrammingApproach(ArrayList<Piece> pieces, int gold, int level){
        PriorityQueue piecePriority = new PriorityQueue(pieces.size());

        ArrayList<String> selectedTypes = new ArrayList<>();
        int maxAP = 0;
        ArrayList<Piece> selectedPieces = new ArrayList<>();
        Iterator<Piece> pIt = pieces.iterator();
        int minCost = Integer.MAX_VALUE;
        Iterator<Piece> minIt = pieces.iterator();
        while (minIt.hasNext()){
            Piece p = minIt.next();
            if(p.getCost() < minCost){
                minCost = p.getCost();
            }
        }
        while (pIt.hasNext()){
            Piece p = pIt.next();
            piecePriority.insert(p, p.getAttackPoint() / p.getCost());
        }
        while(gold > minCost && selectedTypes.size() != level){
            Piece p = piecePriority.remove().getPiece();
            if(!selectedTypes.contains(p.getType())){
                selectedPieces.add(p);
                selectedTypes.add(p.getType());
                maxAP += p.getAttackPoint();
                gold -= p.getCost();
            }
        }
        System.out.println("Total attack point: " + maxAP);
        System.out.println("Selected pieces: ");
        Iterator<Piece> solutionIt = selectedPieces.iterator();
        while (solutionIt.hasNext()){
            Piece p = solutionIt.next();
            System.out.println(p.getName() + " (" + p.getType() + ", " + p.getCost() + " Gold, " + p.getAttackPoint() + " Attack Point)");
        }
        System.out.println();
    }

    public static void dynamicProgrammingApproach(ArrayList<Piece> pieces, int gold, int level, int availablePieceNumber){
        int[][] solutionMatrix = new int[availablePieceNumber * level + 1][gold / 5 + 1];
        for (int cost = 1; cost < gold / 5 + 1; cost++) {
            // filling columns first
            for (int lvl = 1; lvl < level * availablePieceNumber + 1; lvl++) {
                Piece piece = pieces.get(lvl - 1);
                int currentMoney = cost * 5;
                if(piece.getCost() <= currentMoney){
                    int option1;
                    // comparing the current a.p. and the one before
                    if(lvl % availablePieceNumber == 0){
                        option1  = piece.getAttackPoint() + solutionMatrix[lvl - availablePieceNumber][cost - piece.getCost() / 5];
                    }
                    else {
                        option1  = piece.getAttackPoint() + solutionMatrix[lvl - lvl % availablePieceNumber][cost - piece.getCost() / 5];
                    }
                    int option2 = solutionMatrix[lvl - 1][cost];

                    solutionMatrix[lvl][cost] = Math.max(option1, option2);
                }
                else{
                    solutionMatrix[lvl][cost] = solutionMatrix[lvl - 1][cost];
                }

            }
        }

        System.out.println("Total attack point: " + solutionMatrix[availablePieceNumber * level][gold / 5]);
        System.out.println("Selected pieces: ");
        // traceback
        int i = availablePieceNumber * level;
        int j = gold / 5;
        while(solutionMatrix[i][j] != 0){
            if(-1 < i - 1 && solutionMatrix[i][j] != solutionMatrix[i - 1][j]){
                Piece p = pieces.get(i - 1);
                System.out.println(p.getName() + " (" + p.getType() + ", " + p.getCost() + " Gold, " + p.getAttackPoint() + " Attack Point)");
                j = j - pieces.get(i - 1).getCost() / 5;
                if(i % availablePieceNumber == 0){
                    i = i - availablePieceNumber;
                }
                else {
                    i = i - i % availablePieceNumber;
                }
            }
            else{
                i--;
            }
        }
        System.out.println();
    }



    public static void main(String[] args) throws FileNotFoundException {
        int gold = 0;
        int level = 0;
        int availablePieceNumber = 0;
        ArrayList<Piece> pieces = new ArrayList<>(90);
        Scanner scn = new Scanner(System.in);

        while(gold < 5 || gold > 1200){
            System.out.print("gold: ");
            gold = Integer.parseInt(scn.nextLine());
        }

        while (level < 1 || level > 9){
            System.out.print("level: ");
            level = Integer.parseInt(scn.nextLine());
        }

        while (availablePieceNumber < 1 || availablePieceNumber > 10){
            System.out.print("availablePieceNumber: ");
            availablePieceNumber = Integer.parseInt(scn.nextLine());
        }

        File inputFile = new File("input_1.csv");
        scn = new Scanner(inputFile);

        scn.nextLine();
        for (int i = 0; i < level; i++) {

            for (int j = 0; j < availablePieceNumber; j++) {
                String[] pieceData = scn.nextLine().split(",");
                Piece piece = new Piece(pieceData[0], pieceData[1], Integer.parseInt(pieceData[2]), Integer.parseInt(pieceData[3]));
                pieces.add(piece);
            }
            for (int j = 0; j < 10 - availablePieceNumber; j++) {
                scn.nextLine();
            }
        }
        scn.close();
        System.out.println();


        System.out.println("TRIAL #1");
        System.out.println("Computer's greedy approach results:");
        greedyProgrammingApproach(pieces, gold, level);
        System.out.println("User's dynamic programming results:");
        dynamicProgrammingApproach(pieces, gold, level, availablePieceNumber);

        System.out.println();

        System.out.println("TRIAL #2");
        System.out.println("Computer's random approach results:");
        randomApproach(pieces, gold);
        System.out.println("User's dynamic programming results:");
        dynamicProgrammingApproach(pieces, gold, level, availablePieceNumber);

    }
}