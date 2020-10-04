package com.example.occludedfacedetection;

/**
 * Base class for Face Detector
 */
public abstract class DetectorBase implements DetectorInterface {
    float ProcessingTime = 0;
    @Override
    public void process(byte[] data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
        ProcessingTime = detectInImage(data, frameMetadata, graphicOverlay);
    }
    @Override
    public float getProcessingRate(){
        return ProcessingTime;
    }
    protected abstract float detectInImage(byte[] image,final FrameMetadata metadata, final GraphicOverlay graphicOverlay);
}
