package com.example.marija.mosisproj;

import java.util.List;

/**
 * Created by Marija on 8/26/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}