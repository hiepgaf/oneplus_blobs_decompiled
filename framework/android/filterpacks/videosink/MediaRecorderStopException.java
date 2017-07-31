package android.filterpacks.videosink;

public class MediaRecorderStopException
  extends RuntimeException
{
  private static final String TAG = "MediaRecorderStopException";
  
  public MediaRecorderStopException() {}
  
  public MediaRecorderStopException(String paramString)
  {
    super(paramString);
  }
  
  public MediaRecorderStopException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public MediaRecorderStopException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videosink/MediaRecorderStopException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */