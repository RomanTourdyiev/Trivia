package co.tink.carstrivia.object;

/**
 * Created by Tourdyiev Roman on 30.04.2018.
 */

public class POJO {

    String image;
    String text;
    String textRu;
    String symbol;
    int color=-1;

    public POJO() {

    }

    public POJO(
            String image,
            String text
    ) {
        this.image = image;
        this.text = text;
    }

    public POJO(
            String image,
            String text,
            String textRu
    ) {
        this.image = image;
        this.text = text;
        this.textRu = textRu;
    }

    public POJO(
            String image,
            String symbol,
            String text,
            String textRu
    ) {
        this.image = image;
        this.text = text;
        this.symbol = symbol;
        this.textRu = textRu;
    }

    public POJO(
            String symbol
    ) {
        this.symbol = symbol;
    }

    public POJO(
            int color
    ) {
        this.color = color;
    }

    public POJO(
            int color,
            String text,
            String textRu
    ) {
        this.color = color;
        this.textRu = textRu;
        this.text = text;
    }

    public String getlogo() {
        return image;
    }

    public String gettext() {
        return text;
    }

    public String gettextru() {
        return textRu;
    }

    public String getsymbol() {
        return symbol;
    }

    public int getcolor() {
        return color;
    }

    public void setlogo(String logo) {
        this.image = logo;
    }

    public void settext(String text) {
        this.text = text;
    }

    public void settextru(String textRu) {
        this.textRu = textRu;
    }

    public void setsymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setcolor(int color) {
        this.color = color;
    }
}
