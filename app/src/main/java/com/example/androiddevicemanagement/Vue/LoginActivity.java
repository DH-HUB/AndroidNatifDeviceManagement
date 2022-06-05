package com.example.androiddevicemanagement.Vue;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androiddevicemanagement.R;
import com.example.androiddevicemanagement.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultJwtParser;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bouton = findViewById(R.id.button);

        bouton.setOnClickListener(view -> {

            //Si vous etes sur un emulateur, et que vous voulez faire une requete sur un serveur local : 10.0.2.2
            //Si vous etes sur un téléphone physique idem, et que vous voulez faire une requete sur un serveur local , (mais il faut etre sur le même réseau.)
            //Dans autres cas, il faut indiquer l'IP su serveur
            EditText editTextEmail = findViewById(R.id.editTextEmail);
            EditText editTextPassword = findViewById(R.id.editTextPassword);

            Map<String, String> body = new HashMap<String, String>();
            body.put("email", editTextEmail.getText().toString());
            body.put("motDePasse", editTextPassword.getText().toString());

            JsonObjectRequest requete = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://51.77.245.14:8080/hakima-0.0.1-SNAPSHOT/connexion",
                    new JSONObject(body),
                    reponse -> {
                        try {
                            if (reponse.has("erreur")) {
                                Toast.makeText(this, reponse.getString("erreur"), Toast.LENGTH_LONG).show();
                            } else {
                                String[] splitToken = reponse.getString("token").split("\\.");
                                String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";

                                DefaultJwtParser parser = new DefaultJwtParser();
                                Jwt<?, ?> jwt = parser.parse(unsignedToken);
                                Claims donneesToken = (Claims) ((Jwt<?, ?>) jwt).getBody();

                                Toast.makeText(this, "Bienvenue " + donneesToken.getSubject(), Toast.LENGTH_LONG).show();
                                Toast.makeText(this, reponse.getString("token"), Toast.LENGTH_LONG).show();

                                startActivity(
                                        new Intent(this, ListeMaterielActivity.class)
                                );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    erreur -> {
                        Toast.makeText(this, "ERREUR", Toast.LENGTH_LONG).show();
                        Log.d("volley", erreur.getMessage());
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Access-Control-Allow-Origin", "*");
                    return params;
                }
            };

            RequestManager.getInstance(this).addToRequestQueue(requete);

        });
    }}