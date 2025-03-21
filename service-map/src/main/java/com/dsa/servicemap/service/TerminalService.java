package com.dsa.servicemap.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.responese.TrsearchResponse;
import com.dsa.servicemap.remote.TerminalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
public class TerminalService {

    @Autowired
    TerminalClient terminalClient;

    public ResponseResult add(String name, String desc){

        return terminalClient.add(name, desc);
    }

    public ResponseResult<List<TerminalResponse>> aroundSearch(String center, Integer radius){

        return terminalClient.aroundSearch(center, radius);
    }

    public ResponseResult<TrsearchResponse> trsearch(String tid, Long starttime, Long endtime) {

        return terminalClient.trsearch(tid, starttime, endtime);
    }
}
