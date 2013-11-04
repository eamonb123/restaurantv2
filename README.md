##Restaurant Project Repository

###Student Information
  + Name: Eamon Barkhordarian
  + USC Email: ebarkhor@usc.edu
  + USC ID: 2894601675
  + section 30300
  + lab 30239 

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)
  
 ###How to run
  You can simply run the code by clicking the green arrow in Eclipe.
  To run it in Eclipse after cloning the project, do the same as you would to open up the project for Lab1

  TO TEST DIFFERENT SCENARIOS
  - To test the extra credit scenario of the cashier not having enough money to pay the market, change line 87 of the cookagent.java
  To modify line 87 of the cookagent.java, change the 3rd parameter of the Food constructor from 5 to 1
  - Also, you must change line 26 of CashierAgent.java, where the money is being initialized. Set money to 100
  - Now, you have forced the cashier to only have 60 dollars and the cook to only have 1 of every food item
  - Then run the code, but don't click any buttons (do not add waiter or customer, or put waiter on break). Only monitor the print statements until it says that "Market 3 has $0". At this point you know that the cook has ordered from all the markets
  - You have now created a scenario where, upon the store's opening, checks the inventory and notices that there is only 1 of every food item (hence "food is low")
  and consequently orders from the market.
  - The cashier currently only has $100 in this scenario, so Market 1 sends over what he can and charges $56 to the cashier
  - Since the cashier has $60, he is able to pay Market 1 in full and has $4 left to continue the rest of his order to Market 2
  - Market 2 charges the cashier $60 as well, but since the cashier only has $4, he pays Market 2 everything he has and still is left with a $56 bill
  - Now add a waiter and a customer and watch the entire interaction until the customer leaves
  - You will notice in the print statement that because the cashier owes Market 2 money, as soon as the customer pay's the cashier for his meal, the cashier sends that money over to market 2 to help relieve some of his debt. 
  
  
 ###Problems

 