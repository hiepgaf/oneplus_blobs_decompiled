package android.media;

class AudioHandle
{
  private final int mId;
  
  AudioHandle(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject != null) && ((paramObject instanceof AudioHandle)))
    {
      paramObject = (AudioHandle)paramObject;
      if (this.mId == ((AudioHandle)paramObject).id()) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.mId;
  }
  
  int id()
  {
    return this.mId;
  }
  
  public String toString()
  {
    return Integer.toString(this.mId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */