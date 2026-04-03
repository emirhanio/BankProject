package src;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.image.*;
import java.util.HashMap;

import javax.swing.text.NumberFormatter;

class MyExceptions extends Exception
{
	String className;
	String msg;
	public MyExceptions(String className,String msg) {
		this.className=className;
		this.msg=msg;
	}
	public String toString() {
		return className +": " + msg ;
	}
}
abstract class BankAccount
{
	protected String id;
	protected float balance;
	public BankAccount(String id) {
		this.id=id;
	}
	public String toString() {
		return id+" has a balance of "+balance;
	}
	public void Deposit(float x)throws MyExceptions {
		if(x<0)
			throw new MyExceptions("BankAccount","Cannot deposit amount is negative");
		balance+=x;
	}
	public void Withdraw(float y) throws MyExceptions {
		if(!CanWithdraw(y))
			throw new MyExceptions("BankAccount","Cannot Withdraw");
		balance-=y;
	}
	public boolean CanWithdraw(float z) {
		if (!(balance>=z && z>0))
			return false;
		return true;
	}
}
abstract class Bank
{
	Bank() {}
	public String toString() {
		return "Bank Instance";
	}
	public boolean Transfer(BankAccount a,BankAccount b,float x) {
		try {
			a.Withdraw(x);
			b.Deposit(x);
			System.out.println("Transfer Successful");
			return true;
		}
		catch(MyExceptions e) {
			System.out.println(e);
			return false;
		}
	}
}
class CheckingAccount extends BankAccount
{
	private float overdraftLimit;
	CheckingAccount(String id,float overdraftLimit) {
		super(id);
		this.overdraftLimit=overdraftLimit;
	}
	@Override
	public boolean CanWithdraw(float x) {
		return balance+overdraftLimit>=x && x>0;
	}
	@Override
	public String toString() {
		return id+" has a balance of "+balance + " overdraft limit "+overdraftLimit;
	}
	public void Withdraw(float y) throws MyExceptions {
		if(!CanWithdraw(y))
			throw new MyExceptions("CheckingAccount","Cannot Withdraw");
		super.Withdraw(y);
	}
}


class FXAccount extends BankAccount
{
	private float exchangeRate=3;
	FXAccount(String id) {
		super(id);
	}
	@Override
	public void Deposit(float x)throws MyExceptions {
		if(x<0)
			throw new MyExceptions("FXAccount","Cannot deposit amount is negative");
		super.Deposit(x/exchangeRate);
	}
	@Override
	public void Withdraw(float x) throws MyExceptions
	{
		if(!CanWithdraw(x/exchangeRate))
			throw new MyExceptions("FXAccount","Cannot Withdraw");
		super.Withdraw(x/exchangeRate);
	}
}
class CommissionBank extends Bank
{
	private float commissionRate;
	private float balance;
	CommissionBank(float commissionRate) {
		this.commissionRate=commissionRate;
	}
	@Override
	public String toString() {
		return "Bank has a balance of "+balance;
	}
	public boolean Transfer(BankAccount a,BankAccount b,float x) {
		try {
			float y=x+x*commissionRate;
			a.Withdraw(y);
			b.Deposit(x);
			balance+=x*commissionRate;
			System.out.println("Transfer Successful");
			return true;
		}
		catch (MyExceptions e) {
			System.out.println(e);
			System.out.println("Cannot transfer from account1 to account2");
			return false;
		}
	}
}
class MinCommissionBank extends Bank
{
	private float commissionRate;
	private float minCommission=50;
	private float balance;
	MinCommissionBank(float commissionRate) {
		this.commissionRate=commissionRate;
	}
	public String toString() {
		return "Bank has a balance of "+balance;
	}
	public boolean Transfer(BankAccount a,BankAccount b, float x) {
		float y;
		float d;
		if(minCommission>x*commissionRate)
		{
			y=x+minCommission;
			d=minCommission;
		}
		else {
			y=x+x*commissionRate;
			d=x*commissionRate;
		}
		try {
			a.Withdraw(y);
			b.Deposit(x);



			balance+=d;
			System.out.println("Transfer Successful");
			return true;
		}
		catch (MyExceptions e) {
			System.out.println(e);
			System.out.println("Cannot transfer from account1 to account2");
			return false;
		}
	}
}

public class BankProject extends Application{
	private BankAccount loggedInUser;
	private Bank myBank = new CommissionBank(0.05f);

	private HashMap <String, BankAccount> database = new HashMap<>();
	

	
	public void start(Stage stage)throws Exception{

		DatabaseManager.initialize();
		database = DatabaseManager.loadAccounts();


		String inputStyle = "-fx-font-family: 'Segoe UI', sans-serif; " +
                            "-fx-font-size: 14px; " +
                            "-fx-padding: 10px; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-border-color: #bdc3c7; " +
                            "-fx-border-width: 1px; " +
                            "-fx-background-color: white;";

        
        String primaryBtnStyle = "-fx-background-color: #3498db; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-family: 'Segoe UI', sans-serif; " +
                                 "-fx-font-size: 14px; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-padding: 10px 20px; " +
                                 "-fx-background-radius: 5px; " +
                                 "-fx-cursor: hand;";

        
        String secondaryBtnStyle = "-fx-background-color: #95a5a6; " +
                                   "-fx-text-fill: white; " +
                                   "-fx-font-family: 'Segoe UI', sans-serif; " +
                                   "-fx-font-size: 14px; " +
                                   "-fx-font-weight: bold; " +
                                   "-fx-padding: 10px 20px; " +
                                   "-fx-background-radius: 5px; " +
                                   "-fx-cursor: hand;";

        
        String titleStyle = "-fx-font-family: 'Segoe UI', sans-serif; " +
                            "-fx-font-size: 26px; " +
                            "-fx-font-weight: 800; " +
                            "-fx-text-fill: #2c3e50;";

		String dangerBtnStyle = "-fx-background-color: #e74c3c; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-family: 'Segoe UI', sans-serif; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10px 20px; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-cursor: hand;";


		//---------MENU---------	
		VBox root = new VBox();
		root.setSpacing(15);
		root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root,1000,1000);
		stage.setScene(scene);
		stage.setTitle("Bank System");
		Image icon = new Image("file:./src/bankphoto2.png");
		stage.getIcons().add(icon);
        
        Label menuLabel = new Label("Bank Management System");
        menuLabel.setPrefSize(600, 60);
        menuLabel.setAlignment(Pos.CENTER); 
        menuLabel.setStyle("-fx-font-family: 'Segoe UI', sans-serif; " +
                           "-fx-font-size: 32px; " +
                           "-fx-font-weight: 800; " + 
                           "-fx-text-fill: #2c3e50; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 2);");

        Label subTitle = new Label("Manage your FX and Checking accounts");
        subTitle.setPrefSize(400, 30);
        subTitle.setAlignment(Pos.CENTER);
        subTitle.setStyle("-fx-font-family: 'Segoe UI', sans-serif; " +
                          "-fx-font-size: 16px; " +
                          "-fx-text-fill: #7f8c8d;");


		Button loginbtn = new Button("Login");
		loginbtn.setMinSize(400,40);
		Button registerbtn = new Button("Register");
		registerbtn.setMinSize(400,40);
		Button exitbtn = new Button("Exit");
		exitbtn.setMinSize(400,40);
		loginbtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
		registerbtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
		exitbtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

		exitbtn.setOnAction(e -> {stage.close();});
		root.getChildren().addAll(menuLabel,subTitle,loginbtn,registerbtn,exitbtn);

		//---------LOGIN---------
		VBox loginlayout = new VBox(15);
		loginlayout.setAlignment(Pos.CENTER);
		Scene loginScene = new Scene(loginlayout,1000,1000);
		

		Label loginlabel = new Label("Login Screen");
		loginlabel.setStyle(titleStyle);
		TextField loginField = new TextField();
		loginField.setPromptText("Enter ID...");
		loginField.setMaxWidth(200);
		loginField.setStyle(inputStyle);
        loginField.setPrefHeight(40);



		Label loginmsglabel = new Label();

		Button submitlogButton = new Button("Login");
		submitlogButton.setStyle(primaryBtnStyle);
        submitlogButton.setPrefWidth(200);		
		Button backbtn = new Button("Back");
		backbtn.setStyle(secondaryBtnStyle);
        backbtn.setPrefWidth(200);

		//LOGIN)---DASHBOARD---

		VBox dashboardlayout = new VBox(15);
		dashboardlayout.setAlignment(Pos.CENTER);

		Label welcomeLabel = new Label();
		welcomeLabel.setStyle(titleStyle);
		Label balanceLabel = new Label();
		balanceLabel.setStyle("-fx-font-family: 'Segoe UI', sans-serif; " +
                              "-fx-font-size: 18px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-text-fill: #34495e; " +
                              "-fx-padding: 10px;");
		Button logoutbtn = new Button("Logout");
		logoutbtn.setStyle(dangerBtnStyle);
        logoutbtn.setPrefWidth(150);

		TextField amountfield = new TextField();
		amountfield.setPromptText("Enter Amount...");
		amountfield.setMaxWidth(150);
		amountfield.setStyle(inputStyle);
        amountfield.setPrefHeight(40);
        amountfield.setPrefWidth(250);

		TextField targetid = new TextField();
		targetid.setPromptText("Target ID For Transfer");
		targetid.setMaxWidth(150);
		targetid.setStyle(inputStyle);
        targetid.setPrefHeight(40);
        targetid.setPrefWidth(250);

		Label dashmsglabel = new Label();

		Button depositBtn = new Button("Deposit");
		Button withdrawBtn = new Button("Withdraw");
		Button transferbtn = new Button("Transfer");
		depositBtn.setStyle(primaryBtnStyle);
        depositBtn.setPrefWidth(120);
        withdrawBtn.setStyle(primaryBtnStyle);
        withdrawBtn.setPrefWidth(120);
        transferbtn.setStyle(primaryBtnStyle);
        transferbtn.setPrefWidth(120);

		HBox actionbuttons = new HBox(10);
		actionbuttons.setAlignment(Pos.CENTER);
		actionbuttons.getChildren().addAll(depositBtn,withdrawBtn,transferbtn);

		dashboardlayout.getChildren().addAll(welcomeLabel,balanceLabel,amountfield,targetid,actionbuttons,dashmsglabel,logoutbtn);
		Scene dashboardScene = new Scene(dashboardlayout,1000,1000);
		

		loginlayout.getChildren().addAll(loginlabel,loginField,submitlogButton,loginmsglabel,backbtn);


		//LOGIN)---DASHBOARD BUTTON ACTIONS---

		depositBtn.setOnAction(e->{
			try{
				float amount = Float.parseFloat(amountfield.getText());

				loggedInUser.Deposit(amount);

				balanceLabel.setText("Status: "+ loggedInUser.toString());
				dashmsglabel.setText("Deposit Successful");
				dashmsglabel.setTextFill(Color.GREEN);
				amountfield.clear();
			}
			catch(NumberFormatException ex){
				dashmsglabel.setText("Please enter a valid number");
				dashmsglabel.setTextFill(Color.RED);
			}
			catch(MyExceptions ex){
				dashmsglabel.setText(ex.toString());
				dashmsglabel.setTextFill(Color.RED);
			}
		});

		withdrawBtn.setOnAction(e->{
			try{
				float amount = Float.parseFloat(amountfield.getText());
				loggedInUser.Withdraw(amount);

				balanceLabel.setText("Status: "+loggedInUser.toString());
				dashmsglabel.setText("Withdrawal Successful");
				dashmsglabel.setTextFill(Color.GREEN);
				amountfield.clear();
			}
			catch(NumberFormatException ex){
				dashmsglabel.setText("Please enter a valid number");
				dashmsglabel.setTextFill(Color.RED);
			}
			catch(MyExceptions ex){
				dashmsglabel.setText(ex.toString());
				dashmsglabel.setTextFill(Color.RED);
			}
		});

		transferbtn.setOnAction(e->{
			String target = targetid.getText();

			try{
				float amount = Float.parseFloat(amountfield.getText());

				if(target.isEmpty()){
					dashmsglabel.setText("Enter a Target ID");
					dashmsglabel.setTextFill(Color.RED);
				}
				else if(!database.containsKey(target)){
					dashmsglabel.setText("Target account not found in system");
					dashmsglabel.setTextFill(Color.RED);
				}
				else if(target.equals(loggedInUser.id)){
					dashmsglabel.setText("Cannot transfer to yourself");
					dashmsglabel.setTextFill(Color.RED);
				}
				else{
					BankAccount targetAccount = database.get(target);

					boolean success = myBank.Transfer(loggedInUser, targetAccount, amount);

					if(success){
						balanceLabel.setText("Status: "+ loggedInUser.toString());
						dashmsglabel.setText("Transfer Successful");
						dashmsglabel.setTextFill(Color.GREEN);
						amountfield.clear();
						targetid.clear();
					}
					else{
						dashmsglabel.setText("Transfer Failed! Check limits.");
						dashmsglabel.setTextFill(Color.RED);
					}
				}
			}
			catch(NumberFormatException ex){
				dashmsglabel.setText("Please enter a valid number");
				dashmsglabel.setTextFill(Color.RED);
			}
		});


