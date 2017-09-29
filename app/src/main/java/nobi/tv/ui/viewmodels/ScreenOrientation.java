package nobi.tv.ui.viewmodels;

/**
 * Created by Joel on 2/13/2017.
 */

public enum ScreenOrientation {
    Landscape("landscape"),
    Portrait("portrait");

    private final String name;

    private ScreenOrientation(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.toLowerCase().equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
