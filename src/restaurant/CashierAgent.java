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
	public List<Waiter> waiters = Collections.synchronizedList(new ArrayList<Waiter>());
	public List<Order> receipts = Collections.synchronizedList(new ArrayList<Order>());
	public List<Payment> payments = Collections.synchronizedList(new ArrayList<Payment>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());

	private int money=100;
	Map<String, Integer> menu = Collections.synchronizedMap(new HashMap<String, Integer>());
	{
	    menu.put("beef", 15);
    	menu.put("chicken", 10);
    	menu.put("lamb", 5);
	}
	private String name; 
	public WaiterGui waiterGui = null;
	public class MarketBill
	{
		Market market;
		int bill;
		boolean paid = true;
		Map<String, Integer> groceryList = Collections.synchronizedMap(new HashMap<String, Integer>());
		MarketBill(Market market, int bill, Map<String, Integer> groceryList)
		{
			this.market=market;
			this.bill=bill;
			this.paid=true;
			this.groceryList=groceryList;
		}
	}
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
	
	
	public void msgHereIsMarketBill(Market market, int bill, Map<String, Integer> outgoingList)
	{
		synchronized(marketBills)
		{
		for (MarketBill marketBill: marketBills)
		{
			if (marketBill.market.equals(market))
			{
				marketBill.bill=bill;
				marketBill.paid=false;
				marketBill.groceryList=outgoingList;
			}
		}
		}
		stateChanged();
	}
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		synchronized(orders)
		{
		for (Order order : orders) 
		{	
			if (order.state==receiptState.pending)
			{
				order.state=receiptState.complete;
				CalculateReceipt(order);		
				return true;
			}
		}
		}
		synchronized(payments)
		{
		for (Payment payment : payments)
		{
			if (payment.state==paymentState.pending)
			{
				payment.state=paymentState.complete;
				GiveChange(payment);
				return true;
			}
		}
		}
		synchronized(marketBills)
		{
		for (MarketBill marketBill: marketBills)
		{
			if (!marketBill.paid)
			{
				PayMarketBill(marketBill);
				return true;
			}
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
			money+=payment.money;
		}
		else
		{
			money+=payment.bill;
		}
		PayDebt();
		print("the cashier now has $" + money);
		payment.customer.msgHereIsChange(change);
	}
	
	private void PayDebt()
	{
		synchronized(marketBills)
		{
		for (MarketBill marketBill: marketBills)
		{
			if (marketBill.bill!=0)
			{
				if (money>marketBill.bill)
				{
					print("cashier paid " + marketBill.bill +  " to pay off " + marketBill.market.getName() + "s debt in full");
					marketBill.market.msgHereIsPayment(marketBill.bill);
					money-=marketBill.bill;
					marketBill.bill=0;
				}
				else if (money > 0)
				{
					print("cashier paid only some of his debt to " + marketBill.market.getName());
					print("cashier pays " + money + " to " + marketBill.market.getName());
					marketBill.bill-=money;
					marketBill.market.msgHereIsPayment(money);
					money=0;
				}
				else if (money==0)
				{
				}
			}
		}
		}
	}
	
	private void PayMarketBill(MarketBill marketBill)
	{
		if (money>=marketBill.bill)
		{
			int moneyBack = marketBill.bill;
			print ("the cashier has enough money to pay " + marketBill.market.getName() + " for the order");
			money-=marketBill.bill;
			print ("the cashier pays the market $" + marketBill.bill  + " for " + marketBill.groceryList);
			marketBill.bill=0;
			print ("the cashier now has $" + money);
			marketBill.market.msgHereIsPayment(moneyBack);
		}
		else if (money<marketBill.bill)
		{
			int moneyBack=money;
			print ("the cashier does not have enough money to pay " + marketBill.market.getName() + " for " + marketBill.groceryList + ". The cashier paid all he could");
			marketBill.bill-=money;
			print(marketBill.market.getName() + " bill is still $" + marketBill.bill);
			money=0;
			print ("the cashier now has $" + money);
			marketBill.market.msgHereIsPayment(moneyBack);
		}
		marketBill.paid=true;
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
	
	public void setMarket(Market market)
	{
		marketBills.add(new MarketBill(market, 0, null));
	}
}

