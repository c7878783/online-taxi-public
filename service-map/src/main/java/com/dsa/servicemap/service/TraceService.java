package com.dsa.servicemap.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.servicemap.remote.TraceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraceService {

    @Autowired
    TraceClient traceClient;

    public ResponseResult add(String tid){

        return traceClient.add(tid);
    }
}
