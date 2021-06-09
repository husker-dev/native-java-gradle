package com.husker.gradle.ncompile.extensions

import org.gradle.api.provider.Property

abstract class DownloadsExtension {

    abstract Property<String> getUpxDownloadLink()
    abstract Property<String> getResourceHackerDownloadLink()
    abstract Property<String> getVSWhereDownloadLink()

    DownloadsExtension(){
        upxDownloadLink.convention('https://github.com/upx/upx/releases/download/v3.96/upx-3.96-win64.zip')
        VSWhereDownloadLink.convention('https://github.com/microsoft/vswhere/releases/download/2.8.4/vswhere.exe')
        resourceHackerDownloadLink.convention('http://www.angusj.com/resourcehacker/resource_hacker.zip')
    }
}
