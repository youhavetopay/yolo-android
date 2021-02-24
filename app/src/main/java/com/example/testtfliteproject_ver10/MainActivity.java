package com.example.testtfliteproject_ver10;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

                    ImageProcessor imageProcessor =
                            new ImageProcessor.Builder()
                            .add(new ResizeOp(416, 416, ResizeOp.ResizeMethod.BILINEAR))
                            .build();

                    TensorImage tImage = new TensorImage(DataType.FLOAT32);

                    tImage.load(img);
                    tImage = imageProcessor.process(tImage);

                    TensorBuffer result =
                            TensorBuffer.createFixedSize(new int[]{1, 2535, 85}, DataType.FLOAT32);

                    Interpreter tf_lite = getTfliteInterpreter("yolov4-416-tiny.tflite");

                    tf_lite.run(tImage.getBuffer(), result.getBuffer());


//                    final String ASSOCIATED_AXIS_LABELS = "label.txt";
//                    List<String> associatedAxisLabels = null;
//
//                    try {
//                        associatedAxisLabels = FileUtil.loadLabels(this, ASSOCIATED_AXIS_LABELS);
//                    } catch (IOException e){
//                        e.printStackTrace();
//                    }
//
//                    TensorProcessor resultProcessor =
//                            new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();
//
//                    Map<String, Float> floatMap;
//                    if(associatedAxisLabels != null){
//                        TensorLabel labels = new TensorLabel(associatedAxisLabels,
//                                resultProcessor.process(result));
//
//                        floatMap = labels.getMapWithFloatValue();
//                        Log.d("predict","success");
//                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("사진 선택 취소");
            }
        }
    }

    // 모델 파일 인터프리터를 생성하는 공통 함수
    // loadModelFile 함수에 예외가 포함되어 있기 때문에 반드시 try, catch가 필요!
    private Interpreter getTfliteInterpreter(String modelPath){
        try {
            return new Interpreter(loadModelFile(MainActivity.this, modelPath));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    // 모델 읽어오는 함수 텐서플로우라이트 홈페이지에 있음
    // MappedByteBuffer 바이트 버퍼를 Interpreter객체에 전달하면 모델 해석을 할 수 있음
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException{
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}