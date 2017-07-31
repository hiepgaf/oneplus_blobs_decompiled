package android.media;

import android.content.Context;
import android.os.Handler;

public class SRTRenderer
  extends SubtitleController.Renderer
{
  private final Context mContext;
  private final Handler mEventHandler;
  private final boolean mRender;
  private WebVttRenderingWidget mRenderingWidget;
  
  public SRTRenderer(Context paramContext)
  {
    this(paramContext, null);
  }
  
  SRTRenderer(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    if (paramHandler == null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mRender = bool;
      this.mEventHandler = paramHandler;
      return;
    }
  }
  
  public SubtitleTrack createTrack(MediaFormat paramMediaFormat)
  {
    if ((this.mRender) && (this.mRenderingWidget == null)) {
      this.mRenderingWidget = new WebVttRenderingWidget(this.mContext);
    }
    if (this.mRender) {
      return new SRTTrack(this.mRenderingWidget, paramMediaFormat);
    }
    return new SRTTrack(this.mEventHandler, paramMediaFormat);
  }
  
  public boolean supports(MediaFormat paramMediaFormat)
  {
    if (paramMediaFormat.containsKey("mime"))
    {
      if (!paramMediaFormat.getString("mime").equals("application/x-subrip")) {
        return false;
      }
      boolean bool2 = this.mRender;
      if (paramMediaFormat.getInteger("is-timed-text", 0) == 0) {}
      for (boolean bool1 = true; bool2 == bool1; bool1 = false) {
        return true;
      }
      return false;
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SRTRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */