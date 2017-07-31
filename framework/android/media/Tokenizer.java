package android.media;

import android.util.Log;

class Tokenizer
{
  private static final String TAG = "Tokenizer";
  private TokenizerPhase mDataTokenizer = new DataTokenizer();
  private int mHandledLen;
  private String mLine;
  private OnTokenListener mListener;
  private TokenizerPhase mPhase;
  private TokenizerPhase mTagTokenizer = new TagTokenizer();
  
  Tokenizer(OnTokenListener paramOnTokenListener)
  {
    reset();
    this.mListener = paramOnTokenListener;
  }
  
  void reset()
  {
    this.mPhase = this.mDataTokenizer.start();
  }
  
  void tokenize(String paramString)
  {
    this.mHandledLen = 0;
    this.mLine = paramString;
    while (this.mHandledLen < this.mLine.length()) {
      this.mPhase.tokenize();
    }
    if (!(this.mPhase instanceof TagTokenizer)) {
      this.mListener.onLineEnd();
    }
  }
  
  class DataTokenizer
    implements Tokenizer.TokenizerPhase
  {
    private StringBuilder mData;
    
    DataTokenizer() {}
    
    private boolean replaceEscape(String paramString1, String paramString2, int paramInt)
    {
      if (Tokenizer.-get2(Tokenizer.this).startsWith(paramString1, paramInt))
      {
        this.mData.append(Tokenizer.-get2(Tokenizer.this).substring(Tokenizer.-get1(Tokenizer.this), paramInt));
        this.mData.append(paramString2);
        Tokenizer.-set0(Tokenizer.this, paramString1.length() + paramInt);
        Tokenizer.-get1(Tokenizer.this);
        return true;
      }
      return false;
    }
    
    public Tokenizer.TokenizerPhase start()
    {
      this.mData = new StringBuilder();
      return this;
    }
    
    public void tokenize()
    {
      int k = Tokenizer.-get2(Tokenizer.this).length();
      int i = Tokenizer.-get1(Tokenizer.this);
      int j = k;
      if (i < Tokenizer.-get2(Tokenizer.this).length())
      {
        if (Tokenizer.-get2(Tokenizer.this).charAt(i) == '&') {
          if ((replaceEscape("&amp;", "&", i)) || (replaceEscape("&lt;", "<", i)) || (replaceEscape("&gt;", ">", i)) || (replaceEscape("&lrm;", "‎", i)) || (replaceEscape("&rlm;", "‏", i)) || (!replaceEscape("&nbsp;", " ", i))) {}
        }
        while (Tokenizer.-get2(Tokenizer.this).charAt(i) != '<')
        {
          i += 1;
          break;
        }
        Tokenizer.-set1(Tokenizer.this, Tokenizer.-get4(Tokenizer.this).start());
        j = i;
      }
      this.mData.append(Tokenizer.-get2(Tokenizer.this).substring(Tokenizer.-get1(Tokenizer.this), j));
      Tokenizer.-get3(Tokenizer.this).onData(this.mData.toString());
      this.mData.delete(0, this.mData.length());
      Tokenizer.-set0(Tokenizer.this, j);
    }
  }
  
  static abstract interface OnTokenListener
  {
    public abstract void onData(String paramString);
    
    public abstract void onEnd(String paramString);
    
    public abstract void onLineEnd();
    
    public abstract void onStart(String paramString1, String[] paramArrayOfString, String paramString2);
    
    public abstract void onTimeStamp(long paramLong);
  }
  
  class TagTokenizer
    implements Tokenizer.TokenizerPhase
  {
    private String mAnnotation;
    private boolean mAtAnnotation;
    private String mName;
    
    TagTokenizer() {}
    
    private void yield_tag()
    {
      if (this.mName.startsWith("/"))
      {
        Tokenizer.-get3(Tokenizer.this).onEnd(this.mName.substring(1));
        return;
      }
      if ((this.mName.length() > 0) && (Character.isDigit(this.mName.charAt(0)))) {
        try
        {
          long l = WebVttParser.parseTimestampMs(this.mName);
          Tokenizer.-get3(Tokenizer.this).onTimeStamp(l);
          return;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Log.d("Tokenizer", "invalid timestamp tag: <" + this.mName + ">");
          return;
        }
      }
      this.mAnnotation = this.mAnnotation.replaceAll("\\s+", " ");
      if (this.mAnnotation.startsWith(" ")) {
        this.mAnnotation = this.mAnnotation.substring(1);
      }
      if (this.mAnnotation.endsWith(" ")) {
        this.mAnnotation = this.mAnnotation.substring(0, this.mAnnotation.length() - 1);
      }
      String[] arrayOfString = null;
      int i = this.mName.indexOf('.');
      if (i >= 0)
      {
        arrayOfString = this.mName.substring(i + 1).split("\\.");
        this.mName = this.mName.substring(0, i);
      }
      Tokenizer.-get3(Tokenizer.this).onStart(this.mName, arrayOfString, this.mAnnotation);
    }
    
    public Tokenizer.TokenizerPhase start()
    {
      this.mAnnotation = "";
      this.mName = "";
      this.mAtAnnotation = false;
      return this;
    }
    
    public void tokenize()
    {
      Object localObject;
      if (!this.mAtAnnotation)
      {
        localObject = Tokenizer.this;
        Tokenizer.-set0((Tokenizer)localObject, Tokenizer.-get1((Tokenizer)localObject) + 1);
      }
      String str;
      if (Tokenizer.-get1(Tokenizer.this) < Tokenizer.-get2(Tokenizer.this).length())
      {
        if ((!this.mAtAnnotation) && (Tokenizer.-get2(Tokenizer.this).charAt(Tokenizer.-get1(Tokenizer.this)) != '/')) {
          break label273;
        }
        localObject = Tokenizer.-get2(Tokenizer.this).substring(Tokenizer.-get1(Tokenizer.this)).split(">");
        str = Tokenizer.-get2(Tokenizer.this).substring(Tokenizer.-get1(Tokenizer.this), Tokenizer.-get1(Tokenizer.this) + localObject[0].length());
        Tokenizer localTokenizer = Tokenizer.this;
        Tokenizer.-set0(localTokenizer, Tokenizer.-get1(localTokenizer) + localObject[0].length());
        if (!this.mAtAnnotation) {
          break label299;
        }
        this.mAnnotation = (this.mAnnotation + " " + str);
      }
      for (;;)
      {
        this.mAtAnnotation = true;
        if ((Tokenizer.-get1(Tokenizer.this) < Tokenizer.-get2(Tokenizer.this).length()) && (Tokenizer.-get2(Tokenizer.this).charAt(Tokenizer.-get1(Tokenizer.this)) == '>'))
        {
          yield_tag();
          Tokenizer.-set1(Tokenizer.this, Tokenizer.-get0(Tokenizer.this).start());
          localObject = Tokenizer.this;
          Tokenizer.-set0((Tokenizer)localObject, Tokenizer.-get1((Tokenizer)localObject) + 1);
        }
        return;
        label273:
        localObject = Tokenizer.-get2(Tokenizer.this).substring(Tokenizer.-get1(Tokenizer.this)).split("[\t\f >]");
        break;
        label299:
        this.mName = str;
      }
    }
  }
  
  static abstract interface TokenizerPhase
  {
    public abstract TokenizerPhase start();
    
    public abstract void tokenize();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Tokenizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */