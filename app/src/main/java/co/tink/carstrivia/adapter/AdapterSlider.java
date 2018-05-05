package co.tink.carstrivia.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Locale;

import co.tink.carstrivia.R;
import co.tink.carstrivia.object.POJO;

/**
 * Created by Tourdyiev Roman on 30.04.2018.
 */

public class AdapterSlider extends RecyclerView.Adapter<AdapterSlider.ViewHolder> {

    private TextToSpeech textToSpeech;
    private Context context;
    private List<POJO> slides;

    public AdapterSlider(
            final Context context,
            List<POJO> slides) {
        this.context = context;
        this.slides = slides;
        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("ru"));
                    textToSpeech.setSpeechRate(0.7f);
                    textToSpeech.setPitch(0f);
                    AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
                    am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
                }
            }
        });
        Log.d("birds",slides.size()+"");
    }

    @Override
    public AdapterSlider.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_slider, viewGroup, false);
        return new ViewHolder(view);
    }

    public int getItemCount() {
        return slides.size();
    }

    @Override
    public void onBindViewHolder(final AdapterSlider.ViewHolder holder, int i) {
        View itemView = holder.itemView;
        holder.text.setText(slides.get(holder.getAdapterPosition()).gettext());
        holder.symbol.setText(slides.get(holder.getAdapterPosition()).getsymbol());
        Glide.with(context)
                .load(slides.get(holder.getAdapterPosition()).getlogo())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
        holder.card.setCardBackgroundColor(slides.get(holder.getAdapterPosition()).getcolor() != -1 ?
                slides.get(holder.getAdapterPosition()).getcolor() :
                context.getResources().getColor(android.R.color.white));
        holder.speak.setVisibility(slides.get(holder.getAdapterPosition()).gettext() != null ? View.VISIBLE : View.GONE);
        holder.speak.setVisibility(slides.get(holder.getAdapterPosition()).gettextru() != null ? View.VISIBLE : View.GONE);
        if (slides.get(holder.getAdapterPosition()).getcolor() != -1) {
            textToSpeech.setSpeechRate(1.0f);
        } else {
            textToSpeech.setSpeechRate(0.7f);
        }
        if (slides.get(holder.getAdapterPosition()).gettext() != null || slides.get(holder.getAdapterPosition()).gettextru() != null) {
            holder.speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (slides.get(holder.getAdapterPosition()).gettextru() != null) {
                        textToSpeech.speak(slides.get(holder.getAdapterPosition()).gettextru(), TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        textToSpeech.speak(slides.get(holder.getAdapterPosition()).gettext(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected ImageView speak;
        protected TextView text;
        protected TextView symbol;
        protected CardView card;

        public ViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.image);
            this.text = view.findViewById(R.id.text);
            this.symbol = view.findViewById(R.id.symbol);
            this.card = view.findViewById(R.id.card);
            this.speak = view.findViewById(R.id.speak);
        }
    }
}
