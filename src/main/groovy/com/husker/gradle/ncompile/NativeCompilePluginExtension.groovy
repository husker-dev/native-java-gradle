package com.husker.gradle.ncompile;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

abstract class NativeCompilePluginExtension {
    abstract Property<Boolean> getConsole()
    abstract Property<String> getGraalVM()
    abstract Property<String> getVcVars()
    abstract Property<String> getOutputName()
    abstract Property<String> getJarPath()
    abstract ListProperty<String> getUpxArgs()
    abstract ListProperty<String> getGraalArgs()
    abstract Property<Boolean> getCompress()

    // Download links
    abstract Property<String> getUpxDownloadLink()
    abstract Property<String> getResourceHackerDownloadLink()

    // Exe info
    abstract Property<String> getIcon()
    abstract Property<String> getFileVersion()
    abstract Property<String> getProductVersion()
    abstract Property<String> getCompanyName()
    abstract Property<String> getFileDescription()
    abstract Property<String> getCopyright()
    abstract Property<String> getProductName()

    NativeCompilePluginExtension(){
        vcVars.convention('#undefined')
        jarPath.convention('#default')
        graalArgs.convention([])

        upxDownloadLink.convention('https://github.com/upx/upx/releases/download/v3.96/upx-3.96-win64.zip')
        upxArgs.convention(['-9'])
        compress.convention(true)

        resourceHackerDownloadLink.convention('http://www.angusj.com/resourcehacker/resource_hacker.zip')
        console.convention(true)
        outputName.convention("application")

        icon.convention("#undefined")
        fileDescription.convention("Unknown description")
        fileVersion.convention("1.0.0.0")
        productVersion.convention("1.0.0.0")
        companyName.convention("Uknown company")
        copyright.convention("(c) Uknown company 1999-2999")
        productName.convention("Unknown product")
    }
}
