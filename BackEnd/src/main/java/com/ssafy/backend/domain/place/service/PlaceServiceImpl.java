package com.ssafy.backend.domain.place.service;

import com.ssafy.backend.domain.place.dto.PlaceRegisterDto;
import com.ssafy.backend.domain.place.repository.PlaceInfoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    //공공데이터1
    private final String LANG = "LANG_CODE_ID"; //언어
    private final String NAME = "POST_SJ"; //상호명
    private final String ADDRESS = "ADDRESS"; //주소

    // 공공데이터 2 (이미지, 위도, 경도 추가 된 데이터)
    private final String SPOT_DATA = "spot_Data"; //장소 리스트
    private final String SPOT_TITLE = "spot_Title"; //장소명
    private final String SPOT_ADDRESS = "spot_Address"; //주소
    private final String SPOT_IMAGES = "spot_Images"; //이미지
    private final String SPOT_LATITUDE = "spot_Latitude"; //위도
    private final String SPOT_LONGITUDE = "spot_Longitude"; //경도
    private final PlaceInfoRepository placeInfoRepository;

    public PlaceServiceImpl(PlaceInfoRepository placeInfoRepository) {
        this.placeInfoRepository = placeInfoRepository;
    }


    @Override
    public void registPlace(String placeList) {

        try {
            //json데이터 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(placeList);
            System.out.println(jsonObject);

            //row 데이터 뽑기
            JSONObject data = (JSONObject) jsonObject.get("TbVwRestaurants");
            JSONArray rowData = (JSONArray) data.get("row");

            for (Object obj : rowData) {
                JSONObject object = (JSONObject) obj;

                String lang = (String) object.get(LANG);
                String adds = (String) object.get(ADDRESS);

                //주소가 공백이 아니며 한글인 음식점만 고려합니다.
                if (lang.equals("ko") && !adds.isBlank()) {
                    // 관광지명
                    String name = (String) object.get(NAME);

                    PlaceRegisterDto dto = new PlaceRegisterDto();
                    dto.setName(name);
                    dto.setAddress(adds);

                    log.info(dto.getAddress());
                    placeInfoRepository.save(dto.toEntity());

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registPlace2(String placeList) {

        System.out.println("넘어옴");

        try {
            //json데이터 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(placeList);

            //장소에 대한 데이터 json
            JSONArray data = (JSONArray) jsonObject.get(SPOT_DATA);
            if(!data.isEmpty()){

                for (Object o : data) {

                    //장소명
                    JSONArray titles = (JSONArray)((JSONObject) o).get(SPOT_TITLE);
                    //주소
                    JSONArray address = (JSONArray)((JSONObject) o).get(SPOT_ADDRESS);
                    //이미지
                    JSONArray images = (JSONArray)((JSONObject) o).get(SPOT_IMAGES);

                    if (!titles.isEmpty() && !address.isEmpty() && !images.isEmpty()) {
                        //위도
                        String lat = (String) ((JSONObject) o).get(SPOT_LATITUDE);
                        //경도
                        String lon = (String) ((JSONObject) o).get(SPOT_LONGITUDE);

                        String title = (String)titles.get(1); //한글인 타이틀만 추출
                        String adds = (String)address.get(1); //한글인 주소만 추출
                        String image = (String)images.get(0); //한개의 이미지만 추출

//                        //BigDecimal로 위도 경도 타입 변경중....
//                        BigDecimal newLat;
//                        BigDecimal newLon;
//                        try {
//                            newLon = new BigDecimal(lon);
//                            newLat = new BigDecimal(lat);
//                        } catch (NumberFormatException e) {
//                            newLon = new BigDecimal("0");
//                            newLat = new BigDecimal("0");
//                        }

                        //값이 유효한 경우만 데이터로 저장합니다
                        if (!title.isBlank() && !adds.isBlank() && !image.isBlank() && !lat.isBlank() && !lon.isBlank()) {

                            PlaceRegisterDto dto = new PlaceRegisterDto();
                            dto.setName(title);
                            dto.setAddress(adds);
                            dto.setImg(image);
                            dto.setLatitude(lat);
                            dto.setLongitude(lon);

                            placeInfoRepository.save(dto.toEntity());
                        }

                    }

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
