package android.inputmethodservice;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.xmlpull.v1.XmlPullParserException;

public class Keyboard
{
  public static final int EDGE_BOTTOM = 8;
  public static final int EDGE_LEFT = 1;
  public static final int EDGE_RIGHT = 2;
  public static final int EDGE_TOP = 4;
  private static final int GRID_HEIGHT = 5;
  private static final int GRID_SIZE = 50;
  private static final int GRID_WIDTH = 10;
  public static final int KEYCODE_ALT = -6;
  public static final int KEYCODE_CANCEL = -3;
  public static final int KEYCODE_DELETE = -5;
  public static final int KEYCODE_DONE = -4;
  public static final int KEYCODE_MODE_CHANGE = -2;
  public static final int KEYCODE_SHIFT = -1;
  private static float SEARCH_DISTANCE = 1.8F;
  static final String TAG = "Keyboard";
  private static final String TAG_KEY = "Key";
  private static final String TAG_KEYBOARD = "Keyboard";
  private static final String TAG_ROW = "Row";
  private int mCellHeight;
  private int mCellWidth;
  private int mDefaultHeight;
  private int mDefaultHorizontalGap;
  private int mDefaultVerticalGap;
  private int mDefaultWidth;
  private int mDisplayHeight;
  private int mDisplayWidth;
  private int[][] mGridNeighbors;
  private int mKeyHeight;
  private int mKeyWidth;
  private int mKeyboardMode;
  private List<Key> mKeys;
  private CharSequence mLabel;
  private List<Key> mModifierKeys;
  private int mProximityThreshold;
  private int[] mShiftKeyIndices = { -1, -1 };
  private Key[] mShiftKeys = { null, null };
  private boolean mShifted;
  private int mTotalHeight;
  private int mTotalWidth;
  private ArrayList<Row> rows = new ArrayList();
  
  public Keyboard(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, 0);
  }
  
  public Keyboard(Context paramContext, int paramInt1, int paramInt2)
  {
    DisplayMetrics localDisplayMetrics = paramContext.getResources().getDisplayMetrics();
    this.mDisplayWidth = localDisplayMetrics.widthPixels;
    this.mDisplayHeight = localDisplayMetrics.heightPixels;
    this.mDefaultHorizontalGap = 0;
    this.mDefaultWidth = (this.mDisplayWidth / 10);
    this.mDefaultVerticalGap = 0;
    this.mDefaultHeight = this.mDefaultWidth;
    this.mKeys = new ArrayList();
    this.mModifierKeys = new ArrayList();
    this.mKeyboardMode = paramInt2;
    loadKeyboard(paramContext, paramContext.getResources().getXml(paramInt1));
  }
  
  public Keyboard(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mDisplayWidth = paramInt3;
    this.mDisplayHeight = paramInt4;
    this.mDefaultHorizontalGap = 0;
    this.mDefaultWidth = (this.mDisplayWidth / 10);
    this.mDefaultVerticalGap = 0;
    this.mDefaultHeight = this.mDefaultWidth;
    this.mKeys = new ArrayList();
    this.mModifierKeys = new ArrayList();
    this.mKeyboardMode = paramInt2;
    loadKeyboard(paramContext, paramContext.getResources().getXml(paramInt1));
  }
  
  public Keyboard(Context paramContext, int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3)
  {
    this(paramContext, paramInt1);
    paramInt1 = 0;
    int m = 0;
    int k = 0;
    this.mTotalWidth = 0;
    paramContext = new Row(this);
    paramContext.defaultHeight = this.mDefaultHeight;
    paramContext.defaultWidth = this.mDefaultWidth;
    paramContext.defaultHorizontalGap = this.mDefaultHorizontalGap;
    paramContext.verticalGap = this.mDefaultVerticalGap;
    paramContext.rowEdgeFlags = 12;
    if (paramInt2 == -1) {}
    for (int i = Integer.MAX_VALUE;; i = paramInt2)
    {
      int j = 0;
      paramInt2 = k;
      while (j < paramCharSequence.length())
      {
        char c = paramCharSequence.charAt(j);
        int n;
        if (paramInt2 < i)
        {
          n = paramInt2;
          paramInt2 = paramInt1;
          k = m;
          if (this.mDefaultWidth + paramInt1 + paramInt3 <= this.mDisplayWidth) {}
        }
        else
        {
          paramInt2 = 0;
          k = m + (this.mDefaultVerticalGap + this.mDefaultHeight);
          n = 0;
        }
        Key localKey = new Key(paramContext);
        localKey.x = paramInt2;
        localKey.y = k;
        localKey.label = String.valueOf(c);
        localKey.codes = new int[] { c };
        paramInt1 = n + 1;
        m = paramInt2 + (localKey.width + localKey.gap);
        this.mKeys.add(localKey);
        paramContext.mKeys.add(localKey);
        if (m > this.mTotalWidth) {
          this.mTotalWidth = m;
        }
        j += 1;
        paramInt2 = paramInt1;
        paramInt1 = m;
        m = k;
      }
    }
    this.mTotalHeight = (this.mDefaultHeight + m);
    this.rows.add(paramContext);
  }
  
  private void computeNearestNeighbors()
  {
    this.mCellWidth = ((getMinWidth() + 10 - 1) / 10);
    this.mCellHeight = ((getHeight() + 5 - 1) / 5);
    this.mGridNeighbors = new int[50][];
    int[] arrayOfInt = new int[this.mKeys.size()];
    int i1 = this.mCellWidth;
    int i2 = this.mCellHeight;
    int i = 0;
    while (i < i1 * 10)
    {
      int j = 0;
      while (j < i2 * 5)
      {
        int m = 0;
        int k = 0;
        if (k < this.mKeys.size())
        {
          localObject = (Key)this.mKeys.get(k);
          if ((((Key)localObject).squaredDistanceFrom(i, j) < this.mProximityThreshold) || (((Key)localObject).squaredDistanceFrom(this.mCellWidth + i - 1, j) < this.mProximityThreshold)) {}
          for (;;)
          {
            label153:
            arrayOfInt[m] = k;
            int n = m + 1;
            do
            {
              k += 1;
              m = n;
              break;
              if (((Key)localObject).squaredDistanceFrom(this.mCellWidth + i - 1, this.mCellHeight + j - 1) < this.mProximityThreshold) {
                break label153;
              }
              n = m;
            } while (((Key)localObject).squaredDistanceFrom(i, this.mCellHeight + j - 1) >= this.mProximityThreshold);
          }
        }
        Object localObject = new int[m];
        System.arraycopy(arrayOfInt, 0, (int[])localObject, 0, m);
        this.mGridNeighbors[(j / this.mCellHeight * 10 + i / this.mCellWidth)] = localObject;
        j += this.mCellHeight;
      }
      i += this.mCellWidth;
    }
  }
  
  static int getDimensionOrFraction(TypedArray paramTypedArray, int paramInt1, int paramInt2, int paramInt3)
  {
    TypedValue localTypedValue = paramTypedArray.peekValue(paramInt1);
    if (localTypedValue == null) {
      return paramInt3;
    }
    if (localTypedValue.type == 5) {
      return paramTypedArray.getDimensionPixelOffset(paramInt1, paramInt3);
    }
    if (localTypedValue.type == 6) {
      return Math.round(paramTypedArray.getFraction(paramInt1, paramInt2, paramInt2, paramInt3));
    }
    return paramInt3;
  }
  
  private void loadKeyboard(Context paramContext, XmlResourceParser paramXmlResourceParser)
  {
    int m = 0;
    int j = 0;
    int i1 = 0;
    int k = 0;
    int i = 0;
    Key localKey = null;
    Object localObject = null;
    Resources localResources = paramContext.getResources();
    paramContext = (Context)localObject;
    label373:
    label382:
    label415:
    label441:
    label555:
    for (int n = i;; n = 0)
    {
      for (;;)
      {
        try
        {
          i2 = paramXmlResourceParser.next();
          n = i;
          if (i2 != 1)
          {
            if (i2 != 2) {
              break label441;
            }
            n = i;
            localObject = paramXmlResourceParser.getName();
            n = i;
            if ("Row".equals(localObject))
            {
              j = 1;
              i2 = 0;
              n = i;
              localObject = createRowFromXml(localResources, paramXmlResourceParser);
              n = i;
              this.rows.add(localObject);
              n = i;
              if (((Row)localObject).mode == 0) {
                break label555;
              }
              n = i;
              if (((Row)localObject).mode == this.mKeyboardMode) {
                break label555;
              }
              n = 1;
              paramContext = (Context)localObject;
              k = i2;
              if (n == 0) {
                break;
              }
              n = i;
              skipToEndOfRow(paramXmlResourceParser);
              j = 0;
              paramContext = (Context)localObject;
              k = i2;
              break;
            }
            n = i;
            if (!"Key".equals(localObject)) {
              break label415;
            }
            i2 = 1;
            n = i;
            localKey = createKeyFromXml(localResources, paramContext, k, i, paramXmlResourceParser);
            n = i;
            this.mKeys.add(localKey);
            n = i;
            if (localKey.codes[0] != -1) {
              break label382;
            }
            m = 0;
            n = i;
            if (m < this.mShiftKeys.length)
            {
              n = i;
              if (this.mShiftKeys[m] != null) {
                break label373;
              }
              n = i;
              this.mShiftKeys[m] = localKey;
              n = i;
              this.mShiftKeyIndices[m] = (this.mKeys.size() - 1);
            }
            n = i;
            this.mModifierKeys.add(localKey);
            n = i;
            paramContext.mKeys.add(localKey);
            m = i2;
          }
        }
        catch (Exception paramContext)
        {
          Log.e("Keyboard", "Parse error:" + paramContext);
          paramContext.printStackTrace();
          this.mTotalHeight = (n - this.mDefaultVerticalGap);
          return;
        }
        m += 1;
        continue;
        n = i;
        if (localKey.codes[0] == -6)
        {
          n = i;
          this.mModifierKeys.add(localKey);
        }
      }
      n = i;
      if (!"Keyboard".equals(localObject)) {
        break;
      }
      n = i;
      parseKeyboardAttributes(localResources, paramXmlResourceParser);
      break;
      if (i2 != 3) {
        break;
      }
      if (m != 0)
      {
        i2 = 0;
        n = i;
        int i3 = k + (localKey.gap + localKey.width);
        k = i3;
        m = i2;
        n = i;
        if (i3 <= this.mTotalWidth) {
          break;
        }
        n = i;
        this.mTotalWidth = i3;
        k = i3;
        m = i2;
        break;
      }
      if (j == 0) {
        break;
      }
      j = 0;
      n = i;
      i += paramContext.verticalGap;
      n = i;
      int i2 = paramContext.defaultHeight;
      i += i2;
      i1 += 1;
      break;
    }
  }
  
  private void parseKeyboardAttributes(Resources paramResources, XmlResourceParser paramXmlResourceParser)
  {
    paramResources = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlResourceParser), R.styleable.Keyboard);
    this.mDefaultWidth = getDimensionOrFraction(paramResources, 0, this.mDisplayWidth, this.mDisplayWidth / 10);
    this.mDefaultHeight = getDimensionOrFraction(paramResources, 1, this.mDisplayHeight, 50);
    this.mDefaultHorizontalGap = getDimensionOrFraction(paramResources, 2, this.mDisplayWidth, 0);
    this.mDefaultVerticalGap = getDimensionOrFraction(paramResources, 3, this.mDisplayHeight, 0);
    this.mProximityThreshold = ((int)(this.mDefaultWidth * SEARCH_DISTANCE));
    this.mProximityThreshold *= this.mProximityThreshold;
    paramResources.recycle();
  }
  
  private void skipToEndOfRow(XmlResourceParser paramXmlResourceParser)
    throws XmlPullParserException, IOException
  {
    int i;
    do
    {
      i = paramXmlResourceParser.next();
    } while ((i != 1) && ((i != 3) || (!paramXmlResourceParser.getName().equals("Row"))));
  }
  
  protected Key createKeyFromXml(Resources paramResources, Row paramRow, int paramInt1, int paramInt2, XmlResourceParser paramXmlResourceParser)
  {
    return new Key(paramResources, paramRow, paramInt1, paramInt2, paramXmlResourceParser);
  }
  
  protected Row createRowFromXml(Resources paramResources, XmlResourceParser paramXmlResourceParser)
  {
    return new Row(paramResources, this, paramXmlResourceParser);
  }
  
  public int getHeight()
  {
    return this.mTotalHeight;
  }
  
  protected int getHorizontalGap()
  {
    return this.mDefaultHorizontalGap;
  }
  
  protected int getKeyHeight()
  {
    return this.mDefaultHeight;
  }
  
  protected int getKeyWidth()
  {
    return this.mDefaultWidth;
  }
  
  public List<Key> getKeys()
  {
    return this.mKeys;
  }
  
  public int getMinWidth()
  {
    return this.mTotalWidth;
  }
  
  public List<Key> getModifierKeys()
  {
    return this.mModifierKeys;
  }
  
  public int[] getNearestKeys(int paramInt1, int paramInt2)
  {
    if (this.mGridNeighbors == null) {
      computeNearestNeighbors();
    }
    if ((paramInt1 >= 0) && (paramInt1 < getMinWidth()) && (paramInt2 >= 0) && (paramInt2 < getHeight()))
    {
      paramInt1 = paramInt2 / this.mCellHeight * 10 + paramInt1 / this.mCellWidth;
      if (paramInt1 < 50) {
        return this.mGridNeighbors[paramInt1];
      }
    }
    return new int[0];
  }
  
  public int getShiftKeyIndex()
  {
    return this.mShiftKeyIndices[0];
  }
  
  public int[] getShiftKeyIndices()
  {
    return this.mShiftKeyIndices;
  }
  
  protected int getVerticalGap()
  {
    return this.mDefaultVerticalGap;
  }
  
  public boolean isShifted()
  {
    return this.mShifted;
  }
  
  final void resize(int paramInt1, int paramInt2)
  {
    int n = this.rows.size();
    paramInt2 = 0;
    while (paramInt2 < n)
    {
      Row localRow = (Row)this.rows.get(paramInt2);
      int i1 = localRow.mKeys.size();
      int j = 0;
      int i = 0;
      int k = 0;
      Key localKey;
      while (k < i1)
      {
        localKey = (Key)localRow.mKeys.get(k);
        int m = j;
        if (k > 0) {
          m = j + localKey.gap;
        }
        i += localKey.width;
        k += 1;
        j = m;
      }
      if (j + i > paramInt1)
      {
        k = 0;
        float f = (paramInt1 - j) / i;
        i = 0;
        j = k;
        while (i < i1)
        {
          localKey = (Key)localRow.mKeys.get(i);
          localKey.width = ((int)(localKey.width * f));
          localKey.x = j;
          j += localKey.width + localKey.gap;
          i += 1;
        }
      }
      paramInt2 += 1;
    }
    this.mTotalWidth = paramInt1;
  }
  
  protected void setHorizontalGap(int paramInt)
  {
    this.mDefaultHorizontalGap = paramInt;
  }
  
  protected void setKeyHeight(int paramInt)
  {
    this.mDefaultHeight = paramInt;
  }
  
  protected void setKeyWidth(int paramInt)
  {
    this.mDefaultWidth = paramInt;
  }
  
  public boolean setShifted(boolean paramBoolean)
  {
    Key[] arrayOfKey = this.mShiftKeys;
    int j = arrayOfKey.length;
    int i = 0;
    while (i < j)
    {
      Key localKey = arrayOfKey[i];
      if (localKey != null) {
        localKey.on = paramBoolean;
      }
      i += 1;
    }
    if (this.mShifted != paramBoolean)
    {
      this.mShifted = paramBoolean;
      return true;
    }
    return false;
  }
  
  protected void setVerticalGap(int paramInt)
  {
    this.mDefaultVerticalGap = paramInt;
  }
  
  public static class Key
  {
    private static final int[] KEY_STATE_NORMAL = new int[0];
    private static final int[] KEY_STATE_NORMAL_OFF;
    private static final int[] KEY_STATE_NORMAL_ON = { 16842911, 16842912 };
    private static final int[] KEY_STATE_PRESSED = { 16842919 };
    private static final int[] KEY_STATE_PRESSED_OFF;
    private static final int[] KEY_STATE_PRESSED_ON = { 16842919, 16842911, 16842912 };
    public int[] codes;
    public int edgeFlags;
    public int gap;
    public int height;
    public Drawable icon;
    public Drawable iconPreview;
    private Keyboard keyboard;
    public CharSequence label;
    public boolean modifier;
    public boolean on;
    public CharSequence popupCharacters;
    public int popupResId;
    public boolean pressed;
    public boolean repeatable;
    public boolean sticky;
    public CharSequence text;
    public int width;
    public int x;
    public int y;
    
    static
    {
      KEY_STATE_NORMAL_OFF = new int[] { 16842911 };
      KEY_STATE_PRESSED_OFF = new int[] { 16842919, 16842911 };
    }
    
    public Key(Resources paramResources, Keyboard.Row paramRow, int paramInt1, int paramInt2, XmlResourceParser paramXmlResourceParser)
    {
      this(paramRow);
      this.x = paramInt1;
      this.y = paramInt2;
      TypedArray localTypedArray = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlResourceParser), R.styleable.Keyboard);
      this.width = Keyboard.getDimensionOrFraction(localTypedArray, 0, Keyboard.-get5(this.keyboard), paramRow.defaultWidth);
      this.height = Keyboard.getDimensionOrFraction(localTypedArray, 1, Keyboard.-get4(this.keyboard), paramRow.defaultHeight);
      this.gap = Keyboard.getDimensionOrFraction(localTypedArray, 2, Keyboard.-get5(this.keyboard), paramRow.defaultHorizontalGap);
      localTypedArray.recycle();
      paramResources = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlResourceParser), R.styleable.Keyboard_Key);
      this.x += this.gap;
      paramXmlResourceParser = new TypedValue();
      paramResources.getValue(0, paramXmlResourceParser);
      if ((paramXmlResourceParser.type == 16) || (paramXmlResourceParser.type == 17))
      {
        this.codes = new int[] { paramXmlResourceParser.data };
        this.iconPreview = paramResources.getDrawable(7);
        if (this.iconPreview != null) {
          this.iconPreview.setBounds(0, 0, this.iconPreview.getIntrinsicWidth(), this.iconPreview.getIntrinsicHeight());
        }
        this.popupCharacters = paramResources.getText(2);
        this.popupResId = paramResources.getResourceId(1, 0);
        this.repeatable = paramResources.getBoolean(6, false);
        this.modifier = paramResources.getBoolean(4, false);
        this.sticky = paramResources.getBoolean(5, false);
        this.edgeFlags = paramResources.getInt(3, 0);
        this.edgeFlags |= paramRow.rowEdgeFlags;
        this.icon = paramResources.getDrawable(10);
        if (this.icon != null) {
          this.icon.setBounds(0, 0, this.icon.getIntrinsicWidth(), this.icon.getIntrinsicHeight());
        }
        this.label = paramResources.getText(9);
        this.text = paramResources.getText(8);
        if ((this.codes == null) && (!TextUtils.isEmpty(this.label))) {
          break label401;
        }
      }
      for (;;)
      {
        paramResources.recycle();
        return;
        if (paramXmlResourceParser.type != 3) {
          break;
        }
        this.codes = parseCSV(paramXmlResourceParser.string.toString());
        break;
        label401:
        this.codes = new int[] { this.label.charAt(0) };
      }
    }
    
    public Key(Keyboard.Row paramRow)
    {
      this.keyboard = Keyboard.Row.-get0(paramRow);
      this.height = paramRow.defaultHeight;
      this.width = paramRow.defaultWidth;
      this.gap = paramRow.defaultHorizontalGap;
      this.edgeFlags = paramRow.rowEdgeFlags;
    }
    
    public int[] getCurrentDrawableState()
    {
      int[] arrayOfInt = KEY_STATE_NORMAL;
      if (this.on) {
        if (this.pressed) {
          arrayOfInt = KEY_STATE_PRESSED_ON;
        }
      }
      do
      {
        return arrayOfInt;
        return KEY_STATE_NORMAL_ON;
        if (this.sticky)
        {
          if (this.pressed) {
            return KEY_STATE_PRESSED_OFF;
          }
          return KEY_STATE_NORMAL_OFF;
        }
      } while (!this.pressed);
      return KEY_STATE_PRESSED;
    }
    
    public boolean isInside(int paramInt1, int paramInt2)
    {
      int i;
      int j;
      label23:
      int k;
      if ((this.edgeFlags & 0x1) > 0)
      {
        i = 1;
        if ((this.edgeFlags & 0x2) <= 0) {
          break label158;
        }
        j = 1;
        if ((this.edgeFlags & 0x4) <= 0) {
          break label164;
        }
        k = 1;
        label35:
        if ((this.edgeFlags & 0x8) <= 0) {
          break label170;
        }
      }
      label158:
      label164:
      label170:
      for (int m = 1;; m = 0)
      {
        if (((paramInt1 < this.x) && ((i == 0) || (paramInt1 > this.x + this.width))) || ((paramInt1 >= this.x + this.width) && ((j == 0) || (paramInt1 < this.x))) || ((paramInt2 < this.y) && ((k == 0) || (paramInt2 > this.y + this.height))) || ((paramInt2 >= this.y + this.height) && ((m == 0) || (paramInt2 < this.y)))) {
          break label176;
        }
        return true;
        i = 0;
        break;
        j = 0;
        break label23;
        k = 0;
        break label35;
      }
      label176:
      return false;
    }
    
    public void onPressed()
    {
      if (this.pressed) {}
      for (boolean bool = false;; bool = true)
      {
        this.pressed = bool;
        return;
      }
    }
    
    public void onReleased(boolean paramBoolean)
    {
      boolean bool2 = false;
      boolean bool1;
      if (this.pressed)
      {
        bool1 = false;
        this.pressed = bool1;
        if ((this.sticky) && (paramBoolean)) {
          if (!this.on) {
            break label47;
          }
        }
      }
      label47:
      for (paramBoolean = bool2;; paramBoolean = true)
      {
        this.on = paramBoolean;
        return;
        bool1 = true;
        break;
      }
    }
    
    int[] parseCSV(String paramString)
    {
      int j = 0;
      int i = 0;
      if (paramString.length() > 0)
      {
        int k = 1;
        j = i;
        i = k;
        for (;;)
        {
          k = paramString.indexOf(",", j + 1);
          j = i;
          if (k <= 0) {
            break;
          }
          i += 1;
          j = k;
        }
      }
      int[] arrayOfInt = new int[j];
      i = 0;
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
      for (;;)
      {
        if (localStringTokenizer.hasMoreTokens()) {
          try
          {
            arrayOfInt[i] = Integer.parseInt(localStringTokenizer.nextToken());
            i += 1;
          }
          catch (NumberFormatException localNumberFormatException)
          {
            for (;;)
            {
              Log.e("Keyboard", "Error parsing keycodes " + paramString);
            }
          }
        }
      }
      return arrayOfInt;
    }
    
    public int squaredDistanceFrom(int paramInt1, int paramInt2)
    {
      paramInt1 = this.x + this.width / 2 - paramInt1;
      paramInt2 = this.y + this.height / 2 - paramInt2;
      return paramInt1 * paramInt1 + paramInt2 * paramInt2;
    }
  }
  
  public static class Row
  {
    public int defaultHeight;
    public int defaultHorizontalGap;
    public int defaultWidth;
    ArrayList<Keyboard.Key> mKeys = new ArrayList();
    public int mode;
    private Keyboard parent;
    public int rowEdgeFlags;
    public int verticalGap;
    
    public Row(Resources paramResources, Keyboard paramKeyboard, XmlResourceParser paramXmlResourceParser)
    {
      this.parent = paramKeyboard;
      TypedArray localTypedArray = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlResourceParser), R.styleable.Keyboard);
      this.defaultWidth = Keyboard.getDimensionOrFraction(localTypedArray, 0, Keyboard.-get5(paramKeyboard), Keyboard.-get3(paramKeyboard));
      this.defaultHeight = Keyboard.getDimensionOrFraction(localTypedArray, 1, Keyboard.-get4(paramKeyboard), Keyboard.-get0(paramKeyboard));
      this.defaultHorizontalGap = Keyboard.getDimensionOrFraction(localTypedArray, 2, Keyboard.-get5(paramKeyboard), Keyboard.-get1(paramKeyboard));
      this.verticalGap = Keyboard.getDimensionOrFraction(localTypedArray, 3, Keyboard.-get4(paramKeyboard), Keyboard.-get2(paramKeyboard));
      localTypedArray.recycle();
      paramResources = paramResources.obtainAttributes(Xml.asAttributeSet(paramXmlResourceParser), R.styleable.Keyboard_Row);
      this.rowEdgeFlags = paramResources.getInt(0, 0);
      this.mode = paramResources.getResourceId(1, 0);
    }
    
    public Row(Keyboard paramKeyboard)
    {
      this.parent = paramKeyboard;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/Keyboard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */