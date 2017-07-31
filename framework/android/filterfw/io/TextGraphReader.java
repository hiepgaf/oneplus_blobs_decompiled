package android.filterfw.io;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.KeyValueMap;
import android.filterfw.core.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class TextGraphReader
  extends GraphReader
{
  private KeyValueMap mBoundReferences;
  private ArrayList<Command> mCommands = new ArrayList();
  private Filter mCurrentFilter;
  private FilterGraph mCurrentGraph;
  private FilterFactory mFactory;
  private KeyValueMap mSettings;
  
  private void applySettings()
    throws GraphIOException
  {
    Iterator localIterator = this.mSettings.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = this.mSettings.get(str);
      if (str.equals("autoBranch"))
      {
        expectSettingClass(str, localObject, String.class);
        if (localObject.equals("synced")) {
          this.mCurrentGraph.setAutoBranchMode(1);
        } else if (localObject.equals("unsynced")) {
          this.mCurrentGraph.setAutoBranchMode(2);
        } else if (localObject.equals("off")) {
          this.mCurrentGraph.setAutoBranchMode(0);
        } else {
          throw new GraphIOException("Unknown autobranch setting: " + localObject + "!");
        }
      }
      else if (str.equals("discardUnconnectedOutputs"))
      {
        expectSettingClass(str, localObject, Boolean.class);
        this.mCurrentGraph.setDiscardUnconnectedOutputs(((Boolean)localObject).booleanValue());
      }
      else
      {
        throw new GraphIOException("Unknown @setting '" + str + "'!");
      }
    }
  }
  
  private void bindExternal(String paramString)
    throws GraphIOException
  {
    if (this.mReferences.containsKey(paramString))
    {
      Object localObject = this.mReferences.get(paramString);
      this.mBoundReferences.put(paramString, localObject);
      return;
    }
    throw new GraphIOException("Unknown external variable '" + paramString + "'! " + "You must add a reference to this external in the host program using " + "addReference(...)!");
  }
  
  private void checkReferences()
    throws GraphIOException
  {
    Iterator localIterator = this.mReferences.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!this.mBoundReferences.containsKey(str)) {
        throw new GraphIOException("Host program specifies reference to '" + str + "', which is not " + "declared @external in graph file!");
      }
    }
  }
  
  private void executeCommands()
    throws GraphIOException
  {
    Iterator localIterator = this.mCommands.iterator();
    while (localIterator.hasNext()) {
      ((Command)localIterator.next()).execute(this);
    }
  }
  
  private void expectSettingClass(String paramString, Object paramObject, Class paramClass)
    throws GraphIOException
  {
    if (paramObject.getClass() != paramClass) {
      throw new GraphIOException("Setting '" + paramString + "' must have a value of type " + paramClass.getSimpleName() + ", but found a value of type " + paramObject.getClass().getSimpleName() + "!");
    }
  }
  
  private void parseString(String paramString)
    throws GraphIOException
  {
    Pattern localPattern1 = Pattern.compile("@[a-zA-Z]+");
    Pattern localPattern2 = Pattern.compile("\\}");
    Pattern localPattern3 = Pattern.compile("\\{");
    Object localObject1 = Pattern.compile("(\\s+|//[^\\n]*\\n)+");
    Pattern localPattern4 = Pattern.compile("[a-zA-Z\\.]+");
    Pattern localPattern5 = Pattern.compile("[a-zA-Z\\./:]+");
    Pattern localPattern6 = Pattern.compile("\\[[a-zA-Z0-9\\-_]+\\]");
    Pattern localPattern7 = Pattern.compile("=>");
    Pattern localPattern8 = Pattern.compile(";");
    Pattern localPattern9 = Pattern.compile("[a-zA-Z0-9\\-_]+");
    int i = 0;
    PatternScanner localPatternScanner = new PatternScanner(paramString, (Pattern)localObject1);
    paramString = null;
    String str1 = null;
    String str2 = null;
    localObject1 = null;
    while (!localPatternScanner.atEnd())
    {
      Object localObject2;
      switch (i)
      {
      default: 
        break;
      case 0: 
        localObject2 = localPatternScanner.eat(localPattern1, "<command>");
        if (((String)localObject2).equals("@import")) {
          i = 1;
        } else if (((String)localObject2).equals("@library")) {
          i = 2;
        } else if (((String)localObject2).equals("@filter")) {
          i = 3;
        } else if (((String)localObject2).equals("@connect")) {
          i = 8;
        } else if (((String)localObject2).equals("@set")) {
          i = 13;
        } else if (((String)localObject2).equals("@external")) {
          i = 14;
        } else if (((String)localObject2).equals("@setting")) {
          i = 15;
        } else {
          throw new GraphIOException("Unknown command '" + (String)localObject2 + "'!");
        }
        break;
      case 1: 
        localObject2 = localPatternScanner.eat(localPattern4, "<package-name>");
        this.mCommands.add(new ImportPackageCommand((String)localObject2));
        i = 16;
        break;
      case 2: 
        localObject2 = localPatternScanner.eat(localPattern5, "<library-name>");
        this.mCommands.add(new AddLibraryCommand((String)localObject2));
        i = 16;
        break;
      case 3: 
        paramString = localPatternScanner.eat(localPattern9, "<class-name>");
        i = 4;
        break;
      case 4: 
        localObject2 = localPatternScanner.eat(localPattern9, "<filter-name>");
        this.mCommands.add(new AllocateFilterCommand(paramString, (String)localObject2));
        i = 5;
        break;
      case 5: 
        localPatternScanner.eat(localPattern3, "{");
        i = 6;
        break;
      case 6: 
        localObject2 = readKeyValueAssignments(localPatternScanner, localPattern2);
        this.mCommands.add(new InitFilterCommand((KeyValueMap)localObject2));
        i = 7;
        break;
      case 7: 
        localPatternScanner.eat(localPattern2, "}");
        i = 0;
        break;
      case 8: 
        str1 = localPatternScanner.eat(localPattern9, "<source-filter-name>");
        i = 9;
        break;
      case 9: 
        str2 = localPatternScanner.eat(localPattern6, "[<source-port-name>]");
        str2 = str2.substring(1, str2.length() - 1);
        i = 10;
        break;
      case 10: 
        localPatternScanner.eat(localPattern7, "=>");
        i = 11;
        break;
      case 11: 
        localObject1 = localPatternScanner.eat(localPattern9, "<target-filter-name>");
        i = 12;
        break;
      case 12: 
        localObject2 = localPatternScanner.eat(localPattern6, "[<target-port-name>]");
        localObject2 = ((String)localObject2).substring(1, ((String)localObject2).length() - 1);
        this.mCommands.add(new ConnectCommand(str1, str2, (String)localObject1, (String)localObject2));
        i = 16;
        break;
      case 13: 
        localObject2 = readKeyValueAssignments(localPatternScanner, localPattern8);
        this.mBoundReferences.putAll((Map)localObject2);
        i = 16;
        break;
      case 14: 
        bindExternal(localPatternScanner.eat(localPattern9, "<external-identifier>"));
        i = 16;
        break;
      case 15: 
        localObject2 = readKeyValueAssignments(localPatternScanner, localPattern8);
        this.mSettings.putAll((Map)localObject2);
        i = 16;
        break;
      case 16: 
        localPatternScanner.eat(localPattern8, ";");
        i = 0;
      }
    }
    if ((i != 16) && (i != 0)) {
      throw new GraphIOException("Unexpected end of input!");
    }
  }
  
  private KeyValueMap readKeyValueAssignments(PatternScanner paramPatternScanner, Pattern paramPattern)
    throws GraphIOException
  {
    Pattern localPattern1 = Pattern.compile("=");
    Pattern localPattern2 = Pattern.compile(";");
    Pattern localPattern3 = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]*");
    Pattern localPattern4 = Pattern.compile("'[^']*'|\\\"[^\\\"]*\\\"");
    Pattern localPattern5 = Pattern.compile("[0-9]+");
    Pattern localPattern6 = Pattern.compile("[0-9]*\\.[0-9]+f?");
    Pattern localPattern7 = Pattern.compile("\\$[a-zA-Z]+[a-zA-Z0-9]");
    Pattern localPattern8 = Pattern.compile("true|false");
    int i = 0;
    KeyValueMap localKeyValueMap = new KeyValueMap();
    String str1 = null;
    for (;;)
    {
      if ((paramPatternScanner.atEnd()) || ((paramPattern != null) && (paramPatternScanner.peek(paramPattern))))
      {
        if ((i == 0) || (i == 3)) {
          break;
        }
        throw new GraphIOException("Unexpected end of assignments on line " + paramPatternScanner.lineNo() + "!");
      }
      switch (i)
      {
      default: 
        break;
      case 0: 
        str1 = paramPatternScanner.eat(localPattern3, "<identifier>");
        i = 1;
        break;
      case 1: 
        paramPatternScanner.eat(localPattern1, "=");
        i = 2;
        break;
      case 2: 
        Object localObject = paramPatternScanner.tryEat(localPattern4);
        if (localObject != null) {
          localKeyValueMap.put(str1, ((String)localObject).substring(1, ((String)localObject).length() - 1));
        }
        for (;;)
        {
          i = 3;
          break;
          localObject = paramPatternScanner.tryEat(localPattern7);
          if (localObject != null)
          {
            String str2 = ((String)localObject).substring(1, ((String)localObject).length());
            if (this.mBoundReferences != null) {}
            for (localObject = this.mBoundReferences.get(str2); localObject == null; localObject = null) {
              throw new GraphIOException("Unknown object reference to '" + str2 + "'!");
            }
            localKeyValueMap.put(str1, localObject);
          }
          else
          {
            localObject = paramPatternScanner.tryEat(localPattern8);
            if (localObject != null)
            {
              localKeyValueMap.put(str1, Boolean.valueOf(Boolean.parseBoolean((String)localObject)));
            }
            else
            {
              localObject = paramPatternScanner.tryEat(localPattern6);
              if (localObject != null)
              {
                localKeyValueMap.put(str1, Float.valueOf(Float.parseFloat((String)localObject)));
              }
              else
              {
                localObject = paramPatternScanner.tryEat(localPattern5);
                if (localObject == null) {
                  break label443;
                }
                localKeyValueMap.put(str1, Integer.valueOf(Integer.parseInt((String)localObject)));
              }
            }
          }
        }
        throw new GraphIOException(paramPatternScanner.unexpectedTokenMessage("<value>"));
      case 3: 
        label443:
        paramPatternScanner.eat(localPattern2, ";");
        i = 0;
      }
    }
    return localKeyValueMap;
  }
  
  private void reset()
  {
    this.mCurrentGraph = null;
    this.mCurrentFilter = null;
    this.mCommands.clear();
    this.mBoundReferences = new KeyValueMap();
    this.mSettings = new KeyValueMap();
    this.mFactory = new FilterFactory();
  }
  
  public FilterGraph readGraphString(String paramString)
    throws GraphIOException
  {
    FilterGraph localFilterGraph = new FilterGraph();
    reset();
    this.mCurrentGraph = localFilterGraph;
    parseString(paramString);
    applySettings();
    executeCommands();
    reset();
    return localFilterGraph;
  }
  
  public KeyValueMap readKeyValueAssignments(String paramString)
    throws GraphIOException
  {
    return readKeyValueAssignments(new PatternScanner(paramString, Pattern.compile("\\s+")), null);
  }
  
  private class AddLibraryCommand
    implements TextGraphReader.Command
  {
    private String mLibraryName;
    
    public AddLibraryCommand(String paramString)
    {
      this.mLibraryName = paramString;
    }
    
    public void execute(TextGraphReader paramTextGraphReader)
    {
      TextGraphReader.-get2(paramTextGraphReader);
      FilterFactory.addFilterLibrary(this.mLibraryName);
    }
  }
  
  private class AllocateFilterCommand
    implements TextGraphReader.Command
  {
    private String mClassName;
    private String mFilterName;
    
    public AllocateFilterCommand(String paramString1, String paramString2)
    {
      this.mClassName = paramString1;
      this.mFilterName = paramString2;
    }
    
    public void execute(TextGraphReader paramTextGraphReader)
      throws GraphIOException
    {
      try
      {
        Filter localFilter = TextGraphReader.-get2(paramTextGraphReader).createFilterByClassName(this.mClassName, this.mFilterName);
        TextGraphReader.-set0(paramTextGraphReader, localFilter);
        return;
      }
      catch (IllegalArgumentException paramTextGraphReader)
      {
        throw new GraphIOException(paramTextGraphReader.getMessage());
      }
    }
  }
  
  private static abstract interface Command
  {
    public abstract void execute(TextGraphReader paramTextGraphReader)
      throws GraphIOException;
  }
  
  private class ConnectCommand
    implements TextGraphReader.Command
  {
    private String mSourceFilter;
    private String mSourcePort;
    private String mTargetFilter;
    private String mTargetName;
    
    public ConnectCommand(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      this.mSourceFilter = paramString1;
      this.mSourcePort = paramString2;
      this.mTargetFilter = paramString3;
      this.mTargetName = paramString4;
    }
    
    public void execute(TextGraphReader paramTextGraphReader)
    {
      TextGraphReader.-get1(paramTextGraphReader).connect(this.mSourceFilter, this.mSourcePort, this.mTargetFilter, this.mTargetName);
    }
  }
  
  private class ImportPackageCommand
    implements TextGraphReader.Command
  {
    private String mPackageName;
    
    public ImportPackageCommand(String paramString)
    {
      this.mPackageName = paramString;
    }
    
    public void execute(TextGraphReader paramTextGraphReader)
      throws GraphIOException
    {
      try
      {
        TextGraphReader.-get2(paramTextGraphReader).addPackage(this.mPackageName);
        return;
      }
      catch (IllegalArgumentException paramTextGraphReader)
      {
        throw new GraphIOException(paramTextGraphReader.getMessage());
      }
    }
  }
  
  private class InitFilterCommand
    implements TextGraphReader.Command
  {
    private KeyValueMap mParams;
    
    public InitFilterCommand(KeyValueMap paramKeyValueMap)
    {
      this.mParams = paramKeyValueMap;
    }
    
    public void execute(TextGraphReader paramTextGraphReader)
      throws GraphIOException
    {
      Filter localFilter = TextGraphReader.-get0(paramTextGraphReader);
      try
      {
        localFilter.initWithValueMap(this.mParams);
        TextGraphReader.-get1(paramTextGraphReader).addFilter(TextGraphReader.-get0(TextGraphReader.this));
        return;
      }
      catch (ProtocolException paramTextGraphReader)
      {
        throw new GraphIOException(paramTextGraphReader.getMessage());
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/io/TextGraphReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */