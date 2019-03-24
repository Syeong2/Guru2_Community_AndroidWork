package team4.afinal.aclass.team4finalproject.bean;

import java.io.Serializable;

public class NotiBean implements Serializable {
    public String studentID; // 요청 받은 사람 학번
    public String kakaoID; // 요청 보낸 사람 카카오톡 아이디
    public String category; // 어디서 온 요청인지 확인하는 변수
    public String notiId;   //게시물 아이디
    public String requestID; // 요청 보낸 사람 학번


    public NotiBean() {

    }
}
