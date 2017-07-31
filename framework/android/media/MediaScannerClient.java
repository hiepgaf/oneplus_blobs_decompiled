package android.media;

public abstract interface MediaScannerClient
{
  public abstract void handleStringTag(String paramString1, String paramString2);
  
  public abstract void scanFile(String paramString, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void setMimeType(String paramString);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaScannerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */