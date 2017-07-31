package android.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.view.accessibility.CaptioningManager.CaptioningChangeListener;

abstract class ClosedCaptionWidget
  extends ViewGroup
  implements SubtitleTrack.RenderingWidget
{
  private static final CaptioningManager.CaptionStyle DEFAULT_CAPTION_STYLE = CaptioningManager.CaptionStyle.DEFAULT;
  protected CaptioningManager.CaptionStyle mCaptionStyle;
  private final CaptioningManager.CaptioningChangeListener mCaptioningListener = new CaptioningManager.CaptioningChangeListener()
  {
    public void onFontScaleChanged(float paramAnonymousFloat)
    {
      ClosedCaptionWidget.this.mClosedCaptionLayout.setFontScale(paramAnonymousFloat);
    }
    
    public void onUserStyleChanged(CaptioningManager.CaptionStyle paramAnonymousCaptionStyle)
    {
      ClosedCaptionWidget.this.mCaptionStyle = ClosedCaptionWidget.-get0().applyStyle(paramAnonymousCaptionStyle);
      ClosedCaptionWidget.this.mClosedCaptionLayout.setCaptionStyle(ClosedCaptionWidget.this.mCaptionStyle);
    }
  };
  protected ClosedCaptionLayout mClosedCaptionLayout;
  private boolean mHasChangeListener;
  protected SubtitleTrack.RenderingWidget.OnChangedListener mListener;
  private final CaptioningManager mManager;
  
  public ClosedCaptionWidget(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ClosedCaptionWidget(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ClosedCaptionWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ClosedCaptionWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayerType(1, null);
    this.mManager = ((CaptioningManager)paramContext.getSystemService("captioning"));
    this.mCaptionStyle = DEFAULT_CAPTION_STYLE.applyStyle(this.mManager.getUserStyle());
    this.mClosedCaptionLayout = createCaptionLayout(paramContext);
    this.mClosedCaptionLayout.setCaptionStyle(this.mCaptionStyle);
    this.mClosedCaptionLayout.setFontScale(this.mManager.getFontScale());
    addView((ViewGroup)this.mClosedCaptionLayout, -1, -1);
    requestLayout();
  }
  
  private void manageChangeListener()
  {
    if ((isAttachedToWindow()) && (getVisibility() == 0)) {}
    for (boolean bool = true;; bool = false)
    {
      if (this.mHasChangeListener != bool)
      {
        this.mHasChangeListener = bool;
        if (!bool) {
          break;
        }
        this.mManager.addCaptioningChangeListener(this.mCaptioningListener);
      }
      return;
    }
    this.mManager.removeCaptioningChangeListener(this.mCaptioningListener);
  }
  
  public abstract ClosedCaptionLayout createCaptionLayout(Context paramContext);
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    manageChangeListener();
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    manageChangeListener();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ((ViewGroup)this.mClosedCaptionLayout).layout(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    ((ViewGroup)this.mClosedCaptionLayout).measure(paramInt1, paramInt2);
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
    if (paramBoolean) {
      setVisibility(0);
    }
    for (;;)
    {
      manageChangeListener();
      return;
      setVisibility(8);
    }
  }
  
  static abstract interface ClosedCaptionLayout
  {
    public abstract void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle);
    
    public abstract void setFontScale(float paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ClosedCaptionWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */