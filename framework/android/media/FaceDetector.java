package android.media;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

public class FaceDetector
{
  private static boolean sInitialized = false;
  private byte[] mBWBuffer;
  private long mDCR;
  private long mFD;
  private int mHeight;
  private int mMaxFaces;
  private long mSDK;
  private int mWidth;
  
  static
  {
    try
    {
      System.loadLibrary("FFTEm");
      nativeClassInit();
      sInitialized = true;
      return;
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      Log.d("FFTEm", "face detection library not found!");
    }
  }
  
  public FaceDetector(int paramInt1, int paramInt2, int paramInt3)
  {
    if (!sInitialized) {
      return;
    }
    fft_initialize(paramInt1, paramInt2, paramInt3);
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mMaxFaces = paramInt3;
    this.mBWBuffer = new byte[paramInt1 * paramInt2];
  }
  
  private native void fft_destroy();
  
  private native int fft_detect(Bitmap paramBitmap);
  
  private native void fft_get_face(Face paramFace, int paramInt);
  
  private native int fft_initialize(int paramInt1, int paramInt2, int paramInt3);
  
  private static native void nativeClassInit();
  
  protected void finalize()
    throws Throwable
  {
    fft_destroy();
  }
  
  public int findFaces(Bitmap paramBitmap, Face[] paramArrayOfFace)
  {
    if (!sInitialized) {
      return 0;
    }
    if ((paramBitmap.getWidth() != this.mWidth) || (paramBitmap.getHeight() != this.mHeight)) {
      throw new IllegalArgumentException("bitmap size doesn't match initialization");
    }
    if (paramArrayOfFace.length < this.mMaxFaces) {
      throw new IllegalArgumentException("faces[] smaller than maxFaces");
    }
    int j = fft_detect(paramBitmap);
    int i = j;
    if (j >= this.mMaxFaces) {
      i = this.mMaxFaces;
    }
    j = 0;
    while (j < i)
    {
      if (paramArrayOfFace[j] == null) {
        paramArrayOfFace[j] = new Face(null);
      }
      fft_get_face(paramArrayOfFace[j], j);
      j += 1;
    }
    return i;
  }
  
  public class Face
  {
    public static final float CONFIDENCE_THRESHOLD = 0.4F;
    public static final int EULER_X = 0;
    public static final int EULER_Y = 1;
    public static final int EULER_Z = 2;
    private float mConfidence;
    private float mEyesDist;
    private float mMidPointX;
    private float mMidPointY;
    private float mPoseEulerX;
    private float mPoseEulerY;
    private float mPoseEulerZ;
    
    private Face() {}
    
    public float confidence()
    {
      return this.mConfidence;
    }
    
    public float eyesDistance()
    {
      return this.mEyesDist;
    }
    
    public void getMidPoint(PointF paramPointF)
    {
      paramPointF.set(this.mMidPointX, this.mMidPointY);
    }
    
    public float pose(int paramInt)
    {
      if (paramInt == 0) {
        return this.mPoseEulerX;
      }
      if (paramInt == 1) {
        return this.mPoseEulerY;
      }
      if (paramInt == 2) {
        return this.mPoseEulerZ;
      }
      throw new IllegalArgumentException();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/FaceDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */