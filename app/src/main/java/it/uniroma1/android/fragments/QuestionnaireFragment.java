package it.uniroma1.android.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.dialogs.QuestionnaireDialogFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private TextView questionText;

    private View view;

    private Button startButton;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static QuestionnaireFragment newInstance(int sectionNumber) {
        QuestionnaireFragment fragment = new QuestionnaireFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionnaireFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_questionnaire, container, false);
        final MainActivity ma = ((MainActivity) getActivity());
        questionText = (TextView) view.findViewById(R.id.question_text);
        questionText.setText(getString(R.string.question_text));
        ma.getTTS().speak(getString(R.string.question_text), TextToSpeech.QUEUE_FLUSH, null);

        startButton = (Button) view.findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.GONE);
                DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(1);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
