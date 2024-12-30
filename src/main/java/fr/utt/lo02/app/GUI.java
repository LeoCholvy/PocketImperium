package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.GUIIOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.IllegalGameStateExeceptions;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.*;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

enum State {
    FORM,
    GAME,
    END,
}
enum InputState {
    IDLE,
    CELL,
    NUMBER,
    COMMAND,
}

public class GUI implements GUIIOHandler {
    private final static HashMap<Integer, int[]> coords = new HashMap<>(){{
        put(0, new int[]{200,299});
        put(1, new int[]{32,60});
        put(2, new int[]{100,57});
        put(3, new int[]{168,62});
        put(4, new int[]{234,63});
        put(5, new int[]{299,60});
        put(6, new int[]{367,63});
        put(7, new int[]{68,123});
        put(8, new int[]{134,119});
        put(9, new int[]{202,125});
        put(10, new int[]{266,121});
        put(11, new int[]{332,119});
        put(12, new int[]{34,179});
        put(13, new int[]{100,181});
        put(14, new int[]{166,182});
        put(15, new int[]{232,182});
        put(16, new int[]{300,179});
        put(17, new int[]{366,183});
        put(18, new int[]{66,240});
        put(19, new int[]{133,239});
        put(20, new int[]{33,303});
        put(21, new int[]{99,301});
        put(22, new int[]{66,360});
        put(23, new int[]{133,359});
        put(24, new int[]{267,239});
        put(25, new int[]{332,241});
        put(26, new int[]{300,300});
        put(27, new int[]{365,301});
        put(28, new int[]{267,360});
        put(29, new int[]{332,359});
        put(30, new int[]{33,421});
        put(31, new int[]{98,421});
        put(32, new int[]{167,421});
        put(33, new int[]{232,419});
        put(34, new int[]{300,421});
        put(35, new int[]{365,421});
        put(36, new int[]{66,481});
        put(37, new int[]{132,479});
        put(38, new int[]{198,481});
        put(39, new int[]{266,479});
        put(40, new int[]{332,481});
        put(41, new int[]{32,538});
        put(42, new int[]{100,541});
        put(43, new int[]{166,539});
        put(44, new int[]{231,539});
        put(45, new int[]{300,541});
        put(46, new int[]{366,539});
    }};

    private JFrame frame;
    private State state = null;
    private int playerId;
    private InputState inputState = InputState.IDLE;
    // private InputState inputState = InputState.CELL;
    private Color[] playersColor = new Color[]{Color.WHITE, Color.RED, Color.BLUE};
    private int w = 400;
    private int h = 600;
    private int panelHeight = 40;
    private String username;
    private Object tempInputValue;
    private Game game;
    private String[] textInfo = new String[]{"info", "Waiting for your turn"};
    private int tempMaxNumber;
    private Object lock = new Object();
    private boolean skipable = false;

    public GUI() {
        // init the frame
        this.frame = new JFrame("Pocket Imperium");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setGameInstance(Game game) {
        this.game = game;
        this.displayGame();
    }

    public synchronized void displayGame() {
        this.frame.getContentPane().removeAll();
        // this.state = State.GAME;

        // change frame name
        this.frame.setTitle("PocketImperium - "+this.username);

        JPanel gamePanel = new JPanel();

        this.displayTopPanel(gamePanel);

        BufferedImage map;
        try {
            map = ImageIO.read(new File("src/ressources/assets/map.png"));

            Image scaledMap = map.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            map = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = map.createGraphics();
            g2d.drawImage(scaledMap, 0, 0, null);
            g2d.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JLabel mapLabel = new JLabel(new ImageIcon(map));

        this.displayShips(gamePanel);

        mapLabel.setBounds(0,this.panelHeight,w,h);
        gamePanel.setLayout(null);
        gamePanel.add(mapLabel);

        this.frame.add(gamePanel);



        mapLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (GUI.this.inputState == InputState.CELL) {
                    int x = e.getX();
                    int y = e.getY();
                    int cellId = GUI.getClickedCell(x,y);
                    GUI.this.tempInputValue = cellId;
                    synchronized (GUI.this) {
                        GUI.this.notifyAll();
                    }
                }
            }
        });

        this.frame.setSize(w+15, this.panelHeight+h+38);
        // this.frame.pack();
        this.frame.setResizable(false);
        this.frame.setVisible(true);
    }

    private synchronized void displayTopPanel(JPanel gamePanel) {
        if (this.inputState == InputState.IDLE || this.inputState == InputState.CELL) {
            if (this.inputState == InputState.CELL && this.skipable) {
                JButton skip = new JButton("Skip");
                skip.setBounds(this.w-100, this.panelHeight-15, 100, 15);
                gamePanel.add(skip);
                skip.addActionListener(e -> {
                    this.tempInputValue = -1;
                    synchronized (GUI.this) {
                        GUI.this.notifyAll();
                    }
                });
            }
            StringBuilder scoreTxt = new StringBuilder();
            for (Player p : this.game.getPlayers()) {
                scoreTxt.append(p.getName()).append(": ").append(p.getScore()).append(" | ");
            }
            scoreTxt.append("round: ").append(this.game.getRound());
            JLabel score = new JLabel(scoreTxt.toString(), SwingConstants.LEFT);
            score.setFont(new Font("Arial", Font.PLAIN, 10));
            score.setForeground(Color.WHITE);
            score.setOpaque(true);
            score.setBackground(Color.BLACK);
            score.setBounds(0, 0, this.w, 10);
            gamePanel.add(score);
            String txt = this.textInfo[1];
            JLabel label = new JLabel(txt, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            if (this.textInfo[0].equals("error")) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBounds(10, 10, this.w - 20, this.panelHeight - 20);
            gamePanel.add(label);
            JLabel panelColor = new JLabel("", SwingConstants.CENTER);
            panelColor.setOpaque(true);
            panelColor.setBackground(this.playersColor[this.playerId]);
            panelColor.setBounds(0, 0, this.w, this.panelHeight);
            gamePanel.add(panelColor);
        } else if (this.inputState == InputState.NUMBER) {
            JButton[] numberButtons = new JButton[this.tempMaxNumber+1];
            for (int i = 0; i <= this.tempMaxNumber; i++) {
                if (this.skipable) {
                    JButton skip = new JButton("Skip");
                    skip.setBounds(this.w-100, this.panelHeight-15, 100, 15);
                    gamePanel.add(skip);
                    skip.addActionListener(e -> {
                        this.tempInputValue = -1;
                        synchronized (GUI.this) {
                            GUI.this.notifyAll();
                        }
                    });
                }


                numberButtons[i] = new JButton(String.valueOf(i));
                numberButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
                int l = 20;
                numberButtons[i].setBounds((int)(i*l*2.3), this.panelHeight-l, (int) (l*2.3), l);
                gamePanel.add(numberButtons[i]);
                int finalI = i;
                numberButtons[i].addActionListener(e -> {
                    this.tempInputValue = finalI;
                    synchronized (GUI.this) {
                        notifyAll();
                    }
                });
            }

            // info
            String txt = this.textInfo[1];
            JLabel label = new JLabel(txt, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            if (this.textInfo[0].equals("error")) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBounds(0,0,this.w, this.panelHeight/2);
            gamePanel.add(label);
        } else if (this.inputState == InputState.COMMAND) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            listModel.addElement("Expand");
            listModel.addElement("Explore");
            listModel.addElement("Exterminate");

            JList<String> list = new JList<>(listModel);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setDragEnabled(true);
            list.setDropMode(DropMode.INSERT);

            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(1);

            list.setTransferHandler(new TransferHandler() {
                private int draggedIndex = -1;

                @Override
                public int getSourceActions(JComponent c) {
                    return MOVE;
                }

                @Override
                protected Transferable createTransferable(JComponent c) {
                    JList<?> source = (JList<?>) c;
                    draggedIndex = source.getSelectedIndex(); // Stocke l'index de l'élément glissé
                    return new StringSelection(source.getSelectedValue().toString());
                }

                @Override
                public boolean canImport(TransferSupport support) {
                    return support.isDataFlavorSupported(DataFlavor.stringFlavor);
                }

                @Override
                public boolean importData(TransferSupport support) {
                    if (!canImport(support)) {
                        return false;
                    }

                    JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
                    int dropIndex = dropLocation.getIndex();

                    try {
                        String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

                        JList<String> target = (JList<String>) support.getComponent();
                        DefaultListModel<String> listModel = (DefaultListModel<String>) target.getModel();

                        // Ajuste l'index si nécessaire
                        if (draggedIndex < dropIndex) {
                            dropIndex--; // L'élément glissé est temporairement retiré, donc l'indice diminue
                        }

                        listModel.add(dropIndex, data);
                        listModel.remove(draggedIndex < dropIndex ? draggedIndex : draggedIndex + 1);

                        return true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }
            });

            // frame.add(new JScrollPane(list), BorderLayout.CENTER);
            // frame.setVisible(true);
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setWheelScrollingEnabled(false);
            scrollPane.setBounds(0,this.panelHeight/2,this.w-100,this.panelHeight/2);
            scrollPane.setBackground(Color.BLUE);
            gamePanel.add(scrollPane);

            // submit
            JButton submitButton = new JButton("Submit");

            submitButton.addActionListener(e -> {
                System.out.println("Btn clicked");
                if (inputState != InputState.COMMAND) {
                    return;
                }
                List<String> commands_list = new ArrayList<>();
                for (int i = 0; i < listModel.size(); i++) {
                    commands_list.add(listModel.getElementAt(i));
                }
                Command[] commands = new Command[3];
                for (int i=0;i<3;i++){
                    if (commands_list.get(i).equals("Expand")){
                        commands[i] = Command.EXPAND;
                    } else if (commands_list.get(i).equals("Explore")) {
                        commands[i] = Command.EXPLORE;
                    } else if (commands_list.get(i).equals("Exterminate")) {
                        commands[i] = Command.EXTERMINATE;
                    }
                }

                this.tempInputValue = commands;
                synchronized (GUI.this) {
                    notifyAll();
                }
            });
            submitButton.setBounds(this.w-100,this.panelHeight/2,100, this.panelHeight/2);
            gamePanel.add(submitButton);


            // info
            String txt = this.textInfo[1];
            JLabel label = new JLabel(txt, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            if (this.textInfo[0].equals("error")) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBounds(0,0,this.w, this.panelHeight/2);
            gamePanel.add(label);
        }
    }

    private void displayShips(JPanel gamePanel) {
        for (int id : coords.keySet()){
            int nb = this.game.getArea().getCell(id).getShips().length;
            if (nb == 0) {
                continue;
            }

            int nbA = this.game.getArea().getCell(id).getAvailableShipsCount();
            String txt = String.valueOf(nbA)+"+"+String.valueOf(nb-nbA);
            if (nbA == nb) {
                txt = String.valueOf(nb);
            }

            JLabel centerLabel = new JLabel(txt, SwingConstants.CENTER);
            centerLabel.setOpaque(false);
            centerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            centerLabel.setForeground(this.playersColor[this.game.getArea().getCell(id).getOwner().getId()]);
            int x = coords.get(id)[0];
            int y = coords.get(id)[1] + this.panelHeight;
            // centerLabel.setBounds(x-7,y-10, 15, 20);
            centerLabel.setBounds(x-20,y-10, 40, 20);
            gamePanel.add(centerLabel);
        }
    }

    public synchronized void displayForm() {
        this.frame.setSize(300,150);

        JPanel panel = new JPanel();

        JLabel ipLabel = new JLabel("Enter Server IP:");
        JTextField ipTextField = new JTextField(15);
        JLabel userLabel = new JLabel("Enter Username:");
        JTextField userTextField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            System.out.println("Btn clicked");
            this.tempInputValue = ipTextField.getText();
            this.username = userTextField.getText();

            System.out.println("Server IP: " + tempInputValue);
            System.out.println("Username: " + username);

            synchronized (this) {
                this.notifyAll();
            }
        });

        panel.add(ipLabel);
        panel.add(ipTextField);
        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(submitButton);

        this.frame.add(panel);

        // this.frame.pack();
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        String json = DataManipulator.loadSave("model");
        Game game = GameDataConverter.fromJson(json, "stonks");
        gui.setGameInstance(game);
        gui.setPlayerId(1);
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(46));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(3));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(4));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(8));
        game.getPlayer(1).getAvailableShips(1)[0].setCell(game.getArea().getCell(8));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        game.getPlayer(0).getAvailableShips(1)[0].setCell(game.getArea().getCell(9));
        int[][] r = gui.exterminate(1, 2);
        System.out.println(Arrays.deepToString(r));
    }

    public synchronized String getIp() {
        this.state = State.FORM;
        this.displayForm();

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String serverIp = (String) this.tempInputValue;
        if (serverIp.isEmpty() || this.username.isEmpty()) {
            return getUserName();
        }

        System.out.println("Got Ip from GUI");
        return serverIp;
    }

    public String getUserName() {
        return this.username;
    }

    private static int getClickedCell(int x, int y) {
        return coords.entrySet().stream()
                .min((entry1, entry2) -> Double.compare(distance(x, y, entry1.getValue()), distance(x, y, entry2.getValue())))
                .map(entry -> entry.getKey())
                .orElseThrow(() -> new IllegalArgumentException("No closest point found"));
    }

    private static double distance(int x, int y, int[] p) {
        return Math.sqrt(Math.pow(x - p[0], 2) + Math.pow(y - p[1], 2));
    }

    @Override
    public synchronized void displayError(String message, int playerId) {
        if (playerId != this.playerId) {
            return;
        }
        this.textInfo = new String[]{"error", message};
        this.displayGame();
    }

    private synchronized void waitInput(InputState inputState, String txt) {
        if (this.inputState != InputState.IDLE) {
            try {
                this.lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.inputState = inputState;
        this.printTextInfo(txt);
        this.displayGame();
        try {
            this.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void resetInputMode() {
        this.inputState = InputState.IDLE;
        this.resetTextInfo();
        this.displayGame();
        try {
            this.lock.notify();
        } catch (Exception _) {
        }
    }

    @Override
    public synchronized int getStartingCellId(int playerId) {
        // this.inputState = InputState.CELL;
        // this.printTextInfo("Chose a cell to place your ship");
        // this.displayGame();
        System.out.println("Chose a starting cell");

        // wait for player input
        // synchronized (this) {
        //     try {
        //         this.wait();
        //     } catch (InterruptedException e) {
        //         throw new RuntimeException(e);
        //     }
        // }
        this.waitInput(InputState.CELL, "Chose a cell to place your ship");

        int cellId = (int) this.tempInputValue;
        this.resetInputMode();
        System.out.println(">>> Starting cell : "+cellId);
        return cellId;
    }

    private void printTextInfo(String txt) {
        if (!this.textInfo[0].equals("error")){
            System.out.println(this.textInfo[1] + " TO " + txt);
            this.textInfo = new String[]{"info", txt};
        }
    }

    private void resetTextInfo() {
        this.textInfo = new String[]{"info", "Waiting for your turn"};
    }

    @Override
    public synchronized Command[] getCommandOrders(int playerId) {
        // this.inputState = InputState.COMMAND;
        // this.printTextInfo("Chose commands order");
        // this.displayGame();
        System.out.println("Chose commands order");

        // synchronized (this) {
        //     try {
        //         wait();
        //     } catch (InterruptedException e) {
        //         throw new RuntimeException(e);
        //     }
        // }

        this.waitInput(InputState.COMMAND, "Chose commands order");

        Command[] commands = (Command[]) this.tempInputValue;
        this.resetInputMode();
        // this.displayGame();
        System.out.println(Arrays.toString(commands));
        return commands;
    }

    @Override
    public synchronized int[][] expand(int playerId, int nShips) {
        System.out.println("Expand");
        ArrayList<int[]> ships = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        int nShipsMax = Math.min(nShips, player.getNumberAvailableShips());
        while (nShipsMax > 0) {
            int nShipsOnCell;
            int cellId;
            this.skipable = true;
            this.waitInput(InputState.CELL, "Chose a cell to expand");
            this.skipable = false;
            cellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (cellId == -1) {
                break;
            }
            if (this.game.getArea().getCell(cellId).getSystem() == null) {
                this.displayError("You can't expand on a cell without a system", playerId);
                continue;
            }
            if (this.game.getArea().getCell(cellId).getOwner() != player) {
                this.displayError("You can't expand on a cell you don't own", playerId);
                continue;
            }
            if (nShipsMax == 1) {
                nShipsOnCell = 1;
            } else {
                this.tempMaxNumber = nShipsMax;
                this.waitInput(InputState.NUMBER, "Chose the number of ships to place on this cell");
                nShipsOnCell = (int) this.tempInputValue;
                this.resetInputMode();
                Ship[] temp = this.game.getPlayer(this.playerId).getAvailableShips(nShipsOnCell);
                for (Ship s : temp) {
                    s.setCell(this.game.getArea().getCell(cellId));
                }
            }
            ships.add(new int[]{cellId, nShipsOnCell});
            nShipsMax -= nShipsOnCell;
        }

        this.resetInputMode();
        System.out.println(">>> Expanded ships : "+Arrays.deepToString(ships.toArray(new int[0][0])));
        return ships.toArray(new int[0][0]);
    }

    @Override
    public synchronized int[][][] explore(int playerId, int nFleet) {
        System.out.println("Explore");
        List<List<int[]>> input = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        Area area = this.game.getArea();
        while (nFleet > 0) {
            List<Ship> fleetShips = new ArrayList<>();
            List<int[]> fleet = new ArrayList<>();
            int startCellId, nShips, destCellId;
            this.skipable = true;
            this.waitInput(InputState.CELL, "Chose a cell to start the exploration");
            this.skipable = false;
            startCellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (startCellId == -1) {
                break;
            }
            if (area.getCell(startCellId).getOwner() != player) {
                this.displayError("You can't explore from a cell you don't own", playerId);
                continue;
            }

            this.tempMaxNumber = area.getCell(startCellId).getAvailableShipsCount();
            if (this.tempMaxNumber == 0) {
                this.displayError("You don't have any ships on this cell you can move", playerId);
                continue;
            }
            this.waitInput(InputState.NUMBER, "Chose the number of ships to move");
            nShips = (int) this.tempInputValue;
            this.resetInputMode();

            this.waitInput(InputState.CELL, "Chose a destination for the fleet");
            destCellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (area.getCell(destCellId).getOwner() != player && area.getCell(destCellId).getOwner() != null) {
                this.displayError("This cell is already owned by another player", playerId);
                continue;
            }
            if (area.getCell(startCellId).distance(area.getCell(destCellId), 2) != 1) {
                this.displayError("The destination cell is not a neighbor of the starting cell", playerId);
                continue;
            }

            // move the ships
            fleetShips.addAll(Arrays.asList(area.getCell(startCellId).getAvailableShips(nShips)));
            for (Ship s : fleetShips) {
                s.setCell(area.getCell(destCellId));
                s.setUsed(true);
            }
            this.displayGame();

            fleet.add(new int[]{startCellId, nShips, destCellId});


            //second move of the fleet
            int nShips2, destCellId2;
            this.tempMaxNumber = area.getCell(destCellId).getAvailableShipsCount();
            this.skipable = true;
            this.waitInput(InputState.NUMBER, "How many ships do you want to add to the fleet?");
            this.skipable = false;

            nShips2 = (int) this.tempInputValue;
            this.resetInputMode();

            if (nShips2 == -1) {
                input.add(fleet);
                nFleet--;
                continue;
            }

            while (true) {
                this.waitInput(InputState.CELL, "Chose a destination for the fleet");
                destCellId2 = (int) this.tempInputValue;
                this.resetInputMode();
                if (area.getCell(destCellId2).getOwner() != player && area.getCell(destCellId2).getOwner() != null) {
                    this.displayError("This cell is already owned by another player", playerId);
                    continue;
                }
                if (area.getCell(destCellId).distance(area.getCell(destCellId2), 2) != 1) {
                    this.displayError("The destination cell is not a neighbor of the starting cell", playerId);
                    continue;
                }
                break;
            }

            // move the ships
            fleetShips.addAll(Arrays.asList(area.getCell(destCellId).getAvailableShips(nShips2)));
            for (Ship s : fleetShips) {
                s.setCell(area.getCell(destCellId2));
                s.setUsed(true);
            }
            this.displayGame();

            fleet.add(new int[]{destCellId, nShips2, destCellId2});

            input.add(fleet);
            nFleet--;
        }

        this.resetInputMode();

        int [][][] result = new int[input.size()][][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).toArray(new int[0][0]);
        }
        System.out.println(">>> Explored ships : "+Arrays.deepToString(result));
        return result;
    }

    @Override
    public synchronized int[][] exterminate(int playerId, int nSystem) {
        System.out.println("Exterminate");
        List<List<Integer>> input = new ArrayList<>();
        Player player = this.game.getPlayer(playerId);
        Area area = this.game.getArea();

        while (nSystem > 0) {
            List<Integer> currentAttack = new ArrayList<>();
            int cellId, id, nShips;

            this.skipable = true;
            this.waitInput(InputState.CELL, "Chose a cell to exterminate");
            this.skipable = false;
            cellId = (int) this.tempInputValue;
            this.resetInputMode();
            if (cellId == -1) {
                break;
            }
            if (area.getCell(cellId).getOwner() == player || area.getCell(cellId).getOwner() == null) {
                this.displayError("You need to exterminate an enemies cell", playerId);
                continue;
            }
            if (area.getCell(cellId).getSystem() == null) {
                this.displayError("You can't exterminate from a cell without a system", playerId);
                continue;
            }
            Cell attackedCell = area.getCell(cellId);
            List<Ship> attackingShips = new ArrayList<>();
            List<Ship> attackedShips = new ArrayList<>(Arrays.asList(attackedCell.getShips()));

            while(true) {
                this.skipable = true;
                this.waitInput(InputState.CELL, "Chose a cell to attack from");
                this.skipable = false;
                id = (int) this.tempInputValue;
                this.resetInputMode();

                if (id == -1) {;
                    break;
                }

                if (area.getCell(id).getOwner() != player) {
                    this.displayError("You can't attack from a cell you don't own", playerId);
                    continue;
                }
                if (area.getCell(cellId).distance(area.getCell(id), 2) == null) {
                    this.displayError("The destination cell is not a neighbor of the starting cell", playerId);
                    continue;
                }

                this.tempMaxNumber = area.getCell(id).getAvailableShipsCount();
                this.waitInput(InputState.NUMBER, "How many ships do you want to send?");
                nShips = (int) this.tempInputValue;
                this.resetInputMode();
                if (nShips == 0) {
                    continue;
                }
                if (currentAttack.size() == 0) {
                    currentAttack.add(cellId);
                }

                attackingShips.addAll(Arrays.asList(area.getCell(id).getAvailableShips(nShips)));
                currentAttack.add(id);
                currentAttack.add(nShips);

                //move the ships
                while (attackingShips.size() > 0 && attackedShips.size() > 0) {
                    attackingShips.getFirst().setCell(null);
                    Ship attackingShip = attackingShips.removeFirst();
                    attackedShips.getFirst().setCell(null);
                    Ship attackedShip = attackedShips.removeFirst();
                }
                if (attackingShips.size() > 0) {
                    for (Ship s : attackingShips) {
                        s.setCell(attackedCell);
                        s.setUsed(true);
                    }
                }
                this.displayGame();
            }
            if (currentAttack.size() > 0) {
                input.add(currentAttack);
                nSystem--;
            }

            // nSystem--;
        }


        this.resetInputMode();
        int[][] result = new int[input.size()][];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        System.out.println(">>> Exterminated ships : "+Arrays.deepToString(result));
        return result;
    }

    @Override
    public synchronized int score(int id) {
        // this.inputState = InputState.CELL;
        System.out.println("Scoring a sector");
        List<Sector> scorableSectors = this.game.getScorablesSectors();
        List<Integer> scorableSectorIds = scorableSectors.stream().map(Sector::getId).collect(Collectors.toList());
        // this.printTextInfo("Chose a sector to score: "+scorableSectorIds.toString());
        // this.displayGame();

        // try {
        //     this.wait();
        // } catch (InterruptedException e) {
        //     throw new RuntimeException(e);
        // }
        this.waitInput(InputState.CELL, "Chose a sector to score: "+scorableSectorIds.toString());

        int cellId = (int) this.tempInputValue;
        Sector sector = this.game.getArea().getCell(cellId).getSector();
        int sectorId = -1;
        if (sector != null) {
            sectorId = this.game.getArea().getCell(cellId).getSector().getId();
        }

        this.resetInputMode();
        System.out.println(">>> Scored sector : " + sectorId);
        return sectorId;
    }

    @Override
    public synchronized void displayWinner(int[] winnersIds) {
        this.state = State.END;
        StringBuilder txt = new StringBuilder("The winners are: ");
        for(int i=0; i<winnersIds.length; i++) {
            Player p = this.game.getPlayer(winnersIds[i]);
            txt.append(p.getName());
            if (i!=winnersIds.length-1) {
                txt.append(" & ");
            }
        }

        this.textInfo = new String[] {"info", txt.toString()};
        this.displayGame();
    }

    @Override
    public synchronized void displayDraw() {
        this.state = State.END;
        this.textInfo = new String[] {"info", "It's Draw !!!!"};
        this.displayGame();
    }

    public synchronized void setPlayerId(int playerId) {
        this.playerId = playerId;
        this.state = State.GAME;
    }
}
