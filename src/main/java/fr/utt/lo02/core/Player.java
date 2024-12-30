package fr.utt.lo02.core;

import com.google.gson.annotations.Expose;
import fr.utt.lo02.core.components.Area;
import fr.utt.lo02.core.components.Cell;
import fr.utt.lo02.core.components.Sector;
import fr.utt.lo02.core.components.Ship;
import fr.utt.lo02.core.components.System;

import static fr.utt.lo02.data.DataManipulator.getConfigProperties;

import java.util.*;

public class Player {
    // private static int idCounter = 0;
    @Expose
    private final int id;
    @Expose
    private String name;
    @Expose
    private int score;
    // @Expose
    // private List<Command> commandsOrder;
    @Expose
    private Ship[] ships;
    @Expose
    private boolean dead = false;
    private Player nextPlayer;
    @Expose
    private boolean isAI = false;

    /**
     * Constructor for the Player class.
     * @param name the name of the player
     */
    public Player(String name, int id) {
        // give an unique id to the player
        // this.id = idCounter;
        // idCounter++;
        this.id = id;
        this.name = name;
        this.score = 0;
        // get the number of ships per player from the config file
        int nShips = Integer.parseInt(getConfigProperties().getProperty("numberShipsPerPlayer"));
        this.ships = new Ship[nShips];
        // add ships to the player supply
        for (int i = 0; i < nShips; i++) {
            this.ships[i] = new Ship(i);
        }
    }

    public Player(String name, int id, boolean isAI) {
        this(name, id);
        this.isAI = isAI;
    }

    /**
     * Get the name of the player.
     * @return the name of the player
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the score of the player.
     * @return the score of the player
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Set the score of the player.
     * @param score the new score of the player
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get a string representation of the player.
     * @return the name of the player
     */
    public String toString() {
        return this.name;
    }

    /**
     * Get the available ships of the player.
     * @param n the number of ships to get
     * @return the available ships, or null if the player doesn't have enough ships
     */
    public Ship[] getAvailableShips(int n) {
        Ship[] availableShips = new Ship[n];
        int i = 0;
        Game game = Game.getInstance();
        while (n > 0 && i < this.ships.length) {
            if (this.ships[i].isAvailable()) {
                availableShips[n - 1] = this.ships[i];
                n--;
            }
            i++;
        }
        if (n > 0) {
            return null;
        }
        return availableShips;
    }

    /**
     * Get all ships of the player.
     * @return an array of all ships
     */
    public Ship[] getShips() {
        return this.ships;
    }

    /**
     * Get the ID of the player.
     * @return the ID of the player
     */
    public int getId() {
        return this.id;
    }

    /**
     * Check if the player is dead.
     * @return true if the player is dead, false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Set the player's dead status.
     * @param dead the new dead status
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Score the player based on the sector ID.
     */
    public void score (int multiplier) {
        int SectorId = Game.getInstance().getInput().score(this.getId());
        // check if the input is valid
        Sector sector = null;
        try {
            sector = Game.getInstance().getArea().getSector(SectorId);
        } catch (InvalidGameInputExeceptions e) {
            Game.getInstance().getInput().displayError("You must chose a valid sector", this.getId());
            this.score(multiplier);
            return;
        }
        if (!sector.isScorable()) {
            Game.getInstance().getInput().displayError("You can't score this sector, it need to have occupied systems and not to be TriPrime", this.getId());
            this.score();
            return;
        }
        // score the player
        for (Cell cell : sector.getCells()) {
            System system = cell.getSystem();
            if (system != null && cell.getOwner() == this) {
                this.score += system.getLevel() * multiplier;
            }
        }
        sector.setUsed(true);
    }
    public void score() {
        this.score(1);
    }

    /**
     * Expand the player's ships.
     * @param nShips the number of ships to expand
     */
    public void expand(int nShips) {
        int[][] input = Game.getInstance().getInput().expand(this.getId(), nShips);
        // should be array of [cellId, nShips]
        // check if the input is valid
        // can't add too much ships
        if (input == null) {
            Game.getInstance().getInput().displayError("Invalid input", this.getId());
            expand(nShips);
        }
        if (Arrays.stream(input).mapToInt(x -> x[1]).sum() > nShips) {
            Game.getInstance().getInput().displayError("You can't add more ships than you have", this.getId());
            expand(nShips);
        }
        // need to add ships on player's system
        for (int[] i : input) {
            Game game = Game.getInstance();
            Cell cell = Game.getInstance().getArea().getCell(i[0]);
            if (cell.getSystem() == null) {
                Game.getInstance().getInput().displayError("You can't add ships on a cell without a system", this.getId());
                expand(nShips);
                return;
            }
            if (cell.getOwner() != this) {
                Game.getInstance().getInput().displayError("You can't add ships on a cell that doesn't belong to you",this.getId());
                expand(nShips);
                return;
            }
            Ship[] availableShips = this.getAvailableShips(i[1]);
            if (availableShips == null) {
                Game.getInstance().getInput().displayError("You don't have enough ships", this.getId());
                expand(nShips);
                return;
            }
            // add ships
            for (int j = 0; j < i[1]; j++) {
                availableShips[j].setCell(cell);
            }
        }
    }

