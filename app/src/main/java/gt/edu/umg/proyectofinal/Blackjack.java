package gt.edu.umg.proyectofinal;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gt.edu.umg.proyectofinal.Database.DatabaseHelper;

public class Blackjack extends AppCompatActivity {

    private TextView dealerScoreText, playerScoreText, gameResultText, moneyText;
    private LinearLayout dealerCardsContainer, playerCardsContainer;
    private Button hitButton, standButton, resetButton, returnButton;

    private List<Integer> deck = new ArrayList<>();
    private int playerScore = 0;
    private int dealerScore = 0;
    private int money = 500;
    double userLatitude = 0;
    double userLongitude = 0;
    private boolean gameEnded = false;
    private static final int REQUEST_LOCATION_PERMISSION = 1; // Request code for location permission
    private static final int REQUEST_IMAGE_CAPTURE = 2; // Request code for taking a picture
    private Uri imageUri; // URI for the captured image
    private String result;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackjack);

        dealerScoreText = findViewById(R.id.dealer_score);
        playerScoreText = findViewById(R.id.player_score);
        gameResultText = findViewById(R.id.game_result);
        hitButton = findViewById(R.id.hit_button);
        standButton = findViewById(R.id.stand_button);
        resetButton = findViewById(R.id.reset_button);
        returnButton = findViewById(R.id.return_button);
        dealerCardsContainer = findViewById(R.id.dealer_cards_container);
        playerCardsContainer = findViewById(R.id.player_cards_container);
        moneyText = findViewById(R.id.textMonedas);


        requestLocationPermission();
        resetGame();

        hitButton.setOnClickListener(v -> {
            if (!gameEnded) {
                int cardIndex = drawCard();
                playerScore += cardValue(cardIndex);
                displayCard(cardIndex, playerCardsContainer);
                updateScores();
                if (playerScore > 21) {
                    gameResultText.setText("You Bust! Dealer Wins!");
                    gameEnded = true;
                    result = "Perdiste";
                    Toast.makeText(this, "Perdiste 50 monedas", Toast.LENGTH_SHORT).show();
                    moneyText.setText("Monedas: " + money);
                }
            }
        });

        standButton.setOnClickListener(v -> {
            if (!gameEnded) {
                while (dealerScore < 17) {
                    int cardIndex = drawCard();
                    dealerScore += cardValue(cardIndex);
                    displayCard(cardIndex, dealerCardsContainer);
                }
                updateScores();
                if (dealerScore > 21 || playerScore > dealerScore) {
                    gameResultText.setText("You Win!");
                    result = "Ganaste";
                    Toast.makeText(this, "Ganaste 50 monedas", Toast.LENGTH_SHORT).show();
                    money += 50;
                    moneyText.setText("Monedas: " + money);
                } else if (dealerScore == playerScore) {
                    gameResultText.setText("It's a Draw!");
                    result = "Empate";
                    Toast.makeText(this, "No perdiste monedas", Toast.LENGTH_SHORT).show();
                    money += 50;
                    moneyText.setText("Monedas: " + money);
                } else {
                    gameResultText.setText("Dealer Wins!");
                    result = "Perdiste";
                    Toast.makeText(this, "Perdiste 50 monedas", Toast.LENGTH_SHORT).show();
                    moneyText.setText("Monedas: " + money);
                }
                gameEnded = true;
            }
        });

        resetButton.setOnClickListener(v -> {
            if (gameEnded){
                takePicture();
                getUserLocation();
                DatabaseHelper db = new DatabaseHelper(this);
                db.insertGameResult(result,userLatitude,userLongitude, String.valueOf(imageUri));
                resetGame();
            }
            else{
                Toast.makeText(this,"Tienes que terminar el juego primero", Toast.LENGTH_SHORT).show();
            }
        });

        returnButton.setOnClickListener(v->{
            finish();
        });
    }

    private void resetGame() {
        playerScore = 0;
        dealerScore = 0;
        money -= 50;
        Toast.makeText(this, "Apuestas 50 monedas", Toast.LENGTH_SHORT).show();
        moneyText.setText("Monedas: " + money);
        gameEnded = false;
        gameResultText.setText("Welcome to Blackjack!");

        deck.clear();
        for (int i = 1; i <= 52; i++) { // 52 cards
            deck.add(i);
        }
        Collections.shuffle(deck);

        dealerCardsContainer.removeAllViews();
        playerCardsContainer.removeAllViews();

        int card1 = drawCard();
        int card2 = drawCard();
        playerScore += cardValue(card1) + cardValue(card2);
        displayCard(card1, playerCardsContainer);
        displayCard(card2, playerCardsContainer);

        int dealerCard = drawCard();
        dealerScore += cardValue(dealerCard);
        displayCard(dealerCard, dealerCardsContainer);

        updateScores();
    }

    private int drawCard() {
        if (deck.isEmpty()) resetGame();
        return deck.remove(0);
    }

    private int cardValue(int cardIndex) {
        // Determine rank based on card index
        int rank = (cardIndex - 1) % 13 + 1; // Ranks 1 to 13, where Ace is 1, Jack is 11, Queen is 12, King is 13

        // Map rank to card value for Blackjack
        if (rank >= 10) { // Face cards (Jack, Queen, King) are worth 10
            return 10;
        } else if (rank == 1) { // Ace is typically worth 1 (or 11 based on hand logic, which can be added later)
            return 1;
        } else {
            return rank; // Number cards return their rank as value (2-9)
        }
    }

    private void displayCard(int cardIndex, LinearLayout container) {
        ImageView cardImage = new ImageView(this);

        // Determine rank and suit based on cardIndex
        int rank = (cardIndex - 1) % 13 + 1; // Ranks 1 to 13
        int suit = (cardIndex - 1) / 13;      // 0 = hearts, 1 = clubs, 2 = diamonds, 3 = spades

        // Map rank and suit to names
        String[] ranks = {"ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king"};
        String[] suits = {"hearts", "clubs", "diamonds", "spades"};

        String rankName = ranks[rank - 1];  // Adjust for 0-based indexing
        String suitName = suits[suit];

        // Construct the drawable name
        String imageName = rankName + "_of_" + suitName;

        // Fetch the resource ID for the drawable
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (resId != 0) {
            cardImage.setImageResource(resId);
        } else {

        }

        cardImage.setLayoutParams(new LinearLayout.LayoutParams(250, 325)); // Set card size
        container.addView(cardImage);
    }

    private void updateScores() {
        playerScoreText.setText("Player Score: " + playerScore);
        dealerScoreText.setText("Dealer Score: " + dealerScore);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the content values for the new image
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            // Insert the image into MediaStore and get the URI
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (imageUri != null) {

                Toast.makeText(this, "Image saved to:\n" + imageUri.toString(), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserLocation() {
        // Create a location request with high accuracy using LocationRequest.Builder
        LocationRequest locationRequest = new LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(10000) // 10 seconds
                .setMaxUpdateDelayMillis(15000) // 15 seconds max wait time
                .build();

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                }
                // Stop location updates after getting the location
                fusedLocationClient.removeLocationUpdates(this);
            }
        }, getMainLooper());
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getUserLocation(); // Permission already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation(); // If permission is granted, get the location
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}