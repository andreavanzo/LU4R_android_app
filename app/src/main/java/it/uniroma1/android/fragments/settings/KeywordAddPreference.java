package it.uniroma1.android.fragments.settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.widget.Toast.makeText;

/**
 * Class that creates the dialog with an EditText field and a SeekBar for adding a new keyword/keyphrase to PocketSphinx's DB.<p/>
 * When the user confirms the positive choice, any keyword inputted is searched on the dictionary used by PocketSphinx.<p/>
 * If all keywords exist, the keyphrase will be added to the list with its own threshold.<p/>
 * If the list file does not exist yet it is created.<p/>
 * */
public class KeywordAddPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, OnClickListener {
    // ------------------------------------------------------------------------------------------
    // Private attributes :
    private static final String androidns = "http://schemas.android.com/apk/res/android";

    private EditText textBox;
    private SeekBar mSeekBar;
    private TextView mSplashText, mValueText;
    private Context mContext;

    private String mDialogMessage, mSuffix;
    private int mDefault, mMax, mValue, mOrig = 0;

    // ------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------
    // Constructor :
    /**
     * Constructor for the custom dialogView. Handles the editText field, the Seekbar values, and the messages that have to be shown.
     * */
    public KeywordAddPreference(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;

        // Get string value for dialogMessage :
        int mDialogMessageId = attrs.getAttributeResourceValue(androidns,
                "dialogMessage", 0);
        if (mDialogMessageId == 0)
            mDialogMessage = attrs
                    .getAttributeValue(androidns, "dialogMessage");
        else
            mDialogMessage = mContext.getString(mDialogMessageId);

        // Get string value for suffix (text attribute in xml file) :
        int mSuffixId = attrs.getAttributeResourceValue(androidns, "text", 0);
        if (mSuffixId == 0)
            mSuffix = attrs.getAttributeValue(androidns, "text");
        else
            mSuffix = mContext.getString(mSuffixId);

        // Get default and max seekbar values :
        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(androidns, "max", 100);
    }

    // ------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------
    // DialogPreference methods :
    /**
     * DialogView creation. Programmatically set all data like layout, padding, text, text color.
     * */
    @Override
    protected View onCreateDialogView() {

        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mSplashText = new TextView(mContext);
        mSplashText.setPadding(30, 10, 30, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        TextView boxTitle = new TextView(mContext);
        boxTitle.setPadding(30, 10, 30, 10);
        boxTitle.setText("Keyword/Keyphrase");
        boxTitle.setTextColor(Color.parseColor("#F5F5F5"));
        boxTitle.setTextSize(17);
        layout.addView(boxTitle);

        textBox=new EditText(mContext);
        //textBox.setPadding(30,10,30,10);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(textBox, params);

        TextView boxTitle2 = new TextView(mContext);
        boxTitle2.setPadding(30, 10, 30, 10);
        boxTitle2.setText("Threshold");
        boxTitle2.setTextColor(Color.parseColor("#F5F5F5"));
        boxTitle2.setTextSize(17);
        layout.addView(boxTitle2);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(16);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);

        return layout;
    }

    /**
     * Call to the standard dialogView onBind, while loading the SeekBar previous value if required.
     * */
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
    }

    /**
     * Set the initial values of the dialogView fields.
     * */
    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        // Set adjustable value
        if (restore)
            mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
        else
            mValue = (Integer) defaultValue;
        // Set original pre-adjustment value
        mOrig = mValue;
    }

    // ------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------
    // OnSeekBarChangeListener methods :
    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        String t = String.valueOf(value);
        mValueText.setText(mSuffix == null ? t : t.concat(" " + mSuffix));

        if (shouldPersist()) {
            mValue = mSeekBar.getProgress();
            persistInt(mSeekBar.getProgress());
            callChangeListener(Integer.valueOf(mSeekBar.getProgress()));
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek) {
    }


    private void setMax(int max) {
        mMax = max;
    }

    private int getMax() {
        return mMax;
    }

    private void setProgress(int progress) {
        mValue = progress;
        if (mSeekBar != null)
            mSeekBar.setProgress(progress);
    }

    private int getProgress() {
        return mValue;
    }

    // ------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------
    // Set the positive button listener and onClick action :
    @Override
    public void showDialog(Bundle state) {

        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog())
                .getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = ((AlertDialog) getDialog())
                .getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(cListenPos);
        negativeButton.setOnClickListener(cListenNeg);
    }

    /**
     * Create the listener for the positive button.<p/>
     * When it is clicked, the values of the editText and seekbar are saved.<p/>
     * The keywords are searched in the dictionary. If all are found, the keyphrase is valid, so it is added in the list saved on the SD. If the file does not exist, it is created.<p/>
     * */
    View.OnClickListener cListenPos = new View.OnClickListener() {
        public void onClick(View v) {
            if (shouldPersist()) {
                mValue = mSeekBar.getProgress();
                mOrig = mSeekBar.getProgress();
                persistInt(mSeekBar.getProgress());
                callChangeListener(Integer.valueOf(mSeekBar.getProgress()));
            }

            /**Ok has been clicked, check if the keywords are on the dictionary and if yes save them**/
            int threshold=mSeekBar.getProgress();
            String user=textBox.getText().toString();

            //((AlertDialog) getDialog()).dismiss();

            if(user=="" || user==null)
                return;

            //if not possible to write on the sd, return
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                makeText(getContext(), "Problem in reading the SD, please check it", Toast.LENGTH_SHORT).show();
                return;
            }

            //Separate keywords
            String[] key = user.split("\\s+"); //Array of keys
            boolean[] found=new boolean[key.length];
            int toBeFound=key.length;


            //Search word on dictionary
            StringBuilder buf = new StringBuilder();
            InputStream json = null;
            try {
                json = getContext().getAssets().open("sync/cmudict-en-us.dict");
                BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                String str;

                while ((str = in.readLine()) != null) {
                    if(toBeFound==0)
                        break;

                    for(int i=0; i<key.length; i++)
                    {
                        if(!found[i]) //key not found yet
                            if(str.startsWith(key[i]+" ")){
                                found[i]=true;
                                toBeFound--;
                            }
                    }

                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(toBeFound==0) //Found all keywords, proceed
            {
                //Create string to be added
                String addKey;
                if(threshold<1)
                    addKey=user+" /1.0/";
                else
                    addKey=user+" /1e-"+threshold+"/";

                //Add the key in the db
                StringBuilder build = new StringBuilder();
                InputStream json2 = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getPath().toString()+"/SpeechToRobot", "config.gram");
                    if(file.exists()) {//Do something
                        json2 = new BufferedInputStream(new FileInputStream(file));
                    }
                    else
                        json2 = getContext().getAssets().open("sync/key.gram");

                    BufferedReader in = new BufferedReader(new InputStreamReader(json2, "UTF-8"));
                    String str;


                    while ((str = in.readLine()) != null) {
                        build.append(str + "\n");
                    }

                    //Append the user key at the end
                    build.append(addKey);

                    in.close();

                    //Rewrite on file
                    FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath().toString()+"/SpeechToRobot", "config.gram"));
                    stream.write(build.toString().getBytes());
                    stream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            ((AlertDialog) getDialog()).dismiss();
        }
    };

    View.OnClickListener cListenNeg = new View.OnClickListener() {
        public void onClick(View v) {
            if (shouldPersist()) {
                mValue = mOrig;
                persistInt(mOrig);
                callChangeListener(Integer.valueOf(mOrig));
            }
            ((AlertDialog) getDialog()).dismiss();
        }
    };

    @Override
    public void onClick(View v) {}

    // ------------------------------------------------------------------------------------------
}