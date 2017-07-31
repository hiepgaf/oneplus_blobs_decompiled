package android.os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStatVfs;

public class StatFs
{
  private StructStatVfs mStat;
  
  public StatFs(String paramString)
  {
    this.mStat = doStat(paramString);
  }
  
  private static StructStatVfs doStat(String paramString)
  {
    try
    {
      StructStatVfs localStructStatVfs = Os.statvfs(paramString);
      return localStructStatVfs;
    }
    catch (ErrnoException localErrnoException)
    {
      throw new IllegalArgumentException("Invalid path: " + paramString, localErrnoException);
    }
  }
  
  @Deprecated
  public int getAvailableBlocks()
  {
    return (int)this.mStat.f_bavail;
  }
  
  public long getAvailableBlocksLong()
  {
    return this.mStat.f_bavail;
  }
  
  public long getAvailableBytes()
  {
    return this.mStat.f_bavail * this.mStat.f_bsize;
  }
  
  @Deprecated
  public int getBlockCount()
  {
    return (int)this.mStat.f_blocks;
  }
  
  public long getBlockCountLong()
  {
    return this.mStat.f_blocks;
  }
  
  @Deprecated
  public int getBlockSize()
  {
    return (int)this.mStat.f_bsize;
  }
  
  public long getBlockSizeLong()
  {
    return this.mStat.f_bsize;
  }
  
  @Deprecated
  public int getFreeBlocks()
  {
    return (int)this.mStat.f_bfree;
  }
  
  public long getFreeBlocksLong()
  {
    return this.mStat.f_bfree;
  }
  
  public long getFreeBytes()
  {
    return this.mStat.f_bfree * this.mStat.f_bsize;
  }
  
  public long getTotalBytes()
  {
    return this.mStat.f_blocks * this.mStat.f_bsize;
  }
  
  public void restat(String paramString)
  {
    this.mStat = doStat(paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/StatFs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */