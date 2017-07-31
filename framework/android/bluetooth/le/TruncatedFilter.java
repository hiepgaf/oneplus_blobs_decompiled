package android.bluetooth.le;

import java.util.List;

public final class TruncatedFilter
{
  private final ScanFilter mFilter;
  private final List<ResultStorageDescriptor> mStorageDescriptors;
  
  public TruncatedFilter(ScanFilter paramScanFilter, List<ResultStorageDescriptor> paramList)
  {
    this.mFilter = paramScanFilter;
    this.mStorageDescriptors = paramList;
  }
  
  public ScanFilter getFilter()
  {
    return this.mFilter;
  }
  
  public List<ResultStorageDescriptor> getStorageDescriptors()
  {
    return this.mStorageDescriptors;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/TruncatedFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */