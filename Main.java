import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;



/**
 * The main class. Here we do reading of data of input file, checking and putting them to output file.
 * We use java.io.File for file declaration, java.util.Scanner for reading from input file, java.io.BufferedWriter
 * for writing in output file.
 */
public class Main {
    /**
     * This is a board.
     */
    private static Board gameBoard;
    /**
     * List for insects.
     */
    private static LinkedList<EntityPosition> insects = new LinkedList<>();

    /**
     * The main method of all program.
     */
    public static void main(String[] args) throws IOException {
        /*
        ArrayList for check of two entities on the same position.
         */
        ArrayList<String> allTheEntities = new ArrayList<>();
        /*
        Initializing of files.
         */
        File input = new File("input.txt");
        File output = new File("output.txt");
        Scanner scanner = new Scanner(input);
        PrintWriter writer = new PrintWriter(output);
        /*
        Read and validate the board size
         */
        int d = scanner.nextInt();
        final int a = 4;
        final int b = 1000;
        gameBoard = new Board(d);
        /*
        Read and validate the number of insects
         */
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        /*
        Here we did check that board size is available.
         */
        if (d < a || d > b) {
            writer.write((new InvalidBoardSizeException()).getMassage());
            scanner.close();
            writer.close();
            System.exit(0);
        }
        final int c = 1;
        final int z = 16;
        /*
        Here we did check that number of insects is available.
         */
        if (n < c || n > z) {
            writer.write((new InvalidNumberOfInsectException()).getMassage());
            scanner.close();
            writer.close();
            System.exit(0);
        }

        final int f = 200;
        /*
        Here we did check that number of food points is available.
         */
        if (m < c || m > f) {
            writer.write((new InvalidNumberOfFoodPointsException()).getMassage());
            scanner.close();
            writer.close();
            System.exit(0);
        }
        /*
        Read and validate the insects
         */
        for (int i = 0; i < n; i++) {
            String color = scanner.next();
            String type = scanner.next();
            int x = Integer.parseInt(scanner.next());
            int y = Integer.parseInt(scanner.next());
            /*
            Check that do not have two entities on same position exception.
             */
            if (!allTheEntities.contains(x + " " + y)) {
                allTheEntities.add(x + " " + y);
            } else {
                writer.write((new TwoEntitiesOnSamePositionException()).getMassage());
                scanner.close();
                writer.close();
                System.exit(0);
            }
            /*
            Check that we do not have invalid insect color.
             */
            if (!color.matches("Red|Green|Blue|Yellow")) {
                writer.write((new InvalidInsectColorException()).getMassage());
                scanner.close();
                writer.close();
                System.exit(0);
            }
            /*
            Check that we do not have invalid insect type.
             */
            if (!type.matches("Ant|Butterfly|Spider|Grasshopper")) {
                writer.write((new InvalidInsectTypeException()).getMassage());
                scanner.close();
                writer.close();
                System.exit(0);
            }
            /*
            Check that we do not have invalid entity position.
             */
            if (x < 1 || x > d || y < 1 || y > d) {
                writer.write((new InvalidEntityPositionException()).getMassage());
                scanner.close();
                writer.close();
                System.exit(0);
            }
            EntityPosition entityPosition = new EntityPosition(x, y);
            InsectColor insectColor = InsectColor.toColor(color);
            switch (type) {
                case "Ant":
                    Insect ant = new Ant(entityPosition, insectColor);
                    try {
                        if (gameBoard.getEntity(entityPosition).entityPosition.position().
                                equals(entityPosition.position()) && gameBoard.getEntity(entityPosition).
                                getClass().getSimpleName().equals(ant.getClass().getSimpleName())
                                && ((Insect) gameBoard.getEntity(entityPosition)).color.equals(ant.color)) {
                            writer.write((new DuplicateInsectsException()).getMassage());
                        }
                    } catch (Exception e) {
                    }
                    gameBoard.addEntity(ant);
                    insects.add(entityPosition);
                    break;
                case "Butterfly":
                    Insect butterfly = new Butterfly(entityPosition, insectColor);
                    try {
                        if (gameBoard.getEntity(entityPosition).entityPosition.position().
                                equals(entityPosition.position()) && gameBoard.getEntity(entityPosition).
                                getClass().getSimpleName().equals(butterfly.getClass().getSimpleName())
                                && ((Insect) gameBoard.getEntity(entityPosition)).color.equals(butterfly.color)) {
                            writer.write((new DuplicateInsectsException()).getMassage());
                        }
                    } catch (Exception e) {
                    }
                    gameBoard.addEntity(butterfly);
                    insects.add(entityPosition);
                    break;
                case "Spider":
                    Insect spider = new Spider(entityPosition, insectColor);
                    try {
                        if (gameBoard.getEntity(entityPosition).entityPosition.position().
                                equals(entityPosition.position()) && gameBoard.getEntity(entityPosition).
                                getClass().getSimpleName().equals(spider.getClass().getSimpleName())
                                && ((Insect) gameBoard.getEntity(entityPosition)).color.equals(spider.color)) {
                            writer.write((new DuplicateInsectsException()).getMassage());
                        }
                    } catch (Exception e) {
                    }
                    gameBoard.addEntity(spider);
                    insects.add(entityPosition);
                    break;
                default:
                    Insect grasshopper = new Grasshopper(entityPosition, insectColor);
                    try {
                        if (gameBoard.getEntity(entityPosition).entityPosition.position().
                                equals(entityPosition.position()) && gameBoard.getEntity(entityPosition).
                                getClass().getSimpleName().equals(grasshopper.getClass().getSimpleName())
                                && ((Insect) gameBoard.getEntity(entityPosition)).color.equals(grasshopper.color)) {
                            writer.write((new DuplicateInsectsException()).getMassage());
                        }
                    } catch (Exception e) {
                    }
                    gameBoard.addEntity(grasshopper);
                    insects.add(entityPosition);
                    break;
            }
        }
        /*
        Read and validate the food points
         */
        for (int i = 0; i < m; i++) {
            int amount = Integer.parseInt(scanner.next());
            int x = Integer.parseInt(scanner.next());
            int y = Integer.parseInt(scanner.next());
            if (!allTheEntities.contains(x + " " + y)) {
                allTheEntities.add(x + " " + y);
            } else {
                writer.write((new TwoEntitiesOnSamePositionException()).getMassage());
                scanner.close();
                writer.close();
                System.exit(0);
            }
            if (x < 1 || x > d || y < 1 || y > d) {
                writer.write((new InvalidEntityPositionException()).getMassage());
                scanner.close();
                writer.close();
                System.exit(0);
            }
            EntityPosition entityPosition = new EntityPosition(x, y);
            FoodPoint foodPoint = new FoodPoint(entityPosition, amount);
            gameBoard.addEntity(foodPoint);
        }
        for (int i = 0; i < n; i++) {
            writer.write(((Insect) gameBoard.getEntity(insects.get(i))).color.colorToAnswer(((Insect)
                    gameBoard.getEntity(insects.get(i))).color) + " ");
            writer.write(((Insect) gameBoard.getEntity(insects.get(i))).getClass().getSimpleName() + " ");
            if (((Insect) gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Ant")) {
                writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).
                        getTextRepresentation() + " ");
                writer.write(Integer.toString(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i)))));
            } else if (((Insect)
                    gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Butterfly")) {
                writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i)))
                        .getTextRepresentation() + " ");
                writer.write(Integer.toString(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i)))));
            } else if (((Insect)
                    gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Spider")) {
                writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).
                        getTextRepresentation() + " ");
                writer.write(Integer.toString(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i)))));
            } else if (((Insect)
                    gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Grasshopper")) {
                writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).
                        getTextRepresentation() + " ");
                writer.write(Integer.toString(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i)))));
            }
            writer.write("\n");
        }
        scanner.close();
        writer.close();
    }
}

/**
 * The class Board.
 */
class Board {
    private Map<String, BoardEntity> boardData;
    private int size;


    public Board(int size) {
        this.size = size;
        this.boardData = new HashMap<>();
    }

    public void addEntity(BoardEntity entity) {
        boardData.put(entity.entityPosition.position(), entity);
    }

    public BoardEntity getEntity(EntityPosition position) {
        return boardData.get(position.position());
    }

    public Direction getDirection(Insect insect) {
        return insect.getBestDirection(boardData, size);
    }

    public int getDirectionSum(Insect insect) {
        return insect.travelDirection(getDirection(insect), boardData, size);
    }
}

// Ants can move vertically, horizontally, and diagonally.
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {

    public Ant(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }


    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        Direction bestDirection = Direction.N;
        // Check each direction
        for (Direction dir : Direction.values()) {
            switch (dir) {
                case N:
                    int temp = getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp) {
                        maxFood = temp;
                        bestDirection = Direction.N;
                    }
                    break;
                case E:
                    int temp1 = getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp1) {
                        maxFood = temp1;
                        bestDirection = Direction.E;
                    }
                    break;
                case S:
                    int temp2 = getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp2) {
                        maxFood = temp2;
                        bestDirection = Direction.S;
                    }
                    break;
                case W:
                    int temp3 = getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp3) {
                        maxFood = temp3;
                        bestDirection = Direction.W;
                    }
                    break;
                case NE:
                    int temp4 = getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp4) {
                        maxFood = temp4;
                        bestDirection = Direction.NE;
                    }
                    break;
                case SE:
                    int temp5 = getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp5) {
                        maxFood = temp5;
                        bestDirection = Direction.SE;
                    }
                    break;
                case SW:
                    int temp6 = getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp6) {
                        maxFood = temp6;
                        bestDirection = Direction.SW;
                    }
                    break;
                case NW:
                    int temp7 = getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
                    if (maxFood < temp7) {
                        maxFood = temp7;
                        bestDirection = Direction.NW;
                    }
                    break;
                default:
                    break;
            }

        }
        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        Direction dir1 = getBestDirection(boardData, boardSize);
        if (dir1.equals(Direction.N) || dir1.equals(Direction.E) || dir1.equals(Direction.S) || dir1.
                equals(Direction.W)) {
            return travelOrthogonally(dir1, entityPosition, color, boardData, boardSize);
        } else {
            return travelDiagonally(dir1, entityPosition, color, boardData, boardSize);
        }
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String,
            BoardEntity> boardData, int boardSize) {
        return OrthogonalMoving.super.travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        return OrthogonalMoving.super.getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize) {
        return DiagonalMoving.super.getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    //
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String,
            BoardEntity> boardData, int boardSize) {
        return DiagonalMoving.super.travelDiagonally(dir, entityPosition, color, boardData, boardSize);
    }


}

