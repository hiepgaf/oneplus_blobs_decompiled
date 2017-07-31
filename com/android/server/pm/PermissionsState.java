package com.android.server.pm;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class PermissionsState
{
  private static final int[] NO_GIDS = new int[0];
  public static final int PERMISSION_OPERATION_FAILURE = -1;
  public static final int PERMISSION_OPERATION_SUCCESS = 0;
  public static final int PERMISSION_OPERATION_SUCCESS_GIDS_CHANGED = 1;
  private int[] mGlobalGids = NO_GIDS;
  final Object mLock = new Object();
  private SparseBooleanArray mPermissionReviewRequired;
  private ArrayMap<String, PermissionData> mPermissions;
  
  public PermissionsState() {}
  
  public PermissionsState(PermissionsState paramPermissionsState)
  {
    copyFrom(paramPermissionsState);
  }
  
  private static int[] appendInts(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int[] arrayOfInt = paramArrayOfInt1;
    if (paramArrayOfInt1 != null)
    {
      arrayOfInt = paramArrayOfInt1;
      if (paramArrayOfInt2 != null)
      {
        int i = 0;
        int j = paramArrayOfInt2.length;
        for (;;)
        {
          arrayOfInt = paramArrayOfInt1;
          if (i >= j) {
            break;
          }
          paramArrayOfInt1 = ArrayUtils.appendInt(paramArrayOfInt1, paramArrayOfInt2[i]);
          i += 1;
        }
      }
    }
    return arrayOfInt;
  }
  
  private static void enforceValidUserId(int paramInt)
  {
    if ((paramInt != -1) && (paramInt < 0)) {
      throw new IllegalArgumentException("Invalid userId:" + paramInt);
    }
  }
  
  private void ensureNoPermissionData(String paramString)
  {
    if (this.mPermissions == null) {
      return;
    }
    synchronized (this.mLock)
    {
      this.mPermissions.remove(paramString);
      if (this.mPermissions.isEmpty()) {
        this.mPermissions = null;
      }
      return;
    }
  }
  
  private PermissionData ensurePermissionData(BasePermission paramBasePermission)
  {
    if (this.mPermissions == null) {
      this.mPermissions = new ArrayMap();
    }
    PermissionData localPermissionData2 = (PermissionData)this.mPermissions.get(paramBasePermission.name);
    PermissionData localPermissionData1 = localPermissionData2;
    if (localPermissionData2 == null)
    {
      localPermissionData1 = new PermissionData(paramBasePermission);
      this.mPermissions.put(paramBasePermission.name, localPermissionData1);
    }
    return localPermissionData1;
  }
  
  private PermissionState getPermissionState(String paramString, int paramInt)
  {
    if (this.mPermissions == null) {
      return null;
    }
    paramString = (PermissionData)this.mPermissions.get(paramString);
    if (paramString == null) {
      return null;
    }
    return paramString.getPermissionState(paramInt);
  }
  
  private List<PermissionState> getPermissionStatesInternal(int paramInt)
  {
    enforceValidUserId(paramInt);
    Object localObject2 = new ArrayList();
    synchronized (this.mLock)
    {
      if (this.mPermissions == null)
      {
        localObject2 = Collections.emptyList();
        return (List<PermissionState>)localObject2;
      }
      int j = this.mPermissions.size();
      int i = 0;
      while (i < j)
      {
        PermissionState localPermissionState = ((PermissionData)this.mPermissions.valueAt(i)).getPermissionState(paramInt);
        if (localPermissionState != null) {
          ((List)localObject2).add(localPermissionState);
        }
        i += 1;
      }
      return (List<PermissionState>)localObject2;
    }
  }
  
  private int grantPermission(BasePermission paramBasePermission, int paramInt)
  {
    if (hasPermission(paramBasePermission.name, paramInt)) {
      return -1;
    }
    int i;
    if (ArrayUtils.isEmpty(paramBasePermission.computeGids(paramInt)))
    {
      i = 0;
      if (i == 0) {
        break label57;
      }
    }
    label57:
    for (int[] arrayOfInt = computeGids(paramInt);; arrayOfInt = NO_GIDS)
    {
      if (ensurePermissionData(paramBasePermission).grant(paramInt)) {
        break label65;
      }
      return -1;
      i = 1;
      break;
    }
    label65:
    if (i != 0)
    {
      paramBasePermission = computeGids(paramInt);
      if (arrayOfInt.length != paramBasePermission.length) {
        return 1;
      }
    }
    return 0;
  }
  
  private int revokePermission(BasePermission paramBasePermission, int paramInt)
  {
    if (!hasPermission(paramBasePermission.name, paramInt)) {
      return -1;
    }
    int i;
    if (ArrayUtils.isEmpty(paramBasePermission.computeGids(paramInt)))
    {
      i = 0;
      if (i == 0) {
        break label70;
      }
    }
    PermissionData localPermissionData;
    label70:
    for (int[] arrayOfInt = computeGids(paramInt);; arrayOfInt = NO_GIDS)
    {
      localPermissionData = (PermissionData)this.mPermissions.get(paramBasePermission.name);
      if (localPermissionData.revoke(paramInt)) {
        break label78;
      }
      return -1;
      i = 1;
      break;
    }
    label78:
    if (localPermissionData.isDefault()) {
      ensureNoPermissionData(paramBasePermission.name);
    }
    if (i != 0)
    {
      paramBasePermission = computeGids(paramInt);
      if (arrayOfInt.length != paramBasePermission.length) {
        return 1;
      }
    }
    return 0;
  }
  
  public int[] computeGids(int paramInt)
  {
    enforceValidUserId(paramInt);
    Object localObject1 = this.mGlobalGids;
    Object localObject2 = localObject1;
    if (this.mPermissions != null)
    {
      int j = this.mPermissions.size();
      int i = 0;
      localObject2 = localObject1;
      if (i < j)
      {
        if (!hasPermission((String)this.mPermissions.keyAt(i), paramInt)) {
          localObject2 = localObject1;
        }
        for (;;)
        {
          i += 1;
          localObject1 = localObject2;
          break;
          int[] arrayOfInt = ((PermissionData)this.mPermissions.valueAt(i)).computeGids(paramInt);
          localObject2 = localObject1;
          if (arrayOfInt != NO_GIDS) {
            localObject2 = appendInts((int[])localObject1, arrayOfInt);
          }
        }
      }
    }
    return (int[])localObject2;
  }
  
  public int[] computeGids(int[] paramArrayOfInt)
  {
    int[] arrayOfInt = this.mGlobalGids;
    int i = 0;
    int j = paramArrayOfInt.length;
    while (i < j)
    {
      arrayOfInt = appendInts(arrayOfInt, computeGids(paramArrayOfInt[i]));
      i += 1;
    }
    return arrayOfInt;
  }
  
  public void copyFrom(PermissionsState paramPermissionsState)
  {
    if (paramPermissionsState == this) {
      return;
    }
    int j;
    int i;
    synchronized (this.mLock)
    {
      if (this.mPermissions != null)
      {
        if (paramPermissionsState.mPermissions == null) {
          this.mPermissions = null;
        }
      }
      else
      {
        if (paramPermissionsState.mPermissions == null) {
          break label145;
        }
        if (this.mPermissions == null) {
          this.mPermissions = new ArrayMap();
        }
        j = paramPermissionsState.mPermissions.size();
        i = 0;
        while (i < j)
        {
          ??? = (String)paramPermissionsState.mPermissions.keyAt(i);
          PermissionData localPermissionData = (PermissionData)paramPermissionsState.mPermissions.valueAt(i);
          this.mPermissions.put(???, new PermissionData(localPermissionData));
          i += 1;
        }
      }
      this.mPermissions.clear();
    }
    label145:
    this.mGlobalGids = NO_GIDS;
    if (paramPermissionsState.mGlobalGids != NO_GIDS) {
      this.mGlobalGids = Arrays.copyOf(paramPermissionsState.mGlobalGids, paramPermissionsState.mGlobalGids.length);
    }
    if (this.mPermissionReviewRequired != null)
    {
      if (paramPermissionsState.mPermissionReviewRequired != null) {
        break label264;
      }
      this.mPermissionReviewRequired = null;
    }
    while (paramPermissionsState.mPermissionReviewRequired != null)
    {
      if (this.mPermissionReviewRequired == null) {
        this.mPermissionReviewRequired = new SparseBooleanArray();
      }
      j = paramPermissionsState.mPermissionReviewRequired.size();
      i = 0;
      while (i < j)
      {
        boolean bool = paramPermissionsState.mPermissionReviewRequired.valueAt(i);
        this.mPermissionReviewRequired.put(i, bool);
        i += 1;
      }
      label264:
      this.mPermissionReviewRequired.clear();
    }
  }
  
  public PermissionState getInstallPermissionState(String paramString)
  {
    return getPermissionState(paramString, -1);
  }
  
  public List<PermissionState> getInstallPermissionStates()
  {
    return getPermissionStatesInternal(-1);
  }
  
  public int getPermissionFlags(String paramString, int paramInt)
  {
    PermissionState localPermissionState = getInstallPermissionState(paramString);
    if (localPermissionState != null) {
      return localPermissionState.getFlags();
    }
    paramString = getRuntimePermissionState(paramString, paramInt);
    if (paramString != null) {
      return paramString.getFlags();
    }
    return 0;
  }
  
  public Set<String> getPermissions(int paramInt)
  {
    enforceValidUserId(paramInt);
    if (this.mPermissions == null) {
      return Collections.emptySet();
    }
    ArraySet localArraySet = new ArraySet(this.mPermissions.size());
    int j = this.mPermissions.size();
    int i = 0;
    if (i < j)
    {
      String str = (String)this.mPermissions.keyAt(i);
      if (hasInstallPermission(str)) {
        localArraySet.add(str);
      }
      for (;;)
      {
        i += 1;
        break;
        if ((paramInt != -1) && (hasRuntimePermission(str, paramInt))) {
          localArraySet.add(str);
        }
      }
    }
    return localArraySet;
  }
  
  public PermissionState getRuntimePermissionState(String paramString, int paramInt)
  {
    enforceValidUserId(paramInt);
    return getPermissionState(paramString, paramInt);
  }
  
  public List<PermissionState> getRuntimePermissionStates(int paramInt)
  {
    enforceValidUserId(paramInt);
    return getPermissionStatesInternal(paramInt);
  }
  
  public int grantInstallPermission(BasePermission paramBasePermission)
  {
    return grantPermission(paramBasePermission, -1);
  }
  
  public int grantRuntimePermission(BasePermission paramBasePermission, int paramInt)
  {
    enforceValidUserId(paramInt);
    if (paramInt == -1) {
      return -1;
    }
    return grantPermission(paramBasePermission, paramInt);
  }
  
  public boolean hasInstallPermission(String paramString)
  {
    return hasPermission(paramString, -1);
  }
  
  public boolean hasPermission(String paramString, int paramInt)
  {
    boolean bool = false;
    enforceValidUserId(paramInt);
    if (this.mPermissions == null) {
      return false;
    }
    paramString = (PermissionData)this.mPermissions.get(paramString);
    if (paramString != null) {
      bool = paramString.isGranted(paramInt);
    }
    return bool;
  }
  
  public boolean hasRequestedPermission(ArraySet<String> paramArraySet)
  {
    if (this.mPermissions == null) {
      return false;
    }
    int i = paramArraySet.size() - 1;
    while (i >= 0)
    {
      if (this.mPermissions.get(paramArraySet.valueAt(i)) != null) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  public boolean hasRuntimePermission(String paramString, int paramInt)
  {
    enforceValidUserId(paramInt);
    if (!hasInstallPermission(paramString)) {
      return hasPermission(paramString, paramInt);
    }
    return false;
  }
  
  public boolean isPermissionReviewRequired(int paramInt)
  {
    if (this.mPermissionReviewRequired != null) {
      return this.mPermissionReviewRequired.get(paramInt);
    }
    return false;
  }
  
  public void reset()
  {
    this.mGlobalGids = NO_GIDS;
    synchronized (this.mLock)
    {
      this.mPermissions = null;
      this.mPermissionReviewRequired = null;
      return;
    }
  }
  
  public int revokeInstallPermission(BasePermission paramBasePermission)
  {
    return revokePermission(paramBasePermission, -1);
  }
  
  public int revokeRuntimePermission(BasePermission paramBasePermission, int paramInt)
  {
    enforceValidUserId(paramInt);
    if (paramInt == -1) {
      return -1;
    }
    return revokePermission(paramBasePermission, paramInt);
  }
  
  public void setGlobalGids(int[] paramArrayOfInt)
  {
    if (!ArrayUtils.isEmpty(paramArrayOfInt)) {
      this.mGlobalGids = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    }
  }
  
  public boolean updatePermissionFlags(BasePermission paramBasePermission, int paramInt1, int paramInt2, int paramInt3)
  {
    enforceValidUserId(paramInt1);
    if ((paramInt3 != 0) || (paramInt2 != 0)) {
      i = 1;
    }
    while (this.mPermissions == null) {
      if (i == 0)
      {
        return false;
        i = 0;
      }
      else
      {
        ensurePermissionData(paramBasePermission);
      }
    }
    PermissionData localPermissionData2 = (PermissionData)this.mPermissions.get(paramBasePermission.name);
    PermissionData localPermissionData1 = localPermissionData2;
    if (localPermissionData2 == null)
    {
      if (i == 0) {
        return false;
      }
      localPermissionData1 = ensurePermissionData(paramBasePermission);
    }
    int i = localPermissionData1.getFlags(paramInt1);
    boolean bool = localPermissionData1.updateFlags(paramInt1, paramInt2, paramInt3);
    if (bool)
    {
      paramInt2 = localPermissionData1.getFlags(paramInt1);
      if (((i & 0x40) != 0) || ((paramInt2 & 0x40) == 0)) {
        break label157;
      }
      if (this.mPermissionReviewRequired == null) {
        this.mPermissionReviewRequired = new SparseBooleanArray();
      }
      this.mPermissionReviewRequired.put(paramInt1, true);
    }
    label157:
    do
    {
      do
      {
        return bool;
      } while (((i & 0x40) == 0) || ((paramInt2 & 0x40) != 0) || (this.mPermissionReviewRequired == null));
      this.mPermissionReviewRequired.delete(paramInt1);
    } while (this.mPermissionReviewRequired.size() > 0);
    this.mPermissionReviewRequired = null;
    return bool;
  }
  
  public boolean updatePermissionFlagsForAllPermissions(int paramInt1, int paramInt2, int paramInt3)
  {
    enforceValidUserId(paramInt1);
    if (this.mPermissions == null) {
      return false;
    }
    boolean bool = false;
    int j = this.mPermissions.size();
    int i = 0;
    while (i < j)
    {
      bool |= ((PermissionData)this.mPermissions.valueAt(i)).updateFlags(paramInt1, paramInt2, paramInt3);
      i += 1;
    }
    return bool;
  }
  
  private static final class PermissionData
  {
    private final BasePermission mPerm;
    private SparseArray<PermissionsState.PermissionState> mUserStates = new SparseArray();
    
    public PermissionData(BasePermission paramBasePermission)
    {
      this.mPerm = paramBasePermission;
    }
    
    public PermissionData(PermissionData paramPermissionData)
    {
      this(paramPermissionData.mPerm);
      int j = paramPermissionData.mUserStates.size();
      int i = 0;
      while (i < j)
      {
        int k = paramPermissionData.mUserStates.keyAt(i);
        PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)paramPermissionData.mUserStates.valueAt(i);
        this.mUserStates.put(k, new PermissionsState.PermissionState(localPermissionState));
        i += 1;
      }
    }
    
    private boolean isCompatibleUserId(int paramInt)
    {
      boolean bool2 = true;
      boolean bool1 = bool2;
      if (!isDefault())
      {
        bool1 = bool2;
        if ((isInstallPermission() ^ isInstallPermissionKey(paramInt))) {
          bool1 = false;
        }
      }
      return bool1;
    }
    
    private boolean isInstallPermission()
    {
      if (this.mUserStates.size() == 1) {
        return this.mUserStates.get(-1) != null;
      }
      return false;
    }
    
    public static boolean isInstallPermissionKey(int paramInt)
    {
      return paramInt == -1;
    }
    
    public int[] computeGids(int paramInt)
    {
      return this.mPerm.computeGids(paramInt);
    }
    
    public int getFlags(int paramInt)
    {
      PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)this.mUserStates.get(paramInt);
      if (localPermissionState != null) {
        return PermissionsState.PermissionState.-get0(localPermissionState);
      }
      return 0;
    }
    
    public PermissionsState.PermissionState getPermissionState(int paramInt)
    {
      return (PermissionsState.PermissionState)this.mUserStates.get(paramInt);
    }
    
    public boolean grant(int paramInt)
    {
      if (!isCompatibleUserId(paramInt)) {
        return false;
      }
      if (isGranted(paramInt)) {
        return false;
      }
      PermissionsState.PermissionState localPermissionState2 = (PermissionsState.PermissionState)this.mUserStates.get(paramInt);
      PermissionsState.PermissionState localPermissionState1 = localPermissionState2;
      if (localPermissionState2 == null)
      {
        localPermissionState1 = new PermissionsState.PermissionState(this.mPerm.name);
        this.mUserStates.put(paramInt, localPermissionState1);
      }
      PermissionsState.PermissionState.-set1(localPermissionState1, true);
      return true;
    }
    
    public boolean isDefault()
    {
      boolean bool = false;
      if (this.mUserStates.size() <= 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isGranted(int paramInt)
    {
      if (isInstallPermission()) {
        paramInt = -1;
      }
      PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)this.mUserStates.get(paramInt);
      if (localPermissionState == null) {
        return false;
      }
      return PermissionsState.PermissionState.-get1(localPermissionState);
    }
    
    public boolean revoke(int paramInt)
    {
      if (!isCompatibleUserId(paramInt)) {
        return false;
      }
      if (!isGranted(paramInt)) {
        return false;
      }
      PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)this.mUserStates.get(paramInt);
      PermissionsState.PermissionState.-set1(localPermissionState, false);
      if (localPermissionState.isDefault()) {
        this.mUserStates.remove(paramInt);
      }
      return true;
    }
    
    public boolean updateFlags(int paramInt1, int paramInt2, int paramInt3)
    {
      if (isInstallPermission()) {
        paramInt1 = -1;
      }
      if (!isCompatibleUserId(paramInt1)) {
        return false;
      }
      paramInt3 &= paramInt2;
      PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)this.mUserStates.get(paramInt1);
      if (localPermissionState != null)
      {
        int i = PermissionsState.PermissionState.-get0(localPermissionState);
        PermissionsState.PermissionState.-set0(localPermissionState, PermissionsState.PermissionState.-get0(localPermissionState) & paramInt2 | paramInt3);
        if (localPermissionState.isDefault()) {
          this.mUserStates.remove(paramInt1);
        }
        return PermissionsState.PermissionState.-get0(localPermissionState) != i;
      }
      if (paramInt3 != 0)
      {
        localPermissionState = new PermissionsState.PermissionState(this.mPerm.name);
        PermissionsState.PermissionState.-set0(localPermissionState, paramInt3);
        this.mUserStates.put(paramInt1, localPermissionState);
        return true;
      }
      return false;
    }
  }
  
  public static final class PermissionState
  {
    private int mFlags;
    private boolean mGranted;
    private final String mName;
    
    public PermissionState(PermissionState paramPermissionState)
    {
      this.mName = paramPermissionState.mName;
      this.mGranted = paramPermissionState.mGranted;
      this.mFlags = paramPermissionState.mFlags;
    }
    
    public PermissionState(String paramString)
    {
      this.mName = paramString;
    }
    
    public int getFlags()
    {
      return this.mFlags;
    }
    
    public String getName()
    {
      return this.mName;
    }
    
    public boolean isDefault()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (!this.mGranted)
      {
        bool1 = bool2;
        if (this.mFlags == 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public boolean isGranted()
    {
      return this.mGranted;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PermissionsState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */