package com.example.testtfliteproject_ver10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.testtfliteproject_ver10.ml.TestTFLiteModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}