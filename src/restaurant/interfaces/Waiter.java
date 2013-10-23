package restaurant.interfaces;

import java.awt.Point;
import java.util.List;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent.CustomerState;
import restaurant.WaiterAgent.WaiterState;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Waiter {
	public abstract void msgAtTable();
	
	public abstract void msgTryToGoOnBreak();
	
	public abstract void msgYouCannotBreak();
	
	public abstract void msgYouCanBreak();
	
	public abstract void msgPleaseSeatCustomer(Customer customer, int tableNumber, Point loc);
	
	public abstract void msgReadyToOrder(Customer customer);
	
	public abstract void msgHereIsChoice(Customer customer);
	
	public abstract void msgOutOfFood(String choice, int tableNumber);
	
	public abstract void msgOrderIsReady(String choice, int tableNumber);
	
	public abstract void msgDoneEating(Customer customer);
	
	public abstract void msgHereIsReceipt(int bill, int tableNumber);

}