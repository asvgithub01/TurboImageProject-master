package com.munon.turboimage;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.munon.turboimageview.TurboImageView;


public class SampleActivity extends ActionBarActivity {

    private Button drawButton;
    private TurboImageView turboImageView;
    private Button removeButton;
    private Button textButton;
    private EditText lblText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        turboImageView = (TurboImageView) findViewById(R.id.turboImageView);

        drawButton = (Button) findViewById(R.id.addButton);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turboImageView.loadImages(SampleActivity.this, R.drawable.ic_launcher);
            }
        });


        removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turboImageView.deleteSelectedObject();            }
        });


        lblText = (EditText) findViewById(R.id.lblText);


        lblText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String str = v.getText().toString();
                turboImageView.changeSelectedTextObject(str);

                return false;
            }
        });

        textButton = (Button)findViewById(R.id.lblButton);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turboImageView.loadTextView(SampleActivity.this, lblText.getText().toString());

            }
        });


    }

}
