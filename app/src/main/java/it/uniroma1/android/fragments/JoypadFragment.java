package it.uniroma1.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.uniroma1.android.R;
import it.uniroma1.android.activities.MainActivity;
import it.uniroma1.android.move.utils.Joypad;

public class JoypadFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    private Joypad js;
    private RelativeLayout layout_joystick;
    private TextView xTextView, yTextView, angleTextView, distanceTextView, normDistanceTextView, directionTextView;


    public static JoypadFragment newInstance(int sectionNumber) {
        JoypadFragment fragment = new JoypadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public JoypadFragment() {
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String ipAddress = sharedPref.getString("ip_address", "127.0.0.1");
        //String port = sharedPref.getString("port", "4567");


        //connected = client.connect("192.168.10.108", 5555);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joypad, container, false);
        xTextView = (TextView) view.findViewById(R.id.x);
        yTextView = (TextView) view.findViewById(R.id.y);
        angleTextView = (TextView) view.findViewById(R.id.angle);
        distanceTextView = (TextView) view.findViewById(R.id.distance);
        normDistanceTextView = (TextView) view.findViewById(R.id.normalizedDistance);
        directionTextView = (TextView) view.findViewById(R.id.direction);
        layout_joystick = (RelativeLayout) view.findViewById(R.id.layout_joystick);
        js = new Joypad(getActivity().getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(200, 200);
        //js.setLayoutSize(700, 700);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(20);
        if (((MainActivity) getActivity()).getClient().isConnected()) {
            ((MainActivity) getActivity()).getClient().send("$JOY");
        }
        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                String directionString = "";
                if(arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    xTextView.setText("X : " + String.valueOf(js.getX()));
                    yTextView.setText("Y : " + String.valueOf(js.getY()));
                    angleTextView.setText("Angle : " + String.valueOf(js.getAngle()));
                    distanceTextView.setText("Distance : " + String.valueOf(js.getDistance()));
                    normDistanceTextView.setText("Normalized distance : " + String.valueOf(js.getNormalizedDistance()));
                    int direction = js.get8Direction();
                    if(direction == Joypad.STICK_UP) {
                        directionString = "Up";
                    } else if(direction == Joypad.STICK_UPRIGHT) {
                        directionString = "Up Right";
                    } else if(direction == Joypad.STICK_RIGHT) {
                        directionString = "Right";
                    } else if(direction == Joypad.STICK_DOWNRIGHT) {
                        directionString = "Down Right";
                    } else if(direction == Joypad.STICK_DOWN) {
                        directionString = "Down";
                    } else if(direction == Joypad.STICK_DOWNLEFT) {
                        directionString = "Down Left";
                    } else if(direction == Joypad.STICK_LEFT) {
                        directionString = "Left";
                    } else if(direction == Joypad.STICK_UPLEFT) {
                        directionString = "Up Left";
                    } else if(direction == Joypad.STICK_NONE) {
                        directionString = "Center";
                    }
                    directionTextView.setText("Direction : " + directionString);
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    xTextView.setText("X :");
                    yTextView.setText("Y :");
                    angleTextView.setText("Angle :");
                    distanceTextView.setText("Distance :");
                    normDistanceTextView.setText("Normalized distance :");
                    directionTextView.setText("Direction :");
                }
                if (((MainActivity) getActivity()).getClient().isConnected()) {
                    ((MainActivity) getActivity()).getClient().send(js.getNormalizedDistance() + ((MainActivity) getActivity()).getJoyParamSeparator() + js.getAngle());
                }

                return true;
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
