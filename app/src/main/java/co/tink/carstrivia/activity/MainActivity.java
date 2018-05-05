package co.tink.carstrivia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import co.tink.carstrivia.adapter.AdapterQuiz;
import co.tink.carstrivia.adapter.AdapterSlider;
import co.tink.carstrivia.R;
import co.tink.carstrivia.object.POJO;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, CompoundButton.OnCheckedChangeListener {

    private SensorManager sensorManager;
    private AdapterSlider adapterSlider;
    private AdapterQuiz adapterQuiz;

    private RecyclerView sliderRecycler;
    private LinearLayout controls;
    private LinearLayout quizControls;
    private ImageView shake;
    private ImageView back;
    private ImageView backQuiz;
    private ImageView cars;
    private ImageView numbers;
    private ImageView letters;
    private ImageView colors;
    private ImageView animals;
    private ImageView insects;
    private ImageView birds;
    private ImageView quiz;
    private AppCompatCheckBox checkbox0;
    private AppCompatCheckBox checkbox1;
    private AppCompatCheckBox checkbox2;
    private AppCompatCheckBox checkbox3;
    private AppCompatCheckBox checkbox4;
    private AppCompatCheckBox checkbox5;
    private Button start;

    private List<POJO> slides = new ArrayList<>();
    private boolean isSlides = false;
    private boolean isQuiz = false;
    private boolean isQuizSelect = false;
    private boolean isShakable = false;
    private int SHAKE_THRESHOLD = 800;
    private long lastUpdate = 0;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float last_x = 0;
    private float last_y = 0;
    private float last_z = 0;
    public static final int REQ_CODE_SPEECH_INPUT = 10;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cars:
                listCars();
                break;
            case R.id.numbers:
                listNumbers();
                break;
            case R.id.letters:
                listLetters();
                break;
            case R.id.colors:
                listColors();
                break;
            case R.id.animals:
                listAnimals();
                break;
            case R.id.insects:
                listInsects();
                break;
            case R.id.birds:
                listBirds();
                break;
            case R.id.start:
                quiz();
                break;
            case R.id.shake:
                isShakable = !isShakable;
                if (isShakable) {
                    if (sensorManager != null) {
                        sensorManager.registerListener(
                                this,
                                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                SensorManager.SENSOR_DELAY_GAME);
                    }
                } else {
                    if (sensorManager != null) {
                        sensorManager.unregisterListener(this);
                    }
                }
                shake.setImageDrawable(getResources().getDrawable(isShakable ? R.drawable.ic_screen_rotation_white_24dp : R.drawable.ic_screen_lock_rotation_white_24dp));
                break;
        }
        controls.setVisibility(View.GONE);
        quizControls.setVisibility(View.GONE);
        sliderRecycler.setVisibility(View.VISIBLE);
        shake.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        isSlides = true;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        showStart();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            x = event.values[SensorManager.DATA_X];
            y = event.values[SensorManager.DATA_Y];
            z = event.values[SensorManager.DATA_Z];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                shuffle();
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(sliderRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sliderRecycler.setLayoutManager(linearLayoutManager);

        shake.setOnClickListener(this);
        cars.setOnClickListener(this);
        numbers.setOnClickListener(this);
        letters.setOnClickListener(this);
        colors.setOnClickListener(this);
        animals.setOnClickListener(this);
        insects.setOnClickListener(this);
        birds.setOnClickListener(this);
        start.setOnClickListener(this);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizControls.setVisibility(View.VISIBLE);
                isQuizSelect = true;
                showStart();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkbox0.setOnCheckedChangeListener(this);
        checkbox1.setOnCheckedChangeListener(this);
        checkbox2.setOnCheckedChangeListener(this);
        checkbox3.setOnCheckedChangeListener(this);
        checkbox4.setOnCheckedChangeListener(this);
        checkbox5.setOnCheckedChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isQuizSelect) {
            controls.setVisibility(View.VISIBLE);
            quizControls.setVisibility(View.GONE);
            shake.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            sliderRecycler.setVisibility(View.GONE);
            isSlides = false;
            isQuiz = false;
            isQuizSelect = false;
        } else if (isSlides || isQuiz) {
            controls.setVisibility(View.VISIBLE);
            quizControls.setVisibility(View.GONE);
            shake.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            sliderRecycler.setVisibility(View.GONE);
            isSlides = false;
            isQuiz = false;
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (isQuiz) {
                        adapterQuiz.setVoiceInput(result.get(0));
                    }
                }
                break;
            }

        }
    }

    private void initViews() {
        sliderRecycler = findViewById(R.id.slider_recycler);
        controls = findViewById(R.id.controls);
        quizControls = findViewById(R.id.quiz_controls);
        shake = findViewById(R.id.shake);
        cars = findViewById(R.id.cars);
        numbers = findViewById(R.id.numbers);
        letters = findViewById(R.id.letters);
        colors = findViewById(R.id.colors);
        animals = findViewById(R.id.animals);
        insects = findViewById(R.id.insects);
        birds = findViewById(R.id.birds);
        quiz = findViewById(R.id.quiz);
        back = findViewById(R.id.back);
        backQuiz = findViewById(R.id.back_quiz);
        checkbox0 = findViewById(R.id.checkbox_0);
        checkbox1 = findViewById(R.id.checkbox_1);
        checkbox2 = findViewById(R.id.checkbox_2);
        checkbox3 = findViewById(R.id.checkbox_3);
        checkbox4 = findViewById(R.id.checkbox_4);
        checkbox5 = findViewById(R.id.checkbox_5);
        start = findViewById(R.id.start);
    }


    private void listCars() {

        slides = new ArrayList<>();
        getCars();
        list(slides);
    }

    private void listNumbers() {
        slides = new ArrayList<>();
        getNumbers();
        list(slides);
    }

    private void listLetters() {
        slides = new ArrayList<>();
        getLetters();
        list(slides);
    }

    private void listColors() {
        slides = new ArrayList<>();
        getColors();
        list(slides);
    }

    private void listAnimals() {
        slides = new ArrayList<>();
        getAnimals();
        list(slides);
    }

    private void listBirds() {
        slides = new ArrayList<>();
        getBirds();
        list(slides);
    }

    private void listInsects() {
        slides = new ArrayList<>();
        getInsects();
        list(slides);
    }

    private void quiz() {

        isQuiz = true;
        slides = new ArrayList<>();

        if (checkbox0.isChecked()) {
            getNumbers();
        }
        if (checkbox1.isChecked()) {
            getCars();
        }
        if (checkbox2.isChecked()) {
            getColors();
        }
        if (checkbox3.isChecked()) {
            getAnimals();
        }
        if (checkbox4.isChecked()) {
            getBirds();
        }
        if (checkbox5.isChecked()) {
            getInsects();
        }
        listQuiz(slides);

    }

    private void list(List<POJO> slides) {
        adapterSlider = new AdapterSlider(
                this,
                slides);
        sliderRecycler.setAdapter(adapterSlider);
    }

    private void listQuiz(List<POJO> slides) {
        adapterQuiz = new AdapterQuiz(
                this,
                slides);
        sliderRecycler.setAdapter(adapterQuiz);
    }

    private void shuffle() {

        if (slides.size() != 0) {
            Collections.shuffle(slides);
            if (isQuiz) {
                listQuiz(slides);
            } else {
                list(slides);
            }
        }
    }

    private void getCars() {
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLand-Rover-logo-2011-640x335.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Land Rover", "Land Rover"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FPontiac-logo-640x440.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Pontiac", "Pontiac"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMitsubishi-logo-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Mitsubishi", "Mitsubishi"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FVolvo-logo-2012-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Volvo", "Volvo"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FCadillac-logo-2014-640x250.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Cadillac", "Cadillac"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FInfiniti-logo-1989-640x308.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Infiniti", "Infiniti"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FAudi-logo-2009-640x334.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Audi", "Audi"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FKia-logo-640x321.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Kia", "Kia"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMazda-logo-1997-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Mazda", "Mazda"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FChrysler-logo-2010-640x104.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Chrysler", "Chrysler"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FAcura-logo-1990-640x406.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Acura", "Acura"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FGMC-logo-640x145.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "GMC", "GMC"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSubaru-logo-2003-640x358.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Subaru", "Subaru"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FVolkswagen-logo-2015-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Volkswagen", "Volkswagen"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLexus-logo-1988-640x266.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Lexus", "Lexus"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FDodge-logo-1990-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Dodge", "Dodge"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FBMW-logo-2000-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "BMW", "Бэ Эм Вэ"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMercedes-Benz-logo-2011-640x369.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Mercedes-Benz", "Мерседес"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FHyundai-logo-silver-640x401.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Hyundai", "Хёндэ"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FJeep-logo-green-640x258.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Jeep", "Jeep"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FChevrolet-logo-2013-640x281.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Chevrolet", "Шевроле"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FNissan-logo-2013-640x514.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Nissan", "Nissan"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FFord-logo-2003-640x240.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Ford", "Ford"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FToyota-logo-1989-640x524.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Toyota", "Toyota"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FHonda-logo-640x417.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Honda", "Honda"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FOpel-logo-2009-640x496.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Opel", "Опель"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSsangYong-logo-640x422.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Ssangyong", "Ssangyong"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLada-logo-silver-640x248.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Lada", "Lada"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLamborghini-logo-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Lamborghini", "Lamborghini"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FFerrari-logo-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Ferrari", "Ferrari"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FPorsche-logo-2008-640x329.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Porsche", "Porsche"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FFiat-logo-2006-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "FIAT", "Фиат"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FRenault-logo-2015-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Renault", "Рено"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FPeugeot-logo-2010-640x451.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Peugeot", "Пежо"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FBugatti-logo-640x327.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Bugatti", "Bugatti"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FJaguar-logo-2012-640x287.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Jaguar", "Ягуар"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FRolls-Royce-logo-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Rolls Royce", "Rolls Royce"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FBentley-logo-640x324.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Bentley", "Бэнтли"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMcLaren-logo-2002-640x92.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "McLaren", "McLaren"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLotus-logo-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Lotus", "Lotus"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FVauxhall-logo-2008-red-640x478.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Vauxhall", "Во́ксхол"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMaserati-logo-black-640x280.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Maserati", "Maserati"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FAlfa-Romeo-logo-2015-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Alfa Romeo", "Alfa Romeo"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FCitroen-logo-2009-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Citroën", "Citroën"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSkoda-logo-2016-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Skoda", "Шкода"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FKoenigsegg-logo-1994-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Koenigsegg", "Кёнигсег"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMaybach-logo-640x353.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Maybach", "Майбах"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLincoln-logo-1968-640x284.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Lincoln", "Lincoln"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FCorvette-logo-2014-640x431.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Corvette", "Корвэ́т"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FTesla-logo-2003-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Tesla", "Тэсла"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FHummer-logo-2000x205.png?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Hummer", "Hummer"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FDacia-logo-2008-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Dacia", "Дача"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FGeely-logo-2003-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Geely", "Гили"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FDaihatsu-logo-1977-red-1600x1310.png?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Daihatsu", "Дайха́тсу"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FGreat-Wall-logo-2007-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Great wall", "Great wall"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FDMC-logo-640x124.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "DeLorean", "Дэло́риан"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FGAZ-logo-2015-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "GAZ", "Газ"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FLancia-logo-2007-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Lancia", "Лянча"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FUAZ-logo-640x364.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "UAZ", "Уаз"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FZAZ-logo-640x294.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "ZAZ", "Заз"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSmart-logo-1994-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Smart", "Smart"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSEAT-logo-2012-640x508.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Seat", "Сиа́т"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FDaewoo-logo-640x404.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Daewoo", "Дэу"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FChery-logo-old-640x324.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Chery", "Че́ри"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSaab-logo-2013-640x143.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Saab", "Сааб"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FSuzuki-logo-640x425.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Suzuki", "Suzuki"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FKamaz-logo-640x550.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Kamaz", "Kamaz"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FMini-logo-2001-640x270.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Mini", "Mini"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FBuick-logo-2002-640x200.jpg?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "Buick", "Бюик"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/cars%2FGeneral-Motors-logo-2010-3300x3300.png?alt=media&token=b12b39b1-6b1e-4d98-9c86-a797f63ecc7e", "General Motors", "General Motors"));
    }

    private void getNumbers() {
        slides.add(new POJO("", "1", "1", "1"));
        slides.add(new POJO("", "2", "2", "2"));
        slides.add(new POJO("", "3", "3", "3"));
        slides.add(new POJO("", "4", "4", "4"));
        slides.add(new POJO("", "5", "5", "5"));
        slides.add(new POJO("", "6", "6", "6"));
        slides.add(new POJO("", "7", "7", "7"));
        slides.add(new POJO("", "8", "8", "8"));
        slides.add(new POJO("", "9", "9", "9"));
        slides.add(new POJO("", "10", "10", "10"));
    }

    private void getLetters() {
        slides.add(new POJO("", "А", "А", "А"));
        slides.add(new POJO("", "Б", "Б", "Б"));
        slides.add(new POJO("", "В", "В", "В"));
        slides.add(new POJO("", "Г", "Г", "Г"));
        slides.add(new POJO("", "Д", "Д", "Д"));
        slides.add(new POJO("", "Е", "Е", "Е"));
        slides.add(new POJO("", "Ё", "Ё", "Ё"));
        slides.add(new POJO("", "Ж", "Ж", "Ж"));
        slides.add(new POJO("", "З", "З", "З"));
        slides.add(new POJO("", "И", "И", "И"));
        slides.add(new POJO("", "Й", "Й", "Й"));
        slides.add(new POJO("", "К", "К", "К"));
        slides.add(new POJO("", "Л", "Л", "Л"));
        slides.add(new POJO("", "М", "М", "М"));
        slides.add(new POJO("", "Н", "Н", "Н"));
        slides.add(new POJO("", "О", "О", "О"));
        slides.add(new POJO("", "П", "П", "П"));
        slides.add(new POJO("", "Р", "Р", "Р"));
        slides.add(new POJO("", "С", "С", "С"));
        slides.add(new POJO("", "Т", "Т", "Т"));
        slides.add(new POJO("", "У", "У", "У"));
        slides.add(new POJO("", "Ф", "Ф", "Ф"));
        slides.add(new POJO("", "Х", "Х", "Х"));
        slides.add(new POJO("", "Ц", "Ц", "Ц"));
        slides.add(new POJO("", "Ч", "Ч", "Ч"));
        slides.add(new POJO("", "Ш", "Ш", "Ш"));
        slides.add(new POJO("", "Щ", "Щ", "Щ"));
        slides.add(new POJO("", "Ъ", "Ъ", "Ъ"));
        slides.add(new POJO("", "Ы", "Ы", "Ы"));
        slides.add(new POJO("", "Ь", "Ь", "Ь"));
        slides.add(new POJO("", "Э", "Э", "Э"));
        slides.add(new POJO("", "Ю", "Ю", "Ю"));
        slides.add(new POJO("", "Я", "Я", "Я"));
    }

    private void getColors() {
        slides.add(new POJO(Color.parseColor("#FF0000"), "красный", "красный"));
        slides.add(new POJO(Color.parseColor("#FF7F00"), "оранжевый", "оранжевый"));
        slides.add(new POJO(Color.parseColor("#FFFF00"), "жёлтый", "жёлтый"));
        slides.add(new POJO(Color.parseColor("#00ff00"), "зеленый", "зеленый"));
        slides.add(new POJO(Color.parseColor("#87CEEB"), "голубой", "голубой"));
        slides.add(new POJO(Color.parseColor("#0000FF"), "синий", "синий"));
        slides.add(new POJO(Color.parseColor("#4B0082"), "фиолетовый", "фиолетовый"));
        slides.add(new POJO(Color.parseColor("#ffffff"), "белый", "белый"));
        slides.add(new POJO(Color.parseColor("#000000"), "чёрный", "чёрный"));
        slides.add(new POJO(Color.parseColor("#bdbdbd"), "серый", "серый"));
        slides.add(new POJO(Color.parseColor("#A52A2A"), "коричневый", "коричневый"));
        slides.add(new POJO(Color.parseColor("#FFC0CB"), "розовый", "розовый"));
    }

    private void getAnimals() {
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FCat.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "Кот", "Кот"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FDog.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "Собака", "Собака"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FSnake.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "змея", "змея"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FCow.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "корова", "корова"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FBull.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "бык", "бык"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FMouse.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "мышка", "мышка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FTurtle.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "черепаха", "черепаха"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FFish.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "рыбка", "рыбка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FRabbit.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "кролик", "кролик"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FSheep.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "овечка", "овечка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FGoat.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "козёл", "козёл"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FHorse.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "лошадь", "лошадь"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FFox.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "лиса", "лиса"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FSquirrel.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "белка", "белка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FKoala.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "коала", "коала"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FBear.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "медведь", "медведь"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FFrog.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "лягушка", "лягушка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FLizard.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "ящерица", "ящерица"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FLion.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "лев", "лев"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FTiger.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "тигр", "тигр"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FPanda.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "панда", "панда"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FMonkey.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "обезьяна", "обезьяна"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FElephant.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "слон", "слон"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FGiraffe.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "жираф", "жираф"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FLeopard.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "леопард", "леопард"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FZebra.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "зебра", "зебра"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FDeer.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "олень", "олень"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/animals%2FMoose.jpg?alt=media&token=5c928afe-3728-4819-8a3d-6450ca54c39b", "лось", "лось"));
    }

    private void getBirds() {
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/birds%2FTurkey.jpg?alt=media&token=3a60dd07-df8b-4591-b9c4-3a7c5ebb977d", "Индюк", "Индюк"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/birds%2FGoose.jpg?alt=media&token=3a60dd07-df8b-4591-b9c4-3a7c5ebb977d", "Гусь", "Гусь"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/birds%2FDuck.jpg?alt=media&token=3a60dd07-df8b-4591-b9c4-3a7c5ebb977d", "Утка", "Утка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/birds%2FParrot.jpg?alt=media&token=3a60dd07-df8b-4591-b9c4-3a7c5ebb977d", "Попугай", "Попугай"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/birds%2FRooster.jpg?alt=media&token=3a60dd07-df8b-4591-b9c4-3a7c5ebb977d", "Петух", "Петух"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/birds%2FChicken.jpg?alt=media&token=3a60dd07-df8b-4591-b9c4-3a7c5ebb977d", "Курица", "Курица"));
    }

    private void getInsects() {
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FSpider.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Паук", "Паук"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FScorpion.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Скорпион", "Скорпион"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FAnt.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Муравей", "Муравей"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FCockroach.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Таракан", "Таракан"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FButterfly.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Бабочка", "Бабочка"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FMantisReligiosa.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Богомол", "Богомол"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FScarab.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Скарабей", "Скарабей"));
        slides.add(new POJO("https://firebasestorage.googleapis.com/v0/b/trivia-725e6.appspot.com/o/insects%2FBee.jpg?alt=media&token=94865562-5537-471e-9185-3eeea1465c40", "Пчела", "Пчела"));
    }

    private void showStart() {
        start.setVisibility(
                !checkbox0.isChecked()
                        &&
                        !checkbox1.isChecked()
                        &&
                        !checkbox2.isChecked()
                        &&
                        !checkbox3.isChecked()
                        &&
                        !checkbox4.isChecked()
                        &&
                        !checkbox5.isChecked() ? View.GONE : View.VISIBLE);
    }
}
