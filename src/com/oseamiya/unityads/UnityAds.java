package com.oseamiya.unityads;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.unity3d.ads.*;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityAds extends AndroidNonvisibleComponent implements IUnityAdsLoadListener, IUnityAdsShowListener, BannerView.IListener {
    private final Context context;
    private final Activity activity;
    private String gameId;
    private boolean testMode = false;
    private BannerView theBannerView;

    public UnityAds(ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        activity = (Activity) container.$context();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
    @SimpleProperty
    public void TestMode(boolean value) {
        this.testMode = value;
    }

    @SimpleProperty
    public boolean TestMode() {
        return this.testMode;
    }

    @DesignerProperty
    @SimpleProperty
    public void GameId(String value) {
        this.gameId = value;
    }

    @SimpleProperty
    public String GameId() {
        return this.gameId == null ? "" : this.gameId;
    }

    // Events on initialization
    @SimpleEvent
    public void InitializationComplete() {
        EventDispatcher.dispatchEvent(this, "InitializationComplete");
    }

    @SimpleEvent
    public void InitializationFailed(String message) {
        EventDispatcher.dispatchEvent(this, "InitializationFailed", message);
    }

    // Events on ad loading
    @SimpleEvent
    public void AdLoaded(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdLoaded", adUnitId);
    }

    @SimpleEvent
    public void AdFailedToLoad(String adUnitId, String message) {
        EventDispatcher.dispatchEvent(this, "AdFailedToLoad", adUnitId, message);
    }

    // Events on ads showing
    @SimpleEvent
    public void AdShowFailed(String adUnitId, String message) {
        EventDispatcher.dispatchEvent(this, "AdShowFailed", adUnitId, message);
    }

    @SimpleEvent
    public void AdShowStarted(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdShowStarted", adUnitId);
    }

    @SimpleEvent
    public void AdShowClicked(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdShowClicked", adUnitId);
    }

    @SimpleEvent
    public void AdShowCompleted(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdShowCompleted", adUnitId);
    }

    @SimpleEvent
    public void AdShowSkipped(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "AdShowSkipped", adUnitId);
    }

    // Events on banner ad status
    @SimpleEvent
    public void BannerFailedToLoad(String adUnitId, String error) {
        EventDispatcher.dispatchEvent(this, "BannerFailedToLoad", adUnitId, error);
    }

    @SimpleEvent
    public void BannerClicked(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "BannerClicked", adUnitId);
    }

    @SimpleEvent
    public void BannerLoaded(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "BannerLoaded", adUnitId);
    }

    @SimpleEvent
    public void BannerLeftApplication(String adUnitId) {
        EventDispatcher.dispatchEvent(this, "BannerLeftApplication", adUnitId);
    }

    @SimpleFunction
    public void Initialize() {
        com.unity3d.ads.UnityAds.initialize(this.context, this.gameId, this.testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                InitializationComplete();
            }

            @Override
            public void onInitializationFailed(com.unity3d.ads.UnityAds.UnityAdsInitializationError unityAdsInitializationError, String message) {
                InitializationFailed(message);
            }
        });
    }

    // To integrate Interstitial ads
    @SimpleFunction
    public void LoadInterstitialAd(String adUnitId) {
        com.unity3d.ads.UnityAds.load(adUnitId, this);
    }

    @SimpleFunction
    public void ShowInterstitialAd(String adUnitId) {
        com.unity3d.ads.UnityAds.show(this.activity, adUnitId, this);
    }

    // Implementing rewarded ads
    @SimpleFunction
    public void LoadRewardedAd(String adUnitId) {
        com.unity3d.ads.UnityAds.load(adUnitId, this);
    }

    @SimpleFunction
    public void ShowRewardedAd(String adUnitId) {
        com.unity3d.ads.UnityAds.show(activity, adUnitId, new UnityAdsShowOptions(), this);
    }

    // Implementing banner ads
    @SimpleFunction
    public void LoadBannerAd(String adUnitId, Object size) {
        if (size instanceof UnityBannerSize) {
            theBannerView = new BannerView(activity, adUnitId, (UnityBannerSize) size);
            theBannerView.setListener(this);
            theBannerView.load();
        } else {
            throw new YailRuntimeError("Size not found", "RuntimeError");
        }
    }

    @SimpleFunction
    public void ShowBannerAd(AndroidViewComponent in) {
        ViewGroup viewGroup = (ViewGroup) in.getView();
        if (theBannerView.getParent() != null) {
            ((ViewGroup) theBannerView.getParent()).removeView(theBannerView);
        }
        viewGroup.addView(theBannerView);
    }

    @SimpleFunction
    public Object CustomSize(int width, int height) {
        return new UnityBannerSize(width, height);
    }

    @SimpleProperty
    public Object DynamicSize() {
        return UnityBannerSize.getDynamicSize(context);
    }

    @SimpleFunction
    public Object NormalSize() {
        return new UnityBannerSize(320, 50);
    }

    @Override
    public void onUnityAdsAdLoaded(String adUnitId) {
        AdLoaded(adUnitId);
    }

    @Override
    public void onUnityAdsFailedToLoad(String adUnitId, com.unity3d.ads.UnityAds.UnityAdsLoadError unityAdsLoadError, String message) {
        AdFailedToLoad(adUnitId, message);
    }

    @Override
    public void onUnityAdsShowFailure(String adUnitId, com.unity3d.ads.UnityAds.UnityAdsShowError unityAdsShowError, String message) {
        AdShowFailed(adUnitId, message);
    }

    @Override
    public void onUnityAdsShowStart(String adUniId) {
        AdShowStarted(adUniId);
    }

    @Override
    public void onUnityAdsShowClick(String adUnitId) {
        AdShowClicked(adUnitId);
    }

    @Override
    public void onUnityAdsShowComplete(String adUnitId, com.unity3d.ads.UnityAds.UnityAdsShowCompletionState state) {
        if (state == com.unity3d.ads.UnityAds.UnityAdsShowCompletionState.SKIPPED) {
            AdShowSkipped(adUnitId);
        } else {
            AdShowCompleted(adUnitId);
        }
    }

    @Override
    public void onBannerLoaded(BannerView bannerView) {
        this.theBannerView = bannerView;
        BannerLoaded(bannerView.getPlacementId());
    }

    @Override
    public void onBannerClick(BannerView bannerView) {
        BannerClicked(bannerView.getPlacementId());
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
        BannerFailedToLoad(bannerView.getPlacementId(), bannerErrorInfo.errorMessage);
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerView) {
        BannerLeftApplication(bannerView.getPlacementId());
    }
}
