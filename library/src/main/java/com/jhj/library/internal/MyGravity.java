package com.jhj.library.internal;

import android.util.Log;
import android.view.Gravity;

/**
 * Created by jhj_Plus on 2016/4/13.
 */
public class MyGravity extends Gravity {
    private static final String TAG = "MyGravity";

    public static final int NO_Z_AXIS = -1;

    /** Bits defining the vertical axis. */
    public static final int AXIS_Z_SHIFT = 8;

    /** Push object to the top of its container, not changing its size. */
    public static final int ABOVE = (AXIS_PULL_BEFORE|AXIS_SPECIFIED)<<AXIS_Z_SHIFT;
    /** Push object to the bottom of its container, not changing its size. */
    public static final int BELOW = (AXIS_PULL_AFTER|AXIS_SPECIFIED)<<AXIS_Z_SHIFT;

    /**
     * Binary mask to get the absolute horizontal gravity of a gravity.
     */
    public static final int DEEP_GRAVITY_MASK = (AXIS_SPECIFIED |
            AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_Z_SHIFT;

    public static void testZGravity(int gravity) {
        if ((gravity & DEEP_GRAVITY_MASK) == ABOVE) {
            Log.e(TAG, "---------ABOVE-------");
        } else if ((gravity & DEEP_GRAVITY_MASK) == BELOW) {
            Log.e(TAG, "---------BELOW-------");
        }
    }

    public static int checkZGravity(int gravity) {
        return (gravity & DEEP_GRAVITY_MASK) == ABOVE ? ABOVE
                : (gravity & DEEP_GRAVITY_MASK) == BELOW ? BELOW : NO_Z_AXIS;
    }

}
