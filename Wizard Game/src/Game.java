
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Game extends Canvas implements Runnable
{

    private static final long serialVersionUID = 1L;

    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private Camera camera;
    private SpriteSheet ss;

    private BufferedImage level = null;
    private BufferedImage sprite_sheet = null;
    private BufferedImage floor = null;

    public int ammo = 100;
    public int hp = 100;
    public int enemyCount = 0;
    public int score = 100;
    public boolean gameOver = false;
    public boolean scoreSaved = false;

    Path p = Paths.get("test.json");

    Window window;

    public Game()
    {
        window = new Window(1000, 563, "Wizard Game", this);
        start();

        handler = new Handler();
        camera = new Camera(0, 0);
        this.addKeyListener(new KeyInput(handler, this, window));

        BufferedImageLoader loader = new BufferedImageLoader();
        level = loader.loadImage("res/wizard_level.png");
        sprite_sheet = loader.loadImage("res/sprite_sheet.png");

        ss = new SpriteSheet(sprite_sheet);

        floor = ss.grabImage(4, 2, 32, 32);

        this.addMouseListener(new MouseInput(handler, camera, this, ss));

        loadLevel(level);
    }

    public void restart()
    {
        stop();
        window.close();
        new Game();
    }

    private void start()
    {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop()
    {
        isRunning = false;
        try
        {
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (isRunning)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1)
            {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000)
            {
                timer += 1000;
                frames = 0;
            }
        }
    }

    public void tick()
    {
        for (int i = 0; i < handler.object.size(); i++)
        {
            if (handler.object.get(i).getId() == ID.Player)
            {
                camera.tick(handler.object.get(i));
            }
        }
        handler.tick();
    }

    public void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;
        ///////////////////////////////////

        g2d.translate(-camera.getX(), -camera.getY());

        for (int xx = 0; xx < 30 * 72; xx += 32)
        {
            for (int yy = 0; yy < 30 * 72; yy += 32)
            {
                g.drawImage(floor, xx, yy, null);
            }
        }

        handler.render(g);

        g2d.translate(camera.getX(), camera.getY());

        g.setColor(Color.gray);
        g.fillRect(5, 5, 200, 32);
        g.setColor(Color.green);
        g.fillRect(5, 5, hp * 2, 32);
        g.setColor(Color.black);
        g.drawRect(5, 5, 200, 32);

        g.setColor(Color.white);
        g.drawString("Ammo: " + ammo, 5, 50);
        g.drawString("Enemy count:" + enemyCount, 5, 65);

        if (gameOver)
        {
            if (hp <= 0)
            {
                g.drawString("Game over press R to restart", 10, 25);
            }

            if (enemyCount <= 0)
            {
                g.drawString("You won/n", 220, 15);
            }
            g.drawString("Your score: " + score, 220, 30);
            saveScore();

        }
        ///////////////////////////////////
        g.dispose();
        bs.show();

    }

    //loading the level
    private void loadLevel(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        for (int xx = 0; xx < w; xx++)
        {
            for (int yy = 0; yy < h; yy++)
            {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255)
                {
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, ss));
                }

                if (blue == 255 && green == 0)
                {
                    handler.addObject(new Wizard(xx * 32, yy * 32, ID.Player, handler, this, ss));
                }

                if (green == 255 && blue == 0)
                {
                    handler.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy, handler, ss, this));
                    enemyCount++;
                }

                if (green == 255 && blue == 255)
                {
                    handler.addObject(new Crate(xx * 32, yy * 32, ID.Crate, ss));
                }
            }
        }
    }

    private void saveScore()
    {
        if (!scoreSaved)
        {
            JSONObject obj = new JSONObject();
            obj.put("score", score);
            try
            {
                BufferedWriter writer;
                if (Files.exists(p))
                {
                    writer = Files.newBufferedWriter(p, StandardOpenOption.APPEND);
                } else
                {
                    writer = Files.newBufferedWriter(p, StandardOpenOption.CREATE);
                }
                writer.write(obj.toJSONString() + "\r\n");
                writer.close();
                displayScore();

                scoreSaved = true;
            } catch (IOException ie)
            {
                ie.printStackTrace();
            }
        }
    }

    private void displayScore()
    {
        try
        {
            ArrayList<JSONObject> json = new ArrayList<>();
            JSONObject obj;
            ArrayList<Integer> scoreList = new ArrayList<>();

            List<String> scores = Files.readAllLines(p);
            for (String s : scores)
            {
                try
                {
                    obj = (JSONObject) new JSONParser().parse(s);
                    json.add(obj);
                    scoreList.add((int) (long)  obj.get("score"));
                } catch (ParseException pe)
                {
                    pe.printStackTrace();
                }
            }
            Collections.sort(scoreList);
            Collections.reverse(scoreList);
            int i = 1;
            
            for (Integer score : scoreList)
            {
                if(i <= 10)
                {
                 System.err.println(score);
                 i++;
                }
                
            }
        } catch (IOException ie)
        {
            ie.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new Game();
    }

}
