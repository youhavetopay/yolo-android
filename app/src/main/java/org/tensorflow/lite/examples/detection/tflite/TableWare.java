package org.tensorflow.lite.examples.detection.tflite;

import android.graphics.RectF;

// 식기도구 클래스 ( 일단 숟가락 기준으로 하기  21-05-29 )
public class TableWare  {

    private RectF location;
    private String targetName = null;
    private static TableWare tableWare = new TableWare();

    private TableWare(){

    }

    public static TableWare getInstance(){
        return tableWare;
    }


    public void setLocation(RectF location){
        this.location = location;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public RectF getLocation() {
        return location;
    }

    public String getTargetName() {
        return targetName;
    }
}
