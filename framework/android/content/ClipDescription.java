package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.text.TextUtils;
import java.util.ArrayList;

public class ClipDescription
  implements Parcelable
{
  public static final Parcelable.Creator<ClipDescription> CREATOR = new Parcelable.Creator()
  {
    public ClipDescription createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ClipDescription(paramAnonymousParcel);
    }
    
    public ClipDescription[] newArray(int paramAnonymousInt)
    {
      return new ClipDescription[paramAnonymousInt];
    }
  };
  public static final String EXTRA_TARGET_COMPONENT_NAME = "android.content.extra.TARGET_COMPONENT_NAME";
  public static final String EXTRA_USER_SERIAL_NUMBER = "android.content.extra.USER_SERIAL_NUMBER";
  public static final String MIMETYPE_TEXT_HTML = "text/html";
  public static final String MIMETYPE_TEXT_INTENT = "text/vnd.android.intent";
  public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
  public static final String MIMETYPE_TEXT_URILIST = "text/uri-list";
  private PersistableBundle mExtras;
  final CharSequence mLabel;
  final String[] mMimeTypes;
  
  public ClipDescription(ClipDescription paramClipDescription)
  {
    this.mLabel = paramClipDescription.mLabel;
    this.mMimeTypes = paramClipDescription.mMimeTypes;
  }
  
  ClipDescription(Parcel paramParcel)
  {
    this.mLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mMimeTypes = paramParcel.createStringArray();
    this.mExtras = paramParcel.readPersistableBundle();
  }
  
  public ClipDescription(CharSequence paramCharSequence, String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      throw new NullPointerException("mimeTypes is null");
    }
    this.mLabel = paramCharSequence;
    this.mMimeTypes = paramArrayOfString;
  }
  
  public static boolean compareMimeTypes(String paramString1, String paramString2)
  {
    int i = paramString2.length();
    if ((i == 3) && (paramString2.equals("*/*"))) {
      return true;
    }
    int j = paramString2.indexOf('/');
    if (j > 0) {
      if ((i == j + 2) && (paramString2.charAt(j + 1) == '*'))
      {
        if (paramString2.regionMatches(0, paramString1, 0, j + 1)) {
          return true;
        }
      }
      else if (paramString2.equals(paramString1)) {
        return true;
      }
    }
    return false;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String[] filterMimeTypes(String paramString)
  {
    Object localObject1 = null;
    int i = 0;
    while (i < this.mMimeTypes.length)
    {
      Object localObject2 = localObject1;
      if (compareMimeTypes(this.mMimeTypes[i], paramString))
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((ArrayList)localObject2).add(this.mMimeTypes[i]);
      }
      i += 1;
      localObject1 = localObject2;
    }
    if (localObject1 == null) {
      return null;
    }
    paramString = new String[((ArrayList)localObject1).size()];
    ((ArrayList)localObject1).toArray(paramString);
    return paramString;
  }
  
  public PersistableBundle getExtras()
  {
    return this.mExtras;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public String getMimeType(int paramInt)
  {
    return this.mMimeTypes[paramInt];
  }
  
  public int getMimeTypeCount()
  {
    return this.mMimeTypes.length;
  }
  
  public boolean hasMimeType(String paramString)
  {
    int i = 0;
    while (i < this.mMimeTypes.length)
    {
      if (compareMimeTypes(this.mMimeTypes[i], paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void setExtras(PersistableBundle paramPersistableBundle)
  {
    this.mExtras = new PersistableBundle(paramPersistableBundle);
  }
  
  public boolean toShortString(StringBuilder paramStringBuilder)
  {
    if (toShortStringTypesOnly(paramStringBuilder)) {}
    for (int j = 0;; j = 1)
    {
      int i = j;
      if (this.mLabel != null)
      {
        if (j == 0) {
          paramStringBuilder.append(' ');
        }
        i = 0;
        paramStringBuilder.append('"');
        paramStringBuilder.append(this.mLabel);
        paramStringBuilder.append('"');
      }
      j = i;
      if (this.mExtras != null)
      {
        if (i == 0) {
          paramStringBuilder.append(' ');
        }
        j = 0;
        paramStringBuilder.append(this.mExtras.toString());
      }
      if (j == 0) {
        break;
      }
      return false;
    }
    return true;
  }
  
  public boolean toShortStringTypesOnly(StringBuilder paramStringBuilder)
  {
    int j = 1;
    int i = 0;
    while (i < this.mMimeTypes.length)
    {
      if (j == 0) {
        paramStringBuilder.append(' ');
      }
      j = 0;
      paramStringBuilder.append(this.mMimeTypes[i]);
      i += 1;
    }
    return j == 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("ClipDescription { ");
    toShortString(localStringBuilder);
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  public void validate()
  {
    if (this.mMimeTypes == null) {
      throw new NullPointerException("null mime types");
    }
    if (this.mMimeTypes.length <= 0) {
      throw new IllegalArgumentException("must have at least 1 mime type");
    }
    int i = 0;
    while (i < this.mMimeTypes.length)
    {
      if (this.mMimeTypes[i] == null) {
        throw new NullPointerException("mime type at " + i + " is null");
      }
      i += 1;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    TextUtils.writeToParcel(this.mLabel, paramParcel, paramInt);
    paramParcel.writeStringArray(this.mMimeTypes);
    paramParcel.writePersistableBundle(this.mExtras);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ClipDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */