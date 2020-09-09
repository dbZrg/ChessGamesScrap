
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.jaunt.*;
import com.jaunt.util.HandlerForText;


public class ChessScrap {

	public static void main(String[] args) {

		queryString urlString = new queryString();
		List<ChessGame> chessGames = new ArrayList<ChessGame>(); 

		//building get url
		urlString.addUser("DrNykterstein");  // Add user first!
		urlString.addFilter("since", "01/09/2020" );
		
		//examples of additional filters
		
		//urlString.addFilter("until", "06/09/2020" );
		//urlString.addFilter("rated", "true" );
		//urlString.addFilter("color", "white" );
		//...
		
		//DrNykterstein  --> Magnus Carlsen - Svjetski prvak
		 String[]  gamesSplit;
		 String gamesString;
		try{
			 //fetch all games with filters (pgn format)   - Jaunt 1.6.0.  +  https://lichess.org/api#operation/apiGamesUser 
			 UserAgent userAgent = new UserAgent();  
			 HandlerForText handlerForText = new HandlerForText();
			 userAgent.setHandler("application/x-chess-pgn", handlerForText);
			 userAgent.sendGET(urlString.defaultUrl);
			 gamesString = handlerForText.getContent();
			 
			 //parsing pgn games info
			 gamesString = gamesString.replace("[", "");
			 gamesString = gamesString.replace("]", "");
			 gamesString = gamesString.replace("\"", "");
			 gamesSplit = gamesString.split("\n\n\n");
			  
		            
		      for (String gameString : gamesSplit){
		    	  ChessGame game = new ChessGame();
		    	  String[] gameInfo = gameString.split("\n");
		    	  for(String str : gameInfo) {
		    	  
		    		  //add game info to ChessGame object
		    	  if(str.matches(".*\\bDate\\b.*")) {
		    		  str = str.replace("Date", "");
		    		  game.date = str;
		    	  }else if(str.matches(".*\\bWhite\\b.*")){
		    		  str = str.replace("White", "");
		    		  game.white = str;
		    		  
		    	  }else if(str.matches(".*\\bBlack\\b.*")){
		    		  str = str.replace("Black", "");
		    		  game.black = str;
		    		  
		    	  }else if(str.matches(".*\\bResult\\b.*")){
		    		  str = str.replace("Result", "");
		    		  game.result = str;
		    		  
		    	  }else if(str.matches(".*\\bWhiteElo\\b.*")){
		    		  str = str.replace("WhiteElo", "");
		    		  game.whiteElo = str;
		    		  
		    	  }else if(str.matches(".*\\bBlackElo\\b.*")){
		    		  str = str.replace("BlackElo", "");
		    		  game.blackElo = str;
		    		  
		    	  }else if(str.matches(".*\\bTimeControl\\b.*")){
		    		  str = str.replace("TimeControl", "");
		    		  game.timeControl = str;
		    		  
		    	  }else if(str.matches(".*\\b1.\\b.*")){		    		
		    		  game.pgn = str;
		    		  
		    	  }
		    	  }
		    	  chessGames.add(game);
		      }      
			}
			catch(JauntException e){         //if an HTTP/connection error occurs, handle JauntException.
			  System.err.println(e);
			}
		
		 
		  String connectionUrl =
	                "jdbc:sqlserver://DESKTOP-4CVVHUI\\SQLEXPRESS:1433;"
	                        + "database=ChessGames;"
	                        + "user=Korisnik;"
	                        + "password=;"
	                        + "encrypt=false;"
	                        + "trustServerCertificate=false;"
	                        + "loginTimeout=30;"
						    + "integratedSecurity=true;";

	        String insertSql = "INSERT INTO ChessGamesTable (White, Black, WhiteElo, BlackElo, Result, GameDate ,TimeControl, pgn, fens) VALUES "
	                + "(?, ?, ?, ?, ?, ?, ?, ?, ?);";
	     // Connecting to db
	        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
	            
	        	System.out.print("konekcija uspjela");
	        	PreparedStatement ps = connection.prepareStatement(insertSql);            
	        	for (ChessGame game : chessGames) {
	        	    ps.setString(1, game.white);
	        	    ps.setString(2, game.black);
	        	    ps.setString(3, game.whiteElo);
	        	    ps.setString(4, game.blackElo);
	        	    ps.setString(5, game.result);
	        	    ps.setString(6, game.date);
	        	    ps.setString(7, game.timeControl);
	        	    ps.setString(8, game.pgn);
	        	    ps.setString(9, game.fens);
	        	    
	        	    ps.addBatch();
	        	}
	        	ps.executeBatch();
	       
	        	
	        }
	        // Handle any errors that may have occurred.
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	}

}
