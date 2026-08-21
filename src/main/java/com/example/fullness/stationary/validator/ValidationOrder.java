package com.example.fullness.stationary.validator;

import jakarta.validation.GroupSequence;

@GroupSequence({ ValidatorGroup1.class, ValidatorGroup2.class, ValidatorGroup3.class })
public interface ValidationOrder {

}
