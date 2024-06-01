package com.siemmetric.siembol.deployment.storm.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import com.siemmetric.siembol.common.model.ZooKeeperAttributesDto;
import com.siemmetric.siembol.deployment.storm.model.KubernetesAttributesDto;
import com.siemmetric.siembol.deployment.storm.model.StormClusterDto;

@ConfigurationProperties(prefix = "topology-manager")
class ServiceConfigurationProperties {
    private int scheduleAtFixedRateSeconds = 300;

    @NestedConfigurationProperty
    private ZooKeeperAttributesDto desiredState;

    @NestedConfigurationProperty
    private ZooKeeperAttributesDto savedState;

    @NestedConfigurationProperty
    private StormClusterDto storm;

    @NestedConfigurationProperty
    private KubernetesAttributesDto k8s;

    public ZooKeeperAttributesDto getDesiredState() {
        return desiredState;
    }

    public void setDesiredState(ZooKeeperAttributesDto desiredState) {
        this.desiredState = desiredState;
    }

    public ZooKeeperAttributesDto getSavedState() {
        return savedState;
    }

    public void setSavedState(ZooKeeperAttributesDto savedState) {
        this.savedState = savedState;
    }

    public StormClusterDto getStorm() {
        return storm;
    }

    public void setStorm(StormClusterDto storm) {
        this.storm = storm;
    }

    public KubernetesAttributesDto getK8s() {
        return k8s;
    }

    public void setK8s(KubernetesAttributesDto k8s) {
        this.k8s = k8s;
    }

    public int getScheduleAtFixedRateSeconds() {
        return scheduleAtFixedRateSeconds;
    }

    public void setScheduleAtFixedRateSeconds(int scheduleAtFixedRateSeconds) {
        this.scheduleAtFixedRateSeconds = scheduleAtFixedRateSeconds;
    }
}
