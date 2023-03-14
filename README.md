# KGeorgiy media player

Build on top [media player plugin](https://github.com/wuyr/intellij-media-player). Plugin just copypasted in `src`.
Praise [Wuyr](https://github.com/wuyr), thank you for plugin.

From ITMO KT with love.

## How to overload KGeorgiy

1. Locate your current IDEA `.vmoptions` - [here](https://www.jetbrains.com/help/idea/tuning-the-ide.html#locate-jvm-options-file).
2. Add `-Dkgeorgiy.path=%some_path%`

## How to build plugin

Go to `gradle.properties`, set `platformVersion` for IDEA version you use.

Use JDK 17 for project. Either `File > Project Structure` or `JAVA_HOME`. 

Build plugin: `./gradlew buildPlugin`.

`File > Settings > PLugins > Gear icon > Install from disk` - `%repo%/build/distributions`
