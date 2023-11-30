import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.lang.Exception;

public class Main {

    public static void main(String[] args) {
        try {
            File input = new File("/Users/dianayakupova/IdeaProjects/chess/input.txt");
            File output = new File("/Users/dianayakupova/IdeaProjects/chess/output.txt");
            Scanner scanner = new Scanner(input);
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            // Read and validate the board size
            int D = scanner.nextInt();
            if (D < 4 || D > 1000) {
                writer.write("Invalid board size");
                writer.close();
                return;
            }

            // Read and validate the number of insects
            int N = scanner.nextInt();
            if (N < 1 || N > 16) {
                writer.write("Invalid number of insects");
                writer.close();
                return;
            }

            // Read and validate the number of food points
            int M = scanner.nextInt();
            if (M < 1 || M > 200) {
                writer.write("Invalid number of food points");
                writer.close();
                return;
            }

            // Read and validate the insects
            for(int i = 0; i < N; i++) {
                String[] text1 = scanner.nextLine().split(" ");
                String color = text1[0];
                String type = text1[1];
                int X1 = Integer.parseInt(text1[text1.length - 2]);
                int Y1 = Integer.parseInt(text1[text1.length - 1]);
                // Validate the insect
                if (!color.matches("Red|Green|Blue|Yellow")) {
                    writer.write("Invalid insect color");
                    writer.close();
                    return;
                }
                if (!type.matches("Ant|Butterfly|Spider|Grasshopper")) {
                    writer.write("Invalid insect type");
                    writer.close();
                    return;
                }
                if (X1 < 1 || X1 > D || Y1 < 1 || Y1 > D) {
                    writer.write("Invalid entity position");
                    writer.close();
                    return;
                }
            }

            // Read and validate the food points
            for(int i = 0; i < M; i++) {
                String[] text2 = scanner.nextLine().split(" ");
                int amount = Integer.parseInt(text2[0]);
                int X2 = Integer.parseInt(text2[text2.length - 2]);
                int Y2 = Integer.parseInt(text2[text2.length - 1]);

                // Validate the food point
                if (amount < 1 || amount > 100) {
                    writer.write("Invalid food amount");
                    writer.close();
                    return;
                }
                if (X2 < 1 || X2 > D || Y2 < 1 || Y2 > D) {
                    writer.write("Invalid entity position");
                    writer.close();
                    return;
                }
            }
            scanner.close();
            writer.close();

            // Continue with the rest of the program

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        boardData.put(entity.getPosition().toString(), entity);
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
        Direction bestDirection = null;

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
                int food = ((FoodPoint) entityAtNewPosition).getValue();
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            }
        }

        // If no food was found in any direction, return null
        if (bestDirection == null) {
            return null;
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int foodEaten = 0;
        EntityPosition currentPosition = this.entityPosition;

        while (true) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                    newPosition.getY() > boardSize) {
                break;
            }

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, eat it
            if (entityAtNewPosition instanceof FoodPoint) {
                foodEaten += ((FoodPoint) entityAtNewPosition).getValue();
            }

            // If the new position contains another insect, stop moving
            if (entityAtNewPosition instanceof Insect) {
                break;
            }

            // Move the insect to the new position
            this.entityPosition = newPosition;
            currentPosition = newPosition;
        }

        // Remove the insect from the old position and the food from the new position
        boardData.remove(this.entityPosition.toString());
        boardData.remove(currentPosition.toString());

        return foodEaten;
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String,
            BoardEntity> boardData, int boardSize) {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            return ((FoodPoint) entityAtNewPosition).getValue();
        }

        // Otherwise, return 0
        return 0;
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize) {
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
            int foodEaten = ((FoodPoint) entityAtNewPosition).getValue();
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 || newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            return ((FoodPoint) entityAtNewPosition).getValue();
        }

        // Otherwise, return 0
        return 0;
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
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
            int foodEaten = ((FoodPoint) entityAtNewPosition).getValue();
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }

    private EntityPosition calculateNewPosition(EntityPosition currentPosition, Direction direction) {
        switch (direction) {
            case N:
                return new EntityPosition(currentPosition.getX(), currentPosition.getY() - 1);
            case E:
                return new EntityPosition(currentPosition.getX() + 1, currentPosition.getY());
            case S:
                return new EntityPosition(currentPosition.getX(), currentPosition.getY() + 1);
            case W:
                return new EntityPosition(currentPosition.getX() - 1, currentPosition.getY());
            case NE:
                return new EntityPosition(currentPosition.getX() + 1, currentPosition.getY() - 1);
            case SE:
                return new EntityPosition(currentPosition.getX() + 1, currentPosition.getY() + 1);
            case SW:
                return new EntityPosition(currentPosition.getX() - 1, currentPosition.getY() + 1);
            case NW:
                return new EntityPosition(currentPosition.getX() - 1, currentPosition.getY() - 1);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }
}
//Butterflies can move only vertically and horizontally.
class Butterfly extends Insect implements OrthogonalMoving {
    public Butterfly(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        Direction bestDirection = null;

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
                int food = ((FoodPoint) entityAtNewPosition).getValue();
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            }
        }

        // If no food was found in any direction, return null
        if (bestDirection == null) {
            return null;
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
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
            int foodEaten = ((FoodPoint) entityAtNewPosition).getValue();
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 || newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            return ((FoodPoint) entityAtNewPosition).getValue();
        }

        // Otherwise, return 0
        return 0;
    }

    public int travelOrthogonally(Direction dir, EntityPosition entityPosition,
                                  InsectColor color, Map<String, BoardEntity> boardData, int boardSize) {
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
            int foodEaten = ((FoodPoint) entityAtNewPosition).getValue();
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }
    private EntityPosition calculateNewPosition(EntityPosition currentPosition, Direction direction) {
        switch (direction) {
            case N:
                return new EntityPosition(currentPosition.getX(), currentPosition.getY() - 1);
            case E:
                return new EntityPosition(currentPosition.getX() + 1, currentPosition.getY());
            case S:
                return new EntityPosition(currentPosition.getX(), currentPosition.getY() + 1);
            case W:
                return new EntityPosition(currentPosition.getX() - 1, currentPosition.getY());
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }
}

