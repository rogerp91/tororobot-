package com.tororobot.domain.model;

/**
 * Created by Roger Patiño on 23/09/2016.
 */

public class User {

//    @SerializedName("id")
    private Integer id;

//    @SerializedName("name")
    private String name;

//    @SerializedName("email")
    private String email;

//    @SerializedName("enterprise_id")
    private Integer enterpriseId;

//    @SerializedName("status")
    private Integer status;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}