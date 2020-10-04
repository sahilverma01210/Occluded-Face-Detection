package com.example.occludedfacedetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
    Request required Frames Per Second.
 */
public class Main extends AppCompatActivity {
    TextView textView;
    Button detect;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView = (TextView)findViewById(R.id.fps);
        detect = (Button)findViewById(R.id.detect);

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fps = Integer.parseInt(String.valueOf(textView.getText()));
                // Check if requested FPS is within range
                if(fps<=100 && fps>0) {
                    Intent intent = new Intent(Main.this, LivePreviewActivity.class);
                    intent.putExtra("FPS", fps);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"Please enter value from 1 to 100",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
