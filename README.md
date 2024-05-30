### Compilate

![Compilate Screenshot](https://github.com/ashtonhogan/Compilate/blob/main/screenshot.png?raw=true)

Compilate is a versatile cross-platform video tool designed to make fun compilation videos.

#### Running the Application:
To run Compilate on your system, ensure you have the latest Java Virtual Machine (JVM) or Java Development Kit (JDK) installed. You can create your own build using Apache Maven with the command `mvn clean install`, which will generate a JAR file in the `target` directory. Alternatively, download the latest version from releases.

To launch Compilate:
- **Double-click**: Open `Compilate-1.0.0.jar`.
- **Terminal**: Run `java -jar Compilate-1.0.0.jar`.

You will also need the following folders in the same directory as your jar:

- [ffmpeg-6.1-full_build](https://ffmpeg.org/releases/ffmpeg-6.1.tar.xz) - This version works but it will probably also work with the latest version
- InputVideos - Empty folder where you put your input videos
- OutputVideos - Empty folder where this application puts your compilation videos

The free version does not allow compilation videos longer than 1 minute whereas the [full version](https://ashtonhogan.gumroad.com/l/phsfe) has no limitations.