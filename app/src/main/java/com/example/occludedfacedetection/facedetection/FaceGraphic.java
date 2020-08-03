
package com.example.occludedfacedetection.facedetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.occludedfacedetection.GraphicOverlay;
import com.example.occludedfacedetection.GraphicOverlay.Graphic;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {
  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

  private static final int[] COLOR_CHOICES = {
    Color.BLUE
  };

  private static int currentColorIndex = 0;
  private int facing;
  private final Paint facePositionPaint;
  private final Paint idPaint;
  private final Paint boxPaint;
  private volatile Face detectedFace;

  public FaceGraphic(GraphicOverlay overlay) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
  }

  /**
   * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  public void updateFace(Face face, int facing) {
    detectedFace = face;
    this.facing = facing;
    postInvalidate();
  }

  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
//    Face face = detectedFace;
//    if (face == null) {
//      return;
//    }
//
//    // Draws a bounding box around the face.
//    float x = translateX(face.getBoundingBox().centerX());
//    float y = translateY(face.getBoundingBox().centerY());
//    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
//    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
//    float left = x - xOffset;
//    float top = y - yOffset;
//    float right = x + xOffset;
//    float bottom = y + yOffset;
//    canvas.drawRect(left, top, right, bottom, boxPaint);
  }
}