package br.com.oldtown.pharma.infrastructure.parameter.service;

import br.com.oldtown.pharma.infrastructure.parameter.entity.SystemParameter;
import br.com.oldtown.pharma.infrastructure.parameter.entity.SystemParameterKey;
import br.com.oldtown.pharma.infrastructure.parameter.repository.SystemParameterRepository;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SystemParameterService {

    private final SystemParameterRepository repository;

    public SystemParameterService(SystemParameterRepository repository) {
        this.repository = repository;
    }

    public String get(SystemParameterKey key) {
        return repository.findByKeyAndActiveTrue(key)
                .map(SystemParameter::getValue)
                .orElseThrow(() -> new NotFoundException("Parameter not found or inactive: " + key));
    }

    public Integer getInteger(SystemParameterKey key) {
        return Integer.valueOf(get(key));
    }
}
