package android.content;

import android.net.Uri;
import android.net.Uri.Builder;

public class ContentUris
{
  public static Uri.Builder appendId(Uri.Builder paramBuilder, long paramLong)
  {
    return paramBuilder.appendEncodedPath(String.valueOf(paramLong));
  }
  
  public static long parseId(Uri paramUri)
  {
    paramUri = paramUri.getLastPathSegment();
    if (paramUri == null) {
      return -1L;
    }
    return Long.parseLong(paramUri);
  }
  
  public static Uri withAppendedId(Uri paramUri, long paramLong)
  {
    return appendId(paramUri.buildUpon(), paramLong).build();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentUris.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */