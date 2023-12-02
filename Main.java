import java.io.*;
import java.util.*;

public class Main {
    private static Board gameBoard;

    public static Board getGameBoard() {
        return gameBoard;
    }

    public static void main(String[] args) {
        try {
            File input = new File("/Users/dianayakupova/IdeaProjects/chess/input.txt");
            File output = new File("/Users/dianayakupova/IdeaProjects/chess/output.txt");
            Scanner scanner = new Scanner(input);
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            // Read and validate the board size
            int D = scanner.nextInt();
            if (D < 4 || D > 1000) {
                throw new InvalidBoardSizeException();
            }
            gameBoard = new Board(D);

            // Read and validate the number of insects
            int N = scanner.nextInt();
            if (N < 1 || N > 16) {
                throw new InvalidNumberOfInsectException();
            }

            // Read and validate the number of food points
            int M = scanner.nextInt();
            if (M < 1 || M > 200) {
                throw new InvalidNumberOfFoodPointsException();
            }

            // Read and validate the insects
            for(int i = 0; i < N; i++) {
                String[] text1;
                text1 = scanner.nextLine().split(" ");
                String color = text1[0];
                String type = text1[1];
                int X1 = Integer.parseInt(text1[2]);
                int Y1 = Integer.parseInt(text1[3]);
                // Validate the insect
                if (!color.matches("Red|Green|Blue|Yellow")) {
                    throw new InvalidInsectColorException();
                }

                if (!type.matches("Ant|Butterfly|Spider|Grasshopper")) {
                    throw new InvalidInsectTypeException();
                }
                if (X1 < 1 || X1 > D || Y1 < 1 || Y1 > D) {
                    throw new InvalidEntityPositionException();
                }
            }

            // Read and validate the food points
            for(int i = 0; i < M; i++) {
                scanner.next();
                String[] text2;
                text2 = scanner.nextLine().split(" ");
                int amount = Integer.parseInt(text2[0]);
                int X2 = Integer.parseInt(text2[1]);
                int Y2 = Integer.parseInt(text2[2]);

                if (X2 < 1 || X2 > D || Y2 < 1 || Y2 > D) {
                    throw new InvalidEntityPositionException();
                }
            }
            scanner.close();
            writer.close();

            // Continue with the rest of the program

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidBoardSizeException | InvalidNumberOfInsectException | InvalidNumberOfFoodPointsException |
                 InvalidInsectTypeException | InvalidEntityPositionException | InvalidInsectColorException e) {
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
        return boardData.get(position.toString());
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
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                    newPosition.getY() > boardSize) {
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

            case N:
                return new EntityPosition(entityPosition.getX() - 1, entityPosition.getY());
            case E:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() + 1);
            case S:
                return new EntityPosition(entityPosition.getX() + 1 , entityPosition.getY());
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
        Direction bestDirection = Direction.N;

        // Check each direction
        for (Direction dir : Direction.values()) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                    newPosition.getY() > boardSize) {
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
                return new EntityPosition(entityPosition.getX() + 1 , entityPosition.getY());
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
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                    newPosition.getY() > boardSize) {
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
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                    newPosition.getY() > boardSize) {
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
                return new EntityPosition(entityPosition.getX() + 2 , entityPosition.getY());
            case W:
                return new EntityPosition(entityPosition.getX(), entityPosition.getY() - 2);
            default:
        }
        return null;
    }
}

class EntityPosition {
    private int x, y;

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

class InvalidInsectColorException extends Exception{
    public String getMassage(){
        return "Invalid insect color";
    }
}

class DuplicateInsectsException extends Exception {
    public String getMassage(){
        return "Duplicate insects";
    }
}

class InvalidBoardSizeException extends Exception {
    public String getMassage(){

        return "Invalid board size";
    }
}

class InvalidEntityPositionException extends Exception{
    public String getMassage(){
        return "Invalid entity position";
    }
}

class InvalidInsectTypeException extends Exception{
    public String getMassage(){
        return "Invalid insect type";
    }
}

class InvalidNumberOfFoodPointsException extends Exception{
    public String getMassage(){
        return "Invalid number of food points";
    }
}

class InvalidNumberOfInsectException extends Exception {
    public String getMassage(){
        return "Invalid number of insects";
    }
}

class TwoEntitiesOnSamePositionException extends Exception {
    public String getMassage(){
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

    public abstract Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize);

    public abstract int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize);
}

interface DiagonalMoving {
    public default int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                newPosition.getY() > boardSize) {
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
                                Map<String, BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                newPosition.getY() > boardSize) {
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
    private EntityPosition calculateNewPosition(EntityPosition entityPosition, Direction direction) throws InvalidEntityPositionException {
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
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 || newPosition.getY() > boardSize) {
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
                                  Map<String, BoardEntity> boardData, int boardSize) throws InvalidEntityPositionException {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(entityPosition, dir);
        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                newPosition.getY() > boardSize) {
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
                return new EntityPosition(entityPosition.getX() + 1 , entityPosition.getY());
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

    public static final InsectColor toColor(String s) throws InvalidInsectColorException {
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
