package com.blackfalcon.appkiller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Shared state of the Force stop automation (runs on the main thread only). */
final class Bridge {
    private Bridge() {}

    static ForceStopService svc;
    static final Handler H = new Handler(Looper.getMainLooper());
    static ArrayList<String> queue = new ArrayList<String>();
    static int idx, done, skipped, step, gen;
    static boolean active, pending, dirty;
    static Context app;

    /** step: 0 idle, 1 waiting for App Info screen, 2 waiting for confirm dialog, 3 confirmed. */

    static void begin(Context c, List<String> pkgs) {
        app = c.getApplicationContext();
        queue = new ArrayList<String>(pkgs);
        idx = 0;
        done = 0;
        skipped = 0;
        step = 0;
        gen++;
        pending = false;
        active = true;
        launchCurrent(c);
    }

    static void launchCurrent(Context c) {
        if (!active) return;
        if (idx >= queue.size()) {
            finish();
            return;
        }
        final int g = ++gen;
        step = 1;
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + queue.get(idx)));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        } catch (Exception e) {
            onProcessed(false);
            return;
        }
        H.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (active && gen == g && step != 3) {
                    onProcessed(false);
                }
            }
        }, 9000);
    }

    static void onProcessed(boolean ok) {
        if (!active) return;
        if (ok) done++;
        else skipped++;
        gen++;
        step = 0;
        idx++;
        pending = true;
        ForceStopService.back();
        H.postDelayed(new Runnable() {
            @Override
            public void run() {
                pending = false;
                launchCurrent(app);
            }
        }, 900);
    }

    /** Called from MainActivity.onResume: if we are back in our app while work is left, reopen the next App Info. */
    static void resume(Context c) {
        if (active && !pending) {
            launchCurrent(c);
        }
    }

    static void finish() {
        active = false;
        step = 0;
        dirty = true;
        if (app == null) return;
        Toast.makeText(app, String.format(Locale.US, S.t(app, "auto_done"), done, skipped),
                Toast.LENGTH_LONG).show();
        try {
            Intent i = new Intent(app, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            app.startActivity(i);
        } catch (Exception e) {
            // Android may block launching from the background; the toast above still tells the result.
        }
    }

    static void cancel() {
        active = false;
        pending = false;
        step = 0;
        gen++;
        dirty = true;
    }
}
