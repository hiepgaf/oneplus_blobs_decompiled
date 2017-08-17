// 
// Decompiled by Procyon v0.5.30
// 

package com.qti.server.power;

import android.util.Log;

public final class ShutdownOem
{
    private static final String TAG = "QualcommShutdown";
    
    public void rebootOrShutdown(final boolean b, final String s) {
        Log.i("QualcommShutdown", "Qualcomm reboot/shutdown.");
        if (SubSystemShutdown.shutdown() != 0) {
            Log.e("QualcommShutdown", "Failed to shutdown modem.");
            return;
        }
        Log.i("QualcommShutdown", "Modem shutdown successful.");
    }
}
