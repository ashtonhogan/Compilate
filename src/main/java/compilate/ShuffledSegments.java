package compilate;

import compilate.ui.RightPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShuffledSegments {

    private final Map<InputVideo, VideoSegments> inputVideoSegments;
    private final List<VideoSegment> shuffledVideoSegments;

    public ShuffledSegments(Map<InputVideo, VideoSegments> inputVideoSegments) {
        this.inputVideoSegments = inputVideoSegments;
        this.shuffledVideoSegments = new ArrayList<>();
    }

    public Map<InputVideo, VideoSegments> getInputVideoSegments() {
        return inputVideoSegments;
    }

    public final List<VideoSegment> getShuffledVideoSegments() {
        if (shuffledVideoSegments.isEmpty()) {
            // Temporary container to hold the copied inputVideoSegments
            Map<InputVideo, List<VideoSegment>> tempContainer = new HashMap<>();

            // Copy the inputVideoSegments into the tempContainer
            for (Map.Entry<InputVideo, VideoSegments> entry : inputVideoSegments.entrySet()) {
                tempContainer.put(
                        entry.getKey(),
                        new ArrayList<>(entry.getValue().getVideoSegments(entry.getKey()))
                );
            }

            // Resulting list to return
            Random random = new Random();

            // Loop until the tempContainer is empty
            while (!tempContainer.isEmpty()) {
                // Get a random InputVideo from the tempContainer
                List<InputVideo> inputVideos = new ArrayList<>(tempContainer.keySet());
                InputVideo randomInputVideo = inputVideos.get(random.nextInt(inputVideos.size()));

                // Get the list of VideoSegments for the randomInputVideo
                List<VideoSegment> segments = tempContainer.get(randomInputVideo);

                // Get a random VideoSegment from the segments list
                VideoSegment randomSegment = segments.get(random.nextInt(segments.size()));

                // Add the selected VideoSegment to the shuffledSegments list
                shuffledVideoSegments.add(randomSegment);
                // GUI.status.append("Added segment from: " + randomInputVideo.getVideo().getPath().getFileName() + "\n");

                // Remove the selected VideoSegment from the segments list
                segments.remove(randomSegment);

                // If the segments list is empty, remove the InputVideo from the tempContainer
                if (segments.isEmpty()) {
                    tempContainer.remove(randomInputVideo);
                }
            }

            RightPanel.status.append("There are: " + shuffledVideoSegments.size() + " segments available." + "\n");
        }
        return shuffledVideoSegments;
    }

//    public final Map<InputVideo, VideoSegment> getShuffledVideoSegments() {
//        //TODO this method will create a new array by making a copy of this.inputVideoSegments
//        //the copy will act like a temporary container to take entries from
//        //this method will take 1 random entry at a time until the container is empty
//        //for example, let's say that the total array size based on this.inputVideoSegments is arr[1][12]
//        //in other words, there is 1 input video and 12 video segments for it
//        //then this method will generate a random x and a random y for the length of each index
//        //for example arr[ranX][ranY]
//        //when ranY has no more videosegment elements left then the respective arr[x] can be removed from the container
//        //so that no future attempts are made to retrieve elements from there
//        //by the end of this process, it will have copied all the elements to a temporary container
//        //then randomly retrieved and removed each <InputVideo, VideoSegment> entry
//        //until the the temporary container is empty
//        //and the Map being returned has the same total number of elements as inputVideo*videoSegments from inputVideoSegments
//        //The input map is structured like this: 
//        // for (InputVideo inputVideo : inputVideos) { List<VideoSegment> segments = inputVideoSegments.get(inputVideo).getVideoSegments(); }
//        //Good luck
//        
//        Map<InputVideo, VideoSegment> shuffledMap = new LinkedHashMap<>();
//        Set<VideoSegment> usedSegments = new HashSet<>();
//
//        List<InputVideo> inputVideos = new ArrayList<>(inputVideoSegments.keySet());
//        Collections.shuffle(inputVideos); // Shuffle the list to randomize selection order
//
//        Random random = new Random();
//
//        for (InputVideo inputVideo : inputVideos) {
//            List<VideoSegment> segments = inputVideoSegments.get(inputVideo).getVideoSegments();
//            GUI.status.append("Shuffling " + segments.size() + " of segments from " + inputVideo.getVideo().getPath().getFileName() + "\n");
//            while (!segments.isEmpty()) {
//                int randomIndex = random.nextInt(segments.size());
//                VideoSegment selectedSegment = segments.get(randomIndex);
//                GUI.status.append("Current segment start-end " + selectedSegment.getStart() + "-" + selectedSegment.getEnd() + "\n");
//                GUI.status.append("usedSegments contains it? " + usedSegments.contains(selectedSegment) + "\n");
//                if (!usedSegments.contains(selectedSegment)) {
//                    usedSegments.add(selectedSegment);
//                    shuffledMap.put(inputVideo, selectedSegment);
//                    break; // Move to the next InputVideo once a unique segment is found
//                }
//            }
//        }
//
//        return shuffledMap;
//    }
}
