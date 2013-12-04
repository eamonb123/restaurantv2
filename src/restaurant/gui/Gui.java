package restaurant.gui;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public interface Gui {

    public void updatePosition();
    public void draw(Graphics2D g2);
    public boolean isPresent();

}
