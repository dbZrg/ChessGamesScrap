import java.text.SimpleDateFormat;
import java.util.Date;

public class queryString {
	String defaultUrl = "https://lichess.org/api/games/user/";
	
	public void addUser(String userName) {
		defaultUrl += userName + "?";
	}
	
	public void addFilter(String filterType, String filterValue) {
		
		if(filterType == "since" || filterType == "until") {
			filterValue = DateToTimestamp(filterValue);
		}
		defaultUrl += filterType + "=" + filterValue+ "&";
	}
	

	public long getTimeStampOfNow () {
		long s = System.currentTimeMillis();
		return s;
	}
	
	public String DateToTimestamp(String myDate){
		myDate = myDate + " 00:00:01";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = null ;
		try {
			date = sdf.parse(myDate);	
			long timestamp = date.getTime();
			return Long.toString(timestamp);
		}
		catch(Exception e) {
			System.out.print("Parsing failed");
			return null;
		}
		
	}
	
}
