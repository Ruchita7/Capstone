package com.android.fillmyteam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.fillmyteam.model.User;
import com.android.fillmyteam.util.Constants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SportsInfoFragment.Callback {

    public static final String SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";


    // AddressResultReceiver mResultReceiver;
    // GoogleApiClient mGoogleApiClient;
    String mAddressOutput = "";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    User mUser;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //  FirebaseAuth mAuth;
    boolean isDrawerLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("User Credentials")) {
            mUser = (User) getIntent().getSerializableExtra("User Credentials");
            Log.v(LOG_TAG, mUser.getEmail() + "," + mUser.getName() + "," + mUser.getPhotoUrl());
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", mUser.getEmail());
            editor.commit();
        }
    /*    mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

     /*   mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
          //      .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/


        //  editor.putString("email", "poomah29@gmail,com");
        // editor.putString("email", "ruchita,maheshwary@gmail,com");

        //mResultReceiver = new AddressResultReceiver(new Handler());

        //   mResultReceiver.setReceiver(this);
      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
     /*   if (isTablet()) {
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawer.setScrimColor(Color.TRANSPARENT);
            isDrawerLocked = true;
        }
*/
 /*       mUser = new User();
        mUser.setLongitude(28.7514586);
        mUser.setLatitude(77.0994467);
        mUser.setEmail("android.studio@android.com");
        mUser.setName("Android Studio");*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                // getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //    getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mUser != null) {
            View navigationHeader = navigationView.inflateHeaderView(R.layout.nav_header_main);
            TextView userTextView = (TextView) navigationHeader.findViewById(R.id.userNameTextView);
            TextView emailTextView = (TextView) navigationHeader.findViewById(R.id.emailTextView);
            ImageView userPhotoImageView = (ImageView) navigationHeader.findViewById(R.id.profileImageView);
            userTextView.setText(mUser.getName());
            emailTextView.setText(mUser.getEmail());
            if(mUser.getPhotoUrl()!=null &&!mUser.getPhotoUrl().isEmpty())
            {
                Picasso.with(this).load(mUser.getPhotoUrl()).into(userPhotoImageView);
            }
        }
        //   setupDrawerContent(navigationView);
        //   new GcmRegistrationAsyncTask(this).execute();

    /*    if (checkPlayServices()) {
      //      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            if (!sentToken) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }*/

     /*   FirebaseOptions options = null;

        try {
            options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("/Development/Notifications/My-Notification_Server-serviceAccountCredentials.json"))
                    .setDatabaseUrl("https://my-notification-server.firebaseio.com/")
                    .build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

    }

 /*   private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
*/

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // Fragment fragment = null;
        Class fragmentClass = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
       /* if (id == R.id.learn_play) {
            //   fragmentClass = SportsInfoFragment.class;
            SportsInfoFragment sportsInfoFragment = SportsInfoFragment.newInstance(mLatitude, mLongitude);
            fragmentManager.beginTransaction().replace(R.id.content_frame, sportsInfoFragment).commit();
        } else if (id == R.id.find_playmates) {
            //   fragmentClass = FindPlaymatesFragment.class;
            FindPlaymatesFragment findPlaymatesFragment = FindPlaymatesFragment.newInstance(mLatitude, mLongitude);
            fragmentManager.beginTransaction().replace(R.id.content_frame, findPlaymatesFragment).commit();
        } else if (id == R.id.edit_profile) {
            EditProfileFragment editProfileFragmentFragment = EditProfileFragment.newInstance(mLatitude, mLongitude);
            fragmentManager.beginTransaction().replace(R.id.content_frame, editProfileFragmentFragment).commit();
        }*/


        switch (id) {

            case R.id.learn_play:
                //  fragment = (SportsInfoFragment) SportsInfoFragment.newInstance(mUser.getLatitude(), mUser.getLongitude());
                fragment = (SportsInfoFragment) SportsInfoFragment.newInstance(mUser.getLatitude(), mUser.getLongitude());
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.find_playmates:
                fragment = (FindPlaymatesFragment) FindPlaymatesFragment.newInstance(mUser);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.edit_profile:
                fragment = (EditProfileFragment) EditProfileFragment.newInstance(mUser);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.sports_store_locator:
                fragment = (SportsStoreLocatorFragment) SportsStoreLocatorFragment.newInstance(mUser.getLatitude(), mUser.getLongitude());
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.logout:
                logoutUser();
                break;
        }

     /*   try {
            fragment = (Fragment) fragmentClass.newInstance(mAddressOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // Insert the fragment by replacing any existing fragment
        //   FragmentManager fragmentManager = getSupportFragmentManager();
        //   fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        //mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    private void logoutUser() {
     /*   mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                       // updateUI(null);
                        Intent intent = new Intent(getApplicationContext(),GoogleSignInActivity.class);
                        startActivity(intent);
                    }
                });*/
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
        startActivity(intent);
    }

 /*   protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }*/



 /*   class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //  displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                // showToast(getString(R.string.address_found));
                Log.v(LOG_TAG, "address found" + mAddressOutput);
        *//*    if(mReceiver!=null) {
                mReceiver.onReceiveResult(resultCode, resultData);
            }*//*
            }
        }

   *//* @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("Main Activity","received result from Service="+resultData.getString(Constants.RESULT_DATA_KEY));
    }*//*
    }*/

 /*   public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.v(LOG_TAG, "This device is not supported");
                finish();
                ;
            }
            return false;
        }
        return true;
    }*/


    @Override
    public void onItemSelected(String sportId, SportsInfoAdapter.InfoViewHolder vh) {
        /*if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailFragment.DETAIL_URI, dateUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {*/

       /* Bundle args = new Bundle();
        args.putString("Id", sportId);
        SportsDetailFragment fragment = new SportsDetailFragment();
        fragment.setArguments(args);*/
        SportsDetailFragment fragment = SportsDetailFragment.newInstance(sportId);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                //     .addToBackStack(getResources().getString(R.string.book_detail))         //used string resource
                .commit();
       /* Intent intent = new Intent(this, SportsDetailActivity.class)
                .setData(dateUri);


        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, new Pair<View, String>(vh.sportsImage, getString(R.string.detail_icon_transition_name)));
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}