package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CountryInfo extends JFrame implements ActionListener {
    private final JTextField countryField;
    private final JButton searchButton;
    private final JPanel resultArea;

    public CountryInfo() {
        super("Country Info");

        setSize(500, 500);

        countryField = new JTextField(20);
        searchButton = new JButton("Search");
        resultArea = new JPanel();
        resultArea.setLayout(new BoxLayout(resultArea, BoxLayout.Y_AXIS));

        searchButton.addActionListener(this);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter a country name: "));
        inputPanel.add(countryField);
        inputPanel.add(searchButton);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

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
                String encodedCountryName = URLEncoder.encode(countryName, StandardCharsets.UTF_8);
                encodedCountryName = encodedCountryName.replace("+", "%20");

                URL url = new URL("https://restcountries.com/v2/name/" + encodedCountryName);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gson = new Gson();
                JsonArray countriesArray = gson.fromJson(reader, JsonArray.class);
                reader.close();

                if (countriesArray.size() > 0) {
                    resultArea.removeAll();
                    for (int i = 0; i < countriesArray.size(); i++) {
                        JPanel countryPanel = new JPanel();
                        countryPanel.setLayout(new BorderLayout());

                        JsonObject country = countriesArray.get(i).getAsJsonObject();
                        String name = country.get("name").getAsString();
                        String flag = country.get("flags").getAsJsonObject().get("png").getAsString();
                        String currency = country.get("currencies").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                        String language = country.get("languages").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();

                        String countryInfoText = "Name: " + name +
                                "\nCurrency: " + currency +
                                "\nLanguage: " + language;

                        JTextArea countryInfoArea = new JTextArea(countryInfoText);
                        countryInfoArea.setEditable(false);

                        ImageIcon flagIcon = new ImageIcon(new URL(flag));
                        int flagWidth = 470;
                        double aspectRatio = (double) flagIcon.getIconHeight() / flagIcon.getIconWidth();
                        int flagHeight = (int) Math.round(flagWidth * aspectRatio);
                        Image scaledFlagImage = flagIcon.getImage().getScaledInstance(flagWidth, flagHeight, Image.SCALE_SMOOTH);
                        flagIcon = new ImageIcon(scaledFlagImage);

                        JLabel flagLabel = new JLabel(flagIcon);

                        countryPanel.add(countryInfoArea, BorderLayout.NORTH);
                        countryPanel.add(flagLabel, BorderLayout.CENTER);

                        resultArea.add(countryPanel);
                    }
                    resultArea.revalidate();
                    resultArea.repaint();
                } else {
                    resultArea.removeAll();
                    JLabel noCountryLabel = new JLabel("No country found with the name '" + countryName + "'");
                    resultArea.add(noCountryLabel);
                    resultArea.revalidate();
                    resultArea.repaint();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
