package gt.edu.umg.proyectofinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainMenu extends AppCompatActivity {

    Button buttonPlay, buttonMemories, buttonInfo;

    private static final int REQUEST_CODE = 100; // Define the request code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonInfo = findViewById(R.id.buttonInfo);
        buttonMemories = findViewById(R.id.buttonMemories);


        checkPermissions();

        buttonPlay.setOnClickListener(v ->{
            Intent intent = new Intent(this, Blackjack.class);
            startActivity(intent);
        });

        buttonInfo.setOnClickListener(v ->{
            Intent intent = new Intent(this, Instructions.class);
            startActivity(intent);
        });

        buttonMemories.setOnClickListener(v->{
            Intent intent = new Intent(this, Recuerdos.class);
            startActivity(intent);
        });

    }
    private void checkPermissions() {
        // List of all the permissions you want to check
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION // Use ACCESS_FINE_LOCATION for location access
        };

        // Flag to indicate if any permission is not granted
        boolean allPermissionsGranted = true;

        // Check each permission
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false; // At least one permission is not granted
                break; // No need to check further
            }
        }

        // If not all permissions are granted, request them
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        } else {
            // All permissions are already granted, proceed with your functionality
        }
    }
}