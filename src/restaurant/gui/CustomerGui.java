package restaurant.gui;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.CustomerAgent.AgentEvent;

import java.awt.*;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class CustomerGui implements Gui{

	private CustomerAgent customer = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private int xPosition=100;
	private int yPosition=250;
	Point cashierLocation = new Point(520, 100);
	private boolean isMoving=false;
	public String text="";
	public String order;
	public int bill;

	//private HostAgent host;
	RestaurantGui gui;

	public int xPos, yPos;
	public int xDestination, yDestination;
	public enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public CustomerGui(CustomerAgent c, RestaurantGui gui){ //HostAgent m) {
		customer = c;
		xPos = 0;
		yPos = 0;
		xDestination = 20;
		yDestination = 20;
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
		else if (xPos == xDestination && yPos == yDestination && isMoving==true)
		{
			customer.msgSemaphoreRelease();
			isMoving=false;
		}
	}

	public void draw(Graphics2D g) {
    	ImageIcon customer = new ImageIcon("images/restaurantE_customer.png");
    	Image image = customer.getImage();
    	g.drawImage(image, xPos, yPos, null);
	}
	
    public void drawOrder(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
    	g.drawString(order, xPos, yPos);
    }
    
    public void eatingOrder(Graphics2D g, String order) {
        g.setColor(Color.RED);
    	g.drawString(order, xPos, yPos);
    }
    

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		customer.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	//MAPPING TABLE NUMBERS TO COORDINATES ON THE GUI
    HashMap<Integer, Point> tableMap = new HashMap<Integer, Point>();
    {
    	for (int i=1; i<=3; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		tableMap.put(i,location);
    		xPosition+=150;
    	}
    }
	
	public void DoGoToSeat(Point location) 
	{
		isMoving=true;
		xDestination = location.x;
		yDestination = location.y;
		command = Command.GoToSeat;
	}
	
  public void DoGoToCashier()
  {
	  isMoving=true;
	  xDestination = cashierLocation.x;
	  yDestination = cashierLocation.y;
  }

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
