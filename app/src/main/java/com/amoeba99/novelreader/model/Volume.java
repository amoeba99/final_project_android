package com.amoeba99.novelreader.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Amoeba on 11/20/2017.
 */
@IgnoreExtraProperties
public class Volume {
    private List<VolumeDetail> volumeDetail;

    public List<VolumeDetail> getVolumeDetail() {
        return volumeDetail;
    }

    public void setVolumeDetail(List<VolumeDetail> volumeDetail) {
        this.volumeDetail = volumeDetail;
    }
}
