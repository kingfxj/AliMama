package com.example.alimama.addEditMood;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import com.example.alimama.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * This is the class for AddEditMood activity that is used for when a user wants to add or view/edit their selected mood.
 */
public class AddEditMoodActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, OnMapReadyCallback, AddEditMoodContract.View {

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int LOCATION_REQUEST_CODE = 1;



    public static final String EXTRA_USERNAME = "USERNAME";
    public static final String EXTRA_DOCUMENT_ID = "document_id";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_EMOTIONAL_STATE = "emotional_state";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_PHOTO_PATH = "photo_path";
    public static final String EXTRA_LOCATION_LAT = "location_lat";
    public static final String EXTRA_LOCATION_LNG = "location_lng";
    public static final String EXTRA_EMOTICON = "emoticon";
    public static final String EXTRA_SOCIAL_SITUATION = "social_situation";


    private TextView tvTitle;
    private TextView tvEmotionalState;
    private EditText etDatePicker;
    private EditText etTimePicker;
    private EditText etDescription;
    private ImageView ivThumbnail;
    private ImageButton ibCamera;
    private MapView mapView;
    private CheckBox checkBoxLocation;
    private Button btnAddMood;
    private Spinner spSocalSituation;
    private Spinner spEmoticon;

    private GoogleMap googleMap;

    String currentPhotoPath;

    private AddEditMoodPresenter presenter;

    // BEGIN - Android lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_mood);

        String username = getIntent().getStringExtra(EXTRA_USERNAME);

        setupViews();
        presenter = new AddEditMoodPresenter(this);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        setupSocialSituationList();
        setupEmoticonsList();

        String documentId = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);

        String dateStr = getIntent().getStringExtra(EXTRA_DATE);
        String emotionalState = getIntent().getStringExtra(EXTRA_EMOTIONAL_STATE);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String photoPath = getIntent().getStringExtra(EXTRA_PHOTO_PATH);

        final String emoticon = getIntent().getStringExtra(EXTRA_EMOTICON);
        final String socialSituation = getIntent().getStringExtra(EXTRA_SOCIAL_SITUATION);

        double locationLat = getIntent().getDoubleExtra(EXTRA_LOCATION_LAT, 0);
        double locationLng = getIntent().getDoubleExtra(EXTRA_LOCATION_LNG, 0);
        GeoPoint geoPoint = new GeoPoint(locationLat, locationLng);

        spEmoticon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                tvEmotionalState.setText(getEmoticonText(spEmoticon.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        presenter.onAddEditMoodViewCreated(documentId,
                username,
                dateStr,
                emotionalState,
                description,
                photoPath,
                emoticon,
                socialSituation,
                geoPoint);

        etDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        etTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        btnAddMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAddMoodButtonClicked(etDatePicker.getText().toString(),
                        etTimePicker.getText().toString(),
                        tvEmotionalState.getText().toString(),
                        etDescription.getText().toString(),
                        spEmoticon.getSelectedItem().toString(),
                        spSocalSituation.getSelectedItem().toString(),
                        checkBoxLocation.isChecked());
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        presenter.onGoogleMapReady();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            presenter.setPhotoPath(currentPhotoPath);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    getCheckPremissionsAndGetPhoneLocation();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // END - Android lifecycle methods


    // BEGIN - AddEditMoodContract.View methods

    //when we switch to edit mood instead of add mood
    @Override
    public void setEditModeState() {
        tvTitle.setText("Edit Mood");
        btnAddMood.setText("Edit Mood");
    }

    @Override
    public void setDate(String date) {
        etDatePicker.setText(date);
    }

    @Override
    public void setTime(String time) {
        etTimePicker.setText(time);
    }

    @Override
    public void setEmotionalState(String emotionalState) {
        tvEmotionalState.setText(emotionalState);
    }

    @Override
    public void setDescription(String description) {
        etDescription.setText(description);
    }

    @Override
    public void setThumbnail(String photoPath) {
        Glide.with(this).load(photoPath).into(ivThumbnail);
    }

    @Override
    public void setEmoticon(final String emoticon) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                spEmoticon.setSelection(getEmoticonPosition(emoticon));
            }
        });
    }

    @Override
    public void setSocialSituation(final String socialSituation) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                spSocalSituation.setSelection(getSocialSituationPosition(socialSituation));
            }
        });
    }

    @Override
    public void setLocation(GeoPoint geoPoint) {
        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
    }

    @Override
    public void getCheckPremissionsAndGetPhoneLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);

            return;
        }
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        if (location != null) {
            presenter.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }
    }

    //error validation messages when a date or time is not selected or when the description should be less than 20 characters
    @Override
    public void showDateError() {
        etDatePicker.setError("Field is required");
    }

    @Override
    public void showTimeError() {
        etTimePicker.setError("Field is required");
    }

    @Override
    public void showDescriptionError() {
        etDescription.setError("Description should be less than 20 characters");
    }

    @Override
    public void clearDateError() {
        etDatePicker.setError(null);
    }

    @Override
    public void clearTimeError() {
        etTimePicker.setError(null);

    }

    // END - AddEditMoodContract.View methods

    // BEGIN - Date and Time picker methods

    //date and time picker dialogs used to select a required date and time
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        etDatePicker.setText(String.format(Locale.CANADA, "%d-%d-%d", year, month + 1, day));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        etTimePicker.setText(String.format(Locale.CANADA, "%d:%d", hourOfDay, minute));
    }

    // END - Date and Time picker methods


    // BEGIN - Helper methods

    private void setupViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvEmotionalState = findViewById(R.id.tvEmotionalState);
        etDatePicker = findViewById(R.id.etDate);
        etTimePicker = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        ivThumbnail = findViewById(R.id.ivThumbnail);
        ibCamera = findViewById(R.id.ibCamera);
        mapView = findViewById(R.id.mapView);
        checkBoxLocation = findViewById(R.id.checkBoxLocation);
        btnAddMood = findViewById(R.id.btnAddMood);
        spSocalSituation = findViewById(R.id.spSocialSituation);
        spEmoticon = findViewById(R.id.spEmoticon);
    }


    private void setupSocialSituationList() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.social_situation_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSocalSituation.setAdapter(adapter);
    }


    private void setupEmoticonsList() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.emoticons_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmoticon.setAdapter(adapter);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating photo file.", Toast.LENGTH_SHORT);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.alimama.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private int getEmoticonPosition(String value) {
        switch (value) {
            case "\uD83D\uDE0A":
                return 0;
            case "\uD83D\uDE02":
                return 1;
            case "\uD83D\uDE0D":
                return 2;
            case "\uD83D\uDE21":
                return 3;
            case "\uD83D\uDE1C":
                return 4;
            case "\uD83D\uDE22":
                return 5;
            case "\uD83D\uDE0F":
                return 6;
        }
        return -1;
    }

    private String getEmoticonText(String value) {
        switch (value) {
            case "\uD83D\uDE0A":
                return "happy";
            case "\uD83D\uDE02":
                return "tears";
            case "\uD83D\uDE0D":
                return "heart";
            case "\uD83D\uDE21":
                return "angry";
            case "\uD83D\uDE1C":
                return "tongue";
            case "\uD83D\uDE22":
                return "cry";
            case "\uD83D\uDE0F":
                return "smirk";
        }
        return "";
    }

    private int getSocialSituationPosition(String value) {
        switch (value) {
            case "alone":
                return 0;
            case "with one other person":
                return 1;
            case "with two to several ppl":
                return 2;
            case "with a crowd":
                return 3;
        }
        return -1;
    }

    // END - Helper methods
}