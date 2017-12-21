package com.kylar.KylarsVengeance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kylar.Domain.Equipment;
import com.kylar.Domain.Player;
import com.kylar.Domain.ShopItem;
import com.kylar.Repo.EquipmentRepo;
import com.kylar.Repo.PlayerRepo;
import com.kylar.Repo.ShopRepo;

@SpringBootApplication(scanBasePackages = {"com.kylar"})
public class KylarsVengeanceApplication implements CommandLineRunner{

	@Autowired
	PlayerRepo playerRepo;
	@Autowired
	EquipmentRepo equipmentRepo;
	@Autowired
	ShopRepo shopRepo;

	private Scanner sc = new Scanner(System.in);  
	private Player player;
	private String buyKubit = "", fname = "", lname = "", ign="", mClose = "=== Market Closed ===";
	private List<Equipment> equip, market, upgrade;
	private List<ShopItem> shop;
	Random random = new Random();
	private int roundNo = 1,choice;
	private ArrayList<String> creatures = new ArrayList<String>(Arrays.asList("Sphinx", "Cyclops","Chimera", "Empusa", "Hydra", "Charybdis & Scylla","Cerberus", "Minotaur","Typhon"));
	private ArrayList<Boolean> executed = new ArrayList<Boolean>(Arrays.asList(false,false,false));

	
	private void printKylar(){
		System.out.println(" _   __      _                               ");
		System.out.println("| | / /     | |                              ");
		System.out.println("| |/ / _   _| | __ _ _ __ ___                ");
		System.out.println("|    \\| | | | |/ _` | '__/ __|               ");
		System.out.println("| |\\  \\ |_| | | (_| | |  \\__ \\               ");
		System.out.println("\\_| \\_/\\__, |_|\\__,_|_|  |___/               ");
		System.out.println("| | | | __/ |                                ");
		System.out.println("| | | ||___/_ __   __ _  __ _ _ __   ___ ___ ");
		System.out.println("| | | |/ _ \\ '_ \\ / _` |/ _` | '_ \\ / __/ _ \\");
		System.out.println("\\ \\_/ /  __/ | | | (_| | (_| | | | | (_|  __/");
		System.out.println(" \\___/ \\___|_| |_|\\__, |\\__,_|_| |_|\\___\\___|");
		System.out.println("                   __/ |                     ");
		System.out.println("                  |___/                      \n");

	}
	@Override
	public void run(String... arg0) throws Exception {
		printKylar();
		System.out.print("Enter First Name: ");  
		fname = sc.next();  
		System.out.print("Enter Last Name: ");  
		lname= sc.next();  
		System.out.print("Enter IGN: ");
		ign = sc.next();
		player = new Player(fname,lname,ign,1000,100);
		System.out.print("\nBuy some Kubits? 5000 Kubit - €5.99 (Y or N): ");
		buyKubit = sc.next();
		if(buyKubit.toLowerCase().equals("y")){
			System.out.print("\nPlease enter your PayPal Username: ");
			sc.next();
			System.out.print("Please enter your PayPal Password: ");
			sc.next();
			System.out.println("\nThank you, you have purchased 5000 Kubits");
			player.setBalance(player.getBalance() + 5000);
		}
		
		addPlayer();
		equip = equipmentRepo.getAllEquipment();
		displayCurrentStats(player);
		showMenu();
		System.out.println("\nEnd Result\n============\nName: " + player.getFirstName() + " '" + player.getIgn() + "' " + player.getLastName() + "\nBalance: "  + player.getBalance());
		for(Equipment e : equip){
			System.out.println(e.getItem() + " - Level " + e.getCurrentLevel());
		}
		System.out.println("\nThank you for playing!");
	}

	public static void main(String[] args) {
		SpringApplication.run(KylarsVengeanceApplication.class, args);
	}

	private void addPlayer(){
		playerRepo.addPlayer(player);
	}
	private void updatePlayer(){
		playerRepo.updatePlayerInfo(player);
	}

	private void updateEquipmentLists(){
		equip = equipmentRepo.getAllEquipment();
		market = equipmentRepo.getAllSellable();
	}
	private void sell(Equipment e){
		player.setBalance(player.getBalance()+e.getSalePrice());
		updatePlayer();
		equipmentRepo.removeEquipment(e);
		System.out.println(e.getItem() + " sold - " + e.getSalePrice() + " Kubits Received\nCurrent Balance: " + player.getBalance());
		updateEquipmentLists();
		executed.set(1, true);
	}
	private void upgradeItem(Equipment e){
		if(player.getBalance() >= e.getPriceForUpgrade()){
			System.out.println(e.getItem() + " upgraded to level " + e.getCurrentLevel() + " - " + e.getPriceForUpgrade() + " Kubits Taken\n Current Balance: " + player.getBalance());
			equipmentRepo.updateEqipmentInfo(e);
			e = equipmentRepo.getEquipment(e.getItem());
			System.out.println("\n=== UPGRADED STATS ===\nName: " + e.getItem() + "\nProtection: "+ e.getProtection() + "\nDamage: " + e.getDamage() +"\nUpgrade Price: " + e.getPriceForUpgrade() + "\nSale Price: " + e.getSalePrice());
			updateEquipmentLists();
			executed.set(2, true);
		}
		else
			System.out.println("Sorry, Insufficent Funds");
	}
	private void upgrade() {
		upgrade = equipmentRepo.getAllUpgradable();
		System.out.println("Select ONE for upgrade\n======================");
		for(int i = 0; i < upgrade.size(); i++){
			Equipment e = upgrade.get(i);
			System.out.println(i+1 + ". " + e.getItem() + " - Level " + (e.getCurrentLevel()) + " - Upgrade Price: " + e.getPriceForUpgrade());
		}
		System.out.println(upgrade.size()+1 + ". Exit");
		while(!sc.hasNextInt()){
			System.out.println("Transaction Void!");
			sc.next();
			showMenu();
		}	
		choice = sc.nextInt();

		if(choice == upgrade.size()+1)
			showMenu();
		else if(choice > shop.size()+1 || choice < 1){
			System.out.println("Invalid Input, Transaction Void");
			showMenu();
		}
		else{
			upgradeItem(upgrade.get(choice-1));
			showMenu();
		}
	}

	private void getMarket(){
		shop = shopRepo.getStock();
		System.out.println("Select ONE for purchase\n======================");
		for(int i = 0; i < shop.size(); i++){
			ShopItem e = shop.get(i);
			System.out.println(i+1 + ". " + e.getItem() + " - Level " + e.getCurrentLevel() 
			+ " - " + e.getPrice() + " Kubits - " + e.getStock() + " in Stock");
		}
		System.out.println(shop.size()+1 + ". Exit");
		while(!sc.hasNextInt()){
			System.out.println("Transaction Void!");
			sc.next();
			showMenu();
		}
		choice = sc.nextInt();
		if(choice == shop.size()+1)
			showMenu();
		else if(choice > shop.size()+1 || choice < 1){
			System.out.println("Invalid Input, Transaction Void");
			showMenu();
		}
		else{
			buy(shop.get(choice-1));
			showMenu();
		}
	}
	private void getSaleMarket(){
		updateEquipmentLists();
		for(Equipment e : market){
			if(e.getSale() == 1)
				System.out.println(e.getItem());
		}
		System.out.println("Select ONE for sale\n======================");
		for(int i = 0; i < market.size(); i++){
			Equipment eq = market.get(i);
			System.out.println(i+1 + ". " + eq.getItem() + " (Level " + eq.getCurrentLevel() + ") - " + eq.getSalePrice() + " Kubits");
		}
		System.out.println(market.size()+1 + ". Exit");
		while(!sc.hasNextInt()){
			System.out.println("Please Try Again!");
			sc.next();
			showMenu();
		}	
		choice = sc.nextInt();

		if(choice == 1){
			sell(market.get(choice-1));
			showMenu();
		}
		else if(choice == 2)
			showMenu();
		else{
			System.out.println("Invalid Input, Transaction Void");
			showMenu();
		}
	}
	private void buy(ShopItem si){
		if(player.getBalance() >= si.getPrice()){
			shopRepo.dropStock(si);
			if(si.getStock() == 1)
				shopRepo.removeFromShop(si);
			player.setBalance(player.getBalance()-si.getPrice());
			updatePlayer();
			System.out.println(si.getItem() + " bought " + si.getPrice() + " Kubits Taken\nCurrent Balance: " + player.getBalance());
			executed.set(0, true);
			equipmentRepo.addEquipment(si);
			updateEquipmentLists();
		}
		else
			System.out.println("Sorry, Insufficient Funds");
	}

	private void showMenu(){
		if(roundNo <= 10){
			if(executed.get(0) && executed.get(1) && executed.get(2)){
				for(int i = 0; i < executed.size(); i++){
					executed.set(i, false);
				}
				roundNo++;
			}
			updateEquipmentLists();
			if(!executed.get(0) || !executed.get(1) || !executed.get(2)){
				System.out.println("\n=== Round " + roundNo + " ===");
				System.out.println("\n1. Finish Round\n2. Buy new equipment\n3. Sell equipment\n4. Upgrade equipment");
				while(!sc.hasNextInt()){
					System.out.println("Transaction Void!");
					sc.next();
					showMenu();
				}	
				choice = sc.nextInt();

				if(choice == 1)
					finishRound();
				else if(choice == 2){
					if(executed.get(0)){
						System.out.println(mClose);
						showMenu();
					}
					else{
						if(equip.size() < 4)
							marketplace(choice);
						else{
							System.out.println(mClose);
							showMenu();
						}
					}
				}
				else if(choice == 3){
					if(executed.get(1)){
						System.out.println(mClose);
						showMenu();
					}
					else{
						if(equip.size() == 3){
							System.out.println("=== Sorry, You have nothing to sell ===");
							showMenu();
						}
						else{
							marketplace(choice);
							executed.set(choice-1, true);
						}
					}				
				}
				else if(choice == 4){
					if(executed.get(2)){
						System.out.println("=== UPGRADES UNAVIALABLE ===");
						showMenu();
					}
					else
						upgrade();
				}
				else{
					System.out.println("Please Try Again");
					showMenu();
				}
			}
		}

	}
	private void marketplace(int choice) {
		if(choice == 2)
			getMarket();
		else{
			updateEquipmentLists();
			getSaleMarket();
		}
	}	
	private void finishRound() {
		int fightChoice = random.nextInt(100 - 0 + 1) + 0;
		int monster = random.nextInt(8 -0 + 1) + 0;
		int winHealth = random.nextInt(20 - 0 + 1) + 0;
		int loseHealth = random.nextInt(50 - 20 + 1) + 20;
		if(fightChoice % 2 == 0){
			if(fightChoice % 10 == 0){
				if(player.getHealth() < winHealth)
					winHealth = player.getHealth();
				System.out.println("You went to battle, you fought " + creatures.get(monster) + " and you won! You've earned 50 Kubit but lost " + winHealth + " health");
				player.setBalance(player.getBalance() + 50);
				player.setHealth(player.getHealth() - winHealth);

			}
			else{
				if(player.getHealth() < loseHealth)
					loseHealth = player.getHealth();
				System.out.println("You went to battle, you fought " + creatures.get(monster) + " but you ran away and hid! You lost " + loseHealth + " health in the process");
				player.setHealth(player.getHealth() - loseHealth);
			}
		}
		else{
			System.out.println("You had a nice peaceful evening drinking ale in the local tavern, Bragging about your latest adventure and the 'Monster' you 'Slayed'");
			if(player.getHealth() < 100){
				player.setHealth(player.getHealth()+5);
			}
			System.out.println("Healed 5 Health Points");
		}

		roundNo++;
		player.setBalance(player.getBalance() + 500);
		updatePlayer();
		Collections.fill(executed,false);
		if(player.getHealth() == 0){
			System.out.println("\nYou have died. You silly sausage.");
			roundNo = 11;
		}
		if(roundNo <= 10)
			displayCurrentStats(player);
		showMenu();


	}
	private void displayCurrentStats(Player p){
		System.out.println("\nCurrent State\n=============\nHealth: " + player.getHealth() + "%\nBalance: " + player.getBalance() + " Kubits\n");
		updateEquipmentLists();
		for(Equipment eq : equip){
			System.out.println(eq.getItem() + " - " + eq.getDurability() + "/100 - Level " + eq.getCurrentLevel());
		}
	}
}
