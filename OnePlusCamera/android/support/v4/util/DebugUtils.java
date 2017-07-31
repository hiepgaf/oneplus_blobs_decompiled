package android.support.v4.util;

public class DebugUtils
{
  public static void buildShortClassTag(Object paramObject, StringBuilder paramStringBuilder)
  {
    String str;
    int i;
    if (paramObject != null)
    {
      str = paramObject.getClass().getSimpleName();
      if (str != null) {
        break label69;
      }
      str = paramObject.getClass().getName();
      i = str.lastIndexOf('.');
      if (i > 0) {
        break label79;
      }
    }
    for (;;)
    {
      paramStringBuilder.append(str);
      paramStringBuilder.append('{');
      paramStringBuilder.append(Integer.toHexString(System.identityHashCode(paramObject)));
      return;
      paramStringBuilder.append("null");
      return;
      label69:
      if (str.length() <= 0) {
        break;
      }
      continue;
      label79:
      str = str.substring(i + 1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/util/DebugUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */