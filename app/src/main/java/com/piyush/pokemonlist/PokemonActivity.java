package com.piyush.pokemonlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// take some text thats passed into us and display it on the view
public class PokemonActivity extends AppCompatActivity {
    // creating fields
    private TextView nameTextView;
    private TextView numberTextView;
    private String url;
    private TextView type1TextView;
    private TextView type2TextView;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // to get the values of the fields we have to write intent(pass data from one activity to another)
        // getting the intent (getIntent() is described in AppCompatActivity)
        //String name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");
        //int number = getIntent().getIntExtra("number", 0);

        // setting the contents of the text views in activity_pokemon.xml (fields that we created above)
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);

        //nameTextView.setText(name);
        //numberTextView.setText(Integer.toString(number));

        load();
    }

    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));

                    JSONArray typeEntries = response.getJSONArray("types");
                    for(int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        }
                         else if (slot == 2) {
                             type2TextView.setText(type);
                        }
                    }
                }
                catch (JSONException e) {
                    Log.e("inLoadPokemon", "Json error for pokemon", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("inLoadPokemon", "Pokemon details Error", error);
            }
        });

        requestQueue.add(request);
    }
}
