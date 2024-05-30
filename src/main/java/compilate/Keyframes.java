package compilate;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFrame;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.FFprobe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.bramp.ffmpeg.shared.CodecType;

public class Keyframes {

    private final VideoSegment videoSegment;
    private final Integer start;
    private final Integer end;
    private final Path outputSnippet;

    public Keyframes(VideoSegment videoSegment, Path workingDirectory, Path outputSnippet) {
        this.videoSegment = videoSegment;
        this.start = videoSegment.getStart();
        this.end = videoSegment.getEnd();
        this.outputSnippet = outputSnippet;
    }

    public FFmpegBuilder createFFmpegBuilder(FFprobe ffprobe) {
        int adjustedStart = findNearestKeyframe(ffprobe, start);
        int adjustedEnd = findNearestKeyframe(ffprobe, end);

        // Find the nearest keyframe to the adjusted end time
        int nearestEndKeyframeToStart = findNearestKeyframe(ffprobe, adjustedEnd);
        int nearestEndKeyframeToEnd = findNearestKeyframe(ffprobe, adjustedEnd);

        // Determine which keyframe will result in a duration closest to user input
        int durationIfMeasuredFromStart = nearestEndKeyframeToStart - adjustedStart;
        int durationIfMeasuredFromEnd = nearestEndKeyframeToEnd - adjustedStart;
        int nearestKeyFrameToSpecifiedDuration;
        if (Math.abs(durationIfMeasuredFromStart - videoSegment.getDuration()) < Math.abs(durationIfMeasuredFromEnd - videoSegment.getDuration())) {
            nearestKeyFrameToSpecifiedDuration = nearestEndKeyframeToStart;
        } else {
            nearestKeyFrameToSpecifiedDuration = nearestEndKeyframeToEnd;
        }

        return new FFmpegBuilder() // Sometimes creates videos 6s long
                .setInput(videoSegment.getInputVideo().getVideo().getPath().toString())
                .addExtraArgs("-ss", String.valueOf(adjustedStart))
                .addExtraArgs("-to", String.valueOf(nearestKeyFrameToSpecifiedDuration))
                .addOutput(outputSnippet.toString())
                .setVideoCodec("copy") // Copy video stream without re-encoding
                .setAudioCodec("copy") // Copy audio stream without re-encoding
                .addExtraArgs("-avoid_negative_ts", "make_zero")
                .done();
    }

    private int findNearestKeyframe(FFprobe ffprobe, int timeInSeconds) {
        try {
            FFmpegProbeResult probeResult = ffprobe.probe(videoSegment.getInputVideo().getVideo().getPath().toString());
            FFmpegStream videoStream = null;

            for (FFmpegStream stream : probeResult.getStreams()) {
                if (stream.getCodecType() == CodecType.VIDEO) {
                    videoStream = stream;
                    break;
                }
            }

            if (videoStream == null) {
                throw new RuntimeException("No video stream found");
            }

            List<FFmpegFrame> keyframes = new ArrayList<>();
            for (FFmpegFrame frame : probeResult.getFrames()) {
                if (frame.media_type == CodecType.VIDEO && frame.key_frame == 1) {
                    keyframes.add(frame);
                }
            }

            Collections.sort(keyframes, new Comparator<FFmpegFrame>() {
                @Override
                public int compare(FFmpegFrame o1, FFmpegFrame o2) {
                    return Double.compare(o1.pkt_pts_time, o2.pkt_pts_time);
                }
            });

            int nearestKeyframe = timeInSeconds;
            for (FFmpegFrame frame : keyframes) {
                if (frame.pkt_pts_time <= timeInSeconds) {
                    nearestKeyframe = (int) Math.floor(frame.pkt_pts_time);
                } else {
                    break;
                }
            }

            return nearestKeyframe;
        } catch (IOException e) {
            throw new RuntimeException("Error finding keyframe", e);
        }
    }

    public Path getOutputSnippet() {
        return outputSnippet;
    }
}
