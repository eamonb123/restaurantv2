package restaurant.gui;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.MarketAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Host Sarah", this);
    private CookAgent cook = new CookAgent();
    private CashierAgent cashier = new CashierAgent();
//    private WaiterAgent waiter = new WaiterAgent("Matt");
//    private WaiterGui waiterGui = new WaiterGui(waiter);
    private boolean paused;
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();


    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();
    private JPanel biggerGroup = new JPanel();
    private JButton pause = new JButton("pause");
    private JButton restart = new JButton("restart");
    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;

        markets.add(new MarketAgent("Market 1"));
        markets.add(new MarketAgent("Market 2"));
        markets.add(new MarketAgent("Market 3"));
        
        CookGui cookGui = new CookGui(cook, gui);
        gui.animationPanel.addGui(cookGui);
        cook.setGui(cookGui);
        
        for(MarketAgent market: markets)
        {
        	cashier.setMarket(market);
        	market.setCashier(cashier);
        	cook.setMarket(market);
        	market.startThread();
        }
        
        host.startThread();
        cook.startThread();
        cashier.startThread();
        cook.InitializeAreas();
//        cook.CheckInitialFood();
        
        
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));
        biggerGroup.setLayout(new BoxLayout(biggerGroup, BoxLayout.LINE_AXIS));
        
        group.add(customerPanel);
        group.add(waiterPanel);
        biggerGroup.add(group);
        biggerGroup.add(pause);
        biggerGroup.add(restLabel);
        add(biggerGroup);
        
        pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
	        	if (e.getSource() == pause) 
	        	{
	        		if (!paused)
	        		{
	        			paused=true;
	        			for(CustomerAgent customer: customers)
	        			{
	    	    	        customer.pause();
	        			}
	    		        for (WaiterAgent waiter: waiters)
	    		        {
	    	    	        waiter.pause();
	    	    	    }
	    		        for (MarketAgent market: markets)
	    		        {
	    	    	        market.pause();
	    	    	    }
	    		        host.pause();
	    		        cook.pause();
	        		}
	        		else
	        		{
	        			paused=false;
	        			for(CustomerAgent customer: customers)
	        			{
	        				customer.restart();
	        			}
	    		        for (WaiterAgent waiter: waiters)
	    	    	    {
	    		        	waiter.restart();
	    	    	    }
	    		        for (MarketAgent market: markets)
	    		        {
	    	    	        market.restart();
	    	    	    }
	    		        host.restart();
	    		        cook.restart();
	        		}
	        	}
            }
        }); 
        
        initRestLabel();
        //RESTLABEL IS THE MENU
        //add(restLabel);
       // add(pause);
    }

    
    

    
    
    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Beef</td><td>$15.00</td></tr><tr><td>Chicken</td><td>$10.00</td></tr><tr><td>Lamb</td><td>$5.00</td>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if(type.equals("Waiters")) {
        	for(int i=0; i < waiters.size(); i++) {
        		WaiterAgent w = waiters.get(i);
        		if(w.getName()== name)
        			gui.updateInfoPanel(w);
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, JCheckBox checkbox, String name) {
    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);
    		if (checkbox.isSelected())
    		{
    			g.setHungry();
    		}
    		gui.animationPanel.addGui(g);
    		c.setHost(host);
    		c.setGui(g);
    		c.setCashier(cashier);
    		customers.add(c);
    		c.startThread();
    	}
    	if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name);	
    		WaiterGui g = new WaiterGui(w, gui);
    		if (checkbox.isSelected())
    		{
    			System.out.println("checkbox is selected");
//    			w.setHungry();
    		}
    		gui.animationPanel.addGui(g);
    		host.setWaiter(w);
    		cashier.setWaiter(w);
    		host.msgWakeUp();
    		w.setHost(host);
    		w.setGui(g);
    		w.setCashier(cashier);
    		w.setCook(cook);
    		waiters.add(w);
    		w.startThread();
    	}
    }
}
