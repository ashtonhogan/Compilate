package compilate;

import java.util.ArrayList;
import java.util.List;

public class VideoSegments {

    private final Integer videoLength;
    private final String startFrom;
    private final Integer segmentDuration;
    private final List<VideoSegment> videoSegments;

    public VideoSegments(Integer videoLength, String startFrom, Integer segmentDuration) {
        this.videoLength = videoLength;
        this.startFrom = startFrom;
        this.segmentDuration = segmentDuration;
        this.videoSegments = new ArrayList<>();
    }

    public Integer getVideoLength() {
        return videoLength;
    }

    public String getStartFrom() {
        return startFrom;
    }

    public Integer getSegmentDuration() {
        return segmentDuration;
    }

    public List<VideoSegment> getVideoSegments(InputVideo inputVideo) {
        if (videoSegments.isEmpty()) {
            // Parse startFrom to seconds
            String[] timeParts = startFrom.split(":");
            int startFromSeconds = Integer.parseInt(timeParts[0]) * 3600
                    + Integer.parseInt(timeParts[1]) * 60
                    + Integer.parseInt(timeParts[2]);

            // Calculate the remaining video length from the starting point
            int remainingLength = videoLength - startFromSeconds;

            // Check for anomalies
            if (segmentDuration > remainingLength || segmentDuration <= 0) {
                throw new IllegalArgumentException("Segment duration is longer than the remaining video length or is non-positive.");
            }

            int currentStart = startFromSeconds;
            while (currentStart + segmentDuration <= videoLength) {
                int currentEnd = currentStart + segmentDuration;
                videoSegments.add(
                        new VideoSegment(
                                inputVideo,
                                currentStart,
                                currentEnd
                        )
                );
                currentStart = currentEnd;
            }
        }
        return videoSegments;
    }
}
