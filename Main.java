import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * The main class. Here we do reading of data of input file, checking and putting them to output file.
 * We use java.io.File for file declaration, java.util.Scanner for reading from input file, java.io.BufferedWriter
 * for writing in output file.
 */
public class Main {
    private static Board gameBoard;
    private static LinkedList<EntityPosition> insects = new LinkedList<>();
    public static void main(String[] args) {
        try {
            File input = new File("/Users/dianayakupova/IdeaProjects/chess/input.txt");
            File output = new File("/Users/dianayakupova/IdeaProjects/chess/output.txt");
            Scanner scanner = new Scanner(input);
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            // Read and validate the board size
            int d = scanner.nextInt();
            final int a = 4;
            final int b = 1000;
            if (d < a || d > b) {
                throw new InvalidBoardSizeException();
            }
            gameBoard = new Board(d);

            // Read and validate the number of insects
            int n = scanner.nextInt();
            final int c = 1;
            final int z = 16;
            if (n < c || n > z) {
                throw new InvalidNumberOfInsectException();
            }

            // Read and validate the number of food points
            int m = scanner.nextInt();
            final int f = 200;
            if (m < c || m > f) {
                throw new InvalidNumberOfFoodPointsException();
            }

            // Read and validate the insects
            for (int i = 0; i < n; i++) {
                String color = scanner.next();
                String type = scanner.next();
                int x = Integer.parseInt(scanner.next());
                int y = Integer.parseInt(scanner.next());
                // Validate the insect
                if (!color.matches("Red|Green|Blue|Yellow")) {
                    throw new InvalidInsectColorException();
                }
                if (!type.matches("Ant|Butterfly|Spider|Grasshopper")) {
                    throw new InvalidInsectTypeException();
                }
                if (x < 1 || x > d || y < 1 || y > d) {
                    throw new InvalidEntityPositionException();
                }
                EntityPosition entityPosition = new EntityPosition(x, y);
                InsectColor insectColor = InsectColor.toColor(color);
                switch (type) {
                    case "Ant":
                        Insect ant = new Ant(entityPosition, insectColor);
                        gameBoard.addEntity(ant);
                        insects.add(entityPosition);
                        break;
                    case "Butterfly":
                        Insect butterfly = new Butterfly(entityPosition, insectColor);
                        gameBoard.addEntity(butterfly);
                        insects.add(entityPosition);
                        break;
                    case "Spider":
                        Insect spider = new Spider(entityPosition, insectColor);
                        gameBoard.addEntity(spider);
                        insects.add(entityPosition);
                        break;
                    default:
                        Insect grasshopper = new Grasshopper(entityPosition, insectColor);
                        gameBoard.addEntity(grasshopper);
                        insects.add(entityPosition);
                        break;
                }
            }

            // Read and validate the food points
            for (int i = 0; i < m; i++) {
                int amount = Integer.parseInt(scanner.next());
                int x = Integer.parseInt(scanner.next());
                int y = Integer.parseInt(scanner.next());

                if (x < 1 || x > d || y < 1 || y > d) {
                    throw new InvalidEntityPositionException();
                }
                EntityPosition entityPosition = new EntityPosition(x, y);
                FoodPoint foodPoint = new FoodPoint(entityPosition, amount);
                gameBoard.addEntity(foodPoint);
            }
            for (int i = 0; i < n; i++) {
                writer.write(((Insect) gameBoard.getEntity(insects.get(i))).color.toString() + " ");
                writer.write(((Insect) gameBoard.getEntity(insects.get(i))).getClass().getSimpleName() + " ");
                if (((Insect) gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Ant")) {
                    writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).toString());
                    writer.write(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i))));
                } else if (((Insect)
                        gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Butterfly")) {
                    writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).toString());
                    writer.write(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i))));
                } else if (((Insect)
                        gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Spider")) {
                    writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).toString());
                    writer.write(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i))));
                } else if (((Insect)
                        gameBoard.getEntity(insects.get(i))).getClass().getSimpleName().equals("Grasshopper")) {
                    writer.write(gameBoard.getDirection((Insect) gameBoard.getEntity(insects.get(i))).toString());
                    writer.write(gameBoard.getDirectionSum((Insect) gameBoard.getEntity(insects.get(i))));
                }

            }
            scanner.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidBoardSizeException | InvalidNumberOfInsectException | InvalidNumberOfFoodPointsException
                 | InvalidInsectTypeException | InvalidEntityPositionException | InvalidInsectColorException e) {
            throw new RuntimeException(e);
        }
    }
}

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
        int food = 0;
        Direction bestDirection = Direction.N;

        // Check each direction
        for (Direction dir : Direction.values()) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                    || newPosition.getY() > boardSize) {
                continue;
            }

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, check if it's more than the current max food
            if (entityAtNewPosition instanceof FoodPoint) {
                food += ((FoodPoint) entityAtNewPosition).value;
                boardData.remove(this.entityPosition.position());
            }
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int foodEaten = 0;

        while (true) {
            // Calculate the best direction
            Direction bestDirection = getBestDirection(boardData, boardSize);

            // Calculate the new position based on the best direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, bestDirection);

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, eat it, else stop moving
            if (entityAtNewPosition instanceof FoodPoint) {
                foodEaten += ((FoodPoint) entityAtNewPosition).value;
                boardData.remove(newPosition.toString());
            } else {
                break;
            }

            // Move the insect to the new position
            this.entityPosition = newPosition;
        }

        // Remove the insect from the old position
        boardData.remove(entityPosition.toString());

        return foodEaten;
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String,
            BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        return OrthogonalMoving.super.travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        return OrthogonalMoving.super.getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize) throws InvalidEntityPositionException {
        return DiagonalMoving.super.getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    //
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String,
            BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        return DiagonalMoving.super.travelDiagonally(dir, entityPosition, color, boardData, boardSize);
    }

    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction) {
        return getEntityPosition(entityPosition, direction);
    }

    private static EntityPosition getEntityPosition(EntityPosition entityPosition, Direction direction) {
        switch (direction) {

            case N:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY());
            case E:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() + 1);
            case S:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY());
            case W:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() - 1);
            case NE:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY() + 1);
            case SE:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY() + 1);
            case SW:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY() - 1);
            case NW:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY() - 1);
            default:
        }
        return null;
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
        int food = 0;
        Direction bestDirection = Direction.N;

        // Check each direction
        for (Direction dir : Direction.values()) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                    || newPosition.getY() > boardSize) {
                continue;
            }

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, check if it's more than the current max food
            if (entityAtNewPosition instanceof FoodPoint) {
                int food = ((FoodPoint) entityAtNewPosition).value;
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            } else {
                return null;
            }
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int foodEaten = 0;

        while (true) {
            // Calculate the best direction
            Direction bestDirection = getBestDirection(boardData, boardSize);

            // Calculate the new position based on the best direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, bestDirection);

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, eat it, else stop moving
            if (entityAtNewPosition instanceof FoodPoint) {
                foodEaten += ((FoodPoint) entityAtNewPosition).value;
                boardData.remove(newPosition.toString());
            } else {
                break;
            }

            // Move the insect to the new position
            this.entityPosition = newPosition;
        }

        // Remove the insect from the old position
        boardData.remove(entityPosition.toString());

        return foodEaten;
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String,
            BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        return OrthogonalMoving.super.travelOrthogonally(dir, entityPosition, color, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        return OrthogonalMoving.super.getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction) {
        return getEntityPosition(entityPosition, direction);
    }

    private static EntityPosition getEntityPosition(EntityPosition entityPosition, Direction direction) {
        switch (direction) {
            case N:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY());
            case E:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() + 1);
            case S:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY());
            case W:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() - 1);
            default:
        }
        return null;
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
        Direction bestDirection = Direction.N;

        // Check each direction
        for (Direction dir : Direction.values()) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                    || newPosition.getY() > boardSize) {
                continue;
            }

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, check if it's more than the current max food
            if (entityAtNewPosition instanceof FoodPoint) {
                int food = ((FoodPoint) entityAtNewPosition).value;
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            } else {
                return null;
            }
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int foodEaten = 0;

        while (true) {
            // Calculate the best direction
            Direction bestDirection = getBestDirection(boardData, boardSize);

            // Calculate the new position based on the best direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, bestDirection);

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, eat it, else stop moving
            if (entityAtNewPosition instanceof FoodPoint) {
                foodEaten += ((FoodPoint) entityAtNewPosition).value;
                boardData.remove(newPosition.toString());
            } else {
                break;
            }

            // Move the insect to the new position
            this.entityPosition = newPosition;
        }

        // Remove the insect from the old position
        boardData.remove(entityPosition.toString());

        return foodEaten;
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity>
            boardData, int boardSize) throws InvalidEntityPositionException {
        return DiagonalMoving.super.getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String,
            BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        return DiagonalMoving.super.travelDiagonally(dir, entityPosition, color, boardData, boardSize);
    }

    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction) {
        return getEntityPosition(entityPosition, direction);
    }

    private static EntityPosition getEntityPosition(EntityPosition entityPosition, Direction direction) {
        switch (direction) {

            case NE:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY() + 1);
            case SE:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY() + 1);
            case SW:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY() - 1);
            case NW:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY() - 1);
            default:
        }
        return null;
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
        Direction bestDirection = Direction.N;

        // Check each direction
        for (Direction dir : Direction.values()) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                    || newPosition.getY() > boardSize) {
                continue;
            }

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, check if it's more than the current max food
            if (entityAtNewPosition instanceof FoodPoint) {
                int food = ((FoodPoint) entityAtNewPosition).value;
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            } else {
                return null;
            }
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int foodEaten = 0;

        while (true) {
            // Calculate the best direction
            Direction bestDirection = getBestDirection(boardData, boardSize);

            // Calculate the new position based on the best direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, bestDirection);

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, eat it, else stop moving
            if (entityAtNewPosition instanceof FoodPoint) {
                foodEaten += ((FoodPoint) entityAtNewPosition).value;
                boardData.remove(newPosition.toString());
            } else {
                break;
            }

            // Move the insect to the new position
            this.entityPosition = newPosition;
        }

        // Remove the insect from the old position
        boardData.remove(entityPosition.toString());

        return foodEaten;
    }

    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction) {
        return getEntityPosition(entityPosition, direction);
    }

    private static EntityPosition getEntityPosition(EntityPosition entityPosition, Direction direction) {
        switch (direction) {

            case N:
                return new EntityPosition(entityPosition.getX() - 2, entityPosition.getY());
            case E:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() + 2);
            case S:
                return new EntityPosition(entityPosition.getX() + 2, entityPosition.getY());
            case W:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() - 2);
            default:
        }
        return null;
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

class TwoEntitiesOnSamePositionException extends Exception {
    public String getMassage() {
        return "Two entities in the same position";
    }
}

abstract class BoardEntity {
    protected EntityPosition entityPosition;
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
                                                        Map<String, BoardEntity> boardData, int boardSize)
            throws InvalidEntityPositionException {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                || newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            return ((FoodPoint) entityAtNewPosition).value;
        }

        // Otherwise, return 0
        return 0;
    }

    public default int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                        Map<String, BoardEntity> boardData, int boardSize)
            throws InvalidEntityPositionException {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                || newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, eat it and return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            int foodEaten = ((FoodPoint) entityAtNewPosition).value;
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }

    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction)
            throws InvalidEntityPositionException {
        switch (direction) {
            case NE:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY() + 1);
            case SE:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY() + 1);
            case SW:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY() - 1);
            case NW:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY() - 1);
            default:
                throw new InvalidEntityPositionException();
        }
    }
}

interface OrthogonalMoving {
    public default int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                          Map<String, BoardEntity> boardData, int boardSize) {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 || newPosition.getY()
                > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            return ((FoodPoint) entityAtNewPosition).value;
        }

        // Otherwise, return 0
        return 0;
    }

    public default int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                          Map<String, BoardEntity> boardData, int boardSize)
            throws InvalidEntityPositionException {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);
        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1
                || newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, eat it and return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            int foodEaten = ((FoodPoint) entityAtNewPosition).value;
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }

    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction) {
        return getEntityPosition(entityPosition, direction);
    }

    private static EntityPosition getEntityPosition(EntityPosition entityPosition, Direction direction) {
        switch (direction) {
            case N:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY());
            case E:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() + 1);
            case S:
                return new EntityPosition(entityPosition.getX() + 1, entityPosition.getY());
            case W:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() - 1);
            default:
        }
        return null;
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

    public static InsectColor toColor(String s) throws InvalidInsectColorException {
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
                throw new InvalidInsectColorException();
        }
    }
}
