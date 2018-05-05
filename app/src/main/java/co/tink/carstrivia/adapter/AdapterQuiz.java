package co.tink.carstrivia.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Locale;

import co.tink.carstrivia.R;
import co.tink.carstrivia.activity.MainActivity;
import co.tink.carstrivia.object.POJO;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static co.tink.carstrivia.activity.MainActivity.REQ_CODE_SPEECH_INPUT;

/**
 * Created by Tourdyiev Roman on 30.04.2018.
 */

public class AdapterQuiz extends RecyclerView.Adapter<AdapterQuiz.ViewHolder> {

    private TextToSpeech textToSpeech;
    private Context context;
    private List<POJO> slides;
    private ViewHolder currentHolder;
    private int currentPosition = -1;
    private int[] colors_rand;

    public AdapterQuiz(
            final Context context,
            List<POJO> slides) {
        this.context = context;
        this.slides = slides;
        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("ru"));
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.setPitch(1f);
                    AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
                    am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
                }
            }
        });
        colors_rand = new int[]{
                context.getResources().getColor(R.color.colorAccent),
                context.getResources().getColor(R.color.colorPrimary),
                context.getResources().getColor(R.color.green),
                context.getResources().getColor(R.color.colorPrimaryDark)};
    }

    @Override
    public AdapterQuiz.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_quiz, viewGroup, false);
        return new ViewHolder(view);
    }

    public int getItemCount() {
        return slides.size();
    }

    public void setVoiceInput(String voiceInput) {

        currentHolder.text.setText(voiceInput);

        if (getItem(currentPosition).gettextru().equalsIgnoreCase(voiceInput)
                ||
                getItem(currentPosition).gettext().equalsIgnoreCase(voiceInput)) {

            textToSpeech.speak(context.getResources().getString(R.string.good), TextToSpeech.QUEUE_FLUSH, null);
            currentHolder.konfettiView.build()
                        .addColors(colors_rand)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(16, 6f))
                        .setPosition(
                                currentHolder.konfettiView.getX() + currentHolder.konfettiView.getWidth() / 2,
                                currentHolder.konfettiView.getY() + currentHolder.konfettiView.getHeight() / 3)
                        .burst(100);
            currentHolder.correct.setVisibility(View.VISIBLE);
            currentHolder.correct.setAnimation(fade_in());

        } else {
            textToSpeech.speak(context.getResources().getString(R.string.not_good), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public POJO getItem(int position) {
        return slides.get(position);
    }

    @Override
    public void onBindViewHolder(final AdapterQuiz.ViewHolder holder, int i) {

        View itemView = holder.itemView;


        holder.correct.setVisibility(View.GONE);
        holder.text.setText("");
        holder.symbol.setText(slides.get(holder.getAdapterPosition()).getsymbol());
        Glide.with(context)
                .load(slides.get(holder.getAdapterPosition()).getlogo())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
        holder.card.setCardBackgroundColor(slides.get(holder.getAdapterPosition()).getcolor() != -1 ?
                slides.get(holder.getAdapterPosition()).getcolor() :
                context.getResources().getColor(android.R.color.white));


        holder.listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
                currentHolder = holder;
                currentPosition = holder.getAdapterPosition();
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected ImageView listen;
        protected ImageView correct;
        protected TextView text;
        protected TextView symbol;
        protected CardView card;
        protected KonfettiView konfettiView;

        public ViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.image);
            this.text = view.findViewById(R.id.text);
            this.symbol = view.findViewById(R.id.symbol);
            this.card = view.findViewById(R.id.card);
            this.listen = view.findViewById(R.id.listen);
            this.konfettiView = view.findViewById(R.id.konfetti_view);
            this.correct = view.findViewById(R.id.correct);
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speech_prompt));
        try {
            ((MainActivity) context).startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context, context.getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    public Animation fade_in() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        return animation;
    }
}
