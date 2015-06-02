package com.thoughtworks.simpleandroid;

import android.util.Log;

import com.thoughtworks.extensionpoint.TextProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ExtensionPoint implements ServiceTrackerCustomizer<TextProvider, TextProvider> {

    private MainActivity activity;

    public ExtensionPoint(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public TextProvider addingService(ServiceReference<TextProvider> serviceReference) {

        BundleContext context = serviceReference.getBundle().getBundleContext();

        Log.d("extension point", "extension from " + context.getBundle().getSymbolicName());
        TextProvider service = context.getService(serviceReference);
        activity.addButton(serviceReference.hashCode(), service.text());
        Log.d("extension point", "extension id " + serviceReference.hashCode());

        return service;
    }

    @Override
    public void modifiedService(ServiceReference<TextProvider> serviceReference, TextProvider textProvider) {

    }

    @Override
    public void removedService(ServiceReference<TextProvider> serviceReference, TextProvider textProvider) {

    }
}
