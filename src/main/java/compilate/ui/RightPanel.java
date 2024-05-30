package compilate.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class RightPanel extends JPanel {

    public static JTextArea status = new JTextArea();

    public RightPanel() {
        setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.75), Toolkit.getDefaultToolkit().getScreenSize().height));

        // Set panel size and layout
        setLayout(new BorderLayout());

        // Set background color
        setBackground(new Color(230, 230, 230)); // Stylish light gray

        // Output textarea
        status.setText("");
        // Set font size to 16 (adjust as needed)
        Font font = status.getFont();
        Font biggerFont = font.deriveFont(Font.PLAIN, 16); // Change 16 to your desired font size
        status.setFont(biggerFont);
        status.setWrapStyleWord(true);
        status.setLineWrap(true);
        status.setEditable(false); // Make it behave like a label
        status.setOpaque(false); // Make background transparent if needed
        JScrollPane outputScrollPane = new JScrollPane(status);
        outputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(outputScrollPane, BorderLayout.CENTER);
    }
}
