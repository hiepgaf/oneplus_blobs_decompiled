package android.database;

import android.content.res.Resources;
import android.database.sqlite.SQLiteClosable;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Process;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import dalvik.system.CloseGuard;

public class CursorWindow
  extends SQLiteClosable
  implements Parcelable
{
  public static final Parcelable.Creator<CursorWindow> CREATOR = new Parcelable.Creator()
  {
    public CursorWindow createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CursorWindow(paramAnonymousParcel, null);
    }
    
    public CursorWindow[] newArray(int paramAnonymousInt)
    {
      return new CursorWindow[paramAnonymousInt];
    }
  };
  private static final String STATS_TAG = "CursorWindowStats";
  private static int sCursorWindowSize = -1;
  private static final LongSparseArray<Integer> sWindowToPidMap = new LongSparseArray();
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final String mName;
  private int mStartPos;
  public long mWindowPtr;
  
  private CursorWindow(Parcel paramParcel)
  {
    this.mStartPos = paramParcel.readInt();
    this.mWindowPtr = nativeCreateFromParcel(paramParcel);
    if (this.mWindowPtr == 0L) {
      throw new CursorWindowAllocationException("Cursor window could not be created from binder.");
    }
    this.mName = nativeGetName(this.mWindowPtr);
    this.mCloseGuard.open("close");
  }
  
  public CursorWindow(String paramString)
  {
    this.mStartPos = 0;
    if ((paramString != null) && (paramString.length() != 0)) {}
    for (;;)
    {
      this.mName = paramString;
      if (sCursorWindowSize < 0) {
        sCursorWindowSize = Resources.getSystem().getInteger(17694849) * 1024;
      }
      this.mWindowPtr = nativeCreate(this.mName, sCursorWindowSize);
      if (this.mWindowPtr != 0L) {
        break;
      }
      throw new CursorWindowAllocationException("Cursor window allocation of " + sCursorWindowSize / 1024 + " kb failed. " + printStats());
      paramString = "<unnamed>";
    }
    this.mCloseGuard.open("close");
    recordNewWindow(Binder.getCallingPid(), this.mWindowPtr);
  }
  
  @Deprecated
  public CursorWindow(boolean paramBoolean)
  {
    this((String)null);
  }
  
  private void dispose()
  {
    if (this.mCloseGuard != null) {
      this.mCloseGuard.close();
    }
    if (this.mWindowPtr != 0L)
    {
      recordClosingOfWindow(this.mWindowPtr);
      nativeDispose(this.mWindowPtr);
      this.mWindowPtr = 0L;
    }
  }
  
  private static native boolean nativeAllocRow(long paramLong);
  
  private static native void nativeClear(long paramLong);
  
  private static native void nativeCopyStringToBuffer(long paramLong, int paramInt1, int paramInt2, CharArrayBuffer paramCharArrayBuffer);
  
  private static native long nativeCreate(String paramString, int paramInt);
  
  private static native long nativeCreateFromParcel(Parcel paramParcel);
  
  private static native void nativeDispose(long paramLong);
  
  private static native void nativeFreeLastRow(long paramLong);
  
  private static native byte[] nativeGetBlob(long paramLong, int paramInt1, int paramInt2);
  
  private static native double nativeGetDouble(long paramLong, int paramInt1, int paramInt2);
  
  private static native long nativeGetLong(long paramLong, int paramInt1, int paramInt2);
  
  private static native String nativeGetName(long paramLong);
  
  private static native int nativeGetNumRows(long paramLong);
  
  private static native String nativeGetString(long paramLong, int paramInt1, int paramInt2);
  
  private static native int nativeGetType(long paramLong, int paramInt1, int paramInt2);
  
  private static native boolean nativePutBlob(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native boolean nativePutDouble(long paramLong, double paramDouble, int paramInt1, int paramInt2);
  
  private static native boolean nativePutLong(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  private static native boolean nativePutNull(long paramLong, int paramInt1, int paramInt2);
  
  private static native boolean nativePutString(long paramLong, String paramString, int paramInt1, int paramInt2);
  
  private static native boolean nativeSetNumColumns(long paramLong, int paramInt);
  
  private static native void nativeWriteToParcel(long paramLong, Parcel paramParcel);
  
  public static CursorWindow newFromParcel(Parcel paramParcel)
  {
    return (CursorWindow)CREATOR.createFromParcel(paramParcel);
  }
  
  private String printStats()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int k = Process.myPid();
    int j = 0;
    SparseIntArray localSparseIntArray = new SparseIntArray();
    for (;;)
    {
      int n;
      synchronized (sWindowToPidMap)
      {
        int m = sWindowToPidMap.size();
        if (m == 0) {
          return "";
        }
        int i = 0;
        if (i < m)
        {
          n = ((Integer)sWindowToPidMap.valueAt(i)).intValue();
          localSparseIntArray.put(n, localSparseIntArray.get(n) + 1);
          i += 1;
          continue;
        }
        m = localSparseIntArray.size();
        i = 0;
        if (i >= m) {
          break;
        }
        localStringBuilder.append(" (# cursors opened by ");
        n = localSparseIntArray.keyAt(i);
        if (n == k)
        {
          localStringBuilder.append("this proc=");
          n = localSparseIntArray.get(n);
          localStringBuilder.append(n).append(")");
          j += n;
          i += 1;
        }
      }
      ((StringBuilder)localObject2).append("pid ").append(n).append("=");
    }
    if (((StringBuilder)localObject2).length() > 980) {}
    for (??? = ((StringBuilder)localObject2).substring(0, 980);; ??? = ((StringBuilder)localObject2).toString()) {
      return "# Open Cursors=" + j + (String)???;
    }
  }
  
  private void recordClosingOfWindow(long paramLong)
  {
    synchronized (sWindowToPidMap)
    {
      int i = sWindowToPidMap.size();
      if (i == 0) {
        return;
      }
      sWindowToPidMap.delete(paramLong);
      return;
    }
  }
  
  private void recordNewWindow(int paramInt, long paramLong)
  {
    synchronized (sWindowToPidMap)
    {
      sWindowToPidMap.put(paramLong, Integer.valueOf(paramInt));
      if (Log.isLoggable("CursorWindowStats", 2)) {
        Log.i("CursorWindowStats", "Created a new Cursor. " + printStats());
      }
      return;
    }
  }
  
  public boolean allocRow()
  {
    acquireReference();
    try
    {
      boolean bool = nativeAllocRow(this.mWindowPtr);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public void clear()
  {
    acquireReference();
    try
    {
      this.mStartPos = 0;
      nativeClear(this.mWindowPtr);
      return;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public void copyStringToBuffer(int paramInt1, int paramInt2, CharArrayBuffer paramCharArrayBuffer)
  {
    if (paramCharArrayBuffer == null) {
      throw new IllegalArgumentException("CharArrayBuffer should not be null");
    }
    acquireReference();
    try
    {
      nativeCopyStringToBuffer(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2, paramCharArrayBuffer);
      return;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mCloseGuard != null) {
        this.mCloseGuard.warnIfOpen();
      }
      dispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void freeLastRow()
  {
    acquireReference();
    try
    {
      nativeFreeLastRow(this.mWindowPtr);
      return;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public byte[] getBlob(int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      byte[] arrayOfByte = nativeGetBlob(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2);
      return arrayOfByte;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public double getDouble(int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      double d = nativeGetDouble(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2);
      return d;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public float getFloat(int paramInt1, int paramInt2)
  {
    return (float)getDouble(paramInt1, paramInt2);
  }
  
  public int getInt(int paramInt1, int paramInt2)
  {
    return (int)getLong(paramInt1, paramInt2);
  }
  
  public long getLong(int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      long l = nativeGetLong(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2);
      return l;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getNumRows()
  {
    acquireReference();
    try
    {
      int i = nativeGetNumRows(this.mWindowPtr);
      return i;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public short getShort(int paramInt1, int paramInt2)
  {
    return (short)(int)getLong(paramInt1, paramInt2);
  }
  
  public int getStartPosition()
  {
    return this.mStartPos;
  }
  
  public String getString(int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      String str = nativeGetString(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2);
      return str;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public int getType(int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      paramInt1 = nativeGetType(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2);
      return paramInt1;
    }
    finally
    {
      releaseReference();
    }
  }
  
  @Deprecated
  public boolean isBlob(int paramInt1, int paramInt2)
  {
    paramInt1 = getType(paramInt1, paramInt2);
    return (paramInt1 == 4) || (paramInt1 == 0);
  }
  
  @Deprecated
  public boolean isFloat(int paramInt1, int paramInt2)
  {
    return getType(paramInt1, paramInt2) == 2;
  }
  
  @Deprecated
  public boolean isLong(int paramInt1, int paramInt2)
  {
    return getType(paramInt1, paramInt2) == 1;
  }
  
  @Deprecated
  public boolean isNull(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if (getType(paramInt1, paramInt2) == 0) {
      bool = true;
    }
    return bool;
  }
  
  @Deprecated
  public boolean isString(int paramInt1, int paramInt2)
  {
    paramInt1 = getType(paramInt1, paramInt2);
    return (paramInt1 == 3) || (paramInt1 == 0);
  }
  
  protected void onAllReferencesReleased()
  {
    dispose();
  }
  
  public boolean putBlob(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      boolean bool = nativePutBlob(this.mWindowPtr, paramArrayOfByte, paramInt1 - this.mStartPos, paramInt2);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public boolean putDouble(double paramDouble, int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      boolean bool = nativePutDouble(this.mWindowPtr, paramDouble, paramInt1 - this.mStartPos, paramInt2);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public boolean putLong(long paramLong, int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      boolean bool = nativePutLong(this.mWindowPtr, paramLong, paramInt1 - this.mStartPos, paramInt2);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public boolean putNull(int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      boolean bool = nativePutNull(this.mWindowPtr, paramInt1 - this.mStartPos, paramInt2);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public boolean putString(String paramString, int paramInt1, int paramInt2)
  {
    acquireReference();
    try
    {
      boolean bool = nativePutString(this.mWindowPtr, paramString, paramInt1 - this.mStartPos, paramInt2);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public boolean setNumColumns(int paramInt)
  {
    acquireReference();
    try
    {
      boolean bool = nativeSetNumColumns(this.mWindowPtr, paramInt);
      return bool;
    }
    finally
    {
      releaseReference();
    }
  }
  
  public void setStartPosition(int paramInt)
  {
    this.mStartPos = paramInt;
  }
  
  public String toString()
  {
    return getName() + " {" + Long.toHexString(this.mWindowPtr) + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    acquireReference();
    try
    {
      paramParcel.writeInt(this.mStartPos);
      nativeWriteToParcel(this.mWindowPtr, paramParcel);
      releaseReference();
      if ((paramInt & 0x1) != 0) {}
      return;
    }
    finally
    {
      releaseReference();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/database/CursorWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */