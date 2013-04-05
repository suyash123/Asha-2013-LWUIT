package com.bsb.hike.ui;

import com.bsb.hike.dto.AppState;
import com.bsb.hike.util.AppConstants;
import com.bsb.hike.util.AppResource;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.util.Vector;


/**
 * 
 * @author Ranjith Yadav
 * @author Puneet Agarwal
 */
public class FormNotification extends FormHikeBase {

    private Command exitCommand;
    private Vector notificationsVector = new Vector(2);

    
    /**
     * Constructor of Notification form. This form provides user to customize vibration and sound settings.
     */
    public FormNotification() {
        
        getStyle().setBgColor(ColorCodes.settingsScreenBgGrey, true);
        
        //#if nokia2_0
//#         setTitle(LBL_NOTIFICATIONS);
        //#endif
        
        notificationsVector.addElement(new NotificationModel(LBL_FEEL_THE_VIBES, MSG_FEEL_THE_VIBES, AppState.isVibrationOn()));
        notificationsVector.addElement(new NotificationModel(LBL_HEAR_SOUNDS, MSG_HEAR_SOUNDS, AppState.isVolumeOn()));
        
        initCommands();
        
        //#if nokia1_1
//#         Label notificationsLabel = new Label(LBL_NOTIFICATIONS);
//#         notificationsLabel.getStyle().setBgTransparency(0, true);
//#         notificationsLabel.getStyle().setMargin(Component.TOP, 5, true);
//#         addComponent(notificationsLabel);
        //#endif
        
        setScrollable(false);
        setScrollVisible(false);
       
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        final List notificationsList = new List(notificationsVector);
        notificationsList.getStyle().setPadding(Component.LEFT, 0, true);
        notificationsList.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        notificationsList.getStyle().setPadding(Component.RIGHT, 0, true);
        notificationsList.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        notificationsList.getStyle().setPadding(Component.BOTTOM, 0, true);
        notificationsList.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        notificationsList.getStyle().setBgColor(ColorCodes.settingsScreenListBgGrey, true);
        notificationsList.getSelectedStyle().setBgColor(ColorCodes.settingsScreenListBgGrey, true);
        notificationsList.getStyle().setBgTransparency(255, true);
        notificationsList.getSelectedStyle().setBgTransparency(255, true);
        notificationsList.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.settingsScreenBorderGrey, ColorCodes.settingsScreenBorderShadow) , null, null), true);
        notificationsList.getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.settingsScreenBorderGrey, ColorCodes.settingsScreenBorderShadow) , null, null), true);
        notificationsList.setRenderer(new NotificationListRenderer());
        notificationsList.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                switch(notificationsList.getSelectedIndex()){
                    case 0:
                        AppState.setVibrationOn(!AppState.isVibrationOn());
                        ((NotificationModel)notificationsVector.elementAt(0)).setSelected(AppState.isVibrationOn());
                        break;
                    case 1:
                        AppState.setVolumeOn(!AppState.isVolumeOn());
                        ((NotificationModel)notificationsVector.elementAt(1)).setSelected(AppState.isVolumeOn());
                        break;
                }
            }
        }); 
        
        addComponent(notificationsList);
    }
    
    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {
        
        exitCommand = new Command(LBL_BACK) {

            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);
                DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
            }
        };
        
        addCommand(exitCommand);
        setBackCommand(exitCommand);
    }
    
    
    
    /**
     * Notification List Renderer renders the notifications list on the screen.
     */
    private class NotificationListRenderer extends Container implements ListCellRenderer {
        
        Image checkedStateImage, uncheckedStateImage;
        Label checkBox;
        Container mainContainer;
        
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            
            NotificationModel cell = (NotificationModel) value;
            
            checkedStateImage = AppResource.getImageFromResource(AppConstants.PATH_NOTIFICATION_CHECKBOX_ENABLED);
            uncheckedStateImage = AppResource.getImageFromResource(AppConstants.PATH_NOTIFICATION_CHECKBOX_DISABLED);

            setLayout(new BorderLayout());
            
            Label name = new Label();
            name.getStyle().setPadding(Component.TOP, 10, true);
            name.getStyle().setPadding(Component.LEFT, 5, true);
            name.setText(cell.getTitle());
            name.getStyle().setFont(Fonts.MEDIUM, true);
            name.getStyle().setFgColor(ColorCodes.settingsScreenListItemHeadingGrey, true);
            name.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
            name.getStyle().setBgTransparency(0, true);
            
            TextArea description = new TextArea();
            description.setText(cell.getSubTitle());
            description.getStyle().setFgColor(ColorCodes.settingsScreenListItemDescGrey);
            description.getSelectedStyle().setFgColor(ColorCodes.settingsScreenListItemDescGrey);
            description.getStyle().setBgColor(ColorCodes.settingsScreenListBgGrey, true);
            description.getSelectedStyle().setBgColor(ColorCodes.settingsScreenListBgGrey, true);
            description.setEditable(false);
            description.setRows(2);
            description.setPreferredW(Display.getInstance().getDisplayWidth() - 30);
            description.getSelectedStyle().setBgTransparency(255, true);
            description.getStyle().setBgTransparency(255, true);
            description.getStyle().setBorder(Border.createEmpty(), true);
            description.getSelectedStyle().setBorder(Border.createEmpty(), true);
            description.getStyle().setPadding(Component.LEFT, 5, true);
            description.getSelectedStyle().setPadding(Component.LEFT, 5, true);
            description.getStyle().setPadding(Component.TOP, 0, true);
            description.getSelectedStyle().setPadding(Component.TOP, 0, true);
            description.getStyle().setPadding(Component.BOTTOM, 0, true);
            description.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
            description.getSelectedStyle().setFont(Fonts.SMALL, true);
            description.getStyle().setFont(Fonts.SMALL, true);

            Container nameDescContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            nameDescContainer.getStyle().setPadding(Component.RIGHT, 0, true);
            nameDescContainer.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
            nameDescContainer.getStyle().setBorder(Border.createEmpty(), true);
            nameDescContainer.getSelectedStyle().setBorder(Border.createEmpty(), true);
            nameDescContainer.addComponent(name);
            nameDescContainer.addComponent(description);   

            checkBox = new Label();
            checkBox.getStyle().setAlignment(Component.CENTER);
            checkBox.getStyle().setBgTransparency(0, true);
            checkBox.getSelectedStyle().setBgTransparency(0, true);
            checkBox.getPressedStyle().setBgTransparency(0, true);
            checkBox.getPressedStyle().setBorder(null, true);
            checkBox.getStyle().setBorder(null, true);
            checkBox.getSelectedStyle().setBorder(null, true);
            checkBox.getStyle().setAlignment(CENTER);
            checkBox.getStyle().setPadding(Component.RIGHT, 10, true);

            mainContainer = new Container(new BorderLayout());
            mainContainer.setPreferredH(80);
            mainContainer.getStyle().setPadding(Component.TOP, 2, true);
            mainContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.settingsScreenSeperatorGrey, ColorCodes.settingsScreenSeperatorShadow), null, null), true);
            mainContainer.addComponent(BorderLayout.WEST, nameDescContainer);
            mainContainer.addComponent(BorderLayout.EAST, checkBox);
            
            if(cell.isSelected()) {
                checkBox.setIcon(checkedStateImage);
            }else {
                checkBox.setIcon(uncheckedStateImage);
            }
            
            return mainContainer;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
    
    
    /**
     * Data Model class for notification list.
     */
    class NotificationModel{
        private String title;
        private String subTitle;
        private boolean selected;

        public NotificationModel(String title, String subTitle, boolean selected) {
            this.selected = selected;
            this.title = title;
            this.subTitle = subTitle;                    
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @return the subTitle
         */
        public String getSubTitle() {
            return subTitle;
        }

        /**
         * @return the selected
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * @param selected the selected to set
         */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
