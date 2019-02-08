package com.example.lenovo.businesscardscanner;



public class Model
{

    private int id;
    private String name;
    private String company;
    private byte[] image;
    private String status;

    public Model(int id, String name, String company, byte[] image,String status) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.image = image;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
