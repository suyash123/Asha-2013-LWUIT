/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui.helper;

import com.bsb.hike.ui.component.ChatBubbleTextArea;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.Log;
import com.sun.lwuit.Component;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Image;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.plaf.Style;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Sudheer Keshav Bhat
 */
public class SmileyManager {

    private static final String TAG = "SmileyManager";
    //Changed Smiley Mapping for Idea.png (     !:-)    to    (Idea)     )
    //Changed Smiley Mapping for SweetAngel.png (     O:-)    to    (SA)     )
    public String smileys[][] = {{":))", "/smileys/yellow/BigSmile.png"},
        {":-)", "/smileys/yellow/Happy.png"},
        {":-D", "/smileys/yellow/Laugh.png"},
        {"=)", "/smileys/yellow/Smile.png"},
        {";)", "/smileys/yellow/Wink.png"},
        {":-X", "/smileys/yellow/Adore.png"},
        {":-*", "/smileys/yellow/Kiss.png"}, {"(kissed)", "/smileys/yellow/Kissed.png"}, {":-|", "/smileys/yellow/Expressionless.png"}, {":\")", "/smileys/yellow/Pudently.png"}, {"^.^", "/smileys/yellow/Satisfied.png"}, {"(giggle)", "/smileys/yellow/Giggle.png"},
        {":-P", "/smileys/yellow/Impish.png"}, {"=\\", "/smileys/yellow/Disappointment.png"}, {";-)", "/smileys/yellow/BeUpToNoGood.png"}, {"X[", "/smileys/yellow/Frustrated.png"}, {":-(", "/smileys/yellow/Sad.png"}, {":?-(", "/smileys/yellow/Sorry.png"},
        {":\'-(", "/smileys/yellow/Cry.png"}, {"l-o", "/smileys/yellow/Boring.png"}, {":-0", "/smileys/yellow/Hungry.png"}, {"(scared)", "/smileys/yellow/Scared.png"}, {"o_o", "/smileys/yellow/Shock.png"}, {"(sweat)", "/smileys/yellow/Sweat.png"},
        {"T_T", "/smileys/yellow/Crying.png"}, {":D", "/smileys/yellow/LOL.png"}, {":o", "/smileys/yellow/Woo.png"}, {":-O", "/smileys/yellow/Surprise.png"}, {":-<", "/smileys/yellow/Frown.png"}, {"X(", "/smileys/yellow/Angry.png"}, {"(wornout)", "/smileys/yellow/WornOut.png"},
        {"(stop)", "/smileys/yellow/Stop.png"}, {"X-(", "/smileys/yellow/Furious.png"}, {"(smoking)", "/smileys/yellow/Smoking.png"}, {"XD", "/smileys/yellow/Hysterical.png"}, {":@", "/smileys/yellow/Exclamation.png"}, {":-Q", "/smileys/yellow/Question.png"},
        {"u_u", "/smileys/yellow/Sleep.png"}, {":-Z", "/smileys/yellow/Aggressive.png"}, {":-=", "/smileys/yellow/Badly.png"}, {"(^o^)", "/smileys/yellow/Singing.png"}, {"(Bomb)", "/smileys/yellow/Bomb.png"}, {"b-(", "/smileys/yellow/Beaten.png"}, {":-q", "/smileys/yellow/ThumbsDown.png"},
        {":-b", "/smileys/yellow/ThumbsUp.png"}, {"(beer)", "/smileys/yellow/Beer.png"}, {":-c", "/smileys/yellow/Call.png"}, {"(hi)", "/smileys/yellow/Hi.png"}, {"(hug)", "/smileys/yellow/Hug.png"}, {"(face palm)", "/smileys/yellow/Facepalm.png"}, {"$-)", "/smileys/yellow/EasyMoney.png"},
        {"%-}", "/smileys/yellow/Dizzy.png"}, {"DX", "/smileys/yellow/Disgust.png"}, {"(/)", "/smileys/yellow/Cocktail.png"}, {"(coffee)", "/smileys/yellow/Coffee.png"}, {":-`|", "/smileys/yellow/Cold.png"}, {"B-)", "/smileys/yellow/Cool.png"}, {":-E", "/smileys/yellow/Despair.png"},
        {"(@-))", "/smileys/yellow/Hypnotic.png"}, {"%-)", "/smileys/yellow/Stars.png"}, {"(Idea)", "/smileys/yellow/Idea.png"}, {"(monocle)", "/smileys/yellow/Monocle.png"}, {"(movie)", "/smileys/yellow/Movie.png"}, {"(music)", "/smileys/yellow/Music.png"}, {":-B", "/smileys/yellow/Nerd.png"}, {"(ninja)", "/smileys/yellow/Ninja.png"}, {"<:--)", "/smileys/yellow/Party.png"}, {"P-(", "/smileys/yellow/Pirate.png"}, {":-@", "/smileys/yellow/Rage.png"}, {"(@>---)", "/smileys/yellow/Rose.png"}, {":-s", "/smileys/yellow/Sick.png"}, {"(snotty)", "/smileys/yellow/Snotty.png"}, {"-.-", "/smileys/yellow/Stressed.png"}, {"(struggle)", "/smileys/yellow/Struggle.png"}, {"(study)", "/smileys/yellow/Study.png"}, {"(SWA)", "/smileys/yellow/SweetAngel.png"}, {"*-)", "/smileys/yellow/Thinking.png"}, {":-w", "/smileys/yellow/Waiting.png"}, {":-\"", "/smileys/yellow/Whistling.png"}, {"(yawn)", "/smileys/yellow/Yawn.png"},
        //
        {"(exciting1)", "/smileys/popo_the_blacy/81_exciting.png"}, {"(big smile1)", "/smileys/popo_the_blacy/82_big_smile.png"}, {"(haha1)", "/smileys/popo_the_blacy/83_haha.png"}, {"(victory1)", "/smileys/popo_the_blacy/84_victory.png"}, {"(red heart1)", "/smileys/popo_the_blacy/85_red_heart.png"}, {"(amazing1)", "/smileys/popo_the_blacy/86_amazing.png"}, {"(black heart1)", "/smileys/popo_the_blacy/87_black_heart.png"}, {"(what1)", "/smileys/popo_the_blacy/88_what.png"}, {"(bad smile1)", "/smileys/popo_the_blacy/89_bad_smile.png"}, {"(bad egg1)", "/smileys/popo_the_blacy/90_bad_egg.png"}, {"(grimace1)", "/smileys/popo_the_blacy/91_grimace.png"}, {"(girl1)", "/smileys/popo_the_blacy/92_girl.png"}, {"(greedy1)", "/smileys/popo_the_blacy/93_greedy.png"}, {"(anger1)", "/smileys/popo_the_blacy/94_anger.png"}, {"(eyes droped1)", "/smileys/popo_the_blacy/95_eyes_droped.png"}, {"(happy1)", "/smileys/popo_the_blacy/96_happy.png"}, {"(horror1)", "/smileys/popo_the_blacy/97_horror.png"}, {"(money1)", "/smileys/popo_the_blacy/98_money.png"}, {"(nothing1)", "/smileys/popo_the_blacy/99_nothing.png"}, {"(nothing to say1)", "/smileys/popo_the_blacy/100_nothing_to_say.png"}, {"(cry1)", "/smileys/popo_the_blacy/101_cry.png"}, {"(scorn1)", "/smileys/popo_the_blacy/102_scorn.png"}, {"(secret smile1)", "/smileys/popo_the_blacy/103_secret_smile.png"}, {"(shame1)", "/smileys/popo_the_blacy/104_shame.png"}, {"(shocked1)", "/smileys/popo_the_blacy/105_shocked.png"}, {"(super man1)", "/smileys/popo_the_blacy/106_super_man.png"}, {"(iron man1)", "/smileys/popo_the_blacy/107_the_iron_man.png"}, {"(unhappy1)", "/smileys/popo_the_blacy/108_unhappy.png"}, {"(electric shock1)", "/smileys/popo_the_blacy/109_electric_shock.png"}, {"(beaten1)", "/smileys/popo_the_blacy/110_beaten.png"},
        //
        {"(grin2)", "/smileys/yolks/111_grin.png"}, {"(happy2)", "/smileys/yolks/112_happy.png"}, {"(fake smile2)", "/smileys/yolks/113_fake_smile.png"}, {"(in love2)", "/smileys/yolks/114_in_love.png"},
        {"(kiss2)", "/smileys/yolks/115_kiss.png"}, {"(straight face2)", "/smileys/yolks/116_straight_face.png"}, {"(meow2)", "/smileys/yolks/117_meaw.png"}, {"(drunk2)", "/smileys/yolks/118_drunk.png"},
        {"(x_x2)", "/smileys/yolks/119_x_x.png"}, {"(kidding right2)", "/smileys/yolks/120_youre_kidding_right.png"}, {"(sweat2)", "/smileys/yolks/122_sweat.png"},
        {"(nerd2)", "/smileys/yolks/123_nerd.png"}, {"(very angry2)", "/smileys/yolks/124_angry.png"}, {"(disappearing2)", "/smileys/yolks/125_disappearing.png"}, {"(dizzy2)", "/smileys/yolks/126_dizzy.png"},
        {"(music2)", "/smileys/yolks/127_music.png"}, {"(evilish)", "/smileys/yolks/128_evilish.png"}, {"(graffiti)", "/smileys/yolks/129_graffiti.png"}, {"(omg2)", "/smileys/yolks/130_omg.png"},
        {"(on fire2)", "/smileys/yolks/131_on_fire.png"}, {"(ouch2)", "/smileys/yolks/132_ouch.png"}, {"(angry2)", "/smileys/yolks/133_angry.png"}, {"(business2)", "/smileys/yolks/134_serious_business.png"},
        {"(sick2)", "/smileys/yolks/135_sick.png"}, {"(slow2)", "/smileys/yolks/136_slow.png"}, {"(snooty2)", "/smileys/yolks/137_snooty.png"}, {"(suspicious2)", "/smileys/yolks/138_suspicious.png"},
        {"(crying2)", "/smileys/yolks/139_crying.png"}, {"(want2)", "/smileys/yolks/140_want.png"}, {"(gonna die2)", "/smileys/yolks/141_we_all_gonna_die.png"}, {"(wut2)", "/smileys/yolks/142_wut.png"},
        {"(boo2)", "/smileys/yolks/143_boo.png"}, {"(xd2)", "/smileys/yolks/144_XD.png"}, {"(kaboom2)", "/smileys/yolks/145_kaboom.png"}, {"(yarr2)", "/smileys/yolks/146_yarr.png"},
        {"(ninja2)", "/smileys/yolks/147_ninja.png"}, {"(yuush2)", "/smileys/yolks/148_yuush.png"}, {"(brains2)", "/smileys/yolks/149_brains.png"}, {"(sleeping2)", "/smileys/yolks/150_sleeping.png"}};
    private Hashtable mStringToImgNames = new Hashtable(smileys.length);
    private Hashtable mImgNamesToString = new Hashtable(smileys.length);

