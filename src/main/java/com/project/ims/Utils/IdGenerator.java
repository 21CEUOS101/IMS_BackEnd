package com.project.ims.Utils;

import java.util.concurrent.ThreadLocalRandom;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String generate(String prefix) {
        return prefix + ThreadLocalRandom.current().nextInt(1_000_000);
    }
}
