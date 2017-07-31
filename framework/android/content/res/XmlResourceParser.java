package android.content.res;

import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public abstract interface XmlResourceParser
  extends XmlPullParser, AttributeSet, AutoCloseable
{
  public abstract void close();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/XmlResourceParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */