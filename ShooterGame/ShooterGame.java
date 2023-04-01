import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FPSGame extends JPanel implements KeyListener {

    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private BufferedImage crosshairImage, enemyImage;
    private int playerX = WIDTH / 2, playerY = HEIGHT / 2;
    private boolean[] keys = new boolean[256];
    private List<Enemy> enemies = new ArrayList<>();
    private int score = 0;
    private boolean isRunning = true;
    private boolean isFullscreen = true;

    public FPSGame() {
        try {

            crosshairImage = ImageIO.read(new File("Path\\crosshair.png"));//set path for the image
            enemyImage = ImageIO.read(new File("Path\\enemy.png"));//set path for the image
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setFocusable(true);
        this.addKeyListener(this);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    playerX = e.getX();
                    playerY = e.getY();
                    Iterator<Enemy> iter = enemies.iterator();
                    while (iter.hasNext()) {
                        Enemy enemy = iter.next();
                        if (enemy.contains(playerX, playerY)) {
                            iter.remove();
                            score++;
                            break;
                        }
                    }
                }
            }
        });

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void mouseMoved(MouseEvent e) {
        playerX = e.getX();
        playerY = e.getY();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.update();
            enemy.draw(g);
        }

        g.drawImage(crosshairImage, playerX - crosshairImage.getWidth() / 2, playerY - crosshairImage.getHeight() / 2, null);

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    public void update() {

        if (enemies.size() < 10) {
            enemies.add(new Enemy());
        }
    }

    public void run() {
        while (isRunning) {
            update();
            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        FPSGame game = new FPSGame();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.run();
    }

    private class Enemy {

        private int x, y;
        private int directionX, directionY;
        private final int speed;
        private final int width;
        private final int height;

        public Enemy() {
            Random random = new Random();
            x = random.nextInt(WIDTH - 50) + 25;
            y = random.nextInt(HEIGHT - 50) + 25;
            directionX = random.nextInt(3) - 1;
            directionY = random.nextInt(3) - 1;
            speed = random.nextInt(3) + 1;
            width = 40;
            height = 40;
        }

        public void update() {
            x += directionX * speed;
            y += directionY * speed;

            if (x < 0 || x > WIDTH - width) {
                directionX *= -1;
            }

            if (y < 0 || y > HEIGHT - height) {
                directionY *= -1;
            }
        }

        public void draw(Graphics g) {
            g.drawImage(enemyImage, x - width / 2, y - height / 2, null);
        }

        private boolean contains(int x, int y) {
            int margin = 10;
            return x >= this.x - margin && x <= this.x + width + margin
                    && y >= this.y - margin && y <= this.y + height + margin;
        }

    }
}
