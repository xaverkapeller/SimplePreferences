package at.github.simplepreferences;

import com.github.wrdlbrnft.simplepreferences.api.DefaultIntegerValue;
import com.github.wrdlbrnft.simplepreferences.api.DefaultResourceValue;
import com.github.wrdlbrnft.simplepreferences.api.Preferences;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 19/01/15
 */
@Preferences
public interface ExamplePreferences {

    public void setText(String name);
    @DefaultResourceValue(R.string.preferences_default_text)
    public String getText();

    public void setCount(int count);
    @DefaultIntegerValue(27)
    public int getCount();
}
