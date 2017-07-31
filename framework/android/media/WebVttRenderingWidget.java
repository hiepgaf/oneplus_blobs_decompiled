package android.media;

import android.content.Context;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.view.accessibility.CaptioningManager.CaptioningChangeListener;
import android.widget.LinearLayout;
import com.android.internal.widget.SubtitleView;
import java.util.ArrayList;
import java.util.Vector;

class WebVttRenderingWidget
  extends ViewGroup
  implements SubtitleTrack.RenderingWidget
{
  private static final boolean DEBUG = false;
  private static final int DEBUG_CUE_BACKGROUND = -2130771968;
  private static final int DEBUG_REGION_BACKGROUND = -2147483393;
  private static final CaptioningManager.CaptionStyle DEFAULT_CAPTION_STYLE = CaptioningManager.CaptionStyle.DEFAULT;
  private static final float LINE_HEIGHT_RATIO = 0.0533F;
  private CaptioningManager.CaptionStyle mCaptionStyle;
  private final CaptioningManager.CaptioningChangeListener mCaptioningListener = new CaptioningManager.CaptioningChangeListener()
  {
    public void onFontScaleChanged(float paramAnonymousFloat)
    {
      float f = WebVttRenderingWidget.this.getHeight();
      WebVttRenderingWidget.-wrap1(WebVttRenderingWidget.this, WebVttRenderingWidget.-get0(WebVttRenderingWidget.this), f * paramAnonymousFloat * 0.0533F);
    }
    
    public void onUserStyleChanged(CaptioningManager.CaptionStyle paramAnonymousCaptionStyle)
    {
      WebVttRenderingWidget.-wrap1(WebVttRenderingWidget.this, paramAnonymousCaptionStyle, WebVttRenderingWidget.-get1(WebVttRenderingWidget.this));
    }
  };
  private final ArrayMap<TextTrackCue, CueLayout> mCueBoxes = new ArrayMap();
  private float mFontSize;
  private boolean mHasChangeListener;
  private SubtitleTrack.RenderingWidget.OnChangedListener mListener;
  private final CaptioningManager mManager;
  private final ArrayMap<TextTrackRegion, RegionLayout> mRegionBoxes = new ArrayMap();
  
  public WebVttRenderingWidget(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public WebVttRenderingWidget(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public WebVttRenderingWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public WebVttRenderingWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayerType(1, null);
    this.mManager = ((CaptioningManager)paramContext.getSystemService("captioning"));
    this.mCaptionStyle = this.mManager.getUserStyle();
    this.mFontSize = (this.mManager.getFontScale() * getHeight() * 0.0533F);
  }
  
  private int calculateLinePosition(CueLayout paramCueLayout)
  {
    TextTrackCue localTextTrackCue = paramCueLayout.getCue();
    Integer localInteger = localTextTrackCue.mLinePosition;
    boolean bool = localTextTrackCue.mSnapToLines;
    int i;
    if (localInteger == null)
    {
      i = 1;
      if ((!bool) && (i == 0)) {
        break label49;
      }
    }
    for (;;)
    {
      if (i == 0)
      {
        return localInteger.intValue();
        i = 0;
        break;
        label49:
        if ((localInteger.intValue() < 0) || (localInteger.intValue() > 100)) {
          return 100;
        }
      }
    }
    if (!bool) {
      return 100;
    }
    return -(CueLayout.-get0(paramCueLayout) + 1);
  }
  
  private void layoutCue(int paramInt1, int paramInt2, CueLayout paramCueLayout)
  {
    TextTrackCue localTextTrackCue = paramCueLayout.getCue();
    int k = getLayoutDirection();
    int i = resolveCueAlignment(k, localTextTrackCue.mAlignment);
    boolean bool = localTextTrackCue.mSnapToLines;
    int n = paramCueLayout.getMeasuredWidth() * 100 / paramInt1;
    int j;
    switch (i)
    {
    default: 
      j = localTextTrackCue.mTextPosition - n / 2;
      i = j;
      if (k == 1) {
        i = 100 - j;
      }
      int m = n;
      int i1 = i;
      if (bool)
      {
        m = getPaddingLeft() * 100 / paramInt1;
        int i2 = getPaddingRight() * 100 / paramInt1;
        k = n;
        j = i;
        if (i < m)
        {
          k = n;
          j = i;
          if (i + n > m)
          {
            j = i + m;
            k = n - m;
          }
        }
        float f = 100 - i2;
        m = k;
        i1 = j;
        if (j < f)
        {
          m = k;
          i1 = j;
          if (j + k > f)
          {
            m = k - i2;
            i1 = j;
          }
        }
      }
      i = i1 * paramInt1 / 100;
      j = m * paramInt1 / 100;
      paramInt1 = calculateLinePosition(paramCueLayout);
      k = paramCueLayout.getMeasuredHeight();
      if (paramInt1 >= 0) {
        break;
      }
    }
    for (paramInt1 = paramInt2 + paramInt1 * k;; paramInt1 = (paramInt2 - k) * paramInt1 / 100)
    {
      paramCueLayout.layout(i, paramInt1, i + j, paramInt1 + k);
      return;
      j = localTextTrackCue.mTextPosition;
      break;
      j = localTextTrackCue.mTextPosition - n;
      break;
    }
  }
  
  private void layoutRegion(int paramInt1, int paramInt2, RegionLayout paramRegionLayout)
  {
    TextTrackRegion localTextTrackRegion = paramRegionLayout.getRegion();
    int i = paramRegionLayout.getMeasuredHeight();
    int j = paramRegionLayout.getMeasuredWidth();
    float f1 = localTextTrackRegion.mViewportAnchorPointX;
    float f2 = localTextTrackRegion.mViewportAnchorPointY;
    paramInt1 = (int)((paramInt1 - j) * f1 / 100.0F);
    paramInt2 = (int)((paramInt2 - i) * f2 / 100.0F);
    paramRegionLayout.layout(paramInt1, paramInt2, paramInt1 + j, paramInt2 + i);
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
        setCaptionStyle(this.mManager.getUserStyle(), this.mManager.getFontScale() * getHeight() * 0.0533F);
      }
      return;
    }
    this.mManager.removeCaptioningChangeListener(this.mCaptioningListener);
  }
  
  private void prepForPrune()
  {
    int j = this.mRegionBoxes.size();
    int i = 0;
    while (i < j)
    {
      ((RegionLayout)this.mRegionBoxes.valueAt(i)).prepForPrune();
      i += 1;
    }
    j = this.mCueBoxes.size();
    i = 0;
    while (i < j)
    {
      ((CueLayout)this.mCueBoxes.valueAt(i)).prepForPrune();
      i += 1;
    }
  }
  
  private void prune()
  {
    int k = this.mRegionBoxes.size();
    int i = 0;
    Object localObject;
    int m;
    while (i < k)
    {
      localObject = (RegionLayout)this.mRegionBoxes.valueAt(i);
      m = i;
      j = k;
      if (((RegionLayout)localObject).prune())
      {
        removeView((View)localObject);
        this.mRegionBoxes.removeAt(i);
        j = k - 1;
        m = i - 1;
      }
      i = m + 1;
      k = j;
    }
    int j = this.mCueBoxes.size();
    i = 0;
    while (i < j)
    {
      localObject = (CueLayout)this.mCueBoxes.valueAt(i);
      k = j;
      m = i;
      if (!((CueLayout)localObject).isActive())
      {
        removeView((View)localObject);
        this.mCueBoxes.removeAt(i);
        k = j - 1;
        m = i - 1;
      }
      i = m + 1;
      j = k;
    }
  }
  
  private static int resolveCueAlignment(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    default: 
      return paramInt2;
    case 201: 
      if (paramInt1 == 0) {
        return 203;
      }
      return 204;
    }
    if (paramInt1 == 0) {
      return 204;
    }
    return 203;
  }
  
  private void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle, float paramFloat)
  {
    paramCaptionStyle = DEFAULT_CAPTION_STYLE.applyStyle(paramCaptionStyle);
    this.mCaptionStyle = paramCaptionStyle;
    this.mFontSize = paramFloat;
    int j = this.mCueBoxes.size();
    int i = 0;
    while (i < j)
    {
      ((CueLayout)this.mCueBoxes.valueAt(i)).setCaptionStyle(paramCaptionStyle, paramFloat);
      i += 1;
    }
    j = this.mRegionBoxes.size();
    i = 0;
    while (i < j)
    {
      ((RegionLayout)this.mRegionBoxes.valueAt(i)).setCaptionStyle(paramCaptionStyle, paramFloat);
      i += 1;
    }
  }
  
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
    paramInt3 -= paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    setCaptionStyle(this.mCaptionStyle, this.mManager.getFontScale() * 0.0533F * paramInt2);
    paramInt4 = this.mRegionBoxes.size();
    paramInt1 = 0;
    while (paramInt1 < paramInt4)
    {
      layoutRegion(paramInt3, paramInt2, (RegionLayout)this.mRegionBoxes.valueAt(paramInt1));
      paramInt1 += 1;
    }
    paramInt4 = this.mCueBoxes.size();
    paramInt1 = 0;
    while (paramInt1 < paramInt4)
    {
      layoutCue(paramInt3, paramInt2, (CueLayout)this.mCueBoxes.valueAt(paramInt1));
      paramInt1 += 1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int j = this.mRegionBoxes.size();
    int i = 0;
    while (i < j)
    {
      ((RegionLayout)this.mRegionBoxes.valueAt(i)).measureForParent(paramInt1, paramInt2);
      i += 1;
    }
    j = this.mCueBoxes.size();
    i = 0;
    while (i < j)
    {
      ((CueLayout)this.mCueBoxes.valueAt(i)).measureForParent(paramInt1, paramInt2);
      i += 1;
    }
  }
  
  public void setActiveCues(Vector<SubtitleTrack.Cue> paramVector)
  {
    Context localContext = getContext();
    CaptioningManager.CaptionStyle localCaptionStyle = this.mCaptionStyle;
    float f = this.mFontSize;
    prepForPrune();
    int j = paramVector.size();
    int i = 0;
    if (i < j)
    {
      TextTrackCue localTextTrackCue = (TextTrackCue)paramVector.get(i);
      TextTrackRegion localTextTrackRegion = localTextTrackCue.mRegion;
      Object localObject2;
      Object localObject1;
      if (localTextTrackRegion != null)
      {
        localObject2 = (RegionLayout)this.mRegionBoxes.get(localTextTrackRegion);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new RegionLayout(localContext, localTextTrackRegion, localCaptionStyle, f);
          this.mRegionBoxes.put(localTextTrackRegion, localObject1);
          addView((View)localObject1, -2, -2);
        }
        ((RegionLayout)localObject1).put(localTextTrackCue);
      }
      for (;;)
      {
        i += 1;
        break;
        localObject2 = (CueLayout)this.mCueBoxes.get(localTextTrackCue);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new CueLayout(localContext, localTextTrackCue, localCaptionStyle, f);
          this.mCueBoxes.put(localTextTrackCue, localObject1);
          addView((View)localObject1, -2, -2);
        }
        ((CueLayout)localObject1).update();
        ((CueLayout)localObject1).setOrder(i);
      }
    }
    prune();
    setSize(getWidth(), getHeight());
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
  
  private static class CueLayout
    extends LinearLayout
  {
    private boolean mActive;
    private CaptioningManager.CaptionStyle mCaptionStyle;
    public final TextTrackCue mCue;
    private float mFontSize;
    private int mOrder;
    
    public CueLayout(Context paramContext, TextTrackCue paramTextTrackCue, CaptioningManager.CaptionStyle paramCaptionStyle, float paramFloat)
    {
      super();
      this.mCue = paramTextTrackCue;
      this.mCaptionStyle = paramCaptionStyle;
      this.mFontSize = paramFloat;
      int i;
      if (paramTextTrackCue.mWritingDirection == 100)
      {
        i = 1;
        if (i != 0) {
          j = 1;
        }
        setOrientation(j);
        switch (paramTextTrackCue.mAlignment)
        {
        }
      }
      for (;;)
      {
        update();
        return;
        i = 0;
        break;
        setGravity(8388613);
        continue;
        setGravity(3);
        continue;
        if (i != 0) {}
        for (i = k;; i = 16)
        {
          setGravity(i);
          break;
        }
        setGravity(5);
        continue;
        setGravity(8388611);
      }
    }
    
    public TextTrackCue getCue()
    {
      return this.mCue;
    }
    
    public boolean isActive()
    {
      return this.mActive;
    }
    
    public void measureForParent(int paramInt1, int paramInt2)
    {
      TextTrackCue localTextTrackCue = this.mCue;
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      switch (WebVttRenderingWidget.-wrap0(getLayoutDirection(), localTextTrackCue.mAlignment))
      {
      case 201: 
      case 202: 
      default: 
        paramInt1 = 0;
      }
      for (;;)
      {
        measure(View.MeasureSpec.makeMeasureSpec(Math.min(localTextTrackCue.mSize, paramInt1) * i / 100, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
        return;
        paramInt1 = 100 - localTextTrackCue.mTextPosition;
        continue;
        paramInt1 = localTextTrackCue.mTextPosition;
        continue;
        if (localTextTrackCue.mTextPosition <= 50) {
          paramInt1 = localTextTrackCue.mTextPosition * 2;
        } else {
          paramInt1 = (100 - localTextTrackCue.mTextPosition) * 2;
        }
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
    }
    
    public void prepForPrune()
    {
      this.mActive = false;
    }
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle, float paramFloat)
    {
      this.mCaptionStyle = paramCaptionStyle;
      this.mFontSize = paramFloat;
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = getChildAt(i);
        if ((localView instanceof WebVttRenderingWidget.SpanLayout)) {
          localView.setCaptionStyle(paramCaptionStyle, paramFloat);
        }
        i += 1;
      }
    }
    
    public void setOrder(int paramInt)
    {
      this.mOrder = paramInt;
    }
    
    public void update()
    {
      this.mActive = true;
      removeAllViews();
      Layout.Alignment localAlignment;
      switch (WebVttRenderingWidget.-wrap0(getLayoutDirection(), this.mCue.mAlignment))
      {
      default: 
        localAlignment = Layout.Alignment.ALIGN_CENTER;
      }
      for (;;)
      {
        CaptioningManager.CaptionStyle localCaptionStyle = this.mCaptionStyle;
        float f = this.mFontSize;
        TextTrackCueSpan[][] arrayOfTextTrackCueSpan = this.mCue.mLines;
        int j = arrayOfTextTrackCueSpan.length;
        int i = 0;
        while (i < j)
        {
          WebVttRenderingWidget.SpanLayout localSpanLayout = new WebVttRenderingWidget.SpanLayout(getContext(), arrayOfTextTrackCueSpan[i]);
          localSpanLayout.setAlignment(localAlignment);
          localSpanLayout.setCaptionStyle(localCaptionStyle, f);
          addView(localSpanLayout, -2, -2);
          i += 1;
        }
        localAlignment = Layout.Alignment.ALIGN_LEFT;
        continue;
        localAlignment = Layout.Alignment.ALIGN_RIGHT;
      }
    }
  }
  
  private static class RegionLayout
    extends LinearLayout
  {
    private CaptioningManager.CaptionStyle mCaptionStyle;
    private float mFontSize;
    private final TextTrackRegion mRegion;
    private final ArrayList<WebVttRenderingWidget.CueLayout> mRegionCueBoxes = new ArrayList();
    
    public RegionLayout(Context paramContext, TextTrackRegion paramTextTrackRegion, CaptioningManager.CaptionStyle paramCaptionStyle, float paramFloat)
    {
      super();
      this.mRegion = paramTextTrackRegion;
      this.mCaptionStyle = paramCaptionStyle;
      this.mFontSize = paramFloat;
      setOrientation(1);
      setBackgroundColor(paramCaptionStyle.windowColor);
    }
    
    public TextTrackRegion getRegion()
    {
      return this.mRegion;
    }
    
    public void measureForParent(int paramInt1, int paramInt2)
    {
      TextTrackRegion localTextTrackRegion = this.mRegion;
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      measure(View.MeasureSpec.makeMeasureSpec((int)localTextTrackRegion.mWidth * paramInt1 / 100, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
    }
    
    public void prepForPrune()
    {
      int j = this.mRegionCueBoxes.size();
      int i = 0;
      while (i < j)
      {
        ((WebVttRenderingWidget.CueLayout)this.mRegionCueBoxes.get(i)).prepForPrune();
        i += 1;
      }
    }
    
    public boolean prune()
    {
      int j = this.mRegionCueBoxes.size();
      int i = 0;
      while (i < j)
      {
        WebVttRenderingWidget.CueLayout localCueLayout = (WebVttRenderingWidget.CueLayout)this.mRegionCueBoxes.get(i);
        int k = j;
        int m = i;
        if (!localCueLayout.isActive())
        {
          this.mRegionCueBoxes.remove(i);
          removeView(localCueLayout);
          k = j - 1;
          m = i - 1;
        }
        i = m + 1;
        j = k;
      }
      return this.mRegionCueBoxes.isEmpty();
    }
    
    public void put(TextTrackCue paramTextTrackCue)
    {
      int j = this.mRegionCueBoxes.size();
      int i = 0;
      while (i < j)
      {
        WebVttRenderingWidget.CueLayout localCueLayout = (WebVttRenderingWidget.CueLayout)this.mRegionCueBoxes.get(i);
        if (localCueLayout.getCue() == paramTextTrackCue)
        {
          localCueLayout.update();
          return;
        }
        i += 1;
      }
      paramTextTrackCue = new WebVttRenderingWidget.CueLayout(getContext(), paramTextTrackCue, this.mCaptionStyle, this.mFontSize);
      this.mRegionCueBoxes.add(paramTextTrackCue);
      addView(paramTextTrackCue, -2, -2);
      if (getChildCount() > this.mRegion.mLines) {
        removeViewAt(0);
      }
    }
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle, float paramFloat)
    {
      this.mCaptionStyle = paramCaptionStyle;
      this.mFontSize = paramFloat;
      int j = this.mRegionCueBoxes.size();
      int i = 0;
      while (i < j)
      {
        ((WebVttRenderingWidget.CueLayout)this.mRegionCueBoxes.get(i)).setCaptionStyle(paramCaptionStyle, paramFloat);
        i += 1;
      }
      setBackgroundColor(paramCaptionStyle.windowColor);
    }
  }
  
  private static class SpanLayout
    extends SubtitleView
  {
    private final SpannableStringBuilder mBuilder = new SpannableStringBuilder();
    private final TextTrackCueSpan[] mSpans;
    
    public SpanLayout(Context paramContext, TextTrackCueSpan[] paramArrayOfTextTrackCueSpan)
    {
      super();
      this.mSpans = paramArrayOfTextTrackCueSpan;
      update();
    }
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle, float paramFloat)
    {
      setBackgroundColor(paramCaptionStyle.backgroundColor);
      setForegroundColor(paramCaptionStyle.foregroundColor);
      setEdgeColor(paramCaptionStyle.edgeColor);
      setEdgeType(paramCaptionStyle.edgeType);
      setTypeface(paramCaptionStyle.getTypeface());
      setTextSize(paramFloat);
    }
    
    public void update()
    {
      SpannableStringBuilder localSpannableStringBuilder = this.mBuilder;
      TextTrackCueSpan[] arrayOfTextTrackCueSpan = this.mSpans;
      localSpannableStringBuilder.clear();
      localSpannableStringBuilder.clearSpans();
      int j = arrayOfTextTrackCueSpan.length;
      int i = 0;
      while (i < j)
      {
        if (arrayOfTextTrackCueSpan[i].mEnabled) {
          localSpannableStringBuilder.append(arrayOfTextTrackCueSpan[i].mText);
        }
        i += 1;
      }
      setText(localSpannableStringBuilder);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/WebVttRenderingWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */