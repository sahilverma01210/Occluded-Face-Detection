package com.example.occludedfacedetection;

/**
 * Base class for Face Detector
 */
public abstract class DetectorBase implements DetectorInterface {

    @Override
    public void process(byte[] data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
        detectInImage(data, frameMetadata, graphicOverlay);
    }

    protected abstract void detectInImage(byte[] image,final FrameMetadata metadata, final GraphicOverlay graphicOverlay);
}
