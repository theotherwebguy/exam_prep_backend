package org.backend.examprep_backend.service;

import org.backend.examprep_backend.model.Tenant;
import org.backend.examprep_backend.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public Optional<Tenant> findTenantByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }
}
