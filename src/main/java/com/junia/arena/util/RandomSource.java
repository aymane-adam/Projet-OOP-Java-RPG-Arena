package com.junia.arena.util;

/**
 * Source of randomness, abstracted so tests can make battles fully predictable
 * (for example with the lambda {@code () -> 0.99} that never triggers a critical hit).
 */
@FunctionalInterface
public interface RandomSource {

    /**
     * @return a value in [0, 1)
     */
    double nextDouble();
}
