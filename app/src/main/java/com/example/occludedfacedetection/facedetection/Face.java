package com.example.occludedfacedetection.facedetection;

/**
 * To Store Detected Face attributes
 */
public class Face {
    private int width;
    private int height;
    public Face(int width,int height){
        this.width=width;
        this.height=height;
    }
    int getWidth(){
        return width;
    }
    int getHeight(){
        return height;
    }
}
