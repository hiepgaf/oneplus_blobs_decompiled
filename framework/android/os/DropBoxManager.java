package android.os;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.android.internal.os.IDropBoxManagerService;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class DropBoxManager
{
  public static final String ACTION_DROPBOX_ENTRY_ADDED = "android.intent.action.DROPBOX_ENTRY_ADDED";
  public static final String EXTRA_TAG = "tag";
  public static final String EXTRA_TIME = "time";
  private static final int HAS_BYTE_ARRAY = 8;
  public static final int IS_EMPTY = 1;
  public static final int IS_GZIPPED = 4;
  public static final int IS_TEXT = 2;
  private static final String TAG = "DropBoxManager";
  private final Context mContext;
  private final IDropBoxManagerService mService;
  
  protected DropBoxManager()
  {
    this.mContext = null;
    this.mService = null;
  }
  
  public DropBoxManager(Context paramContext, IDropBoxManagerService paramIDropBoxManagerService)
  {
    this.mContext = paramContext;
    this.mService = paramIDropBoxManagerService;
  }
  
  public void addData(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("data == null");
    }
    try
    {
      this.mService.add(new Entry(paramString, 0L, paramArrayOfByte, paramInt));
      return;
    }
    catch (RemoteException paramString)
    {
      if (((paramString instanceof TransactionTooLargeException)) && (this.mContext.getApplicationInfo().targetSdkVersion < 24))
      {
        Log.e("DropBoxManager", "App sent too much data, so it was ignored", paramString);
        return;
      }
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void addFile(String paramString, File paramFile, int paramInt)
    throws IOException
  {
    if (paramFile == null) {
      throw new NullPointerException("file == null");
    }
    paramString = new Entry(paramString, 0L, paramFile, paramInt);
    try
    {
      this.mService.add(paramString);
      return;
    }
    catch (RemoteException paramFile)
    {
      throw paramFile.rethrowFromSystemServer();
    }
    finally
    {
      paramString.close();
    }
  }
  
  public void addText(String paramString1, String paramString2)
  {
    try
    {
      this.mService.add(new Entry(paramString1, 0L, paramString2));
      return;
    }
    catch (RemoteException paramString1)
    {
      if (((paramString1 instanceof TransactionTooLargeException)) && (this.mContext.getApplicationInfo().targetSdkVersion < 24))
      {
        Log.e("DropBoxManager", "App sent too much data, so it was ignored", paramString1);
        return;
      }
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public Entry getNextEntry(String paramString, long paramLong)
  {
    try
    {
      paramString = this.mService.getNextEntry(paramString, paramLong);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isTagEnabled(String paramString)
  {
    try
    {
      boolean bool = this.mService.isTagEnabled(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public static class Entry
    implements Parcelable, Closeable
  {
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator()
    {
      public DropBoxManager.Entry createFromParcel(Parcel paramAnonymousParcel)
      {
        String str = paramAnonymousParcel.readString();
        long l = paramAnonymousParcel.readLong();
        int i = paramAnonymousParcel.readInt();
        if ((i & 0x8) != 0) {
          return new DropBoxManager.Entry(str, l, paramAnonymousParcel.createByteArray(), i & 0xFFFFFFF7);
        }
        return new DropBoxManager.Entry(str, l, (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramAnonymousParcel), i);
      }
      
      public DropBoxManager.Entry[] newArray(int paramAnonymousInt)
      {
        return new DropBoxManager.Entry[paramAnonymousInt];
      }
    };
    private final byte[] mData;
    private final ParcelFileDescriptor mFileDescriptor;
    private final int mFlags;
    private final String mTag;
    private final long mTimeMillis;
    
    public Entry(String paramString, long paramLong)
    {
      if (paramString == null) {
        throw new NullPointerException("tag == null");
      }
      this.mTag = paramString;
      this.mTimeMillis = paramLong;
      this.mData = null;
      this.mFileDescriptor = null;
      this.mFlags = 1;
    }
    
    public Entry(String paramString, long paramLong, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
    {
      if (paramString == null) {
        throw new NullPointerException("tag == null");
      }
      int i;
      if ((paramInt & 0x1) != 0)
      {
        i = 1;
        if (paramParcelFileDescriptor != null) {
          break label77;
        }
      }
      for (;;)
      {
        if (i == j) {
          break label83;
        }
        throw new IllegalArgumentException("Bad flags: " + paramInt);
        i = 0;
        break;
        label77:
        j = 0;
      }
      label83:
      this.mTag = paramString;
      this.mTimeMillis = paramLong;
      this.mData = null;
      this.mFileDescriptor = paramParcelFileDescriptor;
      this.mFlags = paramInt;
    }
    
    public Entry(String paramString, long paramLong, File paramFile, int paramInt)
      throws IOException
    {
      if (paramString == null) {
        throw new NullPointerException("tag == null");
      }
      if ((paramInt & 0x1) != 0) {
        throw new IllegalArgumentException("Bad flags: " + paramInt);
      }
      this.mTag = paramString;
      this.mTimeMillis = paramLong;
      this.mData = null;
      this.mFileDescriptor = ParcelFileDescriptor.open(paramFile, 268435456);
      this.mFlags = paramInt;
    }
    
    public Entry(String paramString1, long paramLong, String paramString2)
    {
      if (paramString1 == null) {
        throw new NullPointerException("tag == null");
      }
      if (paramString2 == null) {
        throw new NullPointerException("text == null");
      }
      this.mTag = paramString1;
      this.mTimeMillis = paramLong;
      this.mData = paramString2.getBytes();
      this.mFileDescriptor = null;
      this.mFlags = 2;
    }
    
    public Entry(String paramString, long paramLong, byte[] paramArrayOfByte, int paramInt)
    {
      if (paramString == null) {
        throw new NullPointerException("tag == null");
      }
      int i;
      if ((paramInt & 0x1) != 0)
      {
        i = 1;
        if (paramArrayOfByte != null) {
          break label77;
        }
      }
      for (;;)
      {
        if (i == j) {
          break label83;
        }
        throw new IllegalArgumentException("Bad flags: " + paramInt);
        i = 0;
        break;
        label77:
        j = 0;
      }
      label83:
      this.mTag = paramString;
      this.mTimeMillis = paramLong;
      this.mData = paramArrayOfByte;
      this.mFileDescriptor = null;
      this.mFlags = paramInt;
    }
    
    public void close()
    {
      try
      {
        if (this.mFileDescriptor != null) {
          this.mFileDescriptor.close();
        }
        return;
      }
      catch (IOException localIOException) {}
    }
    
    public int describeContents()
    {
      if (this.mFileDescriptor != null) {
        return 1;
      }
      return 0;
    }
    
    public int getFlags()
    {
      return this.mFlags & 0xFFFFFFFB;
    }
    
    public InputStream getInputStream()
      throws IOException
    {
      if (this.mData != null) {}
      for (Object localObject1 = new ByteArrayInputStream(this.mData);; localObject1 = new ParcelFileDescriptor.AutoCloseInputStream(this.mFileDescriptor))
      {
        Object localObject2 = localObject1;
        if ((this.mFlags & 0x4) != 0) {
          localObject2 = new GZIPInputStream((InputStream)localObject1);
        }
        return (InputStream)localObject2;
        if (this.mFileDescriptor == null) {
          break;
        }
      }
      return null;
    }
    
    public String getTag()
    {
      return this.mTag;
    }
    
    public String getText(int paramInt)
    {
      if ((this.mFlags & 0x2) == 0) {
        return null;
      }
      if (this.mData != null) {
        return new String(this.mData, 0, Math.min(paramInt, this.mData.length));
      }
      localObject3 = null;
      Object localObject2 = null;
      try
      {
        InputStream localInputStream = getInputStream();
        if (localInputStream == null)
        {
          if (localInputStream != null) {}
          try
          {
            localInputStream.close();
            return null;
          }
          catch (IOException localIOException1)
          {
            return null;
          }
        }
        localObject2 = localIOException1;
        localObject3 = localIOException1;
        Object localObject4 = new byte[paramInt];
        int i = 0;
        int k;
        for (int j = 0;; j = localIOException1.read((byte[])localObject4, i, paramInt - i))
        {
          k = i;
          if (j < 0) {
            break;
          }
          i += j;
          k = i;
          if (i >= paramInt) {
            break;
          }
          localObject2 = localIOException1;
          localObject3 = localIOException1;
        }
        localObject2 = localIOException1;
        localObject3 = localIOException1;
        localObject4 = new String((byte[])localObject4, 0, k);
        if (localIOException1 != null) {}
        try
        {
          localIOException1.close();
          return (String)localObject4;
        }
        catch (IOException localIOException2)
        {
          return (String)localObject4;
        }
        try
        {
          ((InputStream)localObject3).close();
          throw ((Throwable)localObject1);
        }
        catch (IOException localIOException5)
        {
          for (;;) {}
        }
      }
      catch (IOException localIOException3)
      {
        if (localObject2 != null) {}
        try
        {
          ((InputStream)localObject2).close();
          return null;
        }
        catch (IOException localIOException4)
        {
          return null;
        }
      }
      finally
      {
        if (localObject3 == null) {}
      }
    }
    
    public long getTimeMillis()
    {
      return this.mTimeMillis;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mTag);
      paramParcel.writeLong(this.mTimeMillis);
      if (this.mFileDescriptor != null)
      {
        paramParcel.writeInt(this.mFlags & 0xFFFFFFF7);
        this.mFileDescriptor.writeToParcel(paramParcel, paramInt);
        return;
      }
      paramParcel.writeInt(this.mFlags | 0x8);
      paramParcel.writeByteArray(this.mData);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/DropBoxManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */