package android.print;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import java.io.File;

public class PrintFileDocumentAdapter
  extends PrintDocumentAdapter
{
  private static final String LOG_TAG = "PrintedFileDocAdapter";
  private final Context mContext;
  private final PrintDocumentInfo mDocumentInfo;
  private final File mFile;
  private WriteFileAsyncTask mWriteFileAsyncTask;
  
  public PrintFileDocumentAdapter(Context paramContext, File paramFile, PrintDocumentInfo paramPrintDocumentInfo)
  {
    if (paramFile == null) {
      throw new IllegalArgumentException("File cannot be null!");
    }
    if (paramPrintDocumentInfo == null) {
      throw new IllegalArgumentException("documentInfo cannot be null!");
    }
    this.mContext = paramContext;
    this.mFile = paramFile;
    this.mDocumentInfo = paramPrintDocumentInfo;
  }
  
  public void onLayout(PrintAttributes paramPrintAttributes1, PrintAttributes paramPrintAttributes2, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramLayoutResultCallback, Bundle paramBundle)
  {
    paramLayoutResultCallback.onLayoutFinished(this.mDocumentInfo, false);
  }
  
  public void onWrite(PageRange[] paramArrayOfPageRange, ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
  {
    this.mWriteFileAsyncTask = new WriteFileAsyncTask(paramParcelFileDescriptor, paramCancellationSignal, paramWriteResultCallback);
    this.mWriteFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
  }
  
  private final class WriteFileAsyncTask
    extends AsyncTask<Void, Void, Void>
  {
    private final CancellationSignal mCancellationSignal;
    private final ParcelFileDescriptor mDestination;
    private final PrintDocumentAdapter.WriteResultCallback mResultCallback;
    
    public WriteFileAsyncTask(ParcelFileDescriptor paramParcelFileDescriptor, CancellationSignal paramCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
    {
      this.mDestination = paramParcelFileDescriptor;
      this.mResultCallback = paramWriteResultCallback;
      this.mCancellationSignal = paramCancellationSignal;
      this.mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
      {
        public void onCancel()
        {
          PrintFileDocumentAdapter.WriteFileAsyncTask.this.cancel(true);
        }
      });
    }
    
    /* Error */
    protected Void doInBackground(Void... paramVarArgs)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_1
      //   2: aconst_null
      //   3: astore 5
      //   5: new 52	java/io/FileOutputStream
      //   8: dup
      //   9: aload_0
      //   10: getfield 27	android/print/PrintFileDocumentAdapter$WriteFileAsyncTask:mDestination	Landroid/os/ParcelFileDescriptor;
      //   13: invokevirtual 58	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
      //   16: invokespecial 61	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
      //   19: astore 6
      //   21: sipush 8192
      //   24: newarray <illegal type>
      //   26: astore 7
      //   28: new 63	java/io/FileInputStream
      //   31: dup
      //   32: aload_0
      //   33: getfield 22	android/print/PrintFileDocumentAdapter$WriteFileAsyncTask:this$0	Landroid/print/PrintFileDocumentAdapter;
      //   36: invokestatic 67	android/print/PrintFileDocumentAdapter:-get1	(Landroid/print/PrintFileDocumentAdapter;)Ljava/io/File;
      //   39: invokespecial 70	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   42: astore 4
      //   44: aload_0
      //   45: invokevirtual 74	android/os/AsyncTask:isCancelled	()Z
      //   48: istore_3
      //   49: iload_3
      //   50: ifeq +15 -> 65
      //   53: aload 4
      //   55: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   58: aload 6
      //   60: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   63: aconst_null
      //   64: areturn
      //   65: aload 4
      //   67: aload 7
      //   69: invokevirtual 84	java/io/FileInputStream:read	([B)I
      //   72: istore_2
      //   73: iload_2
      //   74: iflt -21 -> 53
      //   77: aload 6
      //   79: aload 7
      //   81: iconst_0
      //   82: iload_2
      //   83: invokevirtual 88	java/io/FileOutputStream:write	([BII)V
      //   86: goto -42 -> 44
      //   89: astore 5
      //   91: aload 4
      //   93: astore_1
      //   94: ldc 90
      //   96: ldc 92
      //   98: aload 5
      //   100: invokestatic 98	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   103: pop
      //   104: aload 4
      //   106: astore_1
      //   107: aload_0
      //   108: getfield 29	android/print/PrintFileDocumentAdapter$WriteFileAsyncTask:mResultCallback	Landroid/print/PrintDocumentAdapter$WriteResultCallback;
      //   111: aload_0
      //   112: getfield 22	android/print/PrintFileDocumentAdapter$WriteFileAsyncTask:this$0	Landroid/print/PrintFileDocumentAdapter;
      //   115: invokestatic 102	android/print/PrintFileDocumentAdapter:-get0	(Landroid/print/PrintFileDocumentAdapter;)Landroid/content/Context;
      //   118: ldc 103
      //   120: invokevirtual 109	android/content/Context:getString	(I)Ljava/lang/String;
      //   123: invokevirtual 115	android/print/PrintDocumentAdapter$WriteResultCallback:onWriteFailed	(Ljava/lang/CharSequence;)V
      //   126: aload 4
      //   128: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   131: aload 6
      //   133: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   136: goto -73 -> 63
      //   139: astore 4
      //   141: aload_1
      //   142: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   145: aload 6
      //   147: invokestatic 80	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   150: aload 4
      //   152: athrow
      //   153: astore 5
      //   155: aload 4
      //   157: astore_1
      //   158: aload 5
      //   160: astore 4
      //   162: goto -21 -> 141
      //   165: astore_1
      //   166: aload 5
      //   168: astore 4
      //   170: aload_1
      //   171: astore 5
      //   173: goto -82 -> 91
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	176	0	this	WriteFileAsyncTask
      //   0	176	1	paramVarArgs	Void[]
      //   72	11	2	i	int
      //   48	2	3	bool	boolean
      //   42	85	4	localFileInputStream	java.io.FileInputStream
      //   139	17	4	localObject1	Object
      //   160	9	4	localObject2	Object
      //   3	1	5	localObject3	Object
      //   89	10	5	localIOException	java.io.IOException
      //   153	14	5	localObject4	Object
      //   171	1	5	arrayOfVoid	Void[]
      //   19	127	6	localFileOutputStream	java.io.FileOutputStream
      //   26	54	7	arrayOfByte	byte[]
      // Exception table:
      //   from	to	target	type
      //   44	49	89	java/io/IOException
      //   65	73	89	java/io/IOException
      //   77	86	89	java/io/IOException
      //   28	44	139	finally
      //   94	104	139	finally
      //   107	126	139	finally
      //   44	49	153	finally
      //   65	73	153	finally
      //   77	86	153	finally
      //   28	44	165	java/io/IOException
    }
    
    protected void onCancelled(Void paramVoid)
    {
      this.mResultCallback.onWriteFailed(PrintFileDocumentAdapter.-get0(PrintFileDocumentAdapter.this).getString(17040801));
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      this.mResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintFileDocumentAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */