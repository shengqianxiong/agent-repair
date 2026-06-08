package com.sqx.modules.agent.support;

import cn.hutool.core.util.StrUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 目录存在性校验
 */
public final class DirectoryChecker {

    private DirectoryChecker() {
    }

    public static boolean exists(String directoryPath) {
        if (StrUtil.isBlank(directoryPath)) {
            return false;
        }
        Path path = Paths.get(directoryPath.trim());
        return Files.exists(path) && Files.isDirectory(path);
    }
}