//Butterflies can move only vertically and horizontally.
class Butterfly extends Insect implements OrthogonalMoving {
    public Butterfly(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        int food;
        Direction bestDirection = Direction.N;
        // Check each direction
        for (Direction dir : Direction.values()) {
            food = getOrthogonalDirectionVisibleValue(dir, getEntityPosition(), boardData, boardSize);
            if (food > maxFood) {
                maxFood = food;
                bestDirection = dir;
            }
        }
        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelOrthogonally(dir, getEntityPosition(), color, boardData, boardSize);
    }
}

//Spiders can move only diagonally.
class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        int food = 0;
        Direction bestDirection = Direction.NE;
        // Check each direction
        for (Direction dir : Direction.values()) {
            food = getDiagonalDirectionVisibleValue(dir, getEntityPosition(), boardData, boardSize);
            if (food > maxFood) {
                maxFood = food;
                bestDirection = dir;
            }
        }
        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return travelDiagonally(dir, getEntityPosition(), color, boardData, boardSize);
    }

}

//Grasshoppers can jump only vertically and horizontally but by skipping odd fields.
class Grasshopper extends Insect {
    public Grasshopper(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        int food = 0;
        Direction bestDirection = Direction.N;
        // Check each direction
        for (Direction dir : Direction.values()) {
            food = getOrthogonalDirectionVisibleValue(dir, getEntityPosition(), boardData, boardSize);
            if (food > maxFood) {
                maxFood = food;
                bestDirection = dir;
            }
        }
        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        String coord = entityPosition.position();
        int x = Integer.parseInt(coord.split(" ")[0]);
        int y = Integer.parseInt(coord.split(" ")[1]);
        int food = 0;
        switch (dir) {
            case N:
                for (int i = x; i >= 1; i -= 2) {
                    int x1 = i;
                    EntityPosition here = new EntityPosition(x1, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case E:
                for (int i = y; i <= boardSize; i += 2) {
                    int y1 = i;
                    EntityPosition here = new EntityPosition(x, y1);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case S:
                for (int i = x; i <= boardSize; i += 2) {
                    int x1 = i;
                    EntityPosition here = new EntityPosition(x1, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case W:
                for (int i = y; i >= 1; i -= 2) {
                    int y1 = i;
                    EntityPosition here = new EntityPosition(x, y1);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            default:
        }
        boardData.remove(coord);
        return food;
    }

    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        String coord = entityPosition.position();
        int x = Integer.parseInt(coord.split(" ")[0]);
        int y = Integer.parseInt(coord.split(" ")[1]);
        int food = 0;
        switch (dir) {
            case N:
                for (int i = x; i >= 1; i -= 2) {
                    int x1 = i;
                    EntityPosition here = new EntityPosition(x1, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case E:
                for (int i = y; i <= boardSize; i += 2) {
                    int y1 = i;
                    EntityPosition here = new EntityPosition(x, y1);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case S:
                for (int i = x; i <= boardSize; i += 2) {
                    int x1 = i;
                    EntityPosition here = new EntityPosition(x1, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case W:
                for (int i = y; i >= 1; i -= 2) {
                    int y1 = i;
                    EntityPosition here = new EntityPosition(x, y1);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            default:
        }
        return food;
    }

}

class EntityPosition {
    private int x;
    private int y;

    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { //check it in the end and look are we need that parts of code or no
        return x;
    }

    public int getY() {
        return y;
    }

    public String position() {
        return x + " " + y;
    }
}

class FoodPoint extends BoardEntity {
    protected int value;

    public FoodPoint(EntityPosition position, int value) {
        this.entityPosition = position;
        this.value = value;
    }
}

class InvalidInsectColorException extends Exception {
    public String getMassage() {
        return "Invalid insect color";
    }
}

class DuplicateInsectsException extends Exception {
    public String getMassage() {
        return "Duplicate insects";
    }
}

class InvalidBoardSizeException extends Exception {
    public String getMassage() {
        return "Invalid board size";
    }
}

class InvalidEntityPositionException extends Exception {
    public String getMassage() {
        return "Invalid entity position";
    }
}

class InvalidInsectTypeException extends Exception {
    public String getMassage() {
        return "Invalid insect type";
    }
}

class InvalidNumberOfFoodPointsException extends Exception {
    public String getMassage() {
        return "Invalid number of food points";
    }
}

class InvalidNumberOfInsectException extends Exception {
    public String getMassage() {
        return "Invalid number of insects";
    }
}

class TwoEntitiesOnSamePositionException {
    public String getMassage() {
        return "Two entities in the same position";
    }
}

abstract class BoardEntity {
    protected EntityPosition entityPosition;

    public EntityPosition getEntityPosition() {
        return entityPosition;
    }
}

abstract class Insect extends BoardEntity {
    protected InsectColor color;

    public Insect(EntityPosition position, InsectColor color) {
        this.entityPosition = position;
        this.color = color;
    }

    public InsectColor getColor() {
        return color;
    }

    public abstract Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize);

    public abstract int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize);
}

interface DiagonalMoving {
    public default int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                        Map<String, BoardEntity> boardData, int boardSize) {
        String coord = entityPosition.position();
        int x = Integer.parseInt(coord.split(" ")[0]);
        int y = Integer.parseInt(coord.split(" ")[1]);
        int food = 0;
        switch (dir) {
            case NE:
                for (int i = x - 1, j = y + 1; i > 0 && j <= boardSize; i--, j++) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case SE:
                for (int i = x + 1, j = y + 1; i <= boardSize && j <= boardSize; i++, j++) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case SW:
                for (int i = x + 1, j = y - 1; i <= boardSize && j > 0; i++, j--) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case NW:
                for (int i = x - 1, j = y - 1; x > 0 && j > 0; i--, j--) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            default:
        }
        return food;
    }

    public default int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                        Map<String, BoardEntity> boardData, int boardSize) {
        String coord = entityPosition.position();
        int x = Integer.parseInt(coord.split(" ")[0]);
        int y = Integer.parseInt(coord.split(" ")[1]);
        int food = 0;
        switch (dir) {
            case NE:
                for (int i = x - 1, j = y + 1; i > 0 && j <= boardSize; i--, j++) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case SE:
                for (int i = x + 1, j = y + 1; i <= boardSize && j <= boardSize; i++, j++) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case SW:
                for (int i = x + 1, j = y - 1; i <= boardSize && j > 0; i++, j--) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case NW:
                for (int i = x - 1, j = y - 1; x > 0 && j > 0; i--, j--) {
                    x = i;
                    y = j;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            default:
        }
        boardData.remove(coord);
        return food;
    }
}

interface OrthogonalMoving {
    public default int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                          Map<String, BoardEntity> boardData, int boardSize) {
        String coord = entityPosition.position();
        int x = Integer.parseInt(coord.split(" ")[0]);
        int y = Integer.parseInt(coord.split(" ")[1]);
        int constXValue = x;
        int constYValue = y;
        int food = 0;
        switch (dir) {
            case N:
                for (int i = 1; i < constXValue; i++) {
                    x -= 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case E:
                for (int i = boardSize; i > constYValue; i--) {
                    y += 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case S:
                for (int i = boardSize; i > constXValue; i--) {
                    x += 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            case W:
                for (int i = 1; i < constYValue; i++) {
                    y -= 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if (boardData.get(here.position()) instanceof FoodPoint) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                    }
                }
                return food;
            default:
        }

        return food;
    }

    public default int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                          Map<String, BoardEntity> boardData, int boardSize) {
        String coord = entityPosition.position();
        int x = Integer.parseInt(coord.split(" ")[0]);
        int y = Integer.parseInt(coord.split(" ")[1]);
        int food = 0;
        int constXValue = x;
        int constYValue = y;
        switch (dir) {
            case N:
                for (int i = 1; i < constXValue; i++) {
                    x -= 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case E:
                for (int i = boardSize; i > constYValue; i--) {
                    y += 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case S:
                for (int i = boardSize; i > constXValue; i--) {
                    x += 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            case W:
                for (int i = 1; i < constYValue; i++) {
                    y -= 1;
                    EntityPosition here = new EntityPosition(x, y);
                    if ((boardData.get(here.position()) instanceof FoodPoint)) {
                        food += ((FoodPoint) boardData.get(here.position())).value;
                        boardData.remove(here.position());
                    } else if (boardData.get(here.position()) instanceof Insect) {
                        if (!(((Insect) boardData.get(here.position())).color.toString().equals(color.toString()))) {
                            boardData.remove(coord);
                            return food;
                        }
                    }
                }
                boardData.remove(coord);
                return food;
            default:
        }
        boardData.remove(coord);
        return food;
    }
}

enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");

    private String textRepresentation;

    Direction(String text) {
        this.textRepresentation = text;
    }

    public String getTextRepresentation() {
        return this.textRepresentation;
    }
}

enum InsectColor {
    RED, GREEN, BLUE, YELLOW;

    public static InsectColor toColor(String s) {

        switch (s.toLowerCase()) {
            case "red":
                return RED;
            case "green":
                return GREEN;
            case "blue":
                return BLUE;
            case "yellow":
                return YELLOW;
            default:
                return null;
        }
    }

    public String colorToAnswer(InsectColor color) {
        switch (color) {
            case RED:
                return "Red";
            case GREEN:
                return "Green";
            case BLUE:
                return "Blue";
            case YELLOW:
                return "Yellow";
            default:
                return null;
        }
    }

}
