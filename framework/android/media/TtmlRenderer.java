package android.media;

import android.content.Context;

public class TtmlRenderer
  extends SubtitleController.Renderer
{
  private static final String MEDIA_MIMETYPE_TEXT_TTML = "application/ttml+xml";
  private final Context mContext;
  private TtmlRenderingWidget mRenderingWidget;
  
  public TtmlRenderer(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public SubtitleTrack createTrack(MediaFormat paramMediaFormat)
  {
    if (this.mRenderingWidget == null) {
      this.mRenderingWidget = new TtmlRenderingWidget(this.mContext);
    }
    return new TtmlTrack(this.mRenderingWidget, paramMediaFormat);
  }
  
  public boolean supports(MediaFormat paramMediaFormat)
  {
    if (paramMediaFormat.containsKey("mime")) {
      return paramMediaFormat.getString("mime").equals("application/ttml+xml");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */