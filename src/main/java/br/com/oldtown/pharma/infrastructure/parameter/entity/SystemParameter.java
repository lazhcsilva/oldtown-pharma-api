package br.com.oldtown.pharma.infrastructure.parameter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_parameter")
public class SystemParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_key", nullable = false, unique = true)
    private SystemParameterKey key;

    @Column(name = "param_value", nullable = false)
    private String value;

    private String description;

    private boolean active = true;

    protected SystemParameter() {
    }

    public Long getId() {
        return id;
    }

    public SystemParameterKey getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}