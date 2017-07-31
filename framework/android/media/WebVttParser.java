package android.media;

import android.util.Log;
import java.util.Vector;

class WebVttParser
{
  private static final String TAG = "WebVttParser";
  private String mBuffer = "";
  private TextTrackCue mCue;
  private Vector<String> mCueTexts;
  private WebVttCueListener mListener;
  private final Phase mParseCueId = new Phase()
  {
    static
    {
      if (4.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public void parse(String paramAnonymousString)
    {
      int i = 0;
      if (paramAnonymousString.length() == 0) {
        return;
      }
      if (!-assertionsDisabled)
      {
        if (WebVttParser.-get0(WebVttParser.this) == null) {
          i = 1;
        }
        if (i == 0) {
          throw new AssertionError();
        }
      }
      if ((paramAnonymousString.equals("NOTE")) || (paramAnonymousString.startsWith("NOTE "))) {
        WebVttParser.-set1(WebVttParser.this, WebVttParser.-get4(WebVttParser.this));
      }
      WebVttParser.-set0(WebVttParser.this, new TextTrackCue());
      WebVttParser.-get1(WebVttParser.this).clear();
      WebVttParser.-set1(WebVttParser.this, WebVttParser.-get5(WebVttParser.this));
      if (paramAnonymousString.contains("-->"))
      {
        WebVttParser.-get7(WebVttParser.this).parse(paramAnonymousString);
        return;
      }
      WebVttParser.-get0(WebVttParser.this).mId = paramAnonymousString;
    }
  };
  private final Phase mParseCueText = new Phase()
  {
    public void parse(String paramAnonymousString)
    {
      if (paramAnonymousString.length() == 0)
      {
        WebVttParser.this.yieldCue();
        WebVttParser.-set1(WebVttParser.this, WebVttParser.-get3(WebVttParser.this));
        return;
      }
      if (WebVttParser.-get0(WebVttParser.this) != null) {
        WebVttParser.-get1(WebVttParser.this).add(paramAnonymousString);
      }
    }
  };
  private final Phase mParseCueTime = new Phase()
  {
    static
    {
      if (5.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public void parse(String paramAnonymousString)
    {
      int i = paramAnonymousString.indexOf("-->");
      if (i < 0)
      {
        WebVttParser.-set0(WebVttParser.this, null);
        WebVttParser.-set1(WebVttParser.this, WebVttParser.-get3(WebVttParser.this));
        return;
      }
      String str2 = paramAnonymousString.substring(0, i).trim();
      String str1 = paramAnonymousString.substring(i + 3).replaceFirst("^\\s+", "").replaceFirst("\\s+", " ");
      i = str1.indexOf(' ');
      label104:
      label147:
      int j;
      if (i > 0)
      {
        paramAnonymousString = str1.substring(0, i);
        if (i <= 0) {
          break label194;
        }
        str1 = str1.substring(i + 1);
        WebVttParser.-get0(WebVttParser.this).mStartTimeMs = WebVttParser.parseTimestampMs(str2);
        WebVttParser.-get0(WebVttParser.this).mEndTimeMs = WebVttParser.parseTimestampMs(paramAnonymousString);
        paramAnonymousString = str1.split(" +");
        int k = paramAnonymousString.length;
        i = 0;
        if (i >= k) {
          break label744;
        }
        str2 = paramAnonymousString[i];
        j = str2.indexOf(':');
        if ((j > 0) && (j != str2.length() - 1)) {
          break label201;
        }
      }
      for (;;)
      {
        i += 1;
        break label147;
        paramAnonymousString = str1;
        break;
        label194:
        str1 = "";
        break label104;
        label201:
        str1 = str2.substring(0, j);
        str2 = str2.substring(j + 1);
        if (str1.equals("region"))
        {
          WebVttParser.-get0(WebVttParser.this).mRegionId = str2;
        }
        else if (str1.equals("vertical"))
        {
          if (str2.equals("rl")) {
            WebVttParser.-get0(WebVttParser.this).mWritingDirection = 101;
          } else if (str2.equals("lr")) {
            WebVttParser.-get0(WebVttParser.this).mWritingDirection = 102;
          } else {
            WebVttParser.-wrap2(WebVttParser.this, "cue setting", str1, "has invalid value", str2);
          }
        }
        else if (str1.equals("line"))
        {
          for (;;)
          {
            try
            {
              if (-assertionsDisabled) {
                break label388;
              }
              if (str2.indexOf(' ') >= 0) {
                break label383;
              }
              j = 1;
              if (j != 0) {
                break label388;
              }
              throw new AssertionError();
            }
            catch (NumberFormatException localNumberFormatException1)
            {
              WebVttParser.-wrap2(WebVttParser.this, "cue setting", str1, "is not numeric or percentage", str2);
            }
            break;
            label383:
            j = 0;
          }
          label388:
          if (str2.endsWith("%"))
          {
            WebVttParser.-get0(WebVttParser.this).mSnapToLines = false;
            WebVttParser.-get0(WebVttParser.this).mLinePosition = Integer.valueOf(WebVttParser.parseIntPercentage(str2));
          }
          else if (str2.matches(".*[^0-9].*"))
          {
            WebVttParser.-wrap2(WebVttParser.this, "cue setting", str1, "contains an invalid character", str2);
          }
          else
          {
            WebVttParser.-get0(WebVttParser.this).mSnapToLines = true;
            WebVttParser.-get0(WebVttParser.this).mLinePosition = Integer.valueOf(Integer.parseInt(str2));
          }
        }
        else if (str1.equals("position"))
        {
          try
          {
            WebVttParser.-get0(WebVttParser.this).mTextPosition = WebVttParser.parseIntPercentage(str2);
          }
          catch (NumberFormatException localNumberFormatException2)
          {
            WebVttParser.-wrap2(WebVttParser.this, "cue setting", str1, "is not numeric or percentage", str2);
          }
        }
        else if (str1.equals("size"))
        {
          try
          {
            WebVttParser.-get0(WebVttParser.this).mSize = WebVttParser.parseIntPercentage(str2);
          }
          catch (NumberFormatException localNumberFormatException3)
          {
            WebVttParser.-wrap2(WebVttParser.this, "cue setting", str1, "is not numeric or percentage", str2);
          }
        }
        else if (str1.equals("align"))
        {
          if (str2.equals("start")) {
            WebVttParser.-get0(WebVttParser.this).mAlignment = 201;
          } else if (str2.equals("middle")) {
            WebVttParser.-get0(WebVttParser.this).mAlignment = 200;
          } else if (str2.equals("end")) {
            WebVttParser.-get0(WebVttParser.this).mAlignment = 202;
          } else if (str2.equals("left")) {
            WebVttParser.-get0(WebVttParser.this).mAlignment = 203;
          } else if (str2.equals("right")) {
            WebVttParser.-get0(WebVttParser.this).mAlignment = 204;
          } else {
            WebVttParser.-wrap2(WebVttParser.this, "cue setting", str1, "has invalid value", str2);
          }
        }
      }
      label744:
      if ((WebVttParser.-get0(WebVttParser.this).mLinePosition != null) || (WebVttParser.-get0(WebVttParser.this).mSize != 100)) {}
      for (;;)
      {
        WebVttParser.-get0(WebVttParser.this).mRegionId = "";
        do
        {
          WebVttParser.-set1(WebVttParser.this, WebVttParser.-get4(WebVttParser.this));
          return;
        } while (WebVttParser.-get0(WebVttParser.this).mWritingDirection == 100);
      }
    }
  };
  private final Phase mParseHeader = new Phase()
  {
    static
    {
      if (3.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public void parse(String paramAnonymousString)
    {
      if (paramAnonymousString.length() == 0) {
        WebVttParser.-set1(WebVttParser.this, WebVttParser.-get3(WebVttParser.this));
      }
      String str;
      do
      {
        return;
        if (paramAnonymousString.contains("-->"))
        {
          WebVttParser.-set1(WebVttParser.this, WebVttParser.-get5(WebVttParser.this));
          WebVttParser.-get7(WebVttParser.this).parse(paramAnonymousString);
          return;
        }
        int i = paramAnonymousString.indexOf(':');
        if ((i <= 0) || (i >= paramAnonymousString.length() - 1)) {
          WebVttParser.-wrap0(WebVttParser.this, "meta data header has invalid format", paramAnonymousString);
        }
        str = paramAnonymousString.substring(0, i);
        paramAnonymousString = paramAnonymousString.substring(i + 1);
      } while (!str.equals("Region"));
      paramAnonymousString = parseRegion(paramAnonymousString);
      WebVttParser.-get2(WebVttParser.this).onRegionParsed(paramAnonymousString);
    }
    
    TextTrackRegion parseRegion(String paramAnonymousString)
    {
      TextTrackRegion localTextTrackRegion = new TextTrackRegion();
      paramAnonymousString = paramAnonymousString.split(" +");
      int k = paramAnonymousString.length;
      int i = 0;
      if (i < k)
      {
        String str2 = paramAnonymousString[i];
        int j = str2.indexOf('=');
        if ((j <= 0) || (j == str2.length() - 1)) {}
        for (;;)
        {
          i += 1;
          break;
          String str1 = str2.substring(0, j);
          str2 = str2.substring(j + 1);
          if (str1.equals("id"))
          {
            localTextTrackRegion.mId = str2;
          }
          else if (str1.equals("width"))
          {
            try
            {
              localTextTrackRegion.mWidth = WebVttParser.parseFloatPercentage(str2);
            }
            catch (NumberFormatException localNumberFormatException2)
            {
              WebVttParser.-wrap1(WebVttParser.this, "region setting", str1, "has invalid value", localNumberFormatException2.getMessage(), str2);
            }
          }
          else if (str1.equals("lines"))
          {
            if (str2.matches(".*[^0-9].*")) {
              WebVttParser.-wrap2(WebVttParser.this, "lines", str1, "contains an invalid character", str2);
            } else {
              for (;;)
              {
                try
                {
                  localTextTrackRegion.mLines = Integer.parseInt(str2);
                  if (-assertionsDisabled) {
                    break;
                  }
                  if (localTextTrackRegion.mLines < 0) {
                    break label258;
                  }
                  j = 1;
                  if (j != 0) {
                    break;
                  }
                  throw new AssertionError();
                }
                catch (NumberFormatException localNumberFormatException3)
                {
                  WebVttParser.-wrap2(WebVttParser.this, "region setting", str1, "is not numeric", str2);
                }
                break;
                label258:
                j = 0;
              }
            }
          }
          else if ((str1.equals("regionanchor")) || (str1.equals("viewportanchor")))
          {
            j = str2.indexOf(",");
            if (j < 0)
            {
              WebVttParser.-wrap2(WebVttParser.this, "region setting", str1, "contains no comma", str2);
            }
            else
            {
              String str3 = str2.substring(0, j);
              str2 = str2.substring(j + 1);
              float f1;
              float f2;
              try
              {
                f1 = WebVttParser.parseFloatPercentage(str3);
              }
              catch (NumberFormatException localNumberFormatException1)
              {
                try
                {
                  f2 = WebVttParser.parseFloatPercentage(str2);
                  if (str1.charAt(0) != 'r') {
                    break label425;
                  }
                  localTextTrackRegion.mAnchorPointX = f1;
                  localTextTrackRegion.mAnchorPointY = f2;
                }
                catch (NumberFormatException localNumberFormatException4)
                {
                  WebVttParser.-wrap1(WebVttParser.this, "region setting", str1, "has invalid y component", localNumberFormatException4.getMessage(), localNumberFormatException1);
                }
                localNumberFormatException1 = localNumberFormatException1;
                WebVttParser.-wrap1(WebVttParser.this, "region setting", str1, "has invalid x component", localNumberFormatException1.getMessage(), str3);
              }
              continue;
              continue;
              label425:
              localTextTrackRegion.mViewportAnchorPointX = f1;
              localTextTrackRegion.mViewportAnchorPointY = f2;
            }
          }
          else if (str1.equals("scroll"))
          {
            if (localNumberFormatException1.equals("up")) {
              localTextTrackRegion.mScrollValue = 301;
            } else {
              WebVttParser.-wrap2(WebVttParser.this, "region setting", str1, "has invalid value", localNumberFormatException1);
            }
          }
        }
      }
      return localTextTrackRegion;
    }
  };
  private final Phase mParseStart = new Phase()
  {
    public void parse(String paramAnonymousString)
    {
      String str = paramAnonymousString;
      if (paramAnonymousString.startsWith("﻿")) {
        str = paramAnonymousString.substring(1);
      }
      if ((str.equals("WEBVTT")) || (str.startsWith("WEBVTT "))) {}
      while (str.startsWith("WEBVTT\t"))
      {
        WebVttParser.-set1(WebVttParser.this, WebVttParser.-get6(WebVttParser.this));
        return;
      }
      WebVttParser.-wrap0(WebVttParser.this, "Not a WEBVTT header", str);
      WebVttParser.-set1(WebVttParser.this, WebVttParser.-get8(WebVttParser.this));
    }
  };
  private Phase mPhase = this.mParseStart;
  private final Phase mSkipRest = new Phase()
  {
    public void parse(String paramAnonymousString) {}
  };
  
  WebVttParser(WebVttCueListener paramWebVttCueListener)
  {
    this.mListener = paramWebVttCueListener;
    this.mCueTexts = new Vector();
  }
  
  private void log_warning(String paramString1, String paramString2)
  {
    Log.w(getClass().getName(), paramString1 + " ('" + paramString2 + "')");
  }
  
  private void log_warning(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Log.w(getClass().getName(), paramString1 + " '" + paramString2 + "' " + paramString3 + " ('" + paramString4 + "')");
  }
  
  private void log_warning(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    Log.w(getClass().getName(), paramString1 + " '" + paramString2 + "' " + paramString3 + " ('" + paramString5 + "' " + paramString4 + ")");
  }
  
  public static float parseFloatPercentage(String paramString)
    throws NumberFormatException
  {
    if (!paramString.endsWith("%")) {
      throw new NumberFormatException("does not end in %");
    }
    paramString = paramString.substring(0, paramString.length() - 1);
    if (paramString.matches(".*[^0-9.].*")) {
      throw new NumberFormatException("contains an invalid character");
    }
    float f;
    try
    {
      f = Float.parseFloat(paramString);
      if ((f < 0.0F) || (f > 100.0F)) {
        throw new NumberFormatException("is out of range");
      }
    }
    catch (NumberFormatException paramString)
    {
      throw new NumberFormatException("is not a number");
    }
    return f;
  }
  
  public static int parseIntPercentage(String paramString)
    throws NumberFormatException
  {
    if (!paramString.endsWith("%")) {
      throw new NumberFormatException("does not end in %");
    }
    paramString = paramString.substring(0, paramString.length() - 1);
    if (paramString.matches(".*[^0-9].*")) {
      throw new NumberFormatException("contains an invalid character");
    }
    int i;
    try
    {
      i = Integer.parseInt(paramString);
      if ((i < 0) || (i > 100)) {
        throw new NumberFormatException("is out of range");
      }
    }
    catch (NumberFormatException paramString)
    {
      throw new NumberFormatException("is not a number");
    }
    return i;
  }
  
  public static long parseTimestampMs(String paramString)
    throws NumberFormatException
  {
    int i = 0;
    if (!paramString.matches("(\\d+:)?[0-5]\\d:[0-5]\\d\\.\\d{3}")) {
      throw new NumberFormatException("has invalid format");
    }
    paramString = paramString.split("\\.", 2);
    long l = 0L;
    String[] arrayOfString = paramString[0].split(":");
    int j = arrayOfString.length;
    while (i < j)
    {
      l = 60L * l + Long.parseLong(arrayOfString[i]);
      i += 1;
    }
    return 1000L * l + Long.parseLong(paramString[1]);
  }
  
  public static String timeToString(long paramLong)
  {
    return String.format("%d:%02d:%02d.%03d", new Object[] { Long.valueOf(paramLong / 3600000L), Long.valueOf(paramLong / 60000L % 60L), Long.valueOf(paramLong / 1000L % 60L), Long.valueOf(paramLong % 1000L) });
  }
  
  public void eos()
  {
    if (this.mBuffer.endsWith("\r")) {
      this.mBuffer = this.mBuffer.substring(0, this.mBuffer.length() - 1);
    }
    this.mPhase.parse(this.mBuffer);
    this.mBuffer = "";
    yieldCue();
    this.mPhase = this.mParseStart;
  }
  
  public void parse(String paramString)
  {
    int i = 0;
    this.mBuffer = (this.mBuffer + paramString.replace("\000", "�")).replace("\r\n", "\n");
    if (this.mBuffer.endsWith("\r"))
    {
      i = 1;
      this.mBuffer = this.mBuffer.substring(0, this.mBuffer.length() - 1);
    }
    paramString = this.mBuffer.split("[\r\n]");
    int j = 0;
    while (j < paramString.length - 1)
    {
      this.mPhase.parse(paramString[j]);
      j += 1;
    }
    this.mBuffer = paramString[(paramString.length - 1)];
    if (i != 0) {
      this.mBuffer += "\r";
    }
  }
  
  public void yieldCue()
  {
    if ((this.mCue != null) && (this.mCueTexts.size() > 0))
    {
      this.mCue.mStrings = new String[this.mCueTexts.size()];
      this.mCueTexts.toArray(this.mCue.mStrings);
      this.mCueTexts.clear();
      this.mListener.onCueParsed(this.mCue);
    }
    this.mCue = null;
  }
  
  static abstract interface Phase
  {
    public abstract void parse(String paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/WebVttParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */