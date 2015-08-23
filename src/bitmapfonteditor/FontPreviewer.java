package bitmapfonteditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.*;
import org.jnode.awt.font.bdf.*;
import org.jnode.font.bdf.*;

/**
 *
 * @author l4l
 */
public class FontPreviewer extends javax.swing.JPanel implements AdjustmentListener, ChangeListener, ItemListener {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FontPreviewer.class);

    Color clrBackground = Color.WHITE;
    Color clrForeground = Color.BLACK;
    Color clrText = Color.BLACK;
    Color clrLines = Color.BLACK;
    Color clrBoxLines = Color.GRAY;
    Color clrAscent = Color.RED;
    Color clrDescent = Color.BLUE;
    Color clrBBox = Color.PINK;
    
    BDFFontContainer fontContainer = null;
    BDFFont font = null;
    
    FontMetrics fontMetrics;
    
    int codepointUpperLeft = 0;
    
    private int glyphHeight = 16; // pure glyph size without any decorations inside
    private int glyphWidth = 8;

    private int headerHeight = 15; // header size with decorations inside
    private int footerHeight = 5; // footer size with decorations inside
    
    private int headerWidth = 10; // header size with decorations inside

    private int maxColumnsVisible = 1;
    private int maxRowsVisible = 1;

    public FontPreviewer() {
        initComponents();
        initFont();
    }

    /** it's size of each "glyph box", including bounding lines on top and left */
    public int getBoxHeight() {
        return getGlyphHeight() + getFooterHeight() + getHeaderHeight();
    }
    
    /** it's size of each "glyph box", including bounding lines on top and left */
    public int getBoxWidth() {
        int boxWidth = getGlyphWidth() + 1;
        return boxWidth > headerWidth ? boxWidth : headerWidth;
    }    
    
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        repaint();
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        
        g.setColor(clrBackground);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        setMaxColumnsVisible((int) Math.floor((double) getWidth() / getBoxWidth()));
        setMaxRowsVisible((int) Math.floor((double) getHeight() / getBoxHeight()));
        
        int maxWidth = maxColumnsVisible * getBoxWidth();
        int maxHeight = maxRowsVisible * getBoxHeight();
        
        
        fontMetrics = g.getFontMetrics();
        
        setHeaderWidth(4 + fontMetrics.charWidth(0x01f1));
        setHeaderHeight(fontMetrics.getHeight() + 2);


        // gray dividers inside a box
        g.setColor(clrBoxLines);
        for (int j = 0; j < maxRowsVisible; j++) {
            int yHeader = j * getBoxHeight() + getHeaderHeight();
            g.drawLine(0, yHeader, maxWidth, yHeader);
        }

        // lines
        g.setColor(clrLines);
        // vertical lines
        for (int i = 0; i <= maxColumnsVisible; i++)
            g.drawLine(i * getBoxWidth(), 0, i * getBoxWidth(), maxHeight);

        // horisontal lines
        for (int j = 0; j <= maxRowsVisible; j++)
            g.drawLine(0, j * getBoxHeight(), maxWidth, j * getBoxHeight());
        
        for (int i = 0; i < maxColumnsVisible; i++)
            for (int j = 0; j < maxRowsVisible; j++)
                drawBox(g, i, j);

    }
    
    private void drawBox(Graphics2D g, int i, int j) {
        int iLeft = i * getBoxWidth() + 1; // coord after left line of the box
        int iTop = j * getBoxHeight() + 1; // coord after top line of the box
        
        g.setColor(clrText);
        char codepoint = (char)(codepointUpperLeft + i + j * getMaxColumnsVisible());
        g.drawString(
            Character.toString(codepoint), 
            iLeft + (getBoxWidth() - 1 - fontMetrics.charWidth(codepoint)) / 2f, 
            iTop + getHeaderHeight()  - fontMetrics.getDescent()
        );
        if (font.canDisplay(codepoint)) {
            BDFGlyph glyph = fontContainer.getGlyph(codepoint);
            log.error("" + 
                glyph.getData().length + " : " + 
                glyph.getName() + " = " + 
                glyph.getBbx().width + "x" + glyph.getBbx().height);
            
            g.setColor(clrBBox);
            g.drawRect(
                iLeft, 
                iTop + getHeaderHeight(),
                getGlyphWidth(),  //glyph.getBbx().width, 
                getGlyphHeight() //glyph.getBbx().height
            );

            g.setColor(clrForeground);
            for (int x = 0; x < glyph.getBbx().width; x++) {
                for (int y = 0; y < glyph.getBbx().height; y++)
                    if (1 == glyph.data[x + y * glyph.getBbx().width]) {
                        System.out.print("X");
                        /*
                        g.drawRect(
                            i * getBoxWidth() + x, 
                            j * getBoxHeight() + getHeaderHeight() + y, 
                            i * getBoxWidth() + x + 1, 
                            j * getBoxHeight() + getHeaderHeight() + y - 1
                        ); */
                    } else
                        System.out.print("_");
                System.out.println();
            }

        }
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private void initFont() {
        try {
            BDFParser parser = new BDFParser(new FileInputStream("/home/l4l/FixedsysMono-16.bdf"));
            fontContainer = parser.loadFont();
            font = new BDFFont(fontContainer);
        } catch (FileNotFoundException | ParseException ex) {
            log.error(ex);
        }
    }

    public int getFooterHeight() {
        return footerHeight;
    }

    public void setFooterHeight(int footerHeight) {
        this.footerHeight = footerHeight;
    }

    public int getGlyphHeight() {
        return glyphHeight;
    }

    public void setGlyphHeight(int glyphHeight) {
        this.glyphHeight = glyphHeight;
    }

    public int getGlyphWidth() {
        return glyphWidth;
    }

    public void setGlyphWidth(int glyphWidth) {
        this.glyphWidth = glyphWidth;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public int getMaxColumnsVisible() {
        return maxColumnsVisible;
    }

    public void setMaxColumnsVisible(int maxColumnsVisible) {
        this.maxColumnsVisible = maxColumnsVisible;
    }

    public int getMaxRowsVisible() {
        return maxRowsVisible;
    }

    public void setMaxRowsVisible(int maxRowsVisible) {
        this.maxRowsVisible = maxRowsVisible;
    }

    public int getHeaderWidth() {
        return headerWidth;
    }

    public void setHeaderWidth(int headerWidth) {
        this.headerWidth = headerWidth;
    }
}
