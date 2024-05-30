package compilate.ui;

import compilate.Compilate;
import compilate.InputVideos;
import compilate.OutputVideos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

public class LeftPanel extends JPanel {

    private SwingWorker<Void, Void> compileWorker;

    public JTextField ffmpegLocationField;
    public JTextField ffprobeLocationField;
    public JTextField startingPointField;
    public JTextField snippetDurationField;
    public JTextField outputVideoDurationField;
    public JTextField outputVideoQuantityField;
    public JComboBox<String> modeDropdown;

    public LeftPanel(JFrame jframe) {
        // Set panel size and layout
        Dimension panelDimensions = new Dimension(
                (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.25),
                Toolkit.getDefaultToolkit().getScreenSize().height
        );

        setPreferredSize(panelDimensions);
        setLayout(new BorderLayout());

        // Set background color
        setBackground(new Color(50, 50, 50)); // Stylish dark gray

        // Logo at the top
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
        Dimension logoDimension = determineLogoDimensions(panelDimensions, logoIcon);
        Image resizedLogo = logoIcon.getImage().getScaledInstance((int) logoDimension.getWidth(), (int) logoDimension.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon resizedLogoIcon = new ImageIcon(resizedLogo);
        JLabel logoLabel = new JLabel(resizedLogoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(logoLabel, BorderLayout.NORTH);

        // Input fields and button
        JPanel inputPanel = new JPanel(new GridLayout(20, 1, 10, 10));
        inputPanel.setBackground(new Color(50, 50, 50)); // Stylish dark gray
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Define font and color settings
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = Color.WHITE;

        JLabel ffmpegLocationLabel = new JLabel();
        ffmpegLocationLabel.setText("ffmpeg.exe Location");
        ffmpegLocationLabel.setFont(labelFont);
        ffmpegLocationLabel.setForeground(labelColor);
        inputPanel.add(ffmpegLocationLabel);

        ffmpegLocationField = new JTextField();
        ffmpegLocationField.setMargin(new Insets(5, 5, 5, 5));
        ffmpegLocationField.setText("ffmpeg-6.1-full_build/bin/ffmpeg.exe");
        inputPanel.add(ffmpegLocationField);

        JLabel ffprobeLocationLabel = new JLabel();
        ffprobeLocationLabel.setText("ffprobe.exe Location");
        ffprobeLocationLabel.setFont(labelFont);
        ffprobeLocationLabel.setForeground(labelColor);
        inputPanel.add(ffprobeLocationLabel);

        ffprobeLocationField = new JTextField();
        ffprobeLocationField.setMargin(new Insets(5, 5, 5, 5));
        ffprobeLocationField.setText("ffmpeg-6.1-full_build/bin/ffprobe.exe");
        inputPanel.add(ffprobeLocationField);

        JLabel startingPointLabel = new JLabel();
        startingPointLabel.setText("Skip beginning (HH:mm:ss)");
        startingPointLabel.setFont(labelFont);
        startingPointLabel.setForeground(labelColor);
        inputPanel.add(startingPointLabel);

        startingPointField = new JTextField();
        startingPointField.setMargin(new Insets(5, 5, 5, 5));
        startingPointField.setText("00:00:35");
        inputPanel.add(startingPointField);

        JLabel snippetDurationLabel = new JLabel();
        snippetDurationLabel.setText("Snippet Videos Duration");
        snippetDurationLabel.setFont(labelFont);
        snippetDurationLabel.setForeground(labelColor);
        inputPanel.add(snippetDurationLabel);

        snippetDurationField = new JTextField();
        snippetDurationField.setMargin(new Insets(5, 5, 5, 5));
        snippetDurationField.setText("2");
        inputPanel.add(snippetDurationField);

        JLabel outputVideoDurationLabel = new JLabel();
        outputVideoDurationLabel.setText("Output Video Duration");
        outputVideoDurationLabel.setFont(labelFont);
        outputVideoDurationLabel.setForeground(labelColor);
        inputPanel.add(outputVideoDurationLabel);

        outputVideoDurationField = new JTextField();
        outputVideoDurationField.setMargin(new Insets(5, 5, 5, 5));
        outputVideoDurationField.setText("30");
        inputPanel.add(outputVideoDurationField);

        JLabel outputVideoQuantityLabel = new JLabel();
        outputVideoQuantityLabel.setText("Output Video Quantity");
        outputVideoQuantityLabel.setFont(labelFont);
        outputVideoQuantityLabel.setForeground(labelColor);
        inputPanel.add(outputVideoQuantityLabel);

        outputVideoQuantityField = new JTextField();
        outputVideoQuantityField.setMargin(new Insets(5, 5, 5, 5));
        outputVideoQuantityField.setText("19");
        inputPanel.add(outputVideoQuantityField);

        JLabel modeDropdownLabel = new JLabel();
        modeDropdownLabel.setText("Processing Mode");
        modeDropdownLabel.setFont(labelFont);
        modeDropdownLabel.setForeground(labelColor);
        inputPanel.add(modeDropdownLabel);

        String[] modes = {"Fast Mode (Potential extended video snippets)", "Accurate Mode"};
        modeDropdown = new JComboBox<>(modes);
        modeDropdown.setSelectedIndex(1); // Default to "Accurate Mode"
        inputPanel.add(modeDropdown);

        inputPanel.add(Box.createVerticalStrut(5));
        
        JButton runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if a worker is already running
                if (compileWorker != null && !compileWorker.isDone()) {
                    // Cancel the existing worker if it's still running
                    compileWorker.cancel(true);
                }

                // Create a new SwingWorker instance
                compileWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Your background task code here
                        String ffmpegLocationInput = ffmpegLocationField.getText();
                        String ffprobeLocationInput = ffprobeLocationField.getText();
                        String startingPointInput = startingPointField.getText();
                        String snippetDurationInput = snippetDurationField.getText();
                        String outputVideoDurationInput = outputVideoDurationField.getText();
                        String outputVideoQuantityInput = outputVideoQuantityField.getText();
                        Boolean fastMode = modeDropdown.getSelectedIndex() == 0;

                        if (Integer.parseInt(outputVideoDurationInput) < 2) {
                            JOptionPane.showMessageDialog(jframe,
                                    "<html>Please provide an output video duration more than 1 second<br/></html>",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (true && Integer.parseInt(outputVideoDurationInput) > 60) {
                            JOptionPane.showMessageDialog(jframe,
                                    "<html>Free version is limited to 60-second videos only.<br/>"
                                    + "Full version can be purchased from <a href=\"https://ashtonhogan.gumroad.com\">ashtonhogan.gumroad.com</a></html>",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            Compilate compilate = new Compilate(
                                    new FFmpeg(ffmpegLocationInput),
                                    new FFprobe(ffprobeLocationInput),
                                    new InputVideos(Paths.get("").toAbsolutePath().resolve("InputVideos")),
                                    new OutputVideos(
                                            Paths.get("").toAbsolutePath().resolve("OutputVideos"),
                                            startingPointInput,
                                            Integer.valueOf(snippetDurationInput),
                                            Integer.valueOf(outputVideoDurationInput),
                                            Integer.valueOf(outputVideoQuantityInput)
                                    ),
                                    fastMode
                            );
                            compilate.execute();
                        }

                        return null;
                    }
                };

                // Execute the SwingWorker instance
                compileWorker.execute();
            }
        });
        inputPanel.add(runButton);

        // Encapsulate the inputPanel within a JScrollPane
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(150); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scroll bar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll bar
        scrollPane.setPreferredSize(panelDimensions); // Set preferred size
        scrollPane.setMinimumSize(panelDimensions); // Set minimum size to ensure it doesn't shrink

        add(scrollPane);

        runButton.requestFocusInWindow();
    }

    private Dimension determineLogoDimensions(Dimension panelDimensions, ImageIcon logoIcon) {
        // Get the width of the panel
        int panelWidth = (int) panelDimensions.getWidth();

        // Get the original dimensions of the logo
        int logoWidth = logoIcon.getIconWidth();
        int logoHeight = logoIcon.getIconHeight();

        // Calculate the aspect ratio of the logo
        double aspectRatio = (double) logoWidth / logoHeight;

        // Determine the height of the logo based on the panel width and the aspect ratio
        int scaledHeight = (int) (panelWidth / aspectRatio);

        // Return the new dimensions with the panel width and the calculated height
        return new Dimension(panelWidth, scaledHeight);
    }

}
