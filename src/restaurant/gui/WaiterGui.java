package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui.Command;

import java.awt.*;
import java.util.*;

public class WaiterGui implements Gui {
    private WaiterAgent waiter = null;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private int xPosition=100;
    private int yPosition=250;
    
    public static final int xTable = 200;
    public static final int yTable = 250;

    public WaiterGui(WaiterAgent agent) {
    	this.waiter = agent;
    }
    
    

    public void updatePosition() {
    	if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;
        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        if (xPos == xDestination && yPos == yDestination) 
        {
        	//waiter.msgAtTable(xDestination, yDestination);
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer) {
        xDestination = xTable + 20;
        yDestination = yTable - 20;
    }
   
    
//	//MAPPING TABLE NUMBERS TO COORDINATES ON THE GUI
//    HashMap<Integer, Point> tableMap = new HashMap<Integer, Point>();
//    {
//    	for (int i=1; i<=3; i++)
//    	{
//    		Point location = new Point(xPosition, yPosition);
//    		tableMap.put(i,location);
//    		xPosition+=150;
//    	}
//    }
    
    public void DoSeatCustomer(Point location)
    {
		xDestination = location.x;
		yDestination = location.y;
    }    
    
    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
