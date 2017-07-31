package com.android.server.input;

import android.hardware.input.TouchCalibration;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.inputmethod.InputMethodSubtypeHandle;
import com.android.internal.util.ArrayUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import libcore.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class PersistentDataStore
{
  static final String TAG = "InputManager";
  private final AtomicFile mAtomicFile = new AtomicFile(new File("/data/system/input-manager-state.xml"));
  private boolean mDirty;
  private final HashMap<String, InputDeviceState> mInputDevices = new HashMap();
  private boolean mLoaded;
  
  private void clearState()
  {
    this.mInputDevices.clear();
  }
  
  private InputDeviceState getInputDeviceState(String paramString, boolean paramBoolean)
  {
    loadIfNeeded();
    InputDeviceState localInputDeviceState2 = (InputDeviceState)this.mInputDevices.get(paramString);
    InputDeviceState localInputDeviceState1 = localInputDeviceState2;
    if (localInputDeviceState2 == null)
    {
      localInputDeviceState1 = localInputDeviceState2;
      if (paramBoolean)
      {
        localInputDeviceState1 = new InputDeviceState(null);
        this.mInputDevices.put(paramString, localInputDeviceState1);
        setDirty();
      }
    }
    return localInputDeviceState1;
  }
  
  /* Error */
  private void load()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 75	com/android/server/input/PersistentDataStore:clearState	()V
    //   4: aload_0
    //   5: getfield 42	com/android/server/input/PersistentDataStore:mAtomicFile	Landroid/util/AtomicFile;
    //   8: invokevirtual 79	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   11: astore_1
    //   12: invokestatic 85	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   15: astore_2
    //   16: aload_2
    //   17: new 87	java/io/BufferedInputStream
    //   20: dup
    //   21: aload_1
    //   22: invokespecial 90	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   25: getstatic 96	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   28: invokevirtual 102	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   31: invokeinterface 108 3 0
    //   36: aload_0
    //   37: aload_2
    //   38: invokespecial 112	com/android/server/input/PersistentDataStore:loadFromXml	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   41: aload_1
    //   42: invokestatic 118	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   45: return
    //   46: astore_1
    //   47: return
    //   48: astore_2
    //   49: ldc 11
    //   51: ldc 120
    //   53: aload_2
    //   54: invokestatic 126	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   57: pop
    //   58: aload_0
    //   59: invokespecial 75	com/android/server/input/PersistentDataStore:clearState	()V
    //   62: aload_1
    //   63: invokestatic 118	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   66: return
    //   67: astore_2
    //   68: ldc 11
    //   70: ldc 120
    //   72: aload_2
    //   73: invokestatic 126	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   76: pop
    //   77: aload_0
    //   78: invokespecial 75	com/android/server/input/PersistentDataStore:clearState	()V
    //   81: aload_1
    //   82: invokestatic 118	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   85: return
    //   86: astore_2
    //   87: aload_1
    //   88: invokestatic 118	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
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
  
  private void loadFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    XmlUtils.beginDocument(paramXmlPullParser, "input-manager-state");
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if (paramXmlPullParser.getName().equals("input-devices")) {
        loadInputDevicesFromXml(paramXmlPullParser);
      }
    }
  }
  
  private void loadIfNeeded()
  {
    if (!this.mLoaded)
    {
      load();
      this.mLoaded = true;
    }
  }
  
  private void loadInputDevicesFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if (paramXmlPullParser.getName().equals("input-device"))
      {
        String str = paramXmlPullParser.getAttributeValue(null, "descriptor");
        if (str == null) {
          throw new XmlPullParserException("Missing descriptor attribute on input-device.");
        }
        if (this.mInputDevices.containsKey(str)) {
          throw new XmlPullParserException("Found duplicate input device.");
        }
        InputDeviceState localInputDeviceState = new InputDeviceState(null);
        localInputDeviceState.loadFromXml(paramXmlPullParser);
        this.mInputDevices.put(str, localInputDeviceState);
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
      Slog.w("InputManager", "Failed to save input manager persistent store data.", localIOException);
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
    paramXmlSerializer.startTag(null, "input-manager-state");
    paramXmlSerializer.startTag(null, "input-devices");
    Iterator localIterator = this.mInputDevices.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      String str = (String)((Map.Entry)localObject).getKey();
      localObject = (InputDeviceState)((Map.Entry)localObject).getValue();
      paramXmlSerializer.startTag(null, "input-device");
      paramXmlSerializer.attribute(null, "descriptor", str);
      ((InputDeviceState)localObject).saveToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "input-device");
    }
    paramXmlSerializer.endTag(null, "input-devices");
    paramXmlSerializer.endTag(null, "input-manager-state");
    paramXmlSerializer.endDocument();
  }
  
  private void setDirty()
  {
    this.mDirty = true;
  }
  
  public boolean addKeyboardLayout(String paramString1, String paramString2)
  {
    if (getInputDeviceState(paramString1, true).addKeyboardLayout(paramString2))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + "PersistentDataStore");
    paramPrintWriter.println(paramString + "  mLoaded=" + this.mLoaded);
    paramPrintWriter.println(paramString + "  mDirty=" + this.mDirty);
    paramPrintWriter.println(paramString + "  InputDeviceStates:");
    int i = 0;
    Iterator localIterator = this.mInputDevices.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramPrintWriter.println(paramString + "    " + i + ": " + (String)localEntry.getKey());
      InputDeviceState.-wrap0((InputDeviceState)localEntry.getValue(), paramPrintWriter, paramString + "      ");
      i += 1;
    }
  }
  
  public String getCurrentKeyboardLayout(String paramString)
  {
    Object localObject = null;
    InputDeviceState localInputDeviceState = getInputDeviceState(paramString, false);
    paramString = (String)localObject;
    if (localInputDeviceState != null) {
      paramString = localInputDeviceState.getCurrentKeyboardLayout();
    }
    return paramString;
  }
  
  public String getKeyboardLayout(String paramString, InputMethodSubtypeHandle paramInputMethodSubtypeHandle)
  {
    paramString = getInputDeviceState(paramString, false);
    if (paramString == null) {
      return null;
    }
    return paramString.getKeyboardLayout(paramInputMethodSubtypeHandle);
  }
  
  public String[] getKeyboardLayouts(String paramString)
  {
    paramString = getInputDeviceState(paramString, false);
    if (paramString == null) {
      return (String[])ArrayUtils.emptyArray(String.class);
    }
    return paramString.getKeyboardLayouts();
  }
  
  public TouchCalibration getTouchCalibration(String paramString, int paramInt)
  {
    paramString = getInputDeviceState(paramString, false);
    if (paramString == null) {
      return TouchCalibration.IDENTITY;
    }
    paramString = paramString.getTouchCalibration(paramInt);
    if (paramString == null) {
      return TouchCalibration.IDENTITY;
    }
    return paramString;
  }
  
  public boolean removeKeyboardLayout(String paramString1, String paramString2)
  {
    if (getInputDeviceState(paramString1, true).removeKeyboardLayout(paramString2))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  public boolean removeUninstalledKeyboardLayouts(Set<String> paramSet)
  {
    int i = 0;
    Iterator localIterator = this.mInputDevices.values().iterator();
    while (localIterator.hasNext()) {
      if (((InputDeviceState)localIterator.next()).removeUninstalledKeyboardLayouts(paramSet)) {
        i = 1;
      }
    }
    if (i != 0)
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  public void saveIfNeeded()
  {
    if (this.mDirty)
    {
      save();
      this.mDirty = false;
    }
  }
  
  public boolean setCurrentKeyboardLayout(String paramString1, String paramString2)
  {
    if (getInputDeviceState(paramString1, true).setCurrentKeyboardLayout(paramString2))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  public boolean setKeyboardLayout(String paramString1, InputMethodSubtypeHandle paramInputMethodSubtypeHandle, String paramString2)
  {
    if (getInputDeviceState(paramString1, true).setKeyboardLayout(paramInputMethodSubtypeHandle, paramString2))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  public boolean setTouchCalibration(String paramString, int paramInt, TouchCalibration paramTouchCalibration)
  {
    if (getInputDeviceState(paramString, true).setTouchCalibration(paramInt, paramTouchCalibration))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  public boolean switchKeyboardLayout(String paramString, InputMethodSubtypeHandle paramInputMethodSubtypeHandle)
  {
    paramString = getInputDeviceState(paramString, false);
    if ((paramString != null) && (paramString.switchKeyboardLayout(paramInputMethodSubtypeHandle)))
    {
      setDirty();
      return true;
    }
    return false;
  }
  
  private static final class InputDeviceState
  {
    private static final String[] CALIBRATION_NAME = { "x_scale", "x_ymix", "x_offset", "y_xmix", "y_scale", "y_offset" };
    private String mCurrentKeyboardLayout;
    private ArrayMap<InputMethodSubtypeHandle, String> mKeyboardLayouts = new ArrayMap();
    private TouchCalibration[] mTouchCalibration = new TouchCalibration[4];
    private List<String> mUnassociatedKeyboardLayouts = new ArrayList();
    
    private void dump(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(paramString + "CurrentKeyboardLayout=" + this.mCurrentKeyboardLayout);
      paramPrintWriter.println(paramString + "UnassociatedKeyboardLayouts=" + this.mUnassociatedKeyboardLayouts);
      paramPrintWriter.println(paramString + "TouchCalibration=" + Arrays.toString(this.mTouchCalibration));
      paramPrintWriter.println(paramString + "Subtype to Layout Mappings:");
      int j = this.mKeyboardLayouts.size();
      if (j != 0)
      {
        int i = 0;
        while (i < j)
        {
          paramPrintWriter.println(paramString + "  " + this.mKeyboardLayouts.keyAt(i) + ": " + (String)this.mKeyboardLayouts.valueAt(i));
          i += 1;
        }
      }
      paramPrintWriter.println(paramString + "  <none>");
    }
    
    private static int stringToSurfaceRotation(String paramString)
    {
      if ("0".equals(paramString)) {
        return 0;
      }
      if ("90".equals(paramString)) {
        return 1;
      }
      if ("180".equals(paramString)) {
        return 2;
      }
      if ("270".equals(paramString)) {
        return 3;
      }
      throw new IllegalArgumentException("Unsupported surface rotation string '" + paramString + "'");
    }
    
    private static String surfaceRotationToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Unsupported surface rotation value" + paramInt);
      case 0: 
        return "0";
      case 1: 
        return "90";
      case 2: 
        return "180";
      }
      return "270";
    }
    
    private void updateCurrentKeyboardLayoutIfRemoved(String paramString, int paramInt)
    {
      if (Objects.equal(this.mCurrentKeyboardLayout, paramString))
      {
        if (!this.mUnassociatedKeyboardLayouts.isEmpty())
        {
          int i = paramInt;
          if (paramInt == this.mUnassociatedKeyboardLayouts.size()) {
            i = 0;
          }
          this.mCurrentKeyboardLayout = ((String)this.mUnassociatedKeyboardLayouts.get(i));
        }
      }
      else {
        return;
      }
      this.mCurrentKeyboardLayout = null;
    }
    
    public boolean addKeyboardLayout(String paramString)
    {
      int i = Collections.binarySearch(this.mUnassociatedKeyboardLayouts, paramString);
      if (i >= 0) {
        return false;
      }
      this.mUnassociatedKeyboardLayouts.add(-i - 1, paramString);
      if (this.mCurrentKeyboardLayout == null) {
        this.mCurrentKeyboardLayout = paramString;
      }
      return true;
    }
    
    public String getCurrentKeyboardLayout()
    {
      return this.mCurrentKeyboardLayout;
    }
    
    public String getKeyboardLayout(InputMethodSubtypeHandle paramInputMethodSubtypeHandle)
    {
      return (String)this.mKeyboardLayouts.get(paramInputMethodSubtypeHandle);
    }
    
    public String[] getKeyboardLayouts()
    {
      if (this.mUnassociatedKeyboardLayouts.isEmpty()) {
        return (String[])ArrayUtils.emptyArray(String.class);
      }
      return (String[])this.mUnassociatedKeyboardLayouts.toArray(new String[this.mUnassociatedKeyboardLayouts.size()]);
    }
    
    public TouchCalibration getTouchCalibration(int paramInt)
    {
      try
      {
        TouchCalibration localTouchCalibration = this.mTouchCalibration[paramInt];
        return localTouchCalibration;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        Slog.w("InputManager", "Cannot get touch calibration.", localArrayIndexOutOfBoundsException);
      }
      return null;
    }
    
    public void loadFromXml(XmlPullParser paramXmlPullParser)
      throws IOException, XmlPullParserException
    {
      int k = paramXmlPullParser.getDepth();
      while (XmlUtils.nextElementWithin(paramXmlPullParser, k))
      {
        Object localObject1;
        Object localObject2;
        String str;
        if (paramXmlPullParser.getName().equals("keyboard-layout"))
        {
          localObject1 = paramXmlPullParser.getAttributeValue(null, "descriptor");
          if (localObject1 == null) {
            throw new XmlPullParserException("Missing descriptor attribute on keyboard-layout.");
          }
          localObject2 = paramXmlPullParser.getAttributeValue(null, "current");
          if ((localObject2 != null) && (((String)localObject2).equals("true")))
          {
            if (this.mCurrentKeyboardLayout != null) {
              throw new XmlPullParserException("Found multiple current keyboard layouts.");
            }
            this.mCurrentKeyboardLayout = ((String)localObject1);
          }
          localObject2 = paramXmlPullParser.getAttributeValue(null, "input-method-id");
          str = paramXmlPullParser.getAttributeValue(null, "input-method-subtype-id");
          if ((localObject2 == null) && (str != null)) {}
          while ((localObject2 != null) && (str == null)) {
            throw new XmlPullParserException("Found an incomplete input method description");
          }
          if (str != null)
          {
            localObject2 = new InputMethodSubtypeHandle((String)localObject2, Integer.parseInt(str));
            if (this.mKeyboardLayouts.containsKey(localObject2)) {
              throw new XmlPullParserException("Found duplicate subtype to keyboard layout mapping: " + localObject2);
            }
            this.mKeyboardLayouts.put(localObject2, localObject1);
          }
          else
          {
            if (this.mUnassociatedKeyboardLayouts.contains(localObject1)) {
              throw new XmlPullParserException("Found duplicate unassociated keyboard layout: " + (String)localObject1);
            }
            this.mUnassociatedKeyboardLayouts.add(localObject1);
          }
        }
        else if (paramXmlPullParser.getName().equals("calibration"))
        {
          localObject1 = paramXmlPullParser.getAttributeValue(null, "format");
          localObject2 = paramXmlPullParser.getAttributeValue(null, "rotation");
          int i = -1;
          if (localObject1 == null) {
            throw new XmlPullParserException("Missing format attribute on calibration.");
          }
          if (!((String)localObject1).equals("affine")) {
            throw new XmlPullParserException("Unsupported format for calibration.");
          }
          if (localObject2 != null) {}
          for (;;)
          {
            try
            {
              i = stringToSurfaceRotation((String)localObject2);
              localObject1 = TouchCalibration.IDENTITY.getAffineTransform();
              int m = paramXmlPullParser.getDepth();
              if (!XmlUtils.nextElementWithin(paramXmlPullParser, m)) {
                break;
              }
              localObject2 = paramXmlPullParser.getName().toLowerCase();
              str = paramXmlPullParser.nextText();
              int j = 0;
              if ((j < localObject1.length) && (j < CALIBRATION_NAME.length)) {
                if (((String)localObject2).equals(CALIBRATION_NAME[j])) {
                  localObject1[j] = Float.parseFloat(str);
                } else {
                  j += 1;
                }
              }
            }
            catch (IllegalArgumentException paramXmlPullParser)
            {
              throw new XmlPullParserException("Unsupported rotation for calibration.");
            }
          }
          if (i == -1)
          {
            i = 0;
            while (i < this.mTouchCalibration.length)
            {
              this.mTouchCalibration[i] = new TouchCalibration(localObject1[0], localObject1[1], localObject1[2], localObject1[3], localObject1[4], localObject1[5]);
              i += 1;
            }
          }
          else
          {
            this.mTouchCalibration[i] = new TouchCalibration(localObject1[0], localObject1[1], localObject1[2], localObject1[3], localObject1[4], localObject1[5]);
          }
        }
      }
      Collections.sort(this.mUnassociatedKeyboardLayouts);
      if ((this.mCurrentKeyboardLayout != null) || (this.mUnassociatedKeyboardLayouts.isEmpty())) {
        return;
      }
      this.mCurrentKeyboardLayout = ((String)this.mUnassociatedKeyboardLayouts.get(0));
    }
    
    public boolean removeKeyboardLayout(String paramString)
    {
      int i = Collections.binarySearch(this.mUnassociatedKeyboardLayouts, paramString);
      if (i < 0) {
        return false;
      }
      this.mUnassociatedKeyboardLayouts.remove(i);
      updateCurrentKeyboardLayoutIfRemoved(paramString, i);
      return true;
    }
    
    public boolean removeUninstalledKeyboardLayouts(Set<String> paramSet)
    {
      boolean bool = false;
      int j;
      for (int i = this.mUnassociatedKeyboardLayouts.size();; i = j)
      {
        j = i - 1;
        if (i <= 0) {
          break;
        }
        String str = (String)this.mUnassociatedKeyboardLayouts.get(j);
        if (!paramSet.contains(str))
        {
          Slog.i("InputManager", "Removing uninstalled keyboard layout " + str);
          this.mUnassociatedKeyboardLayouts.remove(j);
          updateCurrentKeyboardLayoutIfRemoved(str, j);
          bool = true;
        }
      }
      return bool;
    }
    
    public void saveToXml(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      Object localObject1 = this.mUnassociatedKeyboardLayouts.iterator();
      Object localObject2;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        paramXmlSerializer.startTag(null, "keyboard-layout");
        paramXmlSerializer.attribute(null, "descriptor", (String)localObject2);
        paramXmlSerializer.endTag(null, "keyboard-layout");
      }
      int j = this.mKeyboardLayouts.size();
      int i = 0;
      while (i < j)
      {
        localObject1 = (InputMethodSubtypeHandle)this.mKeyboardLayouts.keyAt(i);
        localObject2 = (String)this.mKeyboardLayouts.valueAt(i);
        paramXmlSerializer.startTag(null, "keyboard-layout");
        paramXmlSerializer.attribute(null, "descriptor", (String)localObject2);
        paramXmlSerializer.attribute(null, "input-method-id", ((InputMethodSubtypeHandle)localObject1).getInputMethodId());
        paramXmlSerializer.attribute(null, "input-method-subtype-id", Integer.toString(((InputMethodSubtypeHandle)localObject1).getSubtypeId()));
        if (((String)localObject2).equals(this.mCurrentKeyboardLayout)) {
          paramXmlSerializer.attribute(null, "current", "true");
        }
        paramXmlSerializer.endTag(null, "keyboard-layout");
        i += 1;
      }
      i = 0;
      while (i < this.mTouchCalibration.length)
      {
        if (this.mTouchCalibration[i] != null)
        {
          localObject1 = surfaceRotationToString(i);
          localObject2 = this.mTouchCalibration[i].getAffineTransform();
          paramXmlSerializer.startTag(null, "calibration");
          paramXmlSerializer.attribute(null, "format", "affine");
          paramXmlSerializer.attribute(null, "rotation", (String)localObject1);
          j = 0;
          while ((j < localObject2.length) && (j < CALIBRATION_NAME.length))
          {
            paramXmlSerializer.startTag(null, CALIBRATION_NAME[j]);
            paramXmlSerializer.text(Float.toString(localObject2[j]));
            paramXmlSerializer.endTag(null, CALIBRATION_NAME[j]);
            j += 1;
          }
          paramXmlSerializer.endTag(null, "calibration");
        }
        i += 1;
      }
    }
    
    public boolean setCurrentKeyboardLayout(String paramString)
    {
      if (Objects.equal(this.mCurrentKeyboardLayout, paramString)) {
        return false;
      }
      addKeyboardLayout(paramString);
      this.mCurrentKeyboardLayout = paramString;
      return true;
    }
    
    public boolean setKeyboardLayout(InputMethodSubtypeHandle paramInputMethodSubtypeHandle, String paramString)
    {
      if (TextUtils.equals((String)this.mKeyboardLayouts.get(paramInputMethodSubtypeHandle), paramString)) {
        return false;
      }
      this.mKeyboardLayouts.put(paramInputMethodSubtypeHandle, paramString);
      return true;
    }
    
    public boolean setTouchCalibration(int paramInt, TouchCalibration paramTouchCalibration)
    {
      try
      {
        if (!paramTouchCalibration.equals(this.mTouchCalibration[paramInt]))
        {
          this.mTouchCalibration[paramInt] = paramTouchCalibration;
          return true;
        }
        return false;
      }
      catch (ArrayIndexOutOfBoundsException paramTouchCalibration)
      {
        Slog.w("InputManager", "Cannot set touch calibration.", paramTouchCalibration);
      }
      return false;
    }
    
    public boolean switchKeyboardLayout(InputMethodSubtypeHandle paramInputMethodSubtypeHandle)
    {
      paramInputMethodSubtypeHandle = (String)this.mKeyboardLayouts.get(paramInputMethodSubtypeHandle);
      if (!TextUtils.equals(this.mCurrentKeyboardLayout, paramInputMethodSubtypeHandle))
      {
        this.mCurrentKeyboardLayout = paramInputMethodSubtypeHandle;
        return true;
      }
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/input/PersistentDataStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */