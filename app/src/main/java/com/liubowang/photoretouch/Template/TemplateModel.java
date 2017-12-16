package com.liubowang.photoretouch.Template;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by heshaobo on 2017/11/30.
 */

public class TemplateModel implements Parcelable {
    public String name;
    public int template;
    public String thumbUrl;

    private TemplateModel(){
        super();
    }

    public TemplateModel(String name,int template,String thumbUrl){
        super();
        this.thumbUrl = thumbUrl;
        this.name = name;
        this.template = template;
    }


    protected TemplateModel(Parcel in) {
        name = in.readString();
        template = in.readInt();
        thumbUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(template);
        dest.writeString(thumbUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TemplateModel> CREATOR = new Creator<TemplateModel>() {
        @Override
        public TemplateModel createFromParcel(Parcel in) {
            return new TemplateModel(in);
        }

        @Override
        public TemplateModel[] newArray(int size) {
            return new TemplateModel[size];
        }
    };
}
