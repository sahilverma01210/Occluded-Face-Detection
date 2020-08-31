package com.example.occludedfacedetection.facedetection;

/**
 * To Store Detected Face attributes
 */
public class Face {
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private double confidence;
    public Face(int startX,int startY,int endX,int endY,double confidence){
        this.startX=startX;
        this.startY=startY;
        this.endX=endX;
        this.endY=endY;
        this.confidence=confidence;
    }
    int getStartX(){
        return startX;
    }
    int getStartY(){
        return startY;
    }
    int getEndX(){
        return endX;
    }
    int getEndY(){
        return endY;
    }
    double getConfidence(){
        return confidence;
    }
}