		//LOGIN)---------LOGIN BUTTON ACTIONS---------

		loginbtn.setOnAction(e ->{
			stage.setScene(loginScene);
			loginField.clear();
			amountfield.clear();
		});	


		backbtn.setOnAction(e -> {
			stage.setScene(scene);
			loginField.clear();
			loginmsglabel.setText("");
		});

		logoutbtn.setOnAction(e->{
			stage.setScene(scene);
			loginField.clear();
			amountfield.clear();
			loginmsglabel.setText("");
		});




		submitlogButton.setOnAction(e->{
			String id = loginField.getText();

			if(id.isEmpty()){
				loginmsglabel.setText("Please enter your ID!");
				loginmsglabel.setTextFill(Color.RED);
			}
			else if(database.containsKey(id)){

				loggedInUser = database.get(id);

				welcomeLabel.setText("Welcome, "+ id + "!");
				balanceLabel.setText("Status: "+ loggedInUser.toString());
				dashmsglabel.setText("");

				stage.setScene(dashboardScene);
				loginField.clear();
				loginmsglabel.setText("");
			}
			else{
				loginmsglabel.setText("Account not found!");
				loginmsglabel.setTextFill(Color.RED);
			}
		});


		//---------REGISTER---------
		VBox registerlayout = new VBox(15);
		registerlayout.setAlignment(Pos.CENTER);

		Label registerLabel = new Label("Register Screen");
		registerLabel.setStyle(titleStyle);

		TextField idfield = new TextField();
		idfield.setPromptText("Enter ID...");
		idfield.setMaxWidth(200);
		idfield.setStyle(inputStyle);
        idfield.setPrefHeight(40);

		ComboBox<String> acctypebox = new ComboBox<>();
		acctypebox.getItems().addAll("FXAccount", "CheckingAccount");
		acctypebox.setPromptText("Choose Account Type");
		acctypebox.setStyle(inputStyle);
        acctypebox.setPrefHeight(40);
        acctypebox.setPrefWidth(200);

		Label messageLabel = new Label();

		Button submitregbutton = new Button("Create Account");
		submitregbutton.setStyle(primaryBtnStyle);
        submitregbutton.setPrefWidth(200);

		Button backbtn2 = new Button("Back");
		backbtn2.setStyle(secondaryBtnStyle);
        backbtn2.setPrefWidth(200);
		Scene registerScene = new Scene(registerlayout,1000,1000);

		registerbtn.setOnAction(e ->{
			stage.setScene(registerScene);
		});
		backbtn2.setOnAction(e -> {
			stage.setScene(scene);
		});

		submitregbutton.setOnAction(e ->{
			String id = idfield.getText();
			String type = acctypebox.getValue();
			if(id.isEmpty() || type == null ){
				messageLabel.setText("Please fill in all fields");
				messageLabel.setTextFill(Color.RED);
			}
			else if(database.containsKey(id)){
				messageLabel.setText("This ID already in use");
				messageLabel.setTextFill(Color.RED);
			}
			else{
				BankAccount newAccount;
				if(type.equals("FXAccount")){
					newAccount = new FXAccount(id);
				}
				else{
					newAccount = new CheckingAccount(id,500f );
				}
				database.put(id,newAccount);
				src.DatabaseManager.addAccount(newAccount);
				messageLabel.setText("The account was successfully created.");
				messageLabel.setTextFill(Color.GREEN);
				idfield.clear();
				acctypebox.getSelectionModel().clearSelection();
			}
		});

		registerlayout.getChildren().addAll(registerLabel,idfield,acctypebox,submitregbutton,messageLabel,backbtn2);
		
		stage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}


