
package com.example.occludedfacedetection.facedetection;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.example.occludedfacedetection.FrameMetadata;
import com.example.occludedfacedetection.GraphicOverlay;
import com.example.occludedfacedetection.DetectorBase;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Detect Faces from input Frames
 */
public class FaceDetector extends DetectorBase {
  private static final String TAG = "FaceDetector";
  protected Interpreter tflite;
  private static final int NUM_DETECTIONS = 16800;
  private TensorImage inputImageBuffer;
  private  int imageSizeX;
  private  int imageSizeY;
  private static final float IMAGE_MEAN = 0.0f;
  private static final float IMAGE_STD = 1.0f;

  public FaceDetector(Activity my_context) {
    try{
      tflite=new Interpreter(loadmodelfile(my_context));
    }catch (Exception e) {
      e.printStackTrace();
    }
    inputImageBuffer = new TensorImage(tflite.getInputTensor(0).dataType());
    int[] imageShape = tflite.getInputTensor(0).shape(); // {1, height, width, 3}
    imageSizeY = imageShape[1];
    imageSizeX = imageShape[2];
  }

  @Override
  public void stop() {
      tflite.close();
  }

  @Override
  protected void detectInImage(byte[] data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {

    // Convert frame bytes int bitmap
    YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, frameMetadata.getWidth(), frameMetadata.getHeight(), null);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    yuvImage.compressToJpeg(new Rect(0, 0, frameMetadata.getWidth(), frameMetadata.getHeight()), 100, os);
    byte[] jpegByteArray = os.toByteArray();
    Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);

    // Prepare input and output
    inputImageBuffer = loadImage(bitmap);
    Object[] inputArray = {inputImageBuffer.getBuffer()};
    Map<Integer, Object> outputMap = new HashMap<>();
    float[][][] outputClasses = new float[1][NUM_DETECTIONS][2];
    float[][][] outputLocations = new float[1][NUM_DETECTIONS][4];
    float[][][] outputScores = new float[1][NUM_DETECTIONS][10];
    outputMap.put(0, outputClasses);
    outputMap.put(1, outputLocations);
    outputMap.put(2, outputScores);

    // Run Interpreter
    tflite.runForMultipleInputsOutputs(inputArray,outputMap);

    // Visualise results
    for(int i=0;i<NUM_DETECTIONS-1;i++){
      if(outputClasses[0][i][1]>0.999){
        System.out.println(Arrays.toString(outputLocations[0][i]));
      }
    }
  }

  private TensorImage loadImage(final Bitmap bitmap) {
    // Loads bitmap into a TensorImage.
    inputImageBuffer.load(bitmap);

    // Creates processor for the TensorImage.
    int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
    // TODO(b/143564309): Fuse ops inside ImageProcessor.
    ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                    .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                    .add(getPreprocessNormalizeOp())
                    .build();
    return imageProcessor.process(inputImageBuffer);
  }

  private TensorOperator getPreprocessNormalizeOp() {
    return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
  }

  private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
    AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model_lite.tflite");
    FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
    FileChannel fileChannel=inputStream.getChannel();
    long startoffset = fileDescriptor.getStartOffset();
    long declaredLength=fileDescriptor.getDeclaredLength();
    return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
  }
}