    /**
     * Explore with the player's fleet.
     * @param nFleet the number of fleets to explore with
     */
    public void explore(int nFleet) {
        int[][][] input = Game.getInstance().getInput().explore(this.getId(), nFleet);
        // int[][][] input = [[[30, 2, 22], [22,2, 21]];
        // int[][][] input = new int[][][]{new int[][]{new int[]{11, 2, 16}}};
        // int[][][] input = new int[][][]{new int[][]{new int[]{16, 2, 10}}};
        // int[][][] input = [[[startCellId, nShips, endCellId], [startCellId, nShips, endCellId]], [[startCellId, nShips, endCellId]], ...];
        //
        // check if the input is valid
        Area area = Game.getInstance().getArea();
        if (input == null) {
            Game.getInstance().getInput().displayError("Invalid input", this.getId());
            explore(nFleet);
            return;
        }
        // Il faut un tebleau de cette forme : [nFleet][1,2][3]
        if (input.length > nFleet) {
            Game.getInstance().getInput().displayError("You can't explore with more than +" + nFleet + " fleets", this.getId());
            explore(nFleet);
            return;
        }
        for (int[][] fleetMove : input) {
            if (fleetMove.length > 2) {
                Game.getInstance().getInput().displayError("You can't explore with more than 2 ships per fleet", this.getId());
                explore(nFleet);
                return;
            }
            if (fleetMove.length == 2) {
                if (!(fleetMove[0][2] == fleetMove[1][0])) {
                    Game.getInstance().getInput().displayError("Invalid input, the end cell of the first move must be the start cell of the second move", this.getId());
                    explore(nFleet);
                    return;
                }
                if (area.getCell(fleetMove[0][2]) == area.getTriPrimeCell()) {
                    Game.getInstance().getInput().displayError("You can't pass through the TriPrime cell",this.getId());
                    explore(nFleet);
                    return;
                }
            }
            for (int[] move : fleetMove) {
                try {
                    area.getCell(move[0]);
                    area.getCell(move[2]);
                } catch (Exception e) {
                    Game.getInstance().getInput().displayError("Invalid input, a cell doesn't exist",this.getId());
                    explore(nFleet);
                    return;
                }
                if (move.length != 3) {
                    Game.getInstance().getInput().displayError("Invalid input", this.getId());
                    explore(nFleet);
                }
                Integer distance = area.getCell(move[0]).distance(area.getCell(move[2]), 2);
                if (distance == null || distance != 1) {
                    Game.getInstance().getInput().displayError("Invalid input, the cells must be adjacent", this.getId());
                    explore(nFleet);
                    return;
                }
            }
        }

        this.resetFleet();
        Map<Cell, Ship[]> fleets = new HashMap<>();
        // Area area = Game.getInstance().getArea();
        for (int[][] fleetMove : input) {
            // System.out.println(Arrays.asList(area.getCell(fleetMove[0][0]).getShips()));
            if (!(area.getCell(fleetMove[0][0]).getOwner() == this && area.getCell(fleetMove[0][2]).isAvailable(this))) {
                Game.getInstance().getInput().displayError("You need to start from a cell you own and end on an empty or owned cell", this.getId());
                explore(nFleet);
                return;
            }
            Ship[] availableShips = area.getCell(fleetMove[0][0]).getAvailableShips(fleetMove[0][1]);
            if (availableShips == null) {
                Game.getInstance().getInput().displayError("You don't have enough ships on this cell", this.getId());
                explore(nFleet);
                return;
            }
            List<Ship> fleet = new ArrayList<>(Arrays.asList(availableShips).subList(0, fleetMove[0][1]));
            if (fleetMove.length == 2) {
                if (!(area.getCell(fleetMove[1][2]).isAvailable(this))) {
                    Game.getInstance().getInput().displayError("You need to end on an empty or owned cell", this.getId());
                    explore(nFleet);
                    return;
                }
                availableShips = area.getCell(fleetMove[1][0]).getAvailableShips(fleetMove[1][1]);
                if (availableShips == null) {
                    Game.getInstance().getInput().displayError("You don't have enough ships on this cell", this.getId());
                    explore(nFleet);
                    return;
                }
                fleet.addAll(Arrays.asList(availableShips).subList(0, fleetMove[1][1]));
            }
            for (Ship ship : fleet) {
                ship.setUsed(true);
            }
            // fleets.put(area.getCell(fleetMove[0][0]), fleet.toArray(new Ship[0]));
            if (fleetMove.length == 2) {
                fleets.put(area.getCell(fleetMove[1][2]), fleet.toArray(new Ship[0]));
            } else {
                fleets.put(area.getCell(fleetMove[0][2]), fleet.toArray(new Ship[0]));
            }
        }

        // move the ships
        for (Cell cell : fleets.keySet()) {
            for (Ship ship : fleets.get(cell)) {
                ship.setCell(cell);
            }
        }
        // System.out.println("Fleet moved" + GameDataConverter.toJson(Game.getInstance()));
    }


    /**
     * Exterminate a system with the player's fleet.
     * @param nSystem the number of systems to exterminate
     */
    public void exterminate(int nSystem) {
        Game game = Game.getInstance();
        Area area = game.getArea();
        int[][] input = game.getInput().exterminate(this.getId(), nSystem);
        // check if the input is valid
        if (input == null) {
            game.getInput().displayError("Invalid input",this.getId());
            exterminate(nSystem);
            return;
        }
        if (input.length > nSystem) {
            game.getInput().displayError("You can't extermine more than " + nSystem + " systems", this.getId());
            exterminate(nSystem);
            return;
        }
        for (int[] i : input) {
            if (i.length % 2 != 1) {
                game.getInput().displayError("Invalid input", this.getId());
                exterminate(nSystem);
                return;
            }
            try {
                area.getCell(i[0]);
                for (int j = 1; j<i.length; j+=2) {
                    area.getCell(i[j]);
                }
            } catch (Exception e) {
                game.getInput().displayError("Invalid input, a cell doesn't exist", this.getId());
                exterminate(nSystem);
                return;
            }
        }

        this.resetFleet();
        Game.getInstance().getArea().resetSystems();
        Map<Cell, List<Ship>> newShipsPos = new HashMap<>();
        newShipsPos.put(null, new ArrayList<>());
        for (int[] currentInvasion : input) {
            Cell attackedCell = area.getCell(currentInvasion[0]);
            newShipsPos.put(attackedCell, new ArrayList<>());


            if (attackedCell.getOwner() == null || attackedCell.getOwner() == this) {
                game.getInput().displayError("Invalid input, you need to attack an enemies cell", this.getId());
                exterminate(nSystem);
                return;
            }
            if (attackedCell.getSystem() == null) {
                game.getInput().displayError("Invalid input, you need to attack a cell with a system", this.getId());
                exterminate(nSystem);
                return;
            }

            List<Ship> attackingShips = new ArrayList<>();
            List<Ship> attackedShips = new ArrayList<>(Arrays.asList(attackedCell.getShips()));

            for (int i = 1; i<currentInvasion.length; i+=2 ) {
                Cell c = area.getCell(currentInvasion[i]);
                Ship[] ships = c.getAvailableShips(currentInvasion[i+1]);
                // NOTE : we assume i+1 exist because we tested currentInvasion (size is even)
                if (ships == null) {
                    game.getInput().displayError("Invalid input, you don't have enough ships",this.getId());
                    exterminate(nSystem);
                    return;
                }
                attackingShips.addAll(List.of(ships));
            }

            Ship temp;
            while (!(attackedShips.isEmpty() && attackingShips.isEmpty())) {
                if (attackingShips.isEmpty()) {
                    temp = attackedShips.removeFirst();
                    temp.setUsed(true);
                    newShipsPos.get(attackedCell).add(temp);
                } else if (attackedShips.isEmpty()) {
                    temp = attackingShips.removeFirst();
                    temp.setUsed(true);
                    newShipsPos.get(attackedCell).add(temp);
                } else {
                    temp = attackingShips.removeFirst();
                    temp.setUsed(true);
                    newShipsPos.get(null).add(temp);
                    temp = attackedShips.removeFirst();
                    temp.setUsed(true);
                    newShipsPos.get(null).add(temp);
                }
            }
        }

        for (Cell cell : newShipsPos.keySet()) {
            for (Ship ship : newShipsPos.get(cell)) {
                ship.setCell(cell);
            }
        }
    }

    /**
     * Get the number of available ships.
     * @return the number of available ships
     */
    public int getNumberAvailableShips() {
        return (int) Arrays.stream(this.ships).filter(Ship::isAvailable).count();
    }

    /**
     * Reset the player's fleet. Set all ships to unused.
     */
    public void resetFleet() {
        for (Ship ship : this.ships) {
            ship.setUsed(false);
        }
    }


    /**
     * Set the next player for the iterator.
     * @param nextPlayer the next player
     * @throws IllegalGameStateExeceptions if the next player is already set
     * @see Player#next()
     */
    public void setNextPlayer(Player nextPlayer) {
        if (this.nextPlayer != null) {
            throw new IllegalGameStateExeceptions("Next player already set");
        }

        this.nextPlayer = nextPlayer;
    }

    /**
     * Get the next player.
     * @return the next player
     * But it can return null, if the next player is the starting player (when we already looped through all players)
     * @throws IllegalGameStateExeceptions if the next player is not found
     */
    public Player next() {
        if (this.nextPlayer == null) {
            throw new IllegalGameStateExeceptions("Next player not found");
        }
        Player next = this.nextPlayer;
        Game game = Game.getInstance();
        if (next == game.getStartingPlayer()) {
            return null;
        }
        if (next.isDead()) {
            return next.next();
        }
        return next;
    }

    public boolean checkDeath() {
        for (Ship ship : this.ships) {
            if (ship.getCell() != null) {
                return false;
            }
        }
        this.setDead(true);
        return true;
    }

    public boolean isHuman() {
        return !this.isAI;
    }
}