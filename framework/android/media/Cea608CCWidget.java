package android.media;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

class Cea608CCWidget
  extends ClosedCaptionWidget
  implements Cea608CCParser.DisplayListener
{
  private static final String mDummyText = "1234567890123456789012345678901234";
  private static final Rect mTextBounds = new Rect();
  
  public Cea608CCWidget(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Cea608CCWidget(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public Cea608CCWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Cea608CCWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public ClosedCaptionWidget.ClosedCaptionLayout createCaptionLayout(Context paramContext)
  {
    return new CCLayout(paramContext);
  }
  
  public CaptioningManager.CaptionStyle getCaptionStyle()
  {
    return this.mCaptionStyle;
  }
  
  public void onDisplayChanged(SpannableStringBuilder[] paramArrayOfSpannableStringBuilder)
  {
    ((CCLayout)this.mClosedCaptionLayout).update(paramArrayOfSpannableStringBuilder);
    if (this.mListener != null) {
      this.mListener.onChanged(this);
    }
  }
  
  private static class CCLayout
    extends LinearLayout
    implements ClosedCaptionWidget.ClosedCaptionLayout
  {
    private static final int MAX_ROWS = 15;
    private static final float SAFE_AREA_RATIO = 0.9F;
    private final Cea608CCWidget.CCLineBox[] mLineBoxes = new Cea608CCWidget.CCLineBox[15];
    
    CCLayout(Context paramContext)
    {
      super();
      setGravity(8388611);
      setOrientation(1);
      int i = 0;
      while (i < 15)
      {
        this.mLineBoxes[i] = new Cea608CCWidget.CCLineBox(getContext());
        addView(this.mLineBoxes[i], -2, -2);
        i += 1;
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt3 -= paramInt1;
      paramInt4 -= paramInt2;
      if (paramInt3 * 3 >= paramInt4 * 4) {
        paramInt2 = paramInt4 * 4 / 3;
      }
      for (paramInt1 = paramInt4;; paramInt1 = paramInt3 * 3 / 4)
      {
        paramInt2 = (int)(paramInt2 * 0.9F);
        int i = (int)(paramInt1 * 0.9F);
        paramInt3 = (paramInt3 - paramInt2) / 2;
        paramInt4 = (paramInt4 - i) / 2;
        paramInt1 = 0;
        while (paramInt1 < 15)
        {
          this.mLineBoxes[paramInt1].layout(paramInt3, i * paramInt1 / 15 + paramInt4, paramInt3 + paramInt2, (paramInt1 + 1) * i / 15 + paramInt4);
          paramInt1 += 1;
        }
        paramInt2 = paramInt3;
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      paramInt2 = getMeasuredWidth();
      paramInt1 = getMeasuredHeight();
      if (paramInt2 * 3 >= paramInt1 * 4) {
        paramInt2 = paramInt1 * 4 / 3;
      }
      for (;;)
      {
        int i = (int)(paramInt2 * 0.9F);
        paramInt2 = View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.9F) / 15, 1073741824);
        i = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        paramInt1 = 0;
        while (paramInt1 < 15)
        {
          this.mLineBoxes[paramInt1].measure(i, paramInt2);
          paramInt1 += 1;
        }
        paramInt1 = paramInt2 * 3 / 4;
      }
    }
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      int i = 0;
      while (i < 15)
      {
        this.mLineBoxes[i].setCaptionStyle(paramCaptionStyle);
        i += 1;
      }
    }
    
    public void setFontScale(float paramFloat) {}
    
    void update(SpannableStringBuilder[] paramArrayOfSpannableStringBuilder)
    {
      int i = 0;
      if (i < 15)
      {
        if (paramArrayOfSpannableStringBuilder[i] != null)
        {
          this.mLineBoxes[i].setText(paramArrayOfSpannableStringBuilder[i], TextView.BufferType.SPANNABLE);
          this.mLineBoxes[i].setVisibility(0);
        }
        for (;;)
        {
          i += 1;
          break;
          this.mLineBoxes[i].setVisibility(4);
        }
      }
    }
  }
  
  private static class CCLineBox
    extends TextView
  {
    private static final float EDGE_OUTLINE_RATIO = 0.1F;
    private static final float EDGE_SHADOW_RATIO = 0.05F;
    private static final float FONT_PADDING_RATIO = 0.75F;
    private int mBgColor = -16777216;
    private int mEdgeColor = 0;
    private int mEdgeType = 0;
    private float mOutlineWidth;
    private float mShadowOffset;
    private float mShadowRadius;
    private int mTextColor = -1;
    
    CCLineBox(Context paramContext)
    {
      super();
      setGravity(17);
      setBackgroundColor(0);
      setTextColor(-1);
      setTypeface(Typeface.MONOSPACE);
      setVisibility(4);
      paramContext = getContext().getResources();
      this.mOutlineWidth = paramContext.getDimensionPixelSize(17105055);
      this.mShadowRadius = paramContext.getDimensionPixelSize(17105053);
      this.mShadowOffset = paramContext.getDimensionPixelSize(17105054);
    }
    
    private void drawEdgeOutline(Canvas paramCanvas)
    {
      TextPaint localTextPaint = getPaint();
      Paint.Style localStyle = localTextPaint.getStyle();
      Paint.Join localJoin = localTextPaint.getStrokeJoin();
      float f = localTextPaint.getStrokeWidth();
      setTextColor(this.mEdgeColor);
      localTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      localTextPaint.setStrokeJoin(Paint.Join.ROUND);
      localTextPaint.setStrokeWidth(this.mOutlineWidth);
      super.onDraw(paramCanvas);
      setTextColor(this.mTextColor);
      localTextPaint.setStyle(localStyle);
      localTextPaint.setStrokeJoin(localJoin);
      localTextPaint.setStrokeWidth(f);
      setBackgroundSpans(0);
      super.onDraw(paramCanvas);
      setBackgroundSpans(this.mBgColor);
    }
    
    private void drawEdgeRaisedOrDepressed(Canvas paramCanvas)
    {
      TextPaint localTextPaint = getPaint();
      Paint.Style localStyle = localTextPaint.getStyle();
      localTextPaint.setStyle(Paint.Style.FILL);
      int i;
      if (this.mEdgeType == 3)
      {
        j = 1;
        if (j == 0) {
          break label119;
        }
        i = -1;
        label39:
        if (j == 0) {
          break label127;
        }
      }
      label119:
      label127:
      for (int j = this.mEdgeColor;; j = -1)
      {
        float f = this.mShadowRadius / 2.0F;
        setShadowLayer(this.mShadowRadius, -f, -f, i);
        super.onDraw(paramCanvas);
        setBackgroundSpans(0);
        setShadowLayer(this.mShadowRadius, f, f, j);
        super.onDraw(paramCanvas);
        localTextPaint.setStyle(localStyle);
        setBackgroundSpans(this.mBgColor);
        return;
        j = 0;
        break;
        i = this.mEdgeColor;
        break label39;
      }
    }
    
    private void setBackgroundSpans(int paramInt)
    {
      Object localObject = getText();
      if ((localObject instanceof Spannable))
      {
        localObject = (Spannable)localObject;
        localObject = (Cea608CCParser.MutableBackgroundColorSpan[])((Spannable)localObject).getSpans(0, ((Spannable)localObject).length(), Cea608CCParser.MutableBackgroundColorSpan.class);
        int i = 0;
        while (i < localObject.length)
        {
          localObject[i].setBackgroundColor(paramInt);
          i += 1;
        }
      }
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if ((this.mEdgeType == -1) || (this.mEdgeType == 0)) {}
      while (this.mEdgeType == 2)
      {
        super.onDraw(paramCanvas);
        return;
      }
      if (this.mEdgeType == 1)
      {
        drawEdgeOutline(paramCanvas);
        return;
      }
      drawEdgeRaisedOrDepressed(paramCanvas);
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      float f = View.MeasureSpec.getSize(paramInt2) * 0.75F;
      setTextSize(0, f);
      this.mOutlineWidth = (0.1F * f + 1.0F);
      this.mShadowRadius = (0.05F * f + 1.0F);
      this.mShadowOffset = this.mShadowRadius;
      setScaleX(1.0F);
      getPaint().getTextBounds("1234567890123456789012345678901234", 0, "1234567890123456789012345678901234".length(), Cea608CCWidget.-get0());
      f = Cea608CCWidget.-get0().width();
      setScaleX(View.MeasureSpec.getSize(paramInt1) / f);
      super.onMeasure(paramInt1, paramInt2);
    }
    
    void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      this.mTextColor = paramCaptionStyle.foregroundColor;
      this.mBgColor = paramCaptionStyle.backgroundColor;
      this.mEdgeType = paramCaptionStyle.edgeType;
      this.mEdgeColor = paramCaptionStyle.edgeColor;
      setTextColor(this.mTextColor);
      if (this.mEdgeType == 2) {
        setShadowLayer(this.mShadowRadius, this.mShadowOffset, this.mShadowOffset, this.mEdgeColor);
      }
      for (;;)
      {
        invalidate();
        return;
        setShadowLayer(0.0F, 0.0F, 0.0F, 0);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea608CCWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */