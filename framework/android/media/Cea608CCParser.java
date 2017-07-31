package android.media;

import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import java.util.ArrayList;
import java.util.Arrays;

class Cea608CCParser
{
  private static final int AOF = 34;
  private static final int AON = 35;
  private static final int BS = 33;
  private static final int CR = 45;
  private static final boolean DEBUG = Log.isLoggable("Cea608CCParser", 3);
  private static final int DER = 36;
  private static final int EDM = 44;
  private static final int ENM = 46;
  private static final int EOC = 47;
  private static final int FON = 40;
  private static final int INVALID = -1;
  public static final int MAX_COLS = 32;
  public static final int MAX_ROWS = 15;
  private static final int MODE_PAINT_ON = 1;
  private static final int MODE_POP_ON = 3;
  private static final int MODE_ROLL_UP = 2;
  private static final int MODE_TEXT = 4;
  private static final int MODE_UNKNOWN = 0;
  private static final int RCL = 32;
  private static final int RDC = 41;
  private static final int RTD = 43;
  private static final int RU2 = 37;
  private static final int RU3 = 38;
  private static final int RU4 = 39;
  private static final String TAG = "Cea608CCParser";
  private static final int TR = 42;
  private static final char TS = ' ';
  private CCMemory mDisplay = new CCMemory();
  private final DisplayListener mListener;
  private int mMode = 1;
  private CCMemory mNonDisplay = new CCMemory();
  private int mPrevCtrlCode = -1;
  private int mRollUpSize = 4;
  private CCMemory mTextMem = new CCMemory();
  
  Cea608CCParser(DisplayListener paramDisplayListener)
  {
    this.mListener = paramDisplayListener;
  }
  
  private CCMemory getMemory()
  {
    switch (this.mMode)
    {
    default: 
      Log.w("Cea608CCParser", "unrecoginized mode: " + this.mMode);
      return this.mDisplay;
    case 3: 
      return this.mNonDisplay;
    case 4: 
      return this.mTextMem;
    }
    return this.mDisplay;
  }
  
  private boolean handleCtrlCode(CCData paramCCData)
  {
    int i = paramCCData.getCtrlCode();
    if ((this.mPrevCtrlCode != -1) && (this.mPrevCtrlCode == i))
    {
      this.mPrevCtrlCode = -1;
      return true;
    }
    switch (i)
    {
    case 34: 
    case 35: 
    default: 
      this.mPrevCtrlCode = -1;
      return false;
    case 32: 
      this.mMode = 3;
    }
    for (;;)
    {
      this.mPrevCtrlCode = i;
      return true;
      getMemory().bs();
      continue;
      getMemory().der();
      continue;
      this.mRollUpSize = (i - 35);
      if (this.mMode != 2)
      {
        this.mDisplay.erase();
        this.mNonDisplay.erase();
      }
      this.mMode = 2;
      continue;
      Log.i("Cea608CCParser", "Flash On");
      continue;
      this.mMode = 1;
      continue;
      this.mMode = 4;
      this.mTextMem.erase();
      continue;
      this.mMode = 4;
      continue;
      this.mDisplay.erase();
      updateDisplay();
      continue;
      if (this.mMode == 2) {
        getMemory().rollUp(this.mRollUpSize);
      }
      while (this.mMode == 2)
      {
        updateDisplay();
        break;
        getMemory().cr();
      }
      this.mNonDisplay.erase();
      continue;
      swapMemory();
      this.mMode = 3;
      updateDisplay();
    }
  }
  
  private boolean handleDisplayableChars(CCData paramCCData)
  {
    if (!paramCCData.isDisplayableChar()) {
      return false;
    }
    if (CCData.-wrap0(paramCCData)) {
      getMemory().bs();
    }
    getMemory().writeText(paramCCData.getDisplayText());
    if ((this.mMode == 1) || (this.mMode == 2)) {
      updateDisplay();
    }
    return true;
  }
  
  private boolean handleMidRowCode(CCData paramCCData)
  {
    paramCCData = paramCCData.getMidRow();
    if (paramCCData != null)
    {
      getMemory().writeMidRowCode(paramCCData);
      return true;
    }
    return false;
  }
  
  private boolean handlePACCode(CCData paramCCData)
  {
    paramCCData = paramCCData.getPAC();
    if (paramCCData != null)
    {
      if (this.mMode == 2) {
        CCMemory.-wrap0(getMemory(), paramCCData.getRow(), this.mRollUpSize);
      }
      getMemory().writePAC(paramCCData);
      return true;
    }
    return false;
  }
  
  private boolean handleTabOffsets(CCData paramCCData)
  {
    int i = paramCCData.getTabOffset();
    if (i > 0)
    {
      getMemory().tab(i);
      return true;
    }
    return false;
  }
  
  private void swapMemory()
  {
    CCMemory localCCMemory = this.mDisplay;
    this.mDisplay = this.mNonDisplay;
    this.mNonDisplay = localCCMemory;
  }
  
  private void updateDisplay()
  {
    if (this.mListener != null)
    {
      CaptioningManager.CaptionStyle localCaptionStyle = this.mListener.getCaptionStyle();
      this.mListener.onDisplayChanged(this.mDisplay.getStyledText(localCaptionStyle));
    }
  }
  
  public void parse(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = CCData.fromByteArray(paramArrayOfByte);
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      if (DEBUG) {
        Log.d("Cea608CCParser", paramArrayOfByte[i].toString());
      }
      if ((handleCtrlCode(paramArrayOfByte[i])) || (handleTabOffsets(paramArrayOfByte[i])) || (handlePACCode(paramArrayOfByte[i])) || (handleMidRowCode(paramArrayOfByte[i]))) {}
      for (;;)
      {
        i += 1;
        break;
        handleDisplayableChars(paramArrayOfByte[i]);
      }
    }
  }
  
  private static class CCData
  {
    private static final String[] mCtrlCodeMap = { "RCL", "BS", "AOF", "AON", "DER", "RU2", "RU3", "RU4", "FON", "RDC", "TR", "RTD", "EDM", "CR", "ENM", "EOC" };
    private static final String[] mProtugueseCharMap = { "Ã", "ã", "Í", "Ì", "ì", "Ò", "ò", "Õ", "õ", "{", "}", "\\", "^", "_", "|", "~", "Ä", "ä", "Ö", "ö", "ß", "¥", "¤", "│", "Å", "å", "Ø", "ø", "┌", "┐", "└", "┘" };
    private static final String[] mSpanishCharMap;
    private static final String[] mSpecialCharMap = { "®", "°", "½", "¿", "™", "¢", "£", "♪", "à", " ", "è", "â", "ê", "î", "ô", "û" };
    private final byte mData1;
    private final byte mData2;
    private final byte mType;
    
    static
    {
      mSpanishCharMap = new String[] { "Á", "É", "Ó", "Ú", "Ü", "ü", "‘", "¡", "*", "'", "—", "©", "℠", "•", "“", "”", "À", "Â", "Ç", "È", "Ê", "Ë", "ë", "Î", "Ï", "ï", "Ô", "Ù", "ù", "Û", "«", "»" };
    }
    
    CCData(byte paramByte1, byte paramByte2, byte paramByte3)
    {
      this.mType = paramByte1;
      this.mData1 = paramByte2;
      this.mData2 = paramByte3;
    }
    
    private String ctrlCodeToString(int paramInt)
    {
      return mCtrlCodeMap[(paramInt - 32)];
    }
    
    static CCData[] fromByteArray(byte[] paramArrayOfByte)
    {
      CCData[] arrayOfCCData = new CCData[paramArrayOfByte.length / 3];
      int i = 0;
      while (i < arrayOfCCData.length)
      {
        arrayOfCCData[i] = new CCData(paramArrayOfByte[(i * 3)], paramArrayOfByte[(i * 3 + 1)], paramArrayOfByte[(i * 3 + 2)]);
        i += 1;
      }
      return arrayOfCCData;
    }
    
    private char getBasicChar(byte paramByte)
    {
      switch (paramByte)
      {
      default: 
        return (char)paramByte;
      case 42: 
        return 'á';
      case 92: 
        return 'é';
      case 94: 
        return 'í';
      case 95: 
        return 'ó';
      case 96: 
        return 'ú';
      case 123: 
        return 'ç';
      case 124: 
        return '÷';
      case 125: 
        return 'Ñ';
      case 126: 
        return 'ñ';
      }
      return '█';
    }
    
    private String getBasicChars()
    {
      if ((this.mData1 >= 32) && (this.mData1 <= Byte.MAX_VALUE))
      {
        StringBuilder localStringBuilder = new StringBuilder(2);
        localStringBuilder.append(getBasicChar(this.mData1));
        if ((this.mData2 >= 32) && (this.mData2 <= Byte.MAX_VALUE)) {
          localStringBuilder.append(getBasicChar(this.mData2));
        }
        return localStringBuilder.toString();
      }
      return null;
    }
    
    private String getExtendedChar()
    {
      if (((this.mData1 == 18) || (this.mData1 == 26)) && (this.mData2 >= 32) && (this.mData2 <= 63)) {
        return mSpanishCharMap[(this.mData2 - 32)];
      }
      if (((this.mData1 == 19) || (this.mData1 == 27)) && (this.mData2 >= 32) && (this.mData2 <= 63)) {
        return mProtugueseCharMap[(this.mData2 - 32)];
      }
      return null;
    }
    
    private String getSpecialChar()
    {
      if (((this.mData1 == 17) || (this.mData1 == 25)) && (this.mData2 >= 48) && (this.mData2 <= 63)) {
        return mSpecialCharMap[(this.mData2 - 48)];
      }
      return null;
    }
    
    private boolean isBasicChar()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mData1 >= 32)
      {
        bool1 = bool2;
        if (this.mData1 <= Byte.MAX_VALUE) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    private boolean isExtendedChar()
    {
      boolean bool2 = false;
      if ((this.mData1 == 18) || (this.mData1 == 26)) {}
      for (;;)
      {
        boolean bool1 = bool2;
        if (this.mData2 >= 32)
        {
          bool1 = bool2;
          if (this.mData2 <= 63) {
            bool1 = true;
          }
        }
        do
        {
          return bool1;
          if (this.mData1 == 19) {
            break;
          }
          bool1 = bool2;
        } while (this.mData1 != 27);
      }
    }
    
    private boolean isSpecialChar()
    {
      boolean bool2 = false;
      boolean bool1;
      if (this.mData1 != 17)
      {
        bool1 = bool2;
        if (this.mData1 != 25) {}
      }
      else
      {
        bool1 = bool2;
        if (this.mData2 >= 48)
        {
          bool1 = bool2;
          if (this.mData2 <= 63) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    int getCtrlCode()
    {
      if (((this.mData1 == 20) || (this.mData1 == 28)) && (this.mData2 >= 32) && (this.mData2 <= 47)) {
        return this.mData2;
      }
      return -1;
    }
    
    String getDisplayText()
    {
      String str2 = getBasicChars();
      String str1 = str2;
      if (str2 == null)
      {
        str2 = getSpecialChar();
        str1 = str2;
        if (str2 == null) {
          str1 = getExtendedChar();
        }
      }
      return str1;
    }
    
    Cea608CCParser.StyleCode getMidRow()
    {
      if (((this.mData1 == 17) || (this.mData1 == 25)) && (this.mData2 >= 32) && (this.mData2 <= 47)) {
        return Cea608CCParser.StyleCode.fromByte(this.mData2);
      }
      return null;
    }
    
    Cea608CCParser.PAC getPAC()
    {
      if (((this.mData1 & 0x70) == 16) && ((this.mData2 & 0x40) == 64) && (((this.mData1 & 0x7) != 0) || ((this.mData2 & 0x20) == 0))) {
        return Cea608CCParser.PAC.fromBytes(this.mData1, this.mData2);
      }
      return null;
    }
    
    int getTabOffset()
    {
      if (((this.mData1 == 23) || (this.mData1 == 31)) && (this.mData2 >= 33) && (this.mData2 <= 35)) {
        return this.mData2 & 0x3;
      }
      return 0;
    }
    
    boolean isDisplayableChar()
    {
      if ((!isBasicChar()) && (!isSpecialChar())) {
        return isExtendedChar();
      }
      return true;
    }
    
    public String toString()
    {
      if ((this.mData1 < 16) && (this.mData2 < 16)) {
        return String.format("[%d]Null: %02x %02x", new Object[] { Byte.valueOf(this.mType), Byte.valueOf(this.mData1), Byte.valueOf(this.mData2) });
      }
      int i = getCtrlCode();
      if (i != -1) {
        return String.format("[%d]%s", new Object[] { Byte.valueOf(this.mType), ctrlCodeToString(i) });
      }
      i = getTabOffset();
      if (i > 0) {
        return String.format("[%d]Tab%d", new Object[] { Byte.valueOf(this.mType), Integer.valueOf(i) });
      }
      Object localObject = getPAC();
      if (localObject != null) {
        return String.format("[%d]PAC: %s", new Object[] { Byte.valueOf(this.mType), ((Cea608CCParser.PAC)localObject).toString() });
      }
      localObject = getMidRow();
      if (localObject != null) {
        return String.format("[%d]Mid-row: %s", new Object[] { Byte.valueOf(this.mType), ((Cea608CCParser.StyleCode)localObject).toString() });
      }
      if (isDisplayableChar()) {
        return String.format("[%d]Displayable: %s (%02x %02x)", new Object[] { Byte.valueOf(this.mType), getDisplayText(), Byte.valueOf(this.mData1), Byte.valueOf(this.mData2) });
      }
      return String.format("[%d]Invalid: %02x %02x", new Object[] { Byte.valueOf(this.mType), Byte.valueOf(this.mData1), Byte.valueOf(this.mData2) });
    }
  }
  
  private static class CCLineBuilder
  {
    private final StringBuilder mDisplayChars;
    private final Cea608CCParser.StyleCode[] mMidRowStyles;
    private final Cea608CCParser.StyleCode[] mPACStyles;
    
    CCLineBuilder(String paramString)
    {
      this.mDisplayChars = new StringBuilder(paramString);
      this.mMidRowStyles = new Cea608CCParser.StyleCode[this.mDisplayChars.length()];
      this.mPACStyles = new Cea608CCParser.StyleCode[this.mDisplayChars.length()];
    }
    
    void applyStyleSpan(SpannableStringBuilder paramSpannableStringBuilder, Cea608CCParser.StyleCode paramStyleCode, int paramInt1, int paramInt2)
    {
      if (paramStyleCode.isItalics()) {
        paramSpannableStringBuilder.setSpan(new StyleSpan(2), paramInt1, paramInt2, 33);
      }
      if (paramStyleCode.isUnderline()) {
        paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt1, paramInt2, 33);
      }
    }
    
    char charAt(int paramInt)
    {
      return this.mDisplayChars.charAt(paramInt);
    }
    
    SpannableStringBuilder getStyledText(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(this.mDisplayChars);
      int j = -1;
      int i = 0;
      int k = -1;
      Object localObject2 = null;
      if (i < this.mDisplayChars.length())
      {
        Object localObject3 = null;
        Object localObject1;
        label54:
        int m;
        if (this.mMidRowStyles[i] != null)
        {
          localObject1 = this.mMidRowStyles[i];
          m = k;
          if (localObject1 != null)
          {
            localObject2 = localObject1;
            if ((k >= 0) && (j >= 0)) {
              applyStyleSpan(localSpannableStringBuilder, (Cea608CCParser.StyleCode)localObject1, k, i);
            }
            m = i;
          }
          if (this.mDisplayChars.charAt(i) == ' ') {
            break label165;
          }
          k = j;
          if (j < 0) {
            k = i;
          }
        }
        label165:
        do
        {
          i += 1;
          j = k;
          k = m;
          break;
          localObject1 = localObject3;
          if (this.mPACStyles[i] == null) {
            break label54;
          }
          if (k >= 0)
          {
            localObject1 = localObject3;
            if (j >= 0) {
              break label54;
            }
          }
          localObject1 = this.mPACStyles[i];
          break label54;
          k = j;
        } while (j < 0);
        if (this.mDisplayChars.charAt(j) == ' ') {
          label185:
          if (this.mDisplayChars.charAt(i - 1) != ' ') {
            break label254;
          }
        }
        label254:
        for (k = i;; k = i + 1)
        {
          localSpannableStringBuilder.setSpan(new Cea608CCParser.MutableBackgroundColorSpan(paramCaptionStyle.backgroundColor), j, k, 33);
          if (m >= 0) {
            applyStyleSpan(localSpannableStringBuilder, (Cea608CCParser.StyleCode)localObject2, m, k);
          }
          k = -1;
          break;
          j -= 1;
          break label185;
        }
      }
      return localSpannableStringBuilder;
    }
    
    int length()
    {
      return this.mDisplayChars.length();
    }
    
    void setCharAt(int paramInt, char paramChar)
    {
      this.mDisplayChars.setCharAt(paramInt, paramChar);
      this.mMidRowStyles[paramInt] = null;
    }
    
    void setMidRowAt(int paramInt, Cea608CCParser.StyleCode paramStyleCode)
    {
      this.mDisplayChars.setCharAt(paramInt, ' ');
      this.mMidRowStyles[paramInt] = paramStyleCode;
    }
    
    void setPACAt(int paramInt, Cea608CCParser.PAC paramPAC)
    {
      this.mPACStyles[paramInt] = paramPAC;
    }
  }
  
  private static class CCMemory
  {
    private final String mBlankLine;
    private int mCol;
    private final Cea608CCParser.CCLineBuilder[] mLines = new Cea608CCParser.CCLineBuilder[17];
    private int mRow;
    
    CCMemory()
    {
      char[] arrayOfChar = new char[34];
      Arrays.fill(arrayOfChar, ' ');
      this.mBlankLine = new String(arrayOfChar);
    }
    
    private static int clamp(int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt1 < paramInt2) {
        return paramInt2;
      }
      if (paramInt1 > paramInt3) {
        return paramInt3;
      }
      return paramInt1;
    }
    
    private Cea608CCParser.CCLineBuilder getLineBuffer(int paramInt)
    {
      if (this.mLines[paramInt] == null) {
        this.mLines[paramInt] = new Cea608CCParser.CCLineBuilder(this.mBlankLine);
      }
      return this.mLines[paramInt];
    }
    
    private void moveBaselineTo(int paramInt1, int paramInt2)
    {
      if (this.mRow == paramInt1) {
        return;
      }
      int i = paramInt2;
      if (paramInt1 < paramInt2) {
        i = paramInt1;
      }
      int j = i;
      if (this.mRow < i) {
        j = this.mRow;
      }
      if (paramInt1 < this.mRow)
      {
        i = j - 1;
        while (i >= 0)
        {
          this.mLines[(paramInt1 - i)] = this.mLines[(this.mRow - i)];
          i -= 1;
        }
      }
      i = 0;
      while (i < j)
      {
        this.mLines[(paramInt1 - i)] = this.mLines[(this.mRow - i)];
        i += 1;
      }
      i = 0;
      while (i <= paramInt1 - paramInt2)
      {
        this.mLines[i] = null;
        i += 1;
      }
      paramInt1 += 1;
      while (paramInt1 < this.mLines.length)
      {
        this.mLines[paramInt1] = null;
        paramInt1 += 1;
      }
    }
    
    private void moveCursorByCol(int paramInt)
    {
      this.mCol = clamp(this.mCol + paramInt, 1, 32);
    }
    
    private void moveCursorTo(int paramInt1, int paramInt2)
    {
      this.mRow = clamp(paramInt1, 1, 15);
      this.mCol = clamp(paramInt2, 1, 32);
    }
    
    private void moveCursorToRow(int paramInt)
    {
      this.mRow = clamp(paramInt, 1, 15);
    }
    
    void bs()
    {
      moveCursorByCol(-1);
      if (this.mLines[this.mRow] != null)
      {
        this.mLines[this.mRow].setCharAt(this.mCol, ' ');
        if (this.mCol == 31) {
          this.mLines[this.mRow].setCharAt(32, ' ');
        }
      }
    }
    
    void cr()
    {
      moveCursorTo(this.mRow + 1, 1);
    }
    
    void der()
    {
      if (this.mLines[this.mRow] != null)
      {
        int i = 0;
        while (i < this.mCol)
        {
          if (this.mLines[this.mRow].charAt(i) != ' ')
          {
            i = this.mCol;
            while (i < this.mLines[this.mRow].length())
            {
              this.mLines[i].setCharAt(i, ' ');
              i += 1;
            }
            return;
          }
          i += 1;
        }
        this.mLines[this.mRow] = null;
      }
    }
    
    void erase()
    {
      int i = 0;
      while (i < this.mLines.length)
      {
        this.mLines[i] = null;
        i += 1;
      }
      this.mRow = 15;
      this.mCol = 1;
    }
    
    SpannableStringBuilder[] getStyledText(CaptioningManager.CaptionStyle paramCaptionStyle)
    {
      ArrayList localArrayList = new ArrayList(15);
      int i = 1;
      if (i <= 15)
      {
        if (this.mLines[i] != null) {}
        for (SpannableStringBuilder localSpannableStringBuilder = this.mLines[i].getStyledText(paramCaptionStyle);; localSpannableStringBuilder = null)
        {
          localArrayList.add(localSpannableStringBuilder);
          i += 1;
          break;
        }
      }
      return (SpannableStringBuilder[])localArrayList.toArray(new SpannableStringBuilder[15]);
    }
    
    void rollUp(int paramInt)
    {
      int i = 0;
      while (i <= this.mRow - paramInt)
      {
        this.mLines[i] = null;
        i += 1;
      }
      i = this.mRow - paramInt + 1;
      paramInt = i;
      if (i < 1) {
        paramInt = 1;
      }
      while (paramInt < this.mRow)
      {
        this.mLines[paramInt] = this.mLines[(paramInt + 1)];
        paramInt += 1;
      }
      paramInt = this.mRow;
      while (paramInt < this.mLines.length)
      {
        this.mLines[paramInt] = null;
        paramInt += 1;
      }
      this.mCol = 1;
    }
    
    void tab(int paramInt)
    {
      moveCursorByCol(paramInt);
    }
    
    void writeMidRowCode(Cea608CCParser.StyleCode paramStyleCode)
    {
      getLineBuffer(this.mRow).setMidRowAt(this.mCol, paramStyleCode);
      moveCursorByCol(1);
    }
    
    void writePAC(Cea608CCParser.PAC paramPAC)
    {
      if (paramPAC.isIndentPAC()) {
        moveCursorTo(paramPAC.getRow(), paramPAC.getCol());
      }
      for (;;)
      {
        getLineBuffer(this.mRow).setPACAt(this.mCol, paramPAC);
        return;
        moveCursorTo(paramPAC.getRow(), 1);
      }
    }
    
    void writeText(String paramString)
    {
      int i = 0;
      while (i < paramString.length())
      {
        getLineBuffer(this.mRow).setCharAt(this.mCol, paramString.charAt(i));
        moveCursorByCol(1);
        i += 1;
      }
    }
  }
  
  static abstract interface DisplayListener
  {
    public abstract CaptioningManager.CaptionStyle getCaptionStyle();
    
    public abstract void onDisplayChanged(SpannableStringBuilder[] paramArrayOfSpannableStringBuilder);
  }
  
  public static class MutableBackgroundColorSpan
    extends CharacterStyle
    implements UpdateAppearance
  {
    private int mColor;
    
    public MutableBackgroundColorSpan(int paramInt)
    {
      this.mColor = paramInt;
    }
    
    public int getBackgroundColor()
    {
      return this.mColor;
    }
    
    public void setBackgroundColor(int paramInt)
    {
      this.mColor = paramInt;
    }
    
    public void updateDrawState(TextPaint paramTextPaint)
    {
      paramTextPaint.bgColor = this.mColor;
    }
  }
  
  private static class PAC
    extends Cea608CCParser.StyleCode
  {
    final int mCol;
    final int mRow;
    
    PAC(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt4);
      this.mRow = paramInt1;
      this.mCol = paramInt2;
    }
    
    static PAC fromBytes(byte paramByte1, byte paramByte2)
    {
      int k = new int[] { 11, 1, 3, 12, 14, 5, 7, 9 }[(paramByte1 & 0x7)] + ((paramByte2 & 0x20) >> 5);
      paramByte1 = 0;
      if ((paramByte2 & 0x1) != 0) {
        paramByte1 = 2;
      }
      if ((paramByte2 & 0x10) != 0) {
        return new PAC(k, (paramByte2 >> 1 & 0x7) * 4, paramByte1, 0);
      }
      int j = paramByte2 >> 1 & 0x7;
      int i = j;
      paramByte2 = paramByte1;
      if (j == 7)
      {
        i = 0;
        paramByte2 = paramByte1 | 0x1;
      }
      return new PAC(k, -1, paramByte2, i);
    }
    
    int getCol()
    {
      return this.mCol;
    }
    
    int getRow()
    {
      return this.mRow;
    }
    
    boolean isIndentPAC()
    {
      boolean bool = false;
      if (this.mCol >= 0) {
        bool = true;
      }
      return bool;
    }
    
    public String toString()
    {
      return String.format("{%d, %d}, %s", new Object[] { Integer.valueOf(this.mRow), Integer.valueOf(this.mCol), super.toString() });
    }
  }
  
  private static class StyleCode
  {
    static final int COLOR_BLUE = 2;
    static final int COLOR_CYAN = 3;
    static final int COLOR_GREEN = 1;
    static final int COLOR_INVALID = 7;
    static final int COLOR_MAGENTA = 6;
    static final int COLOR_RED = 4;
    static final int COLOR_WHITE = 0;
    static final int COLOR_YELLOW = 5;
    static final int STYLE_ITALICS = 1;
    static final int STYLE_UNDERLINE = 2;
    static final String[] mColorMap = { "WHITE", "GREEN", "BLUE", "CYAN", "RED", "YELLOW", "MAGENTA", "INVALID" };
    final int mColor;
    final int mStyle;
    
    StyleCode(int paramInt1, int paramInt2)
    {
      this.mStyle = paramInt1;
      this.mColor = paramInt2;
    }
    
    static StyleCode fromByte(byte paramByte)
    {
      byte b = 0;
      int j = paramByte >> 1 & 0x7;
      if ((paramByte & 0x1) != 0) {
        b = 2;
      }
      int i = j;
      paramByte = b;
      if (j == 7)
      {
        i = 0;
        paramByte = b | 0x1;
      }
      return new StyleCode(paramByte, i);
    }
    
    int getColor()
    {
      return this.mColor;
    }
    
    boolean isItalics()
    {
      boolean bool = false;
      if ((this.mStyle & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    
    boolean isUnderline()
    {
      boolean bool = false;
      if ((this.mStyle & 0x2) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("{");
      localStringBuilder.append(mColorMap[this.mColor]);
      if ((this.mStyle & 0x1) != 0) {
        localStringBuilder.append(", ITALICS");
      }
      if ((this.mStyle & 0x2) != 0) {
        localStringBuilder.append(", UNDERLINE");
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea608CCParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */