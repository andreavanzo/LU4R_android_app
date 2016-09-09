package it.uniroma1.android.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.dialogs.QuestionnaireDialogFragment;
import it.uniroma1.android.utils.FloatingActionButton;

public class DialogueInterfaceFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    private final int REQ_CODE_SPEECH_INPUT = 1001;

    private TextView robotSaidContent, robotSaidTitle, youSaidContent, youSaidTitle;

    private String robotResponse;

    private View view;

    private FloatingActionButton speechFabButton;//, setFabButton;

    //final MainActivity ma = ((MainActivity) getActivity());


    public static DialogueInterfaceFragment newInstance(int sectionNumber) {
        DialogueInterfaceFragment fragment = new DialogueInterfaceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DialogueInterfaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        speechFabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.speak))
                .withButtonColor(Color.LTGRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        /*setFabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.set))
                .withButtonColor(Color.LTGRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 96)
                .create();*/

        speechFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                promptSpeechInput();
                //defaultRecognizer.startListening(recognizerIntent);
            }
        });

        /*setFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendProxemicsSettings();
            }
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialogue_interface, container, false);

        robotSaidTitle = (TextView) view.findViewById(R.id.robotSaid);
        robotSaidTitle.setText(R.string.robotSaidTitle);
        robotSaidContent = (TextView) view.findViewById(R.id.robotSaidContent);
        robotSaidContent.setText("");

        youSaidTitle = (TextView) view.findViewById(R.id.youSaid);
        youSaidTitle.setText(R.string.youSaidTitle);
        youSaidContent = (TextView) view.findViewById(R.id.youSaidContent);
        youSaidContent.setText("");


        if (MainActivity.getClient().isConnected()) {
            MainActivity.getClient().send("$DIA");
        }
        //setFabButton.showFloatingActionButton();
        speechFabButton.showFloatingActionButton();



        return view;
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        //listeningThread.interrupt();
        youSaidContent.setText("");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ((MainActivity)getActivity()).getSpeechLanguage().toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity)getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String bestHypoString = "";
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == -1) {
                    if (data != null) {
                        String hypoToSend = "{\"hypotheses\":[";
                        ArrayList<String> strlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        bestHypoString = strlist.get(0);
                        String hypo;
                        for (int i=0; i<strlist.size(); i++) {
                            hypo = strlist.get(i);
                            hypoToSend += "{\"transcription\":\"" + hypo + "\",\"confidence\":0.0,\"rank\":0}";
                            if (i != strlist.size() - 1) {
                                hypoToSend += ",";
                            } else {
                                hypoToSend += "]}";
                            }
                        }
                        youSaidContent.setText(bestHypoString);
                        if (((MainActivity)getActivity()).getClient().isConnected()) {
                            ((MainActivity)getActivity()).getClient().send(hypoToSend);
                            String robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            robotSaidContent.setText(robotResponse);
                            ((MainActivity) getActivity()).getTTS().speak(robotResponse, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } else {
                        System.out.println("ECCE");
                    }
                } else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                    System.out.println("ResultCode: Audio Error");
                } else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                    System.out.println("ResultCode: Client Error");
                } else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                    System.out.println("ResultCode: Network Error");
                } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                    System.out.println("ResultCode: No Match");
                } else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                    System.out.println("ResultCode: Server Error");
                } else {
                    System.out.println("OTHER " + resultCode);
                }
                break;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //setFabButton.hideFloatingActionButton();
        speechFabButton.hideFloatingActionButton();
    }

    //EMANUELE
    /*@Override
    public void onResume(){
        super.onResume();
        robotSaidContent = (TextView) view.findViewById(R.id.robotSaidContent);
        robotSaidContent.setText(String.valueOf(robotResponse));
    }*/
}
