package android.media;

import android.content.Context;

public class WebVttRenderer
  extends SubtitleController.Renderer
{
  private final Context mContext;
  private WebVttRenderingWidget mRenderingWidget;
  
  public WebVttRenderer(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public SubtitleTrack createTrack(MediaFormat paramMediaFormat)
  {
    if (this.mRenderingWidget == null) {
      this.mRenderingWidget = new WebVttRenderingWidget(this.mContext);
    }
    return new WebVttTrack(this.mRenderingWidget, paramMediaFormat);
  }
  
  public boolean supports(MediaFormat paramMediaFormat)
  {
    if (paramMediaFormat.containsKey("mime")) {
      return paramMediaFormat.getString("mime").equals("text/vtt");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/WebVttRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */