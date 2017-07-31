package android.content.res;

import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParserException;

final class XmlBlock
{
  private static final boolean DEBUG = false;
  private final AssetManager mAssets;
  private final long mNative;
  private boolean mOpen = true;
  private int mOpenCount = 1;
  final StringBlock mStrings;
  
  XmlBlock(AssetManager paramAssetManager, long paramLong)
  {
    this.mAssets = paramAssetManager;
    this.mNative = paramLong;
    this.mStrings = new StringBlock(nativeGetStringBlock(paramLong), false);
  }
  
  public XmlBlock(byte[] paramArrayOfByte)
  {
    this.mAssets = null;
    this.mNative = nativeCreate(paramArrayOfByte, 0, paramArrayOfByte.length);
    this.mStrings = new StringBlock(nativeGetStringBlock(this.mNative), false);
  }
  
  public XmlBlock(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.mAssets = null;
    this.mNative = nativeCreate(paramArrayOfByte, paramInt1, paramInt2);
    this.mStrings = new StringBlock(nativeGetStringBlock(this.mNative), false);
  }
  
  private void decOpenCountLocked()
  {
    this.mOpenCount -= 1;
    if (this.mOpenCount == 0)
    {
      nativeDestroy(this.mNative);
      if (this.mAssets != null) {
        this.mAssets.xmlBlockGone(hashCode());
      }
    }
  }
  
  private static final native long nativeCreate(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static final native long nativeCreateParseState(long paramLong);
  
  private static final native void nativeDestroy(long paramLong);
  
  private static final native void nativeDestroyParseState(long paramLong);
  
  private static final native int nativeGetAttributeCount(long paramLong);
  
  private static final native int nativeGetAttributeData(long paramLong, int paramInt);
  
  private static final native int nativeGetAttributeDataType(long paramLong, int paramInt);
  
  private static final native int nativeGetAttributeIndex(long paramLong, String paramString1, String paramString2);
  
  private static final native int nativeGetAttributeName(long paramLong, int paramInt);
  
  private static final native int nativeGetAttributeNamespace(long paramLong, int paramInt);
  
  private static final native int nativeGetAttributeResource(long paramLong, int paramInt);
  
  private static final native int nativeGetAttributeStringValue(long paramLong, int paramInt);
  
  private static final native int nativeGetClassAttribute(long paramLong);
  
  private static final native int nativeGetIdAttribute(long paramLong);
  
  private static final native int nativeGetLineNumber(long paramLong);
  
  static final native int nativeGetName(long paramLong);
  
  private static final native int nativeGetNamespace(long paramLong);
  
  private static final native long nativeGetStringBlock(long paramLong);
  
  private static final native int nativeGetStyleAttribute(long paramLong);
  
  private static final native int nativeGetText(long paramLong);
  
  static final native int nativeNext(long paramLong);
  
  public void close()
  {
    try
    {
      if (this.mOpen)
      {
        this.mOpen = false;
        decOpenCountLocked();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    close();
  }
  
  public XmlResourceParser newParser()
  {
    try
    {
      if (this.mNative != 0L)
      {
        Parser localParser = new Parser(nativeCreateParseState(this.mNative), this);
        return localParser;
      }
      return null;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  final class Parser
    implements XmlResourceParser
  {
    private final XmlBlock mBlock;
    private boolean mDecNextDepth = false;
    private int mDepth = 0;
    private int mEventType = 0;
    long mParseState;
    private boolean mStarted = false;
    
    Parser(long paramLong, XmlBlock paramXmlBlock)
    {
      this.mParseState = paramLong;
      this.mBlock = paramXmlBlock;
      XmlBlock.-set0(paramXmlBlock, XmlBlock.-get0(paramXmlBlock) + 1);
    }
    
    public void close()
    {
      synchronized (this.mBlock)
      {
        if (this.mParseState != 0L)
        {
          XmlBlock.-wrap15(this.mParseState);
          this.mParseState = 0L;
          XmlBlock.-wrap14(this.mBlock);
        }
        return;
      }
    }
    
    public void defineEntityReplacementText(String paramString1, String paramString2)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("defineEntityReplacementText() not supported");
    }
    
    protected void finalize()
      throws Throwable
    {
      close();
    }
    
    public boolean getAttributeBooleanValue(int paramInt, boolean paramBoolean)
    {
      boolean bool = false;
      int i = XmlBlock.-wrap1(this.mParseState, paramInt);
      if ((i >= 16) && (i <= 31))
      {
        paramBoolean = bool;
        if (XmlBlock.-wrap2(this.mParseState, paramInt) != 0) {
          paramBoolean = true;
        }
        return paramBoolean;
      }
      return paramBoolean;
    }
    
    public boolean getAttributeBooleanValue(String paramString1, String paramString2, boolean paramBoolean)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeBooleanValue(i, paramBoolean);
      }
      return paramBoolean;
    }
    
    public int getAttributeCount()
    {
      if (this.mEventType == 2) {
        return XmlBlock.-wrap0(this.mParseState);
      }
      return -1;
    }
    
    public float getAttributeFloatValue(int paramInt, float paramFloat)
    {
      if (XmlBlock.-wrap1(this.mParseState, paramInt) == 4) {
        return Float.intBitsToFloat(XmlBlock.-wrap2(this.mParseState, paramInt));
      }
      throw new RuntimeException("not a float!");
    }
    
    public float getAttributeFloatValue(String paramString1, String paramString2, float paramFloat)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeFloatValue(i, paramFloat);
      }
      return paramFloat;
    }
    
    public int getAttributeIntValue(int paramInt1, int paramInt2)
    {
      int i = XmlBlock.-wrap1(this.mParseState, paramInt1);
      if ((i >= 16) && (i <= 31)) {
        return XmlBlock.-wrap2(this.mParseState, paramInt1);
      }
      return paramInt2;
    }
    
    public int getAttributeIntValue(String paramString1, String paramString2, int paramInt)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeIntValue(i, paramInt);
      }
      return paramInt;
    }
    
    public int getAttributeListValue(int paramInt1, String[] paramArrayOfString, int paramInt2)
    {
      int i = XmlBlock.-wrap1(this.mParseState, paramInt1);
      paramInt1 = XmlBlock.-wrap2(this.mParseState, paramInt1);
      if (i == 3) {
        return XmlUtils.convertValueToList(XmlBlock.this.mStrings.get(paramInt1), paramArrayOfString, paramInt2);
      }
      return paramInt1;
    }
    
    public int getAttributeListValue(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeListValue(i, paramArrayOfString, paramInt);
      }
      return paramInt;
    }
    
    public String getAttributeName(int paramInt)
    {
      int i = XmlBlock.-wrap4(this.mParseState, paramInt);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      throw new IndexOutOfBoundsException(String.valueOf(paramInt));
    }
    
    public int getAttributeNameResource(int paramInt)
    {
      return XmlBlock.-wrap6(this.mParseState, paramInt);
    }
    
    public String getAttributeNamespace(int paramInt)
    {
      int i = XmlBlock.-wrap5(this.mParseState, paramInt);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      if (i == -1) {
        return "";
      }
      throw new IndexOutOfBoundsException(String.valueOf(paramInt));
    }
    
    public String getAttributePrefix(int paramInt)
    {
      throw new RuntimeException("getAttributePrefix not supported");
    }
    
    public int getAttributeResourceValue(int paramInt1, int paramInt2)
    {
      if (XmlBlock.-wrap1(this.mParseState, paramInt1) == 1) {
        return XmlBlock.-wrap2(this.mParseState, paramInt1);
      }
      return paramInt2;
    }
    
    public int getAttributeResourceValue(String paramString1, String paramString2, int paramInt)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeResourceValue(i, paramInt);
      }
      return paramInt;
    }
    
    public String getAttributeType(int paramInt)
    {
      return "CDATA";
    }
    
    public int getAttributeUnsignedIntValue(int paramInt1, int paramInt2)
    {
      int i = XmlBlock.-wrap1(this.mParseState, paramInt1);
      if ((i >= 16) && (i <= 31)) {
        return XmlBlock.-wrap2(this.mParseState, paramInt1);
      }
      return paramInt2;
    }
    
    public int getAttributeUnsignedIntValue(String paramString1, String paramString2, int paramInt)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeUnsignedIntValue(i, paramInt);
      }
      return paramInt;
    }
    
    public String getAttributeValue(int paramInt)
    {
      int i = XmlBlock.-wrap7(this.mParseState, paramInt);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      i = XmlBlock.-wrap1(this.mParseState, paramInt);
      if (i == 0) {
        throw new IndexOutOfBoundsException(String.valueOf(paramInt));
      }
      return TypedValue.coerceToString(i, XmlBlock.-wrap2(this.mParseState, paramInt));
    }
    
    public String getAttributeValue(String paramString1, String paramString2)
    {
      int i = XmlBlock.-wrap3(this.mParseState, paramString1, paramString2);
      if (i >= 0) {
        return getAttributeValue(i);
      }
      return null;
    }
    
    public String getClassAttribute()
    {
      int i = XmlBlock.-wrap8(this.mParseState);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      return null;
    }
    
    public int getColumnNumber()
    {
      return -1;
    }
    
    public int getDepth()
    {
      return this.mDepth;
    }
    
    public int getEventType()
      throws XmlPullParserException
    {
      return this.mEventType;
    }
    
    public boolean getFeature(String paramString)
    {
      if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(paramString)) {
        return true;
      }
      return "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(paramString);
    }
    
    public String getIdAttribute()
    {
      int i = XmlBlock.-wrap9(this.mParseState);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      return null;
    }
    
    public int getIdAttributeResourceValue(int paramInt)
    {
      return getAttributeResourceValue(null, "id", paramInt);
    }
    
    public String getInputEncoding()
    {
      return null;
    }
    
    public int getLineNumber()
    {
      return XmlBlock.-wrap10(this.mParseState);
    }
    
    public String getName()
    {
      int i = XmlBlock.nativeGetName(this.mParseState);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      return null;
    }
    
    public String getNamespace()
    {
      int i = XmlBlock.-wrap11(this.mParseState);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      return "";
    }
    
    public String getNamespace(String paramString)
    {
      throw new RuntimeException("getNamespace() not supported");
    }
    
    public int getNamespaceCount(int paramInt)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("getNamespaceCount() not supported");
    }
    
    public String getNamespacePrefix(int paramInt)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("getNamespacePrefix() not supported");
    }
    
    public String getNamespaceUri(int paramInt)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("getNamespaceUri() not supported");
    }
    
    final CharSequence getPooledString(int paramInt)
    {
      return XmlBlock.this.mStrings.get(paramInt);
    }
    
    public String getPositionDescription()
    {
      return "Binary XML file line #" + getLineNumber();
    }
    
    public String getPrefix()
    {
      throw new RuntimeException("getPrefix not supported");
    }
    
    public Object getProperty(String paramString)
    {
      return null;
    }
    
    public int getStyleAttribute()
    {
      return XmlBlock.-wrap12(this.mParseState);
    }
    
    public String getText()
    {
      int i = XmlBlock.-wrap13(this.mParseState);
      if (i >= 0) {
        return XmlBlock.this.mStrings.get(i).toString();
      }
      return null;
    }
    
    public char[] getTextCharacters(int[] paramArrayOfInt)
    {
      String str = getText();
      char[] arrayOfChar = null;
      if (str != null)
      {
        paramArrayOfInt[0] = 0;
        paramArrayOfInt[1] = str.length();
        arrayOfChar = new char[str.length()];
        str.getChars(0, str.length(), arrayOfChar, 0);
      }
      return arrayOfChar;
    }
    
    public boolean isAttributeDefault(int paramInt)
    {
      return false;
    }
    
    public boolean isEmptyElementTag()
      throws XmlPullParserException
    {
      return false;
    }
    
    public boolean isWhitespace()
      throws XmlPullParserException
    {
      return false;
    }
    
    public int next()
      throws XmlPullParserException, IOException
    {
      if (!this.mStarted)
      {
        this.mStarted = true;
        return 0;
      }
      if (this.mParseState == 0L) {
        return 1;
      }
      int i = XmlBlock.nativeNext(this.mParseState);
      if (this.mDecNextDepth)
      {
        this.mDepth -= 1;
        this.mDecNextDepth = false;
      }
      switch (i)
      {
      }
      for (;;)
      {
        this.mEventType = i;
        if (i == 1) {
          close();
        }
        return i;
        this.mDepth += 1;
        continue;
        this.mDecNextDepth = true;
      }
    }
    
    public int nextTag()
      throws XmlPullParserException, IOException
    {
      int j = next();
      int i = j;
      if (j == 4)
      {
        i = j;
        if (isWhitespace()) {
          i = next();
        }
      }
      if ((i != 2) && (i != 3)) {
        throw new XmlPullParserException(getPositionDescription() + ": expected start or end tag", this, null);
      }
      return i;
    }
    
    public String nextText()
      throws XmlPullParserException, IOException
    {
      if (getEventType() != 2) {
        throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG to read next text", this, null);
      }
      int i = next();
      if (i == 4)
      {
        String str = getText();
        if (next() != 3) {
          throw new XmlPullParserException(getPositionDescription() + ": event TEXT it must be immediately followed by END_TAG", this, null);
        }
        return str;
      }
      if (i == 3) {
        return "";
      }
      throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG or TEXT to read text", this, null);
    }
    
    public int nextToken()
      throws XmlPullParserException, IOException
    {
      return next();
    }
    
    public void require(int paramInt, String paramString1, String paramString2)
      throws XmlPullParserException, IOException
    {
      if ((paramInt == getEventType()) && ((paramString1 == null) || (paramString1.equals(getNamespace()))) && ((paramString2 == null) || (paramString2.equals(getName())))) {
        return;
      }
      throw new XmlPullParserException("expected " + TYPES[paramInt] + getPositionDescription());
    }
    
    public void setFeature(String paramString, boolean paramBoolean)
      throws XmlPullParserException
    {
      if (("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(paramString)) && (paramBoolean)) {
        return;
      }
      if (("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(paramString)) && (paramBoolean)) {
        return;
      }
      throw new XmlPullParserException("Unsupported feature: " + paramString);
    }
    
    public void setInput(InputStream paramInputStream, String paramString)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("setInput() not supported");
    }
    
    public void setInput(Reader paramReader)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("setInput() not supported");
    }
    
    public void setProperty(String paramString, Object paramObject)
      throws XmlPullParserException
    {
      throw new XmlPullParserException("setProperty() not supported");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/XmlBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */