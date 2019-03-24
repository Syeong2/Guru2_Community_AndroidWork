package team4.afinal.aclass.team4finalproject.bean;

import java.io.Serializable;

public class ExchangeBean implements Serializable {

    public String id; // 게시글 고유 ID
    public String studentId; //게시글 소유자 학번
    public String category; //분야
    public String title; //제목
    public String contents; //내용
    public String kakaoUrl; // 오픈 카카오톡 주소
    public String num; // 인원
    public String key; // 키값

    public ExchangeBean(){
        //디폴트 생성자
    }

}
