package it.uniroma1.android.fragments.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static android.widget.Toast.makeText;

/**
 * Class that displays a dialog containing all the current keywords used by PocketSphinx, enabling the user to remove unwanted keywords.<p/>
 * Unless the default list has been modified, the list contained in the assets will be used.<p/>
 * If the keywords have been added or removed, the dialog will show the contents of the keyword file present on the SD.<p/>
 * */
public class KeywordRemovePreference extends MultiSelectListPreference {

    /**
     * Calls the {@link DialogPreference} standard constructor.
     * */
    public KeywordRemovePreference(Context oContext, AttributeSet attrs) {
        super(oContext, attrs);

        List<CharSequence> entries = new ArrayList<CharSequence>();
        List<CharSequence> entriesValues = new ArrayList<CharSequence>();

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entriesValues.toArray(new CharSequence[]{}));
    }



    /**
     * Function that fills the dialog with the contents of the asset file or the SD keyword file.<p/>
     * In the end it will show the currently used keywords/keyphrases, one per line.<p/>
     * */
    @Override
    protected View onCreateDialogView() {

        //Load file

        List<CharSequence> entries = new ArrayList<CharSequence>();
        List<CharSequence> entriesValues = new ArrayList<CharSequence>();

        InputStream json = null;

        try {

            //if sd not available, don't continue
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                json = getContext().getAssets().open("sync/key.gram");
            else
            {
                //check if the sd db exists, if yes read from it, if not read the asset file
                File file = new File(Environment.getExternalStorageDirectory().getPath().toString() + "/SpeechToRobot", "config.gram");
                if (file.exists()) {
                    json = new BufferedInputStream(new FileInputStream(file));
                } else
                    json = getContext().getAssets().open("sync/key.gram");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            int i=0;

            while ((str = in.readLine()) != null) {
                entries.add(str);
                entriesValues.add(String.valueOf(i));
                i++;
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entriesValues.toArray(new CharSequence[]{}));

        //Set message
        return super.onCreateDialogView();
    }

    /**
     * Function that takes care of removing the unwanted keywords when the dialog gets closed.<p/>
     * If the sd file is present, its contents are read and only the unselected lines will be rewritten.<p/>
     * If only the asset db is present, a new file will be created on the sd.<p/>
     * The process quits if the sd is not mounted, as asset files can only be read.<p/>
     * */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult==true) {
            StringBuilder buf = new StringBuilder();
            InputStream json = null;
            try {

                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    makeText(getContext(), "Problem in reading the SD, please check it", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if the sd db exists, if yes read from it, if not read the asset file
                File file = new File(Environment.getExternalStorageDirectory().getPath().toString() + "/SpeechToRobot", "config.gram");
                if (file.exists()) {
                    json = new BufferedInputStream(new FileInputStream(file));
                } else
                    json = getContext().getAssets().open("sync/key.gram");

                BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                String str;
                int i=0;
                Set<String> removeLines = getValues();

                //If the string is not the user string, write it again, else skip and trigger preference change
                while ((str = in.readLine()) != null) {
                    int notFound=0;
                    for(String s: removeLines) {

                        if (Integer.parseInt(s)==i)
                            notFound=1;
                    }

                    if(notFound==0)
                        buf.append(str + "\n");

                    i++;


                }

                in.close();

                //Rewrite on file
                FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath().toString() + "/SpeechToRobot", "config.gram"));
                stream.write(buf.toString().getBytes());
                stream.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            //Clean values
            Set<String> clear = Collections.emptySet();
            setValues(clear);
        }
    }
}
