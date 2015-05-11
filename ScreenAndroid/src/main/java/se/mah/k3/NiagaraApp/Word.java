package se.mah.k3.NiagaraApp;

/**
 * Created by Philip on 2015-05-11.
 */
public class Word {

    Boolean active;
    String text = "";

    public Word(Boolean active, String text) {
        this.active = active;
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
       this.text = text;
    }

    public Boolean getActive(){

        return active;
    }


}
