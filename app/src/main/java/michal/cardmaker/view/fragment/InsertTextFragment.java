package michal.cardmaker.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import michal.cardmaker.R;
import michal.cardmaker.presenter.InsertTextFragmentListener;
import michal.cardmaker.presenter.StickerFragmentListener;
import michal.cardmaker.view.TemplateSinglePhoto;


public class InsertTextFragment extends Fragment {

    Button add_button;
    TextView textView;
    EditText editText;
    InsertTextFragmentListener insertTextFragmentListener;

    public InsertTextFragment(){

    }

    @SuppressLint("ValidFragment")
    public InsertTextFragment(Context context) {
        insertTextFragmentListener = (InsertTextFragmentListener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_text, container, false);

        add_button = view.findViewById(R.id.add_text);
        editText = view.findViewById(R.id.insert_text);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = getActivity().findViewById(R.id.text);
                textView.setText(editText.getText());
                textView.setVisibility(View.VISIBLE);
                textView.setClickable(true);
                textView.setEnabled(true);
                insertTextFragmentListener.sendTextView();
            }
        });

        return view;
    }

    public void clearText() {
        editText.getText().clear();
        editText.setText("");
    }
}
