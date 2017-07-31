package android.media;

import java.util.Vector;

class Cea608CaptionTrack
  extends SubtitleTrack
{
  private final Cea608CCParser mCCParser;
  private final Cea608CCWidget mRenderingWidget;
  
  Cea608CaptionTrack(Cea608CCWidget paramCea608CCWidget, MediaFormat paramMediaFormat)
  {
    super(paramMediaFormat);
    this.mRenderingWidget = paramCea608CCWidget;
    this.mCCParser = new Cea608CCParser(this.mRenderingWidget);
  }
  
  public SubtitleTrack.RenderingWidget getRenderingWidget()
  {
    return this.mRenderingWidget;
  }
  
  public void onData(byte[] paramArrayOfByte, boolean paramBoolean, long paramLong)
  {
    this.mCCParser.parse(paramArrayOfByte);
  }
  
  public void updateView(Vector<SubtitleTrack.Cue> paramVector) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea608CaptionTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */