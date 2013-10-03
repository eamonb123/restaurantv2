package restaurant.gui;

import javax.swing.*;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {
	private WaiterGui waiterGui;
	private CustomerGui customerGui;
    private final int WINDOWX = 450;
    private final int WINDOWY = 350;
    private int xPos=100;
    private int yPos=250;
    int NTABLES = 3;
    private int Width=50;
    private int Height=50;
    private Image bufferImage;
    private Dimension bufferSize;
    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        bufferSize = this.getSize();
        Timer timer = new Timer(10, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        int xPosUpdated=xPos;
        //Here is the table
        for (int i=1; i<=NTABLES; i++) //CREATING GUI TABLES
        {
	        g2.setColor(Color.ORANGE);
	        g2.fillRect(xPosUpdated, yPos, Width, Height);//200 and 250 need to be table params
	        xPosUpdated+=150;
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
            	gui.draw(g2); //
            	//g2.drawString("hello", 30, 30);
                Graphics2D stringOrder = (Graphics2D)g;
            	if (gui instanceof WaiterGui)
            	{
            		WaiterGui waiterGui = (WaiterGui) gui;
            		if (waiterGui.deliveringFood)
            		{
            			waiterGui.drawOrder(stringOrder, waiterGui.order);
            		}
            	}
            	else if (gui instanceof CustomerGui)
            	{
            		CustomerGui customerGui = (CustomerGui) gui;
            		if (customerGui.waitingForOrder)
            		{
            			customerGui.drawOrder(stringOrder, customerGui.order + "?");
            		}
            	}
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
}
