package it.uniroma1.android.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.HashMap;
//import java.util.Map;

import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;

/**
 * Created by nduccio on 10/09/15.
 */
public class QuestionnaireDialogFragment extends DialogFragment {
    private int mNum;
    private RadioGroup answers;
    private Button finishButton, startButton, closeButton;
    static public final HashMap<Integer,String> log = new HashMap<Integer, String>();

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static QuestionnaireDialogFragment newInstance(int num) {
        QuestionnaireDialogFragment f = new QuestionnaireDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
        //System.out.println(mNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final MainActivity ma = ((MainActivity) getActivity());
        View view = inflater.inflate(R.layout.fragment_questionnaire_dialog, container, false);
        answers = (RadioGroup) view.findViewById(R.id.answers);
        TextView tv = (TextView) view.findViewById(R.id.question_text);
        for (int i=0; i<answers.getChildCount(); i++) {
            answers.getChildAt(i).setVisibility(View.GONE);
        }
        finishButton = (Button)view.findViewById(R.id.next_button);
        startButton = (Button) view.findViewById(R.id.start_button);
        closeButton = (Button) view.findViewById(R.id.close_button);
        finishButton.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);
        closeButton.setVisibility(View.GONE);
        log.put(0, "start\n");
        switch(mNum) {
            case 1:
                answers.setVisibility(View.INVISIBLE);
                tv.setText(getString(R.string.question_text));
                ma.getTTS().speak(getString(R.string.question_text), TextToSpeech.QUEUE_FLUSH, null);
                startButton.setVisibility(View.VISIBLE);
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startButton.setVisibility(View.GONE);
                        closeButton.setVisibility(View.GONE);
                        QuestionnaireDialogFragment.this.dismiss();
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(2);
                        newFragment.show(getFragmentManager(), "dialog");
                    }
                });
                closeButton.setVisibility(View.VISIBLE);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startButton.setVisibility(View.GONE);
                        closeButton.setVisibility(View.GONE);
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 2:
                tv.setText("Do you like robots?");
                ma.getTTS().speak("Do you like robots?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yes:
                                System.out.println("YES");
                                log.put(1, "YES\n");
                                break;
                            case R.id.no:
                                System.out.println("NO");
                                log.put(1, "NO\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(3);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 3:
                tv.setText("Have you ever interacted with robots before?");
                ma.getTTS().speak("Have you ever interacted with robots before?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yes:
                                log.put(2, "YES\n");
                                break;
                            case R.id.no:
                                log.put(2, "NO\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(4);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 4:
                tv.setText("Did you feel comfortable with me and my position?");
                ma.getTTS().speak("Did you feel comfortable with me and my position?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yes:
                                log.put(3, "YES\n");
                                break;
                            case R.id.no:
                                log.put(3, "NO\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(5);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 5:
                tv.setText("Do you think that english language had a negative impact on our interaction?");
                ma.getTTS().speak("Do you think that english language had a negative impact on our interaction?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yes:
                                log.put(4, "YES\n");
                                break;
                            case R.id.no:
                                log.put(4, "NO\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(6);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 6:
                tv.setText("Once I will be fully recharged, would you unplug my power cable?");
                ma.getTTS().speak("Once I will be fully recharged, would you unplug my power cable?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        DialogFragment newFragment;
                        switch (checkedId) {
                            case R.id.yes:
                                newFragment = QuestionnaireDialogFragment.newInstance(7);
                                newFragment.show(getFragmentManager(), "dialog");
                                log.put(5, "YES\n");
                                break;
                            case R.id.no:
                                newFragment = QuestionnaireDialogFragment.newInstance(9);
                                newFragment.show(getFragmentManager(), "dialog");
                                log.put(5, "NO\n");
                                break;
                        }
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 7:
                tv.setText("And then, would you help me to get off the stairs?");
                ma.getTTS().speak("And then, would you help me to get off the stairs?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        DialogFragment newFragment;
                        switch (checkedId) {
                            case R.id.yes:
                                newFragment = QuestionnaireDialogFragment.newInstance(8);
                                newFragment.show(getFragmentManager(), "dialog");
                                log.put(6, "YES\n");
                                break;
                            case R.id.no:
                                newFragment = QuestionnaireDialogFragment.newInstance(9);
                                newFragment.show(getFragmentManager(), "dialog");
                                log.put(6, "NO\n");
                                break;
                        }
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 8:
                tv.setText("If I need it, would you escort me to PhD room?");
                ma.getTTS().speak("If I need it, would you escort me to PhD room?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        DialogFragment newFragment;
                        switch (checkedId) {
                            case R.id.yes:
                                newFragment = QuestionnaireDialogFragment.newInstance(9);
                                newFragment.show(getFragmentManager(), "dialog");
                                log.put(7, "YES\n");
                                break;
                            case R.id.no:
                                newFragment = QuestionnaireDialogFragment.newInstance(9);
                                newFragment.show(getFragmentManager(), "dialog");
                                log.put(7, "NO\n");
                                break;
                        }
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 9:
                tv.setText("Are you taller than 1.75m?");
                ma.getTTS().speak("Are you taller than 1.75m?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yes:
                                log.put(8, "YES\n");
                                break;
                            case R.id.no:
                                log.put(8, "NO\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(10);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 10:
                tv.setText("Are you...");
                ma.getTTS().speak("Are you", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(17).setVisibility(View.VISIBLE);
                answers.getChildAt(18).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.male:
                                log.put(9, "Male\n");
                                break;
                            case R.id.female:
                                log.put(9, "Female\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(11);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 11:
                tv.setText("Thank you! Have a nice day!");
                ma.getTTS().speak("Thank you! Have a nice day!", TextToSpeech.QUEUE_FLUSH, null);
                finishButton.setVisibility(View.VISIBLE);
                finishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuestionnaireDialogFragment.this.dismiss();
                        //TODO
                        //((MainActivity) getActivity()).onNavigationDrawerItemSelected(3);
                        //((MainActivity) getActivity()).onSectionAttached(4);
                        log.put(10, "finish\n");
                        // send answers back to the server
                        ma.getClient().send(log.toString());

                        log.clear();
                    }
                });

                break;
            /*case 2:

                tv.setText("If we meet again, would you change the distance from you?");
                ma.getTTS().speak("If we meet again, would you change the distance from you?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        DialogFragment newFragment;
                        switch (checkedId) {
                            case R.id.yes:
                                newFragment = QuestionnaireDialogFragment.newInstance(3);
                                newFragment.show(getFragmentManager(), "dialog");
                                System.out.println("YES");
                                log.put(2, "YES\n");
                                break;
                            case R.id.no:
                                newFragment = QuestionnaireDialogFragment.newInstance(4);
                                newFragment.show(getFragmentManager(), "dialog");
                                System.out.println("NO");
                                log.put(2, "NO\n");
                                break;
                        }
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 3:
                tv.setText("Where do you want to position myself?");
                ma.getTTS().speak("Where do you want to position myself?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(2).setVisibility(View.VISIBLE);
                answers.getChildAt(3).setVisibility(View.VISIBLE);
                answers.getChildAt(4).setVisibility(View.VISIBLE);
                answers.getChildAt(5).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.more25:
                                System.out.println(">3.6m");
                                log.put(3, ">3.6\n");
                                break;
                            case R.id.more1:
                                System.out.println(">1.2m and <3.6m");
                                log.put(3, ">1.2\n");
                                break;
                            case R.id.more05:
                                System.out.println(">0.45m and <1.2m");
                                log.put(3, ">0.45\n");
                                break;
                            case R.id.more0:
                                System.out.println(">0 and <0.45m");
                                log.put(3, ">0\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(4);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 4:
                tv.setText("Do you want to change my approaching angle?");
                ma.getTTS().speak("Do you want to change my approaching angle?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        DialogFragment newFragment;
                        switch (checkedId) {
                            case R.id.yes:
                                newFragment = QuestionnaireDialogFragment.newInstance(5);
                                newFragment.show(getFragmentManager(), "dialog");
                                System.out.println("YES");
                                log.put(4, "YES\n");
                                break;
                            case R.id.no:
                                newFragment = QuestionnaireDialogFragment.newInstance(6);
                                newFragment.show(getFragmentManager(), "dialog");
                                System.out.println("NO");
                                log.put(4, "NO\n");
                                break;
                        }
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 5:
                tv.setText("Where do you want to position myself?");
                ma.getTTS().speak("Where do you want to position myself?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(6).setVisibility(View.VISIBLE);
                answers.getChildAt(7).setVisibility(View.VISIBLE);
                answers.getChildAt(8).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.left:
                                System.out.println("Left");
                                log.put(5, "Left\n");
                                break;
                            case R.id.right:
                                System.out.println("Right");
                                log.put(5, "Right\n");
                                break;
                            case R.id.center:
                                System.out.println("Center");
                                log.put(5, "Center\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(6);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 6:
                tv.setText("Are you satisfied with the interaction?");
                ma.getTTS().speak("Are you satisfied with the interaction?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        DialogFragment newFragment;
                        switch (checkedId) {
                            case R.id.yes:
                                newFragment = QuestionnaireDialogFragment.newInstance(8);
                                newFragment.show(getFragmentManager(), "dialog");
                                System.out.println("YES");
                                log.put(6, "YES\n");
                                break;
                            case R.id.no:
                                newFragment = QuestionnaireDialogFragment.newInstance(7);
                                newFragment.show(getFragmentManager(), "dialog");
                                System.out.println("NO");
                                log.put(6, "NO\n");
                                break;
                        }
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 7:
                tv.setText("Would you prefer a longer interaction?");
                ma.getTTS().speak("Would you prefer a longer interaction?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(0).setVisibility(View.VISIBLE);
                answers.getChildAt(1).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.yes:
                                System.out.println("YES");
                                log.put(7, "YES\n");
                                break;
                            case R.id.no:
                                System.out.println("NO");
                                log.put(7, "NO\n");
                                break;
                        }
                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(8);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 8:
                tv.setText("Which is your overall opinion about this experiment?");
                ma.getTTS().speak("Which is your overall opinion about this experiment?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(9).setVisibility(View.VISIBLE);
                answers.getChildAt(10).setVisibility(View.VISIBLE);
                answers.getChildAt(11).setVisibility(View.VISIBLE);
                answers.getChildAt(12).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.positive:
                                log.put(8, "Positive\n");
                                break;
                            case R.id.almost_positive:
                                log.put(8, "Almost positive\n");
                                break;
                            case R.id.almost_negative:
                                log.put(8, "Almost negative\n");
                                break;
                            case R.id.negative:
                                log.put(8, "Negative\n");
                                break;
                        }

                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(9);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;
            case 9:
                tv.setText("Thank you! Bye bye!");
                ma.getTTS().speak("Thank you! Bye bye!", TextToSpeech.QUEUE_FLUSH, null);
                finishButton.setVisibility(View.VISIBLE);
                finishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuestionnaireDialogFragment.this.dismiss();
                        //TODO
                        //((MainActivity) getActivity()).onNavigationDrawerItemSelected(3);
                        //((MainActivity) getActivity()).onSectionAttached(4);
                        log.put(10, "---\n");
                        System.out.println(log.toString());
                        // send answers back to the server
                        ma.getClient().send(log.toString());
                        System.out.println("sent\n");

                        log.clear();
                    }
                });

                break;

            case 11:
                tv.setText("How do you consider yourself in Robotics?");
                ma.getTTS().speak("How do you consider yourself in Robotics?", TextToSpeech.QUEUE_FLUSH, null);
                answers.setVisibility(View.VISIBLE);
                answers.getChildAt(13).setVisibility(View.VISIBLE);
                answers.getChildAt(14).setVisibility(View.VISIBLE);
                answers.getChildAt(15).setVisibility(View.VISIBLE);
                answers.getChildAt(16).setVisibility(View.VISIBLE);
                answers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.everyday:
                                log.put(9, "Everyday\n");
                                break;
                            case R.id.some_projects:
                                log.put(9, "Some projects\n");
                                break;
                            case R.id.couple_of_times:
                                log.put(9, "Couple of times\n");
                                break;
                            case R.id.never_seen:
                                log.put(9, "Never seen\n");
                                break;
                        }

                        DialogFragment newFragment = QuestionnaireDialogFragment.newInstance(1);
                        newFragment.show(getFragmentManager(), "dialog");
                        QuestionnaireDialogFragment.this.dismiss();
                    }
                });
                break;*/
        }

        return view;
    }
}
