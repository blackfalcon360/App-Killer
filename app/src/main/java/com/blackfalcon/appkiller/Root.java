package com.blackfalcon.appkiller;

import java.io.InputStream;

final class Root {
    private Root() {}

    /** Runs one command through su. Returns true only if su granted access and the command exited with 0. */
    static boolean run(String cmd) {
        try {
            ProcessBuilder pb = new ProcessBuilder("su", "-c", cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            InputStream in = p.getInputStream();
            byte[] buf = new byte[512];
            while (in.read(buf) != -1) {
                // drain output
            }
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
