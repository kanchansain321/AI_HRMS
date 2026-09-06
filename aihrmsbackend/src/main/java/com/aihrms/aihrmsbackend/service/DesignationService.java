package com.aihrms.aihrmsbackend.service;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;

public interface DesignationService {
    CustomResponse addDesignation(String name);

    CustomResponse getDesignationById(Long id);

    CustomResponse getAllDesignations();
}
