package com.poc.filecoin.converters;

import com.poc.filecoin.enums.FilecoinProtocol;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFilecoinProtocolConverter implements Converter<String, FilecoinProtocol> {

    @Override
    public FilecoinProtocol convert(String source) {
        return FilecoinProtocol.valueOf(source.toUpperCase());
    }
}
