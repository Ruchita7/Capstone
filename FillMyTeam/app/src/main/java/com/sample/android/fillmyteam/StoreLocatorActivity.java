package com.sample.android.fillmyteam;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.sample.android.fillmyteam.api.RestService;
import com.sample.android.fillmyteam.model.LocationResponse;
import com.sample.android.fillmyteam.model.Photo;
import com.sample.android.fillmyteam.model.Result;
import com.sample.android.fillmyteam.model.StoreLocatorParcelable;
import com.sample.android.fillmyteam.model.User;
import com.sample.android.fillmyteam.ui.DividerItemDecoration;
import com.sample.android.fillmyteam.util.Constants;
import com.sample.android.fillmyteam.util.Utility;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class StoreLocatorActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    double mLatitude;
    double mLongitude;

    GoogleApiClient mGoogleApiClient;
    AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private TextView mPlaceDetailsText;
   // SportsStoreLocatorFragment mFragment;

    String mPlaceId;
    //ListView mListView;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    StoreLocatorParcelable mStoreLocator;
    List<StoreLocatorParcelable> mStoreLocatorParcelables;
    StoreLocatorAdapter mStoreLocatorAdapter;
    public static final String LOG_TAG = SportsStoreLocatorFragment.class.getSimpleName();
    public static final String SEARCHED_STORE = "searched_store";
    public static final String SELECTED_ITEM = "selected_item";
    Parcelable mIndex;
    TextView emptyList;
    private boolean mAutoSelectView;
    private int mChoiceMode;
    int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_locator);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.USER_DETAILS)) {
                User user = (User) getIntent().getSerializableExtra(Constants.USER_DETAILS);
                mLatitude = user.getLatitude();
                mLongitude = user.getLongitude();
            }
        }

            mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
            mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_places);

            emptyList = (TextView)findViewById(R.id.listview_store_empty);
            mRecyclerView = (RecyclerView) findViewById(R.id.store_locator_list_view);

            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
            ImageView imageView = (ImageView) findViewById(R.id.powered_by_google);

            if (!Utility.checkNetworkState(this)) {
                mAutocompleteView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                emptyList.setVisibility(View.VISIBLE);
            } else {
                mAutocompleteView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        //Log.v(LOG_TAG, "in onEditorAction");
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Utility.hideSoftKeyboard(StoreLocatorActivity.this);
                            //   mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
                            return true;
                        }
                        return false;
                    }
                });
                //  final TextView text1 = (TextView) view.findViewById(R.id.text1);

                mAutocompleteView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Log.v(LOG_TAG, "in onItemSelected");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //Log.v(LOG_TAG, "in onNothingSelected");
                    }
                });
                // Register a listener that receives callbacks when a suggestion has been selected
                mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

                // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
                // the entire world.
                mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                        null, this);
                mAutocompleteView.setAdapter(mAdapter);

                mLayoutManager = new LinearLayoutManager(this);
                mStoreLocatorParcelables = new ArrayList<StoreLocatorParcelable>();
            }

            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(SEARCHED_STORE)) {
                    mPlaceId = savedInstanceState.getString(SEARCHED_STORE);
                    if (mPlaceId != null) {
                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, mPlaceId);
                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                        if (savedInstanceState.containsKey(SELECTED_ITEM)) {
                            mIndex = savedInstanceState.getParcelable(SELECTED_ITEM);
                            //mStoreLocatorAdapter.onRestoreInstanceState(savedInstanceState);
                            mLayoutManager.onRestoreInstanceState(mIndex);
                        }
                    }
                }
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            mGoogleApiClient.disconnect();
        }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            emptyList.setVisibility(View.GONE);
            final AutocompletePrediction item = mAdapter.getItem(position);
            mPlaceId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            mStoreLocatorParcelables = new ArrayList<StoreLocatorParcelable>();
            Utility.hideSoftKeyboard(StoreLocatorActivity.this);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, mPlaceId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            //Log.i(LOG_TAG, "Called getPlaceById to get Place details for " + mPlaceId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            emptyList.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            final Place place = places.get(0);
            LatLng placeLatLng = place.getLatLng();
            String placeName = place.getName().toString();
            retrieveStoreResult(placeName);
            places.release();
        }
    };

    private void retrieveStoreResult(String placeName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.STORE_LOCATOR_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RestService service = retrofit.create(RestService.class);
        placeName = placeName.replace(" ", "+");
        Call<LocationResponse> response = service.retrieveSportsStores(this.getString(R.string.sport_goods_query) + placeName, Constants.GOOGLE_MAPS_KEY);
        response.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Response<LocationResponse> response, Retrofit retrofit) {
                LocationResponse locationResponse = response.body();
                if (locationResponse == null) {
                    ResponseBody responseErrBody = response.errorBody();
                    //  response.code()

                    if (responseErrBody != null) {

                        try {
                            updateEmptyView(response.code());
                            String str = responseErrBody.string();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.getMessage());
                            return;
                        }
                    }
                }
                List<Result> resultList = locationResponse.getResults();
                String name;
                String address;
                String photoReference = null;
                double latitude;
                double longitude;
                List<Photo> photos;
                StoreLocatorParcelable storeLocatorParcelable = null;
                for (Result result : resultList) {
                    name = result.getName();
                    photoReference = "";
                    address = result.getFormatted_address();
                    latitude = result.getGeometry().getLocation().getLat();
                    longitude = result.getGeometry().getLocation().getLng();
                    photos = result.getPhotos();
                    for (Photo photo : photos) {
                        if (photo.getPhoto_reference() != null) {
                            photoReference = photo.getPhoto_reference();
                            break;
                        }
                    }
                    storeLocatorParcelable = new StoreLocatorParcelable(name, address, latitude, longitude, photoReference);
                    mStoreLocatorParcelables.add(storeLocatorParcelable);
                }
                mStoreLocatorAdapter = new StoreLocatorAdapter(StoreLocatorActivity.this, mStoreLocatorParcelables, emptyList, mChoiceMode);

                mRecyclerView.setAdapter(mStoreLocatorAdapter);
                mStoreLocatorAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        //  Log.e(LOG_TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        Toast.makeText(this,
                getString(R.string.google_api_client_connect_error, connectionResult.getErrorCode()),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionSuspended(int i) {
        // Log.e(LOG_TAG, "onConnectionSuspended: ConnectionResult.getErrorCode() = " + i);
        Toast.makeText(this,
                getString(R.string.google_api_client_connect_error),
                Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlaceId != null) {
            outState.putString(SEARCHED_STORE, mPlaceId);
            outState.putParcelable(SELECTED_ITEM, mLayoutManager.onSaveInstanceState());
        }
    }

    public void updateEmptyView(int statusCode) {
        int message = 0;
        switch (statusCode) {
            case CommonStatusCodes.API_NOT_CONNECTED:
                message = R.string.api_not_connected;
                break;
            case CommonStatusCodes.CANCELED:
            case CommonStatusCodes.ERROR:
                message = R.string.location_error;
                break;

            case CommonStatusCodes.NETWORK_ERROR:
                message = R.string.location_data_unavailable;
                break;
            case CommonStatusCodes.TIMEOUT:
                message = R.string.timeout;
                break;

            case PlaceAutocompleteAdapter.NO_RESULTS_FOUND:
                message = R.string.no_location_found;
                break;

            case HttpURLConnection.HTTP_NO_CONTENT:
                message = R.string.invalid_request_error;
                break;

            case HttpURLConnection.HTTP_BAD_REQUEST:
                message = R.string.empty_store_list_server_down;
                break;


            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                message = R.string.empty_store_list_server_error;
                break;
            default:
                if (!Utility.checkNetworkState(this)) {
                    message = R.string.store_network_unavailable;
                }

        }
        if (message != 0) {
            emptyList.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            emptyList.setText(message);
        }
    }
}
