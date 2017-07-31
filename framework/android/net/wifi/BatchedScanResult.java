package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class BatchedScanResult
  implements Parcelable
{
  public static final Parcelable.Creator<BatchedScanResult> CREATOR = new Parcelable.Creator()
  {
    public BatchedScanResult createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool = true;
      BatchedScanResult localBatchedScanResult = new BatchedScanResult();
      if (paramAnonymousParcel.readInt() == 1) {}
      for (;;)
      {
        localBatchedScanResult.truncated = bool;
        int i = paramAnonymousParcel.readInt();
        while (i > 0)
        {
          localBatchedScanResult.scanResults.add((ScanResult)ScanResult.CREATOR.createFromParcel(paramAnonymousParcel));
          i -= 1;
        }
        bool = false;
      }
      return localBatchedScanResult;
    }
    
    public BatchedScanResult[] newArray(int paramAnonymousInt)
    {
      return new BatchedScanResult[paramAnonymousInt];
    }
  };
  private static final String TAG = "BatchedScanResult";
  public final List<ScanResult> scanResults = new ArrayList();
  public boolean truncated;
  
  public BatchedScanResult() {}
  
  public BatchedScanResult(BatchedScanResult paramBatchedScanResult)
  {
    this.truncated = paramBatchedScanResult.truncated;
    paramBatchedScanResult = paramBatchedScanResult.scanResults.iterator();
    while (paramBatchedScanResult.hasNext())
    {
      ScanResult localScanResult = (ScanResult)paramBatchedScanResult.next();
      this.scanResults.add(new ScanResult(localScanResult));
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("BatchedScanResult: ").append("truncated: ").append(String.valueOf(this.truncated)).append("scanResults: [");
    Iterator localIterator = this.scanResults.iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      localStringBuffer.append(" <").append(localScanResult.toString()).append("> ");
    }
    localStringBuffer.append(" ]");
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.truncated) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeInt(this.scanResults.size());
      Iterator localIterator = this.scanResults.iterator();
      while (localIterator.hasNext()) {
        ((ScanResult)localIterator.next()).writeToParcel(paramParcel, paramInt);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/BatchedScanResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */