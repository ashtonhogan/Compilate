package compilate;

import compilate.ui.RightPanel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

public class OutputVideos {

    private final Path outputDir;
    private final String startingPoint;
    private final Integer snippetDuration;
    private final Integer outputVideoDuration;
    private final Integer outputVideoQuantity;
    private final List<OutputVideo> outputVideos;

    public OutputVideos(Path outputDir, String startingPoint, Integer snippetDuration, Integer outputVideoDuration, Integer outputVideoQuantity) {
        this.outputDir = outputDir;
        this.startingPoint = startingPoint;
        this.snippetDuration = snippetDuration;
        this.outputVideoDuration = outputVideoDuration;
        this.outputVideoQuantity = outputVideoQuantity;
        this.outputVideos = new ArrayList<>();
    }

    public Path getOutputDir() throws IOException {
        // Ensure the directory exists before returning the path
        Files.createDirectories(outputDir);
        return outputDir;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public Integer getSnippetDuration() {
        return snippetDuration;
    }

    public Integer getOutputVideoDuration() {
        return outputVideoDuration;
    }

    public Integer getOutputVideoQuantity() {
        return outputVideoQuantity;
    }

    public List<OutputVideo> getOutputVideos(FFmpeg ffmpeg, FFprobe ffprobe, InputVideos inputVideos, Boolean fastMode) throws IOException {
        if (outputVideos.isEmpty()) {
            Integer totalFootage = inputVideos.getTotalFootage(ffmpeg, ffprobe);
            RightPanel.status.append("There is a total of " + (totalFootage / 60) + " minutes of footage to extract from" + "\n");
            Integer maxOutputVideos = Math.divideExact(totalFootage, outputVideoDuration);
            RightPanel.status.append(String.format("Based on the total footage available and the above configuration, you can generate a maximum of %d videos.%n" + "\n", maxOutputVideos));
            if (outputVideoQuantity > maxOutputVideos) {
                throw new IllegalArgumentException("Requested number of output videos exceeds the maximum possible based on available footage.");
            }

            // Generate the segments for all input files
            Map<InputVideo, VideoSegments> inputVideoSegments = new HashMap<>();
            for (InputVideo inputVideo : inputVideos.getInputVideos()) {
                VideoSegments videoSegments = new VideoSegments(
                        inputVideo.getVideo().getVideoLength(ffmpeg, ffprobe),
                        startingPoint,
                        snippetDuration
                );
                inputVideoSegments.put(
                        inputVideo,
                        videoSegments
                );
            }

            //Print snippet headers
            System.out.println("+--------------------------+-----------------------+---------------------+");
            System.out.println("   Input Video filename         Timestamp Start          Timestamp End");
            System.out.println("+--------------------------+-----------------------+---------------------+");

            ShuffledSegments shuffledSegments = new ShuffledSegments(inputVideoSegments);
            List<VideoSegment> videoSegmentContainer = shuffledSegments.getShuffledVideoSegments();
            for (int i = 0; i < outputVideoQuantity; i++) {
                // Extract segments for output video
                List<VideoSegment> segmentsForOutput = new ArrayList<>();
                int remainingDuration = outputVideoDuration;
                Iterator<VideoSegment> iterator = videoSegmentContainer.iterator();
                while (iterator.hasNext() && remainingDuration > 0) {
                    VideoSegment segment = iterator.next();
                    int segmentDuration = segment.getDuration();
                    if (segmentDuration <= remainingDuration) {
                        segmentsForOutput.add(segment);
                        remainingDuration -= segmentDuration;
                        iterator.remove();
                    }
                }
                RightPanel.status.append("Extracted " + segmentsForOutput.size() + " segments for output video. " + videoSegmentContainer.size() + " segments are still available." + "\n");

                OutputVideo outputVideo = new OutputVideo(outputDir.resolve("temp"), segmentsForOutput);

                outputVideos.add(outputVideo);

                RightPanel.status.append("Created " + outputVideo.getOutputVideo(outputDir, UUID.randomUUID() + ".mp4", ffmpeg, ffprobe, fastMode).getPath().getFileName() + "\n");
            }
        }
         RightPanel.status.append("Finished current job");
        return outputVideos;
    }

}
