package android.media;

class TextTrackRegion
{
  static final int SCROLL_VALUE_NONE = 300;
  static final int SCROLL_VALUE_SCROLL_UP = 301;
  float mAnchorPointX = 0.0F;
  float mAnchorPointY = 100.0F;
  String mId = "";
  int mLines = 3;
  int mScrollValue = 300;
  float mViewportAnchorPointX = 0.0F;
  float mViewportAnchorPointY = 100.0F;
  float mWidth = 100.0F;
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(" {id:\"").append(this.mId).append("\", width:").append(this.mWidth).append(", lines:").append(this.mLines).append(", anchorPoint:(").append(this.mAnchorPointX).append(", ").append(this.mAnchorPointY).append("), viewportAnchorPoints:").append(this.mViewportAnchorPointX).append(", ").append(this.mViewportAnchorPointY).append("), scrollValue:");
    String str;
    if (this.mScrollValue == 300) {
      str = "none";
    }
    for (;;)
    {
      return str + "}";
      if (this.mScrollValue == 301) {
        str = "scroll_up";
      } else {
        str = "INVALID";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TextTrackRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */