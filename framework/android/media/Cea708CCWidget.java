package android.media;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.android.internal.widget.SubtitleView;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

class Cea708CCWidget
  extends ClosedCaptionWidget
  implements Cea708CCParser.DisplayListener
{
  private final CCHandler mCCHandler = new CCHandler((CCLayout)this.mClosedCaptionLayout);
  
  public Cea708CCWidget(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Cea708CCWidget(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public Cea708CCWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Cea708CCWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public ClosedCaptionWidget.ClosedCaptionLayout createCaptionLayout(Context paramContext)
  {
    return new CCLayout(paramContext);
  }
  
  public void emitEvent(Cea708CCParser.CaptionEvent paramCaptionEvent)
  {
    this.mCCHandler.processCaptionEvent(paramCaptionEvent);
    setSize(getWidth(), getHeight());
    if (this.mListener != null) {
      this.mListener.onChanged(this);
    }
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    ((ViewGroup)this.mClosedCaptionLayout).draw(paramCanvas);
  }
  
  static class CCHandler
    implements Handler.Callback
  {
    private static final int CAPTION_ALL_WINDOWS_BITMAP = 255;
    private static final long CAPTION_CLEAR_INTERVAL_MS = 60000L;
    private static final int CAPTION_WINDOWS_MAX = 8;
    private static final boolean DEBUG = false;
    private static final int MSG_CAPTION_CLEAR = 2;
    private static final int MSG_DELAY_CANCEL = 1;
    private static final String TAG = "CCHandler";
    private static final int TENTHS_OF_SECOND_IN_MILLIS = 100;
    private final Cea708CCWidget.CCLayout mCCLayout;
    private final Cea708CCWidget.CCWindowLayout[] mCaptionWindowLayouts = new Cea708CCWidget.CCWindowLayout[8];
    private Cea708CCWidget.CCWindowLayout mCurrentWindowLayout;
    private final Handler mHandler;
    private boolean mIsDelayed = false;
    private final ArrayList<Cea708CCParser.CaptionEvent> mPendingCaptionEvents = new ArrayList();
    
    public CCHandler(Cea708CCWidget.CCLayout paramCCLayout)
    {
      this.mCCLayout = paramCCLayout;
      this.mHandler = new Handler(this);
    }
    
    private void clearWindows(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      Iterator localIterator = getWindowsFromBitmap(paramInt).iterator();
      while (localIterator.hasNext()) {
        ((Cea708CCWidget.CCWindowLayout)localIterator.next()).clear();
      }
    }
    
    private void defineWindow(Cea708CCParser.CaptionWindow paramCaptionWindow)
    {
      if (paramCaptionWindow == null) {
        return;
      }
      int i = paramCaptionWindow.id;
      if ((i < 0) || (i >= this.mCaptionWindowLayouts.length)) {
        return;
      }
      Cea708CCWidget.CCWindowLayout localCCWindowLayout2 = this.mCaptionWindowLayouts[i];
      Cea708CCWidget.CCWindowLayout localCCWindowLayout1 = localCCWindowLayout2;
      if (localCCWindowLayout2 == null) {
        localCCWindowLayout1 = new Cea708CCWidget.CCWindowLayout(this.mCCLayout.getContext());
      }
      localCCWindowLayout1.initWindow(this.mCCLayout, paramCaptionWindow);
      this.mCaptionWindowLayouts[i] = localCCWindowLayout1;
      this.mCurrentWindowLayout = localCCWindowLayout1;
    }
    
    private void delay(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 255)) {
        return;
      }
      this.mIsDelayed = true;
      this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), paramInt * 100);
    }
    
    private void delayCancel()
    {
      this.mIsDelayed = false;
      processPendingBuffer();
    }
    
    private void deleteWindows(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      Iterator localIterator = getWindowsFromBitmap(paramInt).iterator();
      while (localIterator.hasNext())
      {
        Cea708CCWidget.CCWindowLayout localCCWindowLayout = (Cea708CCWidget.CCWindowLayout)localIterator.next();
        localCCWindowLayout.removeFromCaptionView();
        this.mCaptionWindowLayouts[localCCWindowLayout.getCaptionWindowId()] = null;
      }
    }
    
    private void displayWindows(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      Iterator localIterator = getWindowsFromBitmap(paramInt).iterator();
      while (localIterator.hasNext()) {
        ((Cea708CCWidget.CCWindowLayout)localIterator.next()).show();
      }
    }
    
    private ArrayList<Cea708CCWidget.CCWindowLayout> getWindowsFromBitmap(int paramInt)
    {
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      while (i < 8)
      {
        if ((1 << i & paramInt) != 0)
        {
          Cea708CCWidget.CCWindowLayout localCCWindowLayout = this.mCaptionWindowLayouts[i];
          if (localCCWindowLayout != null) {
            localArrayList.add(localCCWindowLayout);
          }
        }
        i += 1;
      }
      return localArrayList;
    }
    
    private void hideWindows(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      Iterator localIterator = getWindowsFromBitmap(paramInt).iterator();
      while (localIterator.hasNext()) {
        ((Cea708CCWidget.CCWindowLayout)localIterator.next()).hide();
      }
    }
    
    private void processPendingBuffer()
    {
      Iterator localIterator = this.mPendingCaptionEvents.iterator();
      while (localIterator.hasNext()) {
        processCaptionEvent((Cea708CCParser.CaptionEvent)localIterator.next());
      }
      this.mPendingCaptionEvents.clear();
    }
    
    private void sendBufferToCurrentWindow(String paramString)
    {
      if (this.mCurrentWindowLayout != null)
      {
        this.mCurrentWindowLayout.sendBuffer(paramString);
        this.mHandler.removeMessages(2);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), 60000L);
      }
    }
    
    private void sendControlToCurrentWindow(char paramChar)
    {
      if (this.mCurrentWindowLayout != null) {
        this.mCurrentWindowLayout.sendControl(paramChar);
      }
    }
    
    private void setCurrentWindowLayout(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.mCaptionWindowLayouts.length)) {
        return;
      }
      Cea708CCWidget.CCWindowLayout localCCWindowLayout = this.mCaptionWindowLayouts[paramInt];
      if (localCCWindowLayout == null) {
        return;
      }
      this.mCurrentWindowLayout = localCCWindowLayout;
    }
    
    private void setPenAttr(Cea708CCParser.CaptionPenAttr paramCaptionPenAttr)
    {
      if (this.mCurrentWindowLayout != null) {
        this.mCurrentWindowLayout.setPenAttr(paramCaptionPenAttr);
      }
    }
    
    private void setPenColor(Cea708CCParser.CaptionPenColor paramCaptionPenColor)
    {
      if (this.mCurrentWindowLayout != null) {
        this.mCurrentWindowLayout.setPenColor(paramCaptionPenColor);
      }
    }
    
    private void setPenLocation(Cea708CCParser.CaptionPenLocation paramCaptionPenLocation)
    {
      if (this.mCurrentWindowLayout != null) {
        this.mCurrentWindowLayout.setPenLocation(paramCaptionPenLocation.row, paramCaptionPenLocation.column);
      }
    }
    
    private void setWindowAttr(Cea708CCParser.CaptionWindowAttr paramCaptionWindowAttr)
    {
      if (this.mCurrentWindowLayout != null) {
        this.mCurrentWindowLayout.setWindowAttr(paramCaptionWindowAttr);
      }
    }
    
    private void toggleWindows(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      Iterator localIterator = getWindowsFromBitmap(paramInt).iterator();
      while (localIterator.hasNext())
      {
        Cea708CCWidget.CCWindowLayout localCCWindowLayout = (Cea708CCWidget.CCWindowLayout)localIterator.next();
        if (localCCWindowLayout.isShown()) {
          localCCWindowLayout.hide();
        } else {
          localCCWindowLayout.show();
        }
      }
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 1: 
        delayCancel();
        return true;
      }
      clearWindows(255);
      return true;
    }
    
    public void processCaptionEvent(Cea708CCParser.CaptionEvent paramCaptionEvent)
    {
      if (this.mIsDelayed)
      {
        this.mPendingCaptionEvents.add(paramCaptionEvent);
        return;
      }
      switch (paramCaptionEvent.type)
      {
      default: 
        return;
      case 1: 
        sendBufferToCurrentWindow((String)paramCaptionEvent.obj);
        return;
      case 2: 
        sendControlToCurrentWindow(((Character)paramCaptionEvent.obj).charValue());
        return;
      case 3: 
        setCurrentWindowLayout(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 4: 
        clearWindows(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 5: 
        displayWindows(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 6: 
        hideWindows(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 7: 
        toggleWindows(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 8: 
        deleteWindows(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 9: 
        delay(((Integer)paramCaptionEvent.obj).intValue());
        return;
      case 10: 
        delayCancel();
        return;
      case 11: 
        reset();
        return;
      case 12: 
        setPenAttr((Cea708CCParser.CaptionPenAttr)paramCaptionEvent.obj);
        return;
      case 13: 
        setPenColor((Cea708CCParser.CaptionPenColor)paramCaptionEvent.obj);
        return;
      case 14: 
        setPenLocation((Cea708CCParser.CaptionPenLocation)paramCaptionEvent.obj);
        return;
      case 15: 
        setWindowAttr((Cea708CCParser.CaptionWindowAttr)paramCaptionEvent.obj);
        return;
      }
      defineWindow((Cea708CCParser.CaptionWindow)paramCaptionEvent.obj);
    }
    
    public void reset()
    {
      this.mCurrentWindowLayout = null;
      this.mIsDelayed = false;
      this.mPendingCaptionEvents.clear();
      int i = 0;
      while (i < 8)
      {
        if (this.mCaptionWindowLayouts[i] != null) {
          this.mCaptionWindowLayouts[i].removeFromCaptionView();
        }
        this.mCaptionWindowLayouts[i] = null;
        i += 1;
      }
      this.mCCLayout.setVisibility(4);
      this.mHandler.removeMessages(2);
    }
  }
  
  static class CCLayout
    extends Cea708CCWidget.ScaledLayout
    implements ClosedCaptionWidget.ClosedCaptionLayout
  {
    private static final float SAFE_TITLE_AREA_SCALE_END_X = 0.9F;
    private static final float SAFE_TITLE_AREA_SCALE_END_Y = 0.9F;
    private static final float SAFE_TITLE_AREA_SCALE_START_X = 0.1F;
    private static final float SAFE_TITLE_AREA_SCALE_START_Y = 0.1F;
    private final Cea708CCWidget.ScaledLayout mSafeTitleAreaLayout;
    
    public CCLayout(Context paramContext)
    {
      super();
      this.mSafeTitleAreaLayout = new Cea708CCWidget.ScaledLayout(paramContext);
      addView(this.mSafeTitleAreaLayout, new Cea708CCWidget.ScaledLayout.ScaledLayoutParams(0.1F, 0.9F, 0.1F, 0.9F));
    }
    
    public void addOrUpdateViewToSafeTitleArea(Cea708CCWidget.CCWindowLayout paramCCWindowLayout, Cea708CCWidget.ScaledLayout.ScaledLayoutParams paramScaledLayoutParams)
    {
      if (this.mSafeTitleAreaLayout.indexOfChild(paramCCWindowLayout) < 0)
      {
        this.mSafeTitleAreaLayout.addView(paramCCWindowLayout, paramScaledLayoutParams);
        return;
      }
      this.mSafeTitleAreaLayout.updateViewLayout(paramCCWindowLayout, paramScaledLayoutParams);
    }
    
    public void removeViewFromSafeTitleArea(Cea708CCWidget.CCWindowLayout paramCCWindowLayout)
    {
      this.mSafeTitleAreaLayout.removeView(paramCCWindowLayout);
    }
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      int j = this.mSafeTitleAreaLayout.getChildCount();
      int i = 0;
      while (i < j)
      {
        ((Cea708CCWidget.CCWindowLayout)this.mSafeTitleAreaLayout.getChildAt(i)).setCaptionStyle(paramCaptionStyle);
        i += 1;
      }
    }
    
    public void setFontScale(float paramFloat)
    {
      int j = this.mSafeTitleAreaLayout.getChildCount();
      int i = 0;
      while (i < j)
      {
        ((Cea708CCWidget.CCWindowLayout)this.mSafeTitleAreaLayout.getChildAt(i)).setFontScale(paramFloat);
        i += 1;
      }
    }
  }
  
  static class CCView
    extends SubtitleView
  {
    private static final CaptioningManager.CaptionStyle DEFAULT_CAPTION_STYLE = CaptioningManager.CaptionStyle.DEFAULT;
    
    public CCView(Context paramContext)
    {
      this(paramContext, null);
    }
    
    public CCView(Context paramContext, AttributeSet paramAttributeSet)
    {
      this(paramContext, paramAttributeSet, 0);
    }
    
    public CCView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      this(paramContext, paramAttributeSet, paramInt, 0);
    }
    
    public CCView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
    }
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      if (paramCaptionStyle.hasForegroundColor())
      {
        i = paramCaptionStyle.foregroundColor;
        setForegroundColor(i);
        if (!paramCaptionStyle.hasBackgroundColor()) {
          break label87;
        }
        i = paramCaptionStyle.backgroundColor;
        label29:
        setBackgroundColor(i);
        if (!paramCaptionStyle.hasEdgeType()) {
          break label97;
        }
        i = paramCaptionStyle.edgeType;
        label46:
        setEdgeType(i);
        if (!paramCaptionStyle.hasEdgeColor()) {
          break label107;
        }
      }
      label87:
      label97:
      label107:
      for (int i = paramCaptionStyle.edgeColor;; i = DEFAULT_CAPTION_STYLE.edgeColor)
      {
        setEdgeColor(i);
        setTypeface(paramCaptionStyle.getTypeface());
        return;
        i = DEFAULT_CAPTION_STYLE.foregroundColor;
        break;
        i = DEFAULT_CAPTION_STYLE.backgroundColor;
        break label29;
        i = DEFAULT_CAPTION_STYLE.edgeType;
        break label46;
      }
    }
  }
  
  static class CCWindowLayout
    extends RelativeLayout
    implements View.OnLayoutChangeListener
  {
    private static final int ANCHOR_HORIZONTAL_16_9_MAX = 209;
    private static final int ANCHOR_HORIZONTAL_MODE_CENTER = 1;
    private static final int ANCHOR_HORIZONTAL_MODE_LEFT = 0;
    private static final int ANCHOR_HORIZONTAL_MODE_RIGHT = 2;
    private static final int ANCHOR_MODE_DIVIDER = 3;
    private static final int ANCHOR_RELATIVE_POSITIONING_MAX = 99;
    private static final int ANCHOR_VERTICAL_MAX = 74;
    private static final int ANCHOR_VERTICAL_MODE_BOTTOM = 2;
    private static final int ANCHOR_VERTICAL_MODE_CENTER = 1;
    private static final int ANCHOR_VERTICAL_MODE_TOP = 0;
    private static final int MAX_COLUMN_COUNT_16_9 = 42;
    private static final float PROPORTION_PEN_SIZE_LARGE = 1.25F;
    private static final float PROPORTION_PEN_SIZE_SMALL = 0.75F;
    private static final String TAG = "CCWindowLayout";
    private final SpannableStringBuilder mBuilder = new SpannableStringBuilder();
    private Cea708CCWidget.CCLayout mCCLayout;
    private Cea708CCWidget.CCView mCCView;
    private CaptioningManager.CaptionStyle mCaptionStyle;
    private int mCaptionWindowId;
    private final List<CharacterStyle> mCharacterStyles = new ArrayList();
    private float mFontScale;
    private int mLastCaptionLayoutHeight;
    private int mLastCaptionLayoutWidth;
    private int mRow = -1;
    private int mRowLimit = 0;
    private float mTextSize;
    private String mWidestChar;
    
    public CCWindowLayout(Context paramContext)
    {
      this(paramContext, null);
    }
    
    public CCWindowLayout(Context paramContext, AttributeSet paramAttributeSet)
    {
      this(paramContext, paramAttributeSet, 0);
    }
    
    public CCWindowLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      this(paramContext, paramAttributeSet, paramInt, 0);
    }
    
    public CCWindowLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
      this.mCCView = new Cea708CCWidget.CCView(paramContext);
      paramAttributeSet = new RelativeLayout.LayoutParams(-2, -2);
      addView(this.mCCView, paramAttributeSet);
      paramContext = (CaptioningManager)paramContext.getSystemService("captioning");
      this.mFontScale = paramContext.getFontScale();
      setCaptionStyle(paramContext.getUserStyle());
      this.mCCView.setText("");
      updateWidestChar();
    }
    
    private int getScreenColumnCount()
    {
      return 42;
    }
    
    private void updateText(String paramString, boolean paramBoolean)
    {
      if (!paramBoolean) {
        this.mBuilder.clear();
      }
      if ((paramString != null) && (paramString.length() > 0))
      {
        i = this.mBuilder.length();
        this.mBuilder.append(paramString);
        paramString = this.mCharacterStyles.iterator();
        while (paramString.hasNext())
        {
          CharacterStyle localCharacterStyle = (CharacterStyle)paramString.next();
          this.mBuilder.setSpan(localCharacterStyle, i, this.mBuilder.length(), 33);
        }
      }
      paramString = TextUtils.split(this.mBuilder.toString(), "\n");
      paramString = TextUtils.join("\n", Arrays.copyOfRange(paramString, Math.max(0, paramString.length - (this.mRowLimit + 1)), paramString.length));
      this.mBuilder.delete(0, this.mBuilder.length() - paramString.length());
      int j = 0;
      int m = this.mBuilder.length() - 1;
      int i = m;
      int k;
      for (;;)
      {
        k = i;
        if (j > m) {
          break;
        }
        k = i;
        if (this.mBuilder.charAt(j) > ' ') {
          break;
        }
        j += 1;
      }
      while ((k >= j) && (this.mBuilder.charAt(k) <= ' ')) {
        k -= 1;
      }
      if ((j == 0) && (k == m))
      {
        this.mCCView.setText(this.mBuilder);
        return;
      }
      paramString = new SpannableStringBuilder();
      paramString.append(this.mBuilder);
      if (k < m) {
        paramString.delete(k + 1, m + 1);
      }
      if (j > 0) {
        paramString.delete(0, j);
      }
      this.mCCView.setText(paramString);
    }
    
    private void updateTextSize()
    {
      if (this.mCCLayout == null) {
        return;
      }
      Object localObject = new StringBuilder();
      int j = getScreenColumnCount();
      int i = 0;
      while (i < j)
      {
        ((StringBuilder)localObject).append(this.mWidestChar);
        i += 1;
      }
      localObject = ((StringBuilder)localObject).toString();
      Paint localPaint = new Paint();
      localPaint.setTypeface(this.mCaptionStyle.getTypeface());
      float f1 = 0.0F;
      float f2 = 255.0F;
      while (f1 < f2)
      {
        float f3 = (f1 + f2) / 2.0F;
        localPaint.setTextSize(f3);
        float f4 = localPaint.measureText((String)localObject);
        if (this.mCCLayout.getWidth() * 0.8F > f4) {
          f1 = f3 + 0.01F;
        } else {
          f2 = f3 - 0.01F;
        }
      }
      this.mTextSize = (this.mFontScale * f2);
      this.mCCView.setTextSize(this.mTextSize);
    }
    
    private void updateWidestChar()
    {
      Paint localPaint = new Paint();
      localPaint.setTypeface(this.mCaptionStyle.getTypeface());
      Charset localCharset = Charset.forName("ISO-8859-1");
      float f1 = 0.0F;
      int i = 0;
      while (i < 256)
      {
        String str = new String(new byte[] { (byte)i }, localCharset);
        float f3 = localPaint.measureText(str);
        float f2 = f1;
        if (f1 < f3)
        {
          f2 = f3;
          this.mWidestChar = str;
        }
        i += 1;
        f1 = f2;
      }
      updateTextSize();
    }
    
    public void appendText(String paramString)
    {
      updateText(paramString, true);
    }
    
    public void clear()
    {
      clearText();
      hide();
    }
    
    public void clearText()
    {
      this.mBuilder.clear();
      this.mCCView.setText("");
    }
    
    public int getCaptionWindowId()
    {
      return this.mCaptionWindowId;
    }
    
    public void hide()
    {
      setVisibility(4);
      requestLayout();
    }
    
    public void initWindow(Cea708CCWidget.CCLayout paramCCLayout, Cea708CCParser.CaptionWindow paramCaptionWindow)
    {
      if (this.mCCLayout != paramCCLayout)
      {
        if (this.mCCLayout != null) {
          this.mCCLayout.removeOnLayoutChangeListener(this);
        }
        this.mCCLayout = paramCCLayout;
        this.mCCLayout.addOnLayoutChangeListener(this);
        updateWidestChar();
      }
      float f1 = paramCaptionWindow.anchorVertical;
      int i;
      float f2;
      label81:
      float f3;
      int k;
      float f4;
      float f5;
      float f6;
      if (paramCaptionWindow.relativePositioning)
      {
        i = 99;
        f2 = f1 / i;
        f1 = paramCaptionWindow.anchorHorizontal;
        if (!paramCaptionWindow.relativePositioning) {
          break label358;
        }
        i = 99;
        f3 = f1 / i;
        if (f2 >= 0.0F)
        {
          f1 = f2;
          if (f2 <= 1.0F) {}
        }
        else
        {
          Log.i("CCWindowLayout", "The vertical position of the anchor point should be at the range of 0 and 1 but " + f2);
          f1 = Math.max(0.0F, Math.min(f2, 1.0F));
        }
        if (f3 >= 0.0F)
        {
          f2 = f3;
          if (f3 <= 1.0F) {}
        }
        else
        {
          Log.i("CCWindowLayout", "The horizontal position of the anchor point should be at the range of 0 and 1 but " + f3);
          f2 = Math.max(0.0F, Math.min(f3, 1.0F));
        }
        i = 17;
        k = paramCaptionWindow.anchorId;
        int j = paramCaptionWindow.anchorId / 3;
        f4 = 0.0F;
        f5 = 1.0F;
        f3 = 0.0F;
        f6 = 1.0F;
        switch (k % 3)
        {
        default: 
          f2 = f6;
          switch (j)
          {
          default: 
            label264:
            f1 = f5;
          }
          break;
        }
      }
      for (;;)
      {
        this.mCCLayout.addOrUpdateViewToSafeTitleArea(this, new Cea708CCWidget.ScaledLayout.ScaledLayoutParams(f4, f1, f3, f2));
        setCaptionWindowId(paramCaptionWindow.id);
        setRowLimit(paramCaptionWindow.rowCount);
        setGravity(i);
        if (!paramCaptionWindow.visible) {
          break label676;
        }
        show();
        return;
        i = 74;
        break;
        label358:
        i = 209;
        break label81;
        i = 3;
        this.mCCView.setAlignment(Layout.Alignment.ALIGN_NORMAL);
        f3 = f2;
        f2 = f6;
        break label264;
        f6 = Math.min(1.0F - f2, f2);
        i = paramCaptionWindow.columnCount;
        k = Math.min(getScreenColumnCount(), i + 1);
        paramCCLayout = new StringBuilder();
        i = 0;
        while (i < k)
        {
          paramCCLayout.append(this.mWidestChar);
          i += 1;
        }
        Paint localPaint = new Paint();
        localPaint.setTypeface(this.mCaptionStyle.getTypeface());
        localPaint.setTextSize(this.mTextSize);
        f3 = localPaint.measureText(paramCCLayout.toString());
        if (this.mCCLayout.getWidth() > 0) {}
        for (f3 = f3 / 2.0F / (this.mCCLayout.getWidth() * 0.8F);; f3 = 0.0F)
        {
          if ((f3 <= 0.0F) || (f3 >= f2)) {
            break label574;
          }
          i = 3;
          this.mCCView.setAlignment(Layout.Alignment.ALIGN_NORMAL);
          f3 = f2 - f3;
          f2 = 1.0F;
          break;
        }
        label574:
        i = 1;
        this.mCCView.setAlignment(Layout.Alignment.ALIGN_CENTER);
        f3 = f2 - f6;
        f2 += f6;
        break label264;
        i = 5;
        this.mCCView.setAlignment(Layout.Alignment.ALIGN_RIGHT);
        break label264;
        i |= 0x30;
        f4 = f1;
        f1 = f5;
        continue;
        i |= 0x10;
        f5 = Math.min(1.0F - f1, f1);
        f4 = f1 - f5;
        f1 += f5;
        continue;
        i |= 0x50;
      }
      label676:
      hide();
    }
    
    public void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
    {
      paramInt1 = paramInt3 - paramInt1;
      paramInt2 = paramInt4 - paramInt2;
      if ((paramInt1 != this.mLastCaptionLayoutWidth) || (paramInt2 != this.mLastCaptionLayoutHeight))
      {
        this.mLastCaptionLayoutWidth = paramInt1;
        this.mLastCaptionLayoutHeight = paramInt2;
        updateTextSize();
      }
    }
    
    public void removeFromCaptionView()
    {
      if (this.mCCLayout != null)
      {
        this.mCCLayout.removeViewFromSafeTitleArea(this);
        this.mCCLayout.removeOnLayoutChangeListener(this);
        this.mCCLayout = null;
      }
    }
    
    public void sendBuffer(String paramString)
    {
      appendText(paramString);
    }
    
    public void sendControl(char paramChar) {}
    
    public void setCaptionStyle(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      this.mCaptionStyle = paramCaptionStyle;
      this.mCCView.setCaptionStyle(paramCaptionStyle);
    }
    
    public void setCaptionWindowId(int paramInt)
    {
      this.mCaptionWindowId = paramInt;
    }
    
    public void setFontScale(float paramFloat)
    {
      this.mFontScale = paramFloat;
      updateTextSize();
    }
    
    public void setPenAttr(Cea708CCParser.CaptionPenAttr paramCaptionPenAttr)
    {
      this.mCharacterStyles.clear();
      if (paramCaptionPenAttr.italic) {
        this.mCharacterStyles.add(new StyleSpan(2));
      }
      if (paramCaptionPenAttr.underline) {
        this.mCharacterStyles.add(new UnderlineSpan());
      }
      switch (paramCaptionPenAttr.penSize)
      {
      }
      for (;;)
      {
        switch (paramCaptionPenAttr.penOffset)
        {
        case 1: 
        default: 
          return;
          this.mCharacterStyles.add(new RelativeSizeSpan(0.75F));
          continue;
          this.mCharacterStyles.add(new RelativeSizeSpan(1.25F));
        }
      }
      this.mCharacterStyles.add(new SubscriptSpan());
      return;
      this.mCharacterStyles.add(new SuperscriptSpan());
    }
    
    public void setPenColor(Cea708CCParser.CaptionPenColor paramCaptionPenColor) {}
    
    public void setPenLocation(int paramInt1, int paramInt2)
    {
      if (this.mRow >= 0)
      {
        paramInt2 = this.mRow;
        while (paramInt2 < paramInt1)
        {
          appendText("\n");
          paramInt2 += 1;
        }
      }
      this.mRow = paramInt1;
    }
    
    public void setRowLimit(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("A rowLimit should have a positive number");
      }
      this.mRowLimit = paramInt;
    }
    
    public void setText(String paramString)
    {
      updateText(paramString, false);
    }
    
    public void setWindowAttr(Cea708CCParser.CaptionWindowAttr paramCaptionWindowAttr) {}
    
    public void show()
    {
      setVisibility(0);
      requestLayout();
    }
  }
  
  static class ScaledLayout
    extends ViewGroup
  {
    private static final boolean DEBUG = false;
    private static final String TAG = "ScaledLayout";
    private static final Comparator<Rect> mRectTopLeftSorter = new Comparator()
    {
      public int compare(Rect paramAnonymousRect1, Rect paramAnonymousRect2)
      {
        if (paramAnonymousRect1.top != paramAnonymousRect2.top) {
          return paramAnonymousRect1.top - paramAnonymousRect2.top;
        }
        return paramAnonymousRect1.left - paramAnonymousRect2.left;
      }
    };
    private Rect[] mRectArray;
    
    public ScaledLayout(Context paramContext)
    {
      super();
    }
    
    protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      return paramLayoutParams instanceof ScaledLayoutParams;
    }
    
    public void dispatchDraw(Canvas paramCanvas)
    {
      int j = getPaddingLeft();
      int k = getPaddingTop();
      int m = getChildCount();
      int i = 0;
      for (;;)
      {
        View localView;
        if (i < m)
        {
          localView = getChildAt(i);
          if (localView.getVisibility() == 8) {
            break label107;
          }
          if (i < this.mRectArray.length) {}
        }
        else
        {
          return;
        }
        int n = this.mRectArray[i].left;
        int i1 = this.mRectArray[i].top;
        int i2 = paramCanvas.save();
        paramCanvas.translate(j + n, k + i1);
        localView.draw(paramCanvas);
        paramCanvas.restoreToCount(i2);
        label107:
        i += 1;
      }
    }
    
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
    {
      return new ScaledLayoutParams(getContext(), paramAttributeSet);
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt2 = getPaddingLeft();
      paramInt3 = getPaddingTop();
      paramInt4 = getChildCount();
      paramInt1 = 0;
      while (paramInt1 < paramInt4)
      {
        View localView = getChildAt(paramInt1);
        if (localView.getVisibility() != 8)
        {
          int i = this.mRectArray[paramInt1].left;
          int j = this.mRectArray[paramInt1].top;
          int k = this.mRectArray[paramInt1].bottom;
          localView.layout(paramInt2 + i, paramInt3 + j, paramInt3 + this.mRectArray[paramInt1].right, paramInt2 + k);
        }
        paramInt1 += 1;
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int j = View.MeasureSpec.getSize(paramInt1);
      int k = View.MeasureSpec.getSize(paramInt2);
      paramInt2 = j - getPaddingLeft() - getPaddingRight();
      int m = k - getPaddingTop() - getPaddingBottom();
      int n = getChildCount();
      this.mRectArray = new Rect[n];
      paramInt1 = 0;
      int i;
      while (paramInt1 < n)
      {
        localObject1 = getChildAt(paramInt1);
        localObject2 = ((View)localObject1).getLayoutParams();
        if (!(localObject2 instanceof ScaledLayoutParams)) {
          throw new RuntimeException("A child of ScaledLayout cannot have the UNSPECIFIED scale factors");
        }
        float f1 = ((ScaledLayoutParams)localObject2).scaleStartRow;
        float f2 = ((ScaledLayoutParams)localObject2).scaleEndRow;
        float f3 = ((ScaledLayoutParams)localObject2).scaleStartCol;
        float f4 = ((ScaledLayoutParams)localObject2).scaleEndCol;
        if ((f1 < 0.0F) || (f1 > 1.0F)) {
          throw new RuntimeException("A child of ScaledLayout should have a range of scaleStartRow between 0 and 1");
        }
        if ((f2 < f1) || (f1 > 1.0F)) {
          throw new RuntimeException("A child of ScaledLayout should have a range of scaleEndRow between scaleStartRow and 1");
        }
        if ((f4 < 0.0F) || (f4 > 1.0F)) {
          throw new RuntimeException("A child of ScaledLayout should have a range of scaleStartCol between 0 and 1");
        }
        if ((f4 < f3) || (f4 > 1.0F)) {
          throw new RuntimeException("A child of ScaledLayout should have a range of scaleEndCol between scaleStartCol and 1");
        }
        this.mRectArray[paramInt1] = new Rect((int)(paramInt2 * f3), (int)(m * f1), (int)(paramInt2 * f4), (int)(m * f2));
        i = View.MeasureSpec.makeMeasureSpec((int)(paramInt2 * (f4 - f3)), 1073741824);
        ((View)localObject1).measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
        if (((View)localObject1).getMeasuredHeight() > this.mRectArray[paramInt1].height())
        {
          int i1 = (((View)localObject1).getMeasuredHeight() - this.mRectArray[paramInt1].height() + 1) / 2;
          localObject2 = this.mRectArray[paramInt1];
          ((Rect)localObject2).bottom += i1;
          localObject2 = this.mRectArray[paramInt1];
          ((Rect)localObject2).top -= i1;
          if (this.mRectArray[paramInt1].top < 0)
          {
            localObject2 = this.mRectArray[paramInt1];
            ((Rect)localObject2).bottom -= this.mRectArray[paramInt1].top;
            this.mRectArray[paramInt1].top = 0;
          }
          if (this.mRectArray[paramInt1].bottom > m)
          {
            localObject2 = this.mRectArray[paramInt1];
            ((Rect)localObject2).top -= this.mRectArray[paramInt1].bottom - m;
            this.mRectArray[paramInt1].bottom = m;
          }
        }
        ((View)localObject1).measure(i, View.MeasureSpec.makeMeasureSpec((int)(m * (f2 - f1)), 1073741824));
        paramInt1 += 1;
      }
      paramInt1 = 0;
      Object localObject1 = new int[n];
      Object localObject2 = new Rect[n];
      paramInt2 = 0;
      while (paramInt2 < n)
      {
        i = paramInt1;
        if (getChildAt(paramInt2).getVisibility() == 0)
        {
          localObject1[paramInt1] = paramInt1;
          localObject2[paramInt1] = this.mRectArray[paramInt2];
          i = paramInt1 + 1;
        }
        paramInt2 += 1;
        paramInt1 = i;
      }
      Arrays.sort((Object[])localObject2, 0, paramInt1, mRectTopLeftSorter);
      paramInt2 = 0;
      while (paramInt2 < paramInt1 - 1)
      {
        i = paramInt2 + 1;
        while (i < paramInt1)
        {
          if (Rect.intersects(localObject2[paramInt2], localObject2[i]))
          {
            localObject1[i] = localObject1[paramInt2];
            localObject2[i].set(localObject2[i].left, localObject2[paramInt2].bottom, localObject2[i].right, localObject2[paramInt2].bottom + localObject2[i].height());
          }
          i += 1;
        }
        paramInt2 += 1;
      }
      paramInt1 -= 1;
      while (paramInt1 >= 0)
      {
        if (localObject2[paramInt1].bottom > m)
        {
          i = localObject2[paramInt1].bottom - m;
          paramInt2 = 0;
          while (paramInt2 <= paramInt1)
          {
            if (localObject1[paramInt1] == localObject1[paramInt2]) {
              localObject2[paramInt2].set(localObject2[paramInt2].left, localObject2[paramInt2].top - i, localObject2[paramInt2].right, localObject2[paramInt2].bottom - i);
            }
            paramInt2 += 1;
          }
        }
        paramInt1 -= 1;
      }
      setMeasuredDimension(j, k);
    }
    
    static class ScaledLayoutParams
      extends ViewGroup.LayoutParams
    {
      public static final float SCALE_UNSPECIFIED = -1.0F;
      public float scaleEndCol;
      public float scaleEndRow;
      public float scaleStartCol;
      public float scaleStartRow;
      
      public ScaledLayoutParams(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
      {
        super(-1);
        this.scaleStartRow = paramFloat1;
        this.scaleEndRow = paramFloat2;
        this.scaleStartCol = paramFloat3;
        this.scaleEndCol = paramFloat4;
      }
      
      public ScaledLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
      {
        super(-1);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea708CCWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */