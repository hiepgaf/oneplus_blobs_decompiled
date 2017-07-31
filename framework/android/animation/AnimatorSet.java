package android.animation;

import android.app.ActivityThread;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class AnimatorSet
  extends Animator
{
  private static final String TAG = "AnimatorSet";
  private ValueAnimator mDelayAnim = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F }).setDuration(0L);
  private boolean mDependencyDirty = false;
  private long mDuration = -1L;
  private TimeInterpolator mInterpolator = null;
  private ArrayMap<Animator, Node> mNodeMap = new ArrayMap();
  private ArrayList<Node> mNodes = new ArrayList();
  private ArrayList<Animator> mPlayingSet = new ArrayList();
  private boolean mReversible = true;
  private Node mRootNode = new Node(this.mDelayAnim);
  private AnimatorSetListener mSetListener = new AnimatorSetListener(this);
  private final boolean mShouldIgnoreEndWithoutStart;
  private long mStartDelay = 0L;
  private boolean mStarted = false;
  private boolean mTerminated = false;
  private long mTotalDuration = 0L;
  
  public AnimatorSet()
  {
    this.mNodeMap.put(this.mDelayAnim, this.mRootNode);
    this.mNodes.add(this.mRootNode);
    Application localApplication = ActivityThread.currentApplication();
    if ((localApplication == null) || (localApplication.getApplicationInfo() == null))
    {
      this.mShouldIgnoreEndWithoutStart = true;
      return;
    }
    if (localApplication.getApplicationInfo().targetSdkVersion < 24)
    {
      this.mShouldIgnoreEndWithoutStart = true;
      return;
    }
    this.mShouldIgnoreEndWithoutStart = false;
  }
  
  private void createDependencyGraph()
  {
    int j;
    if (!this.mDependencyDirty)
    {
      k = 0;
      i = 0;
      for (;;)
      {
        j = k;
        if (i < this.mNodes.size())
        {
          localObject = ((Node)this.mNodes.get(i)).mAnimation;
          if (((Node)this.mNodes.get(i)).mTotalDuration != ((Animator)localObject).getTotalDuration()) {
            j = 1;
          }
        }
        else
        {
          if (j != 0) {
            break;
          }
          return;
        }
        i += 1;
      }
    }
    this.mDependencyDirty = false;
    int k = this.mNodes.size();
    int i = 0;
    while (i < k)
    {
      ((Node)this.mNodes.get(i)).mParentsAdded = false;
      i += 1;
    }
    i = 0;
    if (i < k)
    {
      localObject = (Node)this.mNodes.get(i);
      if (((Node)localObject).mParentsAdded) {}
      for (;;)
      {
        i += 1;
        break;
        ((Node)localObject).mParentsAdded = true;
        if (((Node)localObject).mSiblings != null)
        {
          findSiblings((Node)localObject, ((Node)localObject).mSiblings);
          ((Node)localObject).mSiblings.remove(localObject);
          int m = ((Node)localObject).mSiblings.size();
          j = 0;
          while (j < m)
          {
            ((Node)localObject).addParents(((Node)((Node)localObject).mSiblings.get(j)).mParents);
            j += 1;
          }
          j = 0;
          while (j < m)
          {
            Node localNode = (Node)((Node)localObject).mSiblings.get(j);
            localNode.addParents(((Node)localObject).mParents);
            localNode.mParentsAdded = true;
            j += 1;
          }
        }
      }
    }
    i = 0;
    while (i < k)
    {
      localObject = (Node)this.mNodes.get(i);
      if ((localObject != this.mRootNode) && (((Node)localObject).mParents == null)) {
        ((Node)localObject).addParent(this.mRootNode);
      }
      i += 1;
    }
    Object localObject = new ArrayList(this.mNodes.size());
    this.mRootNode.mStartTime = 0L;
    this.mRootNode.mEndTime = this.mDelayAnim.getDuration();
    updatePlayTime(this.mRootNode, (ArrayList)localObject);
    long l1 = 0L;
    i = 0;
    for (;;)
    {
      long l2 = l1;
      if (i < k)
      {
        localObject = (Node)this.mNodes.get(i);
        ((Node)localObject).mTotalDuration = ((Node)localObject).mAnimation.getTotalDuration();
        if (((Node)localObject).mEndTime == -1L) {
          l2 = -1L;
        }
      }
      else
      {
        this.mTotalDuration = l2;
        return;
      }
      l2 = l1;
      if (((Node)localObject).mEndTime > l1) {
        l2 = ((Node)localObject).mEndTime;
      }
      i += 1;
      l1 = l2;
    }
  }
  
  private void endRemainingAnimations()
  {
    ArrayList localArrayList = new ArrayList(this.mNodes.size());
    localArrayList.addAll(this.mPlayingSet);
    int i = 0;
    while (i < localArrayList.size())
    {
      Object localObject = (Animator)localArrayList.get(i);
      ((Animator)localObject).end();
      int k = i + 1;
      localObject = (Node)this.mNodeMap.get(localObject);
      i = k;
      if (((Node)localObject).mChildNodes != null)
      {
        int m = ((Node)localObject).mChildNodes.size();
        int j = 0;
        i = k;
        if (j < m)
        {
          Node localNode = (Node)((Node)localObject).mChildNodes.get(j);
          if (localNode.mLatestParent != localObject) {}
          for (;;)
          {
            j += 1;
            break;
            localArrayList.add(localNode.mAnimation);
          }
        }
      }
    }
  }
  
  private void findSiblings(Node paramNode, ArrayList<Node> paramArrayList)
  {
    if (!paramArrayList.contains(paramNode))
    {
      paramArrayList.add(paramNode);
      if (paramNode.mSiblings == null) {
        return;
      }
      int i = 0;
      while (i < paramNode.mSiblings.size())
      {
        findSiblings((Node)paramNode.mSiblings.get(i), paramArrayList);
        i += 1;
      }
    }
  }
  
  private Node getNodeForAnimation(Animator paramAnimator)
  {
    Node localNode2 = (Node)this.mNodeMap.get(paramAnimator);
    Node localNode1 = localNode2;
    if (localNode2 == null)
    {
      localNode1 = new Node(paramAnimator);
      this.mNodeMap.put(paramAnimator, localNode1);
      this.mNodes.add(localNode1);
    }
    return localNode1;
  }
  
  private void onChildAnimatorEnded(Animator paramAnimator)
  {
    paramAnimator = (Node)this.mNodeMap.get(paramAnimator);
    paramAnimator.mEnded = true;
    if (!this.mTerminated)
    {
      ArrayList localArrayList = paramAnimator.mChildNodes;
      if (localArrayList == null) {}
      int j;
      for (int i = 0;; i = localArrayList.size())
      {
        j = 0;
        while (j < i)
        {
          if (((Node)localArrayList.get(j)).mLatestParent == paramAnimator) {
            start((Node)localArrayList.get(j));
          }
          j += 1;
        }
      }
      int k = 1;
      int m = this.mNodes.size();
      i = 0;
      for (;;)
      {
        j = k;
        if (i < m)
        {
          if (!((Node)this.mNodes.get(i)).mEnded) {
            j = 0;
          }
        }
        else
        {
          if (j == 0) {
            return;
          }
          if (this.mListeners == null) {
            break;
          }
          paramAnimator = (ArrayList)this.mListeners.clone();
          j = paramAnimator.size();
          i = 0;
          while (i < j)
          {
            ((Animator.AnimatorListener)paramAnimator.get(i)).onAnimationEnd(this);
            i += 1;
          }
        }
        i += 1;
      }
      this.mStarted = false;
      this.mPaused = false;
    }
  }
  
  private void printChildCount()
  {
    ArrayList localArrayList = new ArrayList(this.mNodes.size());
    localArrayList.add(this.mRootNode);
    Log.d("AnimatorSet", "Current tree: ");
    int j = 0;
    while (j < localArrayList.size())
    {
      int n = localArrayList.size();
      StringBuilder localStringBuilder = new StringBuilder();
      while (j < n)
      {
        Node localNode1 = (Node)localArrayList.get(j);
        int m = 0;
        int i = 0;
        if (localNode1.mChildNodes != null)
        {
          int k = 0;
          for (;;)
          {
            m = i;
            if (k >= localNode1.mChildNodes.size()) {
              break;
            }
            Node localNode2 = (Node)localNode1.mChildNodes.get(k);
            m = i;
            if (localNode2.mLatestParent == localNode1)
            {
              m = i + 1;
              localArrayList.add(localNode2);
            }
            k += 1;
            i = m;
          }
        }
        localStringBuilder.append(" ");
        localStringBuilder.append(m);
        j += 1;
      }
      Log.d("AnimatorSet", localStringBuilder.toString());
    }
  }
  
  private void updateAnimatorsDuration()
  {
    if (this.mDuration >= 0L)
    {
      int j = this.mNodes.size();
      int i = 0;
      while (i < j)
      {
        ((Node)this.mNodes.get(i)).mAnimation.setDuration(this.mDuration);
        i += 1;
      }
    }
    this.mDelayAnim.setDuration(this.mStartDelay);
  }
  
  private void updatePlayTime(Node paramNode, ArrayList<Node> paramArrayList)
  {
    if (paramNode.mChildNodes == null)
    {
      if (paramNode == this.mRootNode)
      {
        i = 0;
        while (i < this.mNodes.size())
        {
          paramNode = (Node)this.mNodes.get(i);
          if (paramNode != this.mRootNode)
          {
            paramNode.mStartTime = -1L;
            paramNode.mEndTime = -1L;
          }
          i += 1;
        }
      }
      return;
    }
    paramArrayList.add(paramNode);
    int k = paramNode.mChildNodes.size();
    int i = 0;
    if (i < k)
    {
      Node localNode = (Node)paramNode.mChildNodes.get(i);
      int j = paramArrayList.indexOf(localNode);
      if (j >= 0)
      {
        while (j < paramArrayList.size())
        {
          ((Node)paramArrayList.get(j)).mLatestParent = null;
          ((Node)paramArrayList.get(j)).mStartTime = -1L;
          ((Node)paramArrayList.get(j)).mEndTime = -1L;
          j += 1;
        }
        localNode.mStartTime = -1L;
        localNode.mEndTime = -1L;
        localNode.mLatestParent = null;
        Log.w("AnimatorSet", "Cycle found in AnimatorSet: " + this);
      }
      for (;;)
      {
        i += 1;
        break;
        if (localNode.mStartTime != -1L)
        {
          if (paramNode.mEndTime != -1L) {
            break label290;
          }
          localNode.mLatestParent = paramNode;
          localNode.mStartTime = -1L;
          localNode.mEndTime = -1L;
        }
        updatePlayTime(localNode, paramArrayList);
      }
      label290:
      if (paramNode.mEndTime >= localNode.mStartTime)
      {
        localNode.mLatestParent = paramNode;
        localNode.mStartTime = paramNode.mEndTime;
      }
      long l = localNode.mAnimation.getTotalDuration();
      if (l == -1L) {}
      for (l = -1L;; l = localNode.mStartTime + l)
      {
        localNode.mEndTime = l;
        break;
      }
    }
    paramArrayList.remove(paramNode);
  }
  
  public boolean canReverse()
  {
    if (!this.mReversible) {
      return false;
    }
    int j = this.mNodes.size();
    int i = 0;
    while (i < j)
    {
      Node localNode = (Node)this.mNodes.get(i);
      if ((!localNode.mAnimation.canReverse()) || (localNode.mAnimation.getStartDelay() > 0L)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public void cancel()
  {
    this.mTerminated = true;
    if (isStarted())
    {
      Object localObject = null;
      if (this.mListeners != null)
      {
        localArrayList = (ArrayList)this.mListeners.clone();
        j = localArrayList.size();
        i = 0;
        for (;;)
        {
          localObject = localArrayList;
          if (i >= j) {
            break;
          }
          ((Animator.AnimatorListener)localArrayList.get(i)).onAnimationCancel(this);
          i += 1;
        }
      }
      ArrayList localArrayList = new ArrayList(this.mPlayingSet);
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        ((Animator)localArrayList.get(i)).cancel();
        i += 1;
      }
      if (localObject != null)
      {
        j = ((ArrayList)localObject).size();
        i = 0;
        while (i < j)
        {
          ((Animator.AnimatorListener)((ArrayList)localObject).get(i)).onAnimationEnd(this);
          i += 1;
        }
      }
      this.mStarted = false;
    }
  }
  
  public AnimatorSet clone()
  {
    AnimatorSet localAnimatorSet = (AnimatorSet)super.clone();
    int m = this.mNodes.size();
    localAnimatorSet.mTerminated = false;
    localAnimatorSet.mStarted = false;
    localAnimatorSet.mPlayingSet = new ArrayList();
    localAnimatorSet.mNodeMap = new ArrayMap();
    localAnimatorSet.mNodes = new ArrayList(m);
    localAnimatorSet.mReversible = this.mReversible;
    localAnimatorSet.mSetListener = new AnimatorSetListener(localAnimatorSet);
    int i = 0;
    Object localObject;
    Node localNode1;
    int j;
    while (i < m)
    {
      localObject = (Node)this.mNodes.get(i);
      localNode1 = ((Node)localObject).clone();
      Node.-set0((Node)localObject, localNode1);
      localAnimatorSet.mNodes.add(localNode1);
      localAnimatorSet.mNodeMap.put(localNode1.mAnimation, localNode1);
      localObject = localNode1.mAnimation.getListeners();
      if (localObject != null)
      {
        j = ((ArrayList)localObject).size() - 1;
        while (j >= 0)
        {
          if (((Animator.AnimatorListener)((ArrayList)localObject).get(j) instanceof AnimatorSetListener)) {
            ((ArrayList)localObject).remove(j);
          }
          j -= 1;
        }
      }
      i += 1;
    }
    localAnimatorSet.mRootNode = Node.-get0(this.mRootNode);
    localAnimatorSet.mDelayAnim = ((ValueAnimator)localAnimatorSet.mRootNode.mAnimation);
    i = 0;
    while (i < m)
    {
      localNode1 = (Node)this.mNodes.get(i);
      Node localNode2 = Node.-get0(localNode1);
      if (localNode1.mLatestParent == null)
      {
        localObject = null;
        localNode2.mLatestParent = ((Node)localObject);
        if (localNode1.mChildNodes != null) {
          break label356;
        }
      }
      int k;
      label356:
      for (j = 0;; j = localNode1.mChildNodes.size())
      {
        k = 0;
        while (k < j)
        {
          Node.-get0(localNode1).mChildNodes.set(k, Node.-get0((Node)localNode1.mChildNodes.get(k)));
          k += 1;
        }
        localObject = Node.-get0(localNode1.mLatestParent);
        break;
      }
      if (localNode1.mSiblings == null) {}
      for (j = 0;; j = localNode1.mSiblings.size())
      {
        k = 0;
        while (k < j)
        {
          Node.-get0(localNode1).mSiblings.set(k, Node.-get0((Node)localNode1.mSiblings.get(k)));
          k += 1;
        }
      }
      if (localNode1.mParents == null) {}
      for (j = 0;; j = localNode1.mParents.size())
      {
        k = 0;
        while (k < j)
        {
          Node.-get0(localNode1).mParents.set(k, Node.-get0((Node)localNode1.mParents.get(k)));
          k += 1;
        }
      }
      i += 1;
    }
    i = 0;
    while (i < m)
    {
      Node.-set0((Node)this.mNodes.get(i), null);
      i += 1;
    }
    return localAnimatorSet;
  }
  
  public void end()
  {
    ArrayList localArrayList;
    int i;
    if ((!this.mShouldIgnoreEndWithoutStart) || (isStarted()))
    {
      this.mTerminated = true;
      if (isStarted()) {
        endRemainingAnimations();
      }
      if (this.mListeners != null)
      {
        localArrayList = (ArrayList)this.mListeners.clone();
        i = 0;
      }
    }
    else
    {
      while (i < localArrayList.size())
      {
        ((Animator.AnimatorListener)localArrayList.get(i)).onAnimationEnd(this);
        i += 1;
        continue;
        return;
      }
    }
    this.mStarted = false;
  }
  
  public int getChangingConfigurations()
  {
    int j = super.getChangingConfigurations();
    int k = this.mNodes.size();
    int i = 0;
    while (i < k)
    {
      j |= ((Node)this.mNodes.get(i)).mAnimation.getChangingConfigurations();
      i += 1;
    }
    return j;
  }
  
  public ArrayList<Animator> getChildAnimations()
  {
    ArrayList localArrayList = new ArrayList();
    int j = this.mNodes.size();
    int i = 0;
    while (i < j)
    {
      Node localNode = (Node)this.mNodes.get(i);
      if (localNode != this.mRootNode) {
        localArrayList.add(localNode.mAnimation);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public long getDuration()
  {
    return this.mDuration;
  }
  
  public TimeInterpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  public long getStartDelay()
  {
    return this.mStartDelay;
  }
  
  public long getTotalDuration()
  {
    updateAnimatorsDuration();
    createDependencyGraph();
    return this.mTotalDuration;
  }
  
  public boolean isRunning()
  {
    int j = this.mNodes.size();
    int i = 0;
    while (i < j)
    {
      Node localNode = (Node)this.mNodes.get(i);
      if ((localNode != this.mRootNode) && (localNode.mAnimation.isStarted())) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public boolean isStarted()
  {
    return this.mStarted;
  }
  
  public void pause()
  {
    boolean bool = this.mPaused;
    super.pause();
    if ((!bool) && (this.mPaused))
    {
      if (!this.mDelayAnim.isStarted()) {
        break label38;
      }
      this.mDelayAnim.pause();
    }
    for (;;)
    {
      return;
      label38:
      int j = this.mNodes.size();
      int i = 0;
      while (i < j)
      {
        Node localNode = (Node)this.mNodes.get(i);
        if (localNode != this.mRootNode) {
          localNode.mAnimation.pause();
        }
        i += 1;
      }
    }
  }
  
  public Builder play(Animator paramAnimator)
  {
    if (paramAnimator != null) {
      return new Builder(paramAnimator);
    }
    return null;
  }
  
  public void playSequentially(List<Animator> paramList)
  {
    if ((paramList != null) && (paramList.size() > 0))
    {
      if (paramList.size() != 1) {
        break label39;
      }
      play((Animator)paramList.get(0));
    }
    for (;;)
    {
      return;
      label39:
      this.mReversible = false;
      int i = 0;
      while (i < paramList.size() - 1)
      {
        play((Animator)paramList.get(i)).before((Animator)paramList.get(i + 1));
        i += 1;
      }
    }
  }
  
  public void playSequentially(Animator... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      if (paramVarArgs.length != 1) {
        break label19;
      }
      play(paramVarArgs[0]);
    }
    for (;;)
    {
      return;
      label19:
      this.mReversible = false;
      int i = 0;
      while (i < paramVarArgs.length - 1)
      {
        play(paramVarArgs[i]).before(paramVarArgs[(i + 1)]);
        i += 1;
      }
    }
  }
  
  public void playTogether(Collection<Animator> paramCollection)
  {
    if ((paramCollection != null) && (paramCollection.size() > 0))
    {
      Animator localAnimator = null;
      Iterator localIterator = paramCollection.iterator();
      paramCollection = localAnimator;
      while (localIterator.hasNext())
      {
        localAnimator = (Animator)localIterator.next();
        if (paramCollection == null) {
          paramCollection = play(localAnimator);
        } else {
          paramCollection.with(localAnimator);
        }
      }
    }
  }
  
  public void playTogether(Animator... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      Builder localBuilder = play(paramVarArgs[0]);
      int i = 1;
      while (i < paramVarArgs.length)
      {
        localBuilder.with(paramVarArgs[i]);
        i += 1;
      }
    }
  }
  
  public void resume()
  {
    boolean bool = this.mPaused;
    super.resume();
    if ((!bool) || (this.mPaused)) {}
    for (;;)
    {
      return;
      if (this.mDelayAnim.isStarted())
      {
        this.mDelayAnim.resume();
        return;
      }
      int j = this.mNodes.size();
      int i = 0;
      while (i < j)
      {
        Node localNode = (Node)this.mNodes.get(i);
        if (localNode != this.mRootNode) {
          localNode.mAnimation.resume();
        }
        i += 1;
      }
    }
  }
  
  public void reverse()
  {
    if (canReverse())
    {
      int j = this.mNodes.size();
      int i = 0;
      while (i < j)
      {
        ((Node)this.mNodes.get(i)).mAnimation.reverse();
        i += 1;
      }
    }
  }
  
  public AnimatorSet setDuration(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("duration must be a value of zero or greater");
    }
    this.mDependencyDirty = true;
    this.mDuration = paramLong;
    return this;
  }
  
  public void setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    this.mInterpolator = paramTimeInterpolator;
  }
  
  public void setStartDelay(long paramLong)
  {
    long l1 = paramLong;
    if (paramLong < 0L)
    {
      Log.w("AnimatorSet", "Start delay should always be non-negative");
      l1 = 0L;
    }
    long l2 = l1 - this.mStartDelay;
    if (l2 == 0L) {
      return;
    }
    this.mStartDelay = l1;
    if (this.mStartDelay > 0L) {
      this.mReversible = false;
    }
    if (!this.mDependencyDirty)
    {
      int j = this.mNodes.size();
      int i = 0;
      while (i < j)
      {
        Node localNode = (Node)this.mNodes.get(i);
        if (localNode == this.mRootNode)
        {
          localNode.mEndTime = this.mStartDelay;
          i += 1;
        }
        else
        {
          if (localNode.mStartTime == -1L)
          {
            paramLong = -1L;
            label136:
            localNode.mStartTime = paramLong;
            if (localNode.mEndTime != -1L) {
              break label179;
            }
          }
          label179:
          for (paramLong = -1L;; paramLong = localNode.mEndTime + l2)
          {
            localNode.mEndTime = paramLong;
            break;
            paramLong = localNode.mStartTime + l2;
            break label136;
          }
        }
      }
      if (this.mTotalDuration != -1L) {
        this.mTotalDuration += l2;
      }
    }
  }
  
  public void setTarget(Object paramObject)
  {
    int j = this.mNodes.size();
    int i = 0;
    if (i < j)
    {
      Animator localAnimator = ((Node)this.mNodes.get(i)).mAnimation;
      if ((localAnimator instanceof AnimatorSet)) {
        ((AnimatorSet)localAnimator).setTarget(paramObject);
      }
      for (;;)
      {
        i += 1;
        break;
        if ((localAnimator instanceof ObjectAnimator)) {
          ((ObjectAnimator)localAnimator).setTarget(paramObject);
        }
      }
    }
  }
  
  public void setupEndValues()
  {
    int j = this.mNodes.size();
    int i = 0;
    while (i < j)
    {
      Node localNode = (Node)this.mNodes.get(i);
      if (localNode != this.mRootNode) {
        localNode.mAnimation.setupEndValues();
      }
      i += 1;
    }
  }
  
  public void setupStartValues()
  {
    int j = this.mNodes.size();
    int i = 0;
    while (i < j)
    {
      Node localNode = (Node)this.mNodes.get(i);
      if (localNode != this.mRootNode) {
        localNode.mAnimation.setupStartValues();
      }
      i += 1;
    }
  }
  
  public boolean shouldPlayTogether()
  {
    updateAnimatorsDuration();
    createDependencyGraph();
    return (this.mRootNode.mChildNodes == null) || (this.mRootNode.mChildNodes.size() == this.mNodes.size() - 1);
  }
  
  public void start()
  {
    this.mTerminated = false;
    this.mStarted = true;
    this.mPaused = false;
    int j = this.mNodes.size();
    int i = 0;
    Object localObject;
    while (i < j)
    {
      localObject = (Node)this.mNodes.get(i);
      ((Node)localObject).mEnded = false;
      ((Node)localObject).mAnimation.setAllowRunningAsynchronously(false);
      i += 1;
    }
    if (this.mInterpolator != null)
    {
      i = 0;
      while (i < j)
      {
        ((Node)this.mNodes.get(i)).mAnimation.setInterpolator(this.mInterpolator);
        i += 1;
      }
    }
    updateAnimatorsDuration();
    createDependencyGraph();
    i = 0;
    if (this.mStartDelay > 0L) {
      start(this.mRootNode);
    }
    while (this.mListeners != null)
    {
      localObject = (ArrayList)this.mListeners.clone();
      int k = ((ArrayList)localObject).size();
      j = 0;
      while (j < k)
      {
        ((Animator.AnimatorListener)((ArrayList)localObject).get(j)).onAnimationStart(this);
        j += 1;
      }
      if (this.mNodes.size() > 1) {
        onChildAnimatorEnded(this.mDelayAnim);
      } else {
        i = 1;
      }
    }
    if (i != 0) {
      onChildAnimatorEnded(this.mDelayAnim);
    }
  }
  
  void start(Node paramNode)
  {
    paramNode = paramNode.mAnimation;
    this.mPlayingSet.add(paramNode);
    paramNode.addListener(this.mSetListener);
    paramNode.start();
  }
  
  public String toString()
  {
    String str = "AnimatorSet@" + Integer.toHexString(hashCode()) + "{";
    int j = this.mNodes.size();
    int i = 0;
    while (i < j)
    {
      Node localNode = (Node)this.mNodes.get(i);
      str = str + "\n    " + localNode.mAnimation.toString();
      i += 1;
    }
    return str + "\n}";
  }
  
  private static class AnimatorSetListener
    implements Animator.AnimatorListener
  {
    private AnimatorSet mAnimatorSet;
    
    AnimatorSetListener(AnimatorSet paramAnimatorSet)
    {
      this.mAnimatorSet = paramAnimatorSet;
    }
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      if ((!AnimatorSet.-get1(this.mAnimatorSet)) && (AnimatorSet.-get0(this.mAnimatorSet).size() == 0))
      {
        paramAnimator = this.mAnimatorSet.mListeners;
        if (paramAnimator != null)
        {
          int j = paramAnimator.size();
          int i = 0;
          while (i < j)
          {
            ((Animator.AnimatorListener)paramAnimator.get(i)).onAnimationCancel(this.mAnimatorSet);
            i += 1;
          }
        }
      }
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      paramAnimator.removeListener(this);
      AnimatorSet.-get0(this.mAnimatorSet).remove(paramAnimator);
      AnimatorSet.-wrap1(this.mAnimatorSet, paramAnimator);
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator) {}
  }
  
  public class Builder
  {
    private AnimatorSet.Node mCurrentNode;
    
    Builder(Animator paramAnimator)
    {
      AnimatorSet.-set0(AnimatorSet.this, true);
      this.mCurrentNode = AnimatorSet.-wrap0(AnimatorSet.this, paramAnimator);
    }
    
    public Builder after(long paramLong)
    {
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      localValueAnimator.setDuration(paramLong);
      after(localValueAnimator);
      return this;
    }
    
    public Builder after(Animator paramAnimator)
    {
      AnimatorSet.-set1(AnimatorSet.this, false);
      paramAnimator = AnimatorSet.-wrap0(AnimatorSet.this, paramAnimator);
      this.mCurrentNode.addParent(paramAnimator);
      return this;
    }
    
    public Builder before(Animator paramAnimator)
    {
      AnimatorSet.-set1(AnimatorSet.this, false);
      paramAnimator = AnimatorSet.-wrap0(AnimatorSet.this, paramAnimator);
      this.mCurrentNode.addChild(paramAnimator);
      return this;
    }
    
    public Builder with(Animator paramAnimator)
    {
      paramAnimator = AnimatorSet.-wrap0(AnimatorSet.this, paramAnimator);
      this.mCurrentNode.addSibling(paramAnimator);
      return this;
    }
  }
  
  private static class Node
    implements Cloneable
  {
    Animator mAnimation;
    ArrayList<Node> mChildNodes = null;
    long mEndTime = 0L;
    boolean mEnded = false;
    Node mLatestParent = null;
    ArrayList<Node> mParents;
    boolean mParentsAdded = false;
    ArrayList<Node> mSiblings;
    long mStartTime = 0L;
    private Node mTmpClone = null;
    long mTotalDuration = 0L;
    
    public Node(Animator paramAnimator)
    {
      this.mAnimation = paramAnimator;
    }
    
    void addChild(Node paramNode)
    {
      if (this.mChildNodes == null) {
        this.mChildNodes = new ArrayList();
      }
      if (!this.mChildNodes.contains(paramNode))
      {
        this.mChildNodes.add(paramNode);
        paramNode.addParent(this);
      }
    }
    
    public void addParent(Node paramNode)
    {
      if (this.mParents == null) {
        this.mParents = new ArrayList();
      }
      if (!this.mParents.contains(paramNode))
      {
        this.mParents.add(paramNode);
        paramNode.addChild(this);
      }
    }
    
    public void addParents(ArrayList<Node> paramArrayList)
    {
      if (paramArrayList == null) {
        return;
      }
      int j = paramArrayList.size();
      int i = 0;
      while (i < j)
      {
        addParent((Node)paramArrayList.get(i));
        i += 1;
      }
    }
    
    public void addSibling(Node paramNode)
    {
      if (this.mSiblings == null) {
        this.mSiblings = new ArrayList();
      }
      if (!this.mSiblings.contains(paramNode))
      {
        this.mSiblings.add(paramNode);
        paramNode.addSibling(this);
      }
    }
    
    public Node clone()
    {
      try
      {
        Node localNode = (Node)super.clone();
        localNode.mAnimation = this.mAnimation.clone();
        if (this.mChildNodes != null) {
          localNode.mChildNodes = new ArrayList(this.mChildNodes);
        }
        if (this.mSiblings != null) {
          localNode.mSiblings = new ArrayList(this.mSiblings);
        }
        if (this.mParents != null) {
          localNode.mParents = new ArrayList(this.mParents);
        }
        localNode.mEnded = false;
        return localNode;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/AnimatorSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */