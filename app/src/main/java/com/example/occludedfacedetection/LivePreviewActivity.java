
package com.example.occludedfacedetection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display detected faces.
 */
public final class LivePreviewActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {
  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;
  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_live_preview);

    preview = (CameraSourcePreview) findViewById(R.id.framePreview);
    graphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
    textView = (TextView)findViewById(R.id.fpsText);

    if (allPermissionsGranted()) {
      createCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  @SuppressLint("SetTextI18n")
  private void createCameraSource() {
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }
    textView.setText(cameraSource.getFPS() + " FPS");
    cameraSource.setMachineLearningFrameProcessor(this);
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    return false;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (allPermissionsGranted()) {
      createCameraSource();
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void startPreview() {
    if (cameraSource != null) {
      try {
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    startPreview();
  }

  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }
}
