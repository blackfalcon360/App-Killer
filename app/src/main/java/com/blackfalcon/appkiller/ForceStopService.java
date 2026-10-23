package com.blackfalcon.appkiller;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Presses "Force stop" and the confirmation button on the system App Info screen,
 * only while the automation started from MainActivity is running.
 */
public class ForceStopService extends AccessibilityService {

    private static final String[] FORCE_LABELS = {
            "Force stop", "Force Stop",
            "زبردستی روکیں", "زبردستی بند کریں", "فورس اسٹاپ",
            "إيقاف إجباري", "فرض الإيقاف", "إيقاف قسري"
    };
    private static final String[] OK_LABELS = {
            "OK", "Ok", "Force stop", "Force Stop",
            "ٹھیک ہے", "زبردستی روکیں",
            "موافق", "حسنًا", "إيقاف إجباري", "فرض الإيقاف"
    };

    static boolean isOn() {
        return Bridge.svc != null;
    }

    static void back() {
        ForceStopService s = Bridge.svc;
        if (s != null) {
            s.performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Bridge.svc = this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (Bridge.svc == this) {
            Bridge.svc = null;
        }
        Bridge.cancel();
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (!Bridge.active || Bridge.step == 0 || Bridge.step == 3) return;
        CharSequence p = e.getPackageName();
        if (p != null && p.toString().equals(getPackageName())) return;
        handle();
    }

    private void handle() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        final int g = Bridge.gen;

        if (Bridge.step == 2) {
            AccessibilityNodeInfo ok = findById(root, "android:id/button1");
            if (ok == null) ok = findExact(root, OK_LABELS);
            if (ok != null && ok.isEnabled() && ok.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                Bridge.step = 3;
                Bridge.H.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Bridge.active && Bridge.gen == g) {
                            Bridge.onProcessed(true);
                        }
                    }
                }, 700);
            }
            return;
        }

        if (Bridge.step == 1) {
            AccessibilityNodeInfo fs = findExact(root, FORCE_LABELS);
            if (fs == null) fs = findById(root, "com.android.settings:id/right_button");
            if (fs == null) return;
            if (!fs.isEnabled()) {
                // Button is greyed out: the app is already stopped.
                Bridge.step = 3;
                Bridge.H.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Bridge.active && Bridge.gen == g) {
                            Bridge.onProcessed(true);
                        }
                    }
                }, 300);
                return;
            }
            if (fs.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                Bridge.step = 2;
            }
        }
    }

    private static AccessibilityNodeInfo findById(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> l = root.findAccessibilityNodeInfosByViewId(id);
        if (l == null || l.isEmpty()) return null;
        return clickableAncestor(l.get(0));
    }

    /** Finds a clickable node whose text equals one of the labels (ignoring case). */
    private static AccessibilityNodeInfo findExact(AccessibilityNodeInfo root, String[] labels) {
        for (String label : labels) {
            List<AccessibilityNodeInfo> l = root.findAccessibilityNodeInfosByText(label);
            if (l == null) continue;
            for (AccessibilityNodeInfo n : l) {
                CharSequence t = n.getText();
                if (t != null && t.toString().trim().equalsIgnoreCase(label)) {
                    AccessibilityNodeInfo c = clickableAncestor(n);
                    if (c != null) return c;
                }
            }
        }
        return null;
    }

    private static AccessibilityNodeInfo clickableAncestor(AccessibilityNodeInfo n) {
        AccessibilityNodeInfo cur = n;
        int guard = 0;
        while (cur != null && !cur.isClickable() && guard < 6) {
            cur = cur.getParent();
            guard++;
        }
        return (cur != null && cur.isClickable()) ? cur : null;
    }
}
