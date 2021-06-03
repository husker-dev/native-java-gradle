package com.husker.gradle.ncompile.tasks

import org.gradle.api.DefaultTask
import org.gradle.internal.logging.progress.ProgressLogger
import org.gradle.internal.logging.progress.ProgressLoggerFactory

abstract class ProgressTask extends DefaultTask{
    ProgressLogger progressLogger = getProgressLogger()

    void startProgress(){
        progressLogger.started()
    }

    void setProgress(String text){
        progressLogger.progress(text)
    }

    void completeProgress(){
        progressLogger.completed()
    }

    ProgressLogger getProgressLogger(){
        def progressLoggerFactory = project.gradle.getServices().get(ProgressLoggerFactory.class)
        return progressLoggerFactory.newOperation(name)
    }
}