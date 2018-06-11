
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter
{

    Handler handler;
    Game game;
    Window window;

    public KeyInput(Handler handler, Game game, Window window)
    {
        this.handler = handler;
        this.game = game;
        this.window = window;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();

        for (int i = 0; i < handler.object.size(); i++)
        {
            GameObject tempobObject = handler.object.get(i);

            if (tempobObject.getId() == ID.Player)
            {
                if (key == KeyEvent.VK_W)
                {
                    handler.setUp(true);
                }
                if (key == KeyEvent.VK_S)
                {
                    handler.setDown(true);
                }
                if (key == KeyEvent.VK_A)
                {
                    handler.setLeft(true);
                }
                if (key == KeyEvent.VK_D)
                {
                    handler.setRight(true);
                }
            }
        }
        if (key == KeyEvent.VK_R)
        {
            game.restart();
        }
        if (key == KeyEvent.VK_ESCAPE)
        {
            game.stop();
            window.close();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();

        for (int i = 0; i < handler.object.size(); i++)
        {
            GameObject tempobObject = handler.object.get(i);

            if (tempobObject.getId() == ID.Player)
            {
                if (key == KeyEvent.VK_W)
                {
                    handler.setUp(false);
                }
                if (key == KeyEvent.VK_S)
                {
                    handler.setDown(false);
                }
                if (key == KeyEvent.VK_A)
                {
                    handler.setLeft(false);
                }
                if (key == KeyEvent.VK_D)
                {
                    handler.setRight(false);
                }
            }
        }
    }
}
