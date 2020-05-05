package com.piyush.pokemonlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Adapter class in recycler view is used to define what data is going to be displayed in the recycler view and then how that data showuld appear on the screen
// this class hence is used to define all the data in the recycler view
// view holder is used to hold a view (allowing to manipulate whats on the screen)
public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {
    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        // adding fields from pokemon_row.xml for the view holder to hold
        public LinearLayout containerView;
        public TextView textView;

        PokemonViewHolder(View view) {
            super(view); // just to make sure that we are calling super in case those super classes are doing something imp
            containerView = view.findViewById(R.id.pokemon_row);
            textView = view.findViewById(R.id.pokemon_row_textView);

            // when the row is tapped
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // getting the pokemon
                    Pokemon current = (Pokemon) containerView.getTag(); // getTag returns an object so we need to cast to (Pokemon) here

                    // Using intent to pass the data to other activity (second argument is where I have to pass the data)
                    Intent intent = new Intent(v.getContext(), PokemonActivity.class);
                    // giving out the values to be taken
                    //intent.putExtra("name", current.getName());
                    //intent.putExtra("number", current.getNumber());

                    intent.putExtra("url", current.getUrl());

                    // calling start activity with the intent
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    /*
    // hardcoding some data (for onBindViewHolder method)
    private List<Pokemon> pokemon = Arrays.asList(
            new Pokemon("Pikachu", 1),
            new Pokemon("Raichu", 2),
            new Pokemon("Bulbasaur", 3)
    );
     */

    private List<Pokemon> pokemon = new ArrayList<>();
    private RequestQueue requestQueue;

    PokemonAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    // method to load data from api
    public void loadPokemon() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results"); // takes results named array from the api response
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        pokemon.add(new Pokemon(
                                name.substring(0, 1).toUpperCase() + name.substring(1),
                                result.getString("url")
                        ));
                    }
                    notifyDataSetChanged();
                }
                catch (JSONException e) {
                    Log.e("inLoadPokemon", "Json error for array", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("inLoadPokemon", "Pokemon List Error", error);
            }
        });

        requestQueue.add(request);
    }

    // to create a new view holder
    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // go from layout file to view using LayoutInflater
        // .from is says where do I want to get my layout from
        // inflate is used to go from some XML file to a Java file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_row, parent, false);

        // return as its return type is PokemonViewHolder giving it a view to hold
        return new PokemonViewHolder(view);
    }
    // we have now converted the XML file into a Java object in memory


    // this method is called whenever a view scrolls into screen and we need to set the value(s) inside of this row
    // ie, we set the different properties of the view that we have created
    // going from Model to View (what a Controller does)
    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon current = pokemon.get(position);

        // setting the text to the row
        holder.textView.setText(current.getName());

        // pass data to other activity starting here (we now have access to current pokemon from the view holder) (later we will use intent)
        // see PokemonViewHolder for more understanding (setting and getting tag). Since this onBindViewHolder method contains the info of the pokemon we are setting the tag here and getting it somewhere else
        holder.containerView.setTag(current);
    }

    // how many rows to display

    @Override
    public int getItemCount() {
        return pokemon.size();
    }
}