    private SmileyManager() {
                 
        Log.v(TAG, "<init>");
        for (int i = 0; i < smileys.length; i++) {
            String[] smiley = smileys[i];
            mStringToImgNames.put(smiley[0], smiley[1]);
            mImgNamesToString.put(smiley[1], smiley[0]);
        }
    }
    private static SmileyManager sInstance = null;

    public static SmileyManager getInstance() {
        if (sInstance == null) {
            sInstance = new SmileyManager();
        }
        return sInstance;
    }



    public void getRenderList(String text, ChatBubbleTextArea txtArea) {

        int startIdx = 0;
        Vector tuples = new Vector();
        while (startIdx < text.length()) {
            int index = -1;
            boolean found = false;
            int endIndex = 0;
            while (endIndex < text.length()) {
                found = false;
                String searchText = text.substring(0, ++endIndex);
                for (int i = 0; i < smileys.length; i++) {
                    String smileyText = smileys[i][0];
                    index = searchText.indexOf(smileyText, startIdx);
                    if (index >= 0) {
                        found = true;
                        String smileyPath = smileys[i][1];
                        if (index > startIdx) {
                             Log.v(TAG, "Text found");
                            tuples.addElement(new ContainsTuple(
                                    ContainsTuple.Type.TEXT, startIdx, text.substring(startIdx, index),
                                    null));
                        }
                        Log.v(TAG, "Smiley found");
                        tuples.addElement(new ContainsTuple(
                                ContainsTuple.Type.SMILEY, index,
                                smileyText, smileyPath));
                        startIdx = index + smileyText.length();
                        smileyPath = null;
                    }
                    smileyText = null;
                }
                searchText = null;
            }

            if (!found) {
                Log.v(TAG, "No smiley found");
                tuples.addElement(new ContainsTuple(
                        ContainsTuple.Type.TEXT, startIdx, text.substring(
                        startIdx, text.length()), null));
                break;
            }
        }
        Log.v(TAG, "tuple size"+ tuples.size());
        Vector tuplesWithNewLine = new Vector();
        Enumeration enumeration = tuples.elements();
        while (enumeration.hasMoreElements()) {
            ContainsTuple tupleObj = (ContainsTuple) enumeration.nextElement();
            if (tupleObj.type == ContainsTuple.Type.TEXT) {
                Log.v(TAG, "Split tuple");
                split(tuplesWithNewLine, tupleObj.string);
            } else {
                Log.v(TAG, "tuplesWithNewLine");
                tuplesWithNewLine.addElement(tupleObj);
            }
        }
        
        refactorForNewLinesX(tuplesWithNewLine, txtArea);
         for (int i = 0; i < tuplesWithNewLine.size(); i++) {
             ContainsTuple tuple = (ContainsTuple) tuplesWithNewLine.elementAt(i);
             if (tuple.string.equals("\n")) {
                tuplesWithNewLine.removeElementAt(i);
            }
        }
        Log.v(TAG, "tuplesWithNewLine size after filtering"+ tuplesWithNewLine.size());
        txtArea.mRenderList = tuplesWithNewLine;
    }

