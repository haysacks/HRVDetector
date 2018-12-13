package com.example.android.camera2basic;

public class HanningWindow {
    public final double value(int i, int length) {
        if (i >= 0 && i <= length-1) {
            return 0.5 - 0.5 * Math.cos((2 * Math.PI * i) / (length-1));
        } else {
            return 0;
        }
    }

    public final double normalization(int length) {
        double normal = 0.0f;
        for (int i=0; i<=length; i++) {
            normal += this.value(i, length);
        }
        return normal/length;
    }
}
