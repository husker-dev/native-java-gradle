# Native Java Compile
> Gradle plugin

Simpliest way to create lightweight native executable file, that starts without JVM

#### Supported platforms:

- [x] Windows
- [ ] Linux
- [ ] MacOS

## Getting started

Add plugin to ```build.gradle```:
```gradle

plugins {
  id '***.***.***'
}
```

> Windows also require installed Visual Studio for compilation

You are ready to compile - just start ```compile``` task

## Tasks
- ```compile``` - Compiles target jar, compress it and set configuration
- ```createConfig``` - Creates Reflection/JNI/... configuration for GraalVM. [More info...](https://www.graalvm.org/reference-manual/native-image/BuildConfiguration/)
- ```prepareNative``` - Downloads GraalVM, creates folders, initialize variables 

## Working with reflection
If the project uses ```Reflection```, ```JNI```, or another dynamic code call, then you need to create a configuration for GraalVM using task ```createConfig```

When the application is launched, you need to use every possible ```Reflection```, ```JNI```, ... invoking

## Configuration

The configuration is set in the block: ```nativeCompile```
```gradle
// Example
nativeCompile {
  compress = false
  
  output {
    console = false
    icon = "$projectDir/icon.ico"
  }
}
```

| Property | Type | Description | Default value |
| :------ | :--: | :---------: | :-----------: |
| ```jarPath``` | String | Target jar file to compile | build output |
| ```visualStudio``` | String | Custom Visual Studio path | installed instance path |
| ```compress``` | boolean | Enable / Disable the use of UPX for compression | ```true``` |
| ```upxArgs``` | String[] | UPX compressor arguments | ```['-9']``` |

## Output configuration
The output configuration is set in the block: ```output```
| Property | Type | Description | Default value |
| :------ | :--: | :---------: | :-----------: |
| ```outputName``` | String | Output file name | build output name |
| ```console``` | boolean | Enable / Disable console application type | ```true``` |
| ```icon``` | String | Path to ```.ico``` icon file |  |
| ```fileDescription``` | String | Short file description | ```Unknown description``` |
| ```fileVersion``` | String | File version | ```1.0.0.0``` |
| ```productVersion``` | String | Product version | ```1.0.0.0``` |
| ```companyName``` | String | Company name | ```Uknown company``` |
| ```copyright``` | String | Copyright text | ```(c) Uknown company 1999-2999``` |
| ```productName``` | String | Product name | ```Unknown product``` |

## GraalVM configuration
The GraalVM configuration is set in the block: ```graalvm```
| Property | Type | Description | Default value |
| :------ | :--: | :---------: | :-----------: |
| ```java``` | String | Java version | ```11``` |
| ```version``` | String | GraalVM version | ```21.1.0``` |
| ```arch``` | String | Processor archeticture | ```amd64``` |
| ```path``` | String | Custom GraalVM path | install automatically |
| ```args``` | String[] | Compile arguments | ```[]``` |


## Downloads configuration
The Downloads configuration is set in the block: ```downloads```
| Property | Type | Description | Default value |
| :------ | :--: | :---------: | :-----------: |
| ```upxDownloadLink``` | String | ```UPX``` download link | https://github.com/upx/upx/releases/download/v3.96/upx-3.96-win64.zip |
| ```VSWhereDownloadLink``` | String | ```vswhere.exe``` download link | https://github.com/microsoft/vswhere/releases/download/2.8.4/vswhere.exe |
| ```resourceHackerDownloadLink``` | String | ```Resource Hacker``` download link | http://www.angusj.com/resourcehacker/resource_hacker.zip |
