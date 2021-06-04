package webScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class webScraping {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String jdbcDriver = "com.mysql.cj.jdbc.Driver";
		String username = "root";
	    String password = "";
	    boolean dbFound = false;
		String url = "https://www.amazon.com/Best-Sellers-Kindle-Store-Computer-Programming/zgbs/digital-text/156140011";
        Document page = Jsoup.connect(url).get();

        String title=page.title();
        System.out.println("TITLE:"+title+"\n");
        
        String bookTitleSelector = "p13n-sc-truncate";
        String bookRatingSelector = "a-icon-star";
        String bookPriceSelector = "p13n-sc-price";
        // Get the books name using the HTML class selector
        Elements bookTitleElements = page.getElementsByClass(bookTitleSelector);
        Elements bookRatingElements = page.getElementsByClass(bookRatingSelector);
        Elements bookPriceElements = page.getElementsByClass(bookPriceSelector);
	    
        System.out.println(bookTitleElements);
        if(bookTitleElements != null && bookTitleElements.size() > 0) {
        	if(bookRatingElements != null && bookRatingElements.size() > 0) {
        		if(bookPriceElements != null && bookPriceElements.size() > 0) {
//        	        for(int i=0;i<bookElements.size()-1;i++) {
//        	        System.out.println(bookElements.get(i));
//        	        }
        	        List<String> booksTitle = bookTitleElements.stream()
                     .map(element -> element.text())
                     .collect(Collectors.toList());
        	        
        	        List<String> booksRating = bookRatingElements.stream()
        	                .map(element -> element.text())
        	                .collect(Collectors.toList());
        	        
        	        List<String> booksPrice = bookPriceElements.stream()
        	                .map(element -> element.text())
        	                .collect(Collectors.toList());
        	        
        	        System.out.println("BOOKS\n");
                    for(String bookTitle : booksTitle) {
            	    System.out.print(bookTitle+",");
                    }
                    System.out.println("\nRATINGS\n");
                    for(String bookRating : booksRating) {
                	    System.out.print(bookRating+",");
                        }
                    System.out.println("\nPRICE\n");
                    for(String bookPrice : booksPrice) {
                	    System.out.print(bookPrice+",");
                        }
                    
                    try {
                 	   Class.forName(jdbcDriver);
                 	   System.out.println("DRIVER LOADED!\n");
                 	   Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/",username, password);
                 	   System.out.println("CONNECTION TO THE DATABASE SUCCESSFULL!\n");
                 	   Statement stmt = conn.createStatement();
                 	   String sql = "CREATE TABLE DATA " +
                                "(id INTEGER not NULL, " +
                                " bookname VARCHAR(255), " + 
                                " ratings FLOAT, " + 
                                " price FLOAT, " + 
                                " PRIMARY KEY ( id ))"; 
                 	   
                 	   if(!dbFound) {
                 		   String createNewDatabase="CREATE DATABASE WEBSCRAPER";
                 		   int createdb = stmt.executeUpdate(createNewDatabase);
                 		   if (createdb >= 0) {
                                System.out.println("DATABASE CREATED SUCCESSFULLY!\n");
                            }
                 		   dbFound=true;
                 	   }
                 	   stmt.executeUpdate("USE webscraper");
             		   int createtb=stmt.executeUpdate(sql);
             		   if (createtb >= 0) {
                            System.out.println("TABLE CREATED SUCCESSFULLY!\n");
                        }
             		   for(int i=0; i<booksRating.size(); i++)
             		      {
             	          String query = "INSERT INTO data(id, bookname, ratings, price) VALUES (?, ?, ?, ?)";
             	          PreparedStatement pstmt = conn.prepareStatement(query);
             	          pstmt.setInt(1, i+1);
             		      pstmt.setString(2, booksTitle.get(i));
             		      float pre=Float.parseFloat(booksRating.get(i).substring(0,3));  
             		      pstmt.setFloat(3, pre);
             			  float pree=Float.parseFloat(booksPrice.get(i).substring(1));  
             		      pstmt.setFloat(4, pree);
             	          pstmt.execute();
             	          System.out.println("Data inserted......\n");
             		  }
             		  
             		  System.out.println("TABLE DATA\n"); 
             		  ResultSet rs = stmt.executeQuery("select * from data");
             		  while(rs.next()){
                         //Display values
                         System.out.print("ID: " + rs.getInt("id"));
                         System.out.print(", Book Name: " + rs.getString("bookname"));
                         System.out.print(", Ratings: " + rs.getFloat("ratings"));
                         System.out.println(", Price: " + rs.getFloat("price"));
                      }
             		  
             		  System.out.println("\nDATA WHERE RATINGS ARE GREATER THAN 4.5\n");
             		  rs = stmt.executeQuery("select * from data where ratings > 4.5");
             		  while(rs.next()){
                         //Display values
                         System.out.print("ID: " + rs.getInt("id"));
                         System.out.print(", Book Name: " + rs.getString("bookname"));
                         System.out.print(", Ratings: " + rs.getFloat("ratings"));
                         System.out.println(", Price: " + rs.getFloat("price"));
                      }
                 	}
                 catch(ClassNotFoundException ex) {
                 	   System.out.println("DRIVER FAILED TO LOAD!");
                 	   System.exit(1);
                 	}
          }
          
        }
        	
      }
      
	}

}

