package at.github.simplepreferences;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.wrdlbrnft.simplepreferences.PreferencesFactory;

/**
* Created with Android Studio
* User: Xaver
* Date: 19/01/15
*/
public class MainFragment extends Fragment {

    private ExamplePreferences preferences;

    private EditText etText;
    private Button btnIncrement;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        etText = (EditText) view.findViewById(R.id.etText);
        btnIncrement = (Button) view.findViewById(R.id.btnIncrement);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = PreferencesFactory.create(ExamplePreferences.class, getActivity());

        etText.setText(preferences.getText());
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                preferences.setText(s.toString());
            }
        });

        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = preferences.getCount();
                Toast.makeText(getActivity(), String.valueOf(count), Toast.LENGTH_SHORT).show();
                preferences.setCount(count + 1);
            }
        });

    }
}
