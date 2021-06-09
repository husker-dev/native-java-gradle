package com.husker.gradle.ncompile.extensions

import org.gradle.api.provider.Property

abstract class InfoExtension {

    abstract Property<String> getOutputName()
    abstract Property<Boolean> getConsole()
    abstract Property<String> getIcon()
    abstract Property<String> getFileVersion()
    abstract Property<String> getProductVersion()
    abstract Property<String> getCompanyName()
    abstract Property<String> getFileDescription()
    abstract Property<String> getCopyright()
    abstract Property<String> getProductName()

    InfoExtension(){
        outputName.convention("#default")
        console.convention(true)
        icon.convention("#undefined")
        fileDescription.convention("Unknown description")
        fileVersion.convention("1.0.0.0")
        productVersion.convention("1.0.0.0")
        companyName.convention("Uknown company")
        copyright.convention("(c) Uknown company 1999-2999")
        productName.convention("Unknown product")

    }
}
