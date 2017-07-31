package android.filterfw.core;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class KeyValueMap
  extends HashMap<String, Object>
{
  public static KeyValueMap fromKeyValues(Object... paramVarArgs)
  {
    KeyValueMap localKeyValueMap = new KeyValueMap();
    localKeyValueMap.setKeyValues(paramVarArgs);
    return localKeyValueMap;
  }
  
  public float getFloat(String paramString)
  {
    paramString = get(paramString);
    if (paramString != null) {}
    for (paramString = (Float)paramString;; paramString = null) {
      return paramString.floatValue();
    }
  }
  
  public int getInt(String paramString)
  {
    paramString = get(paramString);
    if (paramString != null) {}
    for (paramString = (Integer)paramString;; paramString = null) {
      return paramString.intValue();
    }
  }
  
  public String getString(String paramString)
  {
    paramString = get(paramString);
    if (paramString != null) {
      return (String)paramString;
    }
    return null;
  }
  
  public void setKeyValues(Object... paramVarArgs)
  {
    if (paramVarArgs.length % 2 != 0) {
      throw new RuntimeException("Key-Value arguments passed into setKeyValues must be an alternating list of keys and values!");
    }
    int i = 0;
    while (i < paramVarArgs.length)
    {
      if (!(paramVarArgs[i] instanceof String)) {
        throw new RuntimeException("Key-value argument " + i + " must be a key of type " + "String, but found an object of type " + paramVarArgs[i].getClass() + "!");
      }
      put((String)paramVarArgs[i], paramVarArgs[(i + 1)]);
      i += 2;
    }
  }
  
  public String toString()
  {
    StringWriter localStringWriter = new StringWriter();
    Iterator localIterator = entrySet().iterator();
    if (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject = localEntry.getValue();
      if ((localObject instanceof String)) {}
      for (localObject = "\"" + localObject + "\"";; localObject = localObject.toString())
      {
        localStringWriter.write((String)localEntry.getKey() + " = " + (String)localObject + ";\n");
        break;
      }
    }
    return localStringWriter.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/KeyValueMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */