package android.gesture;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GestureStore
{
  private static final short FILE_FORMAT_VERSION = 1;
  public static final int ORIENTATION_INVARIANT = 1;
  public static final int ORIENTATION_SENSITIVE = 2;
  static final int ORIENTATION_SENSITIVE_4 = 4;
  static final int ORIENTATION_SENSITIVE_8 = 8;
  private static final boolean PROFILE_LOADING_SAVING = false;
  public static final int SEQUENCE_INVARIANT = 1;
  public static final int SEQUENCE_SENSITIVE = 2;
  private boolean mChanged = false;
  private Learner mClassifier = new InstanceLearner();
  private final HashMap<String, ArrayList<Gesture>> mNamedGestures = new HashMap();
  private int mOrientationStyle = 2;
  private int mSequenceType = 2;
  
  private void readFormatV1(DataInputStream paramDataInputStream)
    throws IOException
  {
    Learner localLearner = this.mClassifier;
    HashMap localHashMap = this.mNamedGestures;
    localHashMap.clear();
    int k = paramDataInputStream.readInt();
    int i = 0;
    while (i < k)
    {
      String str = paramDataInputStream.readUTF();
      int m = paramDataInputStream.readInt();
      ArrayList localArrayList = new ArrayList(m);
      int j = 0;
      while (j < m)
      {
        Gesture localGesture = Gesture.deserialize(paramDataInputStream);
        localArrayList.add(localGesture);
        localLearner.addInstance(Instance.createInstance(this.mSequenceType, this.mOrientationStyle, localGesture, str));
        j += 1;
      }
      localHashMap.put(str, localArrayList);
      i += 1;
    }
  }
  
  public void addGesture(String paramString, Gesture paramGesture)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return;
    }
    ArrayList localArrayList2 = (ArrayList)this.mNamedGestures.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.mNamedGestures.put(paramString, localArrayList1);
    }
    localArrayList1.add(paramGesture);
    this.mClassifier.addInstance(Instance.createInstance(this.mSequenceType, this.mOrientationStyle, paramGesture, paramString));
    this.mChanged = true;
  }
  
  public Set<String> getGestureEntries()
  {
    return this.mNamedGestures.keySet();
  }
  
  public ArrayList<Gesture> getGestures(String paramString)
  {
    paramString = (ArrayList)this.mNamedGestures.get(paramString);
    if (paramString != null) {
      return new ArrayList(paramString);
    }
    return null;
  }
  
  Learner getLearner()
  {
    return this.mClassifier;
  }
  
  public int getOrientationStyle()
  {
    return this.mOrientationStyle;
  }
  
  public int getSequenceType()
  {
    return this.mSequenceType;
  }
  
  public boolean hasChanged()
  {
    return this.mChanged;
  }
  
  public void load(InputStream paramInputStream)
    throws IOException
  {
    load(paramInputStream, false);
  }
  
  public void load(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    Object localObject1 = null;
    try
    {
      if ((paramInputStream instanceof BufferedInputStream)) {}
      for (;;)
      {
        paramInputStream = new DataInputStream(paramInputStream);
        try
        {
          int i = paramInputStream.readShort();
          switch (i)
          {
          }
          for (;;)
          {
            if (paramBoolean) {
              GestureUtils.closeStream(paramInputStream);
            }
            return;
            paramInputStream = new BufferedInputStream(paramInputStream, 32768);
            break;
            readFormatV1(paramInputStream);
          }
          if (!paramBoolean) {
            break label85;
          }
        }
        finally {}
      }
    }
    finally
    {
      for (;;)
      {
        label85:
        paramInputStream = (InputStream)localObject2;
        Object localObject3 = localObject4;
      }
    }
    GestureUtils.closeStream(paramInputStream);
    throw ((Throwable)localObject2);
  }
  
  public ArrayList<Prediction> recognize(Gesture paramGesture)
  {
    paramGesture = Instance.createInstance(this.mSequenceType, this.mOrientationStyle, paramGesture, null);
    return this.mClassifier.classify(this.mSequenceType, this.mOrientationStyle, paramGesture.vector);
  }
  
  public void removeEntry(String paramString)
  {
    this.mNamedGestures.remove(paramString);
    this.mClassifier.removeInstances(paramString);
    this.mChanged = true;
  }
  
  public void removeGesture(String paramString, Gesture paramGesture)
  {
    ArrayList localArrayList = (ArrayList)this.mNamedGestures.get(paramString);
    if (localArrayList == null) {
      return;
    }
    localArrayList.remove(paramGesture);
    if (localArrayList.isEmpty()) {
      this.mNamedGestures.remove(paramString);
    }
    this.mClassifier.removeInstance(paramGesture.getID());
    this.mChanged = true;
  }
  
  public void save(OutputStream paramOutputStream)
    throws IOException
  {
    save(paramOutputStream, false);
  }
  
  /* Error */
  public void save(OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: getfield 41	android/gesture/GestureStore:mNamedGestures	Ljava/util/HashMap;
    //   7: astore 5
    //   9: aload_1
    //   10: instanceof 194
    //   13: ifeq +131 -> 144
    //   16: new 196	java/io/DataOutputStream
    //   19: dup
    //   20: aload_1
    //   21: invokespecial 198	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   24: astore_1
    //   25: aload_1
    //   26: iconst_1
    //   27: invokevirtual 201	java/io/DataOutputStream:writeShort	(I)V
    //   30: aload_1
    //   31: aload 5
    //   33: invokevirtual 204	java/util/HashMap:size	()I
    //   36: invokevirtual 207	java/io/DataOutputStream:writeInt	(I)V
    //   39: aload 5
    //   41: invokevirtual 210	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   44: invokeinterface 216 1 0
    //   49: astore 5
    //   51: aload 5
    //   53: invokeinterface 221 1 0
    //   58: ifeq +100 -> 158
    //   61: aload 5
    //   63: invokeinterface 225 1 0
    //   68: checkcast 227	java/util/Map$Entry
    //   71: astore 7
    //   73: aload 7
    //   75: invokeinterface 230 1 0
    //   80: checkcast 102	java/lang/String
    //   83: astore 6
    //   85: aload 7
    //   87: invokeinterface 233 1 0
    //   92: checkcast 68	java/util/ArrayList
    //   95: astore 7
    //   97: aload 7
    //   99: invokevirtual 234	java/util/ArrayList:size	()I
    //   102: istore 4
    //   104: aload_1
    //   105: aload 6
    //   107: invokevirtual 237	java/io/DataOutputStream:writeUTF	(Ljava/lang/String;)V
    //   110: aload_1
    //   111: iload 4
    //   113: invokevirtual 207	java/io/DataOutputStream:writeInt	(I)V
    //   116: iconst_0
    //   117: istore_3
    //   118: iload_3
    //   119: iload 4
    //   121: if_icmpge -70 -> 51
    //   124: aload 7
    //   126: iload_3
    //   127: invokevirtual 240	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   130: checkcast 73	android/gesture/Gesture
    //   133: aload_1
    //   134: invokevirtual 244	android/gesture/Gesture:serialize	(Ljava/io/DataOutputStream;)V
    //   137: iload_3
    //   138: iconst_1
    //   139: iadd
    //   140: istore_3
    //   141: goto -23 -> 118
    //   144: new 194	java/io/BufferedOutputStream
    //   147: dup
    //   148: aload_1
    //   149: ldc -107
    //   151: invokespecial 247	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   154: astore_1
    //   155: goto -139 -> 16
    //   158: aload_1
    //   159: invokevirtual 250	java/io/DataOutputStream:flush	()V
    //   162: aload_0
    //   163: iconst_0
    //   164: putfield 43	android/gesture/GestureStore:mChanged	Z
    //   167: iload_2
    //   168: ifeq +7 -> 175
    //   171: aload_1
    //   172: invokestatic 148	android/gesture/GestureUtils:closeStream	(Ljava/io/Closeable;)V
    //   175: return
    //   176: astore 5
    //   178: aload 6
    //   180: astore_1
    //   181: iload_2
    //   182: ifeq +7 -> 189
    //   185: aload_1
    //   186: invokestatic 148	android/gesture/GestureUtils:closeStream	(Ljava/io/Closeable;)V
    //   189: aload 5
    //   191: athrow
    //   192: astore 5
    //   194: goto -13 -> 181
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	197	0	this	GestureStore
    //   0	197	1	paramOutputStream	OutputStream
    //   0	197	2	paramBoolean	boolean
    //   117	24	3	i	int
    //   102	20	4	j	int
    //   7	55	5	localObject1	Object
    //   176	14	5	localObject2	Object
    //   192	1	5	localObject3	Object
    //   1	178	6	str	String
    //   71	54	7	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   3	16	176	finally
    //   16	25	176	finally
    //   144	155	176	finally
    //   25	51	192	finally
    //   51	116	192	finally
    //   124	137	192	finally
    //   158	167	192	finally
  }
  
  public void setOrientationStyle(int paramInt)
  {
    this.mOrientationStyle = paramInt;
  }
  
  public void setSequenceType(int paramInt)
  {
    this.mSequenceType = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GestureStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */