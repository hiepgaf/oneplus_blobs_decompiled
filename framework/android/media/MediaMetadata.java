package android.media;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.util.Iterator;
import java.util.Set;

public final class MediaMetadata
  implements Parcelable
{
  public static final Parcelable.Creator<MediaMetadata> CREATOR = new Parcelable.Creator()
  {
    public MediaMetadata createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaMetadata(paramAnonymousParcel, null);
    }
    
    public MediaMetadata[] newArray(int paramAnonymousInt)
    {
      return new MediaMetadata[paramAnonymousInt];
    }
  };
  private static final SparseArray<String> EDITOR_KEY_MAPPING;
  private static final ArrayMap<String, Integer> METADATA_KEYS_TYPE;
  public static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
  public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
  public static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
  public static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI";
  public static final String METADATA_KEY_ART = "android.media.metadata.ART";
  public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
  public static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI";
  public static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
  public static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
  public static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
  public static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
  public static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
  public static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION";
  public static final String METADATA_KEY_DISPLAY_ICON = "android.media.metadata.DISPLAY_ICON";
  public static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI";
  public static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE";
  public static final String METADATA_KEY_DISPLAY_TITLE = "android.media.metadata.DISPLAY_TITLE";
  public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
  public static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
  public static final String METADATA_KEY_MEDIA_ID = "android.media.metadata.MEDIA_ID";
  public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
  public static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
  public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
  public static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
  public static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
  public static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
  public static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
  private static final int METADATA_TYPE_BITMAP = 2;
  private static final int METADATA_TYPE_INVALID = -1;
  private static final int METADATA_TYPE_LONG = 0;
  private static final int METADATA_TYPE_RATING = 3;
  private static final int METADATA_TYPE_TEXT = 1;
  private static final String[] PREFERRED_BITMAP_ORDER;
  private static final String[] PREFERRED_DESCRIPTION_ORDER = { "android.media.metadata.TITLE", "android.media.metadata.ARTIST", "android.media.metadata.ALBUM", "android.media.metadata.ALBUM_ARTIST", "android.media.metadata.WRITER", "android.media.metadata.AUTHOR", "android.media.metadata.COMPOSER" };
  private static final String[] PREFERRED_URI_ORDER;
  private static final String TAG = "MediaMetadata";
  private final Bundle mBundle;
  private MediaDescription mDescription;
  
  static
  {
    PREFERRED_BITMAP_ORDER = new String[] { "android.media.metadata.DISPLAY_ICON", "android.media.metadata.ART", "android.media.metadata.ALBUM_ART" };
    PREFERRED_URI_ORDER = new String[] { "android.media.metadata.DISPLAY_ICON_URI", "android.media.metadata.ART_URI", "android.media.metadata.ALBUM_ART_URI" };
    METADATA_KEYS_TYPE = new ArrayMap();
    METADATA_KEYS_TYPE.put("android.media.metadata.TITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.ARTIST", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DURATION", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.AUTHOR", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.WRITER", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.COMPOSER", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.COMPILATION", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DATE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.YEAR", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.GENRE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.TRACK_NUMBER", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.NUM_TRACKS", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISC_NUMBER", Integer.valueOf(0));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ARTIST", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.ART", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.media.metadata.ART_URI", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART_URI", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.USER_RATING", Integer.valueOf(3));
    METADATA_KEYS_TYPE.put("android.media.metadata.RATING", Integer.valueOf(3));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_TITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_SUBTITLE", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_DESCRIPTION", Integer.valueOf(1));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON", Integer.valueOf(2));
    METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON_URI", Integer.valueOf(1));
    EDITOR_KEY_MAPPING = new SparseArray();
    EDITOR_KEY_MAPPING.put(100, "android.media.metadata.ART");
    EDITOR_KEY_MAPPING.put(101, "android.media.metadata.RATING");
    EDITOR_KEY_MAPPING.put(268435457, "android.media.metadata.USER_RATING");
    EDITOR_KEY_MAPPING.put(1, "android.media.metadata.ALBUM");
    EDITOR_KEY_MAPPING.put(13, "android.media.metadata.ALBUM_ARTIST");
    EDITOR_KEY_MAPPING.put(2, "android.media.metadata.ARTIST");
    EDITOR_KEY_MAPPING.put(3, "android.media.metadata.AUTHOR");
    EDITOR_KEY_MAPPING.put(0, "android.media.metadata.TRACK_NUMBER");
    EDITOR_KEY_MAPPING.put(4, "android.media.metadata.COMPOSER");
    EDITOR_KEY_MAPPING.put(15, "android.media.metadata.COMPILATION");
    EDITOR_KEY_MAPPING.put(5, "android.media.metadata.DATE");
    EDITOR_KEY_MAPPING.put(14, "android.media.metadata.DISC_NUMBER");
    EDITOR_KEY_MAPPING.put(9, "android.media.metadata.DURATION");
    EDITOR_KEY_MAPPING.put(6, "android.media.metadata.GENRE");
    EDITOR_KEY_MAPPING.put(10, "android.media.metadata.NUM_TRACKS");
    EDITOR_KEY_MAPPING.put(7, "android.media.metadata.TITLE");
    EDITOR_KEY_MAPPING.put(11, "android.media.metadata.WRITER");
    EDITOR_KEY_MAPPING.put(8, "android.media.metadata.YEAR");
  }
  
  private MediaMetadata(Bundle paramBundle)
  {
    this.mBundle = new Bundle(paramBundle);
  }
  
  private MediaMetadata(Parcel paramParcel)
  {
    this.mBundle = Bundle.setDefusable(paramParcel.readBundle(), true);
  }
  
  public static String getKeyFromMetadataEditorKey(int paramInt)
  {
    return (String)EDITOR_KEY_MAPPING.get(paramInt, null);
  }
  
  public boolean containsKey(String paramString)
  {
    return this.mBundle.containsKey(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Bitmap getBitmap(String paramString)
  {
    try
    {
      paramString = (Bitmap)this.mBundle.getParcelable(paramString);
      return paramString;
    }
    catch (Exception paramString)
    {
      Log.w("MediaMetadata", "Failed to retrieve a key as Bitmap.", paramString);
    }
    return null;
  }
  
  public MediaDescription getDescription()
  {
    if (this.mDescription != null) {
      return this.mDescription;
    }
    String str = getString("android.media.metadata.MEDIA_ID");
    CharSequence[] arrayOfCharSequence = new CharSequence[3];
    Object localObject2 = null;
    MediaDescription.Builder localBuilder = null;
    Object localObject1 = getText("android.media.metadata.DISPLAY_TITLE");
    int i;
    if (!TextUtils.isEmpty((CharSequence)localObject1))
    {
      arrayOfCharSequence[0] = localObject1;
      arrayOfCharSequence[1] = getText("android.media.metadata.DISPLAY_SUBTITLE");
      arrayOfCharSequence[2] = getText("android.media.metadata.DISPLAY_DESCRIPTION");
      i = 0;
      label76:
      localObject1 = localObject2;
      if (i < PREFERRED_BITMAP_ORDER.length)
      {
        localObject1 = getBitmap(PREFERRED_BITMAP_ORDER[i]);
        if (localObject1 == null) {
          break label280;
        }
      }
      i = 0;
    }
    for (;;)
    {
      localObject2 = localBuilder;
      if (i < PREFERRED_URI_ORDER.length)
      {
        localObject2 = getString(PREFERRED_URI_ORDER[i]);
        if (!TextUtils.isEmpty((CharSequence)localObject2)) {
          localObject2 = Uri.parse((String)localObject2);
        }
      }
      else
      {
        localBuilder = new MediaDescription.Builder();
        localBuilder.setMediaId(str);
        localBuilder.setTitle(arrayOfCharSequence[0]);
        localBuilder.setSubtitle(arrayOfCharSequence[1]);
        localBuilder.setDescription(arrayOfCharSequence[2]);
        localBuilder.setIconBitmap((Bitmap)localObject1);
        localBuilder.setIconUri((Uri)localObject2);
        this.mDescription = localBuilder.build();
        return this.mDescription;
        int j = 0;
        i = 0;
        while ((j < arrayOfCharSequence.length) && (i < PREFERRED_DESCRIPTION_ORDER.length))
        {
          localObject1 = getText(PREFERRED_DESCRIPTION_ORDER[i]);
          int k = j;
          if (!TextUtils.isEmpty((CharSequence)localObject1))
          {
            arrayOfCharSequence[j] = localObject1;
            k = j + 1;
          }
          i += 1;
          j = k;
        }
        break;
        label280:
        i += 1;
        break label76;
      }
      i += 1;
    }
  }
  
  public long getLong(String paramString)
  {
    return this.mBundle.getLong(paramString, 0L);
  }
  
  public Rating getRating(String paramString)
  {
    try
    {
      paramString = (Rating)this.mBundle.getParcelable(paramString);
      return paramString;
    }
    catch (Exception paramString)
    {
      Log.w("MediaMetadata", "Failed to retrieve a key as Rating.", paramString);
    }
    return null;
  }
  
  public String getString(String paramString)
  {
    paramString = getText(paramString);
    if (paramString != null) {
      return paramString.toString();
    }
    return null;
  }
  
  public CharSequence getText(String paramString)
  {
    return this.mBundle.getCharSequence(paramString);
  }
  
  public Set<String> keySet()
  {
    return this.mBundle.keySet();
  }
  
  public int size()
  {
    return this.mBundle.size();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeBundle(this.mBundle);
  }
  
  public static final class Builder
  {
    private final Bundle mBundle;
    
    public Builder()
    {
      this.mBundle = new Bundle();
    }
    
    public Builder(MediaMetadata paramMediaMetadata)
    {
      this.mBundle = new Bundle(MediaMetadata.-get1(paramMediaMetadata));
    }
    
    public Builder(MediaMetadata paramMediaMetadata, int paramInt)
    {
      this(paramMediaMetadata);
      paramMediaMetadata = this.mBundle.keySet().iterator();
      while (paramMediaMetadata.hasNext())
      {
        String str = (String)paramMediaMetadata.next();
        Object localObject = this.mBundle.get(str);
        if ((localObject != null) && ((localObject instanceof Bitmap)))
        {
          localObject = (Bitmap)localObject;
          if ((((Bitmap)localObject).getHeight() > paramInt) || (((Bitmap)localObject).getWidth() > paramInt)) {
            putBitmap(str, scaleBitmap((Bitmap)localObject, paramInt));
          }
        }
      }
    }
    
    private Bitmap scaleBitmap(Bitmap paramBitmap, int paramInt)
    {
      float f = paramInt;
      f = Math.min(f / paramBitmap.getWidth(), f / paramBitmap.getHeight());
      paramInt = (int)(paramBitmap.getHeight() * f);
      return Bitmap.createScaledBitmap(paramBitmap, (int)(paramBitmap.getWidth() * f), paramInt, true);
    }
    
    public MediaMetadata build()
    {
      return new MediaMetadata(this.mBundle, null);
    }
    
    public Builder putBitmap(String paramString, Bitmap paramBitmap)
    {
      if ((MediaMetadata.-get0().containsKey(paramString)) && (((Integer)MediaMetadata.-get0().get(paramString)).intValue() != 2)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a Bitmap");
      }
      this.mBundle.putParcelable(paramString, paramBitmap);
      return this;
    }
    
    public Builder putLong(String paramString, long paramLong)
    {
      if ((MediaMetadata.-get0().containsKey(paramString)) && (((Integer)MediaMetadata.-get0().get(paramString)).intValue() != 0)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a long");
      }
      this.mBundle.putLong(paramString, paramLong);
      return this;
    }
    
    public Builder putRating(String paramString, Rating paramRating)
    {
      if ((MediaMetadata.-get0().containsKey(paramString)) && (((Integer)MediaMetadata.-get0().get(paramString)).intValue() != 3)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a Rating");
      }
      this.mBundle.putParcelable(paramString, paramRating);
      return this;
    }
    
    public Builder putString(String paramString1, String paramString2)
    {
      if ((MediaMetadata.-get0().containsKey(paramString1)) && (((Integer)MediaMetadata.-get0().get(paramString1)).intValue() != 1)) {
        throw new IllegalArgumentException("The " + paramString1 + " key cannot be used to put a String");
      }
      this.mBundle.putCharSequence(paramString1, paramString2);
      return this;
    }
    
    public Builder putText(String paramString, CharSequence paramCharSequence)
    {
      if ((MediaMetadata.-get0().containsKey(paramString)) && (((Integer)MediaMetadata.-get0().get(paramString)).intValue() != 1)) {
        throw new IllegalArgumentException("The " + paramString + " key cannot be used to put a CharSequence");
      }
      this.mBundle.putCharSequence(paramString, paramCharSequence);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */