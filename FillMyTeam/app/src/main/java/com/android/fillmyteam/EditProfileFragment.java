package com.android.fillmyteam;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.fillmyteam.model.User;
import com.android.fillmyteam.util.Constants;
import com.android.fillmyteam.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit.Retrofit;


public class EditProfileFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context mContext;
    double mLatitude;
    double mLongitude;
    //  Firebase mUrlRef;
    DatabaseReference mUrlRef;
    //  static TextView mDate;
    static TextView mTime;
    Button submitButton;
    //   GeoFire mGeoFire;
    String mPlayingTime;
    User mUser;
    ValueEventListener mCurrentUserRefListener;
    DatabaseReference mUserRef;
    Retrofit retrofit;
    public static final String LOG_TAG = EditProfileFragment.class.getSimpleName();
    String mParkLocation;
    TextView mPlaceTextView;
    ImageView mPlacePickerImageView;
    public static final int REQUEST_PLACE_PICKER = 1;
    Spinner mSportsListSpinner;
    ArrayAdapter<CharSequence> mAdapter;
    //  DailyAlarmReceiver alarmReceiver = new DailyAlarmReceiver();
    //CheckBox mCalendarInviteCheckbox;
    public static final String SAVED_USER = "saved_user";
    boolean mIsCalendarInvite;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    public static EditProfileFragment newInstance(User user) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_DETAILS, user);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(Constants.USER_DETAILS);
            /*mLatitude = getArguments().getDouble(Constants.LATITUDE);
            mLongitude = getArguments().getDouble(Constants.LONGITUDE);*/
            //   mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mUrlRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.APP_URL_USERS);


    /*    mUserRef = mUrlRef.child("/" + Utility.encodeEmail(mUser.getEmail()));
        mCurrentUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                updateViews();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mPlacePickerImageView = (ImageView) view.findViewById(R.id.picker_image_view);
        mPlacePickerImageView.setOnClickListener(this);
        mPlaceTextView = (TextView) view.findViewById(R.id.place_text_view);
        mTime = (TextView) view.findViewById(R.id.time);
        mTime.setOnClickListener(this);
        submitButton = (Button) view.findViewById(R.id.saveUserButton);
        submitButton.setOnClickListener(this);

        //mCalendarInviteCheckbox = (CheckBox) view.findViewById(R.id.calendar_notify);
        //  calendarInviteCheckbox.setOnClickListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mIsCalendarInvite = sharedPreferences.getBoolean(Constants.CALENDAR_EVENT_CREATION, false);
     /*   if (isCalendarInvite) {
            mCalendarInviteCheckbox.setChecked(true);
        }*/
        mSportsListSpinner = (Spinner) view.findViewById(R.id.sports_list_spinner);
        mAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sports_list, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsListSpinner.setAdapter(mAdapter);
        mSportsListSpinner.setOnItemSelectedListener(this);
        updateViews();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_USER)) {
                mUser = (User) savedInstanceState.getSerializable(SAVED_USER);
                updateViews();
            }
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void updateViews() {
        mTime.setText(mUser.getPlayingTime());
        // LatLng latLng =new LatLng(mUser.getLatitude(),mUser.getLongitude());
        mPlaceTextView.setText(mUser.getPlayingPlace());
        //  mAdapter=(ArrayAdapter)mSportsListSpinner.getAdapter();
        mSportsListSpinner.setSelection(mAdapter.getPosition(mUser.getSport()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
  /*      if (mUrlRef != null) {
            mUserRef.removeEventListener(mCurrentUserRefListener);
        }*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUser != null) {
            mUser.setPlayingTime(mTime.getText().toString());
            //    mUser.setPlayingPlace();
            outState.putSerializable(SAVED_USER, mUser);
        }
    }

    @Override
    public void onClick(View v) {
        DialogFragment newFragment;
        boolean checked;
        switch (v.getId()) {

            case R.id.time:
                newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), Constants.TIME_PICKER);
                break;


            case R.id.saveUserButton:
                if (!Utility.checkNetworkState(getActivity())) {
                    DialogFragment dialogFragment = new NetworkMessageDialogFragment();
                    dialogFragment.show(getFragmentManager(), getString(R.string.no_network));
                    return;
                }
                // mPlayingTime = Utility.getPlayingTimeInfo(mTime.getText().toString());
                mPlayingTime = mTime.getText().toString();
                //    Log.v(LOG_TAG,"playing date is"+date);
                saveUserData();
             /*   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.CALENDAR_EVENT_CREATION, mCalendarInviteCheckbox.isChecked());
                editor.commit();*/
                if (mIsCalendarInvite) {

                    GregorianCalendar gcalendar = new GregorianCalendar();
                    int date = gcalendar.get(Calendar.DATE);
                    String dateString = String.valueOf(date);
                    if (date < 10) {
                        dateString = "0" + date;
                    }
                    int month = gcalendar.get(Calendar.MONTH) + 1;
                    String monthString = String.valueOf(month);
                    if (month < 10) {
                        monthString = "0" + monthString;
                    }
                    int year = gcalendar.get(Calendar.YEAR);

                    String[] timeStamps = Utility.retrieveHourMinute(mPlayingTime);
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(year, month, date, Integer.parseInt(timeStamps[0]), Integer.parseInt(timeStamps[1]));
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(year, month, date, Integer.parseInt(timeStamps[0]) + 1, Integer.parseInt(timeStamps[1]));

                    String time = "T000000Z";
                    String freq = "FREQ=DAILY;";
                    String recurrent = freq + "UNTIL=" + (year + 1) + monthString + dateString + time;
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                            .putExtra(CalendarContract.Events.TITLE, "Recurrent event test")
                            //.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=20170730T083000Z")
                            .putExtra(CalendarContract.Events.RRULE, recurrent)
                            .putExtra(CalendarContract.Events.DESCRIPTION, "Let's play " + mUser.getSport())
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, mUser.getPlayingPlace())
                            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                    //.putExtra(Intent.EXTRA_EMAIL, "ruchita.maheshwary@g");
                    startActivity(intent);
                }
                break;

            case R.id.picker_image_view:
                if (!Utility.checkNetworkState(getActivity())) {
                    DialogFragment dialogFragment = new NetworkMessageDialogFragment();
                    dialogFragment.show(getFragmentManager(), getString(R.string.no_network));
                    return;
                }
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getActivity());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);

                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    //     Toast.makeText(getActivity(), "Google Play Services is not available.",
                    Toast.makeText(getActivity(), getString(R.string.google_services_unavailable),
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;

        }

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.v("Time Dialog", hourOfDay + ":" + minute);

            // updateTime(hourOfDay, minute);
            String playingTime = Utility.updateTime(hourOfDay, minute);
            mTime.setText(playingTime);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // BEGIN_INCLUDE(activity_result)
        Log.v(LOG_TAG, "In on activity result");
        if (requestCode == REQUEST_PLACE_PICKER) {
            // This result is from the PlacePicker dialog.
            if (resultCode == Activity.RESULT_OK) {

                final Place place = PlacePicker.getPlace(data, getActivity());

                String attributions = PlacePicker.getAttributions(data);
                mParkLocation = place.getAddress().toString();
                Log.v(LOG_TAG, "location chosen" + mParkLocation);
                mPlaceTextView.setText(mParkLocation);
                LatLng latLng = place.getLatLng();
                Log.v(LOG_TAG, "lat lng" + latLng.latitude + "," + latLng.longitude);
                mUser.setPlayingPlace(mParkLocation);
                mUser.setLatitude(latLng.latitude);
                mUser.setLongitude(latLng.longitude);


            }
        }
    }


    @Override
    public String toString() {
        return super.toString();
    }


    private void saveUserData() {


        DatabaseReference ref = mUrlRef.child("/" + Utility.encodeEmail(mUser.getEmail()));
        //  mUser.setPlayingTime(mTime.getText().toString());
        //ref.child("playingTime").setValue(mPlayingTime);
        ref.child(Constants.PLAY_TIME).setValue(mPlayingTime);
        //ref.child("latitude").setValue(mUser.getLatitude());
        ref.child(Constants.LATITUDE).setValue(mUser.getLatitude());
        //ref.child("longitude").setValue(mUser.getLongitude());
        ref.child(Constants.LONGITUDE).setValue(mUser.getLongitude());
        //ref.child("playingPlace").setValue(mUser.getPlayingPlace());
        ref.child(Constants.PLAYING_PLACE).setValue(mUser.getPlayingPlace());
        // ref.child("sport").setValue(mUser.getSport());
        ref.child(Constants.SPORT).setValue(mUser.getSport());

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.USER_INFO);
        editor.commit();
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String userJson = ow.writeValueAsString(mUser);
            editor.putString(Constants.USER_INFO, userJson);
            editor.commit();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // Toast.makeText(getContext(), "Profile has been updated successfully!", Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(), getString(R.string.profile_updated), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sport = (String) parent.getItemAtPosition(position);
        Log.v(LOG_TAG, "selected item" + sport);
        mUser.setSport(sport);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public static class NetworkMessageDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.no_network_header);
            builder.setMessage(R.string.no_network)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NetworkMessageDialogFragment.this.getDialog().cancel();
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
