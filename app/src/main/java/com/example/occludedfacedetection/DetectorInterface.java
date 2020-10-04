package com.example.occludedfacedetection;

/**
 *  Interface to process frames with custom models
 */
public interface DetectorInterface {

  /** Processes the images with the underlying machine learning models. */
  void process(byte[] data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);
  float getProcessingRate();
  /** Stops the underlying machine learning model and release resources. */
  void stop();
}
