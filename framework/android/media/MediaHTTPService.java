package android.media;

import android.os.IBinder;

public class MediaHTTPService
  extends IMediaHTTPService.Stub
{
  private static final String TAG = "MediaHTTPService";
  
  static IBinder createHttpServiceBinderIfNecessary(String paramString)
  {
    if ((paramString.startsWith("http://")) || (paramString.startsWith("https://")) || (paramString.startsWith("widevine://"))) {
      return new MediaHTTPService().asBinder();
    }
    return null;
  }
  
  public IMediaHTTPConnection makeHTTPConnection()
  {
    return new MediaHTTPConnection();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaHTTPService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */