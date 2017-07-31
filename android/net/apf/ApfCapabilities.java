package android.net.apf;

public class ApfCapabilities
{
  public final int apfPacketFormat;
  public final int apfVersionSupported;
  public final int maximumApfProgramSize;
  
  public ApfCapabilities(int paramInt1, int paramInt2, int paramInt3)
  {
    this.apfVersionSupported = paramInt1;
    this.maximumApfProgramSize = paramInt2;
    this.apfPacketFormat = paramInt3;
  }
  
  public String toString()
  {
    return String.format("%s{version: %d, maxSize: %d format: %d}", new Object[] { getClass().getSimpleName(), Integer.valueOf(this.apfVersionSupported), Integer.valueOf(this.maximumApfProgramSize), Integer.valueOf(this.apfPacketFormat) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/apf/ApfCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */