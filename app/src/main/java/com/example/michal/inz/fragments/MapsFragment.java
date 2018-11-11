package com.example.michal.inz.fragments;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.michal.inz.R;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements FragmentName {

    private static final String MAP_FILE ="poland.map";
    private MapView mapView;
    public FragmentActivity activity;
    public View view;



    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        AndroidGraphicFactory.createInstance(getActivity().getApplication());
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = view.findViewById(R.id.openmapview);
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        try {

            File mapFile = new File(Environment.getExternalStorageDirectory(), MAP_FILE);



            MapDataStore mapDataStore = new MapFile(mapFile);
            TileCache tileCache = AndroidUtil.createTileCache(this.getActivity(), "fragments",
                    this.mapView.getModel().displayModel.getTileSize(), 1.0f, 1.5);

            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(new ExternalRenderTheme(new File(Environment.getExternalStorageDirectory(), "theme.xml")));


            this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

            mapView.setCenter(new LatLong(52.517037, 18.38886));
            mapView.setZoomLevel((byte) 12);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }



    @Override
    public String getName() {
        return "Maps";
    }
}
