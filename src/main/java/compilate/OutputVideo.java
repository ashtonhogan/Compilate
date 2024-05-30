package compilate;

import compilate.ui.RightPanel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class OutputVideo {

    private final Path workingDirectory;
    private final List<VideoSegment> videoSegments;
    private Video outputVideo;

    public OutputVideo(Path workingDirectory, List<VideoSegment> videoSegments) {
        this.workingDirectory = workingDirectory;
        this.videoSegments = videoSegments;
        this.outputVideo = null;
    }

    public Path getWorkingDirectory() throws IOException {
        // Ensure the directory exists before returning the path
        Files.createDirectories(workingDirectory);
        return workingDirectory;
    }

    public Video getOutputVideo(Path outputDirectory, String outputVideoFilename, FFmpeg ffmpeg, FFprobe ffprobe, Boolean fastMode) throws IOException {
        if (outputVideo == null) {
            List<Path> snippetPaths = new ArrayList<>();
            for (VideoSegment videoSegment : videoSegments) {
                String startTime = String.format("%02d:%02d:%02d", videoSegment.getStart() / 3600, (videoSegment.getStart() % 3600) / 60, videoSegment.getStart() % 60);
                String endTime = String.format("%02d:%02d:%02d", (videoSegment.getStart() + videoSegment.getDuration()) / 3600, ((videoSegment.getStart() + videoSegment.getDuration()) % 3600) / 60, (videoSegment.getStart() + videoSegment.getDuration()) % 60);
                System.out.printf("%-30s %-22s %-21s\n", videoSegment.getInputVideo().getVideo().getPath().getFileName(), startTime, endTime);
                System.out.println("+--------------------------+-----------------------+---------------------+");

                // Create snippet files
                Path outputSnippet = getWorkingDirectory().resolve(UUID.randomUUID() + ".mp4");
                FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

                FFmpegBuilder fmpegBuilder;
                if (fastMode) {
                    Keyframes keyframes = new Keyframes(videoSegment, getWorkingDirectory(), outputSnippet);
                    fmpegBuilder = keyframes.createFFmpegBuilder(ffprobe);
                } else {
                    fmpegBuilder = new FFmpegBuilder()
                            .setStartOffset(videoSegment.getStart(), TimeUnit.SECONDS) // Seeking to the start offset
                            .setInput(videoSegment.getInputVideo().getVideo().getPath().toString())
                            .addOutput(outputSnippet.toString())
                            .setDuration(videoSegment.getDuration(), TimeUnit.SECONDS) // Set the duration of the snippet
                            .setVideoCodec("libx264")
                            //.setVideoCodec("copy") // Don't recompress, just cut (Breaks keyframes)
                            .setPreset("fast") // Set preset for faster encoding
                            .setVideoFrameRate(30)
                            //.addExtraArgs("-avoid_negative_ts", "make_zero") // Ensure keyframe safety (Fixes keyframes for copy but breaks duration)
                            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Enable experimental features if necessary
                            .setAudioCodec("aac")
                            //.setAudioCodec("copy") // Don't recompress, just cut (Breaks keyframes)
                            .setAudioBitRate(128_000)
                            .done();
                }

                executor.createJob(fmpegBuilder).run();
                snippetPaths.add(outputSnippet);
            }

            // Merge snippets into final output video
            Path outputPath = outputDirectory.resolve(outputVideoFilename);
            Path snippetsList = Files.createTempFile(getWorkingDirectory(), "file_list", ".txt");

            try {
                mergeSnippets(ffmpeg, snippetPaths, outputPath, snippetsList);
            } catch (Exception ex) {
                RightPanel.status.append("Your snippets could not be merged, please report the bug to me" + "\n" + ex.getMessage() + "\n");
                Logger.getLogger(OutputVideo.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Delete snippets
            for (Path path : snippetPaths) {
                Files.deleteIfExists(path);
            }
            Files.deleteIfExists(snippetsList);

            outputVideo = new Video(outputPath);
        }
        return outputVideo;
    }

    private void mergeSnippets(FFmpeg ffmpeg, List<Path> snippetPaths, Path outputPath, Path tempFileList) throws Exception {
        // Create a temporary file to list all snippets
        List<String> lines = new ArrayList<>();
        for (Path snippetPath : snippetPaths) {
            lines.add("file '" + snippetPath.toAbsolutePath().toString().replace("\\", "/") + "'");
        }
        Files.write(tempFileList, lines);

        // Execute FFmpeg to merge snippets
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        FFmpegBuilder builder = new FFmpegBuilder()
                .addExtraArgs("-f", "concat")
                .addExtraArgs("-safe", "0")
                .setInput(tempFileList.toString())
                .addOutput(outputPath.toAbsolutePath().toString())
                .setAudioCodec("copy") // Don't recompress, just copy
                .setVideoCodec("copy") // Don't recompress, just copy
                .setVideoFrameRate(30) // Set frame rate (adjust as needed)
                //.addExtraArgs("-avoid_negative_ts", "make_zero") // Ensure keyframe safety
                .done();
        executor.createJob(builder).run();
    }
}
