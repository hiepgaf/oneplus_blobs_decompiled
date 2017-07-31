package android.renderscript;

import dalvik.system.CloseGuard;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

public class BaseObj
{
  final CloseGuard guard = CloseGuard.get();
  private boolean mDestroyed;
  private long mID;
  private String mName;
  RenderScript mRS;
  
  BaseObj(long paramLong, RenderScript paramRenderScript)
  {
    paramRenderScript.validate();
    this.mRS = paramRenderScript;
    this.mID = paramLong;
    this.mDestroyed = false;
  }
  
  private void helpDestroy()
  {
    int i = 0;
    try
    {
      if (!this.mDestroyed)
      {
        i = 1;
        this.mDestroyed = true;
      }
      if (i != 0)
      {
        this.guard.close();
        ReentrantReadWriteLock.ReadLock localReadLock = this.mRS.mRWLock.readLock();
        localReadLock.lock();
        if ((this.mRS.isAlive()) && (this.mID != 0L)) {
          this.mRS.nObjDestroy(this.mID);
        }
        localReadLock.unlock();
        this.mRS = null;
        this.mID = 0L;
      }
      return;
    }
    finally {}
  }
  
  void checkValid()
  {
    if (this.mID == 0L) {
      throw new RSIllegalArgumentException("Invalid object.");
    }
  }
  
  public void destroy()
  {
    if (this.mDestroyed) {
      throw new RSInvalidStateException("Object already destroyed.");
    }
    helpDestroy();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (BaseObj)paramObject;
    return this.mID == ((BaseObj)paramObject).mID;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.guard != null) {
        this.guard.warnIfOpen();
      }
      helpDestroy();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  long getID(RenderScript paramRenderScript)
  {
    this.mRS.validate();
    if (this.mDestroyed) {
      throw new RSInvalidStateException("using a destroyed object.");
    }
    if (this.mID == 0L) {
      throw new RSRuntimeException("Internal error: Object id 0.");
    }
    if ((paramRenderScript != null) && (paramRenderScript != this.mRS)) {
      throw new RSInvalidStateException("using object with mismatched context.");
    }
    return this.mID;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int hashCode()
  {
    return (int)(this.mID & 0xFFFFFFF ^ this.mID >> 32);
  }
  
  void setID(long paramLong)
  {
    if (this.mID != 0L) {
      throw new RSRuntimeException("Internal Error, reset of object ID.");
    }
    this.mID = paramLong;
  }
  
  public void setName(String paramString)
  {
    if (paramString == null) {
      throw new RSIllegalArgumentException("setName requires a string of non-zero length.");
    }
    if (paramString.length() < 1) {
      throw new RSIllegalArgumentException("setName does not accept a zero length string.");
    }
    if (this.mName != null) {
      throw new RSIllegalArgumentException("setName object already has a name.");
    }
    try
    {
      byte[] arrayOfByte = paramString.getBytes("UTF-8");
      this.mRS.nAssignName(this.mID, arrayOfByte);
      this.mName = paramString;
      return;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new RuntimeException(paramString);
    }
  }
  
  void updateFromNative()
  {
    this.mRS.validate();
    this.mName = this.mRS.nGetName(getID(this.mRS));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/BaseObj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */