package team4.afinal.aclass.team4finalproject.bean;

import java.io.Serializable;


public class InfoBean implements Serializable {

   public String id;    //고유 아이디(기본키)
   public String name;  //이름
   public String num;   //학번
   public String key;   //키값
   public String pw;    //비밀번호
   public String userId;     //아이디
   public String imgUri;   //이미지가 업로드된 풀경로
   public String imgName;  //이미지 파일 이름
   public String kakaoID; //카카오톡 아이디
   public String address; // 주소_구

   public InfoBean() {

   }
}