//Spiders can move only diagonally.
class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        Direction bestDirection = null;

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
                int food = ((FoodPoint) entityAtNewPosition).getValue();
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            }
        }

        // If no food was found in any direction, return null
        if (bestDirection == null) {
            return null;
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
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
            int foodEaten = ((FoodPoint) entityAtNewPosition).getValue();
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize) {
        // Calculate the new position based on the direction
        EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

        // Check if the new position is within the board
        if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                newPosition.getY() > boardSize) {
            return 0;
        }

        // Get the entity at the new position
        BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

        // If the new position contains food, return its value
        if (entityAtNewPosition instanceof FoodPoint) {
            return ((FoodPoint) entityAtNewPosition).getValue();
        }

        // Otherwise, return 0
        return 0;
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize) {
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
            int foodEaten = ((FoodPoint) entityAtNewPosition).getValue();
            boardData.remove(newPosition.toString());
            return foodEaten;
        }

        // Otherwise, return 0
        return 0;
    }
    private EntityPosition calculateNewPosition(EntityPosition currentPosition, Direction direction) {
        switch (direction) {
            case NE:
                return new EntityPosition(currentPosition.getX() + 1, currentPosition.getY() - 1);
            case SE:
                return new EntityPosition(currentPosition.getX() + 1, currentPosition.getY() + 1);
            case SW:
                return new EntityPosition(currentPosition.getX() - 1, currentPosition.getY() + 1);
            case NW:
                return new EntityPosition(currentPosition.getX() - 1, currentPosition.getY() - 1);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }
}

//Grasshoppers can jump only vertically and horizontally but by skipping odd fields.
class Grasshopper extends Insect {
    public Grasshopper(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int maxFood = 0;
        Direction bestDirection = null;

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
                int food = ((FoodPoint) entityAtNewPosition).getValue();
                if (food > maxFood) {
                    maxFood = food;
                    bestDirection = dir;
                }
            }
        }

        // If no food was found in any direction, return null
        if (bestDirection == null) {
            return null;
        }

        // Otherwise, return the direction with the most food
        return bestDirection;
    }

    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int foodEaten = 0;
        EntityPosition currentPosition = this.entityPosition;

        while (true) {
            // Calculate the new position based on the direction
            EntityPosition newPosition = calculateNewPosition(this.entityPosition, dir);

            // Check if the new position is within the board
            if (newPosition.getX() < 1 || newPosition.getX() > boardSize || newPosition.getY() < 1 ||
                    newPosition.getY() > boardSize) {
                break;
            }

            // Get the entity at the new position
            BoardEntity entityAtNewPosition = boardData.get(newPosition.toString());

            // If the new position contains food, eat it
            if (entityAtNewPosition instanceof FoodPoint) {
                foodEaten += ((FoodPoint) entityAtNewPosition).getValue();
            }

            // If the new position contains another insect, stop moving
            if (entityAtNewPosition instanceof Insect) {
                break;
            }

            // Move the insect to the new position
            this.entityPosition = newPosition;
            currentPosition = newPosition;
        }

        // Remove the insect from the old position and the food from the new position
        boardData.remove(this.entityPosition.toString());
        boardData.remove(currentPosition.toString());

        return foodEaten;
    }
    private EntityPosition calculateNewPosition(EntityPosition currentPosition, Direction direction) {
        switch (direction) {
            case N:
                return new EntityPosition(currentPosition.getX(), currentPosition.getY() - 2);
            case E:
                return new EntityPosition(currentPosition.getX() + 2, currentPosition.getY());
            case S:
                return new EntityPosition(currentPosition.getX(), currentPosition.getY() + 2);
            case W:
                return new EntityPosition(currentPosition.getX() - 2, currentPosition.getY());
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
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
}

class FoodPoint extends BoardEntity {
    protected int value;

    public FoodPoint(EntityPosition position, int value) {
        super(position);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public EntityPosition getEntityPosition() {
        return entityPosition;
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

    BoardEntity(EntityPosition entityPosition) {
        this.entityPosition = entityPosition;
    }

    public EntityPosition getPosition() {
        return entityPosition;
    }
}

abstract class Insect extends BoardEntity {
    protected InsectColor color;

    public Insect(EntityPosition position, InsectColor color) {
        super(position);
        this.color = color;
    }

    abstract public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize);

    abstract public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize);
}

interface DiagonalMoving {
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize);

    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize);
}

interface OrthogonalMoving {
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize);

    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize);
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
