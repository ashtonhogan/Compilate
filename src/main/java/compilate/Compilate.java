package compilate;

import java.io.IOException;
import java.util.List;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

public class Compilate {

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final InputVideos inputVideos;
    private final OutputVideos outputVideos;
    private final Boolean fastMode;

    public Compilate(FFmpeg ffmpeg, FFprobe ffprobe, InputVideos inputVideos, OutputVideos outputVideos, Boolean fastMode) {
        this.ffmpeg = ffmpeg;
        this.ffprobe = ffprobe;
        this.inputVideos = inputVideos;
        this.outputVideos = outputVideos;
        this.fastMode = fastMode;
    }

    public FFmpeg getFfmpeg() {
        return ffmpeg;
    }

    public FFprobe getFfprobe() {
        return ffprobe;
    }

    public InputVideos getInputVideos() {
        return inputVideos;
    }

    public OutputVideos getOutputVideos() {
        return outputVideos;
    }

    public Boolean getFastMode() {
        return fastMode;
    }

    public List<OutputVideo> execute() throws IOException {
        return outputVideos.getOutputVideos(ffmpeg, ffprobe, inputVideos, fastMode);
    }

}
