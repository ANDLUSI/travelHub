package com.tjetc.common.scenic.vo.param;

import lombok.Data;

@Data
public class ScenicParam {
    private Long id;
    private String spotName;
    private String location;
    private String description;
    private String openingHours;
    private Double ticketPrice;
    private String province;
    private int stars;
    private String tags;
    private int featured;
    private String contact;
    private String imagePath;
    private String www;
}
