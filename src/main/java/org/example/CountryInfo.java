package org.example;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CountryInfo extends JFrame implements ActionListener {
    private JTextField countryField;
    private JButton searchButton;
    private JTextArea resultArea;

    public CountryInfo() {
        super("Country Info");

        // create components
        countryField = new JTextField(20);
        searchButton = new JButton("Search");
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);

        // add action listeners
        searchButton.addActionListener(this);

        // add components to panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter a country name: "));
        inputPanel.add(countryField);
        inputPanel.add(searchButton);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        // add panel to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // set frame properties
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new CountryInfo();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String countryName = countryField.getText();

            try {
                // encode the country name for use in the API request URL
                String encodedCountryName = URLEncoder.encode(countryName, "UTF-8");

                // build the URL for the API request
                URL url = new URL("https://restcountries.com/v2/name/" + encodedCountryName + "?fullText=true");

                // create a connection to the API
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gson = new Gson();
                JsonArray countriesArray = gson.fromJson(reader, JsonArray.class);
                reader.close();

                // extract the relevant information from the response
                if (countriesArray.size() > 0) {
                    JsonObject country = countriesArray.get(0).getAsJsonObject();
                    String name = country.get("name").getAsString();
                    String flag = country.get("flag").getAsString();
                    String currency = country.get("currencies").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                    String language = country.get("languages").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();

                    // display the information in the result area
                    resultArea.setText("Name: " + name + "\nFlag: " + flag + "\nCurrency: " + currency + "\nLanguage: " + language);
                } else {
                    resultArea.setText("No country found with the name '" + countryName + "'");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}