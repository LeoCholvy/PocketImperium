package fr.utt.lo02.IO;

import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.*;
import fr.utt.lo02.data.GameDataConverter;

import java.lang.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The IA class implements the IOHandlerIA interface and provides methods for handling
 * input and output operations for the AI in the game.
 */
public class IA implements IOHandlerIA {
    protected static int errorCounter = 0;
    protected static Game globalGame = null;

    /**
     * Sets the global game instance.
     *
     * @param game the game instance to set
     */
    public static void setGameInstance(Game game) {
        IA.globalGame = game;
    }

    /**
     * Displays an error message for a specific player.
     * If the player is human, the error is ignored.
     * If the error count exceeds certain thresholds, different actions are taken.
     *
     * @param message the error message to display
     * @param playerId the ID of the player
     */
    @Override
    public void displayError(String message, int playerId) {
        if (globalGame.getPlayer(playerId).isHuman()) {
            return;
        }
        errorCounter++;
        System.out.println("Error: " + message);
        if (errorCounter == 10) {
            System.out.println("Too many errors, turning into random IA...");
        }
        if (errorCounter > 15) {
            System.out.println("Too many errors, exiting...");
            throw new GameIAException("Too many errors");
        }
    }

    /**
     * Returns an array of possible starting cell IDs.
     *
     * @return an array of possible starting cell IDs
     */
    protected int[] getStartingCellIdPossiblities() {
        Sector[] sectorsArray = globalGame.getArea().getSectors();
        List<Sector> sectors = new ArrayList<>();
        for (Sector sector : sectorsArray) {
            if (!sector.isUsed()) {
                sectors.add(sector);
            }
        }

        List<Integer> cellIds = new ArrayList<>();
        for (Sector sector : sectors) {
            for (Cell cell : sector.getCells()) {
                if (cell.getSystem() != null && cell.getSystem().getLevel() == 1) {
                    cellIds.add(cell.getId());
                }
            }
        }
        int[] cellIdsArray = new int[cellIds.size()];
        for (int i = 0; i < cellIds.size(); i++) {
            cellIdsArray[i] = cellIds.get(i);
        }
        return cellIdsArray;
    }

    /**
     * Returns the starting cell ID for a specific player.
     *
     * @param playerId the ID of the player
     * @return the starting cell ID
     */
    @Override
    public int getStartingCellId(int playerId) {
        int[] cellIds = this.getStartingCellIdPossiblities();
        int cellId = cellIds[(int) (Math.random() * cellIds.length)];
        System.out.println("Player " + playerId + " starting from cell " + cellId);
        return cellId;
    }

    /**
     * Returns a list of possible command orders.
     *
     * @return a list of possible command orders
     */
    protected List<Command[]> getCommandOrderPossibilities() {
        List<Command[]> commandOrders = new ArrayList<>();
        commandOrders.add(new Command[]{Command.EXPAND, Command.EXPLORE, Command.EXTERMINATE});
        commandOrders.add(new Command[]{Command.EXPAND, Command.EXTERMINATE, Command.EXPLORE});
        commandOrders.add(new Command[]{Command.EXPLORE, Command.EXPAND, Command.EXTERMINATE});
        commandOrders.add(new Command[]{Command.EXPLORE, Command.EXTERMINATE, Command.EXPAND});
        commandOrders.add(new Command[]{Command.EXTERMINATE, Command.EXPAND, Command.EXPLORE});
        commandOrders.add(new Command[]{Command.EXTERMINATE, Command.EXPLORE, Command.EXPAND});
        return commandOrders;
    }

    /**
     * Returns the command order for the AI.
     *
     * @return the command order
     */
    @Override
    public Command[] getCommandOrder() {
        List<Command[]> commandOrders = getCommandOrderPossibilities();
        return commandOrders.get((int) (Math.random() * commandOrders.size()));
    }

    /**
     * Expands the player's ships to new cells.
     *
     * @param playerId the ID of the player
     * @param nShips the number of ships to expand
     * @return a 2D array representing the expansion
     */
    @Override
    public int[][] expand(int playerId, int nShips) {
        Game game = GameDataConverter.copy(IA.globalGame);
        Game.setInstance(game);
        ArrayList<int[]> ships = new ArrayList<>();
        Player player = game.getPlayer(playerId);
        int nShipsMax = Math.min(nShips, player.getNumberAvailableShips());
        while (nShipsMax > 0) {
            int nShipsOnCell;
            List<Cell> expandableCells = this.getExpandableCells(game, player);
            if (expandableCells.isEmpty()) {
                break;
            }
            int cellId = expandableCells.get((int) (Math.random() * expandableCells.size())).getId();

            if (nShipsMax == 1) {
                nShipsOnCell = 1;
            } else {
                nShipsOnCell = (int) (Math.random() * nShipsMax) + 1;
                Ship[] temp = game.getPlayer(playerId).getAvailableShips(nShipsOnCell);
                for (Ship s : temp) {
                    s.setCell(game.getArea().getCell(cellId));
                }
            }
            ships.add(new int[]{cellId, nShipsOnCell});
            nShipsMax -= nShipsOnCell;
        }
        System.out.println("Player " + game.getPlayer(playerId).getName() + " expanding : " + Arrays.deepToString(ships.toArray(new int[0][0])));
        Game.setInstance(IA.globalGame);
        return ships.toArray(new int[0][0]);
    }

    /**
     * Returns a list of cells owned by the player.
     *
     * @param game the game instance
     * @param player the player instance
     * @return a list of cells owned by the player
     */
    private List<Cell> getOwnedCells(Game game, Player player) {
        List<Cell> ownedCells = new ArrayList<>();
        for (Cell cell : game.getArea().getCells()) {
            int c = cell.getAvailableShipsCount();
            if (cell.getOwner() == player && cell.getAvailableShipsCount() > 0) {
                ownedCells.add(cell);
            }
        }
        return ownedCells;
    }

    /**
     * Returns a list of cells that can be expanded by the player.
     *
     * @param game the game instance
     * @param player the player instance
     * @return a list of expandable cells
     */
    private List<Cell> getExpandableCells(Game game, Player player) {
        List<Cell> ownedCells = getOwnedCells(game, player);
        for (int i = 0; i < ownedCells.size(); i++) {
            if (ownedCells.get(i).getSystem() == null) {
                ownedCells.remove(i);
                i--;
            }
        }
        return ownedCells;
    }

    /**
     * Explores new cells with the player's fleet.
     *
     * @param playerId the ID of the player
     * @param nFleet the number of fleets to explore
     * @return a 3D array representing the exploration
     */
    @Override
    public synchronized int[][][] explore(int playerId, int nFleet) {
        List<List<int[]>> input = new ArrayList<>();
        Game game = GameDataConverter.copy(IA.globalGame);
        Game.setInstance(game);
        Player player = game.getPlayer(playerId);
        Area area = game.getArea();
        while (nFleet > 0) {
            List<Ship> fleetShips = new ArrayList<>();
            List<int[]> fleet = new ArrayList<>();
            int startCellId, nShips, destCellId;
            List<Cell> ownedCells = this.getOwnedCells(game, player);
            if (ownedCells.isEmpty()) {
                System.out.println("Player " + game.getPlayer(playerId).getName() + " has no owned cells");
                break;
            }
            startCellId = ownedCells.get((int) (Math.random() * ownedCells.size())).getId();

            int tempMaxNumber = area.getCell(startCellId).getAvailableShipsCount();
            nShips = (int) (Math.random() * tempMaxNumber) + 1;

            if (nShips == 0) {
                nFleet--;
                continue;
            }

            List<Cell> freeNeighbors = area.getCell(startCellId).getFrendlyNeighbors(player);
            if (freeNeighbors.isEmpty()) {
                break;
            }
            destCellId = freeNeighbors.get((int) (Math.random() * freeNeighbors.size())).getId();

            fleetShips.addAll(Arrays.asList(area.getCell(startCellId).getAvailableShips(nShips)));
            for (Ship s : fleetShips) {
                s.setCell(area.getCell(destCellId));
                s.setUsed(true);
            }

            fleet.add(new int[]{startCellId, nShips, destCellId});

            int nShips2, destCellId2;
            tempMaxNumber = area.getCell(destCellId).getAvailableShipsCount();
            nShips2 = (int) (Math.random() * (tempMaxNumber + 1));

            if (nShips2 == 0) {
                input.add(fleet);
                nFleet--;
                continue;
            }

            freeNeighbors = area.getCell(destCellId).getFrendlyNeighbors(player);
            if (freeNeighbors.isEmpty()) {
                input.add(fleet);
                nFleet--;
                continue;
            }
            destCellId2 = freeNeighbors.get((int) (Math.random() * freeNeighbors.size())).getId();

            fleetShips.addAll(Arrays.asList(area.getCell(destCellId).getAvailableShips(nShips2)));
            for (Ship s : fleetShips) {
                s.setCell(area.getCell(destCellId2));
                s.setUsed(true);
            }

            fleet.add(new int[]{destCellId, nShips2, destCellId2});

            input.add(fleet);
            nFleet--;
        }

        int[][][] result = new int[input.size()][][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).toArray(new int[0][0]);
        }
        System.out.println("Player " + game.getPlayer(playerId).getName() + " exploring : " + Arrays.deepToString(result));

