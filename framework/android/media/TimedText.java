package android.media;

import android.graphics.Rect;
import android.os.Parcel;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class TimedText
{
  private static final int FIRST_PRIVATE_KEY = 101;
  private static final int FIRST_PUBLIC_KEY = 1;
  private static final int KEY_BACKGROUND_COLOR_RGBA = 3;
  private static final int KEY_DISPLAY_FLAGS = 1;
  private static final int KEY_END_CHAR = 104;
  private static final int KEY_FONT_ID = 105;
  private static final int KEY_FONT_SIZE = 106;
  private static final int KEY_GLOBAL_SETTING = 101;
  private static final int KEY_HIGHLIGHT_COLOR_RGBA = 4;
  private static final int KEY_LOCAL_SETTING = 102;
  private static final int KEY_SCROLL_DELAY = 5;
  private static final int KEY_START_CHAR = 103;
  private static final int KEY_START_TIME = 7;
  private static final int KEY_STRUCT_BLINKING_TEXT_LIST = 8;
  private static final int KEY_STRUCT_FONT_LIST = 9;
  private static final int KEY_STRUCT_HIGHLIGHT_LIST = 10;
  private static final int KEY_STRUCT_HYPER_TEXT_LIST = 11;
  private static final int KEY_STRUCT_JUSTIFICATION = 15;
  private static final int KEY_STRUCT_KARAOKE_LIST = 12;
  private static final int KEY_STRUCT_STYLE_LIST = 13;
  private static final int KEY_STRUCT_TEXT = 16;
  private static final int KEY_STRUCT_TEXT_POS = 14;
  private static final int KEY_STYLE_FLAGS = 2;
  private static final int KEY_TEXT_COLOR_RGBA = 107;
  private static final int KEY_WRAP_TEXT = 6;
  private static final int LAST_PRIVATE_KEY = 107;
  private static final int LAST_PUBLIC_KEY = 16;
  private static final String TAG = "TimedText";
  private int mBackgroundColorRGBA = -1;
  private List<CharPos> mBlinkingPosList = null;
  private int mDisplayFlags = -1;
  private List<Font> mFontList = null;
  private int mHighlightColorRGBA = -1;
  private List<CharPos> mHighlightPosList = null;
  private List<HyperText> mHyperTextList = null;
  private Justification mJustification;
  private List<Karaoke> mKaraokeList = null;
  private final HashMap<Integer, Object> mKeyObjectMap = new HashMap();
  private int mScrollDelay = -1;
  private List<Style> mStyleList = null;
  private Rect mTextBounds = null;
  private String mTextChars = null;
  private int mWrapText = -1;
  
  public TimedText(Parcel paramParcel)
  {
    if (!parseParcel(paramParcel))
    {
      this.mKeyObjectMap.clear();
      throw new IllegalArgumentException("parseParcel() fails");
    }
  }
  
  private boolean containsKey(int paramInt)
  {
    return (isValidKey(paramInt)) && (this.mKeyObjectMap.containsKey(Integer.valueOf(paramInt)));
  }
  
  private Object getObject(int paramInt)
  {
    if (containsKey(paramInt)) {
      return this.mKeyObjectMap.get(Integer.valueOf(paramInt));
    }
    throw new IllegalArgumentException("Invalid key: " + paramInt);
  }
  
  private boolean isValidKey(int paramInt)
  {
    return ((paramInt >= 1) && (paramInt <= 16)) || ((paramInt >= 101) && (paramInt <= 107));
  }
  
  private Set keySet()
  {
    return this.mKeyObjectMap.keySet();
  }
  
  private boolean parseParcel(Parcel paramParcel)
  {
    paramParcel.setDataPosition(0);
    if (paramParcel.dataAvail() == 0) {
      return false;
    }
    int i = paramParcel.readInt();
    int j;
    Object localObject1;
    if (i == 102)
    {
      i = paramParcel.readInt();
      if (i != 7) {
        return false;
      }
      j = paramParcel.readInt();
      this.mKeyObjectMap.put(Integer.valueOf(i), Integer.valueOf(j));
      if (paramParcel.readInt() != 16) {
        return false;
      }
      paramParcel.readInt();
      localObject1 = paramParcel.createByteArray();
      if ((localObject1 == null) || (localObject1.length == 0)) {
        this.mTextChars = null;
      }
    }
    while (paramParcel.dataAvail() > 0)
    {
      i = paramParcel.readInt();
      if (!isValidKey(i))
      {
        Log.w("TimedText", "Invalid timed text key found: " + i);
        return false;
        this.mTextChars = new String((byte[])localObject1);
        continue;
        if (i != 101)
        {
          Log.w("TimedText", "Invalid timed text key found: " + i);
          return false;
        }
      }
      else
      {
        Object localObject2 = null;
        localObject1 = localObject2;
        switch (i)
        {
        default: 
          localObject1 = localObject2;
        }
        while (localObject1 != null)
        {
          if (this.mKeyObjectMap.containsKey(Integer.valueOf(i))) {
            this.mKeyObjectMap.remove(Integer.valueOf(i));
          }
          this.mKeyObjectMap.put(Integer.valueOf(i), localObject1);
          break;
          readStyle(paramParcel);
          localObject1 = this.mStyleList;
          continue;
          readFont(paramParcel);
          localObject1 = this.mFontList;
          continue;
          readHighlight(paramParcel);
          localObject1 = this.mHighlightPosList;
          continue;
          readKaraoke(paramParcel);
          localObject1 = this.mKaraokeList;
          continue;
          readHyperText(paramParcel);
          localObject1 = this.mHyperTextList;
          continue;
          readBlinkingText(paramParcel);
          localObject1 = this.mBlinkingPosList;
          continue;
          this.mWrapText = paramParcel.readInt();
          localObject1 = Integer.valueOf(this.mWrapText);
          continue;
          this.mHighlightColorRGBA = paramParcel.readInt();
          localObject1 = Integer.valueOf(this.mHighlightColorRGBA);
          continue;
          this.mDisplayFlags = paramParcel.readInt();
          localObject1 = Integer.valueOf(this.mDisplayFlags);
          continue;
          this.mJustification = new Justification(paramParcel.readInt(), paramParcel.readInt());
          localObject1 = this.mJustification;
          continue;
          this.mBackgroundColorRGBA = paramParcel.readInt();
          localObject1 = Integer.valueOf(this.mBackgroundColorRGBA);
          continue;
          j = paramParcel.readInt();
          int k = paramParcel.readInt();
          int m = paramParcel.readInt();
          this.mTextBounds = new Rect(k, j, paramParcel.readInt(), m);
          localObject1 = localObject2;
          continue;
          this.mScrollDelay = paramParcel.readInt();
          localObject1 = Integer.valueOf(this.mScrollDelay);
        }
      }
    }
    return true;
  }
  
  private void readBlinkingText(Parcel paramParcel)
  {
    paramParcel = new CharPos(paramParcel.readInt(), paramParcel.readInt());
    if (this.mBlinkingPosList == null) {
      this.mBlinkingPosList = new ArrayList();
    }
    this.mBlinkingPosList.add(paramParcel);
  }
  
  private void readFont(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      int k = paramParcel.readInt();
      int m = paramParcel.readInt();
      Font localFont = new Font(k, new String(paramParcel.createByteArray(), 0, m));
      if (this.mFontList == null) {
        this.mFontList = new ArrayList();
      }
      this.mFontList.add(localFont);
      i += 1;
    }
  }
  
  private void readHighlight(Parcel paramParcel)
  {
    paramParcel = new CharPos(paramParcel.readInt(), paramParcel.readInt());
    if (this.mHighlightPosList == null) {
      this.mHighlightPosList = new ArrayList();
    }
    this.mHighlightPosList.add(paramParcel);
  }
  
  private void readHyperText(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    int j = paramParcel.readInt();
    int k = paramParcel.readInt();
    String str = new String(paramParcel.createByteArray(), 0, k);
    k = paramParcel.readInt();
    paramParcel = new HyperText(i, j, str, new String(paramParcel.createByteArray(), 0, k));
    if (this.mHyperTextList == null) {
      this.mHyperTextList = new ArrayList();
    }
    this.mHyperTextList.add(paramParcel);
  }
  
  private void readKaraoke(Parcel paramParcel)
  {
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      Karaoke localKaraoke = new Karaoke(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
      if (this.mKaraokeList == null) {
        this.mKaraokeList = new ArrayList();
      }
      this.mKaraokeList.add(localKaraoke);
      i += 1;
    }
  }
  
  private void readStyle(Parcel paramParcel)
  {
    int i = 0;
    int i1 = -1;
    int n = -1;
    int m = -1;
    boolean bool3 = false;
    boolean bool2 = false;
    boolean bool1 = false;
    int k = -1;
    int j = -1;
    while ((i == 0) && (paramParcel.dataAvail() > 0)) {
      switch (paramParcel.readInt())
      {
      default: 
        paramParcel.setDataPosition(paramParcel.dataPosition() - 4);
        i = 1;
        break;
      case 103: 
        i1 = paramParcel.readInt();
        break;
      case 104: 
        n = paramParcel.readInt();
        break;
      case 105: 
        m = paramParcel.readInt();
        break;
      case 2: 
        int i2 = paramParcel.readInt();
        if (i2 % 2 == 1)
        {
          bool1 = true;
          if (i2 % 4 < 2) {
            break label198;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          if (i2 / 4 != 1) {
            break label204;
          }
          bool4 = true;
          bool3 = bool1;
          bool1 = bool4;
          break;
          bool1 = false;
          break label159;
        }
        boolean bool4 = false;
        bool3 = bool1;
        bool1 = bool4;
        break;
      case 106: 
        k = paramParcel.readInt();
        break;
      case 107: 
        label159:
        label198:
        label204:
        j = paramParcel.readInt();
      }
    }
    paramParcel = new Style(i1, n, m, bool3, bool2, bool1, k, j);
    if (this.mStyleList == null) {
      this.mStyleList = new ArrayList();
    }
    this.mStyleList.add(paramParcel);
  }
  
  public Rect getBounds()
  {
    return this.mTextBounds;
  }
  
  public String getText()
  {
    return this.mTextChars;
  }
  
  public static final class CharPos
  {
    public final int endChar;
    public final int startChar;
    
    public CharPos(int paramInt1, int paramInt2)
    {
      this.startChar = paramInt1;
      this.endChar = paramInt2;
    }
  }
  
  public static final class Font
  {
    public final int ID;
    public final String name;
    
    public Font(int paramInt, String paramString)
    {
      this.ID = paramInt;
      this.name = paramString;
    }
  }
  
  public static final class HyperText
  {
    public final String URL;
    public final String altString;
    public final int endChar;
    public final int startChar;
    
    public HyperText(int paramInt1, int paramInt2, String paramString1, String paramString2)
    {
      this.startChar = paramInt1;
      this.endChar = paramInt2;
      this.URL = paramString1;
      this.altString = paramString2;
    }
  }
  
  public static final class Justification
  {
    public final int horizontalJustification;
    public final int verticalJustification;
    
    public Justification(int paramInt1, int paramInt2)
    {
      this.horizontalJustification = paramInt1;
      this.verticalJustification = paramInt2;
    }
  }
  
  public static final class Karaoke
  {
    public final int endChar;
    public final int endTimeMs;
    public final int startChar;
    public final int startTimeMs;
    
    public Karaoke(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.startTimeMs = paramInt1;
      this.endTimeMs = paramInt2;
      this.startChar = paramInt3;
      this.endChar = paramInt4;
    }
  }
  
  public static final class Style
  {
    public final int colorRGBA;
    public final int endChar;
    public final int fontID;
    public final int fontSize;
    public final boolean isBold;
    public final boolean isItalic;
    public final boolean isUnderlined;
    public final int startChar;
    
    public Style(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, int paramInt5)
    {
      this.startChar = paramInt1;
      this.endChar = paramInt2;
      this.fontID = paramInt3;
      this.isBold = paramBoolean1;
      this.isItalic = paramBoolean2;
      this.isUnderlined = paramBoolean3;
      this.fontSize = paramInt4;
      this.colorRGBA = paramInt5;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TimedText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */