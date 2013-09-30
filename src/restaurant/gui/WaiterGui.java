package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui.Command;

import java.awt.*;
import java.util.*;

public class WaiterGui implements Gui {
    private WaiterAgent waiter = null;
	static int NTABLES=3;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private boolean isMoving=false;
    Point cookLocation = new Point(-20, -20);
    Point homeBase = new Point(260, 100);
    public static final int xTable = 100;
    public static final int yTable = 250;

    public WaiterGui(WaiterAgent agent) {
    	this.waiter = agent;
    }
    
    int xPosition=xTable;
    int yPosition=yTable-20;
    HashMap<Integer, Point> tableMap = new HashMap<Integer, Point>();
    {
    	for (int i=1; i<=NTABLES; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		tableMap.put(i,location);
    		xPosition+=150;
    	}
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
        if (xPos == xDestination && yPos == yDestination && isMoving==true) 
        {
        	MoveToPosition(homeBase);
        	waiter.msgAtTable();
        	isMoving=false;
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

//    public void DoBringToTable(CustomerAgent customer) {
//        xDestination = xTable + 20;
//        yDestination = yTable - 20;
//    }
   
    public void MoveToPosition(Point location)
    {
    	isMoving=true;
    	xDestination = location.x;
		yDestination = location.y;
    }
    
    public void DoSeatCustomer(Point location)
    {
    	isMoving=true;
    	xDestination = location.x;
    	yDestination = location.y-20;
    }    
    
    public void DoGoToTable(int tableNumber)
    {
    	Point location = tableMap.get(tableNumber);
    	MoveToPosition(location);
    }
    
    public void DoGiveCook()
    {
    	MoveToPosition(cookLocation);
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
