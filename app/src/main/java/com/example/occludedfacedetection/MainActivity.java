package com.example.occludedfacedetection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Experimental Activity - For Static images from Storage
 */
public class MainActivity extends AppCompatActivity {

    private float[][][] outputLocations;
    // outputClasses: array of shape [Batchsize, NUM_DETECTIONS]
    // contains the classes of detected boxes
    private float[][][] outputClasses;
    // outputScores: array of shape [Batchsize, NUM_DETECTIONS]
    // contains the scores of detected boxes
    private float[][][] outputScores;
    // numDetections: array of shape [Batchsize]
    // contains the number of detected boxes
//    private float[] numDetections;
    private static final int NUM_DETECTIONS = 16800;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private  TensorBuffer outputProbabilityBuffer;
    private  TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    ImageView imageView;
    Uri imageuri;
    Button buclassify;
    TextView classitext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.image);
        buclassify=(Button)findViewById(R.id.classify);
        classitext=(TextView)findViewById(R.id.classifytext);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),12);
            }
        });

        try{
            tflite=new Interpreter(loadmodelfile(this));
        }catch (Exception e) {
            e.printStackTrace();
        }
//        tflite.getOutputTensorCount();
//        int[] shape=tflite.getInputTensor(0).shape();
//        Log.v("size", String.valueOf(shape[3]));
        buclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 1;
                int[] probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(0, 255)).build();

                inputImageBuffer = loadImage(bitmap);
                Object[] inputArray = {inputImageBuffer.getBuffer()};
                Map<Integer, Object> outputMap = new HashMap<>();
                outputClasses = new float[1][NUM_DETECTIONS][2];
                outputLocations = new float[1][NUM_DETECTIONS][4];
                outputScores = new float[1][NUM_DETECTIONS][10];
//                numDetections = new float[1];
                outputMap.put(0, outputClasses);
                outputMap.put(1, outputLocations);
                outputMap.put(2, outputScores);
//                outputMap.put(3, numDetections);
                tflite.runForMultipleInputsOutputs(inputArray,outputMap);
//                tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
////                probabilityProcessor.process(outputProbabilityBuffer);
//                TensorBuffer tensorBuffer=probabilityProcessor.process(outputProbabilityBuffer);
//                ByteBuffer byteBuffer = tensorBuffer.getBuffer();

//                float[][][] out= (float[][][]) outputMap.get(1);

//                final ArrayList<Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//                for (int i = 0; i < NUM_DETECTIONS; ++i) {
//                    final RectF detection =
//                            new RectF(
//                                    outputLocations[0][i][1] * 640,
//                                    outputLocations[0][i][0] * 640,
//                                    outputLocations[0][i][3] * 640,
//                                    outputLocations[0][i][2] * 640);
//                    System.out.println(detection);
//                    // SSD Mobilenet V1 Model assumes class 0 is background class
//                    // in label file and class labels start from 1 to number_of_classes+1,
//                    // while outputClasses correspond to class index from 0 to number_of_classes
////                    int labelOffset = 1;
////                    recognitions.add(
////                            new Recognition(
////                                    "" + i,
////                                    labels.get((int) outputClasses[0][i] + labelOffset),
////                                    outputScores[0][i],
////                                    detection));
//                }

                final Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0f);
                Bitmap croppedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                final Canvas canvas=new Canvas(croppedBitmap);
                for(int i=0;i<NUM_DETECTIONS-1;i++){
                    if(outputClasses[0][i][1]>0.999){
                        float x = outputLocations[0][i][0] * 640;
                        float y = outputLocations[0][i][1] * 640;
                        float w = outputLocations[0][i][2] * 640;
                        float h = outputLocations[0][i][3] * 640;
                        final RectF detection = new RectF(x,y,x+h,y+w);
                        canvas.drawRect(detection,paint);
                    }
                    System.out.println(Arrays.toString(outputLocations[0][i]));
                }
                imageView.setImageBitmap(croppedBitmap);

//                Log.v("Success:", Arrays.deepToString(out[0]));

//                showresult();
            }
        });



    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model_lite.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
//    private TensorOperator getPostprocessNormalizeOp(){
//        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
//    }
//
//    private void showresult(){
//
//        try{
//            labels = FileUtil.loadLabels(this,"dict.txt");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        Map<String, Float> labeledProbability =
//                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
//                        .getMapWithFloatValue();
//        float maxValueInMap =(Collections.max(labeledProbability.values()));
//
//        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
//            if (entry.getValue()==maxValueInMap) {
//                classitext.setText(entry.getKey());
//            }
//        }
//    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null) {
            imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}