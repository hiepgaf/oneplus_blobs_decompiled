package android.content.res;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AssetFileDescriptor
  implements Parcelable, Closeable
{
  public static final Parcelable.Creator<AssetFileDescriptor> CREATOR = new Parcelable.Creator()
  {
    public AssetFileDescriptor createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AssetFileDescriptor(paramAnonymousParcel);
    }
    
    public AssetFileDescriptor[] newArray(int paramAnonymousInt)
    {
      return new AssetFileDescriptor[paramAnonymousInt];
    }
  };
  public static final long UNKNOWN_LENGTH = -1L;
  private final Bundle mExtras;
  private final ParcelFileDescriptor mFd;
  private final long mLength;
  private final long mStartOffset;
  
  AssetFileDescriptor(Parcel paramParcel)
  {
    this.mFd = ((ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel));
    this.mStartOffset = paramParcel.readLong();
    this.mLength = paramParcel.readLong();
    if (paramParcel.readInt() != 0)
    {
      this.mExtras = paramParcel.readBundle();
      return;
    }
    this.mExtras = null;
  }
  
  public AssetFileDescriptor(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, long paramLong2)
  {
    this(paramParcelFileDescriptor, paramLong1, paramLong2, null);
  }
  
  public AssetFileDescriptor(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, long paramLong2, Bundle paramBundle)
  {
    if (paramParcelFileDescriptor == null) {
      throw new IllegalArgumentException("fd must not be null");
    }
    if ((paramLong2 < 0L) && (paramLong1 != 0L)) {
      throw new IllegalArgumentException("startOffset must be 0 when using UNKNOWN_LENGTH");
    }
    this.mFd = paramParcelFileDescriptor;
    this.mStartOffset = paramLong1;
    this.mLength = paramLong2;
    this.mExtras = paramBundle;
  }
  
  public void close()
    throws IOException
  {
    this.mFd.close();
  }
  
  public FileInputStream createInputStream()
    throws IOException
  {
    if (this.mLength < 0L) {
      return new ParcelFileDescriptor.AutoCloseInputStream(this.mFd);
    }
    return new AutoCloseInputStream(this);
  }
  
  public FileOutputStream createOutputStream()
    throws IOException
  {
    if (this.mLength < 0L) {
      return new ParcelFileDescriptor.AutoCloseOutputStream(this.mFd);
    }
    return new AutoCloseOutputStream(this);
  }
  
  public int describeContents()
  {
    return this.mFd.describeContents();
  }
  
  public long getDeclaredLength()
  {
    return this.mLength;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public FileDescriptor getFileDescriptor()
  {
    return this.mFd.getFileDescriptor();
  }
  
  public long getLength()
  {
    if (this.mLength >= 0L) {
      return this.mLength;
    }
    long l = this.mFd.getStatSize();
    if (l >= 0L) {
      return l;
    }
    return -1L;
  }
  
  public ParcelFileDescriptor getParcelFileDescriptor()
  {
    return this.mFd;
  }
  
  public long getStartOffset()
  {
    return this.mStartOffset;
  }
  
  public String toString()
  {
    return "{AssetFileDescriptor: " + this.mFd + " start=" + this.mStartOffset + " len=" + this.mLength + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mFd.writeToParcel(paramParcel, paramInt);
    paramParcel.writeLong(this.mStartOffset);
    paramParcel.writeLong(this.mLength);
    if (this.mExtras != null)
    {
      paramParcel.writeInt(1);
      paramParcel.writeBundle(this.mExtras);
      return;
    }
    paramParcel.writeInt(0);
  }
  
  public static class AutoCloseInputStream
    extends ParcelFileDescriptor.AutoCloseInputStream
  {
    private long mRemaining;
    
    public AutoCloseInputStream(AssetFileDescriptor paramAssetFileDescriptor)
      throws IOException
    {
      super();
      super.skip(paramAssetFileDescriptor.getStartOffset());
      this.mRemaining = ((int)paramAssetFileDescriptor.getLength());
    }
    
    public int available()
      throws IOException
    {
      if (this.mRemaining >= 0L)
      {
        if (this.mRemaining < 2147483647L) {
          return (int)this.mRemaining;
        }
        return Integer.MAX_VALUE;
      }
      return super.available();
    }
    
    public void mark(int paramInt)
    {
      if (this.mRemaining >= 0L) {
        return;
      }
      super.mark(paramInt);
    }
    
    public boolean markSupported()
    {
      if (this.mRemaining >= 0L) {
        return false;
      }
      return super.markSupported();
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      if (read(arrayOfByte, 0, 1) == -1) {
        return -1;
      }
      return arrayOfByte[0] & 0xFF;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (this.mRemaining >= 0L)
      {
        if (this.mRemaining == 0L) {
          return -1;
        }
        int i = paramInt2;
        if (paramInt2 > this.mRemaining) {
          i = (int)this.mRemaining;
        }
        paramInt1 = super.read(paramArrayOfByte, paramInt1, i);
        if (paramInt1 >= 0) {
          this.mRemaining -= paramInt1;
        }
        return paramInt1;
      }
      return super.read(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public void reset()
      throws IOException
    {
      try
      {
        long l = this.mRemaining;
        if (l >= 0L) {
          return;
        }
        super.reset();
        return;
      }
      finally {}
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (this.mRemaining >= 0L)
      {
        if (this.mRemaining == 0L) {
          return -1L;
        }
        long l = paramLong;
        if (paramLong > this.mRemaining) {
          l = this.mRemaining;
        }
        paramLong = super.skip(l);
        if (paramLong >= 0L) {
          this.mRemaining -= paramLong;
        }
        return paramLong;
      }
      return super.skip(paramLong);
    }
  }
  
  public static class AutoCloseOutputStream
    extends ParcelFileDescriptor.AutoCloseOutputStream
  {
    private long mRemaining;
    
    public AutoCloseOutputStream(AssetFileDescriptor paramAssetFileDescriptor)
      throws IOException
    {
      super();
      if (paramAssetFileDescriptor.getParcelFileDescriptor().seekTo(paramAssetFileDescriptor.getStartOffset()) < 0L) {
        throw new IOException("Unable to seek");
      }
      this.mRemaining = ((int)paramAssetFileDescriptor.getLength());
    }
    
    public void write(int paramInt)
      throws IOException
    {
      if (this.mRemaining >= 0L)
      {
        if (this.mRemaining == 0L) {
          return;
        }
        super.write(paramInt);
        this.mRemaining -= 1L;
        return;
      }
      super.write(paramInt);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      if (this.mRemaining >= 0L)
      {
        if (this.mRemaining == 0L) {
          return;
        }
        int j = paramArrayOfByte.length;
        int i = j;
        if (j > this.mRemaining) {
          i = (int)this.mRemaining;
        }
        super.write(paramArrayOfByte);
        this.mRemaining -= i;
        return;
      }
      super.write(paramArrayOfByte);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (this.mRemaining >= 0L)
      {
        if (this.mRemaining == 0L) {
          return;
        }
        int i = paramInt2;
        if (paramInt2 > this.mRemaining) {
          i = (int)this.mRemaining;
        }
        super.write(paramArrayOfByte, paramInt1, i);
        this.mRemaining -= i;
        return;
      }
      super.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/AssetFileDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */