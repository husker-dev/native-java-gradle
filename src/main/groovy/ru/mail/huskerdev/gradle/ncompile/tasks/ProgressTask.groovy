package ru.mail.huskerdev.gradle.ncompile.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.internal.logging.progress.ProgressLogger
import org.gradle.internal.logging.progress.ProgressLoggerFactory

abstract class ProgressTask extends DefaultTask{
    @Internal
    ProgressLogger progressLogger = getProgressLogger()

    void startProgress(String description){
        progressLogger.description = description
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