package android.drm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DrmManagerClient
  implements AutoCloseable
{
  private static final int ACTION_PROCESS_DRM_INFO = 1002;
  private static final int ACTION_REMOVE_ALL_RIGHTS = 1001;
  public static final int ERROR_NONE = 0;
  public static final int ERROR_UNKNOWN = -2000;
  public static final int INVALID_SESSION = -1;
  private static final String TAG = "DrmManagerClient";
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final AtomicBoolean mClosed = new AtomicBoolean();
  private Context mContext;
  private EventHandler mEventHandler;
  HandlerThread mEventThread;
  private InfoHandler mInfoHandler;
  HandlerThread mInfoThread;
  private long mNativeContext;
  private OnErrorListener mOnErrorListener;
  private OnEventListener mOnEventListener;
  private OnInfoListener mOnInfoListener;
  private int mUniqueId;
  
  static
  {
    System.loadLibrary("drmframework_jni");
  }
  
  public DrmManagerClient(Context paramContext)
  {
    this.mContext = paramContext;
    createEventThreads();
    this.mUniqueId = _initialize();
    this.mCloseGuard.open("release");
  }
  
  private native DrmInfo _acquireDrmInfo(int paramInt, DrmInfoRequest paramDrmInfoRequest);
  
  private native boolean _canHandle(int paramInt, String paramString1, String paramString2);
  
  private native int _checkRightsStatus(int paramInt1, String paramString, int paramInt2);
  
  private native DrmConvertedStatus _closeConvertSession(int paramInt1, int paramInt2);
  
  private native DrmConvertedStatus _convertData(int paramInt1, int paramInt2, byte[] paramArrayOfByte);
  
  private native DrmSupportInfo[] _getAllSupportInfo(int paramInt);
  
  private native ContentValues _getConstraints(int paramInt1, String paramString, int paramInt2);
  
  private native int _getDrmObjectType(int paramInt, String paramString1, String paramString2);
  
  private native ContentValues _getMetadata(int paramInt, String paramString);
  
  private native String _getOriginalMimeType(int paramInt, String paramString, FileDescriptor paramFileDescriptor);
  
  private native int _initialize();
  
  private native void _installDrmEngine(int paramInt, String paramString);
  
  private native int _openConvertSession(int paramInt, String paramString);
  
  private native DrmInfoStatus _processDrmInfo(int paramInt, DrmInfo paramDrmInfo);
  
  private native void _release(int paramInt);
  
  private native int _removeAllRights(int paramInt);
  
  private native int _removeRights(int paramInt, String paramString);
  
  private native int _saveRights(int paramInt, DrmRights paramDrmRights, String paramString1, String paramString2);
  
  private native void _setListeners(int paramInt, Object paramObject);
  
  private String convertUriToPath(Uri paramUri)
  {
    Object localObject1 = null;
    if (paramUri != null)
    {
      localObject1 = paramUri.getScheme();
      if ((localObject1 != null) && (!((String)localObject1).equals("")) && (!((String)localObject1).equals("file"))) {
        break label40;
      }
      localObject1 = paramUri.getPath();
    }
    label40:
    Object localObject2;
    do
    {
      return (String)localObject1;
      if (((String)localObject1).equals("http")) {
        return paramUri.toString();
      }
      if (!((String)localObject1).equals("content")) {
        break;
      }
      localObject2 = null;
      localObject1 = null;
      do
      {
        try
        {
          paramUri = this.mContext.getContentResolver().query(paramUri, new String[] { "_data" }, null, null, null);
          if (paramUri != null)
          {
            localObject1 = paramUri;
            localObject2 = paramUri;
            if (paramUri.getCount() != 0) {}
          }
          else
          {
            localObject1 = paramUri;
            localObject2 = paramUri;
            throw new IllegalArgumentException("Given Uri could not be found in media store");
          }
        }
        catch (SQLiteException paramUri)
        {
          localObject2 = localObject1;
          throw new IllegalArgumentException("Given Uri is not formatted in a way so that it can be found in media store.");
        }
        finally
        {
          if (localObject2 != null) {
            ((Cursor)localObject2).close();
          }
        }
        localObject1 = paramUri;
        localObject2 = paramUri;
      } while (!paramUri.moveToFirst());
      localObject1 = paramUri;
      localObject2 = paramUri;
      String str = paramUri.getString(paramUri.getColumnIndexOrThrow("_data"));
      localObject2 = str;
      localObject1 = localObject2;
    } while (paramUri == null);
    paramUri.close();
    return (String)localObject2;
    throw new IllegalArgumentException("Given Uri scheme is not supported");
  }
  
  private void createEventThreads()
  {
    if ((this.mEventHandler == null) && (this.mInfoHandler == null))
    {
      this.mInfoThread = new HandlerThread("DrmManagerClient.InfoHandler");
      this.mInfoThread.start();
      this.mInfoHandler = new InfoHandler(this.mInfoThread.getLooper());
      this.mEventThread = new HandlerThread("DrmManagerClient.EventHandler");
      this.mEventThread.start();
      this.mEventHandler = new EventHandler(this.mEventThread.getLooper());
    }
  }
  
  private void createListeners()
  {
    _setListeners(this.mUniqueId, new WeakReference(this));
  }
  
  private int getErrorType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    }
    return 2006;
  }
  
  private int getEventType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    }
    return 1002;
  }
  
  public static void notify(Object paramObject, int paramInt1, int paramInt2, String paramString)
  {
    paramObject = (DrmManagerClient)((WeakReference)paramObject).get();
    if ((paramObject != null) && (((DrmManagerClient)paramObject).mInfoHandler != null))
    {
      paramString = ((DrmManagerClient)paramObject).mInfoHandler.obtainMessage(1, paramInt1, paramInt2, paramString);
      ((DrmManagerClient)paramObject).mInfoHandler.sendMessage(paramString);
    }
  }
  
  public DrmInfo acquireDrmInfo(DrmInfoRequest paramDrmInfoRequest)
  {
    if ((paramDrmInfoRequest != null) && (paramDrmInfoRequest.isValid())) {
      return _acquireDrmInfo(this.mUniqueId, paramDrmInfoRequest);
    }
    throw new IllegalArgumentException("Given drmInfoRequest is invalid/null");
  }
  
  public int acquireRights(DrmInfoRequest paramDrmInfoRequest)
  {
    paramDrmInfoRequest = acquireDrmInfo(paramDrmInfoRequest);
    if (paramDrmInfoRequest == null) {
      return 63536;
    }
    return processDrmInfo(paramDrmInfoRequest);
  }
  
  public boolean canHandle(Uri paramUri, String paramString)
  {
    if (((paramUri == null) || (Uri.EMPTY == paramUri)) && ((paramString == null) || (paramString.equals("")))) {
      throw new IllegalArgumentException("Uri or the mimetype should be non null");
    }
    return canHandle(convertUriToPath(paramUri), paramString);
  }
  
  public boolean canHandle(String paramString1, String paramString2)
  {
    if (((paramString1 == null) || (paramString1.equals(""))) && ((paramString2 == null) || (paramString2.equals("")))) {
      throw new IllegalArgumentException("Path or the mimetype should be non null");
    }
    return _canHandle(this.mUniqueId, paramString1, paramString2);
  }
  
  public int checkRightsStatus(Uri paramUri)
  {
    if ((paramUri == null) || (Uri.EMPTY == paramUri)) {
      throw new IllegalArgumentException("Given uri is not valid");
    }
    return checkRightsStatus(convertUriToPath(paramUri));
  }
  
  public int checkRightsStatus(Uri paramUri, int paramInt)
  {
    if ((paramUri == null) || (Uri.EMPTY == paramUri)) {
      throw new IllegalArgumentException("Given uri is not valid");
    }
    return checkRightsStatus(convertUriToPath(paramUri), paramInt);
  }
  
  public int checkRightsStatus(String paramString)
  {
    return checkRightsStatus(paramString, 0);
  }
  
  public int checkRightsStatus(String paramString, int paramInt)
  {
    if ((paramString != null) && (!paramString.equals("")) && (DrmStore.Action.isValid(paramInt))) {
      return _checkRightsStatus(this.mUniqueId, paramString, paramInt);
    }
    throw new IllegalArgumentException("Given path or action is not valid");
  }
  
  public void close()
  {
    this.mCloseGuard.close();
    if (this.mClosed.compareAndSet(false, true))
    {
      if (this.mEventHandler != null)
      {
        this.mEventThread.quit();
        this.mEventThread = null;
      }
      if (this.mInfoHandler != null)
      {
        this.mInfoThread.quit();
        this.mInfoThread = null;
      }
      this.mEventHandler = null;
      this.mInfoHandler = null;
      this.mOnEventListener = null;
      this.mOnInfoListener = null;
      this.mOnErrorListener = null;
      _release(this.mUniqueId);
    }
  }
  
  public DrmConvertedStatus closeConvertSession(int paramInt)
  {
    return _closeConvertSession(this.mUniqueId, paramInt);
  }
  
  public DrmConvertedStatus convertData(int paramInt, byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length <= 0)) {
      throw new IllegalArgumentException("Given inputData should be non null");
    }
    return _convertData(this.mUniqueId, paramInt, paramArrayOfByte);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mCloseGuard.warnIfOpen();
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public String[] getAvailableDrmEngines()
  {
    DrmSupportInfo[] arrayOfDrmSupportInfo = _getAllSupportInfo(this.mUniqueId);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < arrayOfDrmSupportInfo.length)
    {
      localArrayList.add(arrayOfDrmSupportInfo[i].getDescriprition());
      i += 1;
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public ContentValues getConstraints(Uri paramUri, int paramInt)
  {
    if ((paramUri == null) || (Uri.EMPTY == paramUri)) {
      throw new IllegalArgumentException("Uri should be non null");
    }
    return getConstraints(convertUriToPath(paramUri), paramInt);
  }
  
  public ContentValues getConstraints(String paramString, int paramInt)
  {
    if ((paramString != null) && (!paramString.equals("")) && (DrmStore.Action.isValid(paramInt))) {
      return _getConstraints(this.mUniqueId, paramString, paramInt);
    }
    throw new IllegalArgumentException("Given usage or path is invalid/null");
  }
  
  public int getDrmObjectType(Uri paramUri, String paramString)
  {
    if (((paramUri == null) || (Uri.EMPTY == paramUri)) && ((paramString == null) || (paramString.equals("")))) {
      throw new IllegalArgumentException("Uri or the mimetype should be non null");
    }
    String str = "";
    try
    {
      paramUri = convertUriToPath(paramUri);
      return getDrmObjectType(paramUri, paramString);
    }
    catch (Exception paramUri)
    {
      for (;;)
      {
        Log.w("DrmManagerClient", "Given Uri could not be found in media store");
        paramUri = str;
      }
    }
  }
  
  public int getDrmObjectType(String paramString1, String paramString2)
  {
    if (((paramString1 == null) || (paramString1.equals(""))) && ((paramString2 == null) || (paramString2.equals("")))) {
      throw new IllegalArgumentException("Path or the mimetype should be non null");
    }
    return _getDrmObjectType(this.mUniqueId, paramString1, paramString2);
  }
  
  public ContentValues getMetadata(Uri paramUri)
  {
    if ((paramUri == null) || (Uri.EMPTY == paramUri)) {
      throw new IllegalArgumentException("Uri should be non null");
    }
    return getMetadata(convertUriToPath(paramUri));
  }
  
  public ContentValues getMetadata(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      throw new IllegalArgumentException("Given path is invalid/null");
    }
    return _getMetadata(this.mUniqueId, paramString);
  }
  
  public String getOriginalMimeType(Uri paramUri)
  {
    if ((paramUri == null) || (Uri.EMPTY == paramUri)) {
      throw new IllegalArgumentException("Given uri is not valid");
    }
    return getOriginalMimeType(convertUriToPath(paramUri));
  }
  
  /* Error */
  public String getOriginalMimeType(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +12 -> 13
    //   4: aload_1
    //   5: ldc -77
    //   7: invokevirtual 185	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   10: ifeq +14 -> 24
    //   13: new 218	java/lang/IllegalArgumentException
    //   16: dup
    //   17: ldc_w 439
    //   20: invokespecial 222	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aconst_null
    //   25: astore 6
    //   27: aconst_null
    //   28: astore 7
    //   30: aconst_null
    //   31: astore 8
    //   33: aconst_null
    //   34: astore_2
    //   35: aconst_null
    //   36: astore 5
    //   38: aload 7
    //   40: astore 4
    //   42: aload 8
    //   44: astore_3
    //   45: new 441	java/io/File
    //   48: dup
    //   49: aload_1
    //   50: invokespecial 442	java/io/File:<init>	(Ljava/lang/String;)V
    //   53: astore 9
    //   55: aload 7
    //   57: astore 4
    //   59: aload 8
    //   61: astore_3
    //   62: aload 9
    //   64: invokevirtual 445	java/io/File:exists	()Z
    //   67: ifeq +26 -> 93
    //   70: aload 7
    //   72: astore 4
    //   74: aload 8
    //   76: astore_3
    //   77: new 447	java/io/FileInputStream
    //   80: dup
    //   81: aload 9
    //   83: invokespecial 450	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   86: astore_2
    //   87: aload_2
    //   88: invokevirtual 454	java/io/FileInputStream:getFD	()Ljava/io/FileDescriptor;
    //   91: astore 5
    //   93: aload_2
    //   94: astore 4
    //   96: aload_2
    //   97: astore_3
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 75	android/drm/DrmManagerClient:mUniqueId	I
    //   103: aload_1
    //   104: aload 5
    //   106: invokespecial 456	android/drm/DrmManagerClient:_getOriginalMimeType	(ILjava/lang/String;Ljava/io/FileDescriptor;)Ljava/lang/String;
    //   109: astore_1
    //   110: aload_1
    //   111: astore_3
    //   112: aload_2
    //   113: ifnull +9 -> 122
    //   116: aload_2
    //   117: invokevirtual 457	java/io/FileInputStream:close	()V
    //   120: aload_1
    //   121: astore_3
    //   122: aload_3
    //   123: areturn
    //   124: astore_2
    //   125: aload_1
    //   126: areturn
    //   127: astore_1
    //   128: aload 6
    //   130: astore_3
    //   131: aload 4
    //   133: ifnull -11 -> 122
    //   136: aload 4
    //   138: invokevirtual 457	java/io/FileInputStream:close	()V
    //   141: aconst_null
    //   142: areturn
    //   143: astore_1
    //   144: aconst_null
    //   145: areturn
    //   146: astore_1
    //   147: aload_3
    //   148: ifnull +7 -> 155
    //   151: aload_3
    //   152: invokevirtual 457	java/io/FileInputStream:close	()V
    //   155: aload_1
    //   156: athrow
    //   157: astore_2
    //   158: goto -3 -> 155
    //   161: astore_1
    //   162: aload_2
    //   163: astore_3
    //   164: goto -17 -> 147
    //   167: astore_1
    //   168: aload_2
    //   169: astore 4
    //   171: goto -43 -> 128
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	174	0	this	DrmManagerClient
    //   0	174	1	paramString	String
    //   34	83	2	localFileInputStream	java.io.FileInputStream
    //   124	1	2	localIOException1	IOException
    //   157	12	2	localIOException2	IOException
    //   44	120	3	localObject1	Object
    //   40	130	4	localObject2	Object
    //   36	69	5	localFileDescriptor	FileDescriptor
    //   25	104	6	localObject3	Object
    //   28	43	7	localObject4	Object
    //   31	44	8	localObject5	Object
    //   53	29	9	localFile	java.io.File
    // Exception table:
    //   from	to	target	type
    //   116	120	124	java/io/IOException
    //   45	55	127	java/io/IOException
    //   62	70	127	java/io/IOException
    //   77	87	127	java/io/IOException
    //   98	110	127	java/io/IOException
    //   136	141	143	java/io/IOException
    //   45	55	146	finally
    //   62	70	146	finally
    //   77	87	146	finally
    //   98	110	146	finally
    //   151	155	157	java/io/IOException
    //   87	93	161	finally
    //   87	93	167	java/io/IOException
  }
  
  public void installDrmEngine(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      throw new IllegalArgumentException("Given engineFilePath: " + paramString + "is not valid");
    }
    _installDrmEngine(this.mUniqueId, paramString);
  }
  
  public int openConvertSession(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      throw new IllegalArgumentException("Path or the mimeType should be non null");
    }
    return _openConvertSession(this.mUniqueId, paramString);
  }
  
  public int processDrmInfo(DrmInfo paramDrmInfo)
  {
    if ((paramDrmInfo != null) && (paramDrmInfo.isValid()))
    {
      int i = 63536;
      if (this.mEventHandler != null)
      {
        paramDrmInfo = this.mEventHandler.obtainMessage(1002, paramDrmInfo);
        if (this.mEventHandler.sendMessage(paramDrmInfo)) {
          i = 0;
        }
      }
      else
      {
        return i;
      }
    }
    else
    {
      throw new IllegalArgumentException("Given drmInfo is invalid/null");
    }
    return 63536;
  }
  
  @Deprecated
  public void release()
  {
    close();
  }
  
  public int removeAllRights()
  {
    int i = 63536;
    if (this.mEventHandler != null)
    {
      Message localMessage = this.mEventHandler.obtainMessage(1001);
      if (this.mEventHandler.sendMessage(localMessage)) {
        i = 0;
      }
    }
    else
    {
      return i;
    }
    return 63536;
  }
  
  public int removeRights(Uri paramUri)
  {
    if ((paramUri == null) || (Uri.EMPTY == paramUri)) {
      throw new IllegalArgumentException("Given uri is not valid");
    }
    return removeRights(convertUriToPath(paramUri));
  }
  
  public int removeRights(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      throw new IllegalArgumentException("Given path should be non null");
    }
    return _removeRights(this.mUniqueId, paramString);
  }
  
  public int saveRights(DrmRights paramDrmRights, String paramString1, String paramString2)
    throws IOException
  {
    if ((paramDrmRights != null) && (paramDrmRights.isValid())) {
      if ((paramString1 != null) && (!paramString1.equals(""))) {
        break label47;
      }
    }
    for (;;)
    {
      return _saveRights(this.mUniqueId, paramDrmRights, paramString1, paramString2);
      throw new IllegalArgumentException("Given drmRights or contentPath is not valid");
      label47:
      DrmUtils.writeToFile(paramString1, paramDrmRights.getData());
    }
  }
  
  public void setOnErrorListener(OnErrorListener paramOnErrorListener)
  {
    try
    {
      this.mOnErrorListener = paramOnErrorListener;
      if (paramOnErrorListener != null) {
        createListeners();
      }
      return;
    }
    finally {}
  }
  
  public void setOnEventListener(OnEventListener paramOnEventListener)
  {
    try
    {
      this.mOnEventListener = paramOnEventListener;
      if (paramOnEventListener != null) {
        createListeners();
      }
      return;
    }
    finally {}
  }
  
  public void setOnInfoListener(OnInfoListener paramOnInfoListener)
  {
    try
    {
      this.mOnInfoListener = paramOnInfoListener;
      if (paramOnInfoListener != null) {
        createListeners();
      }
      return;
    }
    finally {}
  }
  
  private class EventHandler
    extends Handler
  {
    public EventHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      DrmEvent localDrmEvent = null;
      Object localObject = null;
      HashMap localHashMap = new HashMap();
      DrmInfoStatus localDrmInfoStatus;
      switch (paramMessage.what)
      {
      default: 
        Log.e("DrmManagerClient", "Unknown message type " + paramMessage.what);
        return;
      case 1002: 
        paramMessage = (DrmInfo)paramMessage.obj;
        localDrmInfoStatus = DrmManagerClient.-wrap0(DrmManagerClient.this, DrmManagerClient.-get3(DrmManagerClient.this), paramMessage);
        localHashMap.put("drm_info_status_object", localDrmInfoStatus);
        localHashMap.put("drm_info_object", paramMessage);
        if ((localDrmInfoStatus != null) && (1 == localDrmInfoStatus.statusCode))
        {
          localDrmEvent = new DrmEvent(DrmManagerClient.-get3(DrmManagerClient.this), DrmManagerClient.-wrap3(DrmManagerClient.this, localDrmInfoStatus.infoType), null, localHashMap);
          paramMessage = (Message)localObject;
        }
        break;
      }
      for (;;)
      {
        if ((DrmManagerClient.-get1(DrmManagerClient.this) != null) && (localDrmEvent != null)) {
          DrmManagerClient.-get1(DrmManagerClient.this).onEvent(DrmManagerClient.this, localDrmEvent);
        }
        if ((DrmManagerClient.-get0(DrmManagerClient.this) != null) && (paramMessage != null)) {
          DrmManagerClient.-get0(DrmManagerClient.this).onError(DrmManagerClient.this, paramMessage);
        }
        return;
        if (localDrmInfoStatus != null) {}
        for (int i = localDrmInfoStatus.infoType;; i = paramMessage.getInfoType())
        {
          paramMessage = new DrmErrorEvent(DrmManagerClient.-get3(DrmManagerClient.this), DrmManagerClient.-wrap2(DrmManagerClient.this, i), null, localHashMap);
          break;
        }
        if (DrmManagerClient.-wrap1(DrmManagerClient.this, DrmManagerClient.-get3(DrmManagerClient.this)) == 0)
        {
          localDrmEvent = new DrmEvent(DrmManagerClient.-get3(DrmManagerClient.this), 1001, null);
          paramMessage = (Message)localObject;
        }
        else
        {
          paramMessage = new DrmErrorEvent(DrmManagerClient.-get3(DrmManagerClient.this), 2007, null);
        }
      }
    }
  }
  
  private class InfoHandler
    extends Handler
  {
    public static final int INFO_EVENT_TYPE = 1;
    
    public InfoHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject = null;
      DrmErrorEvent localDrmErrorEvent = null;
      switch (paramMessage.what)
      {
      default: 
        Log.e("DrmManagerClient", "Unknown message type " + paramMessage.what);
        return;
      }
      int i = paramMessage.arg1;
      int j = paramMessage.arg2;
      paramMessage = paramMessage.obj.toString();
      switch (j)
      {
      default: 
        localDrmErrorEvent = new DrmErrorEvent(i, j, paramMessage);
      }
      for (paramMessage = (Message)localObject;; paramMessage = new DrmInfoEvent(i, j, paramMessage)) {
        for (;;)
        {
          if ((DrmManagerClient.-get2(DrmManagerClient.this) != null) && (paramMessage != null)) {
            DrmManagerClient.-get2(DrmManagerClient.this).onInfo(DrmManagerClient.this, paramMessage);
          }
          if ((DrmManagerClient.-get0(DrmManagerClient.this) != null) && (localDrmErrorEvent != null)) {
            DrmManagerClient.-get0(DrmManagerClient.this).onError(DrmManagerClient.this, localDrmErrorEvent);
          }
          return;
          try
          {
            DrmUtils.removeFile(paramMessage);
            paramMessage = new DrmInfoEvent(i, j, paramMessage);
          }
          catch (IOException localIOException)
          {
            for (;;)
            {
              localIOException.printStackTrace();
            }
          }
        }
      }
    }
  }
  
  public static abstract interface OnErrorListener
  {
    public abstract void onError(DrmManagerClient paramDrmManagerClient, DrmErrorEvent paramDrmErrorEvent);
  }
  
  public static abstract interface OnEventListener
  {
    public abstract void onEvent(DrmManagerClient paramDrmManagerClient, DrmEvent paramDrmEvent);
  }
  
  public static abstract interface OnInfoListener
  {
    public abstract void onInfo(DrmManagerClient paramDrmManagerClient, DrmInfoEvent paramDrmInfoEvent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmManagerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */