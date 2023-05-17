package com.example.tugasslabactivityweek7;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.weatherInfoTextView);
        editText = findViewById(R.id.cityEditText);
        button = findViewById(R.id.submitButton);
        imageView = findViewById(R.id.logoImageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = editText.getText().toString();
                String apiUrl = "https://api.weatherapi.com/v1/current.json?key=aa87a47d89a445a08f3232501231605&q=" + cityName + "&aqi=no";
                new FetchWeatherTask().execute(apiUrl);
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = readStream(connection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (jsonResult.has("error")) {
                        String errorMessage = jsonResult.getJSONObject("error").getString("message");
                        textView.setText(errorMessage);
                    } else {
                        JSONObject location = jsonResult.getJSONObject("location");
                        JSONObject current = jsonResult.getJSONObject("current");

                        String cityName = location.getString("name");
                        double temperatureC = current.getDouble("temp_c");
                        double temperatureF = current.getDouble("temp_f");
                        String condition = current.getJSONObject("condition").getString("text");

                        String weatherInfo = "City: " + cityName + "\n"
                                + "Temperature: " + temperatureC + "°C / " + temperatureF + "°F\n"
                                + "Condition: " + condition;

                        textView.setText(weatherInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setText("Failed to fetch weather data.");
            }
        }

        private String readStream(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return result.toString();
        }
    }
}
