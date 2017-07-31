package android.media;

import android.graphics.Color;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class Cea708CCParser
{
  public static final int CAPTION_EMIT_TYPE_BUFFER = 1;
  public static final int CAPTION_EMIT_TYPE_COMMAND_CLW = 4;
  public static final int CAPTION_EMIT_TYPE_COMMAND_CWX = 3;
  public static final int CAPTION_EMIT_TYPE_COMMAND_DFX = 16;
  public static final int CAPTION_EMIT_TYPE_COMMAND_DLC = 10;
  public static final int CAPTION_EMIT_TYPE_COMMAND_DLW = 8;
  public static final int CAPTION_EMIT_TYPE_COMMAND_DLY = 9;
  public static final int CAPTION_EMIT_TYPE_COMMAND_DSW = 5;
  public static final int CAPTION_EMIT_TYPE_COMMAND_HDW = 6;
  public static final int CAPTION_EMIT_TYPE_COMMAND_RST = 11;
  public static final int CAPTION_EMIT_TYPE_COMMAND_SPA = 12;
  public static final int CAPTION_EMIT_TYPE_COMMAND_SPC = 13;
  public static final int CAPTION_EMIT_TYPE_COMMAND_SPL = 14;
  public static final int CAPTION_EMIT_TYPE_COMMAND_SWA = 15;
  public static final int CAPTION_EMIT_TYPE_COMMAND_TGW = 7;
  public static final int CAPTION_EMIT_TYPE_CONTROL = 2;
  private static final boolean DEBUG = false;
  private static final String MUSIC_NOTE_CHAR = new String("♫".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
  private static final String TAG = "Cea708CCParser";
  private final StringBuffer mBuffer = new StringBuffer();
  private int mCommand = 0;
  private DisplayListener mListener = new DisplayListener()
  {
    public void emitEvent(Cea708CCParser.CaptionEvent paramAnonymousCaptionEvent) {}
  };
  
  Cea708CCParser(DisplayListener paramDisplayListener)
  {
    if (paramDisplayListener != null) {
      this.mListener = paramDisplayListener;
    }
  }
  
  private void emitCaptionBuffer()
  {
    if (this.mBuffer.length() > 0)
    {
      this.mListener.emitEvent(new CaptionEvent(1, this.mBuffer.toString()));
      this.mBuffer.setLength(0);
    }
  }
  
  private void emitCaptionEvent(CaptionEvent paramCaptionEvent)
  {
    emitCaptionBuffer();
    this.mListener.emitEvent(paramCaptionEvent);
  }
  
  private int parseC0(byte[] paramArrayOfByte, int paramInt)
  {
    int i;
    if ((this.mCommand >= 24) && (this.mCommand <= 31))
    {
      if (this.mCommand == 24)
      {
        if (paramArrayOfByte[paramInt] == 0) {}
        try
        {
          this.mBuffer.append((char)paramArrayOfByte[(paramInt + 1)]);
        }
        catch (UnsupportedEncodingException paramArrayOfByte)
        {
          Log.e("Cea708CCParser", "P16 Code - Could not find supported encoding", paramArrayOfByte);
        }
        paramArrayOfByte = new String(Arrays.copyOfRange(paramArrayOfByte, paramInt, paramInt + 2), "EUC-KR");
        this.mBuffer.append(paramArrayOfByte);
      }
    }
    else
    {
      if ((this.mCommand >= 16) && (this.mCommand <= 23)) {
        return paramInt + 1;
      }
      i = paramInt;
    }
    switch (this.mCommand)
    {
    default: 
      return paramInt;
    case 3: 
      emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char)this.mCommand)));
      return paramInt;
    case 8: 
      emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char)this.mCommand)));
      return paramInt;
    case 12: 
      emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char)this.mCommand)));
      return paramInt;
    case 13: 
      this.mBuffer.append('\n');
      return paramInt;
    case 14: 
      emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char)this.mCommand)));
      return paramInt;
      i = paramInt + 2;
    }
    return i;
  }
  
  private int parseC1(byte[] paramArrayOfByte, int paramInt)
  {
    int j;
    int k;
    boolean bool1;
    boolean bool2;
    int m;
    label484:
    CaptionColor localCaptionColor1;
    CaptionColor localCaptionColor2;
    int n;
    int i1;
    switch (this.mCommand)
    {
    case 147: 
    case 148: 
    case 149: 
    case 150: 
    default: 
      return paramInt;
    case 128: 
    case 129: 
    case 130: 
    case 131: 
    case 132: 
    case 133: 
    case 134: 
    case 135: 
      emitCaptionEvent(new CaptionEvent(3, Integer.valueOf(this.mCommand - 128)));
      return paramInt;
    case 136: 
      emitCaptionEvent(new CaptionEvent(4, Integer.valueOf(paramArrayOfByte[paramInt] & 0xFF)));
      return paramInt + 1;
    case 137: 
      emitCaptionEvent(new CaptionEvent(5, Integer.valueOf(paramArrayOfByte[paramInt] & 0xFF)));
      return paramInt + 1;
    case 138: 
      emitCaptionEvent(new CaptionEvent(6, Integer.valueOf(paramArrayOfByte[paramInt] & 0xFF)));
      return paramInt + 1;
    case 139: 
      emitCaptionEvent(new CaptionEvent(7, Integer.valueOf(paramArrayOfByte[paramInt] & 0xFF)));
      return paramInt + 1;
    case 140: 
      emitCaptionEvent(new CaptionEvent(8, Integer.valueOf(paramArrayOfByte[paramInt] & 0xFF)));
      return paramInt + 1;
    case 141: 
      emitCaptionEvent(new CaptionEvent(9, Integer.valueOf(paramArrayOfByte[paramInt] & 0xFF)));
      return paramInt + 1;
    case 142: 
      emitCaptionEvent(new CaptionEvent(10, null));
      return paramInt;
    case 143: 
      emitCaptionEvent(new CaptionEvent(11, null));
      return paramInt;
    case 144: 
      i = paramArrayOfByte[paramInt];
      j = paramArrayOfByte[paramInt];
      k = paramArrayOfByte[paramInt];
      if ((paramArrayOfByte[(paramInt + 1)] & 0x80) != 0)
      {
        bool1 = true;
        if ((paramArrayOfByte[(paramInt + 1)] & 0x40) == 0) {
          break label484;
        }
      }
      for (bool2 = true;; bool2 = false)
      {
        m = paramArrayOfByte[(paramInt + 1)];
        emitCaptionEvent(new CaptionEvent(12, new CaptionPenAttr(j & 0x3, (k & 0xC) >> 2, (i & 0xF0) >> 4, paramArrayOfByte[(paramInt + 1)] & 0x7, (m & 0x38) >> 3, bool2, bool1)));
        return paramInt + 2;
        bool1 = false;
        break;
      }
    case 145: 
      localCaptionColor1 = new CaptionColor((paramArrayOfByte[paramInt] & 0xC0) >> 6, (paramArrayOfByte[paramInt] & 0x30) >> 4, (paramArrayOfByte[paramInt] & 0xC) >> 2, paramArrayOfByte[paramInt] & 0x3);
      paramInt += 1;
      localCaptionColor2 = new CaptionColor((paramArrayOfByte[paramInt] & 0xC0) >> 6, (paramArrayOfByte[paramInt] & 0x30) >> 4, (paramArrayOfByte[paramInt] & 0xC) >> 2, paramArrayOfByte[paramInt] & 0x3);
      paramInt += 1;
      emitCaptionEvent(new CaptionEvent(13, new CaptionPenColor(localCaptionColor1, localCaptionColor2, new CaptionColor(0, (paramArrayOfByte[paramInt] & 0x30) >> 4, (paramArrayOfByte[paramInt] & 0xC) >> 2, paramArrayOfByte[paramInt] & 0x3))));
      return paramInt + 1;
    case 146: 
      emitCaptionEvent(new CaptionEvent(14, new CaptionPenLocation(paramArrayOfByte[paramInt] & 0xF, paramArrayOfByte[(paramInt + 1)] & 0x3F)));
      return paramInt + 2;
    case 151: 
      localCaptionColor1 = new CaptionColor((paramArrayOfByte[paramInt] & 0xC0) >> 6, (paramArrayOfByte[paramInt] & 0x30) >> 4, (paramArrayOfByte[paramInt] & 0xC) >> 2, paramArrayOfByte[paramInt] & 0x3);
      i = paramArrayOfByte[(paramInt + 1)];
      j = paramArrayOfByte[(paramInt + 2)];
      localCaptionColor2 = new CaptionColor(0, (paramArrayOfByte[(paramInt + 1)] & 0x30) >> 4, (paramArrayOfByte[(paramInt + 1)] & 0xC) >> 2, paramArrayOfByte[(paramInt + 1)] & 0x3);
      if ((paramArrayOfByte[(paramInt + 2)] & 0x40) != 0) {}
      for (bool1 = true;; bool1 = false)
      {
        k = paramArrayOfByte[(paramInt + 2)];
        m = paramArrayOfByte[(paramInt + 2)];
        n = paramArrayOfByte[(paramInt + 2)];
        i1 = paramArrayOfByte[(paramInt + 3)];
        emitCaptionEvent(new CaptionEvent(15, new CaptionWindowAttr(localCaptionColor1, localCaptionColor2, (i & 0xC0) >> 6 | (j & 0x80) >> 5, bool1, (k & 0x30) >> 4, (m & 0xC) >> 2, n & 0x3, (paramArrayOfByte[(paramInt + 3)] & 0xC) >> 2, (i1 & 0xF0) >> 4, paramArrayOfByte[(paramInt + 3)] & 0x3)));
        return paramInt + 4;
      }
    }
    int i = this.mCommand;
    label930:
    boolean bool3;
    if ((paramArrayOfByte[paramInt] & 0x20) != 0)
    {
      bool1 = true;
      if ((paramArrayOfByte[paramInt] & 0x10) == 0) {
        break label1097;
      }
      bool2 = true;
      if ((paramArrayOfByte[paramInt] & 0x8) == 0) {
        break label1103;
      }
      bool3 = true;
      label942:
      j = paramArrayOfByte[paramInt];
      if ((paramArrayOfByte[(paramInt + 1)] & 0x80) == 0) {
        break label1109;
      }
    }
    label1097:
    label1103:
    label1109:
    for (boolean bool4 = true;; bool4 = false)
    {
      k = paramArrayOfByte[(paramInt + 1)];
      m = paramArrayOfByte[(paramInt + 2)];
      n = paramArrayOfByte[(paramInt + 3)];
      i1 = paramArrayOfByte[(paramInt + 3)];
      int i2 = paramArrayOfByte[(paramInt + 4)];
      int i3 = paramArrayOfByte[(paramInt + 5)];
      emitCaptionEvent(new CaptionEvent(16, new CaptionWindow(i - 152, bool1, bool2, bool3, j & 0x7, bool4, k & 0x7F, m & 0xFF, (n & 0xF0) >> 4, i1 & 0xF, i2 & 0x3F, paramArrayOfByte[(paramInt + 5)] & 0x7, (i3 & 0x38) >> 3)));
      return paramInt + 6;
      bool1 = false;
      break;
      bool2 = false;
      break label930;
      bool3 = false;
      break label942;
    }
  }
  
  private int parseC2(byte[] paramArrayOfByte, int paramInt)
  {
    if ((this.mCommand >= 0) && (this.mCommand <= 7)) {}
    do
    {
      return paramInt;
      if ((this.mCommand >= 8) && (this.mCommand <= 15)) {
        return paramInt + 1;
      }
      if ((this.mCommand >= 16) && (this.mCommand <= 23)) {
        return paramInt + 2;
      }
    } while ((this.mCommand < 24) || (this.mCommand > 31));
    return paramInt + 3;
  }
  
  private int parseC3(byte[] paramArrayOfByte, int paramInt)
  {
    int i;
    if ((this.mCommand >= 128) && (this.mCommand <= 135)) {
      i = paramInt + 4;
    }
    do
    {
      do
      {
        return i;
        i = paramInt;
      } while (this.mCommand < 136);
      i = paramInt;
    } while (this.mCommand > 143);
    return paramInt + 5;
  }
  
  private int parseExt1(byte[] paramArrayOfByte, int paramInt)
  {
    this.mCommand = (paramArrayOfByte[paramInt] & 0xFF);
    int i = paramInt + 1;
    if ((this.mCommand >= 0) && (this.mCommand <= 31)) {
      paramInt = parseC2(paramArrayOfByte, i);
    }
    do
    {
      do
      {
        return paramInt;
        if ((this.mCommand >= 128) && (this.mCommand <= 159)) {
          return parseC3(paramArrayOfByte, i);
        }
        if ((this.mCommand >= 32) && (this.mCommand <= 127)) {
          return parseG2(paramArrayOfByte, i);
        }
        paramInt = i;
      } while (this.mCommand < 160);
      paramInt = i;
    } while (this.mCommand > 255);
    return parseG3(paramArrayOfByte, i);
  }
  
  private int parseG0(byte[] paramArrayOfByte, int paramInt)
  {
    if (this.mCommand == 127)
    {
      this.mBuffer.append(MUSIC_NOTE_CHAR);
      return paramInt;
    }
    this.mBuffer.append((char)this.mCommand);
    return paramInt;
  }
  
  private int parseG1(byte[] paramArrayOfByte, int paramInt)
  {
    this.mBuffer.append((char)this.mCommand);
    return paramInt;
  }
  
  private int parseG2(byte[] paramArrayOfByte, int paramInt)
  {
    switch (this.mCommand)
    {
    }
    return paramInt;
  }
  
  private int parseG3(byte[] paramArrayOfByte, int paramInt)
  {
    if (this.mCommand == 160) {}
    return paramInt;
  }
  
  private int parseServiceBlockData(byte[] paramArrayOfByte, int paramInt)
  {
    this.mCommand = (paramArrayOfByte[paramInt] & 0xFF);
    int i = paramInt + 1;
    if (this.mCommand == 16) {
      paramInt = parseExt1(paramArrayOfByte, i);
    }
    do
    {
      do
      {
        return paramInt;
        if ((this.mCommand >= 0) && (this.mCommand <= 31)) {
          return parseC0(paramArrayOfByte, i);
        }
        if ((this.mCommand >= 128) && (this.mCommand <= 159)) {
          return parseC1(paramArrayOfByte, i);
        }
        if ((this.mCommand >= 32) && (this.mCommand <= 127)) {
          return parseG0(paramArrayOfByte, i);
        }
        paramInt = i;
      } while (this.mCommand < 160);
      paramInt = i;
    } while (this.mCommand > 255);
    return parseG1(paramArrayOfByte, i);
  }
  
  public void parse(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i = parseServiceBlockData(paramArrayOfByte, i)) {}
    emitCaptionBuffer();
  }
  
  public static class CaptionColor
  {
    private static final int[] COLOR_MAP = { 0, 15, 240, 255 };
    public static final int OPACITY_FLASH = 1;
    private static final int[] OPACITY_MAP = { 255, 254, 128, 0 };
    public static final int OPACITY_SOLID = 0;
    public static final int OPACITY_TRANSLUCENT = 2;
    public static final int OPACITY_TRANSPARENT = 3;
    public final int blue;
    public final int green;
    public final int opacity;
    public final int red;
    
    public CaptionColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.opacity = paramInt1;
      this.red = paramInt2;
      this.green = paramInt3;
      this.blue = paramInt4;
    }
    
    public int getArgbValue()
    {
      return Color.argb(OPACITY_MAP[this.opacity], COLOR_MAP[this.red], COLOR_MAP[this.green], COLOR_MAP[this.blue]);
    }
  }
  
  public static class CaptionEvent
  {
    public final Object obj;
    public final int type;
    
    public CaptionEvent(int paramInt, Object paramObject)
    {
      this.type = paramInt;
      this.obj = paramObject;
    }
  }
  
  public static class CaptionPenAttr
  {
    public static final int OFFSET_NORMAL = 1;
    public static final int OFFSET_SUBSCRIPT = 0;
    public static final int OFFSET_SUPERSCRIPT = 2;
    public static final int PEN_SIZE_LARGE = 2;
    public static final int PEN_SIZE_SMALL = 0;
    public static final int PEN_SIZE_STANDARD = 1;
    public final int edgeType;
    public final int fontTag;
    public final boolean italic;
    public final int penOffset;
    public final int penSize;
    public final int textTag;
    public final boolean underline;
    
    public CaptionPenAttr(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.penSize = paramInt1;
      this.penOffset = paramInt2;
      this.textTag = paramInt3;
      this.fontTag = paramInt4;
      this.edgeType = paramInt5;
      this.underline = paramBoolean1;
      this.italic = paramBoolean2;
    }
  }
  
  public static class CaptionPenColor
  {
    public final Cea708CCParser.CaptionColor backgroundColor;
    public final Cea708CCParser.CaptionColor edgeColor;
    public final Cea708CCParser.CaptionColor foregroundColor;
    
    public CaptionPenColor(Cea708CCParser.CaptionColor paramCaptionColor1, Cea708CCParser.CaptionColor paramCaptionColor2, Cea708CCParser.CaptionColor paramCaptionColor3)
    {
      this.foregroundColor = paramCaptionColor1;
      this.backgroundColor = paramCaptionColor2;
      this.edgeColor = paramCaptionColor3;
    }
  }
  
  public static class CaptionPenLocation
  {
    public final int column;
    public final int row;
    
    public CaptionPenLocation(int paramInt1, int paramInt2)
    {
      this.row = paramInt1;
      this.column = paramInt2;
    }
  }
  
  public static class CaptionWindow
  {
    public final int anchorHorizontal;
    public final int anchorId;
    public final int anchorVertical;
    public final int columnCount;
    public final boolean columnLock;
    public final int id;
    public final int penStyle;
    public final int priority;
    public final boolean relativePositioning;
    public final int rowCount;
    public final boolean rowLock;
    public final boolean visible;
    public final int windowStyle;
    
    public CaptionWindow(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, boolean paramBoolean4, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
    {
      this.id = paramInt1;
      this.visible = paramBoolean1;
      this.rowLock = paramBoolean2;
      this.columnLock = paramBoolean3;
      this.priority = paramInt2;
      this.relativePositioning = paramBoolean4;
      this.anchorVertical = paramInt3;
      this.anchorHorizontal = paramInt4;
      this.anchorId = paramInt5;
      this.rowCount = paramInt6;
      this.columnCount = paramInt7;
      this.penStyle = paramInt8;
      this.windowStyle = paramInt9;
    }
  }
  
  public static class CaptionWindowAttr
  {
    public final Cea708CCParser.CaptionColor borderColor;
    public final int borderType;
    public final int displayEffect;
    public final int effectDirection;
    public final int effectSpeed;
    public final Cea708CCParser.CaptionColor fillColor;
    public final int justify;
    public final int printDirection;
    public final int scrollDirection;
    public final boolean wordWrap;
    
    public CaptionWindowAttr(Cea708CCParser.CaptionColor paramCaptionColor1, Cea708CCParser.CaptionColor paramCaptionColor2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      this.fillColor = paramCaptionColor1;
      this.borderColor = paramCaptionColor2;
      this.borderType = paramInt1;
      this.wordWrap = paramBoolean;
      this.printDirection = paramInt2;
      this.scrollDirection = paramInt3;
      this.justify = paramInt4;
      this.effectDirection = paramInt5;
      this.effectSpeed = paramInt6;
      this.displayEffect = paramInt7;
    }
  }
  
  private static class Const
  {
    public static final int CODE_C0_BS = 8;
    public static final int CODE_C0_CR = 13;
    public static final int CODE_C0_ETX = 3;
    public static final int CODE_C0_EXT1 = 16;
    public static final int CODE_C0_FF = 12;
    public static final int CODE_C0_HCR = 14;
    public static final int CODE_C0_NUL = 0;
    public static final int CODE_C0_P16 = 24;
    public static final int CODE_C0_RANGE_END = 31;
    public static final int CODE_C0_RANGE_START = 0;
    public static final int CODE_C0_SKIP1_RANGE_END = 23;
    public static final int CODE_C0_SKIP1_RANGE_START = 16;
    public static final int CODE_C0_SKIP2_RANGE_END = 31;
    public static final int CODE_C0_SKIP2_RANGE_START = 24;
    public static final int CODE_C1_CLW = 136;
    public static final int CODE_C1_CW0 = 128;
    public static final int CODE_C1_CW1 = 129;
    public static final int CODE_C1_CW2 = 130;
    public static final int CODE_C1_CW3 = 131;
    public static final int CODE_C1_CW4 = 132;
    public static final int CODE_C1_CW5 = 133;
    public static final int CODE_C1_CW6 = 134;
    public static final int CODE_C1_CW7 = 135;
    public static final int CODE_C1_DF0 = 152;
    public static final int CODE_C1_DF1 = 153;
    public static final int CODE_C1_DF2 = 154;
    public static final int CODE_C1_DF3 = 155;
    public static final int CODE_C1_DF4 = 156;
    public static final int CODE_C1_DF5 = 157;
    public static final int CODE_C1_DF6 = 158;
    public static final int CODE_C1_DF7 = 159;
    public static final int CODE_C1_DLC = 142;
    public static final int CODE_C1_DLW = 140;
    public static final int CODE_C1_DLY = 141;
    public static final int CODE_C1_DSW = 137;
    public static final int CODE_C1_HDW = 138;
    public static final int CODE_C1_RANGE_END = 159;
    public static final int CODE_C1_RANGE_START = 128;
    public static final int CODE_C1_RST = 143;
    public static final int CODE_C1_SPA = 144;
    public static final int CODE_C1_SPC = 145;
    public static final int CODE_C1_SPL = 146;
    public static final int CODE_C1_SWA = 151;
    public static final int CODE_C1_TGW = 139;
    public static final int CODE_C2_RANGE_END = 31;
    public static final int CODE_C2_RANGE_START = 0;
    public static final int CODE_C2_SKIP0_RANGE_END = 7;
    public static final int CODE_C2_SKIP0_RANGE_START = 0;
    public static final int CODE_C2_SKIP1_RANGE_END = 15;
    public static final int CODE_C2_SKIP1_RANGE_START = 8;
    public static final int CODE_C2_SKIP2_RANGE_END = 23;
    public static final int CODE_C2_SKIP2_RANGE_START = 16;
    public static final int CODE_C2_SKIP3_RANGE_END = 31;
    public static final int CODE_C2_SKIP3_RANGE_START = 24;
    public static final int CODE_C3_RANGE_END = 159;
    public static final int CODE_C3_RANGE_START = 128;
    public static final int CODE_C3_SKIP4_RANGE_END = 135;
    public static final int CODE_C3_SKIP4_RANGE_START = 128;
    public static final int CODE_C3_SKIP5_RANGE_END = 143;
    public static final int CODE_C3_SKIP5_RANGE_START = 136;
    public static final int CODE_G0_MUSICNOTE = 127;
    public static final int CODE_G0_RANGE_END = 127;
    public static final int CODE_G0_RANGE_START = 32;
    public static final int CODE_G1_RANGE_END = 255;
    public static final int CODE_G1_RANGE_START = 160;
    public static final int CODE_G2_BLK = 48;
    public static final int CODE_G2_NBTSP = 33;
    public static final int CODE_G2_RANGE_END = 127;
    public static final int CODE_G2_RANGE_START = 32;
    public static final int CODE_G2_TSP = 32;
    public static final int CODE_G3_CC = 160;
    public static final int CODE_G3_RANGE_END = 255;
    public static final int CODE_G3_RANGE_START = 160;
  }
  
  static abstract interface DisplayListener
  {
    public abstract void emitEvent(Cea708CCParser.CaptionEvent paramCaptionEvent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Cea708CCParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */