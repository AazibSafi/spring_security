package com.bns.platotrain.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ConversionUtil {

    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;

    public ConversionUtil(ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    public <D, T> List<D> mapSourceListToDestinationType(Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> mapSourceToDestinationType(entity, outCLass))
                .collect(Collectors.toList());
    }

    public <T> T mapSourceToDestinationType(Object source, Class<T> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public void mapModelSourceToDestination(Object source, Object destination) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(source, destination);
    }

    public String getJSONFromObject(Object requestObject) {
        String newJson = null;
        try {
            log.info("req - JSON : " + String.format("%s ", requestObject));
            newJson = objectMapper.writeValueAsString(requestObject);
        }
        catch (JsonProcessingException e) {
            log.error("Error occurred" + String.format("%s ", e.getMessage()));
        }
        return newJson;
    }

}
