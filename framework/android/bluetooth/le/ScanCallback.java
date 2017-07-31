package android.bluetooth.le;

import java.util.List;

public abstract class ScanCallback
{
  public static final int SCAN_FAILED_ALREADY_STARTED = 1;
  public static final int SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2;
  public static final int SCAN_FAILED_FEATURE_UNSUPPORTED = 4;
  public static final int SCAN_FAILED_INTERNAL_ERROR = 3;
  public static final int SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5;
  
  public void onBatchScanResults(List<ScanResult> paramList) {}
  
  public void onScanFailed(int paramInt) {}
  
  public void onScanResult(int paramInt, ScanResult paramScanResult) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/ScanCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */