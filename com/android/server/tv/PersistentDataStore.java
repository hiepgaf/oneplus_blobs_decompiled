package com.android.server.tv;

import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContentRating;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class PersistentDataStore
{
  private static final String ATTR_ENABLED = "enabled";
  private static final String ATTR_STRING = "string";
  private static final String TAG = "TvInputManagerService";
  private static final String TAG_BLOCKED_RATINGS = "blocked-ratings";
  private static final String TAG_PARENTAL_CONTROLS = "parental-controls";
  private static final String TAG_RATING = "rating";
  private static final String TAG_TV_INPUT_MANAGER_STATE = "tv-input-manager-state";
  private final AtomicFile mAtomicFile;
  private final List<TvContentRating> mBlockedRatings = Collections.synchronizedList(new ArrayList());
  private boolean mBlockedRatingsChanged;
  private final Context mContext;
  private final Handler mHandler = new Handler();
  private boolean mLoaded;
  private boolean mParentalControlsEnabled;
  private boolean mParentalControlsEnabledChanged;
  private final Runnable mSaveRunnable = new Runnable()
  {
    public void run()
    {
      PersistentDataStore.-wrap0(PersistentDataStore.this);
    }
  };
  
  public PersistentDataStore(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    paramContext = Environment.getUserSystemDirectory(paramInt);
    if ((!paramContext.exists()) && (!paramContext.mkdirs())) {
      throw new IllegalStateException("User dir cannot be created: " + paramContext);
    }
    this.mAtomicFile = new AtomicFile(new File(paramContext, "tv-input-manager-state.xml"));
  }
  
  private void broadcastChangesIfNeeded()
  {
    if (this.mParentalControlsEnabledChanged)
    {
      this.mParentalControlsEnabledChanged = false;
      this.mContext.sendBroadcastAsUser(new Intent("android.media.tv.action.PARENTAL_CONTROLS_ENABLED_CHANGED"), UserHandle.ALL);
    }
    if (this.mBlockedRatingsChanged)
    {
      this.mBlockedRatingsChanged = false;
      this.mContext.sendBroadcastAsUser(new Intent("android.media.tv.action.BLOCKED_RATINGS_CHANGED"), UserHandle.ALL);
    }
  }
  
  private void clearState()
  {
    this.mBlockedRatings.clear();
    this.mParentalControlsEnabled = false;
  }
  
  /* Error */
  private void load()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 166	com/android/server/tv/PersistentDataStore:clearState	()V
    //   4: aload_0
    //   5: getfield 125	com/android/server/tv/PersistentDataStore:mAtomicFile	Landroid/util/AtomicFile;
    //   8: invokevirtual 170	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   11: astore_1
    //   12: invokestatic 176	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   15: astore_2
    //   16: aload_2
    //   17: new 178	java/io/BufferedInputStream
    //   20: dup
    //   21: aload_1
    //   22: invokespecial 181	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   25: getstatic 187	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   28: invokevirtual 192	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   31: invokeinterface 198 3 0
    //   36: aload_0
    //   37: aload_2
    //   38: invokespecial 202	com/android/server/tv/PersistentDataStore:loadFromXml	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   41: aload_1
    //   42: invokestatic 208	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   45: return
    //   46: astore_1
    //   47: return
    //   48: astore_2
    //   49: ldc 16
    //   51: ldc -46
    //   53: aload_2
    //   54: invokestatic 216	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   57: pop
    //   58: aload_0
    //   59: invokespecial 166	com/android/server/tv/PersistentDataStore:clearState	()V
    //   62: aload_1
    //   63: invokestatic 208	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   66: return
    //   67: astore_2
    //   68: aload_1
    //   69: invokestatic 208	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   72: aload_2
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	PersistentDataStore
    //   11	31	1	localFileInputStream	java.io.FileInputStream
    //   46	23	1	localFileNotFoundException	java.io.FileNotFoundException
    //   15	23	2	localXmlPullParser	XmlPullParser
    //   48	6	2	localIOException	IOException
    //   67	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	12	46	java/io/FileNotFoundException
    //   12	41	48	java/io/IOException
    //   12	41	48	org/xmlpull/v1/XmlPullParserException
    //   12	41	67	finally
    //   49	62	67	finally
  }
  
  private void loadBlockedRatingsFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if (paramXmlPullParser.getName().equals("rating"))
      {
        String str = paramXmlPullParser.getAttributeValue(null, "string");
        if (TextUtils.isEmpty(str)) {
          throw new XmlPullParserException("Missing string attribute on rating");
        }
        this.mBlockedRatings.add(TvContentRating.unflattenFromString(str));
      }
    }
  }
  
  private void loadFromXml(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    XmlUtils.beginDocument(paramXmlPullParser, "tv-input-manager-state");
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      if (paramXmlPullParser.getName().equals("blocked-ratings"))
      {
        loadBlockedRatingsFromXml(paramXmlPullParser);
      }
      else if (paramXmlPullParser.getName().equals("parental-controls"))
      {
        String str = paramXmlPullParser.getAttributeValue(null, "enabled");
        if (TextUtils.isEmpty(str)) {
          throw new XmlPullParserException("Missing enabled attribute on parental-controls");
        }
        this.mParentalControlsEnabled = Boolean.valueOf(str).booleanValue();
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
  
  private void postSave()
  {
    this.mHandler.removeCallbacks(this.mSaveRunnable);
    this.mHandler.post(this.mSaveRunnable);
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
          broadcastChangesIfNeeded();
          return;
        }
        this.mAtomicFile.failWrite(localFileOutputStream);
        return;
      }
      finally
      {
        if (0 == 0) {
          break label104;
        }
      }
      localIOException = localIOException;
      Slog.w("TvInputManagerService", "Failed to save tv input manager persistent store data.", localIOException);
      return;
    }
    broadcastChangesIfNeeded();
    for (;;)
    {
      throw ((Throwable)localObject);
      label104:
      this.mAtomicFile.failWrite(localIOException);
    }
  }
  
  private void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException
  {
    paramXmlSerializer.startDocument(null, Boolean.valueOf(true));
    paramXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
    paramXmlSerializer.startTag(null, "tv-input-manager-state");
    paramXmlSerializer.startTag(null, "blocked-ratings");
    synchronized (this.mBlockedRatings)
    {
      Iterator localIterator = this.mBlockedRatings.iterator();
      if (localIterator.hasNext())
      {
        TvContentRating localTvContentRating = (TvContentRating)localIterator.next();
        paramXmlSerializer.startTag(null, "rating");
        paramXmlSerializer.attribute(null, "string", localTvContentRating.flattenToString());
        paramXmlSerializer.endTag(null, "rating");
      }
    }
    paramXmlSerializer.endTag(null, "blocked-ratings");
    paramXmlSerializer.startTag(null, "parental-controls");
    paramXmlSerializer.attribute(null, "enabled", Boolean.toString(this.mParentalControlsEnabled));
    paramXmlSerializer.endTag(null, "parental-controls");
    paramXmlSerializer.endTag(null, "tv-input-manager-state");
    paramXmlSerializer.endDocument();
  }
  
  public void addBlockedRating(TvContentRating paramTvContentRating)
  {
    loadIfNeeded();
    if ((paramTvContentRating == null) || (this.mBlockedRatings.contains(paramTvContentRating))) {
      return;
    }
    this.mBlockedRatings.add(paramTvContentRating);
    this.mBlockedRatingsChanged = true;
    postSave();
  }
  
  public TvContentRating[] getBlockedRatings()
  {
    loadIfNeeded();
    return (TvContentRating[])this.mBlockedRatings.toArray(new TvContentRating[this.mBlockedRatings.size()]);
  }
  
  public boolean isParentalControlsEnabled()
  {
    loadIfNeeded();
    return this.mParentalControlsEnabled;
  }
  
  public boolean isRatingBlocked(TvContentRating paramTvContentRating)
  {
    loadIfNeeded();
    synchronized (this.mBlockedRatings)
    {
      Iterator localIterator = this.mBlockedRatings.iterator();
      while (localIterator.hasNext())
      {
        boolean bool = paramTvContentRating.contains((TvContentRating)localIterator.next());
        if (bool) {
          return true;
        }
      }
      return false;
    }
  }
  
  public void removeBlockedRating(TvContentRating paramTvContentRating)
  {
    loadIfNeeded();
    if ((paramTvContentRating != null) && (this.mBlockedRatings.contains(paramTvContentRating)))
    {
      this.mBlockedRatings.remove(paramTvContentRating);
      this.mBlockedRatingsChanged = true;
      postSave();
    }
  }
  
  public void setParentalControlsEnabled(boolean paramBoolean)
  {
    loadIfNeeded();
    if (this.mParentalControlsEnabled != paramBoolean)
    {
      this.mParentalControlsEnabled = paramBoolean;
      this.mParentalControlsEnabledChanged = true;
      postSave();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/PersistentDataStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */