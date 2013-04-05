/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.helper;

import com.bsb.hike.ui.component.ChatBubbleTextArea;
import com.bsb.hike.ui.helper.SmileyManager.ContainsTuple;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.TextUtils;
import com.sun.lwuit.Component;
import com.sun.lwuit.Font;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.geom.Rectangle;
import com.sun.lwuit.plaf.Style;
import java.util.Vector;

/**
 *Custom look & feel class capable of rendering text & emoticons in TextArea & Label controls.
 * 
 * @author Sudheer Keshav Bhat
 */
//Reference: http://projects.developer.nokia.com/LWUIT_for_Series_40/browser/Ports/Nokia/S40/src/com/sun/lwuit/plaf/DefaultLookAndFeel.java
public class EmoticonLookAndFeel extends com.sun.lwuit.plaf.DefaultLookAndFeel {

    private static final String TAG = "EmoticonLookAndFeel";
    private int mTupleIdx;

    /**
     * @inheritDoc
     */
    public void drawTextArea(Graphics g, TextArea ta) {
        setFG(g, ta);

        int leftPadding = ta.getStyle().getPadding(ta.isRTL(), Component.LEFT);
        int rightPadding = ta.getStyle().getPadding(ta.isRTL(), Component.RIGHT);
        int topPadding = ta.getStyle().getPadding(false, Component.TOP);
        int bottomPadding = ta.getStyle().getPadding(false, Component.BOTTOM);

        int line = ta.getLines();
        int oX = g.getClipX();
        int oY = g.getClipY() + topPadding;
        int oWidth = g.getClipWidth();
        int oHeight = g.getClipHeight() - topPadding - bottomPadding;
        Font f = ta.getStyle().getFont();
        int fontHeight = f.getHeight();
        int align = reverseAlignForBidi(ta);


        boolean shouldBreak = false;
        
//        ChatBubbleTextArea cbTxtArea = (ChatBubbleTextArea) ta;
//        Vector/*<SmileyManager.ContainsTuple>*/ renderList = cbTxtArea.getRenderList();
//        //System.out.println(renderList);
//        if (renderList == null) {
//            renderList = SmileyManager.getInstance().getRenderList(ta.getText(), ta);
//            cbTxtArea.setRenderList(renderList);
//
////            int cols = ta.getColumns();
////            int rows = ta.getRows();
////            int reqRows = ((ContainsTuple) renderList.elementAt(renderList.size() - 1)).rowIndex + 1;
////            if (rows != reqRows) {
////                ta.setRows(reqRows);
////                ta.setColumns(cols);
////                ta.getParent().repaint();
////            }
//        }

        mTupleIdx = 0;

        int x = ta.getX() + leftPadding;
        for (int i = 0; i < line; i++) {
            int y = (ta.getY() - ta.getVisibleContentPosition()) + topPadding
                    + (ta.getRowsGap() + fontHeight) * i;
            if (Rectangle.intersects(x, y, ta.getWidth(), fontHeight, oX, oY, oWidth, oHeight)) {

                String rowText = (String) ta.getTextAt(i);
                //display ******** if it is a password field
                String displayText = "";
                if ((ta.getConstraint() & TextArea.PASSWORD) != 0) {
                    for (int j = 0; j < rowText.length(); j++) {
                        displayText += "*";
                    }
                } else {
                    displayText = rowText;
                }

                switch (align) {
                    case Component.RIGHT:
                        x = ta.getX() + ta.getWidth() - rightPadding - f.stringWidth(displayText);
                        break;
                    case Component.CENTER:
                        x += (ta.getWidth() - leftPadding - rightPadding - f.stringWidth(displayText)) / 2;
                        break;
                }
//                g.drawString(displayText, x, y, ta.getStyle().getTextDecoration());
//                drawStringWithSmileys(g, f, displayText, x, y, ta.getStyle().getTextDecoration(), renderList);
                shouldBreak = true;
            } else {
                if (shouldBreak) {
                    break;
                }
            }
        }
         Log.v(TAG, "text area height and width draw text area" + "Width: "+ ta.getWidth()+"height: "+ta.getHeight());
         drawStringWithSmileysX(g, f, x, ta.getStyle().getTextDecoration(), ((ChatBubbleTextArea)ta).getRenderList(), ta, topPadding, fontHeight);
//        drawStringWithSmileysX(g, f, x, ta.getStyle().getTextDecoration(), renderList, ta, topPadding, fontHeight);
//       renderList = null;
    }
    
    
    public void drawStringWithSmileysX(Graphics g, Font font, int x, int txtDecoration, Vector renderList, TextArea ta, int topPadding, int fontHeight) {
        int xPos = x;
        for (int i = 0; i < renderList.size(); i++) {
            ContainsTuple tuple = (ContainsTuple) renderList.elementAt(i);
            int y = (ta.getY() - ta.getVisibleContentPosition()) + topPadding
                    + (ta.getRowsGap() + fontHeight) * tuple.rowIndex;
            if (tuple.type == ContainsTuple.Type.SMILEY) {
                Image img = AppResource.getImageFromResource("/" + tuple.smileyImgPath);
                if (img != null) {
                    g.drawImage(img, xPos, y);
                    xPos += img.getWidth();
                }
                img = null;
            } else if (tuple.type == ContainsTuple.Type.TEXT) {
                g.drawString(tuple.string, xPos, y, txtDecoration);
                //TODO tune
                xPos += font.stringWidth(tuple.string);
            }
            if (i + 1 < renderList.size()) {
                ContainsTuple nxtTuple = (ContainsTuple) renderList.elementAt(i + 1);
                if (nxtTuple.rowIndex > tuple.rowIndex) {
                    xPos = x;
                }
                nxtTuple = null;
            }
            tuple = null;
        }
    }

    /**
     * Reverses alignment in the case of bidi
     */
    private int reverseAlignForBidi(Component c) {
        return reverseAlignForBidi(c, c.getStyle().getAlignment());
    }

    /**
     * Reverses alignment in the case of bidi
     */
    private int reverseAlignForBidi(Component c, int align) {
        if (c.isRTL()) {
            switch (align) {
                case Component.RIGHT:
                    return Component.LEFT;
                case Component.LEFT:
                    return Component.RIGHT;
            }
        }
        return align;
    }

    /**
     * Draws the text of a label
     * 
     * @param g graphics context
     * @param l label component
     * @param text the text for the label
     * @param x position for the label
     * @param y position for the label
     * @param textSpaceW the width available for the component
     * @return the space used by the drawing
     */
    protected int drawLabelText(Graphics g, Label l, String text, int x, int y, int textSpaceW) {
        Style style = l.getStyle();
        Font f = style.getFont();
        boolean rtl = l.isRTL();
        boolean isTickerRunning = l.isTickerRunning();
        int txtW = f.stringWidth(text);
        if ((!isTickerRunning) || rtl) {
            //if there is no space to draw the text add ... at the end
            if (txtW > textSpaceW && textSpaceW > 0) {
                // Handling of adding 3 points and in fact all text positioning when the text is bigger than
                // the allowed space is handled differently in RTL, this is due to the reverse algorithm
                // effects - i.e. when the text includes both Hebrew/Arabic and English/numbers then simply
                // trimming characters from the end of the text (as done with LTR) won't do.
                // Instead we simple reposition the text, and draw the 3 points, this is quite simple, but
                // the downside is that a part of a letter may be shown here as well.

                if (rtl) {
                    if ((!isTickerRunning) && (l.isEndsWith3Points())) {
                        String points = "...";
                        int pointsW = f.stringWidth(points);
//                        g.drawString(points, l.getShiftText() + x, y, l.getStyle().getTextDecoration());
                        drawEmoticonString(g, l.getStyle().getFont(), points, l.getShiftText() + x, y, l.getStyle().getTextDecoration());
                        g.clipRect(pointsW + l.getShiftText() + x, y, textSpaceW - pointsW, f.getHeight());
                    }
                    x = x - txtW + textSpaceW;
                } else {
                    if (l.isEndsWith3Points()) {
                        String points = "...";
                        int index = 1;
                        int widest = f.charWidth('W');
                        int pointsW = f.stringWidth(points);
                        while (fastCharWidthCheck(text, index, textSpaceW - pointsW, widest, f)) {
                            index++;
                        }
                        text = text.substring(0, Math.max(1, index - 1)) + points;
                        txtW = f.stringWidth(text);
                    }
                }
            }
        }

//        g.drawString(text, l.getShiftText() + x, y, style.getTextDecoration());
        drawEmoticonString(g, l.getStyle().getFont(), text, l.getShiftText() + x, y, style.getTextDecoration());
        return Math.min(txtW, textSpaceW);
    }

    private boolean fastCharWidthCheck(String s, int length, int width, int charWidth, Font f) {
        if (length * charWidth < width) {
            return true;
        }
        length = Math.min(s.length(), length);
        return f.substringWidth(s, 0, length) < width;
    }

    public void drawEmoticonString(Graphics g, Font font, String str, int x, int y, int txtDecoration) {
        if (!TextUtils.isEmpty(str)) {
            SmileyManager mgr = SmileyManager.getInstance();
//                     Log.v(TAG, "mgr:" + mgr);
            int startIdx = 0;
            int xPos = x;
            while (startIdx <= str.length()) {
                SmileyManager.ContainsTuple containsTuple = mgr.contains(str, startIdx);
//                         Log.v(TAG, "containsTuple:" + containsTuple);
                if (containsTuple == null || containsTuple.matchedIdx < 0) {
                    String textPart = str.substring(startIdx, str.length());
                    g.drawString(textPart, xPos, y, txtDecoration);
                    xPos += font.stringWidth(textPart);
                    break;
                } else if (containsTuple.matchedIdx - startIdx > 0) {
                    String textPart = str.substring(startIdx, containsTuple.matchedIdx);
                    g.drawString(textPart, xPos, y, txtDecoration);
                    xPos += font.stringWidth(textPart);
                }
                if (containsTuple != null) {
                    Image img = AppResource.getImageFromResource("/" + containsTuple.smileyImgPath);
                    if (img != null) {
                        g.drawImage(img, xPos, y);
                        xPos += img.getWidth();
                    }
                    startIdx = containsTuple.matchedIdx + containsTuple.string.length();
                }
            }
        }
    }

    public void drawStringWithSmileys(Graphics g, Font font, String str, int x, int y, int txtDecoration, Vector/*<SmileyManager.ContainsTuple>*/ renderList) {
        int xPos = x;
        int length = str.length();
        int renderedLength = 0;
        for (int i = mTupleIdx; i < renderList.size(); i++) {
            ContainsTuple tuple = (ContainsTuple) renderList.elementAt(i);
            if (tuple.type == ContainsTuple.Type.SMILEY) {
                Image img = AppResource.getImageFromResource("/" + tuple.smileyImgPath);
                if (img != null) {
                    g.drawImage(img, xPos, y);
                    xPos += img.getWidth();
                }
            } else if (tuple.type == ContainsTuple.Type.TEXT) {
                g.drawString(tuple.string, xPos, y, txtDecoration);
                //TODO tune
                xPos += font.stringWidth(tuple.string);
            }
            renderedLength += tuple.string.length();
            if (renderedLength >= length) {
                mTupleIdx = i + 1;
                break;
            }
        }
    }
    
    ///////////
//    private static final Image SCROLL_DOWN;
//    private static final Image SCROLL_UP;
//
//    static {
//        Image sd = null;
//        Image su = null;
//        try {
//            sd = Image.createImage("/scrollbar-button-south.png");
//            su = Image.createImage("/scrollbar-button-north.png");
//        } catch (IOException ioErr) {
//            ioErr.printStackTrace();
//        }
//        SCROLL_DOWN = sd;
//        SCROLL_UP = su;
//    }
//
//    private void drawScrollImpl(Graphics g, Component c, float offsetRatio, int blockSize, boolean vertical) {
//        int x = c.getX();
//        int y = c.getY();
//        int width, height, aX, aY, bX, bY;
//        width = SCROLL_UP.getWidth();
//        int margin = 0;
//        aX = x + c.getWidth() - width - margin;
//        x = aX;
//        bX = aX;
//        aY = y + margin;
//        bY = y + c.getHeight() - margin - SCROLL_UP.getHeight();
//        y = aY + SCROLL_UP.getHeight();
//        height = c.getHeight() - SCROLL_UP.getHeight() * 2;
//        g.setColor(0xffffff);
//        g.fillRect(x, y, width, height);
//        g.drawImage(SCROLL_UP, aX, aY);
//        g.drawImage(SCROLL_DOWN, bX, bY);
//
//        g.setColor(0xcccccc);
//        g.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10);
//        g.setColor(0x333333);
//        int offset = (int) (height * offsetRatio);
//        g.fillRoundRect(x + 2, y + 2 + offset, width - 4, height / 3, 10, 10);
//    }
//
//    /**
//     * Draws a vertical scoll bar in the given component
//     */
//    public void drawVerticalScroll(Graphics g, Component c, float offsetRatio, float blockSizeRatio) {
//        int blockSize = (int) (c.getHeight() * blockSizeRatio);
//        drawScrollImpl(g, c, offsetRatio, blockSize, true);
//    }
//
//    /**
//     * Scrollbar is drawn on top of existing widgets
//     */
//    public int getVerticalScrollWidth() {
//        return SCROLL_UP.getWidth();
//    }
//
//    /**
//     * Scrollbar is drawn on top of existing widgets
//     */
//    public int getHorizontalScrollHeight() {
//        return 0;
//    }
}
