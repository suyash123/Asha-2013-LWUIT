package com.bsb.hike.ui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bsb.hike.Hike;
import com.bsb.hike.dto.AppState;
import com.bsb.hike.io.ClientConnectionHandler;
import com.bsb.hike.io.ConnectionFailedException;
import com.bsb.hike.io.mqtt.MqttConnectionHandler;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.BaseRetryable;
import com.bsb.hike.util.Log;
import com.bsb.hike.util.ModelUtils;
import com.bsb.hike.util.UIHelper;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
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
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author Ranjith Yadav
 * @author Puneet Agarwal
 */
public class FormPrivacy extends FormHikeBase {

    private Command backCommand;
    private boolean clicked = false;
    private Vector privacyVector = new Vector(2);
    private final static String TAG = "FormPrivacy";

    /**
     * Constructor of privacy form. This form gives options user to delete or unlink account from the device. Adding and initializing all components 
     * are done in this constructor.
     */
    public FormPrivacy() {

        getStyle().setBgColor(ColorCodes.settingsScreenBgGrey, true);

        //#if nokia2_0
//#         setTitle(LBL_PRIVACY);
        //#endif

        privacyVector.addElement(new PrivacyModel(LBL_DELETE_ACCOUNT, MSG_DELETE_ACCOUNT));
        privacyVector.addElement(new PrivacyModel(LBL_SIGNOUT, MSG_SIGNOUT_ACCOUNT));

        initCommands();

        //#if nokia1_1
//#         Label privacyLabel = new Label(LBL_PRIVACY);
//#         privacyLabel.getStyle().setBgTransparency(0, true);
//#         privacyLabel.getStyle().setFgColor(0x808080, true);
//#         privacyLabel.getStyle().setMargin(Component.TOP, 5, true);
//#         addComponent(privacyLabel);
        //#endif

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        final List privacyList = new List(privacyVector);
        privacyList.setFocusable(false);
        privacyList.setFocus(false);
        privacyList.getStyle().setPadding(Component.LEFT, 0, true);
        privacyList.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        privacyList.getStyle().setPadding(Component.RIGHT, 0, true);
        privacyList.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        privacyList.getStyle().setPadding(Component.BOTTOM, 0, true);
        privacyList.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        privacyList.getStyle().setBgColor(ColorCodes.settingsScreenListBgGrey, true);
        privacyList.getSelectedStyle().setBgColor(ColorCodes.settingsScreenListBgGrey, true);
        privacyList.getStyle().setBgTransparency(255, true);
        privacyList.getSelectedStyle().setBgTransparency(255, true);
        privacyList.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.settingsScreenBorderGrey, ColorCodes.settingsScreenBorderShadow), null, null), true);
        privacyList.getSelectedStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.settingsScreenBorderGrey, ColorCodes.settingsScreenBorderShadow), null, null), true);
        privacyList.setRenderer(new PrivacyListRenderer());
        privacyList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!clicked) {
                    clicked = true;

                    switch (privacyList.getSelectedIndex()) {

                        case 0:
                            showDialog(MSG_DELETE_ACCOUNT_CONFIRMATION, LBL_DELETE, LBL_CANCEL);
                            clicked = false;
                            break;

                        case 1:
                            showDialog(MSG_SIGNOUT_ACCOUNT_CONFIRMATION, LBL_UNLINK, LBL_CANCEL);
                            clicked = false;
                            break;
                    }
                }
            }
        });

        addComponent(privacyList);
    }

    
    /**
     * Privacy List Renderer class renders the list.
     */
    private class PrivacyListRenderer extends Container implements ListCellRenderer {

        private Label arrowIconLabel;
        private Image arrowIconImage;
        private Container mainContainer;

        public PrivacyListRenderer() {

            arrowIconImage = AppResource.getImageFromResource(PATH_LIST_ARROW_ICON);
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {

            PrivacyModel cell = (PrivacyModel) value;

            setLayout(new BorderLayout());

            Label name = new Label();
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
            description.setPreferredW(com.sun.lwuit.Display.getInstance().getDisplayWidth() - 20);
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

            arrowIconLabel = new Label();
            arrowIconLabel.setIcon(arrowIconImage);
            arrowIconLabel.getStyle().setAlignment(Component.CENTER);
            arrowIconLabel.getStyle().setBgTransparency(0, true);
            arrowIconLabel.getSelectedStyle().setBgTransparency(0, true);
            arrowIconLabel.getPressedStyle().setBgTransparency(0, true);
            arrowIconLabel.getPressedStyle().setBorder(null, true);
            arrowIconLabel.getStyle().setBorder(null, true);
            arrowIconLabel.getSelectedStyle().setBorder(null, true);
            arrowIconLabel.getStyle().setAlignment(CENTER);
            arrowIconLabel.getStyle().setPadding(Component.RIGHT, 10, true);

            mainContainer = new Container(new BorderLayout());
            mainContainer.setPreferredH(80);
            mainContainer.getStyle().setPadding(Component.TOP, 2, true);
            mainContainer.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.settingsScreenSeperatorGrey, ColorCodes.settingsScreenSeperatorShadow), null, null), true);
            mainContainer.addComponent(BorderLayout.WEST, nameDescContainer);
            mainContainer.addComponent(BorderLayout.EAST, arrowIconLabel);

            return mainContainer;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }

    
    /**
     * Data Model class for privacy list.
     */
    class PrivacyModel {

        private String title;
        private String subTitle;

        public PrivacyModel(String title, String subTitle) {
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
    }

    
    /**
     * This method initializes all the commands which are there on the current form.
     */
    private void initCommands() {

        backCommand = new Command(LBL_BACK) {
            public void actionPerformed(ActionEvent evt) {
                super.actionPerformed(evt);

                DisplayStackManager.showForm(DisplayStackManager.FORM_PROFILE);
            }
        };

        addCommand(backCommand);
        setBackCommand(backCommand);
    }

    
    /**
     * This method opens native LCDUI dialog on the screen for confirmation, when user clicks on delete or unlink account from the list.
     * @param message
     * @param textPositive
     * @param textNegative 
     */
    private void showDialog(final String message, String textPositive, String textNegative) {

        Alert alertDialog = UIHelper.getAlertDialog(LBL_CONFIRM, message, textPositive, textNegative, AlertType.CONFIRMATION);
        alertDialog.setCommandListener(new CommandListener() {
            public void commandAction(javax.microedition.lcdui.Command c, Displayable d) {
                if (c.getLabel().equals(LBL_UNLINK)) {
                    BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                        public void retry() {
                            new Thread() {
                                public void run() {
                                           
                                    Log.v(TAG, "retrying...");
                                    DisplayStackManager.showProgressForm(true, MSG_UNLINK_PROGRESS);
                                    try {
                                        MqttConnectionHandler.getMqttConnectionHandler().close();
                                        if (ClientConnectionHandler.UnlinkAccount()) {
                                            ModelUtils.deleteUser(null);
                                            DisplayStackManager.showForm(AppState.getForm());
                                        } else {
                                            DisplayStackManager.showForm(DisplayStackManager.FORM_ERROR);
                                        }
                                    } catch (Exception ex) {
                                               
                                        Log.v(TAG, "exception in unlink account:" + ex.getClass().getName());
                                        if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                            errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                        }
                                        errorForm.show();
                                    }
                                }
                            }.start();
                        }
                    };
                    addressUpdateRetryable.retry();
                }else if (c.getLabel().equals(LBL_DELETE)) {
                    BaseRetryable addressUpdateRetryable = new BaseRetryable() {
                        public void retry() {
                            new Thread() {
                                public void run() {
                                           
                                    Log.v(TAG, "retrying...");
                                    DisplayStackManager.showProgressForm(true, MSG_DELETE_PROGRESS);
                                    try {
                                        MqttConnectionHandler.getMqttConnectionHandler().close();
                                        if (ClientConnectionHandler.DeleteAccount()) {
                                            ModelUtils.deleteUser(null);
                                            DisplayStackManager.showForm(AppState.getForm());
                                        } else {
                                            DisplayStackManager.showForm(DisplayStackManager.FORM_ERROR);
                                        }
                                    } catch (Exception ex) {
                                               
                                        Log.v(TAG, "exception in delete account:" + ex.getClass().getName());
                                        if (ex instanceof IOException || ex instanceof ConnectionFailedException) {
                                            errorForm.setErrorMessage(NO_NETWORK_TITLE);
                                        }
                                        errorForm.show();
                                    }
                                }
                            }.start();
                        }
                    };
                    addressUpdateRetryable.retry();
                }
                if (c.getLabel().equals(LBL_CANCEL)) {

                    DisplayStackManager.showForm(AppState.getForm());
                }
            }
        });

        javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).setCurrent(alertDialog, javax.microedition.lcdui.Display.getDisplay(Hike.sMidlet).getCurrent());
    }
}
