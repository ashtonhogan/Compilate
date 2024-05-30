package compilate;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

public class InputVideos {

    private final Path inputDir;
    private final List<Path> inputVideoPaths;
    private final List<InputVideo> inputVideos;
    private Integer totalFootage;

    public InputVideos(Path inputDir) {
        this.inputDir = inputDir;
        System.out.println("InputDir: " + inputDir.toAbsolutePath().toString());
        this.inputVideoPaths = new ArrayList<>();
        this.inputVideos = new ArrayList<>();
        this.totalFootage = null;
    }

    public Path getInputDir() throws IOException {
        // Ensure the directory exists before returning the path
        Files.createDirectories(inputDir);
        return inputDir;
    }

    public List<Path> getInputVideoPaths() throws IOException {
        if (this.inputVideoPaths.isEmpty()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(getInputDir())) {
                for (Path path : stream) {
                    if (path.toString().endsWith(".mp4")) {
                        inputVideoPaths.add(path);
                    }
                }
            }
        }
        return inputVideoPaths;
    }

    public List<InputVideo> getInputVideos() throws IOException {
        if (inputVideos.isEmpty()) {
            for (Path path : getInputVideoPaths()) {
                inputVideos.add(new InputVideo(new Video(path)));
            }
        }
        return inputVideos;
    }

    public Integer getTotalFootage(FFmpeg ffmpeg, FFprobe ffprobe) throws IOException {
        if (totalFootage == null) {
            totalFootage = 0;
            for (InputVideo inputVideo : getInputVideos()) {
                totalFootage += inputVideo.getVideo().getVideoLength(ffmpeg, ffprobe);
            }
        }
        return totalFootage;
    }

    public String asString(FFmpeg ffmpeg, FFprobe ffprobe) throws IOException {
        return "InputVideos{" + "inputDir=" + getInputDir() + ", inputVideoPaths=" + getInputVideoPaths() + ", inputVideos=" + getInputVideos() + ", totalFootage=" + getTotalFootage(ffmpeg, ffprobe) + '}';
    }

}
