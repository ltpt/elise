package com.poly.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReport {
	@Id
	private Serializable username;
	private String email;
	private String phone;
	private Long count; // tổng lượt mua
	private Double total; // tổng số tiền đã tiêu
	private Integer month;
	private Integer year;
}
