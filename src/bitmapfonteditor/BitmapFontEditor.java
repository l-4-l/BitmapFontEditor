package bitmapfonteditor;

/**
 *
 * @author l4l
 */
public class BitmapFontEditor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameMain frameMain = new FrameMain();
                frameMain.setVisible(true);
            }
        });
    }
    
}
