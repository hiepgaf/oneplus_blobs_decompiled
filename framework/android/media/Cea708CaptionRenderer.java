package android.media;

import android.content.Context;

public class Cea708CaptionRenderer
  extends SubtitleController.Renderer
{
  private Cea708CCWidget mCCWidget;
  private final Context mContext;
  
  public Cea708CaptionRenderer(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public SubtitleTrack createTrack(MediaFormat paramMediaFormat)
  {
    if ("text/cea-708".equals(paramMediaFormat.getString("mime")))
    {
      if (this.mCCWidget == null) {
        this.mCCWidget = new Cea708CCWidget(this.mContext);
      }
      return new Cea708CaptionTrack(this.mCCWidget, paramMediaFormat);
    }
    throw new RuntimeException("No matching format: " + paramMediaFormat.toString());
  }
  
  public boolean supports(MediaFormat paramMediaFormat)
  {
    if (paramMediaFormat.containsKey("mime")) {
      return "text/cea-708".equals(paramMediaFormat.getString("mime"));
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea708CaptionRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */