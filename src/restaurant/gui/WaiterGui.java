package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui.Command;

import java.awt.*;
import java.util.*;

public class WaiterGui implements Gui {
    private WaiterAgent waiter = null;
	//private HostAgent host;
	RestaurantGui gui;
	static int NTABLES=3;
    //private int xPos = -20, yPos = -20;//default waiter position
	public int xPos = -20, yPos = -20;
    private int xDestination = -20, yDestination = -20;//default start position
    public String text="";
    public boolean onBreak=false;
    public boolean isMoving=false;
    public boolean stayAtBreak=false;
    public String order;
    public int check;
    Point cookLocation = new Point(-40, 135);
    Point homeBase = new Point();
    Point customerLine = new Point(-20,-20);
    Point breakLocation = new Point(290, -20);
    Point cashierLocation = new Point(520, 100);
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

	public WaiterGui(WaiterAgent w, RestaurantGui gui){ 
		waiter = w;
		xPos = -20;
		yPos = -20;
//		xDestination = -40;
//		yDestination = -40;
		//maitreD = m;
		this.gui = gui;
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
        	if (stayAtBreak)
        	{
        		DoMoveToPosition(breakLocation);
        	}
        	else
        	{
        		DoMoveToPosition(homeBase);
        	}
        	waiter.msgAtTable();
        	isMoving=false;
        }
    }

    
    public void setHome(Point location)
    {
    	homeBase=location;
    }
    
    public void setDestination(Point location)
    {
    	xDestination = location.x;
    	yDestination = location.y;
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }
    
    public void drawOrder(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
    	g.drawString(order, xPos, yPos);
    }
    
    public void drawCustomerArea(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
    	g.drawString(order, 20, 15);
    }
    
    public void drawCookArea(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
    	g.drawString(order, 0, 150);
    }
    
    public void drawBreakArea(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
    	g.drawString(order, 260, 20);
    }
    

    public boolean isPresent() {
        return true;
    }

//    public void DoBringToTable(CustomerAgent customer) {
//        xDestination = xTable + 20;
//        yDestination = yTable - 20;
//    }
   
	public void askForBreak() 
	{
		onBreak=true;
		waiter.msgTryToGoOnBreak();
	}
	
	public void getBackToWork()
	{
		onBreak=false;
		waiter.msgBackToWork();
	}
	
	
	
    public void DoMoveToPosition(Point location)
    {
    	isMoving=true;
    	xDestination = location.x;
		yDestination = location.y;
    }
    
    public void goToHome()
    {
    	DoMoveToPosition(homeBase);
    }
    
    public void PickUpCustomer(int x, int y)
    {
    	Point loc = new Point(x, y);
    	DoMoveToPosition(loc);
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
    	DoMoveToPosition(location);
    }
    
    public void DoGoToBreakSpot()
    {
    	DoMoveToPosition(breakLocation);
    }
    
    public void DoGiveCook()
    {
    	DoMoveToPosition(cookLocation);
    }
    
    public void DoPickUpOrder(Point location)
    {
    	DoMoveToPosition(location);
    }
    
    public void DoGoToCustomer(int tableNumber, String order)
    {
    	this.order = order;
    	Point location = tableMap.get(tableNumber);
    	DoMoveToPosition(location);
    }
    
    public void WalkingToReorderingCustomer(int tableNumber, String order)
    {
    	this.order = order;
    	Point location = tableMap.get(tableNumber);
    	DoMoveToPosition(location);
    }
    
    public void DoGoToCashier()
    {
    	DoMoveToPosition(cashierLocation);
    }
    
    public void DoDeliverReceipt(int tableNumber)
    {
    	Point location = tableMap.get(tableNumber);
    	DoMoveToPosition(location);
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
