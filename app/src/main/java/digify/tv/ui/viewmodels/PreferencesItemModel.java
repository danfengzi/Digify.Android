package digify.tv.ui.viewmodels;

/**
 * Created by Joel on 1/26/2017.
 */

public class PreferencesItemModel {
    private PreferencesItemType itemType;
    private String buttonText;

    public PreferencesItemModel() {
    }

    public PreferencesItemModel(PreferencesItemType itemType, String buttonText) {
        this.itemType = itemType;
        this.buttonText = buttonText;
    }

    public PreferencesItemType getItemType() {
        return itemType;
    }

    public void setItemType(PreferencesItemType itemType) {
        this.itemType = itemType;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
