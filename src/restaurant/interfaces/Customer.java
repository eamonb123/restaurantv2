package restaurant.interfaces;

import java.awt.Point;
import java.util.List;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	
	
	
	public abstract void gotHungry();
	
	public abstract void msgSemaphoreRelease();
	
	public abstract void msgFollowMeToTable(Waiter waiter, List<String> menuOptions, int tableNumber, Point loc);
	
	public abstract void msgWhatWouldYouLike(List<String> menu);
	
	public abstract void msgReOrder(List<String> menu);
	
	public abstract void msgHereIsYourFood(String food);
	
	public abstract void msgHereIsReceipt(int bill);
	
	public abstract void setWaiter(Waiter w);
	
	public abstract String getChoice();
	
	public abstract String getName();

	public abstract void setHost(Host h);
	
	
	
	
	
//	/**
//	 * @param total The cost according to the cashier
//	 *
//	 * Sent by the cashier prompting the customer's money after the customer has approached the cashier.
//	 */
//	public abstract void msgHereIsYourTotal(double total);
//
//	
//	
//	
//	
//	/**
//	 * @param total change (if any) due to the customer
//	 *
//	 * Sent by the cashier to end the transaction between him and the customer. total will be >= 0 .
//	 */
//	public abstract void HereIsYourChange(double total);
//
//
//	/**
//	 * @param remaining_cost how much money is owed
//	 * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
//	 */
//	public abstract void YouOweUs(double remaining_cost);

}