package com.siemmetric.siembol.configeditor.rest.application;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.DependsOn;
import org.springframework.util.ResourceUtils;
import com.siemmetric.siembol.common.model.ZooKeeperAttributesDto;
import com.siemmetric.siembol.common.testing.TestingZooKeeperConnectorFactory;
import com.siemmetric.siembol.common.zookeeper.ZooKeeperConnector;
import com.siemmetric.siembol.common.zookeeper.ZooKeeperConnectorFactory;
import com.siemmetric.siembol.common.zookeeper.ZooKeeperConnectorFactoryImpl;
import com.siemmetric.siembol.configeditor.common.AuthorisationProvider;
import com.siemmetric.siembol.configeditor.common.ConfigEditorUtils;
import com.siemmetric.siembol.configeditor.common.ConfigSchemaService;
import com.siemmetric.siembol.common.constants.ServiceType;
import com.siemmetric.siembol.configeditor.sync.common.ConfigServiceHelper;
import com.siemmetric.siembol.configeditor.sync.service.*;
import com.siemmetric.siembol.configeditor.model.ConfigEditorUiLayout;
import com.siemmetric.siembol.configeditor.model.ConfigStoreProperties;
import com.siemmetric.siembol.configeditor.rest.common.ConfigEditorConfigurationProperties;
import com.siemmetric.siembol.configeditor.rest.common.ConfigEditorHelper;
import com.siemmetric.siembol.configeditor.rest.common.ServiceConfigurationProperties;
import com.siemmetric.siembol.configeditor.service.common.ConfigEditorServiceFactory;
import com.siemmetric.siembol.configeditor.serviceaggregator.ServiceAggregator;
import com.siemmetric.siembol.configeditor.serviceaggregator.ServiceAggregatorImpl;
import com.siemmetric.siembol.configeditor.testcase.TestCaseEvaluator;
import com.siemmetric.siembol.configeditor.testcase.TestCaseEvaluatorImpl;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(ConfigEditorConfigurationProperties.class)
public class ConfigEditorConfiguration implements DisposableBean {

    @Autowired
    private ConfigEditorConfigurationProperties properties;

    @Autowired
    private AuthorisationProvider authProvider;

    private ServiceAggregator serviceAggregator;

    @Bean("serviceAggregator")
    ServiceAggregator serviceAggregator() throws Exception {
        Map<String, ConfigStoreProperties> configStorePropertiesMap = ConfigEditorHelper
                .getConfigStoreProperties(this.properties);

        ServiceAggregatorImpl.Builder builder = new ServiceAggregatorImpl.Builder(authProvider);
        for (String name : properties.getServices().keySet()) {
            ServiceConfigurationProperties serviceProperties = properties.getServices().get(name);

            ServiceType serviceType = ServiceType.fromName(serviceProperties.getType());
            ConfigEditorServiceFactory serviceFactory = ConfigEditorServiceFactory.fromServiceType(serviceType);

            ConfigEditorUiLayout uiLayout = ConfigEditorUtils.readUiLayoutFile(serviceProperties.getUiConfigFileName());
            var attributes = Optional.ofNullable(serviceProperties.getAttributes());
            var additionalTesters = Optional.ofNullable(
                    serviceProperties.getAdditionalConfigTesters());
            ConfigSchemaService schemaService = serviceFactory.createConfigSchemaService(uiLayout,
                    attributes,
                    additionalTesters);

            builder.addService(name,
                    serviceType,
                    configStorePropertiesMap.get(name),
                    serviceFactory.getConfigInfoProvider() ,
                    schemaService);
        }
        serviceAggregator = builder.build();
        return serviceAggregator;
    }

    @Bean("testCaseEvaluator")
    TestCaseEvaluator testCaseEvaluator() throws Exception {
        ConfigEditorUiLayout uiLayout = ConfigEditorUtils.readUiLayoutFile(properties.getTestCasesUiConfigFileName());
        return new TestCaseEvaluatorImpl(uiLayout).withErrorMessage();
    }

    @Bean("stormApplicationProvider")
    @ConditionalOnProperty(prefix = "config-editor", value = "synchronisation")
    @DependsOn("zooKeeperConnectorFactory")
    StormApplicationProvider stormApplicationProvider(
            @Autowired ZooKeeperConnectorFactory zooKeeperConnectorFactory) throws Exception {
        return StormApplicationProviderImpl.create(zooKeeperConnectorFactory, properties.getStormTopologiesZooKeeper());
    }

    @Bean("synchronisationService")
    @ConditionalOnProperty(prefix = "config-editor", value = "synchronisation")
    @DependsOn({"zooKeeperConnectorFactory", "stormApplicationProvider"})
    SynchronisationService synchronisationService(
            @Autowired ZooKeeperConnectorFactory zooKeeperConnectorFactory,
            @Autowired StormApplicationProvider stormApplicationProvider) throws Exception {
        serviceAggregator = serviceAggregator();

        List<ConfigServiceHelper> aggregatorServices = serviceAggregator
                .getAggregatorServices()
                .stream()
                .map(x -> new ConfigServiceHelperImpl(x, properties, zooKeeperConnectorFactory))
                .collect(Collectors.toList());

        SynchronisationService ret = new SynchronisationServiceImpl.Builder(stormApplicationProvider)
                .addConfigServiceHelpers(aggregatorServices)
                .build();

        ret.synchroniseAllServices(properties.getSynchronisation());
        return ret;
    }

    @Bean("zooKeeperConnectorFactory")
    @ConditionalOnProperty(prefix = "config-editor", value = "synchronisation")
    ZooKeeperConnectorFactory zooKeeperConnectorFactory() throws Exception {
        if (properties.getTestingZookeeperFiles() == null) {
            return new ZooKeeperConnectorFactoryImpl();
        }

        TestingZooKeeperConnectorFactory ret = new TestingZooKeeperConnectorFactory();
        for (Map.Entry<String, String> entry: properties.getTestingZookeeperFiles().entrySet()){
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + entry.getValue());
            String content = new String(Files.readAllBytes(file.toPath()));
            ret.setData(entry.getKey(), content);
        }

        return ret;
    }

    @Bean("enrichmentTablesProvider")
    @ConditionalOnProperty(prefix = "config-editor", value = "synchronisation")
    @DependsOn("zooKeeperConnectorFactory")
    EnrichmentTablesProvider enrichmentTablesProvider(
            @Autowired ZooKeeperConnectorFactory zooKeeperConnectorFactory) throws Exception {
        Map<String, ZooKeeperConnector> zooKeeperConnectorMap = new HashMap<>();
        if (properties.getEnrichmentTablesZooKeeper() != null) {
            for (Map.Entry<String, ZooKeeperAttributesDto> entry : properties.getEnrichmentTablesZooKeeper().entrySet()) {
                zooKeeperConnectorMap.put(entry.getKey(),
                        zooKeeperConnectorFactory.createZookeeperConnector(entry.getValue()));
            }
        }
        return new EnrichmentTablesProviderImpl(zooKeeperConnectorMap);
    }

    @Override
    public void destroy() {
        if (serviceAggregator != null) {
            serviceAggregator.shutDown();
        }

        if (serviceAggregator != null) {
            serviceAggregator.awaitShutDown();
        }
    }
}
