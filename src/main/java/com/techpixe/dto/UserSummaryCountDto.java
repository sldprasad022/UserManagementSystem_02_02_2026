package com.techpixe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryCountDto 
{
	private long totalUsersCount;
	
	private long totalActiveUsersCount;
	
	private long totalInactiveUsersCount;
}
