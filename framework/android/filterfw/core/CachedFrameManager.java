package android.filterfw.core;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class CachedFrameManager
  extends SimpleFrameManager
{
  private SortedMap<Integer, Frame> mAvailableFrames = new TreeMap();
  private int mStorageCapacity = 25165824;
  private int mStorageSize = 0;
  private int mTimeStamp = 0;
  
  private void dropOldestFrame()
  {
    int i = ((Integer)this.mAvailableFrames.firstKey()).intValue();
    Frame localFrame = (Frame)this.mAvailableFrames.get(Integer.valueOf(i));
    this.mStorageSize -= localFrame.getFormat().getSize();
    localFrame.releaseNativeAllocation();
    this.mAvailableFrames.remove(Integer.valueOf(i));
  }
  
  private Frame findAvailableFrame(FrameFormat paramFrameFormat, int paramInt, long paramLong)
  {
    synchronized (this.mAvailableFrames)
    {
      Iterator localIterator = this.mAvailableFrames.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Frame localFrame = (Frame)localEntry.getValue();
        if ((localFrame.getFormat().isReplaceableBy(paramFrameFormat)) && (paramInt == localFrame.getBindingType()) && ((paramInt == 0) || (paramLong == localFrame.getBindingId())))
        {
          super.retainFrame(localFrame);
          this.mAvailableFrames.remove(localEntry.getKey());
          localFrame.onFrameFetch();
          localFrame.reset(paramFrameFormat);
          this.mStorageSize -= paramFrameFormat.getSize();
          return localFrame;
        }
      }
      return null;
    }
  }
  
  private boolean storeFrame(Frame paramFrame)
  {
    synchronized (this.mAvailableFrames)
    {
      int j = paramFrame.getFormat().getSize();
      int i = this.mStorageCapacity;
      if (j > i) {
        return false;
      }
      for (i = this.mStorageSize + j; i > this.mStorageCapacity; i = this.mStorageSize + j) {
        dropOldestFrame();
      }
      paramFrame.onFrameStore();
      this.mStorageSize = i;
      this.mAvailableFrames.put(Integer.valueOf(this.mTimeStamp), paramFrame);
      this.mTimeStamp += 1;
      return true;
    }
  }
  
  public void clearCache()
  {
    Iterator localIterator = this.mAvailableFrames.values().iterator();
    while (localIterator.hasNext()) {
      ((Frame)localIterator.next()).releaseNativeAllocation();
    }
    this.mAvailableFrames.clear();
  }
  
  public Frame newBoundFrame(FrameFormat paramFrameFormat, int paramInt, long paramLong)
  {
    Frame localFrame2 = findAvailableFrame(paramFrameFormat, paramInt, paramLong);
    Frame localFrame1 = localFrame2;
    if (localFrame2 == null) {
      localFrame1 = super.newBoundFrame(paramFrameFormat, paramInt, paramLong);
    }
    localFrame1.setTimestamp(-2L);
    return localFrame1;
  }
  
  public Frame newFrame(FrameFormat paramFrameFormat)
  {
    Frame localFrame2 = findAvailableFrame(paramFrameFormat, 0, 0L);
    Frame localFrame1 = localFrame2;
    if (localFrame2 == null) {
      localFrame1 = super.newFrame(paramFrameFormat);
    }
    localFrame1.setTimestamp(-2L);
    return localFrame1;
  }
  
  public Frame releaseFrame(Frame paramFrame)
  {
    if (paramFrame.isReusable())
    {
      int i = paramFrame.decRefCount();
      if ((i == 0) && (paramFrame.hasNativeAllocation()))
      {
        if (!storeFrame(paramFrame)) {
          paramFrame.releaseNativeAllocation();
        }
        return null;
      }
      if (i < 0) {
        throw new RuntimeException("Frame reference count dropped below 0!");
      }
    }
    else
    {
      super.releaseFrame(paramFrame);
    }
    return paramFrame;
  }
  
  public Frame retainFrame(Frame paramFrame)
  {
    return super.retainFrame(paramFrame);
  }
  
  public void tearDown()
  {
    clearCache();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/CachedFrameManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */