package compilate.ui;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends JFrame {

    public final LeftPanel leftPanel = new LeftPanel(this);
    public final RightPanel rightPanel = new RightPanel();

    public App(String title) {
        super(title);

        // Set default on close operation
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set layout for the main frame
        this.setLayout(new BorderLayout());

        // Add panels to the main frame
        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.CENTER);

        // Display the frame
        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);

        // Get the screen size and adjust for taskbar
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskbarHeight = screenInsets.bottom;

        // Adjust the height to account for the taskbar
        int adjustedHeight = screenSize.height - taskbarHeight;
        setSize(screenSize.width - 100, adjustedHeight - 100);

        // Center the frame
        setLocationRelativeTo(null);

        this.setResizable(false); // Make the frame non-resizable
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                App app = new App("Compilate");
                JOptionPane.showMessageDialog(null, "Press OK to exit.");
                System.exit(0); // Optional: Explicitly exit the application
            }
        });
    }
}
