package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.util.AppResource;
import com.sun.lwuit.Component;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BorderLayout;

/**
 * @author Puneet Agarwal
 */
public class FormProgressindicator extends FormHikeBase {

    private Image[] angles;
    private int angle, rotation = 0;
    private Label progress = new Label();
    
    
    /**
     * Constructor of progress form. This form is used to show progress when some task is running in the background.
     */
    public FormProgressindicator() {

        getStyle().setBgTransparency(255, true);

        progress.setGap(15);
        progress.setTextPosition(Component.BOTTOM);
        progress.getStyle().setAlignment(Component.CENTER);
        progress.getStyle().setBgTransparency(0, true);
        progress.getStyle().setFgColor(ColorCodes.progressInidicatorTextColor, true);
        progress.getStyle().setFont(Fonts.SMALL, true);
        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER, progress);
    }
    
    /**
     * This method is used to provide the angles to the image to rotate image on the screen.
     * @param frames 
     */
    //heavy call, greater the frames heavier the operation
    private void initAngles(int frames){
        angles = new Image[frames];
        Image image = AppResource.getImageFromResource(progressIndicator);
        for (int i = 0; i < frames; i++) {
            rotation = i * 10;
            angles[i] = image.rotate(rotation);
        }
    }

    
    /**
     * This method returns the progress text based on the current screen.
     * @return 
     */
    private String getProgressText() {
        int page = AppState.getForm();
        switch (page) {
            case DisplayStackManager.FORM_GET_STARTED:
                return MSG_AUTO_NUM_PULL;
            case DisplayStackManager.FORM_ENTER_NUMBER:
                return MSG_SEND_NUM_PROGRESS;
            case DisplayStackManager.FORM_ENTER_PIN:
                return MSG_SEND_PIN_PROGRESS;
            case DisplayStackManager.FORM_SET_NAME:
                return MSG_SEND_NAME_PROGRESS;
        }
        return EMPTY_STRING;
    }

    
    /**
     * This is a delegated method of a form. This will register the animation on this form.
     */
    public void initComponent() {
        super.initComponent();
        getComponentForm().registerAnimated(this);
    }

    
    /**
     * This is a delegated method of the form. This will de-register the animation on this screen.
     */
    protected void deinitialize() {
        super.deinitialize();
        getComponentForm().deregisterAnimated(this);
    }
    
    
    /**
     * This method is called to animate and setting the progress text on the screen.
     * @param animate 
     */
    public void show(boolean animate) {
        if(animate && angles == null) {
            initAngles(18);
        }
        super.show();
        progress.setText(getProgressText());
    }

    
    /**
     * This method has two params for animantion and setting text on the screen.
     * @param animate
     * @param text 
     */
    public void show(boolean animate, String text) {
        if(animate && angles == null) {
            initAngles(18);
        }
        super.show();
        progress.setText(text);
    }

    
    /**
     * This delegated method is called just after showing the form.
     */
    protected void onShow() {
    
    }

    
    /**
     * This method animates the image (Basically drawing the icon on screen with different angles).
     * @return 
     */
    public boolean animate() {
        if(angles != null) {
            progress.setIcon(angles[Math.abs(++angle % angles.length)]);
        }
        return true;
    }
}
