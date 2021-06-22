package io.thoughtbox.hamdan.services;

import javax.inject.Inject;

import io.thoughtbox.hamdan.injections.DaggerApiComponents;

public class ServiceRequest {

    public static ServiceRequest instance;

    @Inject
    public DataService dataService;

    public ServiceRequest() {
        DaggerApiComponents.create().inject(this);
    }

    public static ServiceRequest getInstance() {
        if (instance == null) {
            instance = new ServiceRequest();
        }
        return instance;
    }

    public DataService getDataService() {
        return dataService;
    }
}