    private void split(Vector tupleWithNewLine, String original) {
        String separator = "\n";
        int index = original.indexOf(separator);
        while (index >= 0) {
             Log.v(TAG, "Split tuple index:-"+index+"Splited String"+original.substring(0, index));
            tupleWithNewLine.addElement(new ContainsTuple(ContainsTuple.Type.TEXT, index, original.substring(0, index), null));
            tupleWithNewLine.addElement(new ContainsTuple(ContainsTuple.Type.TEXT, index + 1, separator, null));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }

        Log.v(TAG, "Split tuple index:-"+index+"String"+original);
        tupleWithNewLine.addElement(new ContainsTuple(ContainsTuple.Type.TEXT, index, original, null));
    }
    
    
    private void refactorForNewLinesX(Vector tuples, TextArea txtArea) {
        Style style = txtArea.getStyle();
        int leftPadding = style.getPadding(txtArea.isRTL(), Component.LEFT);
        int rightPadding = style.getPadding(txtArea.isRTL(), Component.RIGHT);
        Log.v(TAG, "leftPadding:" + leftPadding);
        Log.v(TAG, "rightPadding:" + rightPadding);
       // int rowWidth = txtArea.getWidth() - leftPadding - rightPadding;
        int rowWidth = Display.getInstance().getDisplayWidth() - leftPadding - rightPadding - 60;
        int cumTextWidth = 0;
        int txtBubbleWidth = 0;
        int txtBubbleHeight = 18;
        Font font = style.getFont();
        style = null;
        int rowIndex = 0;
                 
        Log.v(TAG, "rowWidth:" + rowWidth);
         Log.v(TAG, "text area height and widht:" + "Width: "+ txtArea.getWidth()+"height: "+txtArea.getHeight());
        
        for (int i = 0; i < tuples.size(); i++) {
            ContainsTuple tuple = (ContainsTuple) tuples.elementAt(i);
            ContainsTuple nextTuple =  null;
             if (i != tuples.size()-1) {
                 nextTuple = (ContainsTuple) tuples.elementAt(i+1);
             }
              if (txtBubbleHeight < font.getHeight()) {
                 txtBubbleHeight = font.getHeight();
            }
            if (tuple.type == ContainsTuple.Type.TEXT) {
                if(tuple.string.equals("\n")) {
                    ++rowIndex;
                    continue;
                }
                int textLen = tuple.string.length();
                int textWidth = font.stringWidth(tuple.string);
                Log.v(TAG, "textLen:" + textLen);
                Log.v(TAG, "textWidth:" + textWidth);
                if ((cumTextWidth + textWidth) > rowWidth) {
                    Log.v(TAG, "first text width is greater then rowWidth:"+"rowindex: "+rowIndex+"cumtextWidth: "+cumTextWidth);
                    tuples.removeElementAt(i);
                    int textIdx = 0;
                    StringBuffer buffer = new StringBuffer();
                    int j = 0;
                    while (textIdx < textLen) {
                        char chr = tuple.string.charAt(textIdx);
                        int charWidth = font.charWidth(chr);
                        if (((cumTextWidth + charWidth) > rowWidth) || textIdx + 1 == textLen) {
                            Log.v(TAG, "second text width is greater then rowWidth:"+"rowindex: "+rowIndex+"cumtextWidth: "+cumTextWidth+"charWidth: "+ charWidth);
                            buffer.append(chr);
                            tuples.insertElementAt(
                                    new ContainsTuple(ContainsTuple.Type.TEXT, buffer.toString(), rowIndex), i + j);
                            if ((cumTextWidth + charWidth) > rowWidth) {
                                 Log.v(TAG, "third text width is greater then rowWidth:"+"rowindex: "+rowIndex+"cumtextWidth: "+cumTextWidth+"charWidth: "+ charWidth);
                                if (textIdx + 1 < textLen) {
                                     Log.v(TAG, "fourth text width is greater then rowWidth:"+"rowindex: "+rowIndex+"cumtextWidth: "+cumTextWidth+"charWidth: "+ charWidth);
                                    buffer = new StringBuffer();
                                    ++j;
                                }
                                ++rowIndex;
                                if (txtBubbleWidth < cumTextWidth) {
                                    txtBubbleWidth = cumTextWidth;
                                }
                                cumTextWidth = 0;
                            }
                        } else {
                            buffer.append(chr);
                            cumTextWidth += charWidth;
                            if (txtBubbleWidth < cumTextWidth) {
                                 txtBubbleWidth = cumTextWidth;
                             }
                             Log.v(TAG, "fifth text width is greater then rowWidth:"+"rowindex: "+rowIndex+"cumtextWidth: "+cumTextWidth+"charWidth: "+ charWidth);
                        }
                        ++textIdx;
                    }
                    i += j;
                    buffer = null;
                } else {
                    tuple.rowIndex = rowIndex;
                    if (nextTuple != null) {
                        if (nextTuple.type == ContainsTuple.Type.TEXT) {
                            if (txtBubbleWidth < textWidth) {
                                 txtBubbleWidth = textWidth;
                             }
                            cumTextWidth = 0;
                        }
                        else{
                            cumTextWidth = cumTextWidth + textWidth;
                            if (txtBubbleWidth < cumTextWidth) {
                                 txtBubbleWidth = cumTextWidth;
                             }
                        } 
                    }else{
                         cumTextWidth = cumTextWidth + textWidth;
                            if (txtBubbleWidth < cumTextWidth) {
                                 txtBubbleWidth = cumTextWidth;
                             }
                    }
                    Log.v(TAG, "text width not greater then rowWidth:"+"rowindex"+rowIndex+"cumtextWidth"+cumTextWidth);
                }
            } else if (tuple.type == ContainsTuple.Type.SMILEY) {
                Image img = AppResource.getImageFromResource("/" + tuple.smileyImgPath);
                if (img != null) {
                    
                    int currentImgWidth = img.getWidth();
                    if (txtBubbleHeight < img.getHeight()) {
                        txtBubbleHeight = img.getHeight();
                    }
                    Log.v(TAG, "currentImgWidth:-"+currentImgWidth);
                    if ((cumTextWidth + currentImgWidth) > rowWidth) {
                        tuple.rowIndex = ++rowIndex;
                        if (txtBubbleWidth < cumTextWidth) {
                            txtBubbleWidth = cumTextWidth;
                        }
                        cumTextWidth = 0;
                        Log.v(TAG, " smiley text width is greater then rowWidth:"+"rowindex"+rowIndex+"cumtextWidth"+cumTextWidth);
                    } else {
                        tuple.rowIndex = rowIndex;
                        cumTextWidth += img.getWidth();
                        if (txtBubbleWidth < cumTextWidth) {
                            txtBubbleWidth = cumTextWidth;
                        }
                        Log.v(TAG, "smiley text width not greater then rowWidth:"+"rowindex"+rowIndex+"cumtextWidth"+cumTextWidth);
                    }
                }
                img = null;
            }
            tuple = null;
        }
        font = null;
        ChatBubbleTextArea cb = (ChatBubbleTextArea) txtArea;
        cb.mHeight = txtBubbleHeight;
        cb.mWidth = txtBubbleWidth;
        cb.mRows = rowIndex;
        Log.v(TAG, "text bubble width:" + "Width: "+ txtBubbleWidth);
        Log.v(TAG, "text bubble height:" + "height: "+ txtBubbleHeight);
    }

