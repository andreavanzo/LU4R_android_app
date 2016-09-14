package it.uniroma1.android.fragments.settings;

import android.content.Context;
import android.os.Environment;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import java.io.File;

/**
 * Class that displays a dialog asking the user to confirm the choice of resetting the internal keyword database.<p/>
 * If the positive button is selected, the internal file will be located and erased.<p/>
 */
public class KeywordResetPreference extends DialogPreference {

    /**
     * Calls the {@link android.preference.DialogPreference} standard constructor.
     * */
    public KeywordResetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Calls the {@link android.preference.DialogPreference} standard onDialogClosed procedure, then performs the database reset depending on the user choice.
     * */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        //if sd not available, don't continue
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return;

        //if the sd is present and the file exists, remove it
        if(positiveResult==true)
        {
            File file = new File(Environment.getExternalStorageDirectory().getPath().toString()+"/SpeechToRobot/config.gram");
            file.delete();
        }
    }

}