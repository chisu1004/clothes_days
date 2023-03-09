package com.example.clothesday.data;

import android.content.Context;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Address {

    /***위도,경도로주소구하기

    *@param lat
    *@param lng
    * @return 주소

     */
     public static String getAddress(Context mContext, double lat, double lng) {
     String nowAddress="현재 위치를 확인할 수 없습니다.";
     Geocoder geocoder=new Geocoder(mContext, Locale.KOREA);
     List<android.location.Address> address;
     try{
         if(geocoder!=null){
         address = geocoder.getFromLocation(lat,lng,1); //세번째 파라미터는 좌표에 대해 주소를 리턴받는 갯수로
             // 한 좌표에 대해 두 개 이상의 이름이 존재할 수 있기에 주소 배열을 리턴 받기 위해 최대 갯수 설정
             if(address!=null&&address.size()>0){//주소 받아오기
              String currentLocationAddress=address.get(0).getAddressLine(0).toString();
              nowAddress=currentLocationAddress;
              }
     }
     }catch(IOException e){
              e.printStackTrace();}
              return nowAddress;
     }


}
