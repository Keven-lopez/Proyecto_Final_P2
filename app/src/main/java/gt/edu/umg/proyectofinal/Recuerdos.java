package gt.edu.umg.proyectofinal;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import gt.edu.umg.proyectofinal.Database.DatabaseHelper;
import android.net.Uri;

public class Recuerdos extends AppCompatActivity {
    private TableLayout tableLayout;
    private Button buttonRegresar,button_Borrar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuerdos);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Get reference to the TableLayout
        tableLayout = findViewById(R.id.tableRegistries);
        buttonRegresar = findViewById(R.id.buttonReturn);
        button_Borrar = findViewById(R.id.buttonBorrar);

        buttonRegresar.setOnClickListener(v->{
            finish();
        });

        button_Borrar.setOnClickListener(v->{
            dbHelper.deleteAllGameResults();
            finish();
        });

        // Display results from the database
        displayResults();
    }

    private void displayResults() {
        // Clear existing rows (if any)
        tableLayout.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Result"));
        headerRow.addView(createTextView("Latitude"));
        headerRow.addView(createTextView("Longitude"));
        headerRow.addView(createTextView("Image"));
        tableLayout.addView(headerRow);

        // Fetch data from the SQLite database
        Cursor cursor = dbHelper.getAllGameResults();


        while (cursor.moveToNext()) {
            String result = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESULT));
            double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE));
            String imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH));

            TableRow row = new TableRow(this);
            row.addView(createTextView(result));
            row.addView(createTextView(String.valueOf(latitude)));
            row.addView(createTextView(String.valueOf(longitude)));
            row.addView(createImageView(imagePath)); // Load image into ImageView

            tableLayout.addView(row);
        }
        cursor.close(); // Close the cursor
    }

    // Method to create a TextView
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView.setTextColor(0xFFFFFFFF);
        return textView;
    }

    // Method to create an ImageView from image path using BitmapFactory
    private ImageView createImageView(String imagePath) {
        ImageView imageView = new ImageView(this);
        //imageView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                0, // Width (0 means it will use weight)
                TableRow.LayoutParams.WRAP_CONTENT // Height
        );
        layoutParams.weight = 1f; // Weight for distributing space in the TableRow
        imageView.setLayoutParams(layoutParams);

        // Set scale type and adjust bounds
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);



        Uri uri = Uri.parse(imagePath);
        imageView.setImageURI(uri);

        return imageView;
    }

}