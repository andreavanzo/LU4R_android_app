package it.uniroma1.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.connectivity.ListeningThread;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AvatarFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private GifDrawable gifDrawable;
    private ListeningThread lt;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AvatarFragment newInstance(int sectionNumber) {
        AvatarFragment fragment = new AvatarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AvatarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        if (MainActivity.getClient().isConnected()) {
            MainActivity.getClient().send("$AVA");
        }
        lt = new ListeningThread();
        lt.start();
        final GifImageView gifImageView = (GifImageView) view.findViewById(R.id.gifImageView);
        gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.stop();
        gifDrawable.seekToFrameAndGet(0);
        gifDrawable.start();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lt.terminate();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
