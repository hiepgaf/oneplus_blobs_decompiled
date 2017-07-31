package com.android.server.am;

class ActivityManagerDebugConfig
{
  static final boolean APPEND_CATEGORY_NAME = false;
  static boolean DEBUG_ADD_REMOVE = false;
  static boolean DEBUG_ALL = false;
  static boolean DEBUG_ALL_ACTIVITIES = false;
  static boolean DEBUG_ANR = false;
  static boolean DEBUG_APP = false;
  static boolean DEBUG_BACKUP = false;
  static boolean DEBUG_BROADCAST = false;
  static boolean DEBUG_BROADCAST_BACKGROUND = false;
  static boolean DEBUG_BROADCAST_LIGHT = false;
  static boolean DEBUG_CLEANUP = false;
  static boolean DEBUG_CONFIGURATION = false;
  static boolean DEBUG_CONTAINERS = false;
  static boolean DEBUG_FOCUS = false;
  static boolean DEBUG_IDLE = false;
  static boolean DEBUG_IMMERSIVE = false;
  static boolean DEBUG_LOCKSCREEN = false;
  static boolean DEBUG_LOCKTASK = false;
  static boolean DEBUG_LRU = false;
  static boolean DEBUG_MU = false;
  static boolean DEBUG_OOM_ADJ = false;
  static boolean DEBUG_PAUSE = false;
  static boolean DEBUG_PERMISSIONS_REVIEW = false;
  static boolean DEBUG_POWER = false;
  static boolean DEBUG_POWER_QUICK = false;
  static boolean DEBUG_PROCESSES = false;
  static boolean DEBUG_PROCESS_OBSERVERS = false;
  static boolean DEBUG_PROVIDER = false;
  static boolean DEBUG_PSS = false;
  static boolean DEBUG_RECENTS = false;
  static boolean DEBUG_RELEASE = false;
  static boolean DEBUG_RESULTS = false;
  static boolean DEBUG_SAVED_STATE = false;
  static boolean DEBUG_SCREENSHOTS = false;
  static boolean DEBUG_SERVICE = false;
  static boolean DEBUG_SERVICE_EXECUTING = false;
  static boolean DEBUG_STACK = false;
  static boolean DEBUG_STATES = false;
  static boolean DEBUG_SWITCH = false;
  static boolean DEBUG_TASKS = false;
  static boolean DEBUG_THUMBNAILS = false;
  static boolean DEBUG_TRANSITION = false;
  static boolean DEBUG_UID_OBSERVERS = false;
  static boolean DEBUG_URI_PERMISSION = false;
  static boolean DEBUG_USAGE_STATS = false;
  static boolean DEBUG_USER_LEAVING = false;
  static boolean DEBUG_VISIBILITY = false;
  static boolean DEBUG_VISIBLE_BEHIND = false;
  static boolean DEBUG_WHITELISTS = false;
  static final String POSTFIX_ADD_REMOVE;
  static final String POSTFIX_APP;
  static final String POSTFIX_BACKUP;
  static final String POSTFIX_BROADCAST;
  static final String POSTFIX_CLEANUP;
  static final String POSTFIX_CONFIGURATION;
  static final String POSTFIX_CONTAINERS;
  static final String POSTFIX_FOCUS;
  static final String POSTFIX_IDLE;
  static final String POSTFIX_IMMERSIVE;
  static final String POSTFIX_LOCKSCREEN;
  static final String POSTFIX_LOCKTASK;
  static final String POSTFIX_LRU;
  static final String POSTFIX_MU = "_MU";
  static final String POSTFIX_OOM_ADJ;
  static final String POSTFIX_PAUSE;
  static final String POSTFIX_POWER;
  static final String POSTFIX_PROCESSES;
  static final String POSTFIX_PROCESS_OBSERVERS;
  static final String POSTFIX_PROVIDER;
  static final String POSTFIX_PSS;
  static final String POSTFIX_RECENTS;
  static final String POSTFIX_RELEASE;
  static final String POSTFIX_RESULTS;
  static final String POSTFIX_SAVED_STATE;
  static final String POSTFIX_SCREENSHOTS;
  static final String POSTFIX_SERVICE;
  static final String POSTFIX_SERVICE_EXECUTING;
  static final String POSTFIX_STACK;
  static final String POSTFIX_STATES;
  static final String POSTFIX_SWITCH;
  static final String POSTFIX_TASKS;
  static final String POSTFIX_THUMBNAILS;
  static final String POSTFIX_TRANSITION;
  static final String POSTFIX_UID_OBSERVERS;
  static final String POSTFIX_URI_PERMISSION;
  static final String POSTFIX_USER_LEAVING;
  static final String POSTFIX_VISIBILITY;
  static final String POSTFIX_VISIBLE_BEHIND;
  static final String TAG_AM = "ActivityManager";
  static final boolean TAG_WITH_CLASS_NAME = false;
  
  static
  {
    boolean bool2 = true;
    DEBUG_ALL = false;
    if (!DEBUG_ALL)
    {
      bool1 = false;
      DEBUG_ALL_ACTIVITIES = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label752;
      }
      bool1 = false;
      label26:
      DEBUG_ADD_REMOVE = bool1;
      DEBUG_ANR = false;
      if (DEBUG_ALL_ACTIVITIES) {
        break label757;
      }
      bool1 = false;
      label42:
      DEBUG_APP = bool1;
      if (DEBUG_ALL) {
        break label762;
      }
      bool1 = false;
      label54:
      DEBUG_BACKUP = bool1;
      if (DEBUG_ALL) {
        break label767;
      }
      bool1 = false;
      label66:
      DEBUG_BROADCAST = bool1;
      if (DEBUG_BROADCAST) {
        break label772;
      }
      bool1 = false;
      label78:
      DEBUG_BROADCAST_BACKGROUND = bool1;
      if (DEBUG_BROADCAST) {
        break label777;
      }
      bool1 = false;
      label90:
      DEBUG_BROADCAST_LIGHT = bool1;
      if (DEBUG_ALL) {
        break label782;
      }
      bool1 = false;
      label102:
      DEBUG_CLEANUP = bool1;
      if (DEBUG_ALL) {
        break label787;
      }
      bool1 = false;
      label114:
      DEBUG_CONFIGURATION = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label792;
      }
      bool1 = false;
      label126:
      DEBUG_CONTAINERS = bool1;
      DEBUG_FOCUS = false;
      if (DEBUG_ALL_ACTIVITIES) {
        break label797;
      }
      bool1 = false;
      label142:
      DEBUG_IDLE = bool1;
      if (DEBUG_ALL) {
        break label802;
      }
      bool1 = false;
      label154:
      DEBUG_IMMERSIVE = bool1;
      if (DEBUG_ALL) {
        break label807;
      }
      bool1 = false;
      label166:
      DEBUG_LOCKSCREEN = bool1;
      if (DEBUG_ALL) {
        break label812;
      }
      bool1 = false;
      label178:
      DEBUG_LOCKTASK = bool1;
      if (DEBUG_ALL) {
        break label817;
      }
      bool1 = false;
      label190:
      DEBUG_LRU = bool1;
      if (DEBUG_ALL) {
        break label822;
      }
      bool1 = false;
      label202:
      DEBUG_MU = bool1;
      if (DEBUG_ALL) {
        break label827;
      }
      bool1 = false;
      label214:
      DEBUG_OOM_ADJ = bool1;
      if (DEBUG_ALL) {
        break label832;
      }
      bool1 = false;
      label226:
      DEBUG_PAUSE = bool1;
      if (DEBUG_ALL) {
        break label837;
      }
      bool1 = false;
      label238:
      DEBUG_POWER = bool1;
      if (DEBUG_POWER) {
        break label842;
      }
      bool1 = false;
      label250:
      DEBUG_POWER_QUICK = bool1;
      if (DEBUG_ALL) {
        break label847;
      }
      bool1 = false;
      label262:
      DEBUG_PROCESS_OBSERVERS = bool1;
      if (DEBUG_ALL) {
        break label852;
      }
      bool1 = false;
      label274:
      DEBUG_PROCESSES = bool1;
      if (DEBUG_ALL) {
        break label857;
      }
      bool1 = false;
      label286:
      DEBUG_PROVIDER = bool1;
      if (DEBUG_ALL) {
        break label862;
      }
      bool1 = false;
      label298:
      DEBUG_PSS = bool1;
      if (DEBUG_ALL) {
        break label867;
      }
      bool1 = false;
      label310:
      DEBUG_RECENTS = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label872;
      }
      bool1 = false;
      label322:
      DEBUG_RELEASE = bool1;
      if (DEBUG_ALL) {
        break label877;
      }
      bool1 = false;
      label334:
      DEBUG_RESULTS = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label882;
      }
      bool1 = false;
      label346:
      DEBUG_SAVED_STATE = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label887;
      }
      bool1 = false;
      label358:
      DEBUG_SCREENSHOTS = bool1;
      if (DEBUG_ALL) {
        break label892;
      }
      bool1 = false;
      label370:
      DEBUG_SERVICE = bool1;
      if (DEBUG_ALL) {
        break label897;
      }
      bool1 = false;
      label382:
      DEBUG_SERVICE_EXECUTING = bool1;
      if (DEBUG_ALL) {
        break label902;
      }
      bool1 = false;
      label394:
      DEBUG_STACK = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label907;
      }
      bool1 = false;
      label406:
      DEBUG_STATES = bool1;
      if (DEBUG_ALL) {
        break label912;
      }
      bool1 = false;
      label418:
      DEBUG_SWITCH = bool1;
      if (DEBUG_ALL) {
        break label917;
      }
      bool1 = false;
      label430:
      DEBUG_TASKS = bool1;
      if (DEBUG_ALL) {
        break label922;
      }
      bool1 = false;
      label442:
      DEBUG_THUMBNAILS = bool1;
      if (DEBUG_ALL) {
        break label927;
      }
      bool1 = false;
      label454:
      DEBUG_TRANSITION = bool1;
      if (DEBUG_ALL) {
        break label932;
      }
      bool1 = false;
      label466:
      DEBUG_UID_OBSERVERS = bool1;
      if (DEBUG_ALL) {
        break label937;
      }
      bool1 = false;
      label478:
      DEBUG_URI_PERMISSION = bool1;
      if (DEBUG_ALL) {
        break label942;
      }
      bool1 = false;
      label490:
      DEBUG_USER_LEAVING = bool1;
      if (DEBUG_ALL) {
        break label947;
      }
      bool1 = false;
      label502:
      DEBUG_VISIBILITY = bool1;
      if (DEBUG_ALL_ACTIVITIES) {
        break label952;
      }
      bool1 = false;
      label514:
      DEBUG_VISIBLE_BEHIND = bool1;
      if (DEBUG_ALL) {
        break label957;
      }
      bool1 = false;
      label526:
      DEBUG_USAGE_STATS = bool1;
      if (DEBUG_ALL) {
        break label962;
      }
    }
    label752:
    label757:
    label762:
    label767:
    label772:
    label777:
    label782:
    label787:
    label792:
    label797:
    label802:
    label807:
    label812:
    label817:
    label822:
    label827:
    label832:
    label837:
    label842:
    label847:
    label852:
    label857:
    label862:
    label867:
    label872:
    label877:
    label882:
    label887:
    label892:
    label897:
    label902:
    label907:
    label912:
    label917:
    label922:
    label927:
    label932:
    label937:
    label942:
    label947:
    label952:
    label957:
    label962:
    for (boolean bool1 = false;; bool1 = true)
    {
      DEBUG_PERMISSIONS_REVIEW = bool1;
      bool1 = bool2;
      if (!DEBUG_ALL) {
        bool1 = false;
      }
      DEBUG_WHITELISTS = bool1;
      POSTFIX_ADD_REMOVE = "";
      POSTFIX_APP = "";
      POSTFIX_BACKUP = "";
      POSTFIX_BROADCAST = "";
      POSTFIX_CLEANUP = "";
      POSTFIX_CONFIGURATION = "";
      POSTFIX_CONTAINERS = "";
      POSTFIX_FOCUS = "";
      POSTFIX_IDLE = "";
      POSTFIX_IMMERSIVE = "";
      POSTFIX_LOCKSCREEN = "";
      POSTFIX_LOCKTASK = "";
      POSTFIX_LRU = "";
      POSTFIX_OOM_ADJ = "";
      POSTFIX_PAUSE = "";
      POSTFIX_POWER = "";
      POSTFIX_PROCESS_OBSERVERS = "";
      POSTFIX_PROCESSES = "";
      POSTFIX_PROVIDER = "";
      POSTFIX_PSS = "";
      POSTFIX_RECENTS = "";
      POSTFIX_RELEASE = "";
      POSTFIX_RESULTS = "";
      POSTFIX_SAVED_STATE = "";
      POSTFIX_SCREENSHOTS = "";
      POSTFIX_SERVICE = "";
      POSTFIX_SERVICE_EXECUTING = "";
      POSTFIX_STACK = "";
      POSTFIX_STATES = "";
      POSTFIX_SWITCH = "";
      POSTFIX_TASKS = "";
      POSTFIX_THUMBNAILS = "";
      POSTFIX_TRANSITION = "";
      POSTFIX_UID_OBSERVERS = "";
      POSTFIX_URI_PERMISSION = "";
      POSTFIX_USER_LEAVING = "";
      POSTFIX_VISIBILITY = "";
      POSTFIX_VISIBLE_BEHIND = "";
      return;
      bool1 = true;
      break;
      bool1 = true;
      break label26;
      bool1 = true;
      break label42;
      bool1 = true;
      break label54;
      bool1 = true;
      break label66;
      bool1 = true;
      break label78;
      bool1 = true;
      break label90;
      bool1 = true;
      break label102;
      bool1 = true;
      break label114;
      bool1 = true;
      break label126;
      bool1 = true;
      break label142;
      bool1 = true;
      break label154;
      bool1 = true;
      break label166;
      bool1 = true;
      break label178;
      bool1 = true;
      break label190;
      bool1 = true;
      break label202;
      bool1 = true;
      break label214;
      bool1 = true;
      break label226;
      bool1 = true;
      break label238;
      bool1 = true;
      break label250;
      bool1 = true;
      break label262;
      bool1 = true;
      break label274;
      bool1 = true;
      break label286;
      bool1 = true;
      break label298;
      bool1 = true;
      break label310;
      bool1 = true;
      break label322;
      bool1 = true;
      break label334;
      bool1 = true;
      break label346;
      bool1 = true;
      break label358;
      bool1 = true;
      break label370;
      bool1 = true;
      break label382;
      bool1 = true;
      break label394;
      bool1 = true;
      break label406;
      bool1 = true;
      break label418;
      bool1 = true;
      break label430;
      bool1 = true;
      break label442;
      bool1 = true;
      break label454;
      bool1 = true;
      break label466;
      bool1 = true;
      break label478;
      bool1 = true;
      break label490;
      bool1 = true;
      break label502;
      bool1 = true;
      break label514;
      bool1 = true;
      break label526;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityManagerDebugConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */