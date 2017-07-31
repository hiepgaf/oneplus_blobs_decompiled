package android.filterfw.io;

import android.content.Context;
import android.content.res.Resources;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.KeyValueMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;

public abstract class GraphReader
{
  protected KeyValueMap mReferences = new KeyValueMap();
  
  public void addReference(String paramString, Object paramObject)
  {
    this.mReferences.put(paramString, paramObject);
  }
  
  public void addReferencesByKeysAndValues(Object... paramVarArgs)
  {
    this.mReferences.setKeyValues(paramVarArgs);
  }
  
  public void addReferencesByMap(KeyValueMap paramKeyValueMap)
  {
    this.mReferences.putAll(paramKeyValueMap);
  }
  
  public FilterGraph readGraphResource(Context paramContext, int paramInt)
    throws GraphIOException
  {
    paramContext = new InputStreamReader(paramContext.getResources().openRawResource(paramInt));
    localStringWriter = new StringWriter();
    char[] arrayOfChar = new char['Ѐ'];
    try
    {
      for (;;)
      {
        paramInt = paramContext.read(arrayOfChar, 0, 1024);
        if (paramInt <= 0) {
          break;
        }
        localStringWriter.write(arrayOfChar, 0, paramInt);
      }
      return readGraphString(localStringWriter.toString());
    }
    catch (IOException paramContext)
    {
      throw new RuntimeException("Could not read specified resource file!");
    }
  }
  
  public abstract FilterGraph readGraphString(String paramString)
    throws GraphIOException;
  
  public abstract KeyValueMap readKeyValueAssignments(String paramString)
    throws GraphIOException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/io/GraphReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */