package android.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Vector;

class TtmlRenderingWidget
  extends LinearLayout
  implements SubtitleTrack.RenderingWidget
{
  private SubtitleTrack.RenderingWidget.OnChangedListener mListener;
  private final TextView mTextView;
  
  public TtmlRenderingWidget(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TtmlRenderingWidget(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TtmlRenderingWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TtmlRenderingWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayerType(1, null);
    paramAttributeSet = (CaptioningManager)paramContext.getSystemService("captioning");
    this.mTextView = new TextView(paramContext);
    this.mTextView.setTextColor(paramAttributeSet.getUserStyle().foregroundColor);
    addView(this.mTextView, -1, -1);
    this.mTextView.setGravity(81);
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
  }
  
  public void setActiveCues(Vector<SubtitleTrack.Cue> paramVector)
  {
    int j = paramVector.size();
    String str = "";
    int i = 0;
    while (i < j)
    {
      TtmlCue localTtmlCue = (TtmlCue)paramVector.get(i);
      str = str + localTtmlCue.mText + "\n";
      i += 1;
    }
    this.mTextView.setText(str);
    if (this.mListener != null) {
      this.mListener.onChanged(this);
    }
  }
  
  public void setOnChangedListener(SubtitleTrack.RenderingWidget.OnChangedListener paramOnChangedListener)
  {
    this.mListener = paramOnChangedListener;
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
    layout(0, 0, paramInt1, paramInt2);
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setVisibility(0);
      return;
    }
    setVisibility(8);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlRenderingWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */