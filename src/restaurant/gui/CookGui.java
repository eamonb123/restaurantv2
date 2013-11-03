package restaurant.gui;
import restaurant.CookAgent;

import java.awt.*;
import java.util.HashMap;

public class CookGui implements Gui{

	private CookAgent cook = null;
	private boolean isPresent = true;
	public String firstPan="";
	public String secondPan="";
	public String thirdPan="";
	//private HostAgent host;
	RestaurantGui gui;
	
	public int xPos, yPos;
	public int xDestination, yDestination;
	public enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public CookGui(CookAgent c, RestaurantGui gui){ //HostAgent m) {
		cook = c;
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
		else if (xPos == xDestination && yPos == yDestination)
		{

		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
	}
	
    public void drawFirstPan(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
    	g.drawString(order, 0, 75);
    }
    
    public void drawSecondPan(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
        g.drawString(order, 0, 105);
    }
    
    public void drawThirdPan(Graphics2D g, String order) {
        g.setColor(Color.BLACK);
        g.drawString(order, 0, 135);
    }
    
    public void eatingOrder(Graphics2D g, String order) {
        g.setColor(Color.RED);
    	g.drawString(order, xPos, yPos);
    }
    

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	
}
