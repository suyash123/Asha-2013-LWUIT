/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsb.hike.ui;

import com.bsb.hike.dto.AddressBookEntry;
import com.bsb.hike.ui.component.SearchTextField;
import com.bsb.hike.util.AppResource;
import com.bsb.hike.util.TextUtils;
import com.nokia.mid.ui.KeyboardVisibilityListener;
import com.nokia.mid.ui.VirtualKeyboard;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import java.util.Vector;

/**
 *
 * @author Ankit Yadav
 */
public class FormSelectBase extends FormHikeBase {

    private static final String TAG = "FormSelectBase";
    private Image mOnHikeImg, mNotOnHikeImg;
    protected Vector mFilteredAddressBook = null;
    protected List mFilteredContactLst = null;
    protected SearchTextField mSearchTxtFld = new SearchTextField();

    
    /**
     * Constructor of the parent class of SelectContact and SelectGroupContact.
     */
    public FormSelectBase() {
        
        Runtime.getRuntime().gc();
        getStyle().setBgColor(ColorCodes.selectContactBgGrey, true);
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        mFilteredAddressBook = new Vector();
        mFilteredContactLst = new List(mFilteredAddressBook);
        mFilteredContactLst.getStyle().setPadding(Component.LEFT, 0, true);
        mFilteredContactLst.getSelectedStyle().setPadding(Component.LEFT, 0, true);
        mFilteredContactLst.getStyle().setPadding(Component.RIGHT, 0, true);
        mFilteredContactLst.getSelectedStyle().setPadding(Component.RIGHT, 0, true);
        mFilteredContactLst.getStyle().setPadding(Component.BOTTOM, 0, true);
        mFilteredContactLst.getSelectedStyle().setPadding(Component.BOTTOM, 0, true);
        mFilteredContactLst.setSmoothScrolling(true);
        mFilteredContactLst.setRenderer(new ContactsRenderer());
        setScrollable(false);
        
        addComponent(mSearchTxtFld);
        addComponent(mFilteredContactLst);        
    }
    
    
    /**
     * Contacts Renderer class which inflates the contact list on the screen.
     */
    class ContactsRenderer extends Container implements ListCellRenderer {

        private Label mNameLbl = new Label("");
        private Label mPhoneNumLbl = new Label("");
        private Label mOnHikeStsLbl = new Label();
        private Label onFocusLbl = new Label();

        public ContactsRenderer() {
            
            mOnHikeImg = AppResource.getImageFromResource(PATH_IC_ON_HIKE);
            mNotOnHikeImg = AppResource.getImageFromResource(PATH_IC_NOT_ON_HIKE);

            Container listPane = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            listPane.getStyle().setBorder(Border.createCompoundBorder(null, Border.createEtchedRaised(ColorCodes.selectContactSeperator, ColorCodes.selectContactSeperatorShadow), null, null), true);
            listPane.getStyle().setPadding(Component.LEFT, 5, true);
            listPane.getStyle().setPadding(Component.TOP, 3, true);
            listPane.getStyle().setPadding(Component.BOTTOM, 8, true);
            setLayout(new BorderLayout());
            
            onFocusLbl.getStyle().setBgTransparency(20, true);
            
            Container container = new Container(new BorderLayout());
            container.addComponent(BorderLayout.CENTER, mPhoneNumLbl);
            container.addComponent(BorderLayout.WEST, mOnHikeStsLbl);

            listPane.addComponent(mNameLbl);
            listPane.addComponent(container);

            addComponent(BorderLayout.CENTER, listPane);
            setFocusable(true);
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            if (value != null) {
                AddressBookEntry entry = (AddressBookEntry) value;
                String name = entry.getName();
                mNameLbl.setText(TextUtils.isEmpty(name) ? entry.getMsisdn() : entry.getName());
                mNameLbl.getStyle().setBgTransparency(0, true);
                mNameLbl.getStyle().setFgColor(ColorCodes.selectContactNameGrey);
                mNameLbl.getStyle().setFont(Fonts.MEDIUM);
                mNameLbl.getStyle().setPadding(Component.BOTTOM, 0, true);

                if (entry.getMsisdn() != null) {
                    mPhoneNumLbl.setText(entry.getMsisdn());
                } else {
                    mPhoneNumLbl.setText(entry.getPhoneNumber());
                }
                mPhoneNumLbl.getStyle().setBgTransparency(0, true);
                mPhoneNumLbl.getStyle().setFgColor(ColorCodes.selectContactNumberGrey);
                mPhoneNumLbl.getStyle().setFont(Fonts.SMALL);
                mPhoneNumLbl.getStyle().setPadding(Component.TOP, 0, true);
                mPhoneNumLbl.getStyle().setPadding(Component.LEFT, 0, true);

                mOnHikeStsLbl.getStyle().setBgTransparency(0, true);
                mOnHikeStsLbl.getStyle().setPadding(Component.RIGHT, 0, true);
                mOnHikeStsLbl.setIcon(entry.isOnHike() ? mOnHikeImg : mNotOnHikeImg);

//                         Log.v(TAG, "value exist");
            } else {
//                         Log.v(TAG, "value is null");
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            return onFocusLbl;
        }
    }
    
    
    /**
     * This class is extending KeyboardVisibilityListener class which will take the events of keyboard up and down.
     */
    private class KeyboardVisibilityListenerImpl implements KeyboardVisibilityListener {
        public void showNotify(int keyboardCategory) {
            if(!mFilteredAddressBook.isEmpty()) {
                 mFilteredContactLst.setSelectedIndex(0, true);
            }
        }

        public void hideNotify(int keyboardCategory) {
            
        }
    } 

    
    /**
     * This delegated method is called just after showing the form.
     */
    protected void onShow() {
        //#if nokia2_0
//#         VirtualKeyboard.setVisibilityListener(new KeyboardVisibilityListenerImpl());
        //#endif
    }
}
