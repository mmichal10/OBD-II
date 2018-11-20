package com.example.michal.inz.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.michal.inz.Location;
import com.example.michal.inz.R;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements FragmentName {

    private static final String MAP_FILE ="poland.map";
    private static final String markerBMP ="myLocatinMarker.png";
    private MapView mapView;
    public FragmentActivity activity;
    public View view;
    Location myLocation = null;
    Marker myLocationMarker;



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
        try {

            File mapFile = new File(Environment.getExternalStorageDirectory(), MAP_FILE);
            Bitmap markerImg = null;
            markerImg = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.location_icon));

            MapDataStore mapDataStore = new MapFile(mapFile);
            TileCache tileCache = AndroidUtil.createTileCache(this.getActivity(), "fragments",
                    this.mapView.getModel().displayModel.getTileSize(), 1.0f, 1.5);

            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(new ExternalRenderTheme(new File(Environment.getExternalStorageDirectory(), "theme.xml")));

            myLocationMarker = new Marker(new LatLong(52.517037, 18.38886), markerImg, 1, 1);
            this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
            this.mapView.getLayerManager().getLayers().add(myLocationMarker);

            mapView.setCenter(new LatLong(52.517037, 18.38886));
            mapView.setZoomLevel((byte) 18);
            mapView.getTouchGestureHandler();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.setZoomLevel((byte) 18);
        if(myLocation != null){
            mapView.setCenter(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
            if (myLocationMarker != null)
                myLocationMarker.setLatLong(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
        }
    }

    @Override
    public String getName() {
        return "Maps";
    }

    public void updateLocation() {
        mapView.setZoomLevel((byte) 18);
        mapView.setCenter(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
        if (myLocationMarker != null)
            myLocationMarker.setLatLong(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
    }
}
