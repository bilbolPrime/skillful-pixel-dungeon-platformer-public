package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import java.util.Random;


public final class MenuEventSchedule {
    private final float[] durations;
    private final int[] order;
    private final Random random;
    private int orderIndex, completed;
    private float time, quietTime;

    public MenuEventSchedule(float[] durations, long seed) {
        if (durations == null || durations.length < 2) throw new IllegalArgumentException("At least two menu events required");
        this.durations = durations.clone();
        order = new int[durations.length];
        for (int i = 0; i < durations.length; i++) {
            if (Float.isNaN(durations[i]) || Float.isInfinite(durations[i]) || durations[i] <= 0f)
                throw new IllegalArgumentException("Invalid menu event duration");
            order[i] = i;
        }
        random = new Random(seed);
        shuffle(-1);
        quietTime = nextQuietTime();
    }

    public void act(float delta) {
        if (Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0f) return;
        time += Math.min(0.1f, delta);
        float duration = durations[index()] + quietTime;
        if (time < duration) return;
        time -= duration;
        int previous = index();
        completed++;
        if (++orderIndex == order.length) {
            shuffle(previous);
            orderIndex = 0;
        }
        quietTime = nextQuietTime();
    }

    public int index() { return order[orderIndex]; }
    public float time() { return time; }
    public int completed() { return completed; }
    public int size() { return order.length; }

    private void shuffle(int previous) {
        for (int i = order.length - 1; i > 0; i--) {
            int other = random.nextInt(i + 1);
            int swap = order[i]; order[i] = order[other]; order[other] = swap;
        }
        if (order[0] == previous) {
            int swap = order[0]; order[0] = order[1]; order[1] = swap;
        }
    }
    private float nextQuietTime() { return 0.8f + random.nextFloat() * 0.7f; }
}
