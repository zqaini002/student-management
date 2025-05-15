package com.example.student.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * 日志工具类
 *
 * @author example
 */
public class LogUtils {

    /**
     * 获取业务日志记录器
     *
     * @param clazz 类
     * @return 日志记录器
     */
    public static Logger getBusinessLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 记录调试日志
     *
     * @param logger  日志记录器
     * @param message 日志内容
     */
    public static void debug(Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * 记录调试日志（带参数）
     *
     * @param logger  日志记录器
     * @param format  日志格式
     * @param objects 参数列表
     */
    public static void debug(Logger logger, String format, Object... objects) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, objects);
        }
    }

    /**
     * 记录信息日志
     *
     * @param logger  日志记录器
     * @param message 日志内容
     */
    public static void info(Logger logger, String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * 记录信息日志（带参数）
     *
     * @param logger  日志记录器
     * @param format  日志格式
     * @param objects 参数列表
     */
    public static void info(Logger logger, String format, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(format, objects);
        }
    }

    /**
     * 记录警告日志
     *
     * @param logger  日志记录器
     * @param message 日志内容
     */
    public static void warn(Logger logger, String message) {
        logger.warn(message);
    }

    /**
     * 记录警告日志（带参数）
     *
     * @param logger  日志记录器
     * @param format  日志格式
     * @param objects 参数列表
     */
    public static void warn(Logger logger, String format, Object... objects) {
        logger.warn(format, objects);
    }

    /**
     * 记录警告日志（带异常）
     *
     * @param logger    日志记录器
     * @param message   日志内容
     * @param throwable 异常
     */
    public static void warn(Logger logger, String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    /**
     * 记录错误日志
     *
     * @param logger  日志记录器
     * @param message 日志内容
     */
    public static void error(Logger logger, String message) {
        logger.error(message);
    }

    /**
     * 记录错误日志（带参数）
     *
     * @param logger  日志记录器
     * @param format  日志格式
     * @param objects 参数列表
     */
    public static void error(Logger logger, String format, Object... objects) {
        logger.error(format, objects);
    }

    /**
     * 记录错误日志（带异常）
     *
     * @param logger    日志记录器
     * @param message   日志内容
     * @param throwable 异常
     */
    public static void error(Logger logger, String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * 记录日志（可变日志级别）
     *
     * @param logger  日志记录器
     * @param level   日志级别
     * @param message 日志内容
     */
    public static void log(Logger logger, Level level, String message) {
        switch (level) {
            case DEBUG -> debug(logger, message);
            case INFO -> info(logger, message);
            case WARN -> warn(logger, message);
            case ERROR -> error(logger, message);
            default -> info(logger, message);
        }
    }

    /**
     * 记录业务操作日志
     *
     * @param module    业务模块
     * @param operation 操作类型
     * @param details   操作详情
     * @param logger    日志记录器
     */
    public static void logOperation(String module, String operation, String details, Logger logger) {
        String logMessage = String.format("[业务日志] 模块:%s, 操作:%s, 详情:%s", module, operation, details);
        info(logger, logMessage);
    }
}