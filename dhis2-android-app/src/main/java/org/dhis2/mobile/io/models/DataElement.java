package org.dhis2.mobile.io.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;



/**
 * Created by xelvias on 2/6/18.
 */

public class DataElement implements Serializable, Parcelable {

    public static final String TAG = "org.dhis.mobile.io.models.DataElement";

    private String id;

    protected DataElement(Parcel in) {
        id = in.readString();
    }

    public static final Creator<DataElement> CREATOR = new Creator<DataElement>() {
        @Override
        public DataElement createFromParcel(Parcel in) {
            return new DataElement(in);
        }

        @Override
        public DataElement[] newArray(int size) {
            return new DataElement[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
    }
}
