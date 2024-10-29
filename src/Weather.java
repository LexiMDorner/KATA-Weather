/*
 * Take Home Kata - Weather
 * Name: Lexi Dorner
 * Created: 10/28
 */

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Gets a city from the user and reports the weather in that city
 */
public class Weather {
    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            displayWeather(getWeather(getCoords(getCity(scan))));
        } catch (Exception e) {
            System.err.println("Please only enter the city in the form city(no spaces), state ex: Milwaukee, WI");
        }
    }

    /**
     * Gets the city from the user returning it as a string array
     * with the first element being the name of the city
     * and the second element being the state the city is in
     * @param scan the scanner to be passed in
     * @return the string array containing the city name and state
     */
    private static String[] getCity(Scanner scan){
        //prompt the user for a location
        System.out.println("Enter the city and state you would like the weather for." +
                " Formated as city name, state abbreviation:");
        String userInput = scan.nextLine();

        //create a string array to store the city name and state code
        String[] location = new String[2];
        location[0] = userInput.substring(0, userInput.indexOf(','));
        location[1] = userInput.substring(userInput.indexOf(',') + 2);

        return location;
    }

    /**
     * gets the coordinates for the entered city
     * @param location the location array containing a city and state
     * @return the coordinates of the in an array with element 1 as latitude
     * and element 2 as longitude
     */
    private static String[] getCoords(String[] location){
        String[] coordinates= new String[2];

        //takes the location entered by the user and puts it into a string format
        // to be able to use the api to get the coordinates of the city
        String apiUrl = "";
        apiUrl += "http://api.openweathermap.org/geo/1.0/direct?q=";
        apiUrl += location[0];
        apiUrl += ",";
        apiUrl += location[1];
        apiUrl += ",US&1={limit}&appid=52cd9e865214b1d19051845052367706";

        //gets the string containing the coordinates of the city
        String apiResponse = callAPI(apiUrl);

        //get the coordinates out of the string and put them in a string array
        //element 1 is the latitude element 2 is the longitude
        coordinates[0]  = apiResponse.substring(apiResponse.indexOf("lat\":") + 5,
                apiResponse.indexOf(",\"lon"));
        coordinates[1]  = apiResponse.substring(apiResponse.indexOf("lon\":") + 5,
                apiResponse.indexOf(",\"cou"));

        return coordinates;
    }

    /**
     * takes in coordinates and gets the weather data for that location
     * @param coordinates the coordinates of the city to get the weather from
     * @return a string array with the weather data
     * element 1 conditions
     * element 2 temperature
     * element 3 feel like temperature
     * element 4 humidity
     * element 5 wind speed
     */
    public static String[] getWeather(String[] coordinates){
        String[] weatherData = new String[5];

        //formats the coordinates into the api url
        String apiUrl = "";
        apiUrl += "https://api.openweathermap.org/data/2.5/weather?lat=";
        apiUrl += coordinates[0];
        apiUrl += "&lon=";
        apiUrl += coordinates[1];
        apiUrl += "&appid=52cd9e865214b1d19051845052367706&units=imperial";

        String apiResponse = callAPI(apiUrl);

        //Get the requested data from the api response string
        weatherData[0] = apiResponse.substring(apiResponse.indexOf("description\":") + 14,
                apiResponse.indexOf(",\"icon")-1);
        weatherData[1] = apiResponse.substring(apiResponse.indexOf("temp\":") + 6,
                apiResponse.indexOf(",\"feels")) + " degrees";
        weatherData[2] = apiResponse.substring(apiResponse.indexOf("feels_like\":") + 12,
                apiResponse.indexOf(",\"temp_min")) + " degrees";
        weatherData[3] = apiResponse.substring(apiResponse.indexOf("humidity\":") + 10,
                apiResponse.indexOf(",\"sea_level")) + "%";
        weatherData[4] = apiResponse.substring(apiResponse.indexOf("speed\":") + 7,
                apiResponse.indexOf(",\"deg")) + " mph";

        return weatherData;
    }

    /**
     * Used to access an api. Returns a string with the information from the website.
     * Note method copied from ChatGPT with minor changes
     * @param apiUrl the url to go to
     * @return a string with the information returned by the api
     */
    private static String callAPI(String apiUrl){
        String apiResponse = "";
        try {
            // Specify the URL of the API
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configure the connection
            conn.setRequestMethod("GET"); // or "POST", "PUT", etc.
            conn.setRequestProperty("Accept", "application/json");

            // Check the response code (200 = OK)
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Read the response line by line
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                apiResponse = response.toString();
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

    /**
     * Displays the weather in a table
     * @param weatherData a string containing weather data for the city
     */
    private static void displayWeather(String[] weatherData){
        //create the column heading for the table
        String[] columnNames = {"Conditions", "Temperature", "Feels Like Temperature",
                    "Humidity", "Wind Speed"};
        //the data contained in the table
        Object[][] data = new Object[1][5];
        data[0] = weatherData;

        //creates the table
        JTable weather = new JTable(data, columnNames);

        //background color is blue if temp < 60 otherwise orange
        if(Double.parseDouble(weatherData[1].substring(0,4)) < 60){
            weather.setBackground(Color.cyan);
        } else{
            weather.setBackground(Color.orange);
        }

        JScrollPane scrollPane = new JScrollPane(weather);
        weather.setFillsViewportHeight(true);

        //creates the JFrame to be able to display the table
        JFrame frame = new JFrame();
        frame.add(scrollPane);
        frame.setSize(800, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
