package gt.edu.umg.proyectofinal;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Instructions extends AppCompatActivity {
    Button buttonRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        buttonRegresar = findViewById(R.id.buttonRegresar);

        buttonRegresar.setOnClickListener(v ->{
            finish();
        });

    }
}