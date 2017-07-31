package android.content;

public class UndoOwner
{
  Object mData;
  final UndoManager mManager;
  int mOpCount;
  int mSavedIdx;
  int mStateSeq;
  final String mTag;
  
  UndoOwner(String paramString, UndoManager paramUndoManager)
  {
    if (paramString == null) {
      throw new NullPointerException("tag can't be null");
    }
    if (paramUndoManager == null) {
      throw new NullPointerException("manager can't be null");
    }
    this.mTag = paramString;
    this.mManager = paramUndoManager;
  }
  
  public Object getData()
  {
    return this.mData;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public String toString()
  {
    return "UndoOwner:[mTag=" + this.mTag + " mManager=" + this.mManager + " mData=" + this.mData + " mData=" + this.mData + " mOpCount=" + this.mOpCount + " mStateSeq=" + this.mStateSeq + " mSavedIdx=" + this.mSavedIdx + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/UndoOwner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */