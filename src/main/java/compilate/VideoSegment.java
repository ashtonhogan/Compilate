package compilate;

public class VideoSegment {

    private final InputVideo inputVideo;
    private final Integer start;
    private final Integer end;

    public VideoSegment(InputVideo inputVideo, Integer start, Integer end) {
        this.inputVideo = inputVideo;
        this.start = start;
        this.end = end;
    }

    public InputVideo getInputVideo() {
        return inputVideo;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public Integer getDuration() {
        return end - start;
    }
}
