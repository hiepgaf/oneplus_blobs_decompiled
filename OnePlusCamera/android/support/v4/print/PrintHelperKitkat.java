package android.support.v4.print;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class PrintHelperKitkat
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  private static final String LOG_TAG = "PrintHelperKitkat";
  private static final int MAX_PRINT_SIZE = 3500;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  int mColorMode = 2;
  final Context mContext;
  BitmapFactory.Options mDecodeOptions = null;
  private final Object mLock = new Object();
  int mOrientation = 1;
  int mScaleMode = 2;
  
  PrintHelperKitkat(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
  {
    Matrix localMatrix = new Matrix();
    float f = paramRectF.width() / paramInt1;
    if (paramInt3 != 2) {}
    for (f = Math.min(f, paramRectF.height() / paramInt2);; f = Math.max(f, paramRectF.height() / paramInt2))
    {
      localMatrix.postScale(f, f);
      localMatrix.postTranslate((paramRectF.width() - paramInt1 * f) / 2.0F, (paramRectF.height() - f * paramInt2) / 2.0F);
      return localMatrix;
    }
  }
  
  private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
    throws FileNotFoundException
  {
    Uri localUri = null;
    if (paramUri == null) {}
    while (this.mContext == null) {
      throw new IllegalArgumentException("bad argument to loadBitmap");
    }
    try
    {
      paramUri = this.mContext.getContentResolver().openInputStream(paramUri);
      localUri = paramUri;
      paramOptions = BitmapFactory.decodeStream(paramUri, null, paramOptions);
      return paramOptions;
      try
      {
        paramUri.close();
        return paramOptions;
      }
      catch (IOException paramUri)
      {
        Log.w("PrintHelperKitkat", "close fail ", paramUri);
        return paramOptions;
      }
      throw paramUri;
    }
    finally
    {
      if (localUri != null) {}
    }
    for (;;)
    {
      try
      {
        localUri.close();
      }
      catch (IOException paramOptions)
      {
        Log.w("PrintHelperKitkat", "close fail ", paramOptions);
      }
    }
  }
  
  /* Error */
  private Bitmap loadConstrainedBitmap(Uri arg1, int paramInt)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: iload_2
    //   3: ifgt +13 -> 16
    //   6: new 108	java/lang/IllegalArgumentException
    //   9: dup
    //   10: ldc -110
    //   12: invokespecial 113	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   15: athrow
    //   16: aload_1
    //   17: ifnull -11 -> 6
    //   20: aload_0
    //   21: getfield 56	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
    //   24: ifnull -18 -> 6
    //   27: new 148	android/graphics/BitmapFactory$Options
    //   30: dup
    //   31: invokespecial 149	android/graphics/BitmapFactory$Options:<init>	()V
    //   34: astore 7
    //   36: aload 7
    //   38: iconst_1
    //   39: putfield 153	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   42: aload_0
    //   43: aload_1
    //   44: aload 7
    //   46: invokespecial 155	android/support/v4/print/PrintHelperKitkat:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   49: pop
    //   50: aload 7
    //   52: getfield 158	android/graphics/BitmapFactory$Options:outWidth	I
    //   55: istore 5
    //   57: aload 7
    //   59: getfield 161	android/graphics/BitmapFactory$Options:outHeight	I
    //   62: istore 6
    //   64: iload 5
    //   66: ifgt +5 -> 71
    //   69: aconst_null
    //   70: areturn
    //   71: iload 6
    //   73: ifle -4 -> 69
    //   76: iload 5
    //   78: iload 6
    //   80: invokestatic 164	java/lang/Math:max	(II)I
    //   83: istore 4
    //   85: iload 4
    //   87: iload_2
    //   88: if_icmpgt +9 -> 97
    //   91: iload_3
    //   92: ifgt +18 -> 110
    //   95: aconst_null
    //   96: areturn
    //   97: iload 4
    //   99: iconst_1
    //   100: iushr
    //   101: istore 4
    //   103: iload_3
    //   104: iconst_1
    //   105: ishl
    //   106: istore_3
    //   107: goto -22 -> 85
    //   110: iload 5
    //   112: iload 6
    //   114: invokestatic 166	java/lang/Math:min	(II)I
    //   117: iload_3
    //   118: idiv
    //   119: ifle -24 -> 95
    //   122: aload_0
    //   123: getfield 48	android/support/v4/print/PrintHelperKitkat:mLock	Ljava/lang/Object;
    //   126: astore 7
    //   128: aload 7
    //   130: monitorenter
    //   131: aload_0
    //   132: new 148	android/graphics/BitmapFactory$Options
    //   135: dup
    //   136: invokespecial 149	android/graphics/BitmapFactory$Options:<init>	()V
    //   139: putfield 46	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   142: aload_0
    //   143: getfield 46	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   146: iconst_1
    //   147: putfield 169	android/graphics/BitmapFactory$Options:inMutable	Z
    //   150: aload_0
    //   151: getfield 46	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   154: iload_3
    //   155: putfield 172	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   158: aload_0
    //   159: getfield 46	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   162: astore 8
    //   164: aload 7
    //   166: monitorexit
    //   167: aload_0
    //   168: aload_1
    //   169: aload 8
    //   171: invokespecial 155	android/support/v4/print/PrintHelperKitkat:loadBitmap	(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   174: astore 7
    //   176: aload_0
    //   177: getfield 48	android/support/v4/print/PrintHelperKitkat:mLock	Ljava/lang/Object;
    //   180: astore_1
    //   181: aload_1
    //   182: monitorenter
    //   183: aload_0
    //   184: aconst_null
    //   185: putfield 46	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   188: aload_1
    //   189: monitorexit
    //   190: aload 7
    //   192: areturn
    //   193: astore_1
    //   194: aload 7
    //   196: monitorexit
    //   197: aload_1
    //   198: athrow
    //   199: astore 7
    //   201: aload_1
    //   202: monitorexit
    //   203: aload 7
    //   205: athrow
    //   206: astore 7
    //   208: aload_0
    //   209: getfield 48	android/support/v4/print/PrintHelperKitkat:mLock	Ljava/lang/Object;
    //   212: astore_1
    //   213: aload_1
    //   214: monitorenter
    //   215: aload_0
    //   216: aconst_null
    //   217: putfield 46	android/support/v4/print/PrintHelperKitkat:mDecodeOptions	Landroid/graphics/BitmapFactory$Options;
    //   220: aload_1
    //   221: monitorexit
    //   222: aload 7
    //   224: athrow
    //   225: astore 7
    //   227: aload_1
    //   228: monitorexit
    //   229: aload 7
    //   231: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	PrintHelperKitkat
    //   0	232	2	paramInt	int
    //   1	154	3	i	int
    //   83	19	4	j	int
    //   55	56	5	k	int
    //   62	51	6	m	int
    //   34	161	7	localObject1	Object
    //   199	5	7	localObject2	Object
    //   206	17	7	localObject3	Object
    //   225	5	7	localObject4	Object
    //   162	8	8	localOptions	BitmapFactory.Options
    // Exception table:
    //   from	to	target	type
    //   131	167	193	finally
    //   194	197	193	finally
    //   183	190	199	finally
    //   201	203	199	finally
    //   167	176	206	finally
    //   215	222	225	finally
    //   227	229	225	finally
  }
  
  public int getColorMode()
  {
    return this.mColorMode;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public int getScaleMode()
  {
    return this.mScaleMode;
  }
  
  public void printBitmap(final String paramString, final Bitmap paramBitmap, final OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    final int i;
    PrintManager localPrintManager;
    Object localObject;
    if (paramBitmap != null)
    {
      i = this.mScaleMode;
      localPrintManager = (PrintManager)this.mContext.getSystemService("print");
      localObject = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
      if (paramBitmap.getWidth() > paramBitmap.getHeight()) {
        break label88;
      }
    }
    for (;;)
    {
      localObject = new PrintAttributes.Builder().setMediaSize((PrintAttributes.MediaSize)localObject).setColorMode(this.mColorMode).build();
      localPrintManager.print(paramString, new PrintDocumentAdapter()
      {
        private PrintAttributes mAttributes;
        
        public void onFinish()
        {
          if (paramOnPrintFinishCallback == null) {
            return;
          }
          paramOnPrintFinishCallback.onFinish();
        }
        
        public void onLayout(PrintAttributes paramAnonymousPrintAttributes1, PrintAttributes paramAnonymousPrintAttributes2, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
        {
          boolean bool = false;
          this.mAttributes = paramAnonymousPrintAttributes2;
          paramAnonymousCancellationSignal = new PrintDocumentInfo.Builder(paramString).setContentType(1).setPageCount(1).build();
          if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
          for (;;)
          {
            paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymousCancellationSignal, bool);
            return;
            bool = true;
          }
        }
        
        public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
        {
          paramAnonymousArrayOfPageRange = new PrintedPdfDocument(PrintHelperKitkat.this.mContext, this.mAttributes);
          try
          {
            paramAnonymousCancellationSignal = paramAnonymousArrayOfPageRange.startPage(1);
            Object localObject = new RectF(paramAnonymousCancellationSignal.getInfo().getContentRect());
            localObject = PrintHelperKitkat.this.getMatrix(paramBitmap.getWidth(), paramBitmap.getHeight(), (RectF)localObject, i);
            paramAnonymousCancellationSignal.getCanvas().drawBitmap(paramBitmap, (Matrix)localObject, null);
            paramAnonymousArrayOfPageRange.finishPage(paramAnonymousCancellationSignal);
            try
            {
              paramAnonymousArrayOfPageRange.writeTo(new FileOutputStream(paramAnonymousParcelFileDescriptor.getFileDescriptor()));
              paramAnonymousWriteResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
              if (paramAnonymousArrayOfPageRange == null) {
                return;
              }
            }
            catch (IOException paramAnonymousCancellationSignal)
            {
              for (;;)
              {
                Log.e("PrintHelperKitkat", "Error writing printed content", paramAnonymousCancellationSignal);
                paramAnonymousWriteResultCallback.onWriteFailed(null);
              }
            }
          }
          finally
          {
            if (paramAnonymousArrayOfPageRange != null) {}
          }
          for (;;)
          {
            throw paramAnonymousCancellationSignal;
            paramAnonymousArrayOfPageRange.close();
            break;
            try
            {
              paramAnonymousParcelFileDescriptor.close();
              return;
            }
            catch (IOException paramAnonymousArrayOfPageRange)
            {
              return;
            }
            paramAnonymousArrayOfPageRange.close();
            continue;
            try
            {
              paramAnonymousParcelFileDescriptor.close();
            }
            catch (IOException paramAnonymousArrayOfPageRange) {}
          }
        }
      }, (PrintAttributes)localObject);
      return;
      return;
      label88:
      localObject = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
    }
  }
  
  public void printBitmap(final String paramString, final Uri paramUri, final OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    paramUri = new PrintDocumentAdapter()
    {
      private PrintAttributes mAttributes;
      Bitmap mBitmap = null;
      AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
      
      private void cancelLoad()
      {
        synchronized (PrintHelperKitkat.this.mLock)
        {
          if (PrintHelperKitkat.this.mDecodeOptions == null) {
            return;
          }
          PrintHelperKitkat.this.mDecodeOptions.requestCancelDecode();
          PrintHelperKitkat.this.mDecodeOptions = null;
        }
      }
      
      public void onFinish()
      {
        super.onFinish();
        cancelLoad();
        if (this.mLoadBitmap == null) {}
        while (paramOnPrintFinishCallback == null)
        {
          return;
          this.mLoadBitmap.cancel(true);
        }
        paramOnPrintFinishCallback.onFinish();
      }
      
      public void onLayout(final PrintAttributes paramAnonymousPrintAttributes1, final PrintAttributes paramAnonymousPrintAttributes2, final CancellationSignal paramAnonymousCancellationSignal, final PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
      {
        boolean bool = true;
        this.mAttributes = paramAnonymousPrintAttributes2;
        if (!paramAnonymousCancellationSignal.isCanceled())
        {
          if (this.mBitmap == null) {
            this.mLoadBitmap = new AsyncTask()
            {
              protected Bitmap doInBackground(Uri... paramAnonymous2VarArgs)
              {
                try
                {
                  paramAnonymous2VarArgs = PrintHelperKitkat.this.loadConstrainedBitmap(PrintHelperKitkat.2.this.val$imageFile, 3500);
                  return paramAnonymous2VarArgs;
                }
                catch (FileNotFoundException paramAnonymous2VarArgs) {}
                return null;
              }
              
              protected void onCancelled(Bitmap paramAnonymous2Bitmap)
              {
                paramAnonymousLayoutResultCallback.onLayoutCancelled();
                PrintHelperKitkat.2.this.mLoadBitmap = null;
              }
              
              protected void onPostExecute(Bitmap paramAnonymous2Bitmap)
              {
                boolean bool = false;
                super.onPostExecute(paramAnonymous2Bitmap);
                PrintHelperKitkat.2.this.mBitmap = paramAnonymous2Bitmap;
                if (paramAnonymous2Bitmap == null)
                {
                  paramAnonymousLayoutResultCallback.onLayoutFailed(null);
                  PrintHelperKitkat.2.this.mLoadBitmap = null;
                  return;
                }
                paramAnonymous2Bitmap = new PrintDocumentInfo.Builder(PrintHelperKitkat.2.this.val$jobName).setContentType(1).setPageCount(1).build();
                if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
                for (;;)
                {
                  paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymous2Bitmap, bool);
                  break;
                  bool = true;
                }
              }
              
              protected void onPreExecute()
              {
                paramAnonymousCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
                {
                  public void onCancel()
                  {
                    PrintHelperKitkat.2.this.cancelLoad();
                    PrintHelperKitkat.2.1.this.cancel(false);
                  }
                });
              }
            }.execute(new Uri[0]);
          }
        }
        else
        {
          paramAnonymousLayoutResultCallback.onLayoutCancelled();
          return;
        }
        paramAnonymousCancellationSignal = new PrintDocumentInfo.Builder(paramString).setContentType(1).setPageCount(1).build();
        if (paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {
          bool = false;
        }
        paramAnonymousLayoutResultCallback.onLayoutFinished(paramAnonymousCancellationSignal, bool);
      }
      
      public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
      {
        paramAnonymousArrayOfPageRange = new PrintedPdfDocument(PrintHelperKitkat.this.mContext, this.mAttributes);
        try
        {
          paramAnonymousCancellationSignal = paramAnonymousArrayOfPageRange.startPage(1);
          Object localObject = new RectF(paramAnonymousCancellationSignal.getInfo().getContentRect());
          localObject = PrintHelperKitkat.this.getMatrix(this.mBitmap.getWidth(), this.mBitmap.getHeight(), (RectF)localObject, this.val$fittingMode);
          paramAnonymousCancellationSignal.getCanvas().drawBitmap(this.mBitmap, (Matrix)localObject, null);
          paramAnonymousArrayOfPageRange.finishPage(paramAnonymousCancellationSignal);
          try
          {
            paramAnonymousArrayOfPageRange.writeTo(new FileOutputStream(paramAnonymousParcelFileDescriptor.getFileDescriptor()));
            paramAnonymousWriteResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
            if (paramAnonymousArrayOfPageRange == null) {
              return;
            }
          }
          catch (IOException paramAnonymousCancellationSignal)
          {
            for (;;)
            {
              Log.e("PrintHelperKitkat", "Error writing printed content", paramAnonymousCancellationSignal);
              paramAnonymousWriteResultCallback.onWriteFailed(null);
            }
          }
        }
        finally
        {
          if (paramAnonymousArrayOfPageRange != null) {}
        }
        for (;;)
        {
          throw paramAnonymousCancellationSignal;
          paramAnonymousArrayOfPageRange.close();
          break;
          try
          {
            paramAnonymousParcelFileDescriptor.close();
            return;
          }
          catch (IOException paramAnonymousArrayOfPageRange)
          {
            return;
          }
          paramAnonymousArrayOfPageRange.close();
          continue;
          try
          {
            paramAnonymousParcelFileDescriptor.close();
          }
          catch (IOException paramAnonymousArrayOfPageRange) {}
        }
      }
    };
    paramOnPrintFinishCallback = (PrintManager)this.mContext.getSystemService("print");
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setColorMode(this.mColorMode);
    if (this.mOrientation != 1) {
      if (this.mOrientation == 2) {
        break label89;
      }
    }
    for (;;)
    {
      paramOnPrintFinishCallback.print(paramString, paramUri, localBuilder.build());
      return;
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
      continue;
      label89:
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
    }
  }
  
  public void setColorMode(int paramInt)
  {
    this.mColorMode = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
  }
  
  public void setScaleMode(int paramInt)
  {
    this.mScaleMode = paramInt;
  }
  
  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/print/PrintHelperKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */