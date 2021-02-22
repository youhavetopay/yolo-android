package com.example.testtfliteproject_ver10;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    private String modelFile = "testTFLiteModel.tflite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    imageView.setImageBitmap(img);

                    ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(1).build();
                    ObjectDetector objectDetector = ObjectDetector.createFromFileAndOptions(getApplicationContext(), modelFile, options);

                    List<Detection> results = objectDetector.detect(TensorImage.fromBitmap(img));
                    System.out.println("awdawdawd        "+results.get(0));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("사진 선택 취소");
            }
        }
    }
}