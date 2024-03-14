import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;

public class Asset {
    private ImageIcon img1;
    private ImageIcon img2;
    private ImageIcon option;
    private int x, y, width, height;
    private double v_x = 0, v_y = 0, a_x = 0, a_y = .0005;

    public Asset(String path1, String path2, int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        img1 = new ImageIcon(path1);
        img2 = new ImageIcon(path2);
        option = img1;
    }

    public void paint(Graphics g, Panel p){
        g.drawImage(option.getImage(), x, y, width, height, p);
    }

    public void setX(int x){
        this.x = x;
    }

    public int getX(){
        return x;
    }

    public boolean isTouching(int mouse_x, int mouse_y){
        return x <= mouse_x && mouse_x <= (x + width) && y <= mouse_y && mouse_y <= (y + height);
    }

    public void changeImg(){
        if(option.equals(img1)){
            option = img2;
        }else{
            option = img1;
        }
        try{
            AudioInputStream s = AudioSystem.getAudioInputStream(new File("src/res/sound/sound1.wav"));
            Clip c = AudioSystem.getClip();
            c.open(s);
            c.start();
            s.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void update(int delay, int width, int height){
        //Note delay is 16 ms
        y += v_y * delay;
        x += v_x * delay;
        v_y += a_y * delay;
        v_x += a_x * delay;

        if(x <= 0){
            v_x = -1 * v_x;
            x = 0;
        }else if((x + this.width) >= width){
            v_x = -1 * v_x;
            x = width - this.width;
        }
        if(y <= 0){
            v_y = -1 * v_y;
            y = 0;
        }else if((y + this.height) >= height){
            v_y = -1 * v_y;
            y = height - this.height;
        }
    }
}
