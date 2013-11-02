package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import restaurant.interfaces.Waiter;
/**
 * Restaurant Host Agent
 */

public class CashierAgent extends Agent implements Cashier{
	public List<Waiter> waiters = new ArrayList<Waiter>();
	public List<Order> receipts = new ArrayList<Order>();
	public List<Payment> payments = new ArrayList<Payment>();
	private int money=1000000;
	HashMap<String, Integer> menu = new HashMap<String, Integer>();
	{
	    menu.put("beef", 15);
    	menu.put("chicken", 10);
    	menu.put("lamb", 5);
	}
	private String name; 
	public WaiterGui waiterGui = null;
	public class Payment
	{
		Customer customer;
		int money;
		int bill;
		paymentState state;
		Payment(Customer customer, int money, int bill, paymentState state)
		{
			this.customer=customer;
			this.money=money;
			this.bill=bill;
			this.state=state;
		}
	}
	public class Order
	{
		Waiter waiter;
		String choice;
		int tableNumber;
		receiptState state;
		Order(Waiter waiter, String choice, int tableNumber, receiptState state)
		{
			this.waiter=waiter;
			this.choice=choice;
			this.tableNumber=tableNumber;
			this.state=state;
		}
	}
	enum receiptState
	{pending, complete};
	enum paymentState
	{pending, complete};
	public List<Order> orders = new ArrayList<Order>();
	boolean incomplete=false;
	boolean ordering=false;
	public enum OrderState
	{nothing};



	
	//Messages
	
	public void msgComputeCheck(Waiter waiter, String choice, int tableNumber)
	{
		Order order = new Order(waiter, choice, tableNumber, receiptState.pending);
		orders.add(order);
		stateChanged();
	}
	
	public void msgPayBill(Customer customer, int money, int bill)
	{
		Payment payment = new Payment(customer, money, bill, paymentState.pending);
		payments.add(payment);
		stateChanged();
	}
	
	
	public void msgHereIsMarketBill(Market market, int bill, HashMap<String, Integer> outgoingList)
	{
		if (money>=bill)
		{
			print ("the cashier has enough money to pay " + market.getName() + " for the order");
			money-=bill;
			print ("the cashier pays the market $" + bill  + " for " + outgoingList);
			print ("the cashier now has $" + money);
			market.msgHereIsPayment(bill);
		}
		if (money<bill)
		{
			print ("the cashier does not have enough money to pay " + market.getName() + " for " + outgoingList + ". The cashier paid all he could");
			bill=bill-money;
			money=0;
			print ("the cashier now has $" + money);
			market.msgHereIsPayment(bill);
		}
		stateChanged();
	}
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		for (Order order : orders) 
		{	
			if (order.state==receiptState.pending)
			{
				order.state=receiptState.complete;
				CalculateReceipt(order);		
				return true;
			}
		}
		for (Payment payment : payments)
		{
			if (payment.state==paymentState.pending)
			{
				payment.state=paymentState.complete;
				GiveChange(payment);
				return true;
			}
		}
		return false;
	}


	// Actions

	private void CalculateReceipt(Order order)
	{
		int bill = menu.get(order.choice);
		order.waiter.msgHereIsReceipt(bill, order.tableNumber);
	}
	
	private void GiveChange(Payment payment)
	{
		int change = payment.money-payment.bill;
		if (change < 0)
		{
			print("customer did not have enough money to pay for meal");
		}
		payment.customer.msgHereIsChange(change);
	}
	
	


	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	public void setWaiter(WaiterAgent waiter)
	{
		waiters.add(waiter);
	}
	
}

