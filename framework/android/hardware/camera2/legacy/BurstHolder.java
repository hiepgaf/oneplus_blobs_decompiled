package android.hardware.camera2.legacy;

import android.hardware.camera2.CaptureRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BurstHolder
{
  private static final String TAG = "BurstHolder";
  private final boolean mRepeating;
  private final ArrayList<RequestHolder.Builder> mRequestBuilders = new ArrayList();
  private final int mRequestId;
  
  public BurstHolder(int paramInt, boolean paramBoolean, CaptureRequest[] paramArrayOfCaptureRequest, Collection<Long> paramCollection)
  {
    int j = 0;
    int k = paramArrayOfCaptureRequest.length;
    int i = 0;
    while (i < k)
    {
      CaptureRequest localCaptureRequest = paramArrayOfCaptureRequest[i];
      this.mRequestBuilders.add(new RequestHolder.Builder(paramInt, j, localCaptureRequest, paramBoolean, paramCollection));
      j += 1;
      i += 1;
    }
    this.mRepeating = paramBoolean;
    this.mRequestId = paramInt;
  }
  
  public int getNumberOfRequests()
  {
    return this.mRequestBuilders.size();
  }
  
  public int getRequestId()
  {
    return this.mRequestId;
  }
  
  public boolean isRepeating()
  {
    return this.mRepeating;
  }
  
  public List<RequestHolder> produceRequestHolders(long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    Iterator localIterator = this.mRequestBuilders.iterator();
    while (localIterator.hasNext())
    {
      localArrayList.add(((RequestHolder.Builder)localIterator.next()).build(i + paramLong));
      i += 1;
    }
    return localArrayList;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/BurstHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */