package team4.afinal.aclass.team4finalproject.bean;

import java.io.Serializable;

public class ReceiverBean implements Serializable {

    // 회원정보 Bean에서 받아와야 함
    public int position; // 선택한 줄 값
    public String name; // 닉네임
    public String key; //키 값
    public String studentId; //학번
    public String pay; // 페이
    public String field; // 분야
    public String imgUrl; //이미지가 업로드된 풀경로
    public String career; // 경력
    public String contents; // 세부 내용
    public String password;
    public String kakaoID; // 카카오톡 아이디
    public String address; // 주소

    public ReceiverBean() {
        //RealDB가 디폴트 생성자를 필요로 한다.
    }
}