package com.husker.gradle.ncompile.extensions;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

abstract class PluginExtension {
    abstract Property<Boolean> getConsole()
    abstract Property<String> getVisualStudio()
    abstract Property<String> getOutputName()
    abstract Property<String> getJarPath()
    abstract ListProperty<String> getUpxArgs()
    abstract Property<Boolean> getCompress()

    // Download links
    abstract Property<String> getUpxDownloadLink()
    abstract Property<String> getResourceHackerDownloadLink()
    abstract Property<String> getVSWhereDownloadLink()

    // Exe info
    abstract Property<String> getIcon()
    abstract Property<String> getFileVersion()
    abstract Property<String> getProductVersion()
    abstract Property<String> getCompanyName()
    abstract Property<String> getFileDescription()
    abstract Property<String> getCopyright()
    abstract Property<String> getProductName()

    PluginExtension(){
        visualStudio.convention('#default')
        jarPath.convention('#default')

        upxDownloadLink.convention('https://github.com/upx/upx/releases/download/v3.96/upx-3.96-win64.zip')
        upxArgs.convention(['-9'])
        compress.convention(true)

        VSWhereDownloadLink.convention('https://github.com/microsoft/vswhere/releases/download/2.8.4/vswhere.exe')

        resourceHackerDownloadLink.convention('http://www.angusj.com/resourcehacker/resource_hacker.zip')
        console.convention(true)
        outputName.convention("#default")

        icon.convention("#undefined")
        fileDescription.convention("Unknown description")
        fileVersion.convention("1.0.0.0")
        productVersion.convention("1.0.0.0")
        companyName.convention("Uknown company")
        copyright.convention("(c) Uknown company 1999-2999")
        productName.convention("Unknown product")
    }
}
