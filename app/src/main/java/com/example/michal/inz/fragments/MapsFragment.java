package com.example.michal.inz.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.michal.inz.Location;
import com.example.michal.inz.MyMapView;
import com.example.michal.inz.R;
import com.example.michal.inz.networking.BluetoothConnectionService;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Cap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Join;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.util.Utils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import org.oscim.core.GeoPoint;
import org.oscim.layers.PathLayer;
import org.oscim.map.Map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements FragmentName {

    private static final String area = "poland-latest";
    private MyMapView mapView;
    public FragmentActivity activity;
    public View view;
    Location myLocation = null;
    Marker myLocationMarker;
    Button yourLocationBtn;
    Button navigateBtn;
    ImageButton myDot;
    GraphHopper graphHopper;
    GeoPoint start;
    GeoPoint end;
    private boolean choosingLocation = false;
    private boolean choosedLocation = false;
    private boolean prepareFinished = false;
    File myFolder;
    PathLayer myRoute;
    private boolean choosingLocationSecond = false;
    Marker endMarker = null;
    Polyline line = null;

    private StatsUpdateReceiver mStatsUpdateReceiver;
    private IntentFilter mFilter;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myLocation = new Location(getContext(), this);

        AndroidGraphicFactory.createInstance(getActivity().getApplication());
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = view.findViewById(R.id.openmapview);
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setParentFragment(this);

        setButtons();

        mFilter = new IntentFilter(BluetoothConnectionService.STATS_UPDATE_INTENT);

        mStatsUpdateReceiver = new StatsUpdateReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStatsUpdateReceiver, mFilter);

        try {

            myFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/graphhopper/maps/");
            if (!myFolder.exists())
                myFolder.mkdirs();

            File mapFile = new File(myFolder, area +"-gh/" +  area + ".map");
            Log.d("map_fragment", String.valueOf(mapFile.exists()));
            Bitmap markerImg = null;
            markerImg = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.location_icon));

            MapDataStore mapDataStore = new MapFile(mapFile);
            TileCache tileCache = AndroidUtil.createTileCache(this.getActivity(), "fragments",
                    this.mapView.getModel().displayModel.getTileSize(), 1.0f, 1.5);

            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(new ExternalRenderTheme(new File(myFolder, "theme.xml")));

            myLocationMarker = new Marker(new LatLong(52.517037, 18.38886), markerImg, 0, 0);
            this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
            this.mapView.getLayerManager().getLayers().add(myLocationMarker);

            mapView.setCenter(new LatLong(52.517037, 18.38886));
            mapView.setZoomLevel((byte) 18);

            ///////////////////////////////////

            new AsyncTask<Void, Void, Path>() {

                protected Path saveDoInBackground(Void... voids){
                    try {
                        GraphHopper tmpHopp = new GraphHopper().forMobile();
                        tmpHopp.load(new File(myFolder, area).getAbsolutePath() + "-gh");
                        graphHopper = tmpHopp;
                        return null;
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected Path doInBackground(Void... voids) {
                    saveDoInBackground(voids);
                    return null;
                }

                protected void onPostExecute(Path o) {
                    finishPrepare();
                }
            }.execute();
            ///

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mStatsUpdateReceiver);
        super.onPause();
    }

    private void finishPrepare() {
        prepareFinished = true;
    }

    private void setButtons() {
        yourLocationBtn = view.findViewById(R.id.button);
        yourLocationBtn.setVisibility(View.GONE);
        yourLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.centerLock = false;
                yourLocationBtn.setVisibility(View.GONE);
                updateLocation();
            }
        });


        myDot = view.findViewById(R.id.radioButton);
        myDot.setVisibility(View.GONE);
        navigateBtn = view.findViewById(R.id.buttonNavigate);
        navigateBtn.setVisibility(View.VISIBLE);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prepareFinished) {
                    if (!choosingLocation && !choosedLocation) {
                        chosePointOnMap();
                    } else if (!choosingLocation && choosedLocation) {
                        removeRoute();
                        choosedLocation = false;
                        navigateBtn.setText("Nawiguj");
                    }
                    else if (choosingLocation) {
                        choosePoint();
                        choosingLocation = false;
                        addEndMarker(end);
                        navigateBtn.setText("Wyznaczanie");
                        createPath(start, end);
                        choosedLocation = true;
                    }
                }
            }
        });
    }

    private void removeRoute() {
        mapView.getLayerManager().getLayers().remove(endMarker);
        mapView.getLayerManager().getLayers().remove(line);
    }

    private void addEndMarker(GeoPoint end) {
        Bitmap markerImg = null;
        markerImg = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.location_icon_end));
        endMarker = new Marker(new LatLong(end.getLatitude(), end.getLongitude()), markerImg, 0, 0);
        mapView.getLayerManager().getLayers().add(endMarker);
    }

    private void createPath(final GeoPoint start, final GeoPoint end) {

        new AsyncTask<Void, Void, PathWrapper>() {
            float time;

            protected PathWrapper doInBackground(Void... v) {
                StopWatch stopWatch = new StopWatch().start();
                GHRequest ghRequest = new GHRequest(start.getLatitude(), start.getLongitude(),
                        end.getLatitude(), end.getLongitude()).
                        setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);

                ghRequest.getHints().put(Parameters.Routing.INSTRUCTIONS, "false");
                GHResponse ghResponse = graphHopper.route(ghRequest);
                time = stopWatch.stop().getSeconds();
                return ghResponse.getBest();
            }

            protected void onPostExecute(PathWrapper ghResponse) {
                if (!ghResponse.hasErrors()) {
                    drawPath(ghResponse);
                    navigateBtn.setText("Nowa trasa");
                }
            }
        }.execute();


    }

    private void drawPath(PathWrapper ghResponse) {

        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();

        paintStroke.setStyle(Style.STROKE);
        paintStroke.setStrokeJoin(Join.ROUND);
        paintStroke.setStrokeCap(Cap.ROUND);
        paintStroke.setColor(Color.GREEN);
        paintStroke.setStrokeWidth(7);

        line = new Polyline(paintStroke,
                AndroidGraphicFactory.INSTANCE);

        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = ghResponse.getPoints();
        for (int i = 0; i < ghResponse.getPoints().getSize(); i++) {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));
        }
        mapView.getLayerManager().getLayers().add(line);
    }

    private void choosePoint() {
        choosingLocationSecond = true;
        GeoPoint p = new GeoPoint(mapView.getModel().mapViewPosition.getMapPosition().latLong.latitude,
                mapView.getModel().mapViewPosition.getMapPosition().latLong.longitude);
        end = p;
        start = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        myDot.setVisibility(View.GONE);
    }

    private void chosePointOnMap() {
        choosingLocation = true;
        myDot.setVisibility(View.VISIBLE);
        navigateBtn.setText("Nawiguj tutaj");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocation();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStatsUpdateReceiver, mFilter);
    }

    @Override
    public String getName() {
        return "Maps";
    }

    public void updateLocation() {
        if (!mapView.centerLock) {
            mapView.setZoomLevel((byte) 18);
            mapView.setCenter(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
        }
        else if (yourLocationBtn.getVisibility() == View.GONE){
            yourLocationBtn.setVisibility(View.VISIBLE);
        }
        if (myLocationMarker != null){
            myLocationMarker.setLatLong(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
            myLocationMarker.setVisible(true);
        }
    }

    public class StatsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothConnectionService.STATS_UPDATE_INTENT)) {
                updateStats(intent);
            }
        }
    }

    private void updateStats(Intent intent) {
        int speed, rpm = -1;
        TextView speedTv = view.findViewById(R.id.speedTV);
        TextView rpmTv = view.findViewById(R.id.rpmTV);

        try {
            speed = intent.getIntExtra(BluetoothConnectionService.SPEED_TAG, 0);
            speedTv.setText(Integer.toString(speed) + "km/h");
        } catch (Exception e) {
            speed = 0;
            speedTv.setText(Integer.toString(speed));
        }

        try {
            rpm = intent.getIntExtra(BluetoothConnectionService.RPM_TAG, 0);
            rpmTv.setText(Integer.toString(rpm) + " rpm");
        } catch (Exception e) {
            rpm = 0;
            rpmTv.setText(Integer.toString(rpm));
        }

    }

}
