package com.siemmetric.siembol.deployment.storm.providers;

import com.siemmetric.siembol.common.model.StormTopologyDto;


public interface KubernetesProvider {
   void createOrReplaceJob(StormTopologyDto attr);
}
