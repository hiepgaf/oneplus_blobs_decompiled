package android.media;

import java.util.ArrayList;
import java.util.List;

class TtmlNode
{
  public final String mAttributes;
  public final List<TtmlNode> mChildren = new ArrayList();
  public final long mEndTimeMs;
  public final String mName;
  public final TtmlNode mParent;
  public final long mRunId;
  public final long mStartTimeMs;
  public final String mText;
  
  public TtmlNode(String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2, TtmlNode paramTtmlNode, long paramLong3)
  {
    this.mName = paramString1;
    this.mAttributes = paramString2;
    this.mText = paramString3;
    this.mStartTimeMs = paramLong1;
    this.mEndTimeMs = paramLong2;
    this.mParent = paramTtmlNode;
    this.mRunId = paramLong3;
  }
  
  public boolean isActive(long paramLong1, long paramLong2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mEndTimeMs > paramLong1)
    {
      bool1 = bool2;
      if (this.mStartTimeMs < paramLong2) {
        bool1 = true;
      }
    }
    return bool1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */