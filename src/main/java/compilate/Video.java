package compilate;

import java.io.IOException;
import java.nio.file.Path;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class Video {

    private final Path path;

    public Video(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

//    public int getVideoLength(FFmpeg ffmpeg, FFprobe ffprobe) throws IOException {
//        System.out.println("getVideolength");
//        FFmpegProbeResult ffmpegProbeResult = ffprobe.probe(path.toString());
//        System.out.println(ffmpegProbeResult.toString());
//        FFmpegStream ffmpegStream = ffmpegProbeResult.getStreams().get(1);
//        System.out.println("Size: " +ffmpegProbeResult.getStreams().size());
//        System.out.println(ffmpegStream.toString());
//        double durationInSeconds = ffmpegStream.duration;
//        System.out.println(durationInSeconds);
//        return (int) durationInSeconds;
//    }
    public int getVideoLength(FFmpeg ffmpeg, FFprobe ffprobe) throws IOException {

        // Run ffprobe command to get duration
        FFmpegProbeResult probeResult = ffprobe.probe(path.toString());

        // Extract duration from FFmpegProbeResult
        FFmpegFormat format = probeResult.getFormat();
        double duration = format.duration;

        return (int) duration;
    }

    public String asString() {
        return "Video{" + "path=" + getPath().toString() + '}';
    }

}
