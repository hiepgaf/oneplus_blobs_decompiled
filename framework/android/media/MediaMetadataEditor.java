package android.media;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseIntArray;

@Deprecated
public abstract class MediaMetadataEditor
{
  public static final int BITMAP_KEY_ARTWORK = 100;
  public static final int KEY_EDITABLE_MASK = 536870911;
  protected static final SparseIntArray METADATA_KEYS_TYPE = new SparseIntArray(18);
  protected static final int METADATA_TYPE_BITMAP = 2;
  protected static final int METADATA_TYPE_INVALID = -1;
  protected static final int METADATA_TYPE_LONG = 0;
  protected static final int METADATA_TYPE_RATING = 3;
  protected static final int METADATA_TYPE_STRING = 1;
  public static final int RATING_KEY_BY_OTHERS = 101;
  public static final int RATING_KEY_BY_USER = 268435457;
  private static final String TAG = "MediaMetadataEditor";
  protected boolean mApplied = false;
  protected boolean mArtworkChanged = false;
  protected long mEditableKeys;
  protected Bitmap mEditorArtwork;
  protected Bundle mEditorMetadata;
  protected MediaMetadata.Builder mMetadataBuilder;
  protected boolean mMetadataChanged = false;
  
  static
  {
    METADATA_KEYS_TYPE.put(0, 0);
    METADATA_KEYS_TYPE.put(14, 0);
    METADATA_KEYS_TYPE.put(9, 0);
    METADATA_KEYS_TYPE.put(8, 0);
    METADATA_KEYS_TYPE.put(1, 1);
    METADATA_KEYS_TYPE.put(13, 1);
    METADATA_KEYS_TYPE.put(7, 1);
    METADATA_KEYS_TYPE.put(2, 1);
    METADATA_KEYS_TYPE.put(3, 1);
    METADATA_KEYS_TYPE.put(15, 1);
    METADATA_KEYS_TYPE.put(4, 1);
    METADATA_KEYS_TYPE.put(5, 1);
    METADATA_KEYS_TYPE.put(6, 1);
    METADATA_KEYS_TYPE.put(11, 1);
    METADATA_KEYS_TYPE.put(100, 2);
    METADATA_KEYS_TYPE.put(101, 3);
    METADATA_KEYS_TYPE.put(268435457, 3);
    METADATA_KEYS_TYPE.put(10, 0);
  }
  
  /* Error */
  public void addEditableKey(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 63	android/media/MediaMetadataEditor:mApplied	Z
    //   6: ifeq +14 -> 20
    //   9: ldc 30
    //   11: ldc 68
    //   13: invokestatic 74	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   16: pop
    //   17: aload_0
    //   18: monitorexit
    //   19: return
    //   20: iload_1
    //   21: ldc 26
    //   23: if_icmpne +25 -> 48
    //   26: aload_0
    //   27: aload_0
    //   28: getfield 76	android/media/MediaMetadataEditor:mEditableKeys	J
    //   31: ldc 10
    //   33: iload_1
    //   34: iand
    //   35: i2l
    //   36: lor
    //   37: putfield 76	android/media/MediaMetadataEditor:mEditableKeys	J
    //   40: aload_0
    //   41: iconst_1
    //   42: putfield 61	android/media/MediaMetadataEditor:mMetadataChanged	Z
    //   45: aload_0
    //   46: monitorexit
    //   47: return
    //   48: ldc 30
    //   50: new 78	java/lang/StringBuilder
    //   53: dup
    //   54: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   57: ldc 81
    //   59: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: iload_1
    //   63: invokevirtual 88	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   66: ldc 90
    //   68: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   74: invokestatic 74	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   77: pop
    //   78: goto -33 -> 45
    //   81: astore_2
    //   82: aload_0
    //   83: monitorexit
    //   84: aload_2
    //   85: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	86	0	this	MediaMetadataEditor
    //   0	86	1	paramInt	int
    //   81	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	17	81	finally
    //   26	45	81	finally
    //   48	78	81	finally
  }
  
  public abstract void apply();
  
  public void clear()
  {
    try
    {
      if (this.mApplied)
      {
        Log.e("MediaMetadataEditor", "Can't clear a previously applied MediaMetadataEditor");
        return;
      }
      this.mEditorMetadata.clear();
      this.mEditorArtwork = null;
      this.mMetadataBuilder = new MediaMetadata.Builder();
      return;
    }
    finally {}
  }
  
  public Bitmap getBitmap(int paramInt, Bitmap paramBitmap)
    throws IllegalArgumentException
  {
    if (paramInt != 100) {
      try
      {
        throw new IllegalArgumentException("Invalid type 'Bitmap' for key " + paramInt);
      }
      finally {}
    }
    if (this.mEditorArtwork != null) {
      paramBitmap = this.mEditorArtwork;
    }
    return paramBitmap;
  }
  
  public int[] getEditableKeys()
  {
    try
    {
      if (this.mEditableKeys == 268435457L) {
        return new int[] { 268435457 };
      }
      return null;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public long getLong(int paramInt, long paramLong)
    throws IllegalArgumentException
  {
    try
    {
      if (METADATA_KEYS_TYPE.get(paramInt, -1) != 0) {
        throw new IllegalArgumentException("Invalid type 'long' for key " + paramInt);
      }
    }
    finally {}
    paramLong = this.mEditorMetadata.getLong(String.valueOf(paramInt), paramLong);
    return paramLong;
  }
  
  public Object getObject(int paramInt, Object paramObject)
    throws IllegalArgumentException
  {
    try
    {
      switch (METADATA_KEYS_TYPE.get(paramInt, -1))
      {
      case 0: 
        throw new IllegalArgumentException("Invalid key " + paramInt);
      }
    }
    finally
    {
      do
      {
        throw ((Throwable)paramObject);
        if (this.mEditorMetadata.containsKey(String.valueOf(paramInt)))
        {
          long l = this.mEditorMetadata.getLong(String.valueOf(paramInt));
          return Long.valueOf(l);
        }
        return paramObject;
        if (this.mEditorMetadata.containsKey(String.valueOf(paramInt)))
        {
          paramObject = this.mEditorMetadata.getString(String.valueOf(paramInt));
          return paramObject;
        }
        return paramObject;
        if (this.mEditorMetadata.containsKey(String.valueOf(paramInt)))
        {
          paramObject = this.mEditorMetadata.getParcelable(String.valueOf(paramInt));
          return paramObject;
        }
        return paramObject;
      } while (paramInt != 100);
      if (this.mEditorArtwork != null) {
        paramObject = this.mEditorArtwork;
      }
    }
  }
  
  public String getString(int paramInt, String paramString)
    throws IllegalArgumentException
  {
    try
    {
      if (METADATA_KEYS_TYPE.get(paramInt, -1) != 1) {
        throw new IllegalArgumentException("Invalid type 'String' for key " + paramInt);
      }
    }
    finally {}
    paramString = this.mEditorMetadata.getString(String.valueOf(paramInt), paramString);
    return paramString;
  }
  
  public MediaMetadataEditor putBitmap(int paramInt, Bitmap paramBitmap)
    throws IllegalArgumentException
  {
    try
    {
      if (this.mApplied)
      {
        Log.e("MediaMetadataEditor", "Can't edit a previously applied MediaMetadataEditor");
        return this;
      }
      if (paramInt != 100) {
        throw new IllegalArgumentException("Invalid type 'Bitmap' for key " + paramInt);
      }
    }
    finally {}
    this.mEditorArtwork = paramBitmap;
    this.mArtworkChanged = true;
    return this;
  }
  
  public MediaMetadataEditor putLong(int paramInt, long paramLong)
    throws IllegalArgumentException
  {
    try
    {
      if (this.mApplied)
      {
        Log.e("MediaMetadataEditor", "Can't edit a previously applied MediaMetadataEditor");
        return this;
      }
      if (METADATA_KEYS_TYPE.get(paramInt, -1) != 0) {
        throw new IllegalArgumentException("Invalid type 'long' for key " + paramInt);
      }
    }
    finally {}
    this.mEditorMetadata.putLong(String.valueOf(paramInt), paramLong);
    this.mMetadataChanged = true;
    return this;
  }
  
  public MediaMetadataEditor putObject(int paramInt, Object paramObject)
    throws IllegalArgumentException
  {
    for (;;)
    {
      try
      {
        if (this.mApplied)
        {
          Log.e("MediaMetadataEditor", "Can't edit a previously applied MediaMetadataEditor");
          return this;
        }
        switch (METADATA_KEYS_TYPE.get(paramInt, -1))
        {
        case 0: 
          throw new IllegalArgumentException("Invalid key " + paramInt);
        }
      }
      finally {}
      if ((paramObject instanceof Long))
      {
        paramObject = putLong(paramInt, ((Long)paramObject).longValue());
        return (MediaMetadataEditor)paramObject;
      }
      throw new IllegalArgumentException("Not a non-null Long for key " + paramInt);
      if ((paramObject == null) || ((paramObject instanceof String)))
      {
        paramObject = putString(paramInt, (String)paramObject);
        return (MediaMetadataEditor)paramObject;
      }
      throw new IllegalArgumentException("Not a String for key " + paramInt);
      this.mEditorMetadata.putParcelable(String.valueOf(paramInt), (Parcelable)paramObject);
      this.mMetadataChanged = true;
      return this;
      if ((paramObject == null) || ((paramObject instanceof Bitmap)))
      {
        paramObject = putBitmap(paramInt, (Bitmap)paramObject);
        return (MediaMetadataEditor)paramObject;
      }
      throw new IllegalArgumentException("Not a Bitmap for key " + paramInt);
    }
  }
  
  public MediaMetadataEditor putString(int paramInt, String paramString)
    throws IllegalArgumentException
  {
    try
    {
      if (this.mApplied)
      {
        Log.e("MediaMetadataEditor", "Can't edit a previously applied MediaMetadataEditor");
        return this;
      }
      if (METADATA_KEYS_TYPE.get(paramInt, -1) != 1) {
        throw new IllegalArgumentException("Invalid type 'String' for key " + paramInt);
      }
    }
    finally {}
    this.mEditorMetadata.putString(String.valueOf(paramInt), paramString);
    this.mMetadataChanged = true;
    return this;
  }
  
  public void removeEditableKeys()
  {
    try
    {
      if (this.mApplied)
      {
        Log.e("MediaMetadataEditor", "Can't remove all editable keys of a previously applied MetadataEditor");
        return;
      }
      if (this.mEditableKeys != 0L)
      {
        this.mEditableKeys = 0L;
        this.mMetadataChanged = true;
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaMetadataEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */