package android.hardware.camera2.params;

import android.graphics.Point;
import android.graphics.Rect;

public final class Face
{
  public static final int ID_UNSUPPORTED = -1;
  public static final int SCORE_MAX = 100;
  public static final int SCORE_MIN = 1;
  private final Rect mBounds;
  private final int mId;
  private final Point mLeftEye;
  private final Point mMouth;
  private final Point mRightEye;
  private final int mScore;
  
  public Face(Rect paramRect, int paramInt)
  {
    this(paramRect, paramInt, -1, null, null, null);
  }
  
  public Face(Rect paramRect, int paramInt1, int paramInt2, Point paramPoint1, Point paramPoint2, Point paramPoint3)
  {
    checkNotNull("bounds", paramRect);
    if ((paramInt1 < 1) || (paramInt1 > 100)) {
      throw new IllegalArgumentException("Confidence out of range");
    }
    if ((paramInt2 < 0) && (paramInt2 != -1)) {
      throw new IllegalArgumentException("Id out of range");
    }
    if (paramInt2 == -1)
    {
      checkNull("leftEyePosition", paramPoint1);
      checkNull("rightEyePosition", paramPoint2);
      checkNull("mouthPosition", paramPoint3);
    }
    this.mBounds = paramRect;
    this.mScore = paramInt1;
    this.mId = paramInt2;
    this.mLeftEye = paramPoint1;
    this.mRightEye = paramPoint2;
    this.mMouth = paramPoint3;
  }
  
  private static void checkNotNull(String paramString, Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException(paramString + " was required, but it was null");
    }
  }
  
  private static void checkNull(String paramString, Object paramObject)
  {
    if (paramObject != null) {
      throw new IllegalArgumentException(paramString + " was required to be null, but it wasn't");
    }
  }
  
  public Rect getBounds()
  {
    return this.mBounds;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public Point getLeftEyePosition()
  {
    return this.mLeftEye;
  }
  
  public Point getMouthPosition()
  {
    return this.mMouth;
  }
  
  public Point getRightEyePosition()
  {
    return this.mRightEye;
  }
  
  public int getScore()
  {
    return this.mScore;
  }
  
  public String toString()
  {
    return String.format("{ bounds: %s, score: %s, id: %d, leftEyePosition: %s, rightEyePosition: %s, mouthPosition: %s }", new Object[] { this.mBounds, Integer.valueOf(this.mScore), Integer.valueOf(this.mId), this.mLeftEye, this.mRightEye, this.mMouth });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/Face.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */