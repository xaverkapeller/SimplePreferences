package at.github.simplepreferences;

import com.github.wrdlbrnft.simplepreferences.api.Preferences;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 19/01/15
 */
@Preferences
public interface ExamplePreferences {

    public void setText(String name);
    public String getText();

    public void setCount(int count);
    public int getCount();
}
