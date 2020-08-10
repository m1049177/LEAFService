package com.mindtree.leafservice3.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import org.hibernate.cache.jcache.ConfigSettings;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.mindtree.leafservice3.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.mindtree.leafservice3.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.mindtree.leafservice3.domain.User.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Authority.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.User.class.getName() + ".authorities");
            createCache(cm, com.mindtree.leafservice3.domain.OraganizationalUnit.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.LineOfBusiness.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Application.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Issue.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Functionality.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Maintenance.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Report.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Integration.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Revenue.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Spend.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Change.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Technology.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Brand.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.BusinessFunction.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Capabilities.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.BusinessProcess.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Activity.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Task.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Employee.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.TechnologyStack.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.TechnologyRecommendation.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Company.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.ExcelTemplate.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.UploadExcel.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Example.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Evaluation.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Assessment.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.TechnologySuggestions.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Budget.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Label.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Label.class.getName() + ".company_ids");
            createCache(cm, com.mindtree.leafservice3.domain.Diagram.class.getName());
            createCache(cm, com.mindtree.leafservice3.domain.Integration.class.getName() + ".integrationApp");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