        Game.setInstance(IA.globalGame);

        return result;
    }

    /**
     * Exterminates enemy systems with the player's ships.
     *
     * @param playerId the ID of the player
     * @param nSystem the number of systems to exterminate
     * @return a 2D array representing the extermination
     */
    @Override
    public synchronized int[][] exterminate(int playerId, int nSystem) {
        List<List<Integer>> input = new ArrayList<>();
        Game game = GameDataConverter.copy(IA.globalGame);
        Game.setInstance(game);
        Player player = game.getPlayer(playerId);
        Area area = game.getArea();

        while (nSystem > 0) {
            List<Integer> currentAttack = new ArrayList<>();
            int cellId, id, nShips;

            List<Cell> enemyCells = game.getArea().getEnemyCells(player);
            if (enemyCells.isEmpty()) {
                break;
            }
            cellId = enemyCells.get((int) (Math.random() * enemyCells.size())).getId();

            Cell attackedCell = area.getCell(cellId);
            List<Ship> attackingShips = new ArrayList<>();
            List<Ship> attackedShips = new ArrayList<>(Arrays.asList(attackedCell.getShips()));

            while (true) {
                List<Cell> ownedNeighbors = getOwnedCells(game, player);
                for (int i = 0; i < ownedNeighbors.size(); i++) {
                    Integer distance = ownedNeighbors.get(i).distance(attackedCell, 2);
                    if (distance == null || distance != 1) {
                        ownedNeighbors.remove(i);
                        i--;
                    }
                }
                if (ownedNeighbors.isEmpty()) {
                    nSystem--;
                    break;
                }
                id = ownedNeighbors.get((int) (Math.random() * ownedNeighbors.size())).getId();

                nShips = (int) (Math.random() * (area.getCell(id).getAvailableShipsCount() + 1));
                if (nShips == 0) {
                    nSystem--;
                    break;
                }
                if (currentAttack.isEmpty()) {
                    currentAttack.add(cellId);
                }

                attackingShips.addAll(Arrays.asList(area.getCell(id).getAvailableShips(nShips)));
                currentAttack.add(id);
                currentAttack.add(nShips);

                while (!attackingShips.isEmpty() && attackedShips.size() > 0) {
                    attackingShips.getFirst().setCell(null);
                    Ship attackingShip = attackingShips.removeFirst();
                    attackedShips.getFirst().setCell(null);
                    Ship attackedShip = attackedShips.removeFirst();
                }
                if (!attackingShips.isEmpty()) {
                    for (Ship s : attackingShips) {
                        s.setCell(attackedCell);
                        s.setUsed(true);
                    }
                }
            }
            if (!currentAttack.isEmpty()) {
                input.add(currentAttack);
                nSystem--;
            }
        }

        int[][] result = new int[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        System.out.println("Player " + game.getPlayer(playerId).getName() + " exterminating : " + input);
        Game.setInstance(IA.globalGame);
        return result;
    }

    /**
     * Returns a list of scorable sectors.
     *
     * @return a list of scorable sectors
     */
    protected List<Sector> scorePossibilities() {
        return globalGame.getScorablesSectors();
    }

    /**
     * Scores a sector for the player.
     *
     * @param id the ID of the player
     * @return the ID of the scored sector
     */
    @Override
    public int score(int id) {
        List<Sector> sectors = scorePossibilities();
        int sectorId = sectors.get((int) (Math.random() * sectors.size())).getId();
        System.out.println("Player " + globalGame.getPlayer(id) + " scoring sector " + sectorId);
        return sectorId;
    }
}