    public static class ContainsTuple {

        public static interface Type {

            byte TEXT = 1;
            byte SMILEY = 2;
        }
        public int matchedIdx;
        public String string;
        public String smileyImgPath;
        public byte/*<Type>*/ type;
        public int endIndex;
        public int rowIndex;

        public ContainsTuple(byte type, int matchedIdx,
                String string,
                String smileyImgPath) {
            this.matchedIdx = matchedIdx;
            this.smileyImgPath = smileyImgPath;
            this.string = string;
            this.type = type;
            this.endIndex = matchedIdx + string.length() - 1;
        }

        public ContainsTuple(byte type,
                String string,
                int rowIndex) {
            this.string = string;
            this.type = type;
            this.rowIndex = rowIndex;
        }

        public String toString() {
            return "ContainsTuple{rowIndex:" + rowIndex + ", smileyImgPath:" + smileyImgPath + ", string:" + string + "}";
        }
    }
    
    public ContainsTuple contains(String text, int startIdx) {
        for (int i = 0; i < smileys.length; i++) {
            int index = text.indexOf(smileys[i][0], startIdx);
            if (index >= 0) {

            }
        }
        return null;
    }
    

//    private void refactorForNewLines(Vector tuples, TextArea txtArea) {
//        Style style = txtArea.getStyle();
//        int leftPadding = style.getPadding(txtArea.isRTL(), Component.LEFT);
//        int rightPadding = style.getPadding(txtArea.isRTL(), Component.RIGHT);
//        int rowWidth = txtArea.getWidth() - leftPadding - rightPadding;
//        int cumTextLen = 0;
//        Font font = style.getFont();
//        int oneCharWidth = font.charWidth('w');
//        int noOfCharsPerRow = rowWidth / oneCharWidth;
//        int rowIndex = 0;
//                
//        Log.v(TAG, "rowWidth:" + rowWidth + ", oneCharWidth:" + oneCharWidth + ", noOfCharsPerRow:" + noOfCharsPerRow);
//        for (int i = 0; i < tuples.size(); i++) {
//            ContainsTuple tuple = (ContainsTuple) tuples.elementAt(i);
//            if (tuple.type == ContainsTuple.Type.TEXT) {
//                int textLen = tuple.string.length();
//
//                if ((cumTextLen + textLen) > noOfCharsPerRow) {
//                    int currentTxtLen = (noOfCharsPerRow - cumTextLen);
//                    int pendingLen = textLen - currentTxtLen;
//                    int noOfSubTextTuples = 1;
//                    if (pendingLen < noOfCharsPerRow) {
//                        noOfSubTextTuples++;
//                    } else {
//                        int modulo = pendingLen % noOfCharsPerRow;
//                        int divisor = pendingLen / noOfCharsPerRow;
//                        noOfSubTextTuples += (modulo == 0) ? divisor : divisor + 1;
//                    }
//                    tuples.removeElementAt(i);
//                    int startIdxCur = 0;
//                    for (int j = 0; j < noOfSubTextTuples; j++) {
//                        String subText = null;
//                        ContainsTuple subTuple = null;
//                        if (j == 0) {
//                            subText = tuple.string.substring(0, currentTxtLen);
//                            subTuple = new ContainsTuple(ContainsTuple.Type.TEXT, -1, subText, null);
//                            subTuple.rowIndex = rowIndex++;
//                            cumTextLen = 0;
//                            startIdxCur = currentTxtLen;
//                        } else {
//                            int endIdx = startIdxCur + noOfCharsPerRow;
//                            if (endIdx > textLen) {
//                                endIdx = textLen;
//                            }
//                            subText = tuple.string.substring(startIdxCur, endIdx);
//                            cumTextLen += subText.length();
//                            startIdxCur = endIdx;
//                            subTuple = new ContainsTuple(ContainsTuple.Type.TEXT, -1, subText, null);
//                            subTuple.rowIndex = rowIndex;
//                            if (cumTextLen >= noOfCharsPerRow) {
//                                tuple.rowIndex = ++rowIndex;
//                                cumTextLen = 0;
//                            }
//                        }
//                        tuples.insertElementAt(subTuple, i + j);
//                    }
//                    i += noOfSubTextTuples - 1;
//                } else {
//                    tuple.rowIndex = rowIndex;
//                    cumTextLen += textLen;
//                }
//            } else if (tuple.type == ContainsTuple.Type.SMILEY) {
//                Image img = AppResource.getImageFromResource("/" + tuple.smileyImgPath);
//                if (img != null) {
//                    int currentImgLen = img.getWidth() / oneCharWidth;
//                    if (cumTextLen + currentImgLen > noOfCharsPerRow) {
//                        tuple.rowIndex = ++rowIndex;
//                        cumTextLen = 0;
//                    } else {
//                        tuple.rowIndex = rowIndex;
//                        cumTextLen += currentImgLen;
//                    }
//                }
//            }
//        }
//    }

    
}
