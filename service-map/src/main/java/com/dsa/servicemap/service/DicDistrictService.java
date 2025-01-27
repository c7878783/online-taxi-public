package com.dsa.servicemap.service;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DicDistrict;
import com.dsa.servicemap.mapper.DicDistrictMapper;
import com.dsa.servicemap.remote.MapDicDistrictClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DicDistrictService {

    @Autowired
    MapDicDistrictClient mapDicDistrictClient;

    @Autowired
    DicDistrictMapper dicDistrictMapper;

    public ResponseResult initDistrict(String keywords) {
        // 请求地图
        String dicDistrictResult = mapDicDistrictClient.dicDistrict(keywords);
        System.out.println(dicDistrictResult);

        JSONObject dicDistrictJsonObject = JSONObject.fromObject(dicDistrictResult);
        int status = dicDistrictJsonObject.getInt(AmapConfigConstants.STATUS);
        if (status != 1) {
            return ResponseResult.fail(CommonStatusEnum.MAP_DISTRICT_ERROR.getCode(), CommonStatusEnum.MAP_DISTRICT_ERROR.getValue());
        }

        JSONArray countryJsonArray = dicDistrictJsonObject.getJSONArray(AmapConfigConstants.DISTRICTS);

        // 解析结果
        for (int i = 0; i < countryJsonArray.size(); i++) { // 修正 <= 为 < 避免越界
            JSONObject countryJsonObject = countryJsonArray.getJSONObject(i);
            String addressCode = countryJsonObject.getString(AmapConfigConstants.ADCODE);
            String name = countryJsonObject.getString(AmapConfigConstants.NAME);
            String parentAddressCode = "0";
            String level = countryJsonObject.getString(AmapConfigConstants.LEVEL);
            int levelInt = generateLevel(level);
            insertDicDistrict(addressCode, name, parentAddressCode, levelInt);

            JSONArray provinceJsonArray = countryJsonObject.getJSONArray(AmapConfigConstants.DISTRICTS);
            for (int p = 0; p < provinceJsonArray.size(); p++) { // 修正 <= 为 <
                JSONObject provinceJsonObject = provinceJsonArray.getJSONObject(p); // 修正 i 为 p
                String provinceAddressCode = provinceJsonObject.getString(AmapConfigConstants.ADCODE);
                String provinceName = provinceJsonObject.getString(AmapConfigConstants.NAME);
                String provinceParentAddressCode = countryJsonObject.getString(AmapConfigConstants.ADCODE);
                String provinceLevel = provinceJsonObject.getString(AmapConfigConstants.LEVEL);
                int provinceLevelInt = generateLevel(provinceLevel);

                insertDicDistrict(provinceAddressCode, provinceName, provinceParentAddressCode, provinceLevelInt);

                JSONArray cityJsonArray = provinceJsonObject.getJSONArray(AmapConfigConstants.DISTRICTS);
                for (int j = 0; j < cityJsonArray.size(); j++) { // 修正 <= 为 <
                    JSONObject cityJsonObject = cityJsonArray.getJSONObject(j); // 修正 i 为 j
                    String cityAddressCode = cityJsonObject.getString(AmapConfigConstants.ADCODE);
                    String cityName = cityJsonObject.getString(AmapConfigConstants.NAME);
                    String cityParentAddressCode = provinceJsonObject.getString(AmapConfigConstants.ADCODE);
                    String cityLevel = cityJsonObject.getString(AmapConfigConstants.LEVEL);
                    int cityLevelInt = generateLevel(cityLevel);



                    insertDicDistrict(cityAddressCode, cityName, cityParentAddressCode, cityLevelInt);

                    JSONArray districtJsonArray = cityJsonObject.getJSONArray(AmapConfigConstants.DISTRICTS);
                    for (int k = 0; k < districtJsonArray.size(); k++) { // 修正 <= 为 <
                        JSONObject districtJsonObject = districtJsonArray.getJSONObject(k); // 修正 i 为 k
                        String districtAddressCode = districtJsonObject.getString(AmapConfigConstants.ADCODE);
                        String districtName = districtJsonObject.getString(AmapConfigConstants.NAME);
                        String districtParentAddressCode = cityJsonObject.getString(AmapConfigConstants.ADCODE);
                        String districtLevel = districtJsonObject.getString(AmapConfigConstants.LEVEL);
                        int districtLevelInt = generateLevel(districtLevel);
                        if(districtLevel.equals(AmapConfigConstants.STREET)){
                            continue;
                        }
                        insertDicDistrict(districtAddressCode, districtName, districtParentAddressCode, districtLevelInt);
                    }
                }
            }
        }

        return ResponseResult.success();
    }


    private void insertDicDistrict(String addressCode, String name, String parentAddressCode, int levelInt){

        DicDistrict dicDistrict = new DicDistrict();
        dicDistrict.setAddressCode(addressCode);
        dicDistrict.setAddressName(name);
        dicDistrict.setParentAddressCode(parentAddressCode);
        dicDistrict.setLevel(levelInt);

        dicDistrictMapper.insert(dicDistrict);
    }

    private int generateLevel(String level){
        int levelInt = 0;
        if (level.trim().equals("country")){
            levelInt = 0;
        } else if (level.trim().equals("province")) {
            levelInt = 1;
        } else if (level.trim().equals("city")) {
            levelInt = 2;
        } else if (level.trim().equals("district")) {
            levelInt = 3;
        }
        return levelInt;
    }
}
