package android.media;

import java.util.Vector;

class Cea708CaptionTrack
  extends SubtitleTrack
{
  private final Cea708CCParser mCCParser;
  private final Cea708CCWidget mRenderingWidget;
  
  Cea708CaptionTrack(Cea708CCWidget paramCea708CCWidget, MediaFormat paramMediaFormat)
  {
    super(paramMediaFormat);
    this.mRenderingWidget = paramCea708CCWidget;
    this.mCCParser = new Cea708CCParser(this.mRenderingWidget);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea708CaptionTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */