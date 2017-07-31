package com.android.server.display;

import android.hardware.display.WifiDisplay;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import libcore.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class PersistentDataStore
{
  static final String TAG = "DisplayManager";
  private final AtomicFile mAtomicFile = new AtomicFile(new File("/data/system/display-manager-state.xml"));
  private boolean mDirty;
  private final HashMap<String, DisplayState> mDisplayStates = new HashMap();
  private boolean mLoaded;
  private ArrayList<WifiDisplay> mRememberedWifiDisplays = new ArrayList();
  
  private void clearState()
  {
    this.mRememberedWifiDisplays.clear();
  }
  
  private int findRememberedWifiDisplay(String paramString)
  {
    int j = this.mRememberedWifiDisplays.size();
    int i = 0;
    while (i < j)
    {
      if (((WifiDisplay)this.mRememberedWifiDisplays.get(i)).getDeviceAddress().equals(paramString)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private DisplayState getDisplayState(String paramString, boolean paramBoolean)
  {
    loadIfNeeded();
    DisplayState localDisplayState2 = (DisplayState)this.mDisplayStates.get(paramString);
    DisplayState localDisplayState1 = localDisplayState2;
    if (localDisplayState2 == null)
    {
      localDisplayState1 = localDisplayState2;
      if (paramBoolean)
      {
        localDisplayState1 = new DisplayState(null);
        this.mDisplayStates.put(paramString, localDisplayState1);
        setDirty();
      }
    }
    return localDisplayState1;
  }
  
  /* Error */
  private void load()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 104	com/android/server/display/PersistentDataStore:clearState	()V
    //   4: aload_0
    //   5: getfield 50	com/android/server/display/PersistentDataStore:mAtomicFile	Landroid/util/AtomicFile;
    //   8: invokevirtual 108	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   11: astore_1
    //   12: invokestatic 114	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   15: astore_2
    //   16: aload_2
    //   17: new 116	java/io/BufferedInputStream
    //   20: dup
    //   21: aload_1
    //   22: invokespecial 119	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   25: getstatic 125	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   28: invokevirtual 130	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   31: invokeinterface 136 3 0
    //   36: aload_0
    //   37: aload_2
    //   38: invokespecial 140	com/android/server/display/PersistentDataStore:loadFromXml	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   41: aload_1
    //   42: invokestatic 146	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   45: return
    //   46: astore_1
    //   47: return
    //   48: astore_2
    //   49: ldc 11
    //   51: ldc -108
    //   53: aload_2
    //   54: invokestatic 154	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   57: pop
    //   58: aload_0
    //   59: invokespecial 104	com/android/server/display/PersistentDataStore:clearState	()V
    //   62: aload_1
    //   63: invokestatic 146	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   66: return
    //   67: astore_2
    //   68: ldc 11
    //   70: ldc -108
    //   72: aload_2
    //   73: invokestatic 154	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   76: pop
    //   77: aload_0
    //   78: invokespecial 104	com/android/server/display/PersistentDataStore:clearState	()V
    //   81: aload_1
    //   82: invokestatic 146	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   85: return
    //   86: astore_2
    //   87: aload_1
    //   88: invokestatic 146	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   91: aload_2
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	PersistentDataStore
    //   11	31	1	localFileInputStream	java.io.FileInputStream
    //   46	42	1	localFileNotFoundException	java.io.FileNotFoundException
    //   15	23	2	localXmlPullParser	XmlPullParser
    //   48	6	2	localXmlPullParserException	XmlPullParserException
    //   67	6	2	localIOException	IOException
    //   86	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	12	46	java/io/FileNotFoundException
    //   12	41	48	org/xmlpull/v1/XmlPullParserException
    //   12	41	67	java/io/IOException
    //   12	41	86	finally
    //   49	62	86	finally
    //   68	81	86	finally
  }
  
  private void loadDisplaysFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if (paramXmlPullParser.getName().equals("display"))
      {
        String str = paramXmlPullParser.getAttributeValue(null, "unique-id");
        if (str == null) {
          throw new XmlPullParserException("Missing unique-id attribute on display.");
        }
        if (this.mDisplayStates.containsKey(str)) {
          throw new XmlPullParserException("Found duplicate display.");
        }
        DisplayState localDisplayState = new DisplayState(null);
        localDisplayState.loadFromXml(paramXmlPullParser);
        this.mDisplayStates.put(str, localDisplayState);
      }
    }
  }
  
  private void loadFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    XmlUtils.beginDocument(paramXmlPullParser, "display-manager-state");
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i))
    {
      if (paramXmlPullParser.getName().equals("remembered-wifi-displays")) {
        loadRememberedWifiDisplaysFromXml(paramXmlPullParser);
      }
      if (paramXmlPullParser.getName().equals("display-states")) {
        loadDisplaysFromXml(paramXmlPullParser);
      }
    }
  }
  
  private void loadRememberedWifiDisplaysFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if (paramXmlPullParser.getName().equals("wifi-display"))
      {
        String str1 = paramXmlPullParser.getAttributeValue(null, "deviceAddress");
        String str2 = paramXmlPullParser.getAttributeValue(null, "deviceName");
        String str3 = paramXmlPullParser.getAttributeValue(null, "deviceAlias");
        if ((str1 == null) || (str2 == null)) {
          throw new XmlPullParserException("Missing deviceAddress or deviceName attribute on wifi-display.");
        }
        if (findRememberedWifiDisplay(str1) >= 0) {
          throw new XmlPullParserException("Found duplicate wifi display device address.");
        }
        this.mRememberedWifiDisplays.add(new WifiDisplay(str1, str2, str3, false, false, false));
      }
    }
  }
  
  private void save()
  {
    try
    {
      FileOutputStream localFileOutputStream = this.mAtomicFile.startWrite();
      FastXmlSerializer localFastXmlSerializer;
      this.mAtomicFile.finishWrite(localIOException);
    }
    catch (IOException localIOException)
    {
      try
      {
        localFastXmlSerializer = new FastXmlSerializer();
        localFastXmlSerializer.setOutput(new BufferedOutputStream(localFileOutputStream), StandardCharsets.UTF_8.name());
        saveToXml(localFastXmlSerializer);
        localFastXmlSerializer.flush();
        if (1 != 0)
        {
          this.mAtomicFile.finishWrite(localFileOutputStream);
          return;
        }
        this.mAtomicFile.failWrite(localFileOutputStream);
        return;
      }
      finally
      {
        if (0 == 0) {
          break label95;
        }
      }
      localIOException = localIOException;
      Slog.w("DisplayManager", "Failed to save display manager persistent store data.", localIOException);
      return;
    }
    for (;;)
    {
      throw ((Throwable)localObject);
      label95:
      this.mAtomicFile.failWrite(localIOException);
    }
  }
  
  private void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startDocument(null, Boolean.valueOf(true));
    paramXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
    paramXmlSerializer.startTag(null, "display-manager-state");
    paramXmlSerializer.startTag(null, "remembered-wifi-displays");
    Iterator localIterator = this.mRememberedWifiDisplays.iterator();
    Object localObject1;
    while (localIterator.hasNext())
    {
      localObject1 = (WifiDisplay)localIterator.next();
      paramXmlSerializer.startTag(null, "wifi-display");
      paramXmlSerializer.attribute(null, "deviceAddress", ((WifiDisplay)localObject1).getDeviceAddress());
      paramXmlSerializer.attribute(null, "deviceName", ((WifiDisplay)localObject1).getDeviceName());
      if (((WifiDisplay)localObject1).getDeviceAlias() != null) {
        paramXmlSerializer.attribute(null, "deviceAlias", ((WifiDisplay)localObject1).getDeviceAlias());
      }
      paramXmlSerializer.endTag(null, "wifi-display");
    }
    paramXmlSerializer.endTag(null, "remembered-wifi-displays");
    paramXmlSerializer.startTag(null, "display-states");
    localIterator = this.mDisplayStates.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = (Map.Entry)localIterator.next();
      localObject1 = (String)((Map.Entry)localObject2).getKey();
      localObject2 = (DisplayState)((Map.Entry)localObject2).getValue();
      paramXmlSerializer.startTag(null, "display");
      paramXmlSerializer.attribute(null, "unique-id", (String)localObject1);
      ((DisplayState)localObject2).saveToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "display");
    }
    paramXmlSerializer.endTag(null, "display-states");
    paramXmlSerializer.endTag(null, "display-manager-state");
    paramXmlSerializer.endDocument();
  }
  
  private void setDirty()
  {
    this.mDirty = true;
  }
  
  public WifiDisplay applyWifiDisplayAlias(WifiDisplay paramWifiDisplay)
  {
    if (paramWifiDisplay != null)
    {
      loadIfNeeded();
      String str = null;
      int i = findRememberedWifiDisplay(paramWifiDisplay.getDeviceAddress());
      if (i >= 0) {
        str = ((WifiDisplay)this.mRememberedWifiDisplays.get(i)).getDeviceAlias();
      }
      if (!Objects.equal(paramWifiDisplay.getDeviceAlias(), str)) {
        return new WifiDisplay(paramWifiDisplay.getDeviceAddress(), paramWifiDisplay.getDeviceName(), str, paramWifiDisplay.isAvailable(), paramWifiDisplay.canConnect(), paramWifiDisplay.isRemembered());
      }
    }
    return paramWifiDisplay;
  }
  
  public WifiDisplay[] applyWifiDisplayAliases(WifiDisplay[] paramArrayOfWifiDisplay)
  {
    Object localObject1 = paramArrayOfWifiDisplay;
    Object localObject2 = localObject1;
    if (paramArrayOfWifiDisplay != null)
    {
      int j = paramArrayOfWifiDisplay.length;
      int i = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (i >= j) {
          break;
        }
        WifiDisplay localWifiDisplay = applyWifiDisplayAlias(paramArrayOfWifiDisplay[i]);
        localObject2 = localObject1;
        if (localWifiDisplay != paramArrayOfWifiDisplay[i])
        {
          localObject2 = localObject1;
          if (localObject1 == paramArrayOfWifiDisplay)
          {
            localObject2 = new WifiDisplay[j];
            System.arraycopy(paramArrayOfWifiDisplay, 0, localObject2, 0, j);
          }
          localObject2[i] = localWifiDisplay;
        }
        i += 1;
        localObject1 = localObject2;
      }
    }
    return (WifiDisplay[])localObject2;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("PersistentDataStore");
    paramPrintWriter.println("  mLoaded=" + this.mLoaded);
    paramPrintWriter.println("  mDirty=" + this.mDirty);
    paramPrintWriter.println("  RememberedWifiDisplays:");
    int i = 0;
    Iterator localIterator = this.mRememberedWifiDisplays.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (WifiDisplay)localIterator.next();
      paramPrintWriter.println("    " + i + ": " + localObject);
      i += 1;
    }
    paramPrintWriter.println("  DisplayStates:");
    i = 0;
    localIterator = this.mDisplayStates.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      paramPrintWriter.println("    " + i + ": " + (String)((Map.Entry)localObject).getKey());
      DisplayState.-wrap0((DisplayState)((Map.Entry)localObject).getValue(), paramPrintWriter, "      ");
      i += 1;
    }
  }
  
  public boolean forgetWifiDisplay(String paramString)
  {
    int i = findRememberedWifiDisplay(paramString);
    if (i >= 0)
    {
      this.mRememberedWifiDisplays.remove(i);
      setDirty();
      return true;
    }
    return false;
  }
  
  public int getColorMode(DisplayDevice paramDisplayDevice)
  {
    if (!paramDisplayDevice.hasStableUniqueId()) {
      return 0;
    }
    paramDisplayDevice = getDisplayState(paramDisplayDevice.getUniqueId(), false);
    if (paramDisplayDevice == null) {
      return 0;
    }
    return paramDisplayDevice.getColorMode();
  }
  
  public WifiDisplay getRememberedWifiDisplay(String paramString)
  {
    loadIfNeeded();
    int i = findRememberedWifiDisplay(paramString);
    if (i >= 0) {
      return (WifiDisplay)this.mRememberedWifiDisplays.get(i);
    }
    return null;
  }
  
  public WifiDisplay[] getRememberedWifiDisplays()
  {
    loadIfNeeded();
    return (WifiDisplay[])this.mRememberedWifiDisplays.toArray(new WifiDisplay[this.mRememberedWifiDisplays.size()]);
  }
  
  public void loadIfNeeded()
  {
    if (!this.mLoaded)
    {
      load();
      this.mLoaded = true;
    }
  }
  
  public boolean rememberWifiDisplay(WifiDisplay paramWifiDisplay)
  {
    loadIfNeeded();
    int i = findRememberedWifiDisplay(paramWifiDisplay.getDeviceAddress());
    if (i >= 0)
    {
      if (((WifiDisplay)this.mRememberedWifiDisplays.get(i)).equals(paramWifiDisplay)) {
        return false;
      }
      this.mRememberedWifiDisplays.set(i, paramWifiDisplay);
    }
    for (;;)
    {
      setDirty();
      return true;
      this.mRememberedWifiDisplays.add(paramWifiDisplay);
    }
  }
  
  public void saveIfNeeded()
  {
    if (this.mDirty)
    {
      save();
      this.mDirty = false;
    }
  }
  
  public boolean setColorMode(DisplayDevice paramDisplayDevice, int paramInt)
  {
    if (!paramDisplayDevice.hasStableUniqueId()) {
      return false;
    }
    if (getDisplayState(paramDisplayDevice.getUniqueId(), true).setColorMode(paramInt))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  private static final class DisplayState
  {
    private int mColorMode;
    
    private void dump(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "ColorMode=" + this.mColorMode);
    }
    
    public int getColorMode()
    {
      return this.mColorMode;
    }
    
    public void loadFromXml(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      int i = paramXmlPullParser.getDepth();
      while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
        if (paramXmlPullParser.getName().equals("color-mode")) {
          this.mColorMode = Integer.parseInt(paramXmlPullParser.nextText());
        }
      }
    }
    
    public void saveToXml(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      paramXmlSerializer.startTag(null, "color-mode");
      paramXmlSerializer.text(Integer.toString(this.mColorMode));
      paramXmlSerializer.endTag(null, "color-mode");
    }
    
    public boolean setColorMode(int paramInt)
    {
      if (paramInt == this.mColorMode) {
        return false;
      }
      this.mColorMode = paramInt;
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/PersistentDataStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */