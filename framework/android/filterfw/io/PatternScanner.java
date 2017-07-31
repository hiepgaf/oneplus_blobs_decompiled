package android.filterfw.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternScanner
{
  private Pattern mIgnorePattern;
  private String mInput;
  private int mLineNo = 0;
  private int mOffset = 0;
  private int mStartOfLine = 0;
  
  public PatternScanner(String paramString)
  {
    this.mInput = paramString;
  }
  
  public PatternScanner(String paramString, Pattern paramPattern)
  {
    this.mInput = paramString;
    this.mIgnorePattern = paramPattern;
    skip(this.mIgnorePattern);
  }
  
  public boolean atEnd()
  {
    return this.mOffset >= this.mInput.length();
  }
  
  public String eat(Pattern paramPattern, String paramString)
  {
    paramPattern = tryEat(paramPattern);
    if (paramPattern == null) {
      throw new RuntimeException(unexpectedTokenMessage(paramString));
    }
    return paramPattern;
  }
  
  public int lineNo()
  {
    return this.mLineNo;
  }
  
  public boolean peek(Pattern paramPattern)
  {
    if (this.mIgnorePattern != null) {
      skip(this.mIgnorePattern);
    }
    paramPattern = paramPattern.matcher(this.mInput);
    paramPattern.region(this.mOffset, this.mInput.length());
    return paramPattern.lookingAt();
  }
  
  public void skip(Pattern paramPattern)
  {
    paramPattern = paramPattern.matcher(this.mInput);
    paramPattern.region(this.mOffset, this.mInput.length());
    if (paramPattern.lookingAt())
    {
      updateLineCount(this.mOffset, paramPattern.end());
      this.mOffset = paramPattern.end();
    }
  }
  
  public String tryEat(Pattern paramPattern)
  {
    if (this.mIgnorePattern != null) {
      skip(this.mIgnorePattern);
    }
    Matcher localMatcher = paramPattern.matcher(this.mInput);
    localMatcher.region(this.mOffset, this.mInput.length());
    paramPattern = null;
    if (localMatcher.lookingAt())
    {
      updateLineCount(this.mOffset, localMatcher.end());
      this.mOffset = localMatcher.end();
      paramPattern = this.mInput.substring(localMatcher.start(), localMatcher.end());
    }
    if ((paramPattern != null) && (this.mIgnorePattern != null)) {
      skip(this.mIgnorePattern);
    }
    return paramPattern;
  }
  
  public String unexpectedTokenMessage(String paramString)
  {
    String str = this.mInput.substring(this.mStartOfLine, this.mOffset);
    return "Unexpected token on line " + (this.mLineNo + 1) + " after '" + str + "' <- Expected " + paramString + "!";
  }
  
  public void updateLineCount(int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      if (this.mInput.charAt(paramInt1) == '\n')
      {
        this.mLineNo += 1;
        this.mStartOfLine = (paramInt1 + 1);
      }
      paramInt1 += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/io/PatternScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */