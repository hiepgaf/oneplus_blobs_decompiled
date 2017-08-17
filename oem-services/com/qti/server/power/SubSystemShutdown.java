// 
// Decompiled by Procyon v0.5.30
// 

package com.qti.server.power;

public final class SubSystemShutdown
{
    private static final String TAG = "SubSystemShutdown";
    
    static {
        System.loadLibrary("SubSystemShutdown");
    }
    
    public static native int shutdown();
}
