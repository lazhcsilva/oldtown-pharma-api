package br.com.oldtown.pharma.infrastructure.parameter.repository;

import br.com.oldtown.pharma.infrastructure.parameter.entity.SystemParameter;
import br.com.oldtown.pharma.infrastructure.parameter.entity.SystemParameterKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    Optional<SystemParameter> findByKeyAndActiveTrue(SystemParameterKey parameterKey);
}