package android.media;

import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParserException;

class TtmlTrack
  extends SubtitleTrack
  implements TtmlNodeListener
{
  private static final String TAG = "TtmlTrack";
  private Long mCurrentRunID;
  private final TtmlParser mParser = new TtmlParser(this);
  private String mParsingData;
  private final TtmlRenderingWidget mRenderingWidget;
  private TtmlNode mRootNode;
  private final TreeSet<Long> mTimeEvents = new TreeSet();
  private final LinkedList<TtmlNode> mTtmlNodes = new LinkedList();
  
  TtmlTrack(TtmlRenderingWidget paramTtmlRenderingWidget, MediaFormat paramMediaFormat)
  {
    super(paramMediaFormat);
    this.mRenderingWidget = paramTtmlRenderingWidget;
    this.mParsingData = "";
  }
  
  private void addTimeEvents(TtmlNode paramTtmlNode)
  {
    this.mTimeEvents.add(Long.valueOf(paramTtmlNode.mStartTimeMs));
    this.mTimeEvents.add(Long.valueOf(paramTtmlNode.mEndTimeMs));
    int i = 0;
    while (i < paramTtmlNode.mChildren.size())
    {
      addTimeEvents((TtmlNode)paramTtmlNode.mChildren.get(i));
      i += 1;
    }
  }
  
  private List<TtmlNode> getActiveNodes(long paramLong1, long paramLong2)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < this.mTtmlNodes.size())
    {
      TtmlNode localTtmlNode = (TtmlNode)this.mTtmlNodes.get(i);
      if (localTtmlNode.isActive(paramLong1, paramLong2)) {
        localArrayList.add(localTtmlNode);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public TtmlCue getNextResult()
  {
    while (this.mTimeEvents.size() >= 2)
    {
      long l1 = ((Long)this.mTimeEvents.pollFirst()).longValue();
      long l2 = ((Long)this.mTimeEvents.first()).longValue();
      if (!getActiveNodes(l1, l2).isEmpty()) {
        return new TtmlCue(l1, l2, TtmlUtils.applySpacePolicy(TtmlUtils.extractText(this.mRootNode, l1, l2), false), TtmlUtils.extractTtmlFragment(this.mRootNode, l1, l2));
      }
    }
    return null;
  }
  
  public TtmlRenderingWidget getRenderingWidget()
  {
    return this.mRenderingWidget;
  }
  
  public void onData(byte[] arg1, boolean paramBoolean, long paramLong)
  {
    try
    {
      String str1 = new String(???, "UTF-8");
      synchronized (this.mParser)
      {
        if ((this.mCurrentRunID != null) && (paramLong != this.mCurrentRunID.longValue())) {
          throw new IllegalStateException("Run #" + this.mCurrentRunID + " in progress.  Cannot process run #" + paramLong);
        }
      }
      this.mCurrentRunID = Long.valueOf(paramLong);
    }
    catch (UnsupportedEncodingException ???)
    {
      Log.w("TtmlTrack", "subtitle data is not UTF-8 encoded: " + ???);
      return;
    }
    this.mParsingData += str2;
    if (paramBoolean) {}
    try
    {
      this.mParser.parse(this.mParsingData, this.mCurrentRunID.longValue());
      finishedRun(paramLong);
      this.mParsingData = "";
      this.mCurrentRunID = null;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localIOException.printStackTrace();
      }
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        localXmlPullParserException.printStackTrace();
      }
    }
  }
  
  public void onRootNodeParsed(TtmlNode paramTtmlNode)
  {
    this.mRootNode = paramTtmlNode;
    for (;;)
    {
      paramTtmlNode = getNextResult();
      if (paramTtmlNode == null) {
        break;
      }
      addCue(paramTtmlNode);
    }
    this.mRootNode = null;
    this.mTtmlNodes.clear();
    this.mTimeEvents.clear();
  }
  
  public void onTtmlNodeParsed(TtmlNode paramTtmlNode)
  {
    this.mTtmlNodes.addLast(paramTtmlNode);
    addTimeEvents(paramTtmlNode);
  }
  
  public void updateView(Vector<SubtitleTrack.Cue> paramVector)
  {
    if (!this.mVisible) {
      return;
    }
    if ((this.DEBUG) && (this.mTimeProvider != null)) {}
    try
    {
      Log.d("TtmlTrack", "at " + this.mTimeProvider.getCurrentTimeUs(false, true) / 1000L + " ms the active cues are:");
      this.mRenderingWidget.setActiveCues(paramVector);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;)
      {
        Log.d("TtmlTrack", "at (illegal state) the active cues are:");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TtmlTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */