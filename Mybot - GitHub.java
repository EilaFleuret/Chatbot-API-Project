import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.net.ssl.HttpsURLConnection;

import org.jibble.pircbot.*;
import org.omg.CORBA.portable.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//This is a simple api project that uses webchat's pircbot to interface with the user. There are two website the programs pulls info from,
//openweathermap.org and official-joke-api.appspot.com. The info is read and parsed as a JSON file before being sent to the user using the 
//chatbot

//For this project you need gson-2.6.2.jar from Maven.org added. You can got to the webchat website from here: https://webchat.freenode.net
class MyBotMain {
    
    public static void main(String[] args) {
        // Now start our bot up.
        MyBot bot = new MyBot();
        String channel = "#Pircbot Chat";
        
        // Enable debugging output.
        bot.setVerbose(true);
        
        try {
            // Connect to the IRC server.
            bot.connect("irc.freenode.net");
        }
        catch (Exception e) {
        	// Outputs error message
        	System.out.println("Can’t connect:" +e);
        	return;
        }

        // Join the #pircbot channel.
        bot.joinChannel(channel);
        
        // Greeting message
        bot.sendMessage(channel, "Hi! I'm a simple chatbot. Enter a message and I'll try replying to you! Ask me about the weather or about a funny joke.");
        
    } 
}//end of main
/////////////////////////////////////////////////////
////////////////////MyBot////////////////////////////
 class MyBot extends PircBot {
	 //variables
	public String jsonInput;
	
	//constructor
    public MyBot() {
        this.setName("Pircbot");
    }
    
    //method
    public void onMessage(String channel, String sender,
            String login, String hostname, String message) {
    	//puts the user message into a string to get the city/pokemon #
    	String userInput = message;
		String array[] = userInput.split(" ");
		int length = array.length;
		userInput = array[--length];
    	
    	// Outputs time
		if (message.equalsIgnoreCase("time")) {
    		String time = new java.util.Date().toString();
    		sendMessage(channel, sender + ": The time is now " + time);
    	}
		
    	// Responds to "Hello"
    	if (message.equalsIgnoreCase("Hello") || message.equalsIgnoreCase("Hi")) {
    		sendMessage(channel, sender + ": Hi! How are you?");
    	}
    	if (message.contains("How are you?")) {
    		sendMessage(channel, sender + ": Good. I didn't exist in 2 minutes ago so this is exciting!");
    	}
    	
    	//Weather protocol//////////////////////
    	if (message.contains("Weather") || message.contains("weather")) {
    		//creates the weather class and gets the json string
    		String city = userInput;
    		Weather w = new Weather(city);
    		jsonInput = w.getReadInput();
    		
    		//gets the temp from the weather functions and truncates
    		double temp = w.parseTemp(jsonInput);
    		double maxTemp = w.parseTempMax(jsonInput);
    		double minTemp = w.parseTempMin(jsonInput);
    		NumberFormat formatter = new DecimalFormat("#0.00");
    		
    		//outputs the temps into the chatbot
    		sendMessage(channel, sender + ": The weather is going to be " +formatter.format(temp)+ 
    				" with a high of " +formatter.format(maxTemp)+ " and a low of " +formatter.format(minTemp));
    	}
    	
    	//book protocol////////////////////////
    	if (message.contains("joke") || message.contains("Joke")) {
    		//creates the weather class and gets the json string
    		jokes j = new jokes();
    		jsonInput = j.getReadInput();
    		
    		//gets the temp from the weather functions and truncates
    		String joke = j.parseTitle(jsonInput);
    		
    		//outputs the temps into the chatbot
    		sendMessage(channel, sender + " " +joke);
    		
    	}
    }
    
    // Responds to "Hello"
}//end of MyBot
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
class Weather{
	//variable
	public String readInput;
	
	//constructors
	Weather(){
	}
	Weather(String c){
		//Variables
		String key = "&APPID=16cb4ed1217c2b9014a5cc54ef34d7d5";
		String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=" +c+ key;
		
		URL url;
		try {
			url = new URL(weatherURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			readInput = rd.readLine();
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//methods
	public String getReadInput() { //returns readInput
		return readInput;
	}
////////////////////////returns temp/////////////////////////////////////
	public static double parseTemp(String json) { 
		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		String cityName = object.get("name").getAsString();
		JsonObject main = object.getAsJsonObject("main");
		double temp = main.get("temp").getAsDouble();
		
		//converts temp from kelvin to *F
		temp = ((temp - 273.15) * (9.0/5.0)) + 32;
		
		return temp; 
	}
////////////////////////returns MAX temp/////////////////////////////////////
	public static double parseTempMax(String json) {
		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		String cityName = object.get("name").getAsString();
		JsonObject main = object.getAsJsonObject("main");
		double temp = main.get("temp_max").getAsDouble();

		//converts temp from kelvin to *F
		temp = ((temp - 273.15) * (9.0/5.0)) + 32;
		
		return temp; 
	}
/////////////////////////returns MIN temp/////////////////////////////////////
	public static double parseTempMin(String json) {
		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		String cityName = object.get("name").getAsString();
		JsonObject main = object.getAsJsonObject("main");
		double temp = main.get("temp_min").getAsDouble();

		//converts temp from kelvin to *F
		System.out.println(temp);
		temp = ((temp - 273.15) * (9.0/5.0)) + 32;
		System.out.println(temp);
		
		return temp; 
	}
}//end of Weather
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
class jokes{
	//variable
	public String readInput;
	
	//constructors
	jokes(){
		//Variables
		String jokesURL = "https://official-joke-api.appspot.com/random_joke";
				
		URL url;
		try {
			url = new URL(jokesURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			readInput = rd.readLine();
			rd.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	//methods
	public String getReadInput() { //returns readInput
		return readInput;
	}
/////////////returns book name///////////////////////////
	public static String parseTitle(String json) {
		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		String setup = object.get("setup").getAsString();
		String punchline = object.get("punchline").getAsString();
		String joke = setup+ " " +punchline;
		return joke;
	}
}