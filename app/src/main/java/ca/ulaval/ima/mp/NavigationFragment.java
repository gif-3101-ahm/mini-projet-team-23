package ca.ulaval.ima.mp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;


import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationFragment extends Fragment implements OnMapReadyCallback, PermissionsListener {
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private CarmenFeature destination = null;
    private View myView;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private LocationComponent locationComponent = null;
    private DirectionsRoute currentRoute = null;
    private NavigationMapRoute navigationMapRoute = null;

    public static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab_location_search).bringToFront();
        view.findViewById(R.id.startButton).bringToFront();
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        this.myView = view;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/lolo13/cjuncf1a46aln1ft7cg1lwhvh"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapboxMap.getStyle().addSource(new GeoJsonSource(geojsonSourceLayerId));
                        mapboxMap.getStyle().addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                        ));
                        initSearchFab();
                        enableLocationComponent(style);
                    }
                });
    }

    private void initSearchFab() {
        myView.findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PlaceAutocompleteFragment autocompleteFragment = PlaceAutocompleteFragment.newInstance("pk.eyJ1IjoibG9sbzEzIiwiYSI6ImNqdW5iaDA0bzFnY3U0NG80YWFoMHB2dncifQ.CwD_x-QkCeKu16kh46QQhw", PlaceOptions.builder().backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS));
                getFragmentManager().beginTransaction().add(R.id.frame_layout, autocompleteFragment).addToBackStack("map").commit();
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(CarmenFeature carmenFeature) {
                        Toast.makeText(getContext(), carmenFeature.text(), Toast.LENGTH_LONG).show();
                        if (getFragmentManager().getBackStackEntryCount() > 0) {
                            getFragmentManager().popBackStack("map", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            destination = carmenFeature;
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Log.d("NavigationTest", carmenFeature.placeName());
                            testonActivityResult();
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (getFragmentManager().getBackStackEntryCount() > 0) {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            getFragmentManager().popBackStack("map", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void testonActivityResult() {
        CarmenFeature selectedCarmenFeature = destination;
                Style style = mapboxMap.getStyle();
                GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
        source.setGeoJson(FeatureCollection.fromFeatures(
                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                ((Point) selectedCarmenFeature.geometry()).longitude()))
                        .zoom(14)
                        .build()), 4000);
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        Point destinationPoint = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).longitude(),
                ((Point) selectedCarmenFeature.geometry()).latitude());
        myView.findViewById(R.id.startButton).setVisibility(View.VISIBLE);
        getRoute(originPoint, destinationPoint);
        myView.findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(true)
                        .lightThemeResId(R.style.CustomNavigationViewLight)
                        .darkThemeResId(R.style.CustomNavigationViewDark)
                        .build();
                NavigationLauncher.startNavigation(NavigationFragment.this.getActivity(), options);
            }
        });

    }

    private void getRoute(Point originPoint, Point destinationPoint){
        NavigationRoute.builder(getContext())
                .accessToken("pk.eyJ1IjoibG9sbzEzIiwiYSI6ImNqdW5iaDA0bzFnY3U0NG80YWFoMHB2dncifQ.CwD_x-QkCeKu16kh46QQhw")
                .origin(originPoint)
                .destination(destinationPoint)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Toast.makeText(getContext(),
                                    "No routes found, make sure you set the right user and access token.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Toast.makeText(getContext(),
                                    "No routes found.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("CurrentRoute", String.valueOf(response.body().routes()));
                        currentRoute = response.body().routes().get(0);

                        if (navigationMapRoute != null) {
                            Log.d("test", "nostyle");
                            navigationMapRoute.removeRoute();
                        } else {
                            Log.d("test", "style");
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.CustomNavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e("test", "Error: " + t.getMessage());
                    }

        });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            this.locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoibG9sbzEzIiwiYSI6ImNqdW5iaDA0bzFnY3U0NG80YWFoMHB2dncifQ.CwD_x-QkCeKu16kh46QQhw");
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
       Log.d("Navigation", "error");
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
