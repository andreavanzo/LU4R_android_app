package it.uniroma1.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Hypothesis;
import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.utils.FloatingActionButton;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class NLPChainFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextView hypothesesContent, hypothesesTitle, commandTitle, commandContent;

    private FloatingActionButton fabButton;

    private SpeechRecognizer defaultRecognizer;

    private Intent recognizerIntent;

    private edu.cmu.pocketsphinx.SpeechRecognizer keywordRecognizer;

    private static final String KWS_SEARCH = "wakeup";

    private static final String KEYPHRASE = "ok robot";

    public static NLPChainFragment newInstance(int sectionNumber) {
        NLPChainFragment fragment = new NLPChainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public NLPChainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        defaultRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity(), ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
        defaultRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech onBeginningOfSpeech");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                System.out.println("Speech onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("Speech onEndOfSpeech");
            }

            @Override
            public void onError(int error) {
                System.out.println("Speech onError: " + error);
                //TODO
                //switchSearch(KWS_SEARCH);

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                System.out.println("Speech onEvent");
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                System.out.println("Speech onPartialResults");
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Speech onReadyForSpeech");
            }


            @Override
            public void onResults(Bundle results) {

                String hypoToSend = "{\"hypotheses\":[";
                ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String hypoResults = "";
                String hypo;
                for (int i=0; i<strlist.size(); i++) {
                    hypo = strlist.get(i);
                    hypoResults += hypo + "\n";
                    hypoToSend += "{\"transcription\":\"" + hypo + "\",\"confidence\":0.0,\"rank\":0}";
                    if (i != strlist.size() - 1) {
                        hypoToSend += ",";
                    } else {
                        hypoToSend += "]}";
                    }
                }
                hypothesesContent.setText(strlist.get(0));
                if (((MainActivity) getActivity()).getClient().isConnected()) {
                    ((MainActivity) getActivity()).getClient().send(hypoToSend);
                    //String robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                    //robotResponse = robotResponse.replaceAll("\\|", "\n").replaceAll(" ", "\t");
                    //commandContent.setText(robotResponse);
                    /*if (robotResponse.contains("Taking")) {
                        ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to take the book that is on the table.", TextToSpeech.QUEUE_FLUSH, null);
                        robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                        if (robotResponse.contains("DON"))
                            ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                    } else if (robotResponse.contains("Bringing")) {
                        if (robotResponse.contains("Lab1")) {
                            ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to bring the book to the table of the laboratory.", TextToSpeech.QUEUE_FLUSH, null);
                            robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            if (robotResponse.contains("DON")) {
                                ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                                robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                                if (robotResponse.contains("DON"))
                                    ((MainActivity) getActivity()).getTTS().speak("Please, could you put this book on the table? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if (robotResponse.contains("Lab2")) {
                            ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to bring the book to the laboratory.", TextToSpeech.QUEUE_FLUSH, null);
                            robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            if (robotResponse.contains("DON")) {
                                ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                                robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                                if (robotResponse.contains("DON"))
                                    ((MainActivity) getActivity()).getTTS().speak("Please, could you remove the book from my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else {
                            ((MainActivity) getActivity()).getTTS().speak("Okay, I'm going to bring the book to the table.", TextToSpeech.QUEUE_FLUSH, null);
                            robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                            if (robotResponse.contains("DON")) {
                                ((MainActivity) getActivity()).getTTS().speak("Please, could you put the book on my tray? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                                robotResponse = ((MainActivity) getActivity()).getClient().readResponse();
                                if (robotResponse.contains("DON"))
                                    ((MainActivity) getActivity()).getTTS().speak("Please, could you put this book on the table? Thank you!", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }*/
                    /*if (robotResponse.contains("[SAY]")) {
                        if (robotResponse.contains("[COMMAND]")) {
                            String[] splitted = robotResponse.split("\\|");
                            ((MainActivity) getActivity()).getTTS().speak(splitted[0].replaceAll("\\[SAY\\]", ""), TextToSpeech.QUEUE_FLUSH, null);
                            commandContent.setText(splitted[1].replaceAll("\\[COMMAND\\]", ""));
                        } else {
                            ((MainActivity) getActivity()).getTTS().speak(robotResponse.replaceAll("\\[SAY\\]", ""), TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } else {
                        commandContent.setText(robotResponse);
                    }*(

                    /*TextView robotText = (TextView) view.findViewById(R.id.robotSaidContent);

                    robotText.setText(robotResponse);
                    ((MainActivity) getActivity()).getTTS().speak(robotResponse, TextToSpeech.QUEUE_FLUSH, null);*/
                }
                //TODO
                //switchSearch(KWS_SEARCH);

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                System.out.println("Speech onRmsChanged");
            }
        });
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ((MainActivity) getActivity()).getSpeechLanguage().toString());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "it.uniroma1.android.fragments");

        fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.speak))
                .withButtonColor(Color.LTGRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultRecognizer.startListening(recognizerIntent);
            }
        });

        //TODO
        /*new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getActivity().getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    System.out.println("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                    System.out.println("Keyword ASR is ready!");
                }
            }
        }.execute();*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View view = inflater.inflate(R.layout.fragment_nlp_chain, container, false);
        hypothesesTitle = (TextView) view.findViewById(R.id.youSaidTitle);
        hypothesesTitle.setText("Transcription");

        commandTitle = (TextView) view.findViewById(R.id.commandTitle);
        commandTitle.setText(R.string.command);
        commandContent = (TextView) view.findViewById(R.id.commandContent);

        hypothesesContent = (TextView) view.findViewById(R.id.youSaidContent);

        if (((MainActivity) getActivity()).getClient().isConnected()) {
            ((MainActivity) getActivity()).getClient().send("$NLP");
        }
        fabButton.showFloatingActionButton();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void switchSearch(String searchName) {
        keywordRecognizer.stop();
        keywordRecognizer.startListening(searchName);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        keywordRecognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir)
                .setBoolean("-allphone_ci", true)
                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)
                .getRecognizer();
        keywordRecognizer.addListener(new edu.cmu.pocketsphinx.RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onEndOfSpeech() {
                if (!keywordRecognizer.getSearchName().equals(KWS_SEARCH))
                    switchSearch(KWS_SEARCH);
            }

            @Override
            public void onPartialResult(Hypothesis hypothesis) {
                if (hypothesis == null)
                    return;
                String text = hypothesis.getHypstr();
                if (text.equals(KEYPHRASE)) {
                    keywordRecognizer.stop();
                    if (defaultRecognizer != null)
                        defaultRecognizer.startListening(recognizerIntent);
                }
            }

            @Override
            public void onResult(Hypothesis hypothesis) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onTimeout() {
                switchSearch(KWS_SEARCH);
            }
        });

        keywordRecognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fabButton.hideFloatingActionButton();
        //TODO
        //keywordRecognizer.stop();
        defaultRecognizer.stopListening();
        //keywordRecognizer.cancel();
        //keywordRecognizer.shutdown();
    }


}
