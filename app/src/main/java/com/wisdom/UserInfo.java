package com.wisdom;


public class UserInfo {

    String dp_uri;
    String email;
    String id_no;
    String name;

    public UserInfo(String dp_uri, String email, String id_no, String name) {
        this.dp_uri = dp_uri;
        this.email = email;
        this.id_no = id_no;
        this.name = name;
    }

    public UserInfo() {
    }

    public String getDp_uri() {
        return dp_uri;
    }

    public void setDp_uri(String dp_uri) {
        this.dp_uri = dp_uri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
