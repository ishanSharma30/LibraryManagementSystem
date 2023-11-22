package application;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class dbmsconnection
{
	String url;
	String username;
	String pass;
	
	public dbmsconnection(String url, String username, String pass)
	{
		super();
		this.url = url;
		this.username = username; 
		this.pass = pass;
	}
	public Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		Connection con=null;
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		con=DriverManager.getConnection(url,username,pass);
//		System.out.println("Connection successful");
		return con;
	}
	
	public void closeConnection(Connection con,Statement smt) throws SQLException
	{
		smt.close();
		con.close();
//		System.out.println("Connection closed");
		
	}
}


public class Main extends Application {
	@Override
	public void start(Stage stage) {
		
		
		//home screen
		GridPane home=new GridPane();
		home.setAlignment(Pos.CENTER);
		home.setVgap(50);
		
		Button issue=new Button("Issue a book");
		issue.setPrefWidth(150);
		issue.setPrefHeight(50);
		Button ret=new Button("Return a book");
		ret.setPrefWidth(150);
		ret.setPrefHeight(50);
		
		Label heading=new Label("Welcome to Library Management System");
		heading.setAlignment(Pos.CENTER);

		heading.setFont(Font.font("Times New Roman",FontWeight.BOLD,24));
		issue.setFont(Font.font("Times New Roman",FontWeight.BOLD,18));
		ret.setFont(Font.font("Times New Roman",FontWeight.BOLD,18));
		home.add(heading,1,0);
		home.add(issue,0,1);
		home.add(ret,2,1);
		
		
		Scene homesc=new Scene(home,1000,600);
		stage.setScene(homesc);
		stage.show();
		
		issue.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				
				
				//issue scene
				
				GridPane issuepane=new GridPane();
				issuepane.setHgap(10);
				issuepane.setVgap(10);
				issuepane.setAlignment(Pos.CENTER);
				
				Label l1=new Label("Enter your details of the book to be issued");
				l1.setFont(Font.font("Times New Roman",FontWeight.BOLD,24));
				issuepane.add(l1,0,0);
				
				
				Label regLabel=new Label("Registration number");
				regLabel.setFont(Font.font("Times New Roman",16));
				TextField regfiled=new TextField();
				issuepane.add(regLabel,0,1);
				issuepane.add(regfiled,1,1);
				
				Label bid=new Label("Book ID");
				bid.setFont(Font.font("Times New Roman",16));
				TextField bidField=new TextField();
				issuepane.add(bid,0,2);
				issuepane.add(bidField,1,2);
				
				
				Label bname=new Label("Book name");
				bname.setFont(Font.font("Times New Roman",16));
				TextField bnameField=new TextField();
				issuepane.add(bname,0,3);
				issuepane.add(bnameField,1,3);
				
				Label doi=new Label("Date of issue");
				doi.setFont(Font.font("Times New Roman",16));
				DatePicker doiPicker=new DatePicker();
				issuepane.add(doi,0,4);
				issuepane.add(doiPicker,1,4);
				
				Label dor=new Label("Date of Return");
				dor.setFont(Font.font("Times New Roman",16));
				DatePicker dorPicker=new DatePicker();
				issuepane.add(dor,0,5);
				issuepane.add(dorPicker,1,5);
				
				Button homButton=new Button("Home");
				issuepane.add(homButton,0,7);
				homButton.setFont(Font.font("Times New Roman",FontWeight.BOLD,20));
				
				homButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						stage.setScene(homesc);
						
					}
				});
				
				Button submit=new Button("Submit");
				issuepane.add(submit,1,7);
				submit.setFont(Font.font("Times New Roman",FontWeight.BOLD,20));
				
				Label errLabel=new Label();
				errLabel.setFont(Font.font("Times New Roman",18));
				
				issuepane.add(errLabel,1,8);
				
				submit.setOnAction(new EventHandler<ActionEvent>() {
					

					@Override
					public void handle(ActionEvent arg0) {
						
						
						try 
						{
							dbmsconnection db=new dbmsconnection("jdbc:mysql://localhost:3306/jdbcprac", "root","");
							Connection con=db.getConnection();
							
							String reg=regfiled.getText();
							String bid=bidField.getText();
							String bname=bnameField.getText();
							
		
							Date doi=Date.valueOf(doiPicker.getValue());
							Date dor=Date.valueOf(dorPicker.getValue());
							
							if(!reg.isEmpty() && !bid.isEmpty() && !bname.isEmpty())
							{
								if(doi.compareTo(dor)<0)
								{
									String sql="insert into book values(?,?,?,?,?)";
									PreparedStatement smt=con.prepareStatement(sql);
									smt.setString(1, reg);
									smt.setString(2, bid);
									smt.setString(3, bname);
									smt.setDate(4, doi);
									smt.setDate(5, dor);
									
									int i=smt.executeUpdate();
									if(i>0)
									{
										errLabel.setText("Book has been issued successfully");
										errLabel.setTextFill(Color.BLUE);
										
										db.closeConnection(con,smt);
										
										homButton.setOnAction(new EventHandler<ActionEvent>() {

											@Override
											public void handle(ActionEvent arg0) {
												
												stage.setScene(homesc);
			
											}
										});
									}
									
								}
								else 
								{
									errLabel.setText("Date of return is less than date of issue");
									errLabel.setTextFill(Color.RED);
								}
							}
							else {
								
								errLabel.setText("Please enter all the values");
								errLabel.setTextFill(Color.RED);
							}
						
						} 
						catch (NullPointerException e) {
							
							errLabel.setText("Dates are empty");
							errLabel.setTextFill(Color.RED);
						}
						catch(Exception e)
						{
							System.out.println(e);
						}

					}
				});
				
				
				Scene issScene=new Scene(issuepane,1000,600);
				stage.setScene(issScene);
				//end of issue scene
			}
		});
		
		
		ret.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				//return scene
				
				GridPane retu =new GridPane();
				retu.setHgap(10);
				retu.setVgap(10);
				retu.setAlignment(Pos.CENTER);
				
				Label l1=new Label("Enter the details of the book to be returned");
				l1.setFont(Font.font("Times New Roman",FontWeight.BOLD,24));
				retu.add(l1,0,0);
				
				Label bid=new Label("Book ID");
				bid.setFont(Font.font("Times New Roman",16));
				TextField bidField=new TextField();
				retu.add(bid,0,1);
				retu.add(bidField,1,1);
				
				Button homButton=new Button("Home");
				retu.add(homButton,0,2);
				homButton.setFont(Font.font("Times New Roman",FontWeight.BOLD,20));
				
				homButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						stage.setScene(homesc);
						
					}
				});
				
				Button submit=new Button("Submit");
				retu.add(submit,1,2);
				submit.setFont(Font.font("Times New Roman",FontWeight.BOLD,20));
				
				Label errLabel=new Label();
				errLabel.setFont(Font.font("Times New Roman",18));
				retu.add(errLabel,1,3);
				
				submit.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						
						
						try 
						{
							dbmsconnection db=new dbmsconnection("jdbc:mysql://localhost:3306/jdbcprac", "root","");
							Connection con=db.getConnection();
							
							String bid=bidField.getText();
							
							if(!bid.isEmpty())
							{
								String sql = "select * from book where bid=?";
								PreparedStatement smt = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
								smt.setString(1, bid);
						
								ResultSet rs = smt.executeQuery();

								if (rs.next() == false) 
								{
									
									errLabel.setText("This book has not been issued");
									errLabel.setTextFill(Color.RED);
									
								} 
								else 
								{
									Date dateOfReturn=rs.getDate(5);
									Date today=Date.valueOf(LocalDate.now());
									if(today.compareTo(dateOfReturn)<=0)
									{
										
										String sql1="delete from book where bid=?";
										PreparedStatement smt1=con.prepareStatement(sql1);
										smt1.setString(1, bid);
										
										int i=smt1.executeUpdate();
										if(i>0)
										{
											errLabel.setText("Book has been successfully returned");
											errLabel.setTextFill(Color.RED);
										}
											
									}
									else 
									{
										int extra=today.getDate()-dateOfReturn.getDate();
										int fine=extra*10;
										
										
										errLabel.setText("Please pay "+"\u20B9"+fine);
										errLabel.setTextFill(Color.RED);
									}
								}
										
							}
							else 
							{
								errLabel.setText("Please enter all the values");
								errLabel.setTextFill(Color.RED);
							}
						
						} 
						catch (Exception e)
						{
							System.out.println(e);
						}
					
					}
				});
				
				Scene retScene=new Scene(retu,1000,600);
				stage.setScene(retScene);
				
			}
			
		});
		
		//home screen finish
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}