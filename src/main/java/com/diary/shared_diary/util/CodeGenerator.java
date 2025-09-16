package com.diary.shared_diary.util;

import java.util.UUID;

public class CodeGenerator {

    public static String generateGroupCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
