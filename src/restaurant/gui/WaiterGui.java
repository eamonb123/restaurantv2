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
        	waiter.msgAtTable();
        }
    }

    public void goToHome()
    {
    	xDestination=40;
    	yDestination=40;
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
