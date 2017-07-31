package android.drm;

public class DrmInfoStatus
{
  public static final int STATUS_ERROR = 2;
  public static final int STATUS_OK = 1;
  public final ProcessedData data;
  public final int infoType;
  public final String mimeType;
  public final int statusCode;
  
  public DrmInfoStatus(int paramInt1, int paramInt2, ProcessedData paramProcessedData, String paramString)
  {
    if (!DrmInfoRequest.isValidType(paramInt2)) {
      throw new IllegalArgumentException("infoType: " + paramInt2);
    }
    if (!isValidStatusCode(paramInt1)) {
      throw new IllegalArgumentException("Unsupported status code: " + paramInt1);
    }
    if ((paramString == null) || (paramString == "")) {
      throw new IllegalArgumentException("mimeType is null or an empty string");
    }
    this.statusCode = paramInt1;
    this.infoType = paramInt2;
    this.data = paramProcessedData;
    this.mimeType = paramString;
  }
  
  private boolean isValidStatusCode(int paramInt)
  {
    return (paramInt == 1) || (paramInt == 2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmInfoStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */