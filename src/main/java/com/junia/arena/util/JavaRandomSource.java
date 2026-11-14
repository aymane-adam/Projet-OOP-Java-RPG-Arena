package com.junia.arena.util;

import java.util.Random;

public class JavaRandomSource implements RandomSource {

    private final Random random;

    public JavaRandomSource() {
        this.random = new Random();
    }

    /** A fixed seed replays exactly the same battles, useful to reproduce a result. */
    public JavaRandomSource(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}
