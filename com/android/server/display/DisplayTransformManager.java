package com.android.server.display;

import android.opengl.Matrix;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import java.lang.reflect.Array;
import java.util.Arrays;

public class DisplayTransformManager
{
  public static final int LEVEL_COLOR_MATRIX_GRAYSCALE = 200;
  public static final int LEVEL_COLOR_MATRIX_INVERT_COLOR = 300;
  public static final int LEVEL_COLOR_MATRIX_NIGHT_DISPLAY = 100;
  private static final String TAG = "DisplayTransformManager";
  @GuardedBy("mColorMatrix")
  private final SparseArray<float[]> mColorMatrix = new SparseArray(3);
  @GuardedBy("mDaltonizerModeLock")
  private int mDaltonizerMode = -1;
  private final Object mDaltonizerModeLock = new Object();
  @GuardedBy("mColorMatrix")
  private final float[][] mTempColorMatrix = (float[][])Array.newInstance(Float.TYPE, new int[] { 2, 16 });
  
  private static void applyColorMatrix(float[] paramArrayOfFloat)
  {
    IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
    Parcel localParcel;
    if (localIBinder != null)
    {
      localParcel = Parcel.obtain();
      localParcel.writeInterfaceToken("android.ui.ISurfaceComposer");
      if (paramArrayOfFloat != null)
      {
        localParcel.writeInt(1);
        int i = 0;
        while (i < 16)
        {
          localParcel.writeFloat(paramArrayOfFloat[i]);
          i += 1;
        }
      }
      localParcel.writeInt(0);
    }
    try
    {
      localIBinder.transact(1015, localParcel, null, 0);
      return;
    }
    catch (RemoteException paramArrayOfFloat)
    {
      Slog.e("DisplayTransformManager", "Failed to set color transform", paramArrayOfFloat);
      return;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  private static void applyDaltonizerMode(int paramInt)
  {
    IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
    Parcel localParcel;
    if (localIBinder != null)
    {
      localParcel = Parcel.obtain();
      localParcel.writeInterfaceToken("android.ui.ISurfaceComposer");
      localParcel.writeInt(paramInt);
    }
    try
    {
      localIBinder.transact(1014, localParcel, null, 0);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("DisplayTransformManager", "Failed to set Daltonizer mode", localRemoteException);
      return;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  @GuardedBy("mColorMatrix")
  private float[] computeColorMatrixLocked()
  {
    int j = this.mColorMatrix.size();
    if (j == 0) {
      return null;
    }
    float[][] arrayOfFloat = this.mTempColorMatrix;
    Matrix.setIdentityM(arrayOfFloat[0], 0);
    int i = 0;
    while (i < j)
    {
      float[] arrayOfFloat1 = (float[])this.mColorMatrix.valueAt(i);
      Matrix.multiplyMM(arrayOfFloat[((i + 1) % 2)], 0, arrayOfFloat[(i % 2)], 0, arrayOfFloat1, 0);
      i += 1;
    }
    return arrayOfFloat[(j % 2)];
  }
  
  public float[] getColorMatrix(int paramInt)
  {
    float[] arrayOfFloat1 = null;
    synchronized (this.mColorMatrix)
    {
      float[] arrayOfFloat2 = (float[])this.mColorMatrix.get(paramInt);
      if (arrayOfFloat2 == null) {
        return arrayOfFloat1;
      }
      arrayOfFloat1 = Arrays.copyOf(arrayOfFloat2, arrayOfFloat2.length);
    }
  }
  
  public int getDaltonizerMode()
  {
    synchronized (this.mDaltonizerModeLock)
    {
      int i = this.mDaltonizerMode;
      return i;
    }
  }
  
  public void setColorMatrix(int paramInt, float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat != null) && (paramArrayOfFloat.length != 16)) {
      throw new IllegalArgumentException("Expected length: 16 (4x4 matrix), actual length: " + paramArrayOfFloat.length);
    }
    SparseArray localSparseArray = this.mColorMatrix;
    if (paramInt == 0) {}
    label101:
    try
    {
      applyColorMatrix(paramArrayOfFloat);
      return;
    }
    finally {}
    float[] arrayOfFloat = (float[])this.mColorMatrix.get(paramInt);
    if (!Arrays.equals(arrayOfFloat, paramArrayOfFloat))
    {
      if (paramArrayOfFloat != null) {
        break label101;
      }
      this.mColorMatrix.remove(paramInt);
    }
    for (;;)
    {
      applyColorMatrix(computeColorMatrixLocked());
      return;
      if (arrayOfFloat == null) {
        this.mColorMatrix.put(paramInt, Arrays.copyOf(paramArrayOfFloat, paramArrayOfFloat.length));
      } else {
        System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, paramArrayOfFloat.length);
      }
    }
  }
  
  public void setDaltonizerMode(int paramInt)
  {
    synchronized (this.mDaltonizerModeLock)
    {
      if (this.mDaltonizerMode != paramInt)
      {
        this.mDaltonizerMode = paramInt;
        applyDaltonizerMode(paramInt);
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayTransformManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */