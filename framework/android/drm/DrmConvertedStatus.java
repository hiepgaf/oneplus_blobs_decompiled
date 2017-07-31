package android.drm;

public class DrmConvertedStatus
{
  public static final int STATUS_ERROR = 3;
  public static final int STATUS_INPUTDATA_ERROR = 2;
  public static final int STATUS_OK = 1;
  public final byte[] convertedData;
  public final int offset;
  public final int statusCode;
  
  public DrmConvertedStatus(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    if (!isValidStatusCode(paramInt1)) {
      throw new IllegalArgumentException("Unsupported status code: " + paramInt1);
    }
    this.statusCode = paramInt1;
    this.convertedData = paramArrayOfByte;
    this.offset = paramInt2;
  }
  
  private boolean isValidStatusCode(int paramInt)
  {
    if ((paramInt == 1) || (paramInt == 2)) {}
    while (paramInt == 3) {
      return true;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmConvertedStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */