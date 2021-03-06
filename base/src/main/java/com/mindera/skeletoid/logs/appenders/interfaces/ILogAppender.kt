package com.mindera.skeletoid.logs.appenders.interfaces

import android.content.Context

import com.mindera.skeletoid.logs.LOG

/**
 * Interface for all Log appenders
 */
interface ILogAppender {

    /**
     * Appender minimum log level {@see com.mindera.skeletoid.logs.LOG.PRIORITY}
     */
    var minLogLevel: LOG.PRIORITY

    /**
     * LOG id (it should be unique within LogAppenders)
     * The name LogcatAppender and LogFileAppender are already taken if the default log appenders
     * are used
     */
    val loggerId: String

    /**
     * Enable the logger
     *
     * @param context Application context
     */
    fun enableAppender(context: Context)

    /**
     * Disable the logger.
     * If its something like a file logger it should close all things related to it to avoid memory leaks
     */
    fun disableAppender()

    /**
     * Write a log
     *
     * @param type Type of log
     * @param t    Throwable (can be null)
     * @param logs  Log
     */
    fun log(type: LOG.PRIORITY, t: Throwable?, vararg logs: String)

    /**
     * Sets a custom property of the user.
     * Default implementation does nothing since there are logging services
     * that don't need to set user properties.
     *
     * @param key  Property name
     * @param value Property value
     */
    fun setUserProperty(key: String, value: String?) {}
}
