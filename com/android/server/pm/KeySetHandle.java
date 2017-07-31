package com.android.server.pm;

import android.os.Binder;

class KeySetHandle
  extends Binder
{
  private final long mId;
  private int mRefCount;
  
  protected KeySetHandle(long paramLong)
  {
    this.mId = paramLong;
    this.mRefCount = 1;
  }
  
  protected KeySetHandle(long paramLong, int paramInt)
  {
    this.mId = paramLong;
    this.mRefCount = paramInt;
  }
  
  protected int decrRefCountLPw()
  {
    this.mRefCount -= 1;
    return this.mRefCount;
  }
  
  public long getId()
  {
    return this.mId;
  }
  
  protected int getRefCountLPr()
  {
    return this.mRefCount;
  }
  
  protected void incrRefCountLPw()
  {
    this.mRefCount += 1;
  }
  
  protected void setRefCountLPw(int paramInt)
  {
    this.mRefCount = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/KeySetHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */