package fr.utt.lo02.app;

import fr.utt.lo02.RmiServer.GUIIOHandler;
import fr.utt.lo02.core.Game;
import fr.utt.lo02.core.Player;
import fr.utt.lo02.core.components.Command;
import fr.utt.lo02.data.DataManipulator;
import fr.utt.lo02.data.GameDataConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

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

    public GUI() {
        // init the frame
        this.frame = new JFrame("Pocket Imperium");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setGameInstance(Game game) {
        this.game = game;
        this.displayGame();
    }

    private void displayCommand() {

    }

    public void displayGame() {
        this.frame.getContentPane().removeAll();
        this.state = State.GAME;

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

    private void displayTopPanel(JPanel gamePanel) {
        if (this.inputState == InputState.IDLE || this.inputState == InputState.CELL) {
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
            label.setBounds(10,10,this.w-20, this.panelHeight-20);
            gamePanel.add(label);
            JLabel panelColor = new JLabel("", SwingConstants.CENTER);
            panelColor.setOpaque(true);
            panelColor.setBackground(this.playersColor[this.playerId]);
            panelColor.setBounds(0,0, this.w, this.panelHeight);
            gamePanel.add(panelColor);
        }
    }

    private void displayShips(JPanel gamePanel) {
        for (int id : coords.keySet()){
            int nb = this.game.getArea().getCell(id).getShips().length;
            if (nb == 0) {
                continue;
            }

            JLabel centerLabel = new JLabel(String.valueOf(nb), SwingConstants.CENTER);
            centerLabel.setOpaque(false);
            centerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            centerLabel.setForeground(this.playersColor[this.game.getArea().getCell(id).getOwner().getId()]);
            int x = coords.get(id)[0];
            int y = coords.get(id)[1] + this.panelHeight;
            centerLabel.setBounds(x-7,y-10, 15, 20);
            gamePanel.add(centerLabel);
        }
    }

    public void displayForm() {
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
        gui.displayWinner(new int[]{0,1});
    }

    public String getIp() {
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
    public void displayError(String message, int playerId) {
        if (playerId != this.playerId) {
            return;
        }
        this.textInfo = new String[]{"error", message};
        this.displayGame();
    }

    @Override
    public int getStartingCellId(int playerId) {
        this.inputState = InputState.CELL;
        this.printTextInfo("Chose a cell to place your ship");
        System.out.println("Chose a starting cell");
        this.displayGame();

        // wait for player input
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        int cellId = (int) this.tempInputValue;
        this.inputState = InputState.IDLE;
        this.resetTextInfo();
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
    public HashMap<Integer, Command[]> getCommandOrders() {
        // TODO
        return null;
    }

    @Override
    public int[][] expand(int playerId, int nShips) {
        // TODO
        return new int[0][];
    }

    @Override
    public int[][][] explore(int playerId, int nFleet) {
        // TODO
        return new int[0][][];
    }

    @Override
    public int[][] exterminate(int playerId, int nFleet) {
        // TODO
        return new int[0][];
    }

    @Override
    public int score(int id) {
        // TODO
        return 0;
    }

    @Override
    public void displayWinner(int[] winnersIds) {
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
    public void displayDraw() {
        this.state = State.END;
        this.textInfo = new String[] {"info", "It's Draw !!!!"};
        this.displayGame();
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
