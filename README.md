##Restaurant Project Repository

###Student Information
  + Name: Eamon Barkhordarian
  + USC Email: ebarkhor@usc.edu
  + USC ID: 2894601675
  + section 30300
  + lab 30239 

Download Restaurant v2.2: 
https://dl.dropboxusercontent.com/s/0zcww1tiu4e8dms/Restaurant%20v2.2.exe

NOTE: ONLY RUNS ON PC

#About Restaurant v2.2
This Restaurant.exe is a restaurant simulation involving a host, customers, waiters, a cook, 3 markets, and a cashier that all interact with one another as a normal restaurant would. Like a typical restaurant, the host assigns waiting customers to different waiters. The customer could either be hungry or not hungry, orders off a menu, eats his food, and pays his bill. The waiter seats the customer, gets the customer's order, gives it to the cook, picks up the cooked order, delivers the food to the customer, and hands the customer his bill. The cook has a limited supply of food (initialized to 5 pieces of each item). When the cook has a low supply of an item, he sends a grocery list to one of three markets to order what he needs. The Market then ships over the supplies and the cook's inventory is restocked. The cashier calculates the customer's bill, receives the customer's money, and gives the customer back their change.

#The Restaurant Members
##The Host
The Host is off-screen, but does all the managing of the restaurant. The host keeps track of all the customers in line and, if a customer is hungry, assigns a customer to the least busy waiter (if a waiter exists). If a waiter does not exist, the host keeps track of the customers’ position in line to notify the waiter when created. The host also keeps track of which tables in the restaurant are occupied and which are available to seat customers.

##The Customers
The customer’s name is inputted in the text field and is added to the list of customers upon clicking the “add” button. The customers are represented by green squares and wait in line when they are selected as “hungry” by the user. They follow the typical customer interactions of sitting down, ordering, eating food, paying the bill, and leaving the restaurant. The customers each have $20. To make a customer hungry, the user clicks the “Hungry?” checkbox

##The Waiters
The waiter’s name is inputted in the “Waiters” text field and is added to the list of waiters upon clicking the “add” button. The waiters are represented by purple squares and have specifically assigned “home bases” where they reside when there is nothing for them to do. Waiters follow the typical waiter interactions of seating a customer, taking their order, delivering it to the cook, picking up the order and serving the customer, and delivering the check to the customer. 
Waiters also have the capability to go on break by the user clicking the break checkbox on the bottom bar of the application. In the example to the right, there are two waiters, but the user assigned waiter Sam to go on break. Sam moves to the break area (off the screen) until the user unclicks the break checkbox.

##The Cook
The cook is off the screen and both cooks the customers’ orders and orders food from the market. The cook has 3 cooking locations (pink colored) and 3 plating locations (cyan colored) and manages the spacing of his orders depending on which spaces are available to place food on. The cook originally begins with 5 of each food item (lamb, chicken, and beef). When food is low, the cook orders from the markets (which are also off the screen and the interactions are handled in the background). If one market cannot fully fulfill the order request of the cook, it sends the cook everything it can fulfill and the cook orders from the next market. To track the messaging between the cook and the market, the user can look into the command line that pops up when running Restaurant v2.2.

##The 3 Markets
There are three markets in Restaurant v2.2 which are all off the screen. Each market has 4 of each item (beef, lamb, chicken) that cannot be resupplied. During each order from the cook, the market charges the cashier the appropriate bill and ships the items to the cook.

##The Cashier
There is one cashier in Restaurant v2.2 which is off the screen on the right side. The cashier starts with $100,000 and charges customers depending on what they ordered. In addition, the cashier collects the customer’s money when the customer pays the bill and hands the customer any change as well. Lastly, the cashier is the one that pays the three markets any time the cook makes an order.



#Command Line
All the messaging between the different agents of the restaurant (host, customers, waiters, cook, markets, and cashier) is printed out in the command line which pops up when the Restaurant v2.1.exe is run. The restaurant's visuals will be displayed in the GUI, but in the background, all the messaging is printed out in the command line. As the user interacts with the application and creates new waiters and customers, the different messages will display there.


#Normative Run-through
A typical run-through of the simulation would involve adding a customer to the restaurant and setting him to "hungry," and then adding a waiter. The user would then see the waiter and customer interactions including
- A waiter seating a customer to the first open table
- The customer deciding to order some food item off the menu (beef, chicken, or lamb)
- The customer telling the waiter he is ready to order
- The waiter taking the customer’s order and giving it to the cook
- The cook putting the order on a list of orders
- The waiter waiting for the cook to cook the food
- The waiter picking up the order from the cook when the food is ready and delivering it to the customer
- The customer eating the food
- The customer asking the waiter for the receipt when he is done eating
- The waiter going to the cashier and getting the customer's bill and delivering it to the customer
- The customer receiving the bill and leaving the table to go to the cashier
- The customer paying the cashier and receiving change


#Non-Normative Run-through
- Adding a customer without clicking the "hungry" button. To notify the waiter to serve the customer, click the customer's name in the table and click the hungry checkbox in the bottom bar.
- Setting a waiter on break by clicking the waiter's name in the table and click the break checkbox in the bottom bar. If the waiter has no customers upon clicking the break checkbox, he will go up to his break location. If the waiter has customers he is currently serving while asked to go on break, he will finish all his customers before going on break
