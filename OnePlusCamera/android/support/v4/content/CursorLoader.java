package android.support.v4.content;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

public class CursorLoader
  extends AsyncTaskLoader<Cursor>
{
  Cursor mCursor;
  final Loader<Cursor>.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver(this);
  String[] mProjection;
  String mSelection;
  String[] mSelectionArgs;
  String mSortOrder;
  Uri mUri;
  
  public CursorLoader(Context paramContext)
  {
    super(paramContext);
  }
  
  public CursorLoader(Context paramContext, Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    super(paramContext);
    this.mUri = paramUri;
    this.mProjection = paramArrayOfString1;
    this.mSelection = paramString1;
    this.mSelectionArgs = paramArrayOfString2;
    this.mSortOrder = paramString2;
  }
  
  public void deliverResult(Cursor paramCursor)
  {
    Cursor localCursor;
    if (!isReset())
    {
      localCursor = this.mCursor;
      this.mCursor = paramCursor;
      if (isStarted()) {
        break label41;
      }
      if (localCursor != null) {
        break label49;
      }
    }
    label41:
    label49:
    while ((localCursor == paramCursor) || (localCursor.isClosed()))
    {
      return;
      if (paramCursor == null) {
        return;
      }
      paramCursor.close();
      return;
      super.deliverResult(paramCursor);
      break;
    }
    localCursor.close();
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mUri=");
    paramPrintWriter.println(this.mUri);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mProjection=");
    paramPrintWriter.println(Arrays.toString(this.mProjection));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSelection=");
    paramPrintWriter.println(this.mSelection);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSelectionArgs=");
    paramPrintWriter.println(Arrays.toString(this.mSelectionArgs));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mSortOrder=");
    paramPrintWriter.println(this.mSortOrder);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mCursor=");
    paramPrintWriter.println(this.mCursor);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mContentChanged=");
    paramPrintWriter.println(this.mContentChanged);
  }
  
  public String[] getProjection()
  {
    return this.mProjection;
  }
  
  public String getSelection()
  {
    return this.mSelection;
  }
  
  public String[] getSelectionArgs()
  {
    return this.mSelectionArgs;
  }
  
  public String getSortOrder()
  {
    return this.mSortOrder;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
  
  public Cursor loadInBackground()
  {
    Cursor localCursor = getContext().getContentResolver().query(this.mUri, this.mProjection, this.mSelection, this.mSelectionArgs, this.mSortOrder);
    if (localCursor == null) {
      return localCursor;
    }
    localCursor.getCount();
    localCursor.registerContentObserver(this.mObserver);
    return localCursor;
  }
  
  public void onCanceled(Cursor paramCursor)
  {
    if (paramCursor == null) {}
    while (paramCursor.isClosed()) {
      return;
    }
    paramCursor.close();
  }
  
  protected void onReset()
  {
    super.onReset();
    onStopLoading();
    if (this.mCursor == null) {}
    for (;;)
    {
      this.mCursor = null;
      return;
      if (!this.mCursor.isClosed()) {
        this.mCursor.close();
      }
    }
  }
  
  protected void onStartLoading()
  {
    if (this.mCursor == null) {
      if (!takeContentChanged()) {
        break label30;
      }
    }
    label30:
    while (this.mCursor == null)
    {
      forceLoad();
      return;
      deliverResult(this.mCursor);
      break;
    }
  }
  
  protected void onStopLoading()
  {
    cancelLoad();
  }
  
  public void setProjection(String[] paramArrayOfString)
  {
    this.mProjection = paramArrayOfString;
  }
  
  public void setSelection(String paramString)
  {
    this.mSelection = paramString;
  }
  
  public void setSelectionArgs(String[] paramArrayOfString)
  {
    this.mSelectionArgs = paramArrayOfString;
  }
  
  public void setSortOrder(String paramString)
  {
    this.mSortOrder = paramString;
  }
  
  public void setUri(Uri paramUri)
  {
    this.mUri = paramUri;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/CursorLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */