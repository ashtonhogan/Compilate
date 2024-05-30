package compilate;

public class InputVideo {

    private final Video video;

    public InputVideo(Video video) {
        this.video = video;
    }

    public Video getVideo() {
        return video;
    }

    public String asString() {
        return "InputVideo{" + "video=" + getVideo().asString() + '}';
    }

    @Override
    public String toString() {
        return asString();
    }

}
