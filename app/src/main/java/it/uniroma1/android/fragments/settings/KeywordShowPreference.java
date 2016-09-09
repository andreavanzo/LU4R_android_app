package it.uniroma1.android.fragments.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that displays a dialog containing all the current keywords used by PocketSphinx.<p/>
 * Unless the default list has been modified, the list contained in the assets will be used.<p/>
 * If the keywords have been added or removed, the dialog will show the contents of the keyword file present on the SD.<p/>
 * */
public class KeywordShowPreference extends DialogPreference {

    /**
     * Calls the {@link android.preference.DialogPreference} standard constructor.
     * */
    public KeywordShowPreference(Context oContext, AttributeSet attrs) { super(oContext, attrs); }


    /**
     * Calls the {@link android.preference.DialogPreference} standard dialog builder and removes the cancel button.
     * */
    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNegativeButton(null, null);
    }

    /**
     * Function that fills the dialog with the contents of the asset file or the SD keyword file.<p/>
     * In the end it will show the currently used keywords/keyphrases, one per line.<p/>
     * */
    @Override
    protected View onCreateDialogView() {

        //Load file

        StringBuilder buf = new StringBuilder();
        InputStream json = null;
        try {

            //if sd not available, don't continue
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                json = getContext().getAssets().open("sync/key.gram");
            else
            {
                File file = new File(Environment.getExternalStorageDirectory().getPath().toString() + "/SpeechToRobot", "config.gram");
                if (file.exists()) {
                    json = new BufferedInputStream(new FileInputStream(file));
                } else
                    json = getContext().getAssets().open("sync/key.gram");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            //read the file and show every line in the dialogue
            while ((str = in.readLine()) != null) {
                buf.append(str + "\n");
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Set message
        setDialogMessage(buf);
        return super.onCreateDialogView();
    }

